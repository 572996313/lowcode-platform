import { request } from '@/utils/request'

// ==========================================
// 类型定义 - 页面配置接口
// ==========================================

/** 搜索字段类型 */
export type SearchFieldType = 'input' | 'select' | 'date' | 'daterange' | 'number'

/** 搜索字段配置 */
export interface SearchFieldConfig {
  /** 字段名（对应查询参数名） */
  field: string
  /** 显示标签 */
  label: string
  /** 组件类型 */
  type: SearchFieldType
  /** 占位文本 */
  placeholder?: string
  /** 是否可清空 */
  clearable?: boolean
  /** 下拉选项（type=select 时） */
  options?: OptionItem[]
  /** 组件宽度（px） */
  width?: number
  /** 默认值 */
  defaultValue?: any
}

/** 选项项 */
export interface OptionItem {
  label: string
  value: any
}

/** 表格列类型 */
export type ColumnType = 'text' | 'tag' | 'date' | 'index' | 'selection' | 'action'

/** 表格列配置 */
export interface TableColumnConfig {
  /** 字段名 */
  prop?: string
  /** 列标题 */
  label: string
  /** 列宽度 */
  width?: number
  /** 最小列宽 */
  minWidth?: number
  /** 对齐方式 */
  align?: 'left' | 'center' | 'right'
  /** 是否固定列 */
  fixed?: 'left' | 'right' | false
  /** 超出是否显示 tooltip */
  showOverflowTooltip?: boolean
  /** 列渲染类型 */
  type?: ColumnType
  /** Tag 类型配置（type=tag 时） */
  tagConfig?: TagColumnConfig
  /** 操作按钮配置（type=action 时） */
  actionConfig?: ActionColumnConfig
}

/** Tag 列配置 */
export interface TagColumnConfig {
  /** 值到标签的映射，key 是字段值的字符串形式 */
  mapping: Record<string, { text: string; type?: string }>
}

/** 操作列按钮配置 */
export interface ActionColumnConfig {
  /** 按钮列表 */
  buttons: ActionButton[]
}

/** 操作按钮 */
export interface ActionButton {
  /** 按钮文本 */
  label: string
  /** 按钮类型 */
  btnType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | ''
  /** 按钮大小 */
  size?: 'large' | 'default' | 'small'
  /** 操作标识：edit / delete / 自定义 */
  action: string
  /** 显示条件表达式字段名，该字段值为 true 时显示 */
  showWhen?: string
}

/** 表单字段类型 */
export type FormFieldType = 'input' | 'select' | 'textarea' | 'number' | 'switch' | 'date'

/** 表单字段配置 */
export interface FormFieldConfig {
  /** 字段名 */
  field: string
  /** 显示标签 */
  label: string
  /** 组件类型 */
  type: FormFieldType
  /** 占位文本 */
  placeholder?: string
  /** 是否必填 */
  required?: boolean
  /** 自定义校验规则 */
  rules?: FormRule[]
  /** 下拉选项（type=select 时） */
  options?: OptionItem[]
  /** 编辑时是否禁用 */
  disabledOnEdit?: boolean
  /** textarea 行数 */
  rows?: number
  /** switch 的 activeValue */
  activeValue?: any
  /** switch 的 inactiveValue */
  inactiveValue?: any
  /** label 宽度 */
  labelWidth?: string
}

/** 表单校验规则 */
export interface FormRule {
  required?: boolean
  message: string
  trigger?: 'blur' | 'change'
  pattern?: string
  min?: number
  max?: number
}

/** 表格整体配置 */
export interface TableConfig {
  border?: boolean
  stripe?: boolean
  size?: 'large' | 'default' | 'small'
  showPagination?: boolean
  pageSize?: number
  pageSizes?: number[]
  showIndex?: boolean
  showSelection?: boolean
}

/** 工具栏按钮配置 */
export interface ToolbarButton {
  /** 按钮文本 */
  label: string
  /** 按钮类型 */
  btnType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | ''
  /** 图标名称（Element Plus 图标组件名，如 Plus/Download/Delete） */
  icon?: string
  /** 操作标识：add / export / batchDelete / 自定义 */
  action: string
}

/** 工具栏配置 */
export interface ToolbarConfig {
  /** 按钮列表 */
  buttons: ToolbarButton[]
}

