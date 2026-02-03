/**
 * 自由画布页面配置类型定义
 * Free Canvas Page Configuration Types
 */

// ============= 页面配置 =============

/**
 * 自由画布页面配置
 */
export interface FreeCanvasPageConfig {
  version: 'free-canvas'                     // 版本标识

  // 页面基础信息
  pageInfo: PageInfo

  // 画布配置
  canvas: CanvasConfig

  // 组件列表（扁平结构）
  components: ComponentInstance[]
}

/**
 * 页面基础信息
 */
export interface PageInfo {
  pageName: string
  pageCode: string
  pageType: PageType
  description?: string
  routePath?: string
  published: boolean
}

/**
 * 页面类型
 */
export type PageType = 'list' | 'form' | 'detail' | 'dashboard' | 'custom'

// ============= 画布配置 =============

/**
 * 画布配置
 */
export interface CanvasConfig {
  width: number                 // 画布宽度（px）
  height?: number                // 画布高度（px，null 表示自适应）
  backgroundColor?: string       // 背景色
  backgroundImage?: string       // 背景图
  gridSize?: number              // 网格大小（px）
  snapToGrid?: boolean           // 是否对齐网格
  zoom?: number                  // 缩放比例 (1 = 100%)
  minZoom?: number               // 最小缩放比例 (默认 1)
  maxZoom?: number               // 最大缩放比例 (默认 1.5)
}

// ============= 组件实例 =============

/**
 * 组件实例
 */
export interface ComponentInstance {
  id: string                    // 组件唯一ID
  name: string                  // 组件名称（用户自定义）
  type: ComponentType           // 组件类型
  position: ComponentPosition   // 位置和尺寸
  config: ComponentSpecificConfig  // 组件特定配置
  style?: ComponentStyle        // 样式覆盖
  enabled?: boolean             // 是否启用
}

// ============= 组件位置 =============

/**
 * 组件位置和尺寸
 */
export interface ComponentPosition {
  x: number                     // X 坐标（px）
  y: number                     // Y 坐标（px）
  width: number                 // 宽度（px）
  height: number                // 高度（px）
  zIndex?: number               // 层级（默认按添加顺序）
  anchor?: AnchorPoint          // 锚点（默认左上角）
}

/**
 * 锚点位置
 */
export type AnchorPoint =
  | 'top-left'       // 左上角
  | 'top-right'      // 右上角
  | 'bottom-left'    // 左下角
  | 'bottom-right'   // 右下角
  | 'center'         // 中心

// ============= 组件类型（可扩展） =============

/**
 * 组件类型
 */
export type ComponentType =
  | 'tree'            // 树组件
  | 'search-form'     // 查询表单
  | 'table'           // 表格
  | 'button-group'    // 按钮组
  | 'form'            // 表单
  | 'chart'           // 图表
  | 'tabs'            // 标签页
  | 'card'            // 卡片
  | 'divider'         // 分割线
  | 'spacer'          // 占位符
  | 'custom'          // 自定义组件

// ============= 组件特定配置 =============

/**
 * 组件特定配置（联合类型）
 */
export type ComponentSpecificConfig =
  | TreeComponentConfig
  | SearchFormComponentConfig
  | TableComponentConfig
  | ButtonGroupComponentConfig
  | FormComponentConfig
  | ChartComponentConfig
  | TabsComponentConfig
  | CardComponentConfig
  | DividerComponentConfig
  | SpacerComponentConfig
  | Record<string, any>  // 自定义组件配置

/**
 * 树组件配置
 */
export interface TreeComponentConfig {
  dataSource: DataSourceConfig
  displayField: string           // 显示字段
  childrenField: string          // 子节点字段
  idField: string                // ID 字段
  parentField?: string           // 父节点字段（用于平铺数据转树）
  expandOnFilter?: boolean       // 过滤时是否展开
  defaultExpandAll?: boolean     // 默认是否展开所有
  showIcon?: boolean             // 是否显示图标
  showCheckbox?: boolean         // 是否显示复选框
  draggable?: boolean            // 是否可拖拽
}

