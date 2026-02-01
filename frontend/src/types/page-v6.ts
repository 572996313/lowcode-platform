/**
 * 页面配置类型定义（V6 版本 - 列表页设计器）
 */

// ============= 基础类型 =============

/**
 * 字段类型
 */
export type FieldType =
  | 'input'        // 输入框
  | 'textarea'     // 文本域
  | 'select'       // 下拉框
  | 'radio'        // 单选框
  | 'checkbox'     // 多选框
  | 'date'         // 日期
  | 'datetime'     // 日期时间
  | 'number'       // 数字
  | 'switch'       // 开关

/**
 * 列类型
 */
export type ColumnType =
  | 'text'         // 文本
  | 'number'       // 数字
  | 'date'         // 日期
  | 'datetime'     // 日期时间
  | 'tag'          // 标签
  | 'image'        // 图片
  | 'link'         // 链接
  | 'switch'       // 开关
  | 'progress'     // 进度条

/**
 * 按钮类型
 */
export type ButtonType = 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'

/**
 * 按钮动作类型
 */
export type ButtonActionType = 'add' | 'edit' | 'delete' | 'export' | 'refresh' | 'custom'

/**
 * HTTP 方法
 */
export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

/**
 * 对齐方式
 */
export type AlignType = 'left' | 'center' | 'right'

// ============= 页面配置 =============

/**
 * 页面配置（新版 - 简化版）
 */
export interface NewPageConfig {
  version: 'v6'                    // 版本标识
  pageId?: number                  // 页面ID（编辑时有值）

  // 页面基本信息
  pageName: string                 // 页面名称
  pageCode: string                 // 页面编码
  description?: string             // 页面描述

  // 页面布局
  toolbar: ToolbarConfig           // 工具栏配置
  search: SearchConfig             // 查询区配置
  table: TableConfig               // 表格配置

  // 全局配置
  globalConfig: GlobalConfig

  // 元数据
  published?: boolean              // 是否已发布
  routePath?: string               // 路由路径
  createdAt?: string               // 创建时间
  updatedAt?: string               // 更新时间
}

/**
 * 全局配置
 */
export interface GlobalConfig {
  apiEndpoint?: string           // 数据接口
  pageSize?: number              // 每页条数
  pageSizes?: number[]           // 分页选项
  stripe?: boolean               // 斑马纹
  border?: boolean               // 边框
}

// ============= 工具栏配置 =============

/**
 * 工具栏配置
 */
export interface ToolbarConfig {
  enabled: boolean                 // 是否启用
  align: AlignType                // 对齐方式
  buttons: ToolbarButton[]         // 按钮列表
}

/**
 * 工具栏按钮
 */
export interface ToolbarButton {
  id: string                       // 按钮ID
  name: string                     // 按钮名称
  type: ButtonType                 // 按钮类型
  icon?: string                    // 图标（Element Plus 图标名）
  action: ButtonAction             // 按钮动作
  visible?: boolean                // 是否显示
  disabled?: boolean               // 是否禁用
  perms?: string                   // 权限标识

  // === 模板引用字段 ===
  templateId?: number              // 模板ID（来自按钮库）
  templateType?: 'button'          // 模板类型（固定为 'button'）
  isLinked?: boolean               // 是否保持引用关系（true=保持引用，false=独立副本）
  overwrite?: Partial<ToolbarButton> // 覆盖的配置（仅在 isLinked=true 时使用）
  templateName?: string            // 模板名称（仅用于显示）
}

/**
 * 按钮动作
 */
export interface ButtonAction {
  type: ButtonActionType           // 动作类型
  apiEndpoint?: string             // API 端点
  method?: HttpMethod              // HTTP 方法
  confirmMessage?: string          // 确认提示
  successMessage?: string          // 成功提示
  redirectUrl?: string             // 跳转地址
}

// ============= 查询区配置 =============

/**
 * 查询区配置
 */
export interface SearchConfig {
  enabled: boolean                 // 是否启用
  collapsible: boolean             // 是否可折叠
  collapsed: boolean               // 默认是否折叠
  labelWidth: number               // 标签宽度
  labelPosition: 'left' | 'right' | 'top'  // 标签位置
  fields: SearchField[]            // 查询字段列表
  layout: SearchLayout             // 布局配置
}

/**
 * 查询区布局
 */
export interface SearchLayout {
  span: number                     // 栅格宽度（一行几列）
  gap: number                      // 间距
}

