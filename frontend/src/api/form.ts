import { request } from '@/utils/request'

// 表单配置接口
export interface FormConfig {
  id?: number
  formName: string
  formCode: string
  templateCode?: string  // 表单模板编码
  pageId?: number
  tableId?: number
  styleTemplateId?: number  // 样式模板ID
  formType?: string
  layoutType?: string
  layoutCols?: number
  labelWidth?: number
  labelPosition?: string
  size?: string
  configJson?: string
  rulesJson?: string
  status?: boolean
  fields?: FormField[]
}

// 表单字段接口
export interface FormField {
  id?: number
  formId?: number
  fieldName: string
  fieldCode: string
  fieldType: string
  label: string
  placeholder?: string
  defaultValue?: string
  required?: boolean
  readonly?: boolean
  disabled?: boolean
  visible?: boolean
  sortOrder?: number
  span?: number
  labelWidth?: number
  slotId?: string  // 模板槽位ID（用于基于模板的表单）
  optionsJson?: string
  rulesJson?: string
  propsJson?: string
  eventsJson?: string
  linkageJson?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 获取表单列表
export const getFormList = (params: any) => {
  return request.get<PageResult<FormConfig>>('/form/list', params)
}

// 获取表单详情
export const getFormConfig = (id: number) => {
  return request.get<FormConfig>(`/form/${id}`)
}

// 创建表单
export const createForm = (data: FormConfig) => {
  return request.post<number>('/form', data)
}

// 更新表单
export const updateForm = (id: number, data: FormConfig) => {
  return request.put(`/form/${id}`, data)
}

// 删除表单
export const deleteForm = (id: number) => {
  return request.delete(`/form/${id}`)
}

// 根据表单ID查询按钮
export const getButtonsByFormId = (formId: number) => {
  return request.get(`/button/form/${formId}`)
}
