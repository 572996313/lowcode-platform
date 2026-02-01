// --- 下拉相关类型定义 ---

/** 下拉类型 */
export type DropdownType = 'flat' | 'tree' | 'cascader'

/** 数据源类型 */
export type DataSourceType = 'static' | 'dict' | 'api' | 'dropdown'

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

/** 下拉数据源配置 */
export interface DropdownDataSource {
  configCode: string      // 下拉配置编码
  params?: Record<string, any>  // 运行时参数
}

/** 静态数据源 */
export interface StaticDataSource {
  options: DropdownOption[]
}

/** 字典数据源 */
export interface DictDataSource {
  dictCode: string
}

/** API 数据源 */
export interface ApiDataSource {
  apiUrl: string
  labelField: string
  valueField: string
  method?: 'GET' | 'POST'
  params?: Record<string, any>
}
