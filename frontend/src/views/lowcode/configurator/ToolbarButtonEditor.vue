<template>
  <ArrayEditor
    :model-value="modelValue"
    title="工具栏按钮"
    dialog-title="编辑按钮"
    :get-item-label="(b: any) => b.label || '按钮'"
    :get-item-type="(b: any) => getActionTypeLabel(b)"
    :create-default="createDefaultButton"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #form="{ data }">
      <el-form label-width="80px">
        <el-form-item label="按钮文字">
          <el-input v-model="data.label" placeholder="如: 新增、导出" />
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
          <el-input v-model="data.action" placeholder="如: add、export" />
        </el-form-item>

        <el-divider content-position="left">动作配置</el-divider>
        <el-form-item label="动作类型">
          <el-select
            :model-value="data.actionConfig?.type || 'custom'"
            style="width: 100%"
            @update:model-value="val => setActionConfig(data, 'type', val)"
          >
            <el-option label="打开表单" value="openForm" />
            <el-option label="打开表格" value="openTable" />
            <el-option label="路由跳转" value="route" />
            <el-option label="提交数据" value="submit" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>

        <!-- openForm / openTable 的目标配置 -->
        <template v-if="(data.actionConfig?.type === 'openForm' || data.actionConfig?.type === 'openTable')">
          <el-form-item :label="data.actionConfig.type === 'openForm' ? '目标表单' : '目标表格'">
            <el-select
              :model-value="data.actionConfig?.targetCode || ''"
              style="width: 100%"
              clearable
              placeholder="选择已注册的配置"
              @update:model-value="val => setActionConfig(data, 'targetCode', val)"
            >
              <el-option
                v-for="entry in getAvailableTargets(data.actionConfig?.type)"
                :key="entry.code"
                :label="`${entry.name} (${entry.code})`"
                :value="entry.code"
              />
              <template #empty>
                <div style="padding: 10px; color: #909399; font-size: 12px;">
                  暂无配置，请先在表单/表格配置器中保存配置
                </div>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="打开方式">
            <el-select
              :model-value="data.actionConfig?.openMode || 'dialog'"
              style="width: 100%"
              @update:model-value="val => setActionConfig(data, 'openMode', val)"
            >
              <el-option label="弹窗 (dialog)" value="dialog" />
              <el-option label="抽屉 (drawer)" value="drawer" />
              <el-option label="整页面 (page)" value="page" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 路由跳转配置 -->
        <template v-if="data.actionConfig?.type === 'route'">
          <el-form-item label="路由路径">
            <el-input
              :model-value="data.actionConfig?.routePath || ''"
              placeholder="/some/path"
              @update:model-value="val => setActionConfig(data, 'routePath', val)"
            />
          </el-form-item>
        </template>

        <!-- 提交数据配置 -->
        <template v-if="data.actionConfig?.type === 'submit'">
          <el-form-item label="选择模式">
            <el-select
              :model-value="data.actionConfig?.selectionMode || 'none'"
              style="width: 100%"
              @update:model-value="val => setActionConfig(data, 'selectionMode', val)"
            >
              <el-option label="无需选择" value="none" />
              <el-option label="单选（选一条）" value="single" />
              <el-option label="多选（选多条）" value="multiple" />
            </el-select>
          </el-form-item>
          <el-form-item label="确认提示">
            <el-input
              :model-value="data.actionConfig?.confirmText || ''"
              placeholder="如: 确定要提交选中的数据吗？"
              @update:model-value="val => setActionConfig(data, 'confirmText', val)"
            />
          </el-form-item>
        </template>
      </el-form>
    </template>
  </ArrayEditor>
</template>

<script setup lang="ts">
import ArrayEditor from './ArrayEditor.vue'
import type { ToolbarButton } from '@/api/table-standard'
import { getConfigsByType } from '@/utils/configRegistry'

defineProps<{
  modelValue: ToolbarButton[]
}>()

defineEmits<{
  (e: 'update:modelValue', value: ToolbarButton[]): void
}>()

const createDefaultButton = () => ({
  label: '新按钮',
  action: 'custom',
  btnType: '',
  actionConfig: { type: 'custom' as const }
})

const getActionTypeLabel = (btn: any): string => {
  const t = btn.actionConfig?.type
  if (t === 'openForm') return '打开表单'
  if (t === 'openTable') return '打开表格'
  if (t === 'route') return '路由跳转'
  if (t === 'submit') {
    const mode = btn.actionConfig?.selectionMode
    if (mode === 'multiple') return '提交(多选)'
    if (mode === 'single') return '提交(单选)'
    return '提交数据'
  }
  return btn.action || '自定义'
}

const getAvailableTargets = (type?: string) => {
  if (type === 'openForm') return getConfigsByType('form')
  if (type === 'openTable') return getConfigsByType('table')
  return []
}

/** 安全设置 actionConfig 字段 */
const setActionConfig = (data: any, key: string, value: any) => {
  if (!data.actionConfig) data.actionConfig = { type: 'custom' }
  if (key === 'type') {
    data.actionConfig = { type: value }
  } else {
    data.actionConfig[key] = value
  }
}
</script>
