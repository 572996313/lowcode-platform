import { request } from '@/utils/request'

// 分组接口
export interface DbTableGroup {
  id?: number
  parentId?: number
  groupName: string
  groupCode?: string
  description?: string
  sortOrder?: number
  createTime?: string
  updateTime?: string
  children?: DbTableGroup[]
  tableCount?: number
}

// 获取分组树
export const getGroupTree = () => {
  return request.get<DbTableGroup[]>('/db-table-group/tree')
}

// 创建分组
export const createGroup = (data: DbTableGroup) => {
  return request.post<number>('/db-table-group', data)
}

// 更新分组
export const updateGroup = (id: number, data: DbTableGroup) => {
  return request.put(`/db-table-group/${id}`, data)
}

// 删除分组
export const deleteGroup = (id: number) => {
  return request.delete(`/db-table-group/${id}`)
}
