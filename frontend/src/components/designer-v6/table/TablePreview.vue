<template>
  <div class="table-preview">
    <div v-if="config.columns.length === 0" class="empty-state">
      <el-icon><Plus /></el-icon>
      <p>暂无表格列</p>
      <span class="hint">从底部组件库拖拽列到此处</span>
    </div>

    <div v-else class="table-content">
      <el-table
        :data="mockData"
        :stripe="config.stripe"
        :border="config.border"
        :highlight-current-row="config.highlightCurrentRow"
        :show-header="config.showHeader"
        style="width: 100%"
      >
        <el-table-column
          v-for="col in config.columns"
          :key="col.id"
          :prop="col.prop"
          :label="col.label"
          :width="col.width"
          :min-width="col.minWidth"
          :align="col.align"
          :fixed="col.fixed"
          :sortable="col.sortable ? 'custom' : false"
          :show-overflow-tooltip="col.showOverflowTooltip"
        >
          <template #default="{ row }">
            <div
              class="cell-wrapper"
              :class="{ 'is-selected': isSelected(col) }"
              @click.stop="handleSelectColumn(col)"
            >
              <ColumnRenderer :column="col" :value="row[col.prop]" />
            </div>
          </template>
        </el-table-column>

        <!-- 行操作列 -->
        <el-table-column
          v-if="config.rowActions && config.rowActions.length > 0"
          label="操作"
          :width="config.rowActions.length * 70"
          fixed="right"
          align="center"
        >
          <template #default="{ row }">
            <div class="row-actions-wrapper">
              <el-button
                v-for="action in config.rowActions"
                :key="action.id"
                :type="action.type"
                size="small"
                text
                @click.stop="handleSelectRowAction(action)"
              >
                {{ action.name }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div v-if="config.pagination" class="pagination-wrapper">
        <el-pagination
          :current-page="1"
          :page-size="config.pageSize"
          :page-sizes="config.pageSizes"
          :total="mockData.length"
          layout="total, sizes, prev, pager, next, jumper"
          small
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElTag, ElImage, ElSwitch, ElProgress, ElLink } from 'element-plus'
import type { TableConfig, SelectedObject, TableColumn, RowAction } from '@/types/page-v6'

// Props
interface Props {
  config: TableConfig
  selected?: SelectedObject | null
}

// Emits
interface Emits {
  (e: 'update:selected', value: SelectedObject): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 模拟数据
const mockData = ref([
  { id: 1, name: '张三', age: 25, status: 'active', avatar: 'https://via.placeholder.com/40', progress: 60 },
  { id: 2, name: '李四', age: 30, status: 'inactive', avatar: 'https://via.placeholder.com/40', progress: 80 },
  { id: 3, name: '王五', age: 28, status: 'active', avatar: 'https://via.placeholder.com/40', progress: 45 },
])

/**
 * 列渲染器
 */
const ColumnRenderer = (props: { column: TableColumn, value: any }) => {
  const { column, value } = props

  switch (column.type) {
    case 'tag':
      const tagConfig = column.tagConfig?.[value]
      return h(ElTag, { type: tagConfig?.type || 'info' }, () => tagConfig?.text || value)

    case 'image':
      return h(ElImage, {
        src: value,
        ...column.imageConfig,
        preview: true
      })

    case 'switch':
      return h(ElSwitch, {
        modelValue: value,
        ...column.switchConfig,
        disabled: true
      })

    case 'progress':
      return h(ElProgress, {
        percentage: value,
        strokeWidth: 6
      })

    case 'link':
      const href = column.linkConfig?.href.replace('{value}', value) || value
      return h(ElLink, {
        href,
        target: column.linkConfig?.target || '_self'
      }, () => value)

    default:
      return h('span', value || '-')
  }
}

/**
 * 判断列是否被选中
 */
function isSelected(col: TableColumn): boolean {
  return props.selected?.type === 'column' &&
    props.selected.data.id === col.id
}

/**
 * 选中列
 */
function handleSelectColumn(col: TableColumn) {
  emit('update:selected', {
    type: 'column',
    data: col,
    parent: 'table'
  })
}

/**
 * 选中行操作
 */
function handleSelectRowAction(action: RowAction) {
  emit('update:selected', {
    type: 'rowAction',
    data: action,
    parent: 'table'
  })
}
</script>

<style scoped lang="scss">
.table-preview {
  padding: 16px;
  min-height: 200px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;

  .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
    color: #c0c4cc;
  }

  p {
    margin: 0 0 4px;
    font-size: 14px;
    font-weight: 500;
  }

  .hint {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.table-content {
  .cell-wrapper {
    display: inline-block;
    padding: 2px 4px;
    border: 2px solid transparent;
    border-radius: 4px;
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      border-color: #c0c4cc;
      background: #f5f7fa;
    }

    &.is-selected {
      border-color: #409eff;
      background: #ecf5ff;
    }
  }

  .row-actions-wrapper {
    display: flex;
    gap: 8px;
    justify-content: center;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  :deep(.el-table) {
    font-size: 13px;

    th {
      background: #f5f7fa;
    }
  }
}
</style>
