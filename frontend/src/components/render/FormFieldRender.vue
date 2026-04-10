<template>
  <!-- 单个字段渲染组件 -->
  <el-form-item
    :label="widgetConfig?.label || fieldLabel"
    :prop="fieldCode"
    :required="widgetConfig?.required"
  >
    <!-- 输入框 -->
    <el-input
      v-if="widgetType === 'input'"
      :model-value="value"
      :placeholder="widgetConfig?.placeholder"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    />

    <!-- 文本域 -->
    <el-input
      v-else-if="widgetType === 'textarea'"
      type="textarea"
      :model-value="value"
      :placeholder="widgetConfig?.placeholder"
      :rows="widgetConfig?.rows || 3"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    />

    <!-- 下拉框 -->
    <el-select
      v-else-if="widgetType === 'select'"
      :model-value="value"
      :placeholder="widgetConfig?.placeholder"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    >
      <el-option
        v-for="option in widgetConfig?.options"
        :key="option.value"
        :label="option.label"
        :value="option.value"
      />
    </el-select>

    <!-- 单选框组 -->
    <el-radio-group
      v-else-if="widgetType === 'radio'"
      :model-value="value"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    >
      <el-radio
        v-for="option in widgetConfig?.options"
        :key="option.value"
        :label="option.value"
      >
        {{ option.label }}
      </el-radio>
    </el-radio-group>

    <!-- 复选框组 -->
    <el-checkbox-group
      v-else-if="widgetType === 'checkbox'"
      :model-value="value"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    >
      <el-checkbox
        v-for="option in widgetConfig?.options"
        :key="option.value"
        :label="option.value"
      >
        {{ option.label }}
      </el-checkbox>
    </el-checkbox-group>

    <!-- 日期选择器 -->
    <el-date-picker
      v-else-if="widgetType === 'date'"
      :model-value="value"
      type="date"
      :placeholder="widgetConfig?.placeholder || '选择日期'"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    />

    <!-- 日期时间选择器 -->
    <el-date-picker
      v-else-if="widgetType === 'datetime'"
      :model-value="value"
      type="datetime"
      :placeholder="widgetConfig?.placeholder || '选择日期时间'"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    />

    <!-- 数字输入框 -->
    <el-input-number
      v-else-if="widgetType === 'number'"
      :model-value="value"
      :disabled="widgetConfig?.disabled"
      @update:model-value="$emit('update:value', $event)"
    />

    <!-- 默认输入框 -->
    <el-input
      v-else
      :model-value="value"
      :placeholder="widgetConfig?.placeholder || '请输入'"
      @update:model-value="$emit('update:value', $event)"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import type { WidgetConfig } from '@/types/dataset'

interface FormFieldProps {
  fieldCode: string
  fieldLabel?: string
  widgetType: string
  widgetConfig?: WidgetConfig
  value: any
}

const props = defineProps<FormFieldProps>()
const emit = defineEmits<{
  'update:value': [value: any]
}>()
</script>
