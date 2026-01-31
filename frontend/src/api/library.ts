import request from '@/utils/request'

/**
 * 组件库相关接口
 */

/**
 * 获取组件库中的组件
 * @param libraryType 组件分类 (common=通用组件库, business=业务组件库)
 * @param componentType 组件类型 (form/table/button，不传则返回所有类型)
 */
export const getLibraryComponents = (
  libraryType: 'common' | 'business',
  componentType?: string
) => {
  return request.get<{
    buttons: ButtonConfig[]
    forms: FormConfig[]
    tables: TableConfig[]
  }>('/library/components', {
    params: { libraryType, componentType }
  })
}

/**
 * 获取按钮库
 * @param libraryType 组件分类 (common=通用组件库, business=业务组件库)
 * @param tags 标签筛选 (逗号分隔，如: crud,export)
 */
export const getButtonLibrary = (
  libraryType: 'common' | 'business',
  tags?: string
) => {
  return request.get<ButtonConfig[]>(`/button/library/${libraryType}`, {
    params: { tags }
  })
}

/**
 * 批量获取按钮配置
 * @param ids 按钮 ID 列表
 */
export const getButtonsByIds = (ids: number[]) => {
  return request.post<ButtonConfig[]>('/button/batch', ids)
}

/**
 * 获取组件库统计信息
 */
export const getLibraryStats = () => {
  return request.get<{
    common: {
      buttons: number
      forms: number
      tables: number
    }
    business: {
      buttons: number
      forms: number
      tables: number
    }
  }>('/library/stats')
}
