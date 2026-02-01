<template>
  <div class="search-field-render">
    <el-input
      v-if="field.fieldType === 'input'"
      v-model="internalValue"
      :placeholder="field.placeholder"
    />
    <el-input
      v-else-if="field.fieldType === 'textarea'"
      v-model="internalValue"
      type="textarea"
      :rows="2"
      :placeholder="field.placeholder"
    />
    <el-select
      v-else-if="field.fieldType === 'select'"
      v-model="internalValue"
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
    <el-date-picker
      v-else-if="field.fieldType === 'date'"
      v-model="internalValue"
      type="date"
      :placeholder="field.placeholder"
      value-format="YYYY-MM-DD"
      style="width: 100%"
    />
    <el-date-picker
      v-else-if="field.fieldType === 'datetime'"
      v-model="internalValue"
      type="datetime"
      :placeholder="field.placeholder"
      value-format="YYYY-MM-DD HH:mm:ss"
      style="width: 100%"
    />
    <el-input-number
      v-else-if="field.fieldType === 'number'"
      v-model="internalValue"
      v-bind="field.numberConfig"
      style="width: 100%"
    />
    <el-switch
      v-else-if="field.fieldType === 'switch'"
      v-model="internalValue"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { SearchField } from '@/types/page-v6'

// Props
interface Props {
  field: SearchField
  modelValue: any
}

// Emits
interface Emits {
  (e: 'update:modelValue', value: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const internalValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
</script>
