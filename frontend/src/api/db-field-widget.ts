import { request } from '@/utils/request'
import type { PageResult } from './db-table'

// ========== 数据来源类型定义 ==========

/**
 * 数据来源类型
 */
export type DataSourceType = 'static' | 'dict' | 'api'

/**
 * 静态数据来源
 */
export interface StaticDataSource {
  options: Array<{ label: string; value: any }>
}

/**
 * 数据字典来源
 */
export interface DictDataSource {
  code: string
}

/**
 * API接口数据来源
 */
export interface ApiDataSource {
  url: string
  method: 'GET' | 'POST'
  labelField: string
  valueField: string
  params?: Record<string, any>
}

/**
 * 数据来源配置
 */
export interface DataSource {
  type: DataSourceType
  static?: StaticDataSource
  dict?: DictDataSource
  api?: ApiDataSource
}

// ========== 字段-控件绑定配置接口 ==========
export interface DbFieldWidgetConfig {
  id?: number
  tableId?: number
  fieldId?: number
  widgetType: string
  widgetConfig?: string
  isDefault?: number
  defaultForFieldType?: string
  enabled?: number
  sortOrder?: number
  createTime?: string
  updateTime?: string
  table?: any
  field?: any
}

// 字段控件模板接口
export interface DbFieldWidgetTemplate {
  id?: number
  tableId: number
  fieldId: number
  templateName: string
  templateCode?: string
  widgetType: string
  widgetConfig?: string
  isSystem?: number
  description?: string
  enabled?: number
  sortOrder?: number
  createTime?: string
  updateTime?: string
  field?: any
}

// 字段配置及模板组合接口
export interface FieldConfigWithTemplates {
  config: DbFieldWidgetConfig | null
  templates: DbFieldWidgetTemplate[]
}

// 获取绑定配置列表
export const getConfigList = (params: any) => {
  return request.get<PageResult<DbFieldWidgetConfig>>('/db-field-widget/list', params)
}

// 获取配置详情
export const getConfigDetail = (id: number) => {
  return request.get<DbFieldWidgetConfig>(`/db-field-widget/${id}`)
}

// 获取表的所有绑定配置
export const getConfigsByTableId = (tableId: number) => {
  return request.get<DbFieldWidgetConfig[]>(`/db-field-widget/table/${tableId}`)
}

// 获取字段的绑定配置
export const getConfigByFieldId = (fieldId: number) => {
  return request.get<DbFieldWidgetConfig>(`/db-field-widget/field/${fieldId}`)
}

// 获取所有默认映射规则
export const getDefaultMappings = () => {
  return request.get<DbFieldWidgetConfig[]>('/db-field-widget/default-mappings')
}

// 创建绑定配置
export const createConfig = (data: DbFieldWidgetConfig) => {
  return request.post('/db-field-widget', data)
}

// 更新绑定配置
export const updateConfig = (id: number, data: DbFieldWidgetConfig) => {
  return request.put(`/db-field-widget/${id}`, data)
}

// 删除绑定配置
export const deleteConfig = (id: number) => {
  return request.delete(`/db-field-widget/${id}`)
}

// 批量保存表的字段绑定配置
export const batchSaveConfigs = (tableId: number, configs: DbFieldWidgetConfig[]) => {
  return request.post(`/db-field-widget/table/${tableId}/batch`, configs)
}

// 应用默认映射到表字段
export const applyDefaultsToTable = (tableId: number) => {
  return request.post(`/db-field-widget/table/${tableId}/apply-defaults`)
}

// 初始化系统默认映射规则
export const initDefaultMappings = () => {
  return request.post('/db-field-widget/init-defaults')
}

// ========== 字段控件模板 API ==========

// 获取字段的所有模板
export const getTemplatesByFieldId = (fieldId: number) => {
  return request.get<DbFieldWidgetTemplate[]>(`/db-field-widget-template/field/${fieldId}`)
}

// 获取表的所有模板
export const getTemplatesByTableId = (tableId: number) => {
  return request.get<DbFieldWidgetTemplate[]>(`/db-field-widget-template/table/${tableId}`)
}

// 根据字段ID和模板编码获取模板
export const getTemplateByCode = (fieldId: number, templateCode: string) => {
  return request.get<DbFieldWidgetTemplate>(`/db-field-widget-template/field/${fieldId}/code/${templateCode}`)
}

// 获取模板详情
export const getTemplateDetail = (id: number) => {
  return request.get<DbFieldWidgetTemplate>(`/db-field-widget-template/${id}`)
}

// 创建模板
export const createTemplate = (data: DbFieldWidgetTemplate) => {
  return request.post<DbFieldWidgetTemplate>('/db-field-widget-template', data)
}

// 更新模板
export const updateTemplate = (id: number, data: DbFieldWidgetTemplate) => {
  return request.put<DbFieldWidgetTemplate>(`/db-field-widget-template/${id}`, data)
}

// 删除模板
export const deleteTemplate = (id: number) => {
  return request.delete(`/db-field-widget-template/${id}`)
}

// 应用模板为主配置
export const applyTemplateAsPrimary = (templateId: number) => {
  return request.post(`/db-field-widget-template/${templateId}/apply`)
}

// 切换模板启用状态
export const toggleTemplateEnabled = (id: number, enabled: number) => {
  return request.put(`/db-field-widget-template/${id}/toggle`, null, { enabled })
}

// 获取字段配置及所有可用模板
export const getFieldConfigWithTemplates = (fieldId: number) => {
  return request.get<FieldConfigWithTemplates>(`/db-field-widget/field/${fieldId}/with-templates`)
}
