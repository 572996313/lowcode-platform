/**
 * 查询表单渲染器（运行时）
 */
<template>
  <div class="search-form-render-wrapper" :style="wrapperStyle">
    <el-form :model="formData" :inline="true" class="search-form">
      <el-row :gutter="16">
        <el-col
          v-for="field in config.fields"
          :key="field.id"
          :span="Math.floor(24 / (config.layoutCols || 4))"
        >
          <el-form-item :label="field.label">
            <el-input
              v-if="field.fieldType === 'input'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              clearable
            />
            <el-input
              v-else-if="field.fieldType === 'textarea'"
              v-model="formData[field.fieldCode]"
              type="textarea"
              :placeholder="field.placeholder"
            />
            <el-select
              v-else-if="field.fieldType === 'select'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
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
            <el-input-number
              v-else-if="field.fieldType === 'number'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="Math.floor(24 / (config.layoutCols || 4))">
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import type { SearchFormComponentConfig, FormFieldConfig } from '@/types/page-free-canvas'

interface Props {
  config: SearchFormComponentConfig
  style?: Record<string, any>
}

interface Emits {
  (e: 'search', params: Record<string, any>): void
  (e: 'reset'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'block'
}))

// 初始化表单数据
const initFormData = () => {
  const data: Record<string, any> = {}
  props.config.fields.forEach((field: FormFieldConfig) => {
    if (field.defaultValue !== undefined) {
      data[field.fieldCode] = field.defaultValue
    }
  })
  return data
}

const formData = reactive<Record<string, any>>(initFormData())

// 监听配置变化，重置表单
watch(() => props.config.fields, () => {
  Object.assign(formData, initFormData())
}, { deep: true })

function handleSearch() {
  emit('search', { ...formData })
}

function handleReset() {
  Object.keys(formData).forEach(key => {
    formData[key] = undefined
  })
  emit('reset')
}
</script>

<style scoped lang="scss">
.search-form-render-wrapper {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-sizing: border-box;

  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 12px;
    }
  }
}
</style>
