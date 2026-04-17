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
  role?: 'main' | 'linked'     // 组件角色：main=主组件(直接渲染) linked=弹窗组件(由按钮触发)
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
  | 'tree'              // 树组件
  | 'table-standard'    // 标准表格（完整表格页面：工具栏+搜索+表格）
  | 'form-standard'     // 标准表单（完整表单页面：工具栏+表单分组）
  | 'chart'             // 图表
  | 'tabs'              // 标签页
  | 'card'              // 卡片
  | 'divider'           // 分割线
  | 'spacer'            // 占位符
  | 'custom'            // 自定义组件

// ============= 组件特定配置 =============

/**
 * 标准表格组件配置 - 复用 table-standard API 的 PageConfigResponse
 */
export interface TableStandardComponentConfig {
  pageCode: string
  pageName: string
  apiUrl?: string
  apiMethod?: 'GET' | 'POST'
  toolbar: {
    buttons: TableStandardButton[]
  }
  searchFields: TableStandardSearchField[]
  tableColumns: TableStandardColumn[]
  tableConfig: {
    border?: boolean
    stripe?: boolean
    size?: 'large' | 'default' | 'small'
    showPagination?: boolean
    pageSize?: number
    pageSizes?: number[]
    showIndex?: boolean
    showSelection?: boolean
  }
}

/**
 * 标准表格工具栏按钮
 */
export interface TableStandardButton {
  label: string
  btnType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | ''
  icon?: string
  action: string
  position?: 'toolbar' | 'table-column'
  actionConfig?: {
    type: 'openForm' | 'openTable' | 'route' | 'submit' | 'api' | 'custom'
    targetCode?: string
    openMode?: 'dialog' | 'drawer' | 'page'
    apiUrl?: string
    routePath?: string
    routeQuery?: Record<string, string>
    selectionMode?: 'none' | 'single' | 'multiple'
    confirmText?: string
  }
}

/**
 * 标准表格搜索字段
 */
export interface TableStandardSearchField {
  field: string
  label: string
  type: 'input' | 'select' | 'date' | 'daterange' | 'number'
  placeholder?: string
  clearable?: boolean
  options?: { label: string; value: any }[]
  width?: number
  defaultValue?: any
}

/**
 * 标准表格列
 */
export interface TableStandardColumn {
  prop?: string
  label: string
  width?: number
  minWidth?: number
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right' | false
  showOverflowTooltip?: boolean
  type?: 'text' | 'tag' | 'date' | 'index' | 'selection'
  tagConfig?: Record<string, { text: string; type: string }>
}

/**
 * 标准表单组件配置
 */
export interface FormStandardComponentConfig {
  pageCode: string
  pageName: string
  layout: {
    columns?: number
    labelWidth?: string
    labelPosition?: 'left' | 'right' | 'top'
    size?: 'large' | 'default' | 'small'
    rowGutter?: number
  }
  toolbar: {
    buttons: FormStandardButton[]
  }
  groups: FormStandardGroup[]
}

/**
 * 标准表单工具栏按钮
 */
export interface FormStandardButton {
  label: string
  btnType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | ''
  icon?: string
  action: string
  showInModes?: ('add' | 'edit' | 'view')[]
  actionConfig?: TableStandardButton['actionConfig']
}

/**
 * 标准表单分组
 */
export interface FormStandardGroup {
  title?: string
  description?: string
  collapsible?: boolean
  defaultCollapsed?: boolean
  fields: FormStandardField[]
}

/**
 * 标准表单字段
 */
export interface FormStandardField {
  field: string
  label: string
  type: 'input' | 'textarea' | 'select' | 'number' | 'switch' | 'date' | 'radio' | 'checkbox' | 'datetime' | 'upload'
  placeholder?: string
  defaultValue?: any
  required?: boolean
  editable?: boolean
  rules?: any[]
  options?: { label: string; value: any }[]
  span?: number
  disabled?: boolean
  tooltip?: string
  min?: number
  max?: number
  step?: number
  uploadConfig?: {
    accept?: string
    limit?: number
    maxSize?: number
    multiple?: boolean
    listType?: 'text' | 'picture' | 'picture-card'
  }
}

/**
 * 组件特定配置（联合类型）
 */
export type ComponentSpecificConfig =
  | TreeComponentConfig
  | TableStandardComponentConfig
  | FormStandardComponentConfig
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

// ============= 标签页/图表配置 =============

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
      pageType: 'custom',
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
  role: 'main' | 'linked' = 'main'
): ComponentInstance {
  const defaults = getDefaultConfigForType(type)
  return {
    id: `${type}_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`,
    name: getDefaultNameForType(type),
    type,
    role,
    position: {
      x: 0,
      y: 0,
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
  const sizeMap: Record<string, { width: number; height: number }> = {
    'tree': { width: 200, height: 300 },
    'table-standard': { width: 900, height: 500 },
    'form-standard': { width: 700, height: 400 },
    'chart': { width: 400, height: 250 },
    'tabs': { width: 600, height: 200 },
    'card': { width: 300, height: 150 },
    'divider': { width: 500, height: 16 },
    'spacer': { width: 80, height: 40 },
    'custom': { width: 150, height: 150 }
  }

  const configMap: Record<string, ComponentSpecificConfig> = {
    'tree': {
      dataSource: { type: 'static', static: [] },
      displayField: 'name',
      childrenField: 'children',
      idField: 'id'
    },
    'table-standard': {
      pageCode: '',
      pageName: '标准表格',
      toolbar: { buttons: [] },
      searchFields: [],
      tableColumns: [
        { prop: 'name', label: '名称', width: 150 },
        { prop: 'createTime', label: '创建时间', width: 180 }
      ],
      tableConfig: {
        border: true,
        stripe: true,
        showPagination: true,
        pageSize: 10,
        pageSizes: [10, 20, 50, 100]
      }
    },
    'form-standard': {
      pageCode: '',
      pageName: '标准表单',
      layout: {
        columns: 2,
        labelWidth: '120px',
        labelPosition: 'right',
        size: 'default',
        rowGutter: 20
      },
      toolbar: { buttons: [] },
      groups: [
        { title: '基本信息', collapsible: false, fields: [] }
      ]
    },
    'chart': { chartType: 'line', dataSource: { type: 'static', static: [] } },
    'tabs': { tabs: [] },
    'card': { title: '卡片', showHeader: true },
    'divider': { direction: 'horizontal' },
    'spacer': { transparent: true },
    'custom': {}
  }

  const styleMap: Record<string, ComponentStyle> = {
    'tree': { backgroundColor: '#fff', borderRadius: '4px' },
    'table-standard': { backgroundColor: '#fff', borderRadius: '4px', padding: '12px' },
    'form-standard': { backgroundColor: '#fff', borderRadius: '4px', padding: '12px' },
    'chart': { backgroundColor: '#fff', borderRadius: '4px', padding: '16px' },
    'tabs': { backgroundColor: '#fff', borderRadius: '4px' },
    'card': {},
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
  const nameMap: Record<string, string> = {
    'tree': '树组件',
    'table-standard': '标准表格',
    'form-standard': '标准表单',
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
      type: 'table-standard',
      label: '标准表格',
      icon: '📋',
      description: '完整表格页面（工具栏+搜索+表格）',
      defaultSize: { width: 900, height: 500 },
      category: 'data'
    },
    {
      type: 'form-standard',
      label: '标准表单',
      icon: '📝',
      description: '完整表单页面（工具栏+表单分组）',
      defaultSize: { width: 700, height: 400 },
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
