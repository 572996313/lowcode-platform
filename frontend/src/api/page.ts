import { request } from '@/utils/request'

// 查询字段接口
export interface SearchField {
  id: string
  label: string
  fieldCode: string
  type: 'input' | 'select' | 'date' | 'dateRange' | 'number'
  placeholder?: string
  options?: Array<{ label: string; value: string }>
  required?: boolean
}

// 查询区域配置
export interface SearchAreaConfig {
  enabled: boolean
  title: string
  fields: SearchField[]
}

// 内容区域配置
export interface ContentAreaConfig {
  type: 'table' | 'form'
  configId: number
  title: string
  showToolbar: boolean
}

// 页面配置接口（v1版本，兼容旧版）
export interface PageConfig {
  id?: number
  pageName: string
  pageCode: string
  pageType?: 'list' | 'form' | 'custom' | 'detail' | 'dashboard'
  templateId?: number
  layoutType?: string
  configJson?: string  // 后端存储的是 JSON 字符串
  configJsonObject?: {
    searchArea?: SearchAreaConfig
    contentArea?: ContentAreaConfig
  }  // 前端使用的对象形式
  configTemplate?: string  // v2版本配置模板
  layoutConfig?: string  // 后端存储的是 JSON 字符串
  layoutConfigObject?: {
    searchAreaHeight?: string
    contentAreaFlex?: number
  }  // 前端使用的对象形式
  status?: boolean
  description?: string
  configVersion?: number  // 配置版本
  routePath?: string  // 路由路径
  published?: boolean  // 是否已发布
  publishTime?: string  // 发布时间
}

// =============================================
// 新版配置类型定义 (v2)
// =============================================

// 配置版本标识
export const CONFIG_VERSION_V2 = 2
export const CONFIG_VERSION_V1 = 1

// 基础组件类型
export interface BaseComponent {
  id: string
  type: string
  name?: string
  props: Record<string, any>
  condition?: string
}

// 容器组件
export interface ContainerComponent extends BaseComponent {
  type: 'container'
  layoutType: 'row' | 'column' | 'tabs' | 'grid' | 'card' | 'collapsible'
  children: Component[]
}

// 表格组件
export interface TableComponent extends BaseComponent {
  type: 'table'
  configId?: number
  props: {
    showToolbar?: boolean
    autoLoad?: boolean
    [key: string]: any
  }
}

// 表单组件
export interface FormComponent extends BaseComponent {
  type: 'form'
  configId?: number
  props: {
    formType?: 'add' | 'edit' | 'detail' | 'search'
    mode?: 'toolbar' | 'inline' | 'card'
    [key: string]: any
  }
}

// 组件联合类型
export type Component = ContainerComponent | TableComponent | FormComponent | BaseComponent

// 新版页面配置结构
export interface NewPageConfig {
  version: number
  page: {
    title: string
    description?: string
  }
  root: ContainerComponent
  globalButtons?: any[]
}

// 组件模板类型
export interface ComponentTemplate {
  id: number
  templateName: string
  templateCode: string
  componentType: string
  configTemplate: string
  category: 'layout' | 'content'
  previewImage?: string
  description: string
  keywords: string
  isSystem: boolean
  sortOrder: number
}

// 页面模板类型
export interface PageTemplate {
  id: number
  templateName: string
  templateCode: string
  templateType: string
  layoutType: string
  configTemplate?: string
  previewImage?: string
  description: string
  isSystem: boolean
}

// 分页结果接口
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 获取页面列表（分页）
export const getPageList = (params: { current: number; size: number; keyword?: string }) => {
  return request.get<PageResult<PageConfig>>('/page/list', params)
}

// 获取页面详情
export const getPage = (id: number) => {
  return request.get<PageConfig>(`/page/${id}`)
}

// 创建页面
export const createPage = (data: PageConfig) => {
  // 在提交前转换对象为 JSON 字符串
  const submitData = { ...data }
  if (submitData.configJsonObject) {
    submitData.configJson = JSON.stringify(submitData.configJsonObject)
  }
  if (submitData.layoutConfigObject) {
    submitData.layoutConfig = JSON.stringify(submitData.layoutConfigObject)
  }
  return request.post<number>('/page', submitData)
}

