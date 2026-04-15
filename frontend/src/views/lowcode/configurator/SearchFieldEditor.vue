<template>
  <ArrayEditor
    :model-value="modelValue"
    title="搜索字段"
    dialog-title="编辑搜索字段"
    :get-item-label="(f: any) => f.label || f.field || '字段'"
    :get-item-type="(f: any) => f.type || 'input'"
    :create-default="() => ({ field: 'newField', label: '新字段', type: 'input', placeholder: '', clearable: true })"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #form="{ data }">
      <el-form label-width="80px">
        <el-form-item label="字段名">
          <el-input v-model="data.field" placeholder="对应查询参数名" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="data.label" placeholder="显示名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="data.type" style="width: 100%">
            <el-option label="输入框" value="input" />
            <el-option label="下拉选择" value="select" />
            <el-option label="日期" value="date" />
            <el-option label="日期范围" value="daterange" />
            <el-option label="数字" value="number" />
          </el-select>
        </el-form-item>
        <el-form-item label="占位文本">
          <el-input v-model="data.placeholder" placeholder="请输入占位文本" />
        </el-form-item>
        <el-form-item label="可清空">
          <el-switch v-model="data.clearable" />
        </el-form-item>
        <el-form-item label="宽度(px)">
          <el-input-number v-model="data.width" :min="80" :max="400" controls-position="right" />
        </el-form-item>
        <el-form-item v-if="data.type === 'select'" label="选项">
          <div class="options-editor">
            <div v-for="(opt, oi) in (data.options || [])" :key="oi" class="option-row">
              <el-input v-model="opt.label" placeholder="标签" size="small" style="width: 120px" />
              <el-input v-model="opt.value" placeholder="值" size="small" style="width: 120px" />
              <el-button size="small" type="danger" :icon="Delete" circle @click="data.options.splice(oi, 1)" />
            </div>
            <el-button size="small" @click="addOption(data)">+ 添加选项</el-button>
          </div>
        </el-form-item>
      </el-form>
    </template>
  </ArrayEditor>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'
import ArrayEditor from './ArrayEditor.vue'
import type { SearchFieldConfig } from '@/api/table-standard'

defineProps<{
  modelValue: SearchFieldConfig[]
}>()

defineEmits<{
  (e: 'update:modelValue', value: SearchFieldConfig[]): void
}>()

const addOption = (data: any) => {
  if (!data.options) data.options = []
  data.options.push({ label: '', value: '' })
}
</script>

<style scoped lang="scss">
.options-editor {
  .option-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 6px;
  }
}
</style>
