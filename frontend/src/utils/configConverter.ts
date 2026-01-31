/**
 * 配置版本转换工具
 * 支持 v1 (searchArea + contentArea) 和 v2 (areas 数组) 格式之间的相互转换
 */

// ============ 类型定义 ============

export interface V1Config {
  searchArea?: {
    enabled: boolean
    title: string
    fields: any[]
  }
  contentArea?: {
    type: 'table' | 'form'
    configId: number
    title: string
    showToolbar: boolean
  }
}

export interface V2Config {
  version: 2
  templateCode?: string
  layoutType?: string
  areas: NormalizedArea[]
}

export interface NormalizedArea {
  id: string
  type: string // search, content, tree, toolbar, tabs, stats, charts, header, custom
  name: string
  enabled: boolean
  required?: boolean
  config?: Record<string, any>
  props?: Record<string, any>
}

export interface AreaInfo {
  id: string
  type: string
  name: string
  required?: boolean
  description?: string
}

// ============ 版本检测 ============

/**
 * 检测配置版本
 * @param config - 配置对象
 * @returns 版本号 (1 或 2) 或 null
 */
export function detectVersion(config: any): number | null {
  if (!config) {
    return null
  }

  // v2 格式必须有 version 字段且值为 2，且有 areas 数组
  if (config.version === 2 && Array.isArray(config.areas)) {
    return 2
  }

  // v1 格式有 searchArea 或 contentArea 字段
  if (config.searchArea || config.contentArea) {
    return 1
  }

  return null
}

// ============ V1 转 V2 ============

/**
 * 将 v1 格式转换为 v2 格式
 * @param v1Config - v1 配置对象
 * @param templateAreas - 模板区域定义（可选）
 * @returns v2 配置对象
 */
export function v1ToV2(v1Config: V1Config, templateAreas?: AreaInfo[]): V2Config {
  const areas: NormalizedArea[] = []

  // 转换查询区
  if (v1Config.searchArea) {
    areas.push({
      id: 'search',
      type: 'search',
      name: '查询区',
      enabled: v1Config.searchArea.enabled,
      config: {
        title: v1Config.searchArea.title,
        fields: v1Config.searchArea.fields || []
      }
    })
  } else if (templateAreas?.find(a => a.id === 'search')) {
    // 如果模板定义了 search 区域但配置中没有，添加默认配置
    areas.push({
      id: 'search',
      type: 'search',
      name: '查询区',
      enabled: true,
      config: {
        title: '查询条件',
        fields: []
      }
    })
  }

  // 转换内容区
  if (v1Config.contentArea) {
    areas.push({
      id: 'content',
      type: 'content',
      name: '内容区',
      enabled: true,
      required: true,
      config: {
        title: v1Config.contentArea.title,
        componentType: v1Config.contentArea.type,
        configId: v1Config.contentArea.configId,
        showToolbar: v1Config.contentArea.showToolbar
      }
    })
  } else if (templateAreas?.find(a => a.id === 'content')) {
    // 如果模板定义了 content 区域但配置中没有，添加默认配置
    areas.push({
      id: 'content',
      type: 'content',
      name: '内容区',
      enabled: true,
      required: true,
      config: {
        title: '数据列表',
        componentType: 'table',
        configId: null,
        showToolbar: true
      }
    })
  }

  // 添加模板定义的其他区域（如果未在配置中）
  if (templateAreas) {
    const configuredIds = new Set(areas.map(a => a.id))

    for (const templateArea of templateAreas) {
      if (!configuredIds.has(templateArea.id)) {
        areas.push({
          id: templateArea.id,
          type: templateArea.type,
          name: templateArea.name,
          enabled: false,
          required: templateArea.required,
          config: getDefaultConfigForArea(templateArea.type)
        })
      }
    }
  }

  return {
    version: 2,
    areas
  }
}

// ============ V2 转 V1 ============

/**
 * 将 v2 格式转换为 v1 格式（向后兼容）
 * @param v2Config - v2 配置对象
 * @returns v1 配置对象
 */
