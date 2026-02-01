/**
 * 表格列配置类型定义 - V4 格式
 */

/**
 * 列类型枚举
 */
export type ColumnType =
  | 'selection'    // 多选列
  | 'index'        // 序号列
  | 'text'         // 普通文本（默认）
  | 'image'        // 图片
  | 'tag'          // 标签
  | 'switch'       // 开关
  | 'datetime'     // 日期时间
  | 'date'         // 日期
  | 'link'         // 链接
  | 'progress'     // 进度条
  | 'actions'      // 操作列

/**
 * 图片配置
 */
export interface ImageConfig {
  width: number              // 图片宽度（如 40）
  height: number             // 图片高度（如 40）
  fit: 'cover' | 'contain' | 'fill' | 'none' | 'scale-down'  // 填充方式
  radius: string             // 圆角（如 '50%' 为圆形，'4px' 为圆角）
  preview?: boolean          // 是否支持预览
}

/**
 * 标签配置
 */
export interface TagConfig {
  [key: string]: {           // 值映射（如 "1": {...}, "0": {...}）
    text: string             // 显示文本
    type: 'success' | 'warning' | 'danger' | 'info' | 'primary'
  }
}

/**
 * 日期配置
 */
export interface DateConfig {
  format: string             // 格式（如 'YYYY-MM-DD HH:mm:ss'）
}

/**
 * 开关配置
 */
export interface SwitchConfig {
  activeValue?: any          // 激活值（默认 true）
  inactiveValue?: any        // 未激活值（默认 false）
  disabled?: boolean         // 是否禁用
}

/**
 * 链接配置
 */
export interface LinkConfig {
  target?: '_blank' | '_self'
  href?: string | ((row: any) => string)
}

/**
 * 进度条配置
 */
export interface ProgressConfig {
  strokeWidth?: number       // 进度条宽度（默认 6）
  showText?: boolean         // 是否显示文字（默认 true）
  status?: 'success' | 'warning' | 'exception'  // 状态
}

/**
 * 操作按钮配置
 */
export interface ActionButton {
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

/**
 * 表格列配置 - V4 格式
 */
export interface TableColumnV4 {
  // 基础属性
  prop: string                    // 字段名
  label: string                   // 列标题

  // 列类型
  type: ColumnType                // 列类型枚举

  // 布局属性
  width?: number
  minWidth?: number
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right'
  sortable?: boolean
  showOverflowTooltip?: boolean

  // 类型特定配置
  imageConfig?: ImageConfig       // type = 'image' 时
  tagConfig?: TagConfig           // type = 'tag' 时
  dateConfig?: DateConfig         // type = 'date' | 'datetime' 时
  switchConfig?: SwitchConfig     // type = 'switch' 时
  linkConfig?: LinkConfig         // type = 'link' 时
  progressConfig?: ProgressConfig // type = 'progress' 时
  actions?: ActionButton[]        // type = 'actions' 时
}

/**
 * 表格配置
 */
export interface TableConfigV4 {
  stripe?: boolean
  border?: boolean
  highlightCurrentRow?: boolean
  showHeader?: boolean
  pagination?: boolean
  pageSize?: number
  pageSizes?: number[]
}

/**
 * 表格区域配置（用于页面模板）
 */
export interface TableAreaConfig {
  componentType: 'table'
  columns: TableColumnV4[]
  tableConfig?: TableConfigV4
}
