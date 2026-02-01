import { request } from '@/utils/request'
import type { NewPageConfig } from '@/types/page-v6'

// =============================================
// 类型定义（兼容旧代码）
// =============================================

// 分页结果接口
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 页面配置接口（兼容）
export interface PageConfig {
  id?: number
  pageName: string
  pageCode: string
  pageType?: 'list' | 'form' | 'custom' | 'detail' | 'dashboard'
  templateId?: number
  layoutType?: string
  configJson?: string
  configTemplate?: string
  layoutConfig?: string
  status?: boolean
  description?: string
  routePath?: string
  published?: boolean
  publishTime?: string
}

// =============================================
// API 接口
// =============================================

// 获取页面列表（分页）
export const getPageList = (params: { current: number; size: number; keyword?: string }) => {
  return request.get<PageResult<PageConfig>>('/page/list', params)
}

// 获取页面详情
export const getPage = (id: number) => {
  return request.get<PageConfig>(`/page/${id}`)
}

// 创建页面
export const createPage = (data: Partial<PageConfig>) => {
  return request.post<number>('/page', data)
}

// 更新页面
export const updatePage = (id: number, data: Partial<PageConfig>) => {
  return request.put(`/page/${id}`, data)
}

// 删除页面
export const deletePage = (id: number) => {
  return request.delete(`/page/${id}`)
}

// 发布页面
export const publishPage = (id: number, data: { routePath: string }) => {
  return request.post<number>(`/page/${id}/publish`, data)
}

// 取消发布页面
export const unpublishPage = (id: number) => {
  return request.post(`/page/${id}/unpublish`)
}

// 获取已发布的页面
export const getPublishedPages = () => {
  return request.get<PageConfig[]>('/page/published')
}

// 获取所有页面（不分页，用于下拉选择）
export const getAllPages = () => {
  return request.get<PageConfig[]>('/page/all')
}
