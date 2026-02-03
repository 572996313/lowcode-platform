/**
 * 表单渲染器（运行时）
 */
<template>
  <div class="form-render-wrapper" :style="wrapperStyle">
    <el-form
      :model="formData"
      :label-width="config.labelWidth ? config.labelWidth + 'px' : undefined"
      :label-position="config.labelPosition || 'left'"
      class="form-render"
    >
      <el-row :gutter="16">
        <el-col
          v-for="field in config.fields"
          :key="field.id"
          :span="Math.floor(24 / (config.layoutCols || 2))"
        >
          <el-form-item :label="field.label" :required="field.required">
            <el-input
              v-if="field.fieldType === 'input'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
            />
            <el-input
              v-else-if="field.fieldType === 'textarea'"
              v-model="formData[field.fieldCode]"
              type="textarea"
              :placeholder="field.placeholder"
              :rows="3"
            />
            <el-input-number
              v-else-if="field.fieldType === 'number'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              style="width: 100%"
            />
            <el-switch
              v-else-if="field.fieldType === 'switch'"
              v-model="formData[field.fieldCode]"
            />
            <el-select
              v-else-if="field.fieldType === 'select'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              style="width: 100%"
            >
              <el-option
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
            <el-radio-group
              v-else-if="field.fieldType === 'radio'"
              v-model="formData[field.fieldCode]"
            >
              <el-radio
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-radio>
            </el-radio-group>
            <el-checkbox-group
              v-else-if="field.fieldType === 'checkbox'"
              v-model="formData[field.fieldCode]"
            >
              <el-checkbox
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-checkbox>
            </el-checkbox-group>
            <el-date-picker
              v-else-if="field.fieldType === 'date'"
              v-model="formData[field.fieldCode]"
              type="date"
              :placeholder="field.placeholder"
              style="width: 100%"
            />
            <el-date-picker
              v-else-if="field.fieldType === 'datetime'"
              v-model="formData[field.fieldCode]"
              type="datetime"
              :placeholder="field.placeholder"
              style="width: 100%"
            />
            <el-date-picker
              v-else-if="field.fieldType === 'dateRange'"
              v-model="formData[field.fieldCode]"
              type="daterange"
              :placeholder="field.placeholder"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import type { FormComponentConfig, FormFieldConfig } from '@/types/page-free-canvas'

interface Props {
  config: FormComponentConfig
  style?: Record<string, any>
}

interface Emits {
  (e: 'submit', data: Record<string, any>): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'block',
  overflow: 'auto'
}))

// 初始化表单数据
const initFormData = () => {
  const data: Record<string, any> = {}
  props.config.fields.forEach((field: FormFieldConfig) => {
    if (field.defaultValue !== undefined) {
      data[field.fieldCode] = field.defaultValue
    } else if (field.fieldType === 'checkbox') {
      data[field.fieldCode] = []
    }
  })
  return data
}

const formData = reactive<Record<string, any>>(initFormData())

// 监听配置变化，重置表单
watch(() => props.config.fields, () => {
  Object.assign(formData, initFormData())
}, { deep: true })

// 暴露提交方法（可以通过 ref 调用）
defineExpose({
  submit: () => emit('submit', { ...formData }),
  getData: () => ({ ...formData }),
  reset: () => Object.assign(formData, initFormData())
})
</script>

<style scoped lang="scss">
.form-render-wrapper {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-sizing: border-box;

  .form-render {
    :deep(.el-form-item) {
      margin-bottom: 18px;
    }
  }
}
</style>
