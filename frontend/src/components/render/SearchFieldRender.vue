<template>
  <el-form-item :label="field.label" :required="field.required">
    <!-- 输入框 -->
    <el-input
      v-if="field.fieldType === 'input'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled"
      @input="handleChange"
    />

    <!-- 文本域 -->
    <el-input
      v-else-if="field.fieldType === 'textarea'"
      :model-value="model[field.fieldCode]"
      type="textarea"
      :placeholder="field.placeholder"
      :rows="3"
      @input="handleChange"
    />

    <!-- 下拉框 -->
    <el-select
      v-else-if="field.fieldType === 'select'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled"
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
      :disabled="field.disabled"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 日期范围选择 -->
    <el-date-picker
      v-else-if="field.fieldType === 'dateRange'"
      :model-value="model[field.fieldCode]"
      type="daterange"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      :disabled="field.disabled"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 数字输入 -->
    <el-input-number
      v-else-if="field.fieldType === 'number'"
      :model-value="model[field.fieldCode]"
      :placeholder="field.placeholder"
      :disabled="field.disabled"
      :min="field.props?.min"
      :max="field.props?.max"
      :step="field.props?.step || 1"
      style="width: 100%"
      @change="handleChange"
    />

    <!-- 开关 -->
    <el-switch
      v-else-if="field.fieldType === 'switch'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled"
      @change="handleChange"
    />

    <!-- 单选框 -->
    <el-radio-group
      v-else-if="field.fieldType === 'radio'"
      :model-value="model[field.fieldCode]"
      :disabled="field.disabled"
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
      :disabled="field.disabled"
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

    <!-- 未知的字段类型 -->
    <span v-else class="text-gray-400">未知的字段类型: {{ field.fieldType }}</span>
  </el-form-item>
</template>

<script setup lang="ts">
import type { SearchField } from '@/api/page'

interface Props {
  field: SearchField
  model: Record<string, any>
}

interface Emits {
  (e: 'change', fieldCode: string, value: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const handleChange = (value: any) => {
  emit('change', props.field.fieldCode, value)
}
</script>
