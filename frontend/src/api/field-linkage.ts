/**
 * 字段联动配置 API 接口
 */

import { request } from '@/utils/request'
import type { FieldLinkage } from '@/types/field-linkage'

/**
 * 分页查询联动规则
 */
export function getFieldLinkagePage(params: {
  current: number
  size: number
  sourceField?: string
  targetField?: string
}) {
  return request.get('/field-linkage/page', { params })
}

/**
 * 获取联动规则详情
 */
export function getFieldLinkage(id: number) {
  return request.get<FieldLinkage>(`/field-linkage/${id}`)
}

/**
 * 创建联动规则
 */
export function createFieldLinkage(data: FieldLinkage) {
  return request.post<number>('/field-linkage', data)
}

/**
 * 更新联动规则
 */
export function updateFieldLinkage(id: number, data: FieldLinkage) {
  return request.put(`/field-linkage/${id}`, data)
}

/**
 * 删除联动规则
 */
export function deleteFieldLinkage(id: number) {
  return request.delete(`/field-linkage/${id}`)
}

/**
 * 批量删除联动规则
 */
export function batchDeleteFieldLinkage(ids: number[]) {
  return request.delete('/field-linkage/batch', { data: ids })
}

/**
 * 根据源字段获取联动规则
 */
export function getFieldLinkageBySource(sourceField: string) {
  return request.get<FieldLinkage[]>(`/field-linkage/source/${sourceField}`)
}

/**
 * 执行字段联动计算
 */
export function executeFieldLinkage(formData: Record<string, any>) {
  return request.post<Record<string, any>>('/field-linkage/execute', formData)
}
