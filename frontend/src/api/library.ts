import request from '@/utils/request'
import type {
  TemplateReferenceResult,
  SyncRequest,
  SyncResult,
  TemplateType,
  SyncStrategy
} from '@/types/page-v6'

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

// ============= 模板引用管理 API =============

/**
 * 查询引用了指定模板的页面列表
 * @param templateType 模板类型 (button/column/field)
 * @param templateId 模板ID
 * @returns 引用该模板的页面列表
 */
export const getTemplateReferences = (
  templateType: TemplateType,
  templateId: number
) => {
  return request.get<TemplateReferenceResult>(`/template/${templateType}/${templateId}/references`)
}

/**
 * 同步模板到页面
 * @param templateType 模板类型
 * @param templateId 模板ID
 * @param pageIds 要同步的页面ID列表
 * @param strategy 同步策略 (merge=合并保留overwrite, replace=替换清空overwrite)
 * @returns 同步结果
 */
export const syncTemplateToPages = (
  templateType: TemplateType,
  templateId: number,
  pageIds: number[],
  strategy: SyncStrategy
) => {
  return request.post<SyncResult>(`/template/${templateType}/${templateId}/sync`, {
    pageIds,
    strategy
  })
}

/**
 * 批量查询模板引用关系
 * @param requests 批量查询请求列表
 */
export const batchGetTemplateReferences = (
  requests: Array<{ templateType: TemplateType; templateId: number }>
) => {
  return request.post<TemplateReferenceResult[]>('/template/references/batch', requests)
}
