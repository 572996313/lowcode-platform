import SimpleFormTemplate from './SimpleFormTemplate.vue'
import TabFormTemplate from './TabFormTemplate.vue'
import CardFormTemplate from './CardFormTemplate.vue'

export interface FormTemplateMeta {
  component: any
  templateCode: string
  templateName: string
  description: string
  previewImage?: string
  fieldSlots: Array<{
    id: string
    label: string
    minFields: number
    maxFields: number
  }>
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