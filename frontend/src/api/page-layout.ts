import { request } from '@/utils/request'

// 页面布局接口
export interface PageLayout {
  id?: number
  layoutName: string
  layoutCode: string
  layoutType: string
  areaConfig?: string
  published?: boolean
  routePath?: string
  menuId?: number
  status?: boolean
  createBy?: string
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

// 区域配置接口
export interface AreaConfig {
  id: string
  title: string
  componentType: 'form' | 'table' | 'button'
  formId?: number
  tableId?: number
  buttonIds?: number[]
}

// 获取页面布局列表
export const getPageLayoutList = (params: any) => {
  return request.get<PageResult<PageLayout>>('/page-layout/list', params)
}

// 获取页面布局详情
export const getPageLayout = (id: number) => {
  return request.get<PageLayout>(`/page-layout/${id}`)
}

// 根据编码获取页面布局
export const getPageLayoutByCode = (layoutCode: string) => {
  return request.get<PageLayout>(`/page-layout/code/${layoutCode}`)
}

// 创建页面布局
export const createPageLayout = (data: PageLayout) => {
  return request.post<number>('/page-layout', data)
}

// 更新页面布局
export const updatePageLayout = (id: number, data: PageLayout) => {
  return request.put(`/page-layout/${id}`, data)
}

// 删除页面布局
export const deletePageLayout = (id: number) => {
  return request.delete(`/page-layout/${id}`)
}

// 发布页面布局
export const publishPageLayout = (id: number) => {
  return request.post(`/page-layout/${id}/publish`)
}

// 取消发布页面布局
export const unpublishPageLayout = (id: number) => {
  return request.post(`/page-layout/${id}/unpublish`)
}
