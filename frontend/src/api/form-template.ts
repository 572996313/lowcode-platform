import request from '@/utils/request'

export interface FormTemplate {
  id?: number
  templateName: string
  templateCode: string
  description?: string
  previewImage?: string
  isSystem?: boolean
  status?: boolean
  sortOrder?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

// 获取模板分页列表
export const getTemplateList = (params: any) =>
  request.get<PageResult<FormTemplate>>('/api/form-template/page', params)

// 获取所有启用模板
export const getAllTemplates = () =>
  request.get<FormTemplate[]>('/api/form-template/all')

// 根据模板编码获取模板
export const getTemplateByCode = (code: string) =>
  request.get<FormTemplate>(`/api/form-template/code/${code}`)

// 创建模板
export const createTemplate = (data: FormTemplate) =>
  request.post<number>('/api/form-template', data)

// 更新模板
export const updateTemplate = (id: number, data: FormTemplate) =>
  request.put(`/api/form-template/${id}`, data)

// 删除模板
export const deleteTemplate = (id: number) =>
  request.delete(`/api/form-template/${id}`)