<template>
  <div class="property-panel-v6">
    <div class="panel-header">
      <h3>属性配置</h3>
    </div>

    <div class="panel-content">
      <el-empty v-if="!selected" description="请选择要编辑的组件或区域" />

      <!-- 工具栏配置 -->
      <div v-else-if="selected.type === 'toolbar'" class="config-form">
        <div class="config-title">工具栏配置</div>
        <el-form label-position="top" size="small">
          <el-form-item label="是否启用">
            <el-switch v-model="toolbarConfig.enabled" />
          </el-form-item>
          <el-form-item label="对齐方式">
            <el-radio-group v-model="toolbarConfig.align">
              <el-radio-button label="left">左对齐</el-radio-button>
              <el-radio-button label="center">居中</el-radio-button>
              <el-radio-button label="right">右对齐</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-divider>按钮列表</el-divider>
          <div v-if="toolbarConfig.buttons.length === 0" class="empty-tip">
            暂无按钮，请从底部组件库拖拽
          </div>
          <div v-else class="item-list">
            <div
              v-for="(btn, index) in toolbarConfig.buttons"
              :key="btn.id"
              class="list-item"
            >
              <el-icon :size="16"><Menu /></el-icon>
              <span>{{ btn.name }}</span>
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                text
                @click="removeButton(index)"
              />
            </div>
          </div>
        </el-form>
      </div>

      <!-- 查询区配置 -->
      <div v-else-if="selected.type === 'search'" class="config-form">
        <div class="config-title">查询区配置</div>
        <el-form label-position="top" size="small">
          <el-form-item label="是否启用">
            <el-switch v-model="searchConfig.enabled" />
          </el-form-item>
          <el-form-item label="是否可折叠">
            <el-switch v-model="searchConfig.collapsible" />
          </el-form-item>
          <el-form-item label="默认折叠">
            <el-switch v-model="searchConfig.collapsed" />
          </el-form-item>
          <el-form-item label="标签宽度">
            <el-input-number v-model="searchConfig.labelWidth" :min="40" :max="200" :step="10" />
          </el-form-item>
          <el-form-item label="每行显示列数">
            <el-radio-group v-model="searchConfig.layout.span">
              <el-radio-button :label="24">1列</el-radio-button>
              <el-radio-button :label="12">2列</el-radio-button>
              <el-radio-button :label="8">3列</el-radio-button>
              <el-radio-button :label="6">4列</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-divider>字段列表</el-divider>
          <div v-if="searchConfig.fields.length === 0" class="empty-tip">
            暂无字段，请从底部组件库拖拽
          </div>
          <div v-else class="item-list">
            <div
              v-for="(field, index) in searchConfig.fields"
              :key="field.id"
              class="list-item"
            >
              <el-icon :size="16"><Menu /></el-icon>
              <span>{{ field.label }} ({{ field.fieldType }})</span>
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                text
                @click="removeField(index)"
              />
            </div>
          </div>
        </el-form>
      </div>

      <!-- 表格配置 -->
      <div v-else-if="selected.type === 'table'" class="config-form">
        <div class="config-title">表格配置</div>
        <el-form label-position="top" size="small">
          <el-form-item label="斑马纹">
            <el-switch v-model="tableConfig.stripe" />
          </el-form-item>
          <el-form-item label="边框">
            <el-switch v-model="tableConfig.border" />
          </el-form-item>
          <el-form-item label="高亮当前行">
            <el-switch v-model="tableConfig.highlightCurrentRow" />
          </el-form-item>
          <el-form-item label="显示表头">
            <el-switch v-model="tableConfig.showHeader" />
          </el-form-item>
          <el-form-item label="分页">
            <el-switch v-model="tableConfig.pagination" />
          </el-form-item>
          <el-form-item label="每页条数">
            <el-input-number v-model="tableConfig.pageSize" :min="10" :max="100" :step="10" />
          </el-form-item>
          <el-divider>列列表</el-divider>
          <div v-if="tableConfig.columns.length === 0" class="empty-tip">
            暂无列，请从底部组件库拖拽
          </div>
          <div v-else class="item-list">
            <div
              v-for="(col, index) in tableConfig.columns"
              :key="col.id"
              class="list-item"
            >
              <el-icon :size="16"><Menu /></el-icon>
              <span>{{ col.label }} ({{ col.type }})</span>
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                text
                @click="removeColumn(index)"
              />
            </div>
          </div>
          <el-divider>行操作</el-divider>
          <div v-if="!tableConfig.rowActions?.length" class="empty-tip">
            暂无行操作，请从底部组件库拖拽
          </div>
          <div v-else class="item-list">
            <div
              v-for="(action, index) in tableConfig.rowActions"
              :key="action.id"
              class="list-item"
            >
              <el-icon :size="16"><Menu /></el-icon>
              <span>{{ action.name }}</span>
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                text
                @click="removeRowAction(index)"
              />
            </div>
          </div>
        </el-form>
      </div>

      <!-- 工具栏按钮配置 -->
      <div v-else-if="selected.type === 'button'" class="config-form">
        <div class="config-title">按钮配置</div>

        <!-- 模板引用信息 -->
        <div v-if="buttonConfig?.templateId" class="template-info">
          <el-alert type="info" :closable="false">
            <template #title>
              <div class="template-header">
                <el-icon><Link /></el-icon>
                <span>来自模板：{{ buttonConfig.templateName || '按钮模板 #' + buttonConfig.templateId }}</span>
              </div>
            </template>
            <div class="template-actions">
              <el-button size="small" type="primary" @click="handleSyncFromTemplate">
                从模板同步
              </el-button>
              <el-button size="small" @click="handleUnlinkTemplate">
                断开引用
              </el-button>
            </div>
          </el-alert>
        </div>

        <el-form label-position="top" size="small">
          <el-form-item label="按钮名称">
            <el-input v-model="buttonConfig.name" />
          </el-form-item>
          <el-form-item label="按钮类型">
            <el-select v-model="buttonConfig.type">
              <el-option label="主要" value="primary" />
              <el-option label="成功" value="success" />
              <el-option label="警告" value="warning" />
              <el-option label="危险" value="danger" />
              <el-option label="信息" value="info" />
              <el-option label="默认" value="default" />
            </el-select>
          </el-form-item>
          <el-form-item label="是否显示">
            <el-switch v-model="buttonConfig.visible" />
          </el-form-item>
          <el-form-item label="是否禁用">
            <el-switch v-model="buttonConfig.disabled" />
          </el-form-item>
          <el-divider>动作配置</el-divider>
          <el-form-item label="动作类型">
            <el-select v-model="buttonConfig.action.type">
              <el-option label="新增" value="add" />
              <el-option label="编辑" value="edit" />
              <el-option label="删除" value="delete" />
              <el-option label="导出" value="export" />
              <el-option label="刷新" value="refresh" />
              <el-option label="自定义" value="custom" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="buttonConfig.action.confirmMessage" label="确认提示">
            <el-input v-model="buttonConfig.action.confirmMessage" type="textarea" />
          </el-form-item>
          <el-form-item v-if="buttonConfig.action.successMessage" label="成功提示">
            <el-input v-model="buttonConfig.action.successMessage" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 查询字段配置 -->
      <div v-else-if="selected.type === 'field'" class="config-form">
        <div class="config-title">字段配置</div>
        <el-form label-position="top" size="small">
          <el-form-item label="字段编码">
            <el-input v-model="fieldConfig.fieldCode" />
          </el-form-item>
          <el-form-item label="字段标签">
            <el-input v-model="fieldConfig.label" />
          </el-form-item>
          <el-form-item label="占位提示">
            <el-input v-model="fieldConfig.placeholder" />
          </el-form-item>
          <el-form-item label="栅格宽度">
            <el-select v-model="fieldConfig.span">
              <el-option :label="6" :value="6">1/4宽度</el-option>
              <el-option :label="8" :value="8">1/3宽度</el-option>
              <el-option :label="12" :value="12">1/2宽度</el-option>
              <el-option :label="24" :value="24">全宽度</el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否必填">
            <el-switch v-model="fieldConfig.required" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格列配置 -->
      <div v-else-if="selected.type === 'column'" class="config-form">
        <div class="config-title">列配置</div>
        <el-form label-position="top" size="small">
          <el-form-item label="字段名">
            <el-input v-model="columnConfig.prop" />
          </el-form-item>
          <el-form-item label="列标题">
            <el-input v-model="columnConfig.label" />
          </el-form-item>
          <el-form-item label="列宽度">
            <el-input-number v-model="columnConfig.width" :min="50" :max="500" :step="10" />
          </el-form-item>
          <el-form-item label="对齐方式">
            <el-radio-group v-model="columnConfig.align">
              <el-radio-button label="left">左对齐</el-radio-button>
              <el-radio-button label="center">居中</el-radio-button>
              <el-radio-button label="right">右对齐</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="溢出提示">
            <el-switch v-model="columnConfig.showOverflowTooltip" />
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Menu, Delete, Link } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type {
  SelectedObject,
  ToolbarConfig,
  SearchConfig,
  TableConfig,
  ToolbarButton,
  SearchField,
  TableColumn
} from '@/types/page-v6'

