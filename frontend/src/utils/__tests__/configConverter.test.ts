/**
 * V3 配置转换测试
 * 测试 V1/V2/V3 配置之间的相互转换
 */

import { describe, it, expect } from 'vitest'
import {
  detectVersion,
  v1ToV2,
  v2ToV1,
  v2ToV3,
  v3ToV2,
  v1ToV3,
  v3ToV1
} from '@/utils/configConverter'
import type { V1Config, V2Config, V3Config } from '@/api/page'

describe('V3 配置转换测试', () => {
  // V1 示例配置
  const v1Config: V1Config = {
    searchArea: {
      enabled: true,
      title: '查询条件',
      fields: [
        { id: 'f1', label: '姓名', fieldCode: 'name', type: 'input' },
        { id: 'f2', label: '年龄', fieldCode: 'age', type: 'number' }
      ]
    },
    contentArea: {
      type: 'table',
      configId: 1,
      title: '用户列表',
      showToolbar: true
    }
  }

  // V2 示例配置
  const v2Config: V2Config = {
    version: 2,
    templateCode: 'top-bottom',
    layoutType: 'top-bottom',
    areas: [
      {
        id: 'search',
        type: 'search',
        name: '查询区',
        enabled: true,
        required: false,
        config: {
          title: '查询条件',
          fields: [
            { id: 'f1', label: '姓名', fieldCode: 'name', type: 'input' },
            { id: 'f2', label: '年龄', fieldCode: 'age', type: 'number' }
          ]
        }
      },
      {
        id: 'content',
        type: 'content',
        name: '内容区',
        enabled: true,
        required: true,
        config: {
          title: '用户列表',
          componentType: 'table',
          configId: 1,
          showToolbar: true
        }
      }
    ]
  }

  // V3 示例配置
  const v3Config: V3Config = {
    version: 3,
    templateCode: 'top-bottom',
    layoutType: 'top-bottom',
    layoutConfig: {
      gap: '16px',
      padding: '0'
    },
    areas: [
      {
        id: 'search',
        type: 'search',
        name: '查询区',
        enabled: true,
        required: false,
        position: 'top',
        role: 'secondary',
        layoutHints: {
          collapsible: true,
          collapsed: false
        },
        config: {
          title: '查询条件',
          fields: [
            { id: 'f1', label: '姓名', fieldCode: 'name', type: 'input' },
            { id: 'f2', label: '年龄', fieldCode: 'age', type: 'number' }
          ]
        }
      },
      {
        id: 'content',
        type: 'content',
        name: '内容区',
        enabled: true,
        required: true,
        position: 'main',
        role: 'primary',
        layoutHints: {
          flex: 1,
          scrollable: true
        },
        config: {
          title: '用户列表',
          componentType: 'table',
          configId: 1,
          showToolbar: true
        }
      }
    ]
  }

  describe('版本检测', () => {
    it('应该正确检测 V1 配置', () => {
      expect(detectVersion(v1Config)).toBe(1)
    })

    it('应该正确检测 V2 配置', () => {
      expect(detectVersion(v2Config)).toBe(2)
    })

    it('应该正确检测 V3 配置', () => {
      expect(detectVersion(v3Config)).toBe(3)
    })

    it('应该对未知配置返回 null', () => {
      expect(detectVersion({})).toBeNull()
      expect(detectVersion(null)).toBeNull()
      expect(detectVersion(undefined)).toBeNull()
    })
  })

  describe('V1 <-> V2 转换', () => {
    it('应该将 V1 转换为 V2', () => {
      const result = v1ToV2(v1Config)
      expect(result.version).toBe(2)
      expect(result.areas).toHaveLength(2)
      expect(result.areas[0].id).toBe('search')
      expect(result.areas[1].id).toBe('content')
    })

    it('应该将 V2 转换为 V1', () => {
      const result = v2ToV1(v2Config)
      expect(result.searchArea).toBeDefined()
      expect(result.contentArea).toBeDefined()
      expect(result.searchArea?.enabled).toBe(true)
      expect(result.contentArea?.type).toBe('table')
    })

    it('V1 -> V2 -> V1 应该保持数据一致性', () => {
      const v2 = v1ToV2(v1Config)
      const restoredV1 = v2ToV1(v2)

      expect(restoredV1.searchArea?.title).toBe(v1Config.searchArea?.title)
      expect(restoredV1.searchArea?.fields).toHaveLength(v1Config.searchArea?.fields?.length || 0)
      expect(restoredV1.contentArea?.type).toBe(v1Config.contentArea?.type)
      expect(restoredV1.contentArea?.configId).toBe(v1Config.contentArea?.configId)
    })
  })

  describe('V2 <-> V3 转换', () => {
    it('应该将 V2 转换为 V3', () => {
      const result = v2ToV3(v2Config)
      expect(result.version).toBe(3)
      expect(result.areas).toHaveLength(2)
      expect(result.areas[0].position).toBeDefined()
      expect(result.areas[0].role).toBeDefined()
      expect(result.areas[0].layoutHints).toBeDefined()
    })

    it('应该将 V3 转换为 V2', () => {
      const result = v3ToV2(v3Config)
      expect(result.version).toBe(2)
      expect(result.areas).toHaveLength(2)
      expect(result.areas[0].position).toBeUndefined()
      expect(result.areas[0].role).toBeUndefined()
      expect(result.areas[0].layoutHints).toBeUndefined()
    })

    it('V2 -> V3 -> V2 应该保持数据一致性', () => {
      const v3 = v2ToV3(v2Config)
      const restoredV2 = v3ToV2(v3)

      expect(restoredV2.version).toBe(v2Config.version)
      expect(restoredV2.areas).toHaveLength(v2Config.areas.length)
      expect(restoredV2.areas[0].id).toBe(v2Config.areas[0].id)
      expect(restoredV2.areas[0].config).toEqual(v2Config.areas[0].config)
    })

    it('V3 转换为 V2 应该去除 V3 专用字段', () => {
      const result = v3ToV2(v3Config)
      const area = result.areas[0]

      expect(area.position).toBeUndefined()
      expect(area.role).toBeUndefined()
      expect(area.layoutHints).toBeUndefined()
      // 保留原有字段
      expect(area.id).toBeDefined()
      expect(area.type).toBeDefined()
      expect(area.config).toBeDefined()
    })
  })

  describe('V1 <-> V3 转换', () => {
    it('应该将 V1 转换为 V3', () => {
      const result = v1ToV3(v1Config)
      expect(result.version).toBe(3)
      expect(result.areas).toHaveLength(2)
      expect(result.areas[0].position).toBe('top')
      expect(result.areas[1].position).toBe('main')
    })

    it('应该将 V3 转换为 V1', () => {
      const result = v3ToV1(v3Config)
      expect(result.searchArea).toBeDefined()
      expect(result.contentArea).toBeDefined()
    })

    it('V1 -> V3 -> V1 应该保持数据一致性', () => {
      const v3 = v1ToV3(v1Config)
      const restoredV1 = v3ToV1(v3)

      expect(restoredV1.searchArea?.title).toBe(v1Config.searchArea?.title)
      expect(restoredV1.contentArea?.configId).toBe(v1Config.contentArea?.configId)
    })
  })

  describe('V3 默认位置和布局提示', () => {
    it('应该为 search 区域设置默认位置 top', () => {
      const v2 = {
        version: 2,
        areas: [
          { id: 'search', type: 'search', name: '查询', enabled: true, required: false }
        ]
      } as V2Config

      const v3 = v2ToV3(v2)
      expect(v3.areas[0].position).toBe('top')
    })

    it('应该为 content 区域设置默认位置 main', () => {
      const v2 = {
        version: 2,
        areas: [
          { id: 'content', type: 'content', name: '内容', enabled: true, required: true }
        ]
      } as V2Config

      const v3 = v2ToV3(v2)
      expect(v3.areas[0].position).toBe('main')
    })

    it('应该为 tree 区域设置默认位置 left', () => {
      const v2 = {
        version: 2,
        areas: [
          { id: 'tree', type: 'tree', name: '树', enabled: true, required: false }
        ]
      } as V2Config

      const v3 = v2ToV3(v2)
      expect(v3.areas[0].position).toBe('left')
    })

    it('应该为 toolbar-top 设置位置 top', () => {
      const v2 = {
        version: 2,
        areas: [
          { id: 'toolbar-top', type: 'toolbar', name: '工具栏', enabled: true, required: false }
        ]
      } as V2Config

      const v3 = v2ToV3(v2)
      expect(v3.areas[0].position).toBe('top')
    })

    it('应该为 toolbar-content 设置位置 main', () => {
      const v2 = {
        version: 2,
        areas: [
          { id: 'toolbar-content', type: 'toolbar', name: '工具栏', enabled: true, required: false }
        ]
      } as V2Config

      const v3 = v2ToV3(v2)
      expect(v3.areas[0].position).toBe('main')
    })
  })

  describe('多工具栏布局测试', () => {
    it('应该支持多个工具栏区域', () => {
      const v3MultiToolbar: V3Config = {
        version: 3,
        layoutType: 'top-bottom',
        areas: [
          {
            id: 'toolbar-top',
            type: 'toolbar',
            name: '顶部工具栏',
            enabled: true,
            position: 'top',
            role: 'auxiliary',
            layoutHints: { order: 0 },
            config: { buttons: [] }
          },
          {
            id: 'search',
            type: 'search',
            name: '查询区',
            enabled: true,
            position: 'top',
            role: 'secondary',
            config: { fields: [] }
          },
          {
            id: 'toolbar-content',
            type: 'toolbar',
            name: '表格工具栏',
            enabled: true,
            position: 'main',
            role: 'auxiliary',
            layoutHints: { order: -1 },
            config: { buttons: [] }
          },
          {
            id: 'content-main',
            type: 'content',
            name: '主内容区',
            enabled: true,
            position: 'main',
            role: 'primary',
            layoutHints: { flex: 1 },
            config: { componentType: 'table', configId: null }
          }
        ]
      }

      // 转换为 V2 应该保留所有区域
      const v2 = v3ToV2(v3MultiToolbar)
      expect(v2.areas).toHaveLength(4)

      // 转换回 V3 应该保持工具栏位置
      const restoredV3 = v2ToV3(v2)
      const topToolbars = restoredV3.areas.filter(a => a.position === 'top' && a.type === 'toolbar')
      const mainToolbars = restoredV3.areas.filter(a => a.position === 'main' && a.type === 'toolbar')

      expect(topToolbars).toHaveLength(1)
      expect(mainToolbars).toHaveLength(1)
    })
  })

  describe('主内容+底部面板布局测试', () => {
    it('应该支持底部面板区域', () => {
      const v3MainBottom: V3Config = {
        version: 3,
        layoutType: 'top-bottom',
        areas: [
          {
            id: 'content-main',
            type: 'content',
            name: '主内容区',
            enabled: true,
            position: 'main',
            role: 'primary',
            layoutHints: { flex: 1 },
            config: { componentType: 'table', configId: null }
          },
          {
            id: 'content-bottom',
            type: 'content',
            name: '底部面板',
            enabled: true,
            position: 'bottom',
            role: 'secondary',
            layoutHints: { height: '300px', resizable: true },
            config: { componentType: 'form', configId: null }
          }
        ]
      }

      const v2 = v3ToV2(v3MainBottom)
      expect(v2.areas).toHaveLength(2)

      const restoredV3 = v2ToV3(v2)
      const bottomAreas = restoredV3.areas.filter(a => a.position === 'bottom')

      expect(bottomAreas).toHaveLength(1)
      expect(bottomAreas[0].layoutHints?.height).toBe('300px')
      expect(bottomAreas[0].layoutHints?.resizable).toBe(true)
    })
  })
})
