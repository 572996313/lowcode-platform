<template>
  <ArrayEditor
    :model-value="modelValue"
    title="表格列"
    dialog-title="编辑列"
    :get-item-label="(c: any) => c.label || c.prop || '列'"
    :get-item-type="(c: any) => c.type || 'text'"
    :create-default="() => ({ prop: 'newColumn', label: '新列', width: 150 })"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #form="{ data }">
      <el-form label-width="80px">
        <el-form-item label="列标题">
          <el-input v-model="data.label" placeholder="列标题" />
        </el-form-item>
        <el-form-item label="字段名">
          <el-input v-model="data.prop" placeholder="对应数据字段" :disabled="data.type === 'index' || data.type === 'selection'" />
        </el-form-item>
        <el-form-item label="列类型">
          <el-select v-model="data.type" style="width: 100%" clearable>
            <el-option label="文本" value="text" />
            <el-option label="标签" value="tag" />
            <el-option label="日期" value="date" />
            <el-option label="序号" value="index" />
            <el-option label="选择框" value="selection" />
            <el-option label="操作" value="action" />
          </el-select>
        </el-form-item>
        <el-form-item label="宽度">
          <el-input-number v-model="data.width" :min="50" :max="500" controls-position="right" />
        </el-form-item>
        <el-form-item label="最小宽度">
          <el-input-number v-model="data.minWidth" :min="50" :max="500" controls-position="right" />
        </el-form-item>
        <el-form-item label="对齐">
          <el-select v-model="data.align" style="width: 100%" clearable>
            <el-option label="左" value="left" />
            <el-option label="中" value="center" />
            <el-option label="右" value="right" />
          </el-select>
        </el-form-item>
        <el-form-item label="固定">
          <el-select v-model="data.fixed" style="width: 100%" clearable>
            <el-option label="不固定" :value="false" />
            <el-option label="左固定" value="left" />
            <el-option label="右固定" value="right" />
          </el-select>
        </el-form-item>
        <el-form-item label="Tooltip">
          <el-switch v-model="data.showOverflowTooltip" />
        </el-form-item>

        <!-- tag 类型：映射配置 -->
        <template v-if="data.type === 'tag'">
          <el-divider content-position="left">Tag 映射配置</el-divider>
          <el-form-item label="Tag 映射">
            <div class="mapping-editor">
              <div v-for="(entry, key) in (data.tagConfig?.mapping || {})" :key="key" class="mapping-row">
                <el-input :model-value="key" size="small" style="width: 80px" disabled />
                <el-input v-model="entry.text" size="small" placeholder="显示文本" style="width: 100px" />
                <el-select v-model="entry.type" size="small" style="width: 100px" clearable>
                  <el-option label="默认" value="" />
                  <el-option label="成功" value="success" />
                  <el-option label="警告" value="warning" />
                  <el-option label="危险" value="danger" />
                  <el-option label="信息" value="info" />
                </el-select>
                <el-button size="small" type="danger" :icon="Delete" circle @click="removeMapping(data, String(key))" />
              </div>
              <div class="add-mapping-row">
                <el-input v-model="newMappingKey" size="small" placeholder="值" style="width: 80px" />
                <el-button size="small" @click="addMapping(data)">添加</el-button>
              </div>
            </div>
          </el-form-item>
        </template>

        <!-- action 类型：按钮配置 -->
        <template v-if="data.type === 'action'">
          <el-divider content-position="left">操作按钮</el-divider>
          <div class="action-buttons-editor">
            <div v-for="(btn, bi) in (data.actionConfig?.buttons || [])" :key="bi" class="action-btn-item">
              <div class="action-btn-basic">
                <el-input v-model="btn.label" size="small" placeholder="文字" style="width: 70px" />
                <el-select v-model="btn.btnType" size="small" style="width: 80px" clearable>
                  <el-option label="主要" value="primary" />
                  <el-option label="危险" value="danger" />
                  <el-option label="成功" value="success" />
                  <el-option label="警告" value="warning" />
                </el-select>
                <el-input v-model="btn.action" size="small" placeholder="操作" style="width: 70px" />
                <el-button size="small" type="danger" :icon="Delete" circle @click="data.actionConfig.buttons.splice(bi, 1)" />
              </div>
              <div class="action-btn-config">
                <el-select
                  :model-value="btn.actionConfig?.type || 'none'"
                  size="small"
                  style="width: 100px"
                  placeholder="动作"
                  @update:model-value="val => setBtnAction(btn, 'type', val)"
                >
                  <el-option label="无动作" value="none" />
                  <el-option label="打开表单" value="openForm" />
                  <el-option label="打开表格" value="openTable" />
                  <el-option label="路由跳转" value="route" />
                  <el-option label="自定义" value="custom" />
                </el-select>
                <el-select
                  v-if="btn.actionConfig?.type === 'openForm' || btn.actionConfig?.type === 'openTable'"
                  :model-value="btn.actionConfig?.targetCode || ''"
                  size="small"
                  style="width: 120px"
                  clearable
                  placeholder="目标"
                  @update:model-value="val => setBtnAction(btn, 'targetCode', val)"
                >
                  <el-option
                    v-for="entry in getAvailableTargets(btn.actionConfig?.type)"
                    :key="entry.code"
                    :label="entry.name"
                    :value="entry.code"
                  />
                </el-select>
                <el-select
                  v-if="btn.actionConfig?.type === 'openForm' || btn.actionConfig?.type === 'openTable'"
                  :model-value="btn.actionConfig?.openMode || 'dialog'"
                  size="small"
                  style="width: 90px"
                  @update:model-value="val => setBtnAction(btn, 'openMode', val)"
                >
                  <el-option label="弹窗" value="dialog" />
                  <el-option label="抽屉" value="drawer" />
                  <el-option label="页面" value="page" />
                </el-select>
              </div>
            </div>
            <el-button size="small" @click="addActionBtn(data)">+ 添加按钮</el-button>
          </div>
        </template>
      </el-form>
    </template>
  </ArrayEditor>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import ArrayEditor from './ArrayEditor.vue'
