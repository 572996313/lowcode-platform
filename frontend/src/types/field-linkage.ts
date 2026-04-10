/**
 * 字段联动配置类型定义
 */

/**
 * 联动类型枚举
 */
export type LinkageType = 'visibility' | 'value' | 'options' | 'required' | 'readonly'

/**
 * 条件操作符枚举
 */
export type ConditionOperator = 'equals' | 'not_equals' | 'in' | 'not_in' | 'contains' | 'not_contains' | 'greater_than' | 'less_than' | 'not_empty' | 'is_empty'

/**
 * 条件配置
 */
export interface ConditionConfig {
  operator: ConditionOperator
  value?: any
  values?: any[]
}

/**
 * 动作配置
 */
export interface ActionConfig {
  visible?: boolean
  required?: boolean
  readonly?: boolean
  value?: any
  dataSource?: 'dict' | 'api' | 'static'
  dictCode?: string
  apiUrl?: string
  options?: Array<{ label: string; value: any }>
}

/**
 * 字段联动配置
 */
export interface FieldLinkage {
  id?: number
  ruleName: string
  ruleCode: string
  sourceField: string
  targetField: string
  linkageType: LinkageType
  conditionConfig: ConditionConfig
  actionConfig: ActionConfig
  description?: string
  status: number
  createTime?: string
  updateTime?: string
}

/**
 * 联动类型标签映射
 */
export const LinkageTypeLabels: Record<LinkageType, string> = {
  visibility: '显示/隐藏',
  value: '值改变',
  options: '选项改变',
  required: '必填改变',
  readonly: '只读改变'
}

/**
 * 联动类型颜色映射
 */
export const LinkageTypeColors: Record<LinkageType, string> = {
  visibility: 'success',
  value: 'primary',
  options: 'warning',
  required: 'danger',
  readonly: 'info'
}

/**
 * 条件操作符标签映射
 */
export const ConditionOperatorLabels: Record<ConditionOperator, string> = {
  equals: '等于',
  not_equals: '不等于',
  in: '包含于',
  not_in: '不包含于',
  contains: '包含',
  not_contains: '不包含',
  greater_than: '大于',
  less_than: '小于',
  not_empty: '不为空',
  is_empty: '为空'
}
