import { request } from '@/utils/request'

// 查询字段接口
export interface SearchField {
  id: string
  label: string
  fieldCode: string
  type: 'input' | 'select' | 'date' | 'dateRange' | 'number'
  placeholder?: string
  options?: Array<{ label: string; value: string }>
  required?: boolean
}

// 查询区域配置
export interface SearchAreaConfig {
  enabled: boolean
  title: string
  fields: SearchField[]
}

// 内容区域配置
export interface ContentAreaConfig {
  type: 'table' | 'form'
  configId: number
  title: string
  showToolbar: boolean
}

// 页面配置接口
export interface PageConfig {
  id?: number
  pageName: string
  pageCode: string
  pageType?: 'list' | 'form' | 'custom'
  configJson?: string  // 后端存储的是 JSON 字符串
  configJsonObject?: {
    searchArea: SearchAreaConfig
    contentArea: ContentAreaConfig
  }  // 前端使用的对象形式
  layoutConfig?: string  // 后端存储的是 JSON 字符串
  layoutConfigObject?: {
    searchAreaHeight?: string
    contentAreaFlex?: number
  }  // 前端使用的对象形式
  status?: boolean
  description?: string
}

// 分页结果接口
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 获取页面列表（分页）
export const getPageList = (params: { current: number; size: number; keyword?: string }) => {
  return request.get<PageResult<PageConfig>>('/page/list', params)
}

// 获取页面详情
export const getPage = (id: number) => {
  return request.get<PageConfig>(`/page/${id}`)
}

// 创建页面
export const createPage = (data: PageConfig) => {
  // 在提交前转换对象为 JSON 字符串
  const submitData = { ...data }
  if (submitData.configJsonObject) {
    submitData.configJson = JSON.stringify(submitData.configJsonObject)
  }
  if (submitData.layoutConfigObject) {
    submitData.layoutConfig = JSON.stringify(submitData.layoutConfigObject)
  }
  return request.post<number>('/page', submitData)
}

// 更新页面
export const updatePage = (id: number, data: Partial<PageConfig>) => {
  // 在提交前转换对象为 JSON 字符串
  const submitData = { ...data }
  if (submitData.configJsonObject) {
    submitData.configJson = JSON.stringify(submitData.configJsonObject)
  }
  if (submitData.layoutConfigObject) {
    submitData.layoutConfig = JSON.stringify(submitData.layoutConfigObject)
  }
  return request.put(`/page/${id}`, submitData)
}

// 删除页面
export const deletePage = (id: number) => {
  return request.delete(`/page/${id}`)
}

// 获取所有页面（不分页，用于下拉选择）
export const getAllPages = () => {
  return request.get<PageConfig[]>('/page/all')
}