/**
 * 查询表单组件配置
 */
export interface SearchFormComponentConfig {
  fields: FormFieldConfig[]
  buttonAlign?: 'left' | 'center' | 'right'
  showCollapseButton?: boolean
  defaultCollapsed?: boolean
  layoutCols?: number            // 列数（1-4）
}

/**
 * 表格组件配置
 */
export interface TableComponentConfig {
  dataSource: DataSourceConfig
  columns: TableColumnConfig[]
  pagination?: boolean
  pageSize?: number
  showIndex?: boolean            // 是否显示序号
  stripe?: boolean               // 斑马纹
  border?: boolean               // 边框
  selectionMode?: 'none' | 'single' | 'multiple'
  rowActions?: RowActionConfig[]
}

/**
 * 按钮组组件配置
 */
export interface ButtonGroupComponentConfig {
  buttons: ButtonConfig[]
  direction?: 'horizontal' | 'vertical'
  align?: 'left' | 'center' | 'right'
  size?: 'large' | 'default' | 'small'
}

/**
 * 表单组件配置
 */
export interface FormComponentConfig {
  fields: FormFieldConfig[]
  layoutCols?: number
  labelWidth?: number
  labelPosition?: 'left' | 'right' | 'top'
}

/**
 * 图表组件配置
 */
export interface ChartComponentConfig {
  chartType: 'line' | 'bar' | 'pie' | 'gauge' | 'scatter'
  dataSource: DataSourceConfig
  xAxis?: string                 // X 轴字段
  yAxis?: string[]               // Y 轴字段
  series?: ChartSeriesConfig[]
}

/**
 * 标签页组件配置
 */
export interface TabsComponentConfig {
  tabs: TabConfig[]
  tabPosition?: 'top' | 'right' | 'bottom' | 'left'
  type?: 'card' | 'border-card'
}

/**
 * 卡片组件配置
 */
export interface CardComponentConfig {
  title?: string
  content?: string
  showHeader?: boolean
  shadow?: 'always' | 'hover' | 'never'
}

/**
 * 分割线组件配置
 */
export interface DividerComponentConfig {
  direction?: 'horizontal' | 'vertical'
  contentPosition?: 'left' | 'center' | 'right'
  text?: string
}

/**
 * 占位符组件配置
 */
export interface SpacerComponentConfig {
  transparent?: boolean
}

// ============= 数据源配置 =============

/**
 * 数据源配置
 */
export interface DataSourceConfig {
  type: 'api' | 'sql' | 'static' | 'websocket'
  api?: {
    url: string
    method: 'GET' | 'POST' | 'PUT' | 'DELETE'
    params?: Record<string, any>
    headers?: Record<string, string>
  }
  sql?: {
    content: string
    params?: any[]
  }
  static?: any[]
  refreshInterval?: number        // 刷新间隔（秒）
}

// ============= 字段/列配置 =============

/**
 * 表单字段配置
 */
export interface FormFieldConfig {
  id: string
  fieldCode: string
  label: string
  fieldType: FieldType
  placeholder?: string
  defaultValue?: any
  required?: boolean
  options?: FieldOption[]
  width?: number                  // 宽度（栅格占比，1-24）
  dataSource?: DataSourceConfig   // 下拉等组件的数据源
}

/**
 * 字段类型
 */
export type FieldType =
  | 'input' | 'textarea' | 'select'
  | 'radio' | 'checkbox' | 'switch'
  | 'date' | 'datetime' | 'dateRange'
  | 'number' | 'cascader' | 'upload'

/**
 * 字段选项
 */
export interface FieldOption {
  label: string
  value: any
  color?: string                 // tag 类型时的颜色
}

/**
 * 表格列配置
 */
export interface TableColumnConfig {
  id: string
  prop: string
  label: string
  type: ColumnType
  width?: number
  minWidth?: number
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right'
  sortable?: boolean
  formatter?: string             // 格式化表达式
  dictCode?: string              // 字典编码
  tagConfig?: Record<string, { text: string; type: string }>
  linkConfig?: { href: string; target?: string }
}

