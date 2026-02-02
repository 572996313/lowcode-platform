import { request } from '@/utils/request'

// 表单字段绑定接口
export interface FormFieldBinding {
  id?: number
  formId: number
  fieldId: number
  widgetType: string
  widgetConfig?: string
  span?: number
  required?: boolean
  visible?: boolean
  readonly?: boolean
  disabled?: boolean
  sortOrder?: number
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

// 获取表单字段绑定列表
export const getFormFieldBindingList = (params: any) => {
  return request.get<PageResult<FormFieldBinding>>('/form-field-binding/list', params)
}

// 获取表单字段绑定详情
export const getFormFieldBinding = (id: number) => {
  return request.get<FormFieldBinding>(`/form-field-binding/${id}`)
}

// 根据表单ID获取字段绑定列表（包含字段信息）
export const getBindingsByFormId = (formId: number) => {
  return request.get<FormFieldBinding[]>(`/form-field-binding/form/${formId}`)
}

// 根据数据库表ID获取可绑定的字段列表
export const getAvailableFields = (tableId: number) => {
  return request.get(`/form-field-binding/table/${tableId}/fields`)
}

// 创建表单字段绑定
export const createFormFieldBinding = (data: FormFieldBinding) => {
  return request.post<number>('/form-field-binding', data)
}

// 批量保存表单字段绑定
export const batchSaveBindings = (formId: number, bindings: FormFieldBinding[]) => {
  return request.post(`/form-field-binding/batch/${formId}`, bindings)
}

// 更新表单字段绑定
export const updateFormFieldBinding = (id: number, data: FormFieldBinding) => {
  return request.put(`/form-field-binding/${id}`, data)
}

// 删除表单字段绑定
export const deleteFormFieldBinding = (id: number) => {
  return request.delete(`/form-field-binding/${id}`)
}

// 删除表单的所有字段绑定
export const deleteBindingsByFormId = (formId: number) => {
  return request.delete(`/form-field-binding/form/${formId}`)
}