/** 页面配置（后端返回的完整页面配置） */
export interface PageConfigResponse {
  /** 页面编码 */
  pageCode: string
  /** 页面名称 */
  pageName: string
  /** 工具栏配置 */
  toolbar: ToolbarConfig
  /** 搜索区域配置 */
  searchFields: SearchFieldConfig[]
  /** 表格列配置 */
  tableColumns: TableColumnConfig[]
  /** 表格整体配置 */
  tableConfig: TableConfig
  /** 表单字段配置 */
  formFields: FormFieldConfig[]
}

/** 分页结果 */
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// ==========================================
// API 接口
// ==========================================

/**
 * 获取页面配置
 * 后端接口：GET /api/table-standard/config
 */
export const getTableStandardConfig = (): Promise<PageConfigResponse> => {
  // TODO: 替换为真实后端接口
  // return request.get<PageConfigResponse>('/table-standard/config')

  // Mock 数据 - 模拟后端返回的页面配置
  return Promise.resolve(mockPageConfig)
}

/**
 * 分页查询数据列表
 * 后端接口：GET /api/table-standard/page
 */
export const getTableStandardPage = (params: Record<string, any>): Promise<PageResult<Record<string, any>>> => {
  // TODO: 替换为真实后端接口
  // return request.get<PageResult<Record<string, any>>>('/table-standard/page', params)

  // Mock 数据 - 模拟后端分页查询
  return Promise.resolve(mockPageQuery(params))
}

/**
 * 新增数据
 * 后端接口：POST /api/table-standard
 */
export const createTableStandard = (data: Record<string, any>): Promise<number> => {
  // TODO: 替换为真实后端接口
  // return request.post<number>('/table-standard', data)

  const newId = mockDataList.length > 0 ? Math.max(...mockDataList.map(item => item.id as number)) + 1 : 1
  const newItem = {
    ...data,
    id: newId,
    createTime: new Date().toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit'
    }).replace(/\//g, '-')
  }
  mockDataList.unshift(newItem)
  return Promise.resolve(newId)
}

/**
 * 更新数据
 * 后端接口：PUT /api/table-standard/{id}
 */
export const updateTableStandard = (id: number, data: Record<string, any>): Promise<void> => {
  // TODO: 替换为真实后端接口
  // return request.put(`/table-standard/${id}`, data)

  const index = mockDataList.findIndex(item => item.id === id)
  if (index > -1) {
    mockDataList[index] = { ...mockDataList[index], ...data }
  }
  return Promise.resolve()
}

/**
 * 删除数据
 * 后端接口：DELETE /api/table-standard/{id}
 */
export const deleteTableStandard = (id: number): Promise<void> => {
  // TODO: 替换为真实后端接口
  // return request.delete(`/table-standard/${id}`)

  const index = mockDataList.findIndex(item => item.id === id)
  if (index > -1) {
    mockDataList.splice(index, 1)
  }
  return Promise.resolve()
}

// ==========================================
// Mock 数据
// ==========================================

/** Mock 页面配置 - 模拟后端返回 */
const mockPageConfig: PageConfigResponse = {
  pageCode: 'table_standard_demo',
  pageName: '表格标准页面',
  toolbar: {
    buttons: [
      { label: '新增', btnType: 'primary', icon: 'Plus', action: 'add' },
      { label: '导出', icon: 'Download', action: 'export' }
    ]
  },
  searchFields: [
    { field: 'name', label: '名称', type: 'input', placeholder: '请输入名称', clearable: true },
    { field: 'code', label: '编码', type: 'input', placeholder: '请输入编码', clearable: true },
    {
      field: 'status', label: '状态', type: 'select', placeholder: '请选择状态', clearable: true, width: 120,
      options: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 }
      ]
    }
  ],
  tableColumns: [
    { prop: 'name', label: '名称', width: 150 },
    { prop: 'code', label: '编码', width: 150 },
    { prop: 'category', label: '分类', width: 120 },
    { prop: 'description', label: '描述', showOverflowTooltip: true },
    {
      prop: 'status', label: '状态', width: 100, align: 'center',
      type: 'tag',
      tagConfig: {
        mapping: {
          '1': { text: '启用', type: 'success' },
          '0': { text: '禁用', type: 'danger' }
        }
      }
    },
    { prop: 'createTime', label: '创建时间', width: 180 },
    {
      label: '操作', width: 180, align: 'center', fixed: 'right',
      type: 'action',
      actionConfig: {
        buttons: [
          { label: '编辑', btnType: 'primary', size: 'small', action: 'edit' },
          { label: '删除', btnType: 'danger', size: 'small', action: 'delete' }
        ]
      }
    }
  ],
  tableConfig: {
    border: true,
    stripe: true,
    showPagination: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100]
  },
  formFields: [
    { field: 'name', label: '名称', type: 'input', placeholder: '请输入名称', required: true, rules: [{ required: true, message: '请输入名称', trigger: 'blur' }] },
    { field: 'code', label: '编码', type: 'input', placeholder: '请输入编码（英文）', required: true, disabledOnEdit: true, rules: [{ required: true, message: '请输入编码', trigger: 'blur' }, { pattern: '^[a-zA-Z0-9_]+$', message: '编码只能包含字母、数字和下划线', trigger: 'blur' }] },
    {
      field: 'category', label: '分类', type: 'select', placeholder: '请选择分类', required: true,
      rules: [{ required: true, message: '请选择分类', trigger: 'change' }],
      options: [
        { label: '分类A', value: '分类A' },
        { label: '分类B', value: '分类B' },
        { label: '分类C', value: '分类C' }
      ]
    },
    { field: 'description', label: '描述', type: 'textarea', placeholder: '请输入描述', rows: 3 },
    { field: 'status', label: '状态', type: 'switch', activeValue: 1, inactiveValue: 0 }
  ]
}

