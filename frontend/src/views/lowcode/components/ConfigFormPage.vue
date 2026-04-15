<template>
  <el-form
    v-if="allFields.length > 0"
    ref="formRef"
    :model="formData"
    :rules="computedRules"
    :label-width="layout.labelWidth || '120px'"
    :label-position="layout.labelPosition || 'right'"
    :size="layout.size || 'default'"
    :disabled="formDisabled"
  >
    <!-- 单组无标题 → 扁平渲染 -->
    <template v-if="isFlatLayout">
      <el-row :gutter="layout.rowGutter || 20">
        <el-col v-for="field in allFields" :key="field.field" :span="getFieldSpan(field)">
          <el-form-item :label="field.label" :prop="field.field" :label-width="field.labelWidth">
            <FieldRenderer
              :field="field"
              :value="formData[field.field]"
              :mode="mode"
              @update:value="val => updateField(field.field, val)"
            />
            <div v-if="field.tooltip" class="field-tooltip">{{ field.tooltip }}</div>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 多组或有标题 → 分组渲染 -->
    <template v-else>
      <template v-for="(group, gi) in groups" :key="gi">
        <!-- 可折叠分组 -->
        <el-collapse v-if="group.collapsible" v-model="collapsedState[gi]" class="form-group-collapse">
          <el-collapse-item :title="group.title || ''" name="open">
            <el-row :gutter="layout.rowGutter || 20">
              <el-col v-for="field in group.fields" :key="field.field" :span="getFieldSpan(field)">
                <el-form-item :label="field.label" :prop="field.field" :label-width="field.labelWidth">
                  <FieldRenderer
                    :field="field"
                    :value="formData[field.field]"
                    :mode="mode"
                    @update:value="val => updateField(field.field, val)"
                  />
                  <div v-if="field.tooltip" class="field-tooltip">{{ field.tooltip }}</div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>

        <!-- 不可折叠分组 -->
        <el-card v-else shadow="never" class="form-group-card">
          <template v-if="group.title" #header>
            <div class="group-header">
              <span class="group-title">{{ group.title }}</span>
              <span v-if="group.description" class="group-desc">{{ group.description }}</span>
            </div>
          </template>
          <el-row :gutter="layout.rowGutter || 20">
            <el-col v-for="field in group.fields" :key="field.field" :span="getFieldSpan(field)">
              <el-form-item :label="field.label" :prop="field.field" :label-width="field.labelWidth">
                <FieldRenderer
                  :field="field"
                  :value="formData[field.field]"
                  :mode="mode"
                  @update:value="val => updateField(field.field, val)"
                />
                <div v-if="field.tooltip" class="field-tooltip">{{ field.tooltip }}</div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </template>
    </template>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick } from 'vue'
import type { FormPageFieldConfig, FormGroupConfig, FormLayoutConfig, FormPageMode } from '@/api/form-standard'

