import { request } from '@/utils/request'

// --- 类型定义 ---

/** 下拉类型 */
export type DropdownType = 'flat' | 'tree' | 'cascader'

/** 参数配置 */
export interface ParamConfig {
  name: string
  type: string
  required: boolean
  defaultValue?: any
  description?: string
}

/** 下拉配置 */
export interface DropdownConfig {
  id?: number
  configName: string
  configCode: string
  dropdownType: DropdownType
  sqlTemplate: string
  labelField: string
  valueField: string
  parentField?: string
  paramConfig?: string
  extraFields?: string
  description?: string
  enabled?: boolean
  sortOrder?: number
  createdBy?: string
  createTime?: string
  updateTime?: string
  deleted?: boolean
}

/** 下拉选项 */
export interface DropdownOption {
  label: string
  value: any
  children?: DropdownOption[]
  [key: string]: any
}

/** 下拉数据响应 */
export interface DropdownDataResponse {
  labelField: string
  valueField: string
  parentField?: string
  dropdownType: DropdownType
  options: DropdownOption[]
}

/** 分页结果 */
export interface PageResult<T> {
  total: number
  records: T[]
  current: number
  size: number
  pages: number
}

// --- 下拉配置管理接口 ---

/** 分页查询下拉配置 */
export const getDropdownList = (params: any) => {
  return request.get<PageResult<DropdownConfig>>('/dropdown/config/list', params)
}

/** 获取下拉配置详情 */
export const getDropdownConfig = (id: number) => {
  return request.get<DropdownConfig>(`/dropdown/config/${id}`)
}

/** 根据编码获取配置 */
export const getDropdownByCode = (code: string) => {
  return request.get<DropdownConfig>(`/dropdown/config/code/${code}`)
}

/** 创建下拉配置 */
export const createDropdown = (data: DropdownConfig) => {
  return request.post<number>('/dropdown/config', data)
}

/** 更新下拉配置 */
export const updateDropdown = (id: number, data: DropdownConfig) => {
  return request.put(`/dropdown/config/${id}`, data)
}

/** 删除下拉配置 */
export const deleteDropdown = (id: number) => {
  return request.delete(`/dropdown/config/${id}`)
}

/** 测试 SQL */
export const testSql = (config: DropdownConfig, params?: Record<string, any>) => {
  return request.post<Record<string, any>[]>('/dropdown/config/test', { config, params })
}

// --- 下拉数据查询接口 ---

/** 查询下拉数据 */
export const queryDropdownData = (configId?: number, configCode?: string, params?: Record<string, any>) => {
  return request.post<DropdownDataResponse>('/dropdown/data/query', { configId, configCode, params })
}

/** 根据配置ID获取下拉数据 */
export const getDropdownData = (configId: number) => {
  return request.get<DropdownDataResponse>(`/dropdown/data/${configId}`)
}

// --- 工具函数 ---

/** 解析参数配置 JSON */
export const parseParamConfig = (jsonStr?: string): ParamConfig[] => {
  if (!jsonStr) return []
  try {
    const data = JSON.parse(jsonStr)
    return data.params || []
  } catch {
    return []
  }
}

/** 序列化参数配置 JSON */
export const stringifyParamConfig = (params: ParamConfig[]): string => {
  const data = { params: params || [] }
  return JSON.stringify(data)
}

/** 解析额外字段 */
export const parseExtraFields = (fieldsStr?: string): string[] => {
  if (!fieldsStr) return []
  return fieldsStr.split(',').map(f => f.trim()).filter(f => f)
}

/** 序列化额外字段 */
export const stringifyExtraFields = (fields: string[]): string => {
  return fields.filter(f => f).join(',')
}
