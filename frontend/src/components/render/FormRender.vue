<template>
  <div v-loading="loading" class="form-render">
    <!-- 基于模板的表单渲染 -->
    <component
      v-if="templateComponent && formConfig.templateCode"
      :is="templateComponent"
      :title="formConfig.formName"
    >
      <template v-for="slot in templateSlots" :key="slot.id" #[slot.id]>
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          :label-width="formConfig.labelWidth + 'px'"
          :label-position="formConfig.labelPosition"
          :size="formConfig.size"
          class="slot-form"
        >
          <el-row :gutter="20">
            <el-col
              v-for="field in getFieldsBySlot(slot.id)"
              :key="field.id"
              :span="field.span || formConfig.layoutCols || 12"
            >
              <FormFieldRender
                :field="field"
                :model="formData"
                @change="handleFieldChange"
              />
            </el-col>
          </el-row>
        </el-form>
      </template>
    </component>

    <!-- 通用表单渲染（兼容没有模板的情况） -->
    <el-form
      v-else
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="formConfig.labelWidth + 'px'"
      :label-position="formConfig.labelPosition"
      :size="formConfig.size"
    >
      <el-row :gutter="20">
        <el-col
          v-for="field in formConfig.fields"
          :key="field.id"
          :span="field.span || formConfig.layoutCols || 12"
        >
          <FormFieldRender
            :field="field"
            :model="formData"
            @change="handleFieldChange"
          />
        </el-col>
      </el-row>

      <!-- 表单按钮区 -->
      <div v-if="footerButtons.length > 0" class="form-actions">
        <ButtonRenderer
          v-for="btn in footerButtons"
          :key="btn.id"
          :config="btn"
          :context="{ form: formData }"
          @click="handleButtonAction"
        />
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { FormConfig, FormField } from '@/api/form'
import type { ButtonConfig } from '@/api/button'
import { getFormConfig, getButtonsByFormId } from '@/api/form'
import {
  getTemplateByCode,
  getTemplateSlots,
  getTemplateMeta,
  type FormSlotMeta
} from '@/components/templates/TemplateRegistry'
import FormFieldRender from './FormFieldRender.vue'
import ButtonRenderer from './ButtonRenderer.vue'
import { createButtonActionHandler } from '@/utils/buttonActionHandler'

interface Props {
  configId: number
  mode?: 'add' | 'edit' | 'view' | 'search'
  readonly?: boolean
  initialData?: Record<string, any>
}

interface Emits {
  (e: 'submit', data: Record<string, any>): void
  (e: 'reset'): void
  (e: 'change', fieldCode: string, value: any): void
  (e: 'refresh'): void
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'add',
  readonly: false
})

const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const formConfig = ref<FormConfig & { fields?: FormField[] }>({})
const formData = reactive<Record<string, any>>({})
const rules = ref<Record<string, any>>({})
const footerButtons = ref<ButtonConfig[]>([])

// 模板相关状态
const templateComponent = shallowRef<any>(null)
const templateSlots = ref<FormSlotMeta[]>([])

// 根据 slotId 获取字段列表
const getFieldsBySlot = (slotId: string) => {
  return (formConfig.value.fields || []).filter(field => field.slotId === slotId)
}

// 初始化表单数据
const initFormData = (fields: FormField[]) => {
  fields.forEach(field => {
    if (props.initialData && props.initialData[field.fieldCode] !== undefined) {
      formData[field.fieldCode] = props.initialData[field.fieldCode]
    } else if (field.defaultValue !== undefined) {
      // 解析默认值
      formData[field.fieldCode] = parseDefaultValue(field.defaultValue)
    } else {
      formData[field.fieldCode] = undefined
    }
  })
}

// 解析默认值
const parseDefaultValue = (value: any): any => {
  if (typeof value === 'string') {
    try {
      return JSON.parse(value)
    } catch {
      return value
    }
  }
  return value
}

// 初始化验证规则
const initRules = (fields: FormField[]) => {
  const rulesMap: Record<string, any> = {}

  fields.forEach(field => {
    const fieldRules: any[] = []

    // 必填验证
    if (field.required) {
      fieldRules.push({
        required: true,
        message: `请输入${field.label}`,
        trigger: ['blur', 'change']
      })
    }

    // 从 rulesJson 解析验证规则
    if (field.rulesJson) {
      try {
        const customRules = JSON.parse(field.rulesJson)
        if (Array.isArray(customRules)) {
          fieldRules.push(...customRules)
        }
      } catch (e) {
        console.error('解析验证规则失败:', e)
      }
    }

    if (fieldRules.length > 0) {
      rulesMap[field.fieldCode] = fieldRules
    }
  })

  rules.value = rulesMap
}