// Props
interface Props {
  selected?: SelectedObject | null
  pageConfig?: any
}

// Emits
interface Emits {
  (e: 'update:selected', value: SelectedObject | null): void
  (e: 'update:pageConfig', value: any): void
  (e: 'update'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 工具栏配置
const toolbarConfig = computed({
  get: () => props.selected?.type === 'toolbar' ? props.selected.data as ToolbarConfig : null,
  set: (val) => {
    if (props.selected?.type === 'toolbar') {
      emit('update:selected', { type: 'toolbar', data: val })
      emit('update')
    }
  }
})

// 查询区配置
const searchConfig = computed({
  get: () => props.selected?.type === 'search' ? props.selected.data as SearchConfig : null,
  set: (val) => {
    if (props.selected?.type === 'search') {
      emit('update:selected', { type: 'search', data: val })
      emit('update')
    }
  }
})

// 表格配置
const tableConfig = computed({
  get: () => props.selected?.type === 'table' ? props.selected.data as TableConfig : null,
  set: (val) => {
    if (props.selected?.type === 'table') {
      emit('update:selected', { type: 'table', data: val })
      emit('update')
    }
  }
})

// 按钮配置（支持模板引用）
const buttonConfig = computed({
  get: () => {
    if (props.selected?.type !== 'button') return null
    const data = props.selected.data as ToolbarButton

    // 如果是引用模式，返回合并后的配置用于显示
    if (data.isLinked && data.overwrite) {
      return {
        ...data,
        ...data.overwrite
      }
    }
    return data
  },
  set: (val) => {
    if (props.selected?.type === 'button' && props.pageConfig) {
      const buttons = props.pageConfig.toolbar.buttons
      const index = buttons.findIndex((b: ToolbarButton) => b.id === val.id)
      if (index > -1) {
        const target = buttons[index]

        // 如果是引用模式，更新 overwrite
        if (target.isLinked) {
          // 计算差异，存储到 overwrite
          const original = { ...target }
          delete original.overwrite
          target.overwrite = calculateDiff(original, val)
        } else {
          // 独立模式，直接更新
          buttons[index] = val
        }

        emit('update:selected', { ...props.selected, data: val })
        emit('update')
      }
    }
  }
})

// 字段配置
const fieldConfig = computed({
  get: () => props.selected?.type === 'field' ? props.selected.data as SearchField : null,
  set: (val) => {
    if (props.selected?.type === 'field' && props.pageConfig) {
      const fields = props.pageConfig.search.fields
      const index = fields.findIndex((f: SearchField) => f.id === val.id)
      if (index > -1) {
        const target = fields[index]
        if (target.isLinked) {
          const original = { ...target }
          delete original.overwrite
          target.overwrite = calculateDiff(original, val)
        } else {
          fields[index] = val
        }
        emit('update:selected', { ...props.selected, data: val })
        emit('update')
      }
    }
  }
})

// 列配置
const columnConfig = computed({
  get: () => props.selected?.type === 'column' ? props.selected.data as TableColumn : null,
  set: (val) => {
    if (props.selected?.type === 'column' && props.pageConfig) {
      const columns = props.pageConfig.table.columns
      const index = columns.findIndex((c: TableColumn) => c.id === val.id)
      if (index > -1) {
        const target = columns[index]
        if (target.isLinked) {
          const original = { ...target }
          delete original.overwrite
          target.overwrite = calculateDiff(original, val)
        } else {
          columns[index] = val
        }
        emit('update:selected', { ...props.selected, data: val })
        emit('update')
      }
    }
  }
})

/**
 * 计算配置差异
 */
function calculateDiff(original: any, modified: any): any {
  const diff: any = {}
  for (const key in modified) {
    if (JSON.stringify(original[key]) !== JSON.stringify(modified[key])) {
      diff[key] = modified[key]
    }
  }
  return diff
}

/**
 * 从模板同步
 */
async function handleSyncFromTemplate() {
  try {
    await ElMessageBox.confirm(
      '同步将覆盖当前的自定义配置，是否继续？',
      '确认同步',
      {
        type: 'warning',
        confirmButtonText: '确认同步',
        cancelButtonText: '取消'
      }
    )

    // TODO: 调用 API 获取最新模板配置
    ElMessage.success('同步成功')
  } catch (error) {
    // 用户取消
  }
}

/**
 * 断开模板引用
 */
async function handleUnlinkTemplate() {
  try {
    await ElMessageBox.confirm(
      '断开后将无法接收模板更新，是否继续？',
      '确认断开',
      {
        type: 'warning',
        confirmButtonText: '确认断开',
        cancelButtonText: '取消'
      }
    )

    if (props.selected?.type === 'button' && props.pageConfig) {
      const buttons = props.pageConfig.toolbar.buttons
      const index = buttons.findIndex((b: ToolbarButton) => b.id === props.selected.data.id)
      if (index > -1) {
        const target = buttons[index]

        // 合并模板配置和 overwrite
        const mergedConfig = {
          ...target,
          ...target.overwrite
        }

        // 移除引用字段
        delete mergedConfig.templateId
        delete mergedConfig.templateType
        delete mergedConfig.isLinked
        delete mergedConfig.overwrite
        delete mergedConfig.templateName

        buttons[index] = mergedConfig
        emit('update:selected', { type: 'button', data: mergedConfig, parent: 'toolbar' })
        emit('update')
        ElMessage.success('已断开引用')
      }
    }
  } catch (error) {
    // 用户取消
  }
}

/**
 * 删除按钮
 */
function removeButton(index: number) {
  if (props.pageConfig) {
    props.pageConfig.toolbar.buttons.splice(index, 1)
    emit('update:selected', null)
    emit('update')
  }
}

/**
 * 删除字段
 */
function removeField(index: number) {
  if (props.pageConfig) {
    props.pageConfig.search.fields.splice(index, 1)
    emit('update:selected', null)
    emit('update')
  }
}

/**
 * 删除列
 */
function removeColumn(index: number) {
  if (props.pageConfig) {
    props.pageConfig.table.columns.splice(index, 1)
    emit('update:selected', null)
    emit('update')
  }
}

/**
 * 删除行操作
 */
function removeRowAction(index: number) {
  if (props.pageConfig && props.pageConfig.table.rowActions) {
    props.pageConfig.table.rowActions.splice(index, 1)
    emit('update:selected', null)
    emit('update')
  }
}
</script>

<style scoped lang="scss">
.property-panel-v6 {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-left: 1px solid #e4e7ed;
}

.panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;

  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 500;
    color: #303133;
  }
}

.panel-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.config-form {
  .config-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e4e7ed;
  }

  .empty-tip {
    padding: 20px;
    text-align: center;
    color: #909399;
    font-size: 13px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .item-list {
    .list-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 8px;

      > span {
        flex: 1;
        font-size: 13px;
        color: #606266;
      }
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 16px;
  }

  :deep(.el-divider) {
    margin: 20px 0;
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
    color: #606266;
  }

  // 模板引用信息样式
  .template-info {
    margin-bottom: 16px;

    .template-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
    }

    .template-actions {
      margin-top: 12px;
      display: flex;
      gap: 8px;
    }

    :deep(.el-alert__title) {
      font-size: 13px;
    }
  }
}
</style>
