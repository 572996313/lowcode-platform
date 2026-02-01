import { request } from '@/utils/request'

/**
 * 分页响应
 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

/**
 * 字典分类接口
 */
export interface DictCategory {
  id: number
  parentId: number
  categoryName: string
  categoryCode: string
  sortOrder: number
  status: boolean
  description?: string
  children?: DictCategory[]
}

/**
 * 字典明细接口
 */
export interface DictItem {
  id: number
  categoryId: number
  parentId: number
  itemLabel: string
  itemValue: string
  sortOrder: number
  status: boolean
  description?: string
  cssClass?: string
  icon?: string
  children?: DictItem[]
}

/**
 * 字典项（兼容旧接口）
 */
export interface DictItemSimple {
  label: string
  value: string
}

// ===== 分类接口 =====

/**
 * 获取字典分类树
 */
export const getCategoryTree = () => request.get<DictCategory[]>('/dict/category/tree')

/**
 * 创建字典分类
 */
export const createCategory = (data: Partial<DictCategory>) =>
  request.post<number>('/dict/category', data)

/**
 * 更新字典分类
 */
export const updateCategory = (id: number, data: Partial<DictCategory>) =>
  request.put(`/dict/category/${id}`, data)

/**
 * 删除字典分类
 */
export const deleteCategory = (id: number) => request.delete(`/dict/category/${id}`)

// ===== 字典明细接口 =====

/**
 * 获取分类下的字典树
 */
export const getItemTree = (categoryId: number) =>
  request.get<DictItem[]>(`/dict/item/tree?categoryId=${categoryId}`)

/**
 * 分页查询字典明细
 */
export const getItemPage = (params: {
  categoryId: number
  parentId?: number
  current: number
  size: number
}) => request.get<PageResult<DictItem>>('/dict/item/page', { params })

/**
 * 创建字典明细
 */
export const createItem = (data: Partial<DictItem>) => request.post<number>('/dict/item', data)

/**
 * 更新字典明细
 */
export const updateItem = (id: number, data: Partial<DictItem>) =>
  request.put(`/dict/item/${id}`, data)

/**
 * 删除字典明细
 */
export const deleteItem = (id: number) => request.delete(`/dict/item/${id}`)

/**
 * 批量删除字典明细
 */
export const batchDeleteItems = (ids: number[]) => request.post('/dict/item/batch-delete', ids)

// ===== 控件调用接口 =====

/**
 * 根据分类编码获取字典数据
 */
export const getItemsByCategoryCode = (categoryCode: string) =>
  request.get<DictItem[]>(`/dict/items/by-code?categoryCode=${categoryCode}`)

// ===== 兼容旧接口 =====

/**
 * 根据字典编码获取字典数据（兼容旧接口）
 * @deprecated 请使用 getItemsByCategoryCode
 */
export const getDictByCode = (code: string) => request.get<{ label: string; value: string }[]>(`/dict/${code}`)