import type { TableColumnConfig } from '@/api/table-standard'
import { getConfigsByType } from '@/utils/configRegistry'

defineProps<{
  modelValue: TableColumnConfig[]
}>()

defineEmits<{
  (e: 'update:modelValue', value: TableColumnConfig[]): void
}>()

const newMappingKey = ref('')

const addMapping = (data: any) => {
  if (!newMappingKey.value) return
  if (!data.tagConfig) data.tagConfig = { mapping: {} }
  data.tagConfig.mapping[newMappingKey.value] = { text: '', type: '' }
  newMappingKey.value = ''
}

const removeMapping = (data: any, key: string) => {
  delete data.tagConfig.mapping[key]
}

const addActionBtn = (data: any) => {
  if (!data.actionConfig) data.actionConfig = { buttons: [] }
  data.actionConfig.buttons.push({ label: '按钮', action: 'custom', btnType: 'primary', size: 'small' })
}

const getAvailableTargets = (type?: string) => {
  if (type === 'openForm') return getConfigsByType('form')
  if (type === 'openTable') return getConfigsByType('table')
  return []
}

const setBtnAction = (btn: any, key: string, value: any) => {
  if (!btn.actionConfig) btn.actionConfig = { type: 'custom' }
  if (key === 'type') {
    if (value === 'none') {
      btn.actionConfig = undefined
    } else {
      btn.actionConfig = { type: value }
    }
  } else if (btn.actionConfig) {
    btn.actionConfig[key] = value
  }
}
</script>

<style scoped lang="scss">
.mapping-editor {
  .mapping-row {
    display: flex;
    gap: 6px;
    align-items: center;
    margin-bottom: 6px;
  }

  .add-mapping-row {
    display: flex;
    gap: 6px;
    align-items: center;
  }
}

.action-buttons-editor {
  .action-btn-item {
    margin-bottom: 8px;
    padding: 8px;
    background: #f5f7fa;
    border-radius: 6px;

    .action-btn-basic {
      display: flex;
      gap: 6px;
      align-items: center;
      margin-bottom: 6px;
    }

    .action-btn-config {
      display: flex;
      gap: 6px;
      align-items: center;
      padding-left: 4px;
    }
  }
}
</style>
