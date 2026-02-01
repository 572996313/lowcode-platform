/**
 * 页面配置 API（V6 版本）
 */
import request from '@/utils/request'
import type { NewPageConfig } from '@/types/page-v6'

/**
 * 分页查询页面列表
 */
export function getPageList(params: {
  page: number
  size: number
  keyword?: string
}) {
  return request({
    url: '/lowcode/v6/page',
    method: 'get',
    params
  })
}

/**
 * 获取页面详情
 */
export function getPageConfig(id: number) {
  return request({
    url: `/lowcode/v6/page/${id}`,
    method: 'get'
  })
}

/**
 * 新增页面
 */
export function createPage(data: NewPageConfig) {
  return request({
    url: '/lowcode/v6/page',
    method: 'post',
    data
  })
}

/**
 * 更新页面
 */
export function updatePage(id: number, data: NewPageConfig) {
  return request({
    url: `/lowcode/v6/page/${id}`,
    method: 'put',
    data
  })
}

/**
 * 保存页面配置（新增或更新）
 */
export function savePageConfig(data: NewPageConfig) {
  if (data.pageId) {
    return updatePage(data.pageId, data)
  }
  return createPage(data)
}

/**
 * 删除页面
 */
export function deletePage(id: number) {
  return request({
    url: `/lowcode/v6/page/${id}`,
    method: 'delete'
  })
}

/**
 * 发布页面
 */
export function publishPage(id: number) {
  return request({
    url: `/lowcode/v6/page/${id}/publish`,
    method: 'post'
  })
}

/**
 * 取消发布页面
 */
export function unpublishPage(id: number) {
  return request({
    url: `/lowcode/v6/page/${id}/unpublish`,
    method: 'post'
  })
}

/**
 * 复制页面
 */
export function copyPage(id: number, newName: string) {
  return request({
    url: `/lowcode/v6/page/${id}/copy`,
    method: 'post',
    data: { pageName: newName }
  })
}