// 加载表单配置
const loadFormConfig = async () => {
  loading.value = true
  try {
    const data = await getFormConfig(props.configId)

    // 处理字段数据
    if (data.configJson) {
      try {
        const configObj = JSON.parse(data.configJson)
        formConfig.value = { ...data, ...configObj }
      } catch (e) {
        formConfig.value = data
      }
    } else {
      formConfig.value = data
    }

    // 加载模板组件（如果有 templateCode）
    if (formConfig.value.templateCode) {
      const templateMeta = getTemplateMeta(formConfig.value.templateCode)
      if (templateMeta) {
        templateSlots.value = templateMeta.fieldSlots
        // 动态加载模板组件
        const template = getTemplateByCode(formConfig.value.templateCode)
        if (template) {
          templateComponent.value = template.component
        }
      }
    }

    // 初始化表单数据和验证规则
    if (formConfig.value.fields && formConfig.value.fields.length > 0) {
      initFormData(formConfig.value.fields)
      initRules(formConfig.value.fields)
    }

    // 加载按钮配置
    const buttons = await getButtonsByFormId(props.configId)
    footerButtons.value = buttons.filter((btn: ButtonConfig) => btn.position === 'footer' || btn.position === 'form')
  } catch (error: any) {
    ElMessage.error(error.message || '加载表单配置失败')
  } finally {
    loading.value = false
  }
}

// 字段值变化
const handleFieldChange = (fieldCode: string, value: any) => {
  formData[fieldCode] = value
  emit('change', fieldCode, value)

  // 处理字段联动
  handleFieldLinkage(fieldCode, value)
}

// 处理字段联动
const handleFieldLinkage = (triggerField: string, value: any) => {
  const fields = formConfig.value.fields || []

  fields.forEach(field => {
    if (!field.linkageJson) return

    try {
      const linkageRules = JSON.parse(field.linkageJson)

      linkageRules.forEach((rule: any) => {
        if (rule.triggerField === triggerField) {
          const shouldTrigger = evaluateCondition(rule.condition, value)

          if (shouldTrigger) {
            if (rule.action === 'setValue') {
              formData[field.fieldCode] = rule.value
            } else if (rule.action === 'setVisible') {
              // TODO: 实现字段显示/隐藏逻辑
            } else if (rule.action === 'setDisabled') {
              // TODO: 实现字段禁用/启用逻辑
            } else if (rule.action === 'setOptions') {
              // TODO: 动态加载选项
            }
          }
        }
      })
    } catch (e) {
      console.error('处理字段联动失败:', e)
    }
  })
}

// 评估条件表达式
const evaluateCondition = (condition: string | Function, value: any): boolean => {
  if (typeof condition === 'function') {
    try {
      return condition(value)
    } catch {
      return false
    }
  }

  if (typeof condition === 'string') {
    try {
      const func = new Function('value', `return ${condition}`)
      return func(value)
    } catch {
      return false
    }
  }

  return false
}

// 按钮动作处理
const handleButtonAction = async (event: MouseEvent, config: ButtonConfig) => {
  const context = {
    form: formData,
    refresh: () => emit('refresh')
  }

  const actionHandler = createButtonActionHandler(context)
  await actionHandler.handle(config)
}

// 表单提交
const submitForm = async () => {
  if (!formRef.value) return false

  try {
    await formRef.value.validate()
    emit('submit', { ...formData })
    return true
  } catch {
    return false
  }
}

// 表单重置
const resetForm = () => {
  formRef.value?.resetFields()
  emit('reset')
}

// 获取表单数据
const getFormData = () => {
  return { ...formData }
}

// 设置表单数据
const setFormData = (data: Record<string, any>) => {
  Object.assign(formData, data)
}

// 监听初始数据变化
watch(() => props.initialData, (newData) => {
  if (newData && formConfig.value.fields) {
    initFormData(formConfig.value.fields)
  }
}, { deep: true })

// 暴露方法供父组件调用
defineExpose({
  submitForm,
  resetForm,
  getFormData,
  setFormData
})

onMounted(() => {
  loadFormConfig()
})
</script>

<style scoped>
.form-render {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.form-render :deep(.slot-form) {
  /* Slot 内的表单样式 */
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  margin-top: 20px;
}
</style>
