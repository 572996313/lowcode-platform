<template>
  <el-form :inline="true" :model="modelValue">
    <template v-for="field in fields" :key="field.field">
      <el-form-item :label="field.label">
        <!-- 输入框 -->
        <el-input
          v-if="field.type === 'input'"
          :model-value="modelValue[field.field]"
          @update:model-value="val => updateField(field.field, val)"
          :placeholder="field.placeholder"
          :clearable="field.clearable !== false"
          :style="field.width ? { width: field.width + 'px' } : {}"
          @keyup.enter="emit('search')"
        />
        <!-- 下拉选择 -->
        <el-select
          v-else-if="field.type === 'select'"
          :model-value="modelValue[field.field]"
          @update:model-value="val => updateField(field.field, val)"
          :placeholder="field.placeholder"
          :clearable="field.clearable !== false"
          :style="field.width ? { width: field.width + 'px' } : {}"
        >
          <el-option v-for="opt in field.options" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <!-- 日期选择 -->
        <el-date-picker
          v-else-if="field.type === 'date'"
          :model-value="modelValue[field.field]"
          @update:model-value="val => updateField(field.field, val)"
          :placeholder="field.placeholder"
          :clearable="field.clearable !== false"
          value-format="YYYY-MM-DD"
        />
        <!-- 日期范围 -->
        <el-date-picker
          v-else-if="field.type === 'daterange'"
          :model-value="modelValue[field.field]"
          @update:model-value="val => updateField(field.field, val)"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.type === 'number'"
          :model-value="modelValue[field.field]"
          @update:model-value="val => updateField(field.field, val)"
          :placeholder="field.placeholder"
          :style="field.width ? { width: field.width + 'px' } : {}"
        />
      </el-form-item>
    </template>
    <el-form-item>
      <el-button type="primary" @click="emit('search')">查询</el-button>
      <el-button @click="emit('reset')">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import type { SearchFieldConfig } from '@/api/table-standard'

const props = defineProps<{
  fields: SearchFieldConfig[]
  modelValue: Record<string, any>
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', data: Record<string, any>): void
  (e: 'search'): void
  (e: 'reset'): void
}>()

const updateField = (field: string, value: any) => {
  emit('update:modelValue', { ...props.modelValue, [field]: value })
}
</script>
