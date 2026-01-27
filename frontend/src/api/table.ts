import { request } from '@/utils/request'

// 表格配置接口
export interface TableConfig {
  id?: number
  tableName: string
  tableCode: string
  pageId?: number
  dataSourceType?: string
  apiUrl?: string
  apiMethod?: string
  apiParamsJson?: string
  sqlContent?: string
  pagination?: boolean
  pageSize?: number
  pageSizes?: string
  selection?: boolean
  selectionType?: string
  showIndex?: boolean
  border?: boolean
  stripe?: boolean
  rowKey?: string
  emptyText?: string
  height?: string
  maxHeight?: string
  configJson?: string
  status?: boolean
  columns?: TableColumn[]
}

// 表格列接口
export interface TableColumn {
  id?: number
  tableId?: number
  columnName: string
  columnCode: string
  columnType?: string
  label: string
  width?: number
  minWidth?: number
  sortable?: boolean
  sortOrders?: string
  fixed?: string
  align?: string
  headerAlign?: string
  resizable?: boolean
  showOverflowTooltip?: boolean
  formatterType?: string
  formatterConfig?: string
  dictCode?: string
  sortOrder?: number
  visible?: boolean
  export?: boolean
  propsJson?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 获取表格列表
export const getTableList = (params: any) => {
  return request.get<PageResult<TableConfig>>('/table/list', params)
}

// 获取表格详情
export const getTableConfig = (id: number) => {
  return request.get<TableConfig>(`/table/${id}`)
}

// 创建表格
export const createTable = (data: TableConfig) => {
  return request.post<number>('/table', data)
}

// 更新表格
export const updateTable = (id: number, data: TableConfig) => {
  return request.put(`/table/${id}`, data)
}

// 删除表格
export const deleteTable = (id: number) => {
  return request.delete(`/table/${id}`)
}