/**
 * 查询字段
 */
export interface SearchField {
  id: string                       // 字段ID
  fieldCode: string                // 字段编码
  label: string                    // 字段标签
  fieldType: FieldType            // 字段类型
  placeholder?: string             // 占位提示
  defaultValue?: any               // 默认值
  required?: boolean               // 是否必填
  options?: FieldOption[]          // 选项（下拉/单选/多选）
  span?: number                    // 栅格占位（1-24）
  // 类型特定配置
  dateConfig?: DateFieldConfig     // 日期字段配置
  numberConfig?: NumberFieldConfig // 数字字段配置

  // === 模板引用字段 ===
  templateId?: number              // 模板ID（来自表单字段库）
  templateType?: 'field'           // 模板类型（固定为 'field'）
  isLinked?: boolean               // 是否保持引用关系
  overwrite?: Partial<SearchField> // 覆盖的配置
  templateName?: string            // 模板名称（仅用于显示）
}

// ============= 表格配置 =============

/**
 * 表格配置
 */
export interface TableConfig {
  columns: TableColumn[]           // 表格列列表
  stripe: boolean                  // 斑马纹
  border: boolean                  // 边框
  highlightCurrentRow: boolean     // 高亮当前行
  showHeader: boolean              // 显示表头
  pagination: boolean              // 分页
  pageSize: number                 // 每页条数
  pageSizes: number[]              // 分页选项
  rowActions?: RowAction[]         // 行操作按钮
}

/**
 * 表格列
 */
export interface TableColumn {
  id: string                       // 列ID
  prop: string                     // 字段名
  label: string                    // 列标题
  type: ColumnType                 // 列类型
  width?: number                   // 列宽度
  minWidth?: number                // 最小宽度
  align?: AlignType                // 对齐方式
  fixed?: 'left' | 'right'         // 固定列
  sortable?: boolean               // 是否可排序
  showOverflowTooltip?: boolean    // 溢出提示
  // 类型特定配置
  formatter?: string               // 格式化表达式
  tagConfig?: TagColumnConfig      // 标签列配置
  dateConfig?: DateColumnConfig    // 日期列配置
  imageConfig?: ImageColumnConfig  // 图片列配置
  linkConfig?: LinkColumnConfig    // 链接列配置
  switchConfig?: SwitchColumnConfig // 开关列配置

  // === 模板引用字段 ===
  templateId?: number              // 模板ID（来自表格列库）
  templateType?: 'column'          // 模板类型（固定为 'column'）
  isLinked?: boolean               // 是否保持引用关系
  overwrite?: Partial<TableColumn> // 覆盖的配置
  templateName?: string            // 模板名称（仅用于显示）
}

/**
 * 行操作按钮
 */
export interface RowAction {
  id: string                       // 按钮ID
  name: string                     // 按钮名称
  type: ButtonType                 // 按钮类型
  action: ButtonAction             // 按钮动作
  visible?: boolean                // 是否显示
  perms?: string                   // 权限标识
}

// ============= 字段/列特定配置 =============

/**
 * 字段选项
 */
export interface FieldOption {
  label: string
  value: any
}

/**
 * 日期字段配置
 */
export interface DateFieldConfig {
  format?: string                  // 日期格式
  clearable?: boolean              // 是否可清空
  disabledDate?: string            // 禁用日期表达式
}

/**
 * 数字字段配置
 */
export interface NumberFieldConfig {
  min?: number
  max?: number
  step?: number
  precision?: number               // 精度
}

/**
 * 标签列配置
 */
export type TagColumnConfig = Record<string, {
  text: string
  type: 'success' | 'warning' | 'danger' | 'info' | 'primary'
}>

/**
 * 日期列配置
 */
export interface DateColumnConfig {
  format: string                   // 格式，如 'YYYY-MM-DD HH:mm:ss'
}

/**
 * 图片列配置
 */
export interface ImageColumnConfig {
  width: number
  height: number
  fit: 'cover' | 'contain' | 'fill'
  preview: boolean                 // 是否可预览
}

/**
 * 链接列配置
 */
export interface LinkColumnConfig {
  target?: '_blank' | '_self'
  href: string                     // 支持 {value} 变量
}

/**
 * 开关列配置
 */
export interface SwitchColumnConfig {
  activeValue: any
  inactiveValue: any
  disabled?: boolean
}

