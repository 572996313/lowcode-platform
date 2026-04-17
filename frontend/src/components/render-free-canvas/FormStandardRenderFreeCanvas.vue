<template>
  <div class="form-standard-render" :style="containerStyle">
    <!-- 表单区域 -->
    <div class="standard-form-wrapper">
      <el-form
        :label-width="config.layout?.labelWidth || '120px'"
        :label-position="config.layout?.labelPosition || 'right'"
        :size="config.layout?.size || 'default'"
        :model="formData"
        class="standard-form"
      >
        <template v-for="(group, gi) in config.groups" :key="gi">
          <!-- 可折叠分组 -->
          <el-collapse v-if="group.collapsible" v-model="collapsedState[gi]" class="form-group-collapse">
            <el-collapse-item :title="group.title || ''" name="open">
              <el-row :gutter="config.layout?.rowGutter || 20">
                <el-col v-for="field in group.fields" :key="field.field" :span="getFieldSpan(field)">
                  <el-form-item :label="field.label">
                    <FieldRender
                      :field="field"
                      :value="formData[field.field]"
                      :disabled="isFieldDisabled(field)"
                      @change="val => formData[field.field] = val"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
          </el-collapse>

          <!-- 普通分组 -->
          <div v-else class="form-group">
            <div v-if="group.title" class="group-title">{{ group.title }}</div>
            <el-row :gutter="config.layout?.rowGutter || 20">
              <el-col v-for="field in group.fields" :key="field.field" :span="getFieldSpan(field)">
                <el-form-item :label="field.label">
                  <FieldRender
                    :field="field"
                    :value="formData[field.field]"
                    :disabled="isFieldDisabled(field)"
                    @change="val => formData[field.field] = val"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </template>

        <!-- 空状态 -->
        <el-empty
          v-if="!config.groups?.length || config.groups.every(g => !g.fields?.length)"
          description="暂无表单字段，请在属性面板中配置"
          :image-size="60"
        />
      </el-form>
    </div>

    <!-- 底部操作按钮 -->
    <div v-if="mode !== 'view' && hasFields" class="form-footer">
      <el-button
        v-for="btn in config.toolbar?.buttons"
        :key="btn.action"
        :type="(btn.btnType as any) || ''"
        @click="handleSubmit"
      >
        {{ btn.label }}
      </el-button>
      <el-button @click="handleCancel">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, watch, h } from 'vue'
import {
  ElInput, ElSelect, ElOption, ElInputNumber,
  ElSwitch, ElDatePicker, ElRadioGroup, ElRadio,
  ElCheckboxGroup, ElCheckbox
} from 'element-plus'
import type { FormStandardComponentConfig, FormStandardField, ComponentStyle } from '@/types/page-free-canvas'

const props = defineProps<{
  config: FormStandardComponentConfig
  compStyle?: ComponentStyle
  mode?: 'add' | 'edit' | 'view'
  initialData?: Record<string, any>
}>()

const emit = defineEmits<{
  (e: 'action', data: { action: string; actionConfig?: any }): void
  (e: 'submit', data: { mode: string; data: Record<string, any> }): void
  (e: 'cancel'): void
}>()

const formData = reactive<Record<string, any>>({})

watch(() => props.initialData, (data) => {
  for (const key of Object.keys(formData)) {
    delete formData[key]
  }
  if (data) {
    for (const [key, val] of Object.entries(data)) {
      formData[key] = val
    }
  }
}, { immediate: true })

const collapsedState = reactive<Record<number, string[]>>({})

const hasFields = computed(() => {
  return props.config.groups?.some(g => g.fields?.length)
})

// 判断字段是否禁用：view 模式全禁用，否则看 editable 配置
function isFieldDisabled(field: FormStandardField): boolean {
  if (props.mode === 'view') return true
  if (field.editable === false) return true
  return false
}

function handleButtonClick(btn: any) {
  emit('action', { action: btn.action, actionConfig: btn.actionConfig })
}

function handleSubmit() {
  emit('submit', { mode: props.mode || 'add', data: { ...formData } })
}

function handleCancel() {
  emit('cancel')
}

const containerStyle = computed(() => ({
  width: '100%',
  height: '100%',
  display: 'flex',
  flexDirection: 'column' as const,
  backgroundColor: props.compStyle?.backgroundColor || '#fff',
  borderRadius: props.compStyle?.borderRadius || '4px',
  padding: props.compStyle?.padding || '12px',
  border: props.compStyle?.border || '1px solid #ebeef5',
  overflow: 'auto'
}))

function getFieldSpan(field: FormStandardField): number {
  const columns = props.config.layout?.columns || 2
  return field.span || Math.floor(24 / columns)
}

// 字段渲染组件（使用 render 函数 + 实际组件引用）
const FieldRender = {
  props: {
    field: { type: Object as () => FormStandardField, required: true },
    value: { default: undefined as any },
    disabled: { type: Boolean, default: false }
  },
  emits: ['change'],
  setup(props: any, { emit }: any) {
    return () => {
      const f = props.field
      const val = props.value
      const dis = props.disabled
      const onChange = (v: any) => emit('change', v)

      if (f.type === 'input' || f.type === 'textarea') {
        return h(ElInput, {
          type: f.type === 'textarea' ? 'textarea' : 'text',
          modelValue: val,
          placeholder: f.placeholder || '请输入',
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        })
      }
      if (f.type === 'select') {
        return h(ElSelect, {
          modelValue: val,
          placeholder: f.placeholder || '请选择',
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        }, (f.options || []).map((opt: any) =>
          h(ElOption, { key: opt.value, label: opt.label, value: opt.value })
        ))
      }
      if (f.type === 'number') {
        return h(ElInputNumber, {
          modelValue: val,
          placeholder: f.placeholder,
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        })
      }
      if (f.type === 'switch') {
        return h(ElSwitch, {
          modelValue: val,
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        })
      }
      if (f.type === 'date') {
        return h(ElDatePicker, {
          modelValue: val,
          placeholder: f.placeholder || '请选择日期',
          disabled: dis,
          size: 'small',
          valueFormat: 'YYYY-MM-DD',
          'onUpdate:modelValue': onChange
        })
      }
      if (f.type === 'radio') {
        return h(ElRadioGroup, {
          modelValue: val,
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        }, (f.options || []).map((opt: any) =>
          h(ElRadio, { key: opt.value, value: opt.value }, () => opt.label)
        ))
      }
      if (f.type === 'checkbox') {
        return h(ElCheckboxGroup, {
          modelValue: val || [],
          disabled: dis,
          size: 'small',
          'onUpdate:modelValue': onChange
        }, (f.options || []).map((opt: any) =>
          h(ElCheckbox, { key: opt.value, label: opt.value }, () => opt.label)
        ))
      }
      return h('span', { class: 'field-placeholder' }, f.placeholder || f.type)
    }
  }
}
</script>

<style scoped lang="scss">
.form-standard-render {
  box-sizing: border-box;

  .standard-form-wrapper {
    flex: 1;
    overflow: auto;
  }

  .standard-form {
    .form-group {
      margin-bottom: 16px;

      .group-title {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 12px;
        padding-left: 8px;
        border-left: 3px solid #409eff;
      }
    }

    .form-group-collapse {
      margin-bottom: 16px;

      :deep(.el-collapse-item__header) {
        font-weight: 500;
        color: #303133;
      }
    }

    .field-placeholder {
      color: #c0c4cc;
      font-size: 12px;
    }
  }

  .form-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
    margin-top: 16px;
    flex-shrink: 0;
  }
}
</style>