export function v2ToV1(v2Config: V2Config): V1Config {
  const v1Config: V1Config = {}

  // 查找 search 和 content 区域
  const searchArea = v2Config.areas?.find(a => a.id === 'search' && a.enabled)
  const contentArea = v2Config.areas?.find(a => a.id === 'content' && a.enabled)

  // 转换查询区
  if (searchArea?.config) {
    v1Config.searchArea = {
      enabled: searchArea.enabled,
      title: searchArea.config.title || '查询条件',
      fields: searchArea.config.fields || []
    }
  }

  // 转换内容区
  if (contentArea?.config) {
    v1Config.contentArea = {
      type: contentArea.config.componentType || 'table',
      configId: contentArea.config.configId,
      title: contentArea.config.title || '数据列表',
      showToolbar: contentArea.config.showToolbar !== false
    }
  }

  return v1Config
}

// ============ 标准化区域配置 ============

/**
 * 标准化区域配置为对象（以 areaId 为 key）
 * @param areas - 区域数组
 * @returns 标准化后的区域对象
 */
export function normalizeAreas(areas: NormalizedArea[]): Record<string, NormalizedArea> {
  const result: Record<string, NormalizedArea> = {}

  for (const area of areas) {
    result[area.id] = { ...area }
  }

  return result
}

/**
 * 将区域对象转换为数组
 * @param areasObj - 区域对象
 * @returns 区域数组
 */
export function denormalizeAreas(areasObj: Record<string, NormalizedArea>): NormalizedArea[] {
  return Object.values(areasObj).filter(a => a.enabled !== false)
}

// ============ 工具函数 ============

/**
 * 获取区域类型的默认配置
 * @param areaType - 区域类型
 * @returns 默认配置
 */
function getDefaultConfigForArea(areaType: string): Record<string, any> {
  const defaults: Record<string, any> = {
    search: {
      title: '查询条件',
      fields: [],
      collapsible: true
    },
    content: {
      title: '数据列表',
      componentType: 'table',
      configId: null,
      showToolbar: true
    },
    tree: {
      title: '分类树',
      dataUrl: '',
      labelField: 'name',
      childrenField: 'children',
      idField: 'id'
    },
    toolbar: {
      buttons: [],
      align: 'left'
    },
    tabs: {
      tabs: [],
      defaultTab: 0,
      position: 'top'
    },
    stats: {
      cards: []
    },
    charts: {
      charts: []
    },
    header: {
      title: '',
      showBreadcrumb: true,
      showActions: false
    },
    custom: {
      allowedComponents: [],
      layout: 'vertical'
    }
  }

  return defaults[areaType] || {}
}

/**
 * 合并模板区域定义和页面配置
 * @param templateAreas - 模板定义的区域
 * @param pageAreas - 页面配置的区域
 * @returns 合并后的完整区域配置
 */
export function mergeTemplateAndPageAreas(
  templateAreas: AreaInfo[],
  pageAreas: Record<string, NormalizedArea>
): Record<string, NormalizedArea> {
  const result: Record<string, NormalizedArea> = {}

  // 先添加模板定义的所有区域
  for (const templateArea of templateAreas) {
    const pageArea = pageAreas[templateArea.id]

    result[templateArea.id] = {
      id: templateArea.id,
      type: templateArea.type,
      name: templateArea.name,
      enabled: pageArea?.enabled ?? templateArea.required ?? true,
      required: templateArea.required,
      config: pageArea?.config || getDefaultConfigForArea(templateArea.type),
      props: pageArea?.props
    }
  }

  // 再添加页面配置中额外定义的区域（不在模板中的）
  for (const [id, pageArea] of Object.entries(pageAreas)) {
    if (!result[id]) {
      result[id] = { ...pageArea }
    }
  }

  return result
}

/**
 * 验证配置完整性
 * @param config - v2 配置对象
 * @param templateAreas - 模板区域定义
 * @returns 验证结果和错误消息
 */
export function validateConfig(
  config: V2Config,
  templateAreas: AreaInfo[]
): { valid: boolean; errors: string[] } {
  const errors: string[] = []

  // 验证必填区域
  for (const area of templateAreas) {
    if (area.required) {
      const configArea = config.areas?.find(a => a.id === area.id)
      if (!configArea || !configArea.enabled) {
        errors.push(`${area.name} 是必填区域，不能禁用`)
      }
    }
  }

  // 验证内容区配置
  const contentArea = config.areas?.find(a => a.id === 'content' && a.enabled)
  if (contentArea && contentArea.config) {
    if (!contentArea.config.componentType) {
      errors.push('内容区必须指定组件类型（table 或 form）')
    }
    if (!contentArea.config.configId) {
      errors.push('内容区必须选择表格或表单配置')
    }
  }

  return {
    valid: errors.length === 0,
    errors
  }
}
