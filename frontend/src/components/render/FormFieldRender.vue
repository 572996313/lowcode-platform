<template>
  <el-form-item :label="field.label" :required="field.required">
    <!-- 输入框 -->
    <el-input
      v-if="field.fieldType === 'input'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      @input="handleChange"
    />

    <!-- 文本域 -->
    <el-input
      v-else-if="field.fieldType === 'textarea'"
      :model-value="model[field.fieldCode]"
      type="textarea"
      :placeholder="field.placeholder"
      :rows="3"
      :disabled="field.disabled || readonly"
      @input="handleChange"
    />

    <!-- 下拉框 -->
    <el-select
      v-else-if="field.fieldType === 'select'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      style="width: 100%"
      @change="handleChange"
    >
      <el-option
        v-for="opt in field.options"
        :key="opt.value"
        :label="opt.label"
        :value="opt.value"
      />
    </el-select>

    <!-- 日期选择 -->
    <el-date-picker
      v-else-if="field.fieldType === 'date'"
      :model-value="model[field.fieldCode]"
      type="date"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 日期时间选择 -->
    <el-date-picker
      v-else-if="field.fieldType === 'datetime'"
      :model-value="model[field.fieldCode]"
      type="datetime"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 时间选择 -->
    <el-time-picker
      v-else-if="field.fieldType === 'time'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 数字输入 -->
    <el-input-number
      v-else-if="field.fieldType === 'number'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      :min="field.props?.min"
      :max="field.props?.max"
      :step="field.props?.step || 1"
      :precision="field.props?.precision"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 开关 -->
    <el-switch
      v-else-if="field.fieldType === 'switch'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled || readonly"
      @change="handleChange"
    />

    <!-- 滑块 -->
    <el-slider
      v-else-if="field.fieldType === 'slider'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled || readonly"
      :min="field.props?.min || 0"
      :max="field.props?.max || 100"
      :step="field.props?.step || 1"
      @change="handleChange"
    />

    <!-- 单选框 -->
    <el-radio-group
      v-else-if="field.fieldType === 'radio'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled || readonly"
      @change="handleChange"
    >
      <el-radio
        v-for="opt in field.options"
        :key="opt.value"
        :value="opt.value"
      >
        {{ opt.label }}
      </el-radio>
    </el-radio-group>

    <!-- 多选框 -->
    <el-checkbox-group
      v-else-if="field.fieldType === 'checkbox'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled || readonly"
      @change="handleChange"
    >
      <el-checkbox
        v-for="opt in field.options"
        :key="opt.value"
        :value="opt.value"
      >
        {{ opt.label }}
      </el-checkbox>
    </el-checkbox-group>

    <!-- 日期范围选择 -->
    <el-date-picker
      v-else-if="field.fieldType === 'dateRange'"
      :model-value="model[field.fieldCode]"
      type="daterange"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      :disabled="field.disabled || readonly"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 图片上传 -->
    <el-upload
      v-else-if="field.fieldType === 'imageUpload'"
      :file-list="getFileList(field.fieldCode)"
      :disabled="field.disabled || readonly"
      :action="field.props?.action || '/api/upload'"
      list-type="picture-card"
      :limit="field.props?.limit || 1"
      :on-success="(res) => handleUploadSuccess(field.fieldCode, res)"
      :on-remove="(file) => handleUploadRemove(field.fieldCode, file)"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <!-- 文件上传 -->
    <el-upload
      v-else-if="field.fieldType === 'fileUpload'"
      :file-list="getFileList(field.fieldCode)"
      :disabled="field.disabled || readonly"
      :action="field.props?.action || '/api/upload'"
      :limit="field.props?.limit || 3"
      :on-success="(res) => handleUploadSuccess(field.fieldCode, res)"
      :on-remove="(file) => handleUploadRemove(field.fieldCode, file)"
    >
      <el-button type="primary">
        <el-icon class="mr-1"><Upload /></el-icon>
        点击上传
      </el-button>
    </el-upload>

    <!-- 富文本编辑器 -->
    <div v-else-if="field.fieldType === 'richtext'" class="richtext-container">
      <el-input
        :model-value="model[field.fieldCode]"
        type="textarea"
        :rows="6"
        :disabled="field.disabled || readonly"
        @input="handleChange"
      />
      <div class="text-xs text-gray-400 mt-1">
        注：完整的富文本编辑器需要集成第三方组件（如 tinymce 或 quill）
      </div>
    </div>

    <!-- 级联选择器 -->
    <el-cascader
      v-else-if="field.fieldType === 'cascader'"
      :model-value="model[field.fieldCode]"
      :options="field.options"
      :placeholder="field.placeholder"
      :disabled="field.disabled || readonly"
      :props="field.props?.cascaderProps"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 未知的字段类型 -->
    <span v-else class="text-gray-400 text-sm">未知的字段类型: {{ field.fieldType }}</span>
  </el-form-item>
</template>

<script setup lang="ts">
import type { FormField } from '@/api/form'
import type { UploadFile, UploadUserFile } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

interface Props {
  field: FormField
  model: Record<string, any>
  readonly?: boolean
}

interface Emits {
  (e: 'change', fieldCode: string, value: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 解析选项 JSON 字符串
const parseOptions = (optionsJson?: string) => {
  if (!optionsJson) return []
  try {
    return JSON.parse(optionsJson)
  } catch {
    return []
  }
}

// 解析属性 JSON 字符串
const parseProps = (propsJson?: string) => {
  if (!propsJson) return {}
  try {
    return JSON.parse(propsJson)
  } catch {
    return {}
  }
}

// 获取上传文件列表
const getFileList = (fieldCode: string): UploadUserFile[] => {
  const value = props.model[fieldCode]
  if (!value) return []

  if (Array.isArray(value)) {
    return value.map((url, index) => ({
      name: `file_${index}`,
      url,
      uid: Date.now() + index
    }))
  } else if (typeof value === 'string') {
    return [{
      name: 'file',
      url: value,
      uid: Date.now()
    }]
  }
  return []
}

// 上传成功回调
const handleUploadSuccess = (fieldCode: string, response: any) => {
  const currentValue = props.model[fieldCode]

  if (Array.isArray(currentValue)) {
    currentValue.push(response.data?.url || response.url)
  } else {
    props.model[fieldCode] = [response.data?.url || response.url]
  }

  emit('change', fieldCode, props.model[fieldCode])
}

// 删除文件回调
const handleUploadRemove = (fieldCode: string, file: UploadFile) => {
  const currentValue = props.model[fieldCode]

  if (Array.isArray(currentValue)) {
    const index = currentValue.findIndex((url: string) => url === file.url)
    if (index > -1) {
      currentValue.splice(index, 1)
    }
  }

  emit('change', fieldCode, props.model[fieldCode])
}

const handleChange = (value: any) => {
  emit('change', props.field.fieldCode, value)
}
</script>

<style scoped>
.richtext-container {
  width: 100%;
}
</style>
