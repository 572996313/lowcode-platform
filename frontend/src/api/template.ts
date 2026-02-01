/**
 * 模板相关 API
 * 用于加载按钮、表格列、表单字段等模板配置
 */

import { request } from '@/utils/request'
import type {
  ButtonAction,
  ButtonType,
  ToolbarButton,
  TableColumn,
  SearchField,
  FieldType,
  ColumnType
} from '@/types/page-v6'

// ============= 类型定义 =============

/**
 * 按钮模板配置
 */
export interface ButtonTemplate {
  id: number
  buttonName: string
  buttonCode: string
  componentCategory: 'common' | 'business'
  position: 'toolbar' | 'row' | 'form' | 'dialog' | 'footer'
  buttonType: ButtonType
  buttonSize?: 'large' | 'default' | 'small'
  icon?: string
  actionType: 'api' | 'dialog' | 'drawer' | 'route' | 'link' | 'custom' | 'confirm'
  actionConfig?: string
  confirmConfig?: string
  perms?: string
  sortOrder?: number
}

/**
 * 表格列模板配置
 */
export interface ColumnTemplate {
  id: number
  columnName: string
  columnCode: string
  columnType: ColumnType
  width?: number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  formatterJson?: string
  tagConfigJson?: string
  dateConfigJson?: string
}

/**
 * 表单字段模板配置
 */
export interface FieldTemplate {
  id: number
  fieldName: string
  fieldCode: string
  fieldType: FieldType
  placeholder?: string
  required?: boolean
  optionsJson?: string
  defaultValue?: any
  rulesJson?: string
}

/**
 * 模板加载结果（包含完整配置和默认配置）
 */
export interface TemplateLoadResult<T> {
  templateConfig: T         // 完整模板配置
  defaultConfig: any        // 用于页面配置的默认配置
}

// ============= 按钮模板 API =============

/**
 * 获取按钮模板配置
 * @param templateId 模板ID
 * @returns 包含完整配置和默认配置的结果
 */
export const getButtonTemplate = (templateId: number) => {
  return request.get<TemplateLoadResult<Partial<ToolbarButton>>>(`/template/button/${templateId}`)
}

/**
 * 批量获取按钮模板配置
 * @param templateIds 模板ID列表
 */
export const getButtonTemplates = (templateIds: number[]) => {
  return request.post<TemplateLoadResult<Partial<ToolbarButton>>[]>('/template/button/batch', templateIds)
}

// ============= 表格列模板 API =============

/**
 * 获取表格列模板配置
 * @param templateId 模板ID
 */
export const getColumnTemplate = (templateId: number) => {
  return request.get<TemplateLoadResult<Partial<TableColumn>>>(`/template/column/${templateId}`)
}

/**
 * 批量获取表格列模板配置
 * @param templateIds 模板ID列表
 */
export const getColumnTemplates = (templateIds: number[]) => {
  return request.post<TemplateLoadResult<Partial<TableColumn>>[]>('/template/column/batch', templateIds)
}

// ============= 表单字段模板 API =============

/**
 * 获取表单字段模板配置
 * @param templateId 模板ID
 */
export const getFieldTemplate = (templateId: number) => {
  return request.get<TemplateLoadResult<Partial<SearchField>>>(`/template/field/${templateId}`)
}

/**
 * 批量获取表单字段模板配置
 * @param templateIds 模板ID列表
 */
export const getFieldTemplates = (templateIds: number[]) => {
  return request.post<TemplateLoadResult<Partial<SearchField>>[]>('/template/field/batch', templateIds)
}