/** Mock 数据列表 */
const mockDataList: Record<string, any>[] = [
  { id: 1, name: '示例数据1', code: 'DEMO_001', category: '分类A', description: '这是示例数据1的描述信息', status: 1, createTime: '2026-01-15 10:30:00' },
  { id: 2, name: '示例数据2', code: 'DEMO_002', category: '分类B', description: '这是示例数据2的描述信息', status: 1, createTime: '2026-01-16 14:20:00' },
  { id: 3, name: '示例数据3', code: 'DEMO_003', category: '分类A', description: '这是示例数据3的描述信息', status: 0, createTime: '2026-02-01 09:00:00' },
  { id: 4, name: '示例数据4', code: 'DEMO_004', category: '分类C', description: '这是示例数据4的描述信息', status: 1, createTime: '2026-02-10 16:45:00' },
  { id: 5, name: '示例数据5', code: 'DEMO_005', category: '分类B', description: '这是示例数据5的描述信息', status: 0, createTime: '2026-02-20 11:15:00' },
  { id: 6, name: '示例数据6', code: 'DEMO_006', category: '分类A', description: '这是示例数据6的描述信息', status: 1, createTime: '2026-03-01 08:30:00' },
  { id: 7, name: '示例数据7', code: 'DEMO_007', category: '分类C', description: '这是示例数据7的描述信息', status: 1, createTime: '2026-03-05 13:00:00' },
  { id: 8, name: '示例数据8', code: 'DEMO_008', category: '分类B', description: '这是示例数据8的描述信息', status: 0, createTime: '2026-03-10 17:30:00' },
  { id: 9, name: '示例数据9', code: 'DEMO_009', category: '分类A', description: '这是示例数据9的描述信息', status: 1, createTime: '2026-03-15 10:00:00' },
  { id: 10, name: '示例数据10', code: 'DEMO_010', category: '分类C', description: '这是示例数据10的描述信息', status: 1, createTime: '2026-03-20 14:30:00' },
  { id: 11, name: '示例数据11', code: 'DEMO_011', category: '分类B', description: '这是示例数据11的描述信息', status: 1, createTime: '2026-03-25 09:45:00' },
  { id: 12, name: '示例数据12', code: 'DEMO_012', category: '分类A', description: '这是示例数据12的描述信息', status: 0, createTime: '2026-04-01 16:00:00' },
]

/** Mock 分页查询逻辑 */
function mockPageQuery(params: Record<string, any>): PageResult<Record<string, any>> {
  let filtered = [...mockDataList]

  // 模糊搜索
  if (params.name) {
    filtered = filtered.filter(item => String(item.name).includes(params.name))
  }
  if (params.code) {
    filtered = filtered.filter(item => String(item.code).toLowerCase().includes(params.code.toLowerCase()))
  }
  if (params.status !== undefined && params.status !== null && params.status !== '') {
    filtered = filtered.filter(item => item.status === Number(params.status))
  }

  const current = Number(params.current || 1)
  const size = Number(params.size || 10)
  const total = filtered.length
  const start = (current - 1) * size
  const records = filtered.slice(start, start + size)

  return { total, records, current, size, pages: Math.ceil(total / size) }
}
