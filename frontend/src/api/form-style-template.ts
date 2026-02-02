import { request } from '@/utils/request'

// 表单样式模板接口
export interface FormStyleTemplate {
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

// 表单样式配置
export interface FormStyleConfig {
  labelWidth?: number
  labelPosition?: 'left' | 'right' | 'top'
  layoutCols?: 1 | 2 | 3 | 4
  size?: 'large' | 'default' | 'small'
  border?: boolean
  backgroundColor?: string
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
export const getFormStyleTemplatePage = (params: any) => {
  return request.get<PageResult<FormStyleTemplate>>('/form-style-template/page', params)
}

// 获取所有启用的模板
export const getAllFormStyleTemplates = () => {
  return request.get<FormStyleTemplate[]>('/form-style-template/all')
}

// 根据编码获取模板
export const getFormStyleTemplateByCode = (code: string) => {
  return request.get<FormStyleTemplate>(`/form-style-template/code/${code}`)
}

// 创建模板
export const createFormStyleTemplate = (data: FormStyleTemplate) => {
  return request.post<number>('/form-style-template', data)
}

// 更新模板
export const updateFormStyleTemplate = (id: number, data: FormStyleTemplate) => {
  return request.put(`/form-style-template/${id}`, data)
}

// 删除模板
export const deleteFormStyleTemplate = (id: number) => {
  return request.delete(`/form-style-template/${id}`)
}

// 切换状态
export const toggleFormStyleTemplateStatus = (id: number) => {
  return request.put(`/form-style-template/${id}/toggle-status`)
}