/**
 * 列类型
 */
export type ColumnType =
  | 'text' | 'number' | 'date' | 'datetime'
  | 'tag' | 'image' | 'link' | 'switch'
  | 'progress' | 'rating' | 'color'

/**
 * 按钮配置
 */
export interface ButtonConfig {
  id: string
  name: string
  type: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: string
  action: ButtonAction
  confirmMessage?: string
}

/**
 * 按钮动作
 */
export interface ButtonAction {
  type: 'add' | 'edit' | 'delete' | 'export' | 'refresh' | 'submit' | 'reset' | 'custom'
  apiEndpoint?: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  redirectUrl?: string
}

/**
 * 行操作配置
 */
export interface RowActionConfig {
  id: string
  name: string
  type: 'primary' | 'success' | 'warning' | 'danger' | 'text' | 'default'
  icon?: string
  action: ButtonAction
}

/**
 * 标签页配置
 */
export interface TabConfig {
  id: string
  label: string
  icon?: string
  content?: string               // 静态内容
  componentId?: string           // 引用组件ID（用于动态加载）
}

/**
 * 图表系列配置
 */
export interface ChartSeriesConfig {
  name: string
  type?: 'line' | 'bar' | 'scatter'
  dataField: string
  color?: string
}

// ============= 组件样式 =============

/**
 * 组件样式
 */
export interface ComponentStyle {
  styleTemplateId?: number        // 样式模板ID
  customStyles?: Record<string, any>
  border?: string
  borderRadius?: string
  backgroundColor?: string
  color?: string
  padding?: string | number
  margin?: string | number
  boxShadow?: string
  opacity?: number
}

// ============= 组件库元数据 =============

/**
 * 组件库元数据
 */
export interface ComponentLibraryItem {
  type: ComponentType
  label: string
  icon: string
  description: string
  defaultSize: { width: number; height: number }
  category: 'data' | 'form' | 'display' | 'layout'
}

// ============= 工厂函数 =============

/**
 * 创建空的页面配置
 */
export function createEmptyPageConfig(): FreeCanvasPageConfig {
  return {
    version: 'free-canvas',
    pageInfo: {
      pageName: '',
      pageCode: '',
      pageType: 'list',
      published: false
    },
    canvas: {
      width: 1200,
      height: null,
      backgroundColor: '#f5f7fa',
      gridSize: 10,
      snapToGrid: true,
      zoom: 1,
      minZoom: 1,
      maxZoom: 1.5
    },
    components: []
  }
}

/**
 * 创建组件实例
 */
export function createComponentInstance(
  type: ComponentType,
  x: number,
  y: number
): ComponentInstance {
  const defaults = getDefaultConfigForType(type)
  return {
    id: `${type}_${Date.now()}`,
    name: getDefaultNameForType(type),
    type,
    position: {
      x,
      y,
      ...defaults.size
    },
    config: defaults.config,
    style: defaults.style,
    enabled: true
  }
}

/**
 * 获取组件类型的默认配置
 */
