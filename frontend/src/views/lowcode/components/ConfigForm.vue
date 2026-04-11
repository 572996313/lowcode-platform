<template>
  <el-form :model="formData" :rules="computedRules" ref="formRef" :label-width="labelWidth">
    <template v-for="field in fields" :key="field.field">
      <el-form-item :label="field.label" :prop="field.field" :label-width="field.labelWidth">
        <!-- 输入框 -->
        <el-input
          v-if="field.type === 'input'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          :disabled="isEdit && field.disabledOnEdit"
        />
        <!-- 下拉选择 -->
        <el-select
          v-else-if="field.type === 'select'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          :disabled="isEdit && field.disabledOnEdit"
          style="width: 100%"
        >
          <el-option
            v-for="opt in field.options"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <!-- 文本域 -->
        <el-input
          v-else-if="field.type === 'textarea'"
          v-model="formData[field.field]"
          type="textarea"
          :rows="field.rows || 3"
          :placeholder="field.placeholder"
        />
        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.type === 'number'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          style="width: 100%"
        />
        <!-- 开关 -->
        <el-switch
          v-else-if="field.type === 'switch'"
          v-model="formData[field.field]"
          :active-value="field.activeValue ?? true"
          :inactive-value="field.inactiveValue ?? false"
        />
        <!-- 日期选择 -->
        <el-date-picker
          v-else-if="field.type === 'date'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
    </template>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import type { FormFieldConfig } from '@/api/table-standard'

const props = withDefaults(defineProps<{
  fields: FormFieldConfig[]
  modelValue?: Record<string, any>
  isEdit?: boolean
  labelWidth?: string
}>(), {
  modelValue: () => ({}),
  isEdit: false,
  labelWidth: '80px'
})

const emit = defineEmits<{
  (e: 'update:modelValue', data: Record<string, any>): void
}>()

const formRef = ref()

// 内部表单数据
const formData = reactive<Record<string, any>>({})

// 初始化表单字段默认值
const initDefaults = () => {
  for (const field of props.fields) {
    if (field.type === 'switch') {
      formData[field.field] = field.activeValue ?? true
    } else {
      formData[field.field] = ''
    }
  }
}

// 监听外部数据变化（编辑时填充数据）
watch(() => props.modelValue, (val) => {
  initDefaults()
  Object.assign(formData, val)
}, { immediate: true, deep: true })

// 双向绑定：内部变化通知外部
watch(formData, (val) => {
  emit('update:modelValue', { ...val })
}, { deep: true })

// 根据字段配置动态生成校验规则
const computedRules = computed(() => {
  const rules: Record<string, any[]> = {}
  for (const field of props.fields) {
    if (field.rules && field.rules.length > 0) {
      rules[field.field] = field.rules.map(rule => {
        const r: any = { ...rule }
        if (rule.pattern) {
          r.pattern = new RegExp(rule.pattern)
        }
        return r
      })
    } else if (field.required) {
      rules[field.field] = [{
        required: true,
        message: `请${field.type === 'select' ? '选择' : '输入'}${field.label}`,
        trigger: field.type === 'select' ? 'change' : 'blur'
      }]
    }
  }
  return rules
})

// 暴露方法供父组件调用
const validate = async (): Promise<boolean> => {
  if (!formRef.value) return false
  return new Promise((resolve) => {
    formRef.value.validate((valid: boolean) => resolve(valid))
  })
}

const resetFields = () => {
  formRef.value?.resetFields()
}

const getFormData = (): Record<string, any> => {
  return { ...formData }
}

defineExpose({ validate, resetFields, getFormData })
</script>
