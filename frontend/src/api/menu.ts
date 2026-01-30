import { request } from '@/utils/request'

// 菜单接口
export interface Menu {
  id: number
  parentId: number
  menuName: string
  menuCode: string
  menuType: number
  icon?: string
  sortOrder: number
  routePath?: string
  componentPath?: string
  pageId?: number
  external?: boolean
  externalUrl?: string
  visible?: boolean
  status?: boolean
  children?: Menu[]
}

// 获取菜单树
export const getMenuTree = () => {
  return request.get<Menu[]>('/menu/tree')
}

// 获取菜单详情
export const getMenu = (id: number) => {
  return request.get<Menu>(`/menu/${id}`)
}

// 创建菜单
export const createMenu = (data: Partial<Menu>) => {
  return request.post<number>('/menu', data)
}

// 更新菜单
export const updateMenu = (id: number, data: Partial<Menu>) => {
  return request.put(`/menu/${id}`, data)
}

// 删除菜单
export const deleteMenu = (id: number) => {
  return request.delete(`/menu/${id}`)
}

// 根据页面ID查询关联的菜单
export const getMenusByPageId = (pageId: number) => {
  return request.get<Menu[]>(`/menu/by-page/${pageId}`)
}

// 批量更新菜单路由地址
export const syncRoutePath = (pageId: number, newRoutePath: string) => {
  return request.put<string>('/menu/sync-route', null, {
    params: { pageId, newRoutePath }
  })
}

// 批量禁用菜单（根据页面ID）
export const disableByPageId = (pageId: number) => {
  return request.put<string>(`/menu/disable-by-page/${pageId}`)
}