// 更新页面
export const updatePage = (id: number, data: Partial<PageConfig>) => {
  // 在提交前转换对象为 JSON 字符串
  const submitData = { ...data }
  if (submitData.configJsonObject) {
    submitData.configJson = JSON.stringify(submitData.configJsonObject)
  }
  if (submitData.layoutConfigObject) {
    submitData.layoutConfig = JSON.stringify(submitData.layoutConfigObject)
  }
  return request.put(`/page/${id}`, submitData)
}

// 删除页面
export const deletePage = (id: number) => {
  return request.delete(`/page/${id}`)
}

// 发布页面
export const publishPage = (id: number, data: { routePath: string }) => {
  return request.post<number>(`/page/${id}/publish`, data)
}

// 取消发布页面
export const unpublishPage = (id: number) => {
  return request.post(`/page/${id}/unpublish`)
}

// 获取已发布的页面
export const getPublishedPages = () => {
  return request.get<PageConfig[]>('/page/published')
}

// 获取所有页面（不分页，用于下拉选择）
export const getAllPages = () => {
  return request.get<PageConfig[]>('/page/all')
}

// =============================================
// 模板相关接口（v2）
// =============================================

// 获取组件模板列表（分页）
export const getComponentTemplates = (params?: {
  current?: number
  size?: number
  keyword?: string
  category?: string
  componentType?: string
}) => {
  return request.get<PageResult<ComponentTemplate>>('/component-template/list', params)
}

// 获取组件模板列表（不分页）
export const getComponentTemplatesAll = (params?: {
  category?: string
  componentType?: string
}) => {
  return request.get<ComponentTemplate[]>('/component-template/all', params)
}

// 获取组件模板详情
export const getComponentTemplate = (id: number) => {
  return request.get<ComponentTemplate>(`/component-template/${id}`)
}

// 获取页面模板列表
export const getPageTemplates = () => {
  return request.get<PageTemplate[]>('/page/template/list')
}

// 获取页面模板详情
export const getPageTemplate = (id: number) => {
  return request.get<PageTemplate>(`/page/template/${id}`)
}

// 从模板创建页面
export const createPageFromTemplate = (params: {
  templateCode: string
  pageName: string
  pageCode: string
}) => {
  return request.post<number>('/page/template/from-template', params)
}

// =============================================
// 配置迁移工具
// =============================================

/**
 * 获取默认配置
 */
export const getDefaultConfig = (): NewPageConfig => ({
  version: 2,
  page: { title: '' },
  root: {
    id: 'root',
    type: 'container',
    layoutType: 'column',
    children: []
  }
})

/**
 * 配置迁移：将 v1 配置升级到 v2
 */
export const migrateConfig = (config: any): NewPageConfig => {
  if (config.version === 2) {
    return config as NewPageConfig
  }

  // v1 配置迁移逻辑
  const newConfig: NewPageConfig = {
    version: 2,
    page: { title: '', description: '' },
    root: {
      id: 'root_migrated',
      type: 'container',
      layoutType: 'column',
      children: []
    }
  }

  if (config.configJsonObject) {
    const { searchArea, contentArea } = config.configJsonObject

    // 迁移查询区
    if (searchArea && searchArea.enabled) {
      newConfig.root.children?.push({
        id: 'search_section',
        type: 'container',
        name: searchArea.title || '查询条件',
        layoutType: 'collapsible',
        props: { title: searchArea.title || '查询条件', defaultCollapsed: false },
        children: []
      })
    }

    // 迁移内容区
    if (contentArea) {
      if (contentArea.type === 'table') {
        newConfig.root.children?.push({
          id: 'main_table',
          type: 'table',
          name: contentArea.title || '数据表格',
          props: { showToolbar: contentArea.showToolbar }
        })
      } else if (contentArea.type === 'form') {
        newConfig.root.children?.push({
          id: 'main_form',
          type: 'form',
          name: contentArea.title || '数据表单',
          props: {}
        })
      }
    }
  }

  return newConfig
}

/**
 * 解析配置JSON字符串
 */
export const parseConfigJson = (jsonStr?: string): any => {
  if (!jsonStr) return getDefaultConfig()
  try {
    const config = JSON.parse(jsonStr)
    return migrateConfig(config)
  } catch (e) {
    console.error('解析配置JSON失败:', e)
    return getDefaultConfig()
  }
}

/**
 * 序列化配置对象为JSON字符串
 */
export const stringifyConfigJson = (config: NewPageConfig): string => {
  try {
    return JSON.stringify(config)
  } catch (e) {
    console.error('序列化配置JSON失败:', e)
    return JSON.stringify(getDefaultConfig())
  }
}
