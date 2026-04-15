<template>
  <ArrayEditor
    :model-value="modelValue"
    title="工具栏按钮"
    dialog-title="编辑按钮"
    :get-item-label="(b: any) => b.label || '按钮'"
    :get-item-type="(b: any) => b.showInModes ? b.showInModes.join('/') : '全部'"
    :create-default="() => ({ label: '新按钮', action: 'custom', btnType: '' })"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #form="{ data }">
      <el-form label-width="80px">
        <el-form-item label="按钮文字">
          <el-input v-model="data.label" placeholder="如: 保存、提交" />
        </el-form-item>
        <el-form-item label="按钮类型">
          <el-select v-model="data.btnType" placeholder="默认" clearable style="width: 100%">
            <el-option label="默认" value="" />
            <el-option label="主要 (primary)" value="primary" />
            <el-option label="成功 (success)" value="success" />
            <el-option label="警告 (warning)" value="warning" />
            <el-option label="危险 (danger)" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="data.icon" placeholder="Element Plus 图标名" />
        </el-form-item>
        <el-form-item label="操作标识">
          <el-input v-model="data.action" placeholder="如: save、submit、edit、back" />
        </el-form-item>
        <el-form-item label="显示模式">
          <el-checkbox-group v-model="data.showInModes">
            <el-checkbox value="add">新增</el-checkbox>
            <el-checkbox value="edit">编辑</el-checkbox>
            <el-checkbox value="view">查看</el-checkbox>
          </el-checkbox-group>
          <div class="form-tip">不选则所有模式都显示</div>
        </el-form-item>
      </el-form>
    </template>
  </ArrayEditor>
</template>

<script setup lang="ts">
import ArrayEditor from './ArrayEditor.vue'
import type { FormToolbarButton } from '@/api/form-standard'

defineProps<{
  modelValue: FormToolbarButton[]
}>()

defineEmits<{
  (e: 'update:modelValue', value: FormToolbarButton[]): void
}>()
</script>

<style scoped lang="scss">
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
