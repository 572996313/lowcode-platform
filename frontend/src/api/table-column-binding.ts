import { request } from '@/utils/request'

// 表格列绑定接口
export interface TableColumnBinding {
  id?: number
  tableConfigId: number
  fieldId: number
  columnLabel?: string
  columnType?: string
  width?: number
  minWidth?: number
  sortable?: boolean
  fixed?: string
  align?: string
  showOverflow?: boolean
  formatterConfig?: string
  sortOrder?: number
  visible?: boolean
  createTime?: string
  updateTime?: string
  // 非数据库字段
  field?: {
    id: number
    fieldName: string
    fieldType: string
    fieldComment?: string
    fieldLabel?: string
    ordinalPosition: number
  }
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 获取表格列绑定列表
export const getTableColumnBindingList = (params: any) => {
  return request.get<PageResult<TableColumnBinding>>('/table-column-binding/list', params)
}

// 获取表格列绑定详情
export const getTableColumnBinding = (id: number) => {
  return request.get<TableColumnBinding>(`/table-column-binding/${id}`)
}

// 根据表格配置ID获取列绑定列表（包含字段信息）
export const getBindingsByTableConfigId = (tableConfigId: number) => {
  return request.get<TableColumnBinding[]>(`/table-column-binding/table/${tableConfigId}`)
}

// 根据数据库表ID获取可绑定的字段列表
export const getAvailableFields = (tableId: number) => {
  return request.get(`/table-column-binding/table/${tableId}/fields`)
}

// 创建表格列绑定
export const createTableColumnBinding = (data: TableColumnBinding) => {
  return request.post<number>('/table-column-binding', data)
}

// 批量保存表格列绑定
export const batchSaveBindings = (tableConfigId: number, bindings: TableColumnBinding[]) => {
  return request.post(`/table-column-binding/batch/${tableConfigId}`, bindings)
}

// 更新表格列绑定
export const updateTableColumnBinding = (id: number, data: TableColumnBinding) => {
  return request.put(`/table-column-binding/${id}`, data)
}

// 删除表格列绑定
export const deleteTableColumnBinding = (id: number) => {
  return request.delete(`/table-column-binding/${id}`)
}

// 删除表格配置的所有列绑定
export const deleteBindingsByTableConfigId = (tableConfigId: number) => {
  return request.delete(`/table-column-binding/table/${tableConfigId}`)
}