// ---- 字段渲染子组件 ----
const FieldRenderer = {
  name: 'FieldRenderer',
  props: {
    field: { type: Object as () => FormPageFieldConfig, required: true },
    value: { default: undefined as any },
    mode: { type: String as () => FormPageMode, default: 'add' }
  },
  emits: ['update:value'],
  setup(props: any, { emit }: any) {
    const isView = computed(() => props.mode === 'view')
    const isEdit = computed(() => props.mode === 'edit')

    const updateVal = (val: any) => emit('update:value', val)

    // 获取选项标签
    const getOptionLabel = (field: FormPageFieldConfig, value: any): string => {
      if (!field.options) return String(value ?? '')
      const opt = field.options.find(o => o.value === value)
      return opt ? opt.label : String(value ?? '')
    }

    const getOptionLabels = (field: FormPageFieldConfig, values: any[]): string => {
      if (!values || !field.options) return ''
      return values.map((v: any) => getOptionLabel(field, v)).join('、')
    }

    // switch 的显示文本
    const getSwitchText = (field: FormPageFieldConfig, value: any): string => {
      const active = field.activeValue ?? true
      return value === active ? '是' : '否'
    }

    return { isView, isEdit, updateVal, getOptionLabel, getOptionLabels, getSwitchText }
  },
  template: `
    <div class="field-renderer">
      <!-- view 模式：纯文本展示 -->
      <template v-if="isView">
        <span v-if="field.type === 'input' || field.type === 'number'" class="view-text">{{ value ?? '-' }}</span>
        <span v-else-if="field.type === 'textarea'" class="view-text view-textarea">{{ value || '-' }}</span>
        <span v-else-if="field.type === 'select'" class="view-text">{{ getOptionLabel(field, value) }}</span>
        <span v-else-if="field.type === 'radio'" class="view-text">{{ getOptionLabel(field, value) }}</span>
        <span v-else-if="field.type === 'checkbox'" class="view-text">{{ getOptionLabels(field, value) || '-' }}</span>
        <span v-else-if="field.type === 'switch'" class="view-text">{{ getSwitchText(field, value) }}</span>
        <span v-else-if="field.type === 'date'" class="view-text">{{ value || '-' }}</span>
        <span v-else-if="field.type === 'datetime'" class="view-text">{{ value || '-' }}</span>
        <span v-else-if="field.type === 'upload'" class="view-text">{{ (value && value.length) ? value.length + ' 个文件' : '无附件' }}</span>
        <span v-else class="view-text">{{ value ?? '-' }}</span>
      </template>

      <!-- add/edit 模式：表单控件 -->
      <template v-else>
        <el-input
          v-if="field.type === 'input'"
          :model-value="value"
          :placeholder="field.placeholder || '请输入'"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        />
        <el-input
          v-else-if="field.type === 'textarea'"
          type="textarea"
          :model-value="value"
          :placeholder="field.placeholder || '请输入'"
          :rows="field.rows || 3"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        />
        <el-input-number
          v-else-if="field.type === 'number'"
          :model-value="value"
          :placeholder="field.placeholder || '请输入'"
          :min="field.min"
          :max="field.max"
          :step="field.step || 1"
          :disabled="isEdit && field.disabledOnEdit"
          controls-position="right"
          style="width: 100%"
          @update:model-value="updateVal"
        />
        <el-select
          v-else-if="field.type === 'select'"
          :model-value="value"
          :placeholder="field.placeholder || '请选择'"
          :disabled="isEdit && field.disabledOnEdit"
          style="width: 100%"
          @update:model-value="updateVal"
        >
          <el-option
            v-for="opt in field.options"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-radio-group
          v-else-if="field.type === 'radio'"
          :model-value="value"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        >
          <el-radio v-for="opt in field.options" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </el-radio>
        </el-radio-group>
        <el-checkbox-group
          v-else-if="field.type === 'checkbox'"
          :model-value="value || []"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        >
          <el-checkbox v-for="opt in field.options" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </el-checkbox>
        </el-checkbox-group>
        <el-switch
          v-else-if="field.type === 'switch'"
          :model-value="value"
          :active-value="field.activeValue ?? true"
          :inactive-value="field.inactiveValue ?? false"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        />
        <el-date-picker
          v-else-if="field.type === 'date'"
          :model-value="value"
          :placeholder="field.placeholder || '请选择日期'"
          value-format="YYYY-MM-DD"
          style="width: 100%"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        />
        <el-date-picker
          v-else-if="field.type === 'datetime'"
          :model-value="value"
          type="datetime"
          :placeholder="field.placeholder || '请选择时间'"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
          :disabled="isEdit && field.disabledOnEdit"
          @update:model-value="updateVal"
        />
        <el-upload
          v-else-if="field.type === 'upload'"
          action="#"
          :auto-upload="false"
          :limit="field.uploadConfig?.limit"
          :accept="field.uploadConfig?.accept"
          :multiple="field.uploadConfig?.multiple"
          :file-list="value || []"
          :on-change="(file: any, fileList: any) => updateVal(fileList)"
        >
          <el-button type="primary">选择文件</el-button>
          <template v-if="field.uploadConfig?.tip" #tip>
            <div class="el-upload__tip">{{ field.uploadConfig.tip }}</div>
          </template>
        </el-upload>
        <el-input
          v-else
          :model-value="value"
          :placeholder="field.placeholder || '请输入'"
          @update:model-value="updateVal"
        />
      </template>
    </div>
  `
}

// ---- Props ----
const props = withDefaults(defineProps<{
  config?: {
    layout?: FormLayoutConfig
    groups?: FormGroupConfig[]
  } | null
  fields?: FormPageFieldConfig[]
  modelValue?: Record<string, any>
  mode?: FormPageMode
  isEdit?: boolean
  labelWidth?: string
}>(), {
  config: null,
  modelValue: () => ({}),
  mode: 'add',
  isEdit: false,
  labelWidth: '120px'
})

