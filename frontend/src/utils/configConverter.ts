/**
 * V3 配置工具
 * 页面配置使用 V3 格式，支持灵活的布局架构
 */

import type {
  V3Config,
  NormalizedAreaV3,
  AreaInfo
} from '@/api/page'

// ============ 类型定义 ============

// V3 配置类型定义在 @/api/page 中

// ============ 版本检测 ============

/**
 * 检测配置版本
 * @param config - 配置对象
 * @returns 是否为 V3 配置
 */
export function isV3Config(config: any): config is V3Config {
  return config && config.version === 3 && Array.isArray(config.areas)
}

/**
 * 检测是否为 V4 配置
 * @param config - 配置对象
 * @returns 是否为 V4 配置
 */
export function isV4Config(config: any): boolean {
  return config && config.version === 4 && Array.isArray(config.areas)
}

/**
 * 检查并迁移旧版本配置到 V3
 * @param configStr - 配置字符串或对象
 * @returns V3 配置对象
 */
export function ensureV3Config(config: any): V3Config {
  // 如果已经是 V3，直接返回
  if (isV3Config(config)) {
    return config
  }

  // 如果是空或无效配置，返回默认 V3 配置
  if (!config) {
    return createDefaultV3Config()
  }

  // 尝试从 V1/V2 迁移（简化版：只转换常见的 search 和 content 区域）
  return migrateToV3(config)
}

/**
 * 迁移旧版本配置到 V3
 */
function migrateToV3(oldConfig: any): V3Config {
  const areas: NormalizedAreaV3[] = []

  // 迁移 V1 格式的 searchArea
  if (oldConfig.searchArea) {
    areas.push({
      id: 'search',
      type: 'search',
      name: '查询区',
      enabled: oldConfig.searchArea.enabled ?? true,
      required: false,
      position: 'top',
      role: 'secondary',
      layoutHints: { collapsible: true, collapsed: false },
      config: {
        title: oldConfig.searchArea.title || '查询条件',
        fields: oldConfig.searchArea.fields || []
      }
    })
  }

  // 迁移 V1 格式的 contentArea
  if (oldConfig.contentArea) {
    areas.push({
      id: 'content',
      type: 'content',
      name: '内容区',
      enabled: true,
      required: false,
      position: 'main',
      role: 'primary',
      layoutHints: { flex: 1, scrollable: true },
      config: {
        title: oldConfig.contentArea.title || '数据列表',
        componentType: oldConfig.contentArea.type || 'table',
        configId: oldConfig.contentArea.configId,
        showToolbar: oldConfig.contentArea.showToolbar !== false
      }
    })
  }

  // 如果是 V2 格式（有 areas 数组）
  if (Array.isArray(oldConfig.areas)) {
    for (const area of oldConfig.areas) {
      areas.push({
        ...area,
        position: (area as any).position || getDefaultPositionForType(area.type, area.id).position,
        role: (area as any).role || getDefaultPositionForType(area.type, area.id).role,
        layoutHints: (area as any).layoutHints || getDefaultPositionForType(area.type, area.id).layoutHints
      } as NormalizedAreaV3)
    }
  }

  return {
    version: 3,
    templateCode: oldConfig.templateCode,
    layoutType: oldConfig.layoutType,
    layoutConfig: oldConfig.layoutConfig,
    areas: areas.length > 0 ? areas : createDefaultV3Config().areas
  }
}

// ============ 默认配置 ============

/**
 * 创建默认 V3 配置
 */
export function createDefaultV3Config(): V3Config {
  return {
    version: 3,
    layoutType: 'top-bottom',
    areas: [
      {
        id: 'search',
        type: 'search',
        name: '查询区',
        enabled: true,
        required: false,
        position: 'top',
        role: 'secondary',
        layoutHints: { collapsible: true, collapsed: false },
        config: {
          title: '查询条件',
          fields: []
        }
      },
      {
        id: 'content',
        type: 'content',
        name: '内容区',
        enabled: true,
        required: false,
        position: 'main',
        role: 'primary',
        layoutHints: { flex: 1, scrollable: true },
        config: {
          title: '数据列表',
          componentType: 'table',
          configId: null,
          showToolbar: true
        }
      }
    ]
  }
}

/**
 * 获取区域类型的默认配置
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

// ============ 区域布局工具 ============

/**
 * 获取区域类型和ID的默认位置和布局提示
 */
function getDefaultPositionForType(
  type: string,
  id: string
): Pick<NormalizedAreaV3, 'position' | 'role' | 'layoutHints'> {
  const idLower = id.toLowerCase()

  // 工具栏位置推断
  if (type === 'toolbar') {
    if (idLower.includes('top') || idLower === 'toolbar') {
      return {
        position: 'top',
        role: 'auxiliary',
        layoutHints: { order: 0 }
      }
    }
    if (idLower.includes('content') || idLower.includes('main')) {
      return {
        position: 'main',
        role: 'auxiliary',
        layoutHints: { order: -1 }
      }
    }
  }

  // 查询区默认在顶部
  if (type === 'search' || idLower === 'search') {
    return {
      position: 'top',
      role: 'secondary',
      layoutHints: { collapsible: true, collapsed: false }
    }
  }

  // 树形区默认在左侧
  if (type === 'tree' || idLower.includes('tree')) {
    return {
      position: 'left',
      role: 'secondary',
      layoutHints: { width: '280px', resizable: true }
    }
  }

  // 主内容区
  if (type === 'content' || idLower === 'content' || idLower === 'main') {
    return {
      position: 'main',
      role: 'primary',
      layoutHints: { flex: 1, scrollable: true }
    }
  }

  // 底部面板
  if (idLower.includes('bottom')) {
    return {
      position: 'bottom',
      role: 'secondary',
      layoutHints: { height: '300px', resizable: true }
    }
  }

  // 默认为主内容区
  return {
    position: 'main',
    role: 'primary',
    layoutHints: {}
  }
}