function getDefaultConfigForType(type: ComponentType): {
  size: { width: number; height: number }
  config: ComponentSpecificConfig
  style?: ComponentStyle
} {
  // 更小的默认尺寸，更适合画布布局
  const sizeMap: Record<ComponentType, { width: number; height: number }> = {
    'tree': { width: 200, height: 300 },
    'search-form': { width: 600, height: 80 },
    'table': { width: 700, height: 250 },
    'button-group': { width: 300, height: 40 },
    'form': { width: 400, height: 200 },
    'chart': { width: 400, height: 250 },
    'tabs': { width: 600, height: 200 },
    'card': { width: 300, height: 150 },
    'divider': { width: 500, height: 16 },
    'spacer': { width: 80, height: 40 },
    'custom': { width: 150, height: 150 }
  }

  const configMap: Record<ComponentType, ComponentSpecificConfig> = {
    'tree': {
      dataSource: { type: 'static', static: [] },
      displayField: 'name',
      childrenField: 'children',
      idField: 'id'
    },
    'search-form': {
      fields: [],
      buttonAlign: 'left',
      layoutCols: 4
    },
    'table': {
      dataSource: { type: 'api', api: { url: '', method: 'GET' } },
      columns: [],
      pagination: true,
      pageSize: 10
    },
    'button-group': {
      buttons: [],
      direction: 'horizontal',
      align: 'left'
    },
    'form': { fields: [], layoutCols: 2 },
    'chart': { chartType: 'line', dataSource: { type: 'static', static: [] } },
    'tabs': { tabs: [] },
    'card': { title: '卡片', showHeader: true },
    'divider': { direction: 'horizontal' },
    'spacer': { transparent: true },
    'custom': {}
  }

  const styleMap: Record<ComponentType, ComponentStyle> = {
    'tree': { backgroundColor: '#fff', borderRadius: '4px' },
    'search-form': { backgroundColor: '#fff', borderRadius: '4px', padding: '16px' },
    'table': { backgroundColor: '#fff', borderRadius: '4px' },
    'button-group': {},
    'form': { backgroundColor: '#fff', borderRadius: '4px', padding: '20px' },
    'chart': { backgroundColor: '#fff', borderRadius: '4px', padding: '16px' },
    'tabs': { backgroundColor: '#fff', borderRadius: '4px' },
    'card': { shadow: 'hover' },
    'divider': {},
    'spacer': {},
    'custom': {}
  }

  return {
    size: sizeMap[type] || { width: 200, height: 200 },
    config: configMap[type] || {},
    style: styleMap[type]
  }
}

/**
 * 获取组件类型的默认名称
 */
function getDefaultNameForType(type: ComponentType): string {
  const nameMap: Record<ComponentType, string> = {
    'tree': '树组件',
    'search-form': '查询表单',
    'table': '表格',
    'button-group': '按钮组',
    'form': '表单',
    'chart': '图表',
    'tabs': '标签页',
    'card': '卡片',
    'divider': '分割线',
    'spacer': '占位符',
    'custom': '自定义组件'
  }
  return nameMap[type] || '未命名组件'
}

/**
 * 获取组件库元数据列表
 */
export function getComponentLibraryItems(): ComponentLibraryItem[] {
  return [
    {
      type: 'tree',
      label: '树组件',
      icon: '🌲',
      description: '树形结构数据展示',
      defaultSize: { width: 200, height: 300 },
      category: 'data'
    },
    {
      type: 'search-form',
      label: '查询表单',
      icon: '🔍',
      description: '搜索查询条件表单',
      defaultSize: { width: 600, height: 80 },
      category: 'form'
    },
    {
      type: 'table',
      label: '表格',
      icon: '📊',
      description: '数据表格展示',
      defaultSize: { width: 700, height: 250 },
      category: 'data'
    },
    {
      type: 'button-group',
      label: '按钮组',
      icon: '🔘',
      description: '操作按钮组',
      defaultSize: { width: 300, height: 40 },
      category: 'form'
    },
    {
      type: 'form',
      label: '表单',
      icon: '📝',
      description: '数据录入表单',
      defaultSize: { width: 400, height: 200 },
      category: 'form'
    },
    {
      type: 'chart',
      label: '图表',
      icon: '📈',
      description: '数据可视化图表',
      defaultSize: { width: 400, height: 250 },
      category: 'display'
    },
    {
      type: 'tabs',
      label: '标签页',
      icon: '📑',
      description: '标签页容器',
      defaultSize: { width: 600, height: 200 },
      category: 'layout'
    },
    {
      type: 'card',
      label: '卡片',
      icon: '📦',
      description: '卡片容器',
      defaultSize: { width: 300, height: 150 },
      category: 'layout'
    },
    {
      type: 'divider',
      label: '分割线',
      icon: '➖',
      description: '内容分割线',
      defaultSize: { width: 500, height: 16 },
      category: 'layout'
    },
    {
      type: 'spacer',
      label: '占位符',
      icon: '⬜',
      description: '空白占位',
      defaultSize: { width: 80, height: 40 },
      category: 'layout'
    }
  ]
}