const emit = defineEmits<{
  (e: 'update:modelValue', data: Record<string, any>): void
}>()

const formRef = ref<InstanceType<typeof import('element-plus')['ElForm']>>()

// ---- 计算属性 ----

const layout = computed<FormLayoutConfig>(() => props.config?.layout || {
  labelWidth: props.labelWidth
})

const groups = computed<FormGroupConfig[]>(() => {
  if (props.config?.groups?.length) return props.config.groups
  if (props.fields?.length) return [{ fields: props.fields }]
  return []
})

const allFields = computed<FormPageFieldConfig[]>(() => {
  const fields: FormPageFieldConfig[] = []
  for (const group of groups.value) {
    fields.push(...group.fields)
  }
  return fields
})

const isFlatLayout = computed(() =>
  groups.value.length === 1 && !groups.value[0].title
)

const formDisabled = computed(() => props.mode === 'view')

const defaultSpan = computed(() => {
  const cols = layout.value.columns || 1
  return 24 / cols
})

// ---- 折叠状态 ----

const collapsedState = reactive<Record<number, string[]>>({})
watch(groups, (newGroups) => {
  for (let i = 0; i < newGroups.length; i++) {
    collapsedState[i] = newGroups[i].defaultCollapsed ? [] : ['open']
  }
}, { immediate: true })

// ---- 表单数据 ----

const formData = reactive<Record<string, any>>({})

const initDefaults = () => {
  for (const field of allFields.value) {
    if (field.type === 'switch') {
      formData[field.field] = field.activeValue ?? true
    } else if (field.type === 'checkbox') {
      formData[field.field] = []
    } else if (field.defaultValue !== undefined) {
      formData[field.field] = field.defaultValue
    } else {
      formData[field.field] = ''
    }
  }
}

// 防止 watch 循环：props → formData → emit → props
let isUpdatingFromProps = false

watch(() => props.modelValue, (val) => {
  isUpdatingFromProps = true
  initDefaults()
  if (val && Object.keys(val).length > 0) {
    Object.assign(formData, val)
  }
  nextTick(() => { isUpdatingFromProps = false })
}, { immediate: true, deep: true })

watch(formData, () => {
  if (!isUpdatingFromProps) {
    emit('update:modelValue', { ...formData })
  }
}, { deep: true })

// ---- 字段跨度 ----

const getFieldSpan = (field: FormPageFieldConfig): number => {
  return field.span || defaultSpan.value
}

// ---- 更新字段值 ----

const updateField = (field: string, value: any) => {
  formData[field] = value
}

// ---- 校验规则 ----

const computedRules = computed(() => {
  const rules: Record<string, any[]> = {}
  for (const field of allFields.value) {
    const fieldRules: any[] = []
    if (field.rules && field.rules.length > 0) {
      for (const rule of field.rules) {
        const r: any = { ...rule }
        if (rule.pattern) {
          r.pattern = new RegExp(rule.pattern)
        }
        fieldRules.push(r)
      }
    } else if (field.required) {
      const isSelectType = ['select', 'radio', 'checkbox', 'date', 'datetime', 'upload'].includes(field.type)
      fieldRules.push({
        required: true,
        message: isSelectType ? `请选择${field.label}` : `请输入${field.label}`,
        trigger: isSelectType ? 'change' : 'blur'
      })
    }
    if (fieldRules.length > 0) {
      rules[field.field] = fieldRules
    }
  }
  return rules
})

// ---- Expose ----

defineExpose({
  validate: async (): Promise<boolean> => {
    if (!formRef.value) return false
    try {
      await formRef.value.validate()
      return true
    } catch {
      return false
    }
  },
  resetFields: () => {
    formRef.value?.resetFields()
  },
  getFormData: (): Record<string, any> => {
    return { ...formData }
  }
})
</script>

<style scoped lang="scss">
.form-group-card {
  margin-bottom: 16px;

  .group-header {
    display: flex;
    align-items: center;
    gap: 12px;

    .group-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }

    .group-desc {
      font-size: 13px;
      color: #909399;
    }
  }
}

.form-group-collapse {
  margin-bottom: 16px;
  border: none;

  :deep(.el-collapse-item__header) {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    background: transparent;
    border: none;
  }

  :deep(.el-collapse-item__wrap) {
    border: none;
  }
}

.field-tooltip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}

.view-text {
  font-size: 14px;
  color: #303133;
  line-height: 32px;
}

.view-textarea {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