// ============ 区域操作 ============

/**
 * 合并模板区域定义和页面配置
 */
export function mergeTemplateAndPageAreas(
  templateAreas: AreaInfo[],
  pageAreas: Record<string, NormalizedAreaV3>
): Record<string, NormalizedAreaV3> {
  const result: Record<string, NormalizedAreaV3> = {}

  // 先添加模板定义的所有区域
  for (const templateArea of templateAreas) {
    const pageArea = pageAreas[templateArea.id]
    const defaults = getDefaultPositionForType(templateArea.type, templateArea.id)

    result[templateArea.id] = {
      id: templateArea.id,
      type: templateArea.type,
      name: templateArea.name,
      enabled: pageArea?.enabled ?? (templateArea.required === true),
      required: templateArea.required === true,
      position: pageArea?.position || defaults.position,
      role: pageArea?.role || defaults.role,
      layoutHints: pageArea?.layoutHints || defaults.layoutHints,
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
 * V3 配置标准化为对象（以 areaId 为 key）
 */
export function normalizeV3Areas(v3Config: V3Config): Record<string, NormalizedAreaV3> {
  const result: Record<string, NormalizedAreaV3> = {}

  for (const area of v3Config.areas) {
    result[area.id] = { ...area }
  }

  return result
}

/**
 * 将 V3 区域对象转换为数组
 */
export function denormalizeV3Areas(areasObj: Record<string, NormalizedAreaV3>): NormalizedAreaV3[] {
  return Object.values(areasObj).filter(a => a.enabled !== false)
}

// ============ 配置验证 ============

/**
 * 验证 V3 配置完整性
 */
export function validateV3Config(
  config: V3Config,
  templateAreas: AreaInfo[]
): { valid: boolean; errors: string[] } {
  const errors: string[] = []

  // 验证必填区域
  for (const area of templateAreas) {
    if (area.required === true) {
      const configArea = config.areas?.find(a => a.id === area.id)
      if (!configArea || configArea.enabled === false) {
        errors.push(`${area.name} 是必填区域，不能禁用`)
      }
    }
  }

  // 验证启用的内容区配置
  const contentArea = config.areas?.find(a => a.id === 'content' && a.enabled === true)
  if (contentArea) {
    if (contentArea.config?.componentType && !contentArea.config.configId) {
      errors.push('内容区已选择组件类型，请选择对应的表格或表单配置')
    }
  }

  return {
    valid: errors.length === 0,
    errors
  }
}

// ============ 配置序列化 ============

/**
 * 序列化 V3 配置为 JSON 字符串
 */
export function stringifyV3Config(config: V3Config): string {
  try {
    return JSON.stringify(config)
  } catch (e) {
    console.error('序列化 V3 配置失败:', e)
    return JSON.stringify(createDefaultV3Config())
  }
}

/**
 * 解析配置字符串为 V3 配置对象
 * 支持 V4 格式自动转换为 V3
 */
export function parseV3Config(configStr?: string | object): V3Config {
  if (!configStr) {
    return createDefaultV3Config()
  }

  // 如果已经是对象，直接使用
  if (typeof configStr === 'object') {
    const config = ensureV3Config(configStr)
    // 如果是 V4 格式，转换为 V3
    if (isV4Config(configStr)) {
      return convertV4ToV3(configStr)
    }
    return config
  }

  try {
    const config = JSON.parse(configStr as string)
    // 如果是 V4 格式，转换为 V3
    if (isV4Config(config)) {
      return convertV4ToV3(config)
    }
    return ensureV3Config(config)
  } catch (e) {
    console.error('解析配置失败:', e)
    return createDefaultV3Config()
  }
}

/**
 * 将 V4 配置转换为 V3 配置
 */
function convertV4ToV3(v4Config: any): V3Config {
  const areas: NormalizedAreaV3[] = []

  if (v4Config.areas && Array.isArray(v4Config.areas)) {
    for (const area of v4Config.areas) {
      areas.push({
        id: area.id,
        type: area.type,
        name: area.name,
        enabled: area.enabled !== false,
        required: false,
        position: area.position || getDefaultPositionForType(area.type, area.id).position,
        role: area.role || getDefaultPositionForType(area.type, area.id).role,
        layoutHints: area.layoutHints || getDefaultPositionForType(area.type, area.id).layoutHints,
        config: area.config || {}
      })
    }
  }

  return {
    version: 3,
    templateCode: v4Config.templateCode,
    layoutType: v4Config.layoutType || 'top-bottom',
    layoutConfig: v4Config.layoutConfig,
    areas: areas.length > 0 ? areas : createDefaultV3Config().areas
  }
}
