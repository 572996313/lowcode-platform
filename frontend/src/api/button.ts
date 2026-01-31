import { request } from '@/utils/request'

// 按钮配置接口
export interface ButtonConfig {
  id?: number
  buttonName: string
  buttonCode: string
  // pageId?: number  // 已废弃
  // formId?: number  // 已废弃
  // tableId?: number  // 已废弃
  componentCategory?: 'common' | 'business'  // 新增：组件分类
  componentTags?: string  // 新增：组件标签JSON
  position: 'toolbar' | 'row' | 'form' | 'dialog' | 'footer'
  buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  buttonSize?: 'large' | 'default' | 'small'
  icon?: string
  plain?: boolean
  round?: boolean
  circle?: boolean
  loading?: boolean
  disabled?: boolean
  actionType: 'api' | 'dialog' | 'drawer' | 'route' | 'link' | 'custom' | 'confirm'
  actionConfig?: string // JSON字符串
  confirmConfig?: string // JSON字符串
  perms?: string
  sortOrder?: number
  visible?: boolean
  showCondition?: string
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// 获取按钮列表（分页）
export const getButtonList = (params: any) => {
  return request.get<PageResult<ButtonConfig>>('/button/list', params)
}

// 根据页面ID查询按钮
export const getButtonsByPageId = (pageId: number) => {
  return request.get<ButtonConfig[]>(`/button/page/${pageId}`)
}

// 根据表单ID查询按钮
export const getButtonsByFormId = (formId: number) => {
  return request.get<ButtonConfig[]>(`/button/form/${formId}`)
}

// 根据表格ID查询按钮
export const getButtonsByTableId = (tableId: number) => {
  return request.get<ButtonConfig[]>(`/button/table/${tableId}`)
}

// 创建按钮
export const createButton = (data: ButtonConfig) => {
  return request.post<number>('/button', data)
}

// 批量保存按钮
export const batchSaveButtons = (pageId: number, buttons: ButtonConfig[]) => {
  return request.post(`/button/batch/${pageId}`, buttons)
}

// 按表单ID批量保存按钮
export const batchSaveButtonsByFormId = (formId: number, buttons: ButtonConfig[]) => {
  return request.post(`/button/batch/form/${formId}`, buttons)
}

// 按表格ID批量保存按钮
export const batchSaveButtonsByTableId = (tableId: number, buttons: ButtonConfig[]) => {
  return request.post(`/button/batch/table/${tableId}`, buttons)
}

// 更新按钮
export const updateButton = (id: number, data: ButtonConfig) => {
  return request.put(`/button/${id}`, data)
}

// 删除按钮
export const deleteButton = (id: number) => {
  return request.delete(`/button/${id}`)
}

// ✅ 新增接口

// 批量获取按钮配置（根据ID列表）
export const getButtonsByIds = (ids: number[]) => {
  return request.post<ButtonConfig[]>('/button/batch', ids)
}

// ❌ 废弃接口（保留但注释）

// // 根据表单ID查询按钮（已废弃，请使用组件库接口）
// export const getButtonsByFormId = (formId: number) => {
//   return request.get<ButtonConfig[]>(`/button/form/${formId}`)
// }

// // 根据表格ID查询按钮（已废弃，请使用组件库接口）
// export const getButtonsByTableId = (tableId: number) => {
//   return request.get<ButtonConfig[]>(`/button/table/${tableId}`)
// }

// // 按表单ID批量保存按钮（已废弃）
// export const batchSaveButtonsByFormId = (formId: number, buttons: ButtonConfig[]) => {
//   return request.post(`/button/batch/form/${formId}`, buttons)
// }

// // 按表格ID批量保存按钮（已废弃）
// export const batchSaveButtonsByTableId = (tableId: number, buttons: ButtonConfig[]) => {
//   return request.post(`/button/batch/table/${tableId}`, buttons)
// }
