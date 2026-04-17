/**
 * 表格配置类型定义
 * 统一使用 table-standard.ts 中的标准页类型
 * 旧版 V1-V4 类型已全部移除
 */

// 从标准页 API 重新导出所有类型，供表格设计器和渲染组件使用
export type {
  SearchFieldType,
  SearchFieldConfig,
  OptionItem,
  ColumnType,
  TableColumnConfig,
  TagColumnConfig,
  ActionColumnConfig,
  ActionButton,
  TableConfig,
  SelectionMode,
  ButtonActionConfig,
  ToolbarButton,
  ToolbarConfig,
  PageConfigResponse
} from '@/api/table-standard'

// 保留旧版操作按钮配置（供 render/TableColumnRender.vue 兼容使用）
export interface ActionButtonLegacy {
  id: string
  label: string
  buttonCode: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  icon?: string
  link?: boolean
  confirm?: boolean
  confirmTitle?: string
  confirmMessage?: string
}