// ============= 拖拽相关类型 =============

/**
 * 拖拽数据格式（从组件库）
 */
export interface DragData {
  source: 'component-library'      // 来源
  itemType: 'button' | 'field' | 'column' | 'rowAction'  // 组件类型
  itemSubType?: string             // 子类型（如: input, select）
  defaultConfig: any               // 默认配置
}

/**
 * 模板拖拽数据格式（从模板库）
 */
export interface TemplateDragData {
  source: 'template-library'       // 来源标识
  itemType: 'button' | 'column' | 'field'  // 组件类型
  templateId: number               // 模板ID
  templateName: string             // 模板名称
  templateConfig: any              // 完整模板配置
  defaultConfig: any               // 提取的默认配置（用于拖拽后生成组件）
}

/**
 * 合并的拖拽数据类型
 */
export type CombinedDragData = DragData | TemplateDragData

/**
 * 选中对象类型
 */
export type SelectedObject =
  | { type: 'toolbar'; data: ToolbarConfig }
  | { type: 'search'; data: SearchConfig }
  | { type: 'table'; data: TableConfig }
  | { type: 'button'; data: ToolbarButton; parent: 'toolbar' }
  | { type: 'field'; data: SearchField; parent: 'search' }
  | { type: 'column'; data: TableColumn; parent: 'table' }
  | { type: 'rowAction'; data: RowAction; parent: 'table' }
  | null

// ============= 默认配置工厂函数 =============

/**
 * 创建空页面配置
 */
export function createEmptyPageConfig(): NewPageConfig {
  return {
    version: 'v6',
    pageName: '',
    pageCode: '',
    description: '',
    toolbar: createEmptyToolbarConfig(),
    search: createEmptySearchConfig(),
    table: createEmptyTableConfig(),
    globalConfig: {
      pageSize: 10,
      pageSizes: [10, 20, 50, 100],
      stripe: true,
      border: false
    }
  }
}

/**
 * 创建空工具栏配置
 */
export function createEmptyToolbarConfig(): ToolbarConfig {
  return {
    enabled: true,
    align: 'left',
    buttons: []
  }
}

/**
 * 创建空查询区配置
 */
export function createEmptySearchConfig(): SearchConfig {
  return {
    enabled: true,
    collapsible: true,
    collapsed: false,
    labelWidth: 80,
    labelPosition: 'left',
    fields: [],
    layout: {
      span: 6,
      gap: 16
    }
  }
}

/**
 * 创建空表格配置
 */
export function createEmptyTableConfig(): TableConfig {
  return {
    columns: [],
    stripe: true,
    border: false,
    highlightCurrentRow: false,
    showHeader: true,
    pagination: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    rowActions: []
  }
}

/**
 * 生成唯一ID
 */
export function generateId(prefix: string): string {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`
}

// ============= 模板引用相关类型 =============

/**
 * 模板类型
 */
export type TemplateType = 'button' | 'column' | 'field'

/**
 * 同步策略
 */
export type SyncStrategy = 'merge' | 'replace'

/**
 * 页面引用信息
 */
export interface PageReference {
  pageId: number                   // 页面ID
  pageName: string                 // 页面名称
  pageCode: string                 // 页面编码
  referencePath: string            // 引用路径（如：toolbar.buttons[0]）
  isLinked: boolean                // 是否保持引用
  updatedAt: string                // 更新时间
}

/**
 * 模板引用查询结果
 */
export interface TemplateReferenceResult {
  templateId: number               // 模板ID
  templateType: TemplateType       // 模板类型
  templateName: string             // 模板名称
  pages: PageReference[]           // 引用该模板的页面列表
  totalCount: number               // 总引用数
}

/**
 * 同步请求参数
 */
export interface SyncRequest {
  templateId: number               // 模板ID
  templateType: TemplateType       // 模板类型
  pageIds: number[]                // 要同步到的页面ID列表
  strategy: SyncStrategy           // 同步策略：merge（合并）或 replace（替换）
}

/**
 * 同步结果
 */
export interface SyncResult {
  totalCount: number               // 总页数
  successCount: number             // 成功数量
  failedCount: number              // 失败数量
  errors: SyncError[]              // 错误列表
}

/**
 * 同步错误信息
 */
export interface SyncError {
  pageId: number                   // 页面ID
  pageName: string                 // 页面名称
  errorMessage: string             // 错误消息
}
