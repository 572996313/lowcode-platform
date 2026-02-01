/**
 * 模板注册表
 *
 * 所有可用的页面模板都在这里注册
 */

import type { TemplateMeta } from '@/types/template'

export const TEMPLATES: TemplateMeta[] = [
  {
    id: 'standard-list',
    name: '标准列表页',
    type: 'list',
    category: '常用',
    description: '包含查询区、工具栏、数据表格的经典布局',
    previewImage: '/templates/standard-list.png',

    // 设计器组件
    designer: () => import('@/designer-templates/StandardListTemplate.vue'),

    // 渲染器组件
    renderer: () => import('@/render-templates/StandardListRender.vue')
  },
  {
    id: 'tree-table',
    name: '左树右表页',
    type: 'list',
    category: '常用',
    description: '左侧树形导航，右侧数据表格的布局',
    previewImage: '/templates/tree-table.png',

    designer: () => import('@/designer-templates/TreeTableTemplate.vue'),
    renderer: () => import('@/render-templates/TreeTableRender.vue')
  },
  {
    id: 'form-page',
    name: '表单页',
    type: 'form',
    category: '常用',
    description: '纯表单提交页面',

    designer: () => import('@/designer-templates/FormPageTemplate.vue'),
    renderer: () => import('@/render-templates/FormPageRender.vue')
  }
]

/**
 * 根据 ID 获取模板
 */
export function getTemplateById(id: string): TemplateMeta | undefined {
  return TEMPLATES.find(t => t.id === id)
}

/**
 * 根据类型获取模板列表
 */
export function getTemplatesByType(type: string): TemplateMeta[] {
  return TEMPLATES.filter(t => t.type === type)
}

/**
 * 根据分类获取模板列表
 */
export function getTemplatesByCategory(category: string): TemplateMeta[] {
  return TEMPLATES.filter(t => t.category === category)
}

/**
 * 获取所有分类
 */
export function getCategories(): string[] {
  return Array.from(new Set(TEMPLATES.map(t => t.category)))
}
