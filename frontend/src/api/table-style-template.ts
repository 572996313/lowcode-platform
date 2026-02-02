import { request } from '@/utils/request'

// 表格样式模板接口
export interface TableStyleTemplate {
  id?: number
  templateName: string
  templateCode: string
  description?: string
  styleConfig?: string
  isSystem?: boolean
  status?: boolean
  sortOrder?: number
  createTime?: string
  updateTime?: string
}

// 表格样式配置
export interface TableStyleConfig {
  border?: boolean
  stripe?: boolean
  size?: 'large' | 'default' | 'small'
  pagination?: boolean
  pageSize?: number
  showIndex?: boolean
  selection?: boolean
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 分页查询模板列表
export const getTableStyleTemplatePage = (params: any) => {
  return request.get<PageResult<TableStyleTemplate>>('/table-style-template/page', params)
}

// 获取所有启用的模板
export const getAllTableStyleTemplates = () => {
  return request.get<TableStyleTemplate[]>('/table-style-template/all')
}

// 根据编码获取模板
export const getTableStyleTemplateByCode = (code: string) => {
  return request.get<TableStyleTemplate>(`/table-style-template/code/${code}`)
}

// 创建模板
export const createTableStyleTemplate = (data: TableStyleTemplate) => {
  return request.post<number>('/table-style-template', data)
}

// 更新模板
export const updateTableStyleTemplate = (id: number, data: TableStyleTemplate) => {
  return request.put(`/table-style-template/${id}`, data)
}

// 删除模板
export const deleteTableStyleTemplate = (id: number) => {
  return request.delete(`/table-style-template/${id}`)
}

// 切换状态
export const toggleTableStyleTemplateStatus = (id: number) => {
  return request.put(`/table-style-template/${id}/toggle-status`)
}
