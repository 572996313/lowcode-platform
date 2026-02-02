import { request } from '@/utils/request'

// 库表接口
export interface DbTable {
  id?: number
  tableSchema: string
  tableName: string
  tableType: string
  tableComment?: string
  syncStatus?: number
  syncTime?: string
  groupId?: number | null
  createTime?: string
  updateTime?: string
  fields?: DbTableField[]
}

// 字段接口
export interface DbTableField {
  id?: number
  tableId?: number
  fieldName: string
  fieldType: string
  fieldLength?: number
  decimalDigits?: number
  isNullable?: number
  columnDefault?: string
  isPrimaryKey?: number
  isAutoIncrement?: number
  fieldComment?: string
  fieldLabel?: string
  ordinalPosition: number
  createTime?: string
  updateTime?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 获取库表列表
export const getTableList = (params: any) => {
  return request.get<PageResult<DbTable>>('/db-table/list', params)
}

// 获取库表详情（包含字段）
export const getTableDetail = (id: number) => {
  return request.get<DbTable>(`/db-table/${id}`)
}

// 获取表的字段列表
export const getTableFields = (id: number) => {
  return request.get<DbTableField[]>(`/db-table/${id}/fields`)
}

// 扫描并更新所有数据库对象
export const scanAllTables = () => {
  return request.post('/db-table/scan-all')
}

// 同步指定表的字段信息
export const syncTableFields = (id: number) => {
  return request.post(`/db-table/${id}/sync`)
}

// 更新库表信息
export const updateTable = (id: number, data: DbTable) => {
  return request.put(`/db-table/${id}`, data)
}

// 更新字段显示名称
export const updateFieldLabel = (fieldId: number, fieldLabel: string) => {
  return request.put(`/db-table/field/${fieldId}`, { fieldLabel })
}

// 批量更新字段标签
export const batchUpdateFieldLabels = (fieldUpdates: Array<{ id: number; fieldLabel: string }>) => {
  return request.put('/db-table/field/batch', fieldUpdates)
}

// 删除库表（级联删除字段）
export const deleteTable = (id: number) => {
  return request.delete(`/db-table/${id}`)
}

// 设置表的分组
export const setTableGroup = (id: number, groupId: number | null) => {
  return request.put(`/db-table/${id}/group`, { groupId })
}

// 批量设置表的分组
export const batchSetTableGroup = (tableIds: number[], groupId: number | null) => {
  return request.put('/db-table/batch-group', { tableIds, groupId })
}
