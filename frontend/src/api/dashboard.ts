import { request } from '@/utils/request'

/**
 * 首页统计数据接口
 */
export interface DashboardStats {
  // 统计数据
  pageCount: number
  publishedPageCount: number
  formCount: number
  tableCount: number
  menuCount: number
  buttonCount: number
  dbTableCount: number
  // 系统信息
  systemVersion: string
  javaVersion: string
  springBootVersion: string
  databaseVersion: string
  frontendFramework: string
  osName: string
  osArch: string
}

/**
 * 获取首页统计数据
 */
export const getDashboardStats = () => {
  return request.get<DashboardStats>('/dashboard/stats')
}
