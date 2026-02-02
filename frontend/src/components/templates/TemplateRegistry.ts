import SimpleFormTemplate from './SimpleFormTemplate.vue'
import TabFormTemplate from './TabFormTemplate.vue'
import CardFormTemplate from './CardFormTemplate.vue'

// 表单槽位元数据
export interface FormSlotMeta {
  id: string        // slot ID，用于 v-slot:name
  label: string     // slot 显示名称
  minFields?: number
  maxFields?: number
}

export interface FormTemplateMeta {
  component: any
  templateCode: string
  templateName: string
  description: string
  previewImage?: string
  fieldSlots: FormSlotMeta[]
}

export const formTemplateRegistry: Record<string, FormTemplateMeta> = {
  simple: {
    component: SimpleFormTemplate,
    templateCode: 'simple',
    templateName: '简单表单模板',
    description: '适用于基础信息录入，包含标题和分割线',
    fieldSlots: [
      { id: 'basic', label: '基础信息区域', minFields: 0, maxFields: 10 },
      { id: 'extend', label: '扩展信息区域', minFields: 0, maxFields: 20 }
    ]
  },
  tab: {
    component: TabFormTemplate,
    templateCode: 'tab',
    templateName: '页签表单模板',
    description: '适用于多分类信息录入，使用页签分组',
    fieldSlots: [
      { id: 'tab-basic', label: '基础信息', minFields: 0, maxFields: 10 },
      { id: 'tab-detail', label: '详细信息', minFields: 0, maxFields: 10 },
      { id: 'tab-other', label: '其他信息', minFields: 0, maxFields: 10 }
    ]
  },
  card: {
    component: CardFormTemplate,
    templateCode: 'card',
    templateName: '卡片表单模板',
    description: '适用于分组信息录入，使用卡片容器',
    fieldSlots: [
      { id: 'card-basic', label: '基础信息', minFields: 0, maxFields: 10 },
      { id: 'card-detail', label: '详细信息', minFields: 0, maxFields: 10 }
    ]
  }
}

// 获取所有模板
export const getAllTemplates = () => Object.values(formTemplateRegistry)

// 根据 templateCode 获取模板
export const getTemplateByCode = (code: string) => formTemplateRegistry[code]

/**
 * 获取模板的槽位定义
 * @param templateCode 模板编码
 * @returns 槽位数组，如果模板不存在则返回空数组
 */
export function getTemplateSlots(templateCode: string): FormSlotMeta[] {
  const template = formTemplateRegistry[templateCode]
  return template?.fieldSlots || []
}

/**
 * 获取模板元数据（不包含组件）
 * @param templateCode 模板编码
 * @returns 模板元数据，如果模板不存在则返回 null
 */
export function getTemplateMeta(templateCode: string): FormTemplateMeta | null {
  const template = formTemplateRegistry[templateCode]
  if (!template) return null
  // 返回不包含 component 的元数据
  const { component, ...meta } = template
  return meta
}