import { request } from '@/utils/request'
import type { FormFieldConfig, ToolbarButton, OptionItem } from './table-standard'
import { registerConfig } from '@/utils/configRegistry'
import {
  createTableStandard,
  updateTableStandard,
  getTableStandardPage
} from './table-standard'

// ==========================================
// 类型定义 - 表单页面配置接口
// ==========================================

/** 表单页面模式 */
export type FormPageMode = 'add' | 'edit' | 'view'

/** 增强的表单字段类型（扩展自 ConfigForm 的 FormFieldType） */
export type FormPageFieldType = 'input' | 'textarea' | 'number' | 'select'
  | 'radio' | 'checkbox'
  | 'switch' | 'date' | 'datetime'
  | 'upload'

/** 文件上传配置 */
export interface UploadConfig {
  /** 接受的文件类型，如 '.jpg,.png,.pdf' */
  accept?: string
  /** 最大文件数量 */
  limit?: number
  /** 最大文件大小（MB） */
  maxSize?: number
  /** 是否允许多选 */
  multiple?: boolean
  /** 文件列表类型 */
  listType?: 'text' | 'picture' | 'picture-card'
}

/** 增强的表单字段配置（扩展自 FormFieldConfig） */
export interface FormPageFieldConfig extends FormFieldConfig {
  /** 栅格占比（1-24） */
  span?: number
  /** 数字最小值 */
  min?: number
  /** 数字最大值 */
  max?: number
  /** 数字步长 */
  step?: number
  /** 上传配置 */
  uploadConfig?: UploadConfig
  /** 默认值 */
  defaultValue?: any
  /** 字段提示文本 */
  tooltip?: string
}

/** 表单分组配置 */
export interface FormGroupConfig {
  /** 分组标题 */
  title?: string
  /** 分组描述 */
  description?: string
  /** 是否可折叠 */
  collapsible?: boolean
  /** 默认折叠状态 */
  defaultCollapsed?: boolean
  /** 分组内的字段 */
  fields: FormPageFieldConfig[]
}

/** 表单布局配置 */
export interface FormLayoutConfig {
  /** 列数（1-4） */
  columns?: number
  /** 标签宽度 */
  labelWidth?: string
  /** 标签位置 */
  labelPosition?: 'left' | 'right' | 'top'
  /** 组件尺寸 */
  size?: 'large' | 'default' | 'small'
  /** 行间距 */
  rowGutter?: number
}

/** 表单工具栏按钮（扩展 showInModes） */
export interface FormToolbarButton extends ToolbarButton {
  /** 仅在指定模式显示 */
  showInModes?: FormPageMode[]
}

/** 表单页面配置（后端返回） */
export interface FormPageConfigResponse {
  /** 页面编码 */
  pageCode: string
  /** 页面名称 */
  pageName: string
  /** 表单布局 */
  layout: FormLayoutConfig
  /** 工具栏配置 */
  toolbar: {
    buttons: FormToolbarButton[]
  }
  /** 表单分组 */
  groups: FormGroupConfig[]
}

// ==========================================
// API 接口
// ==========================================

/**
 * 获取表单页面配置
 * 后端接口：GET /api/form-standard/config
 */
export const getFormStandardConfig = (): Promise<FormPageConfigResponse> => {
  // TODO: 替换为真实后端接口
  // return request.get<FormPageConfigResponse>('/form-standard/config')
  return Promise.resolve(mockFormPageConfig)
}

/**
 * 获取表单数据（编辑/查看模式）
 * 后端接口：GET /api/form-standard/data/{id}
 * Mock: 从 table-standard 的 mockDataList 中查找
 */
export const getFormStandardData = async (id: number | string): Promise<Record<string, any>> => {
  // TODO: 替换为真实后端接口
  // return request.get<Record<string, any>>(`/form-standard/data/${id}`)
  const result = await getTableStandardPage({ current: 1, size: 1000 })
  const record = result.records.find((r: any) => String(r.id) === String(id))
  return record || { ...mockExistingRecord, id: Number(id) }
}

/**
 * 新增数据
 * 后端接口：POST /api/form-standard
 * Mock: 委托给 table-standard 的 createTableStandard，确保数据互通
 */
export const createFormStandard = (data: Record<string, any>): Promise<number> => {
  // TODO: 替换为真实后端接口
  // return request.post<number>('/form-standard', data)
  return createTableStandard(data)
}

/**
 * 更新数据
 * 后端接口：PUT /api/form-standard/{id}
 * Mock: 委托给 table-standard 的 updateTableStandard，确保数据互通
 */
export const updateFormStandard = (id: number | string, data: Record<string, any>): Promise<void> => {
  // TODO: 替换为真实后端接口
  // return request.put(`/form-standard/${id}`, data)
  return updateTableStandard(Number(id), data)
}

// ==========================================
// Mock 数据
// ==========================================

const mockFormPageConfig: FormPageConfigResponse = {
  pageCode: 'form_standard_demo',
  pageName: '表单标准页面',
  layout: {
    columns: 2,
    labelWidth: '120px',
    labelPosition: 'right',
    size: 'default',
    rowGutter: 20
  },
  toolbar: {
    buttons: [
      { label: '保存', btnType: 'primary', icon: 'Check', action: 'save', showInModes: ['add', 'edit'] },
      { label: '提交', btnType: 'success', icon: 'Position', action: 'submit', showInModes: ['add', 'edit'] },
      { label: '编辑', btnType: 'warning', icon: 'Edit', action: 'edit', showInModes: ['view'] },
      { label: '返回', icon: 'Back', action: 'back' }
    ]
  },
  groups: [
    {
      title: '基本信息',
      collapsible: false,
      fields: [
        { field: 'name', label: '名称', type: 'input', required: true, span: 24, placeholder: '请输入名称', rules: [{ required: true, message: '请输入名称', trigger: 'blur' }, { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }] },
        { field: 'code', label: '编码', type: 'input', required: true, disabledOnEdit: true, placeholder: '请输入编码', rules: [{ required: true, message: '请输入编码', trigger: 'blur' }, { pattern: '^[a-zA-Z0-9_]+$', message: '只能包含字母、数字和下划线', trigger: 'blur' }] },
        {
          field: 'category', label: '分类', type: 'select', required: true, placeholder: '请选择分类',
          rules: [{ required: true, message: '请选择分类', trigger: 'change' }],
          options: [
            { label: '分类A', value: 'A' },
            { label: '分类B', value: 'B' },
            { label: '分类C', value: 'C' }
          ]
        },
        {
          field: 'priority', label: '优先级', type: 'radio', defaultValue: 2,
          options: [
            { label: '低', value: 1 },
            { label: '中', value: 2 },
            { label: '高', value: 3 }
          ]
        },
        { field: 'status', label: '状态', type: 'switch', activeValue: 1, inactiveValue: 0 },
        { field: 'publishDate', label: '发布日期', type: 'date', placeholder: '请选择日期' }
      ]
    },
    {
      title: '详细信息',
      description: '填写更多详细信息',
      collapsible: true,
      defaultCollapsed: false,
      fields: [
        { field: 'description', label: '描述', type: 'textarea', rows: 4, span: 24, placeholder: '请输入描述' },
        { field: 'remark', label: '备注', type: 'textarea', rows: 2, span: 24, placeholder: '请输入备注' },
        { field: 'amount', label: '金额', type: 'number', min: 0, max: 999999, step: 0.01, placeholder: '请输入金额' },
        { field: 'deadline', label: '截止时间', type: 'datetime', placeholder: '请选择时间' },
        {
          field: 'tags', label: '标签', type: 'checkbox',
          options: [
            { label: '标签1', value: 'tag1' },
            { label: '标签2', value: 'tag2' },
            { label: '标签3', value: 'tag3' }
          ]
        },
        {
          field: 'attachments', label: '附件', type: 'upload', span: 24,
          uploadConfig: {
            accept: '.jpg,.png,.pdf,.doc,.docx',
            limit: 5,
            maxSize: 10,
            listType: 'text',
            multiple: true
          }
        }
      ]
    }
  ]
}

const mockExistingRecord: Record<string, any> = {
  name: '示例项目名称',
  code: 'PROJECT_001',
  category: 'A',
  priority: 2,
  status: 1,
  publishDate: '2026-03-15',
  description: '这是一个示例项目的详细描述信息...',
  remark: '备注内容',
  amount: 15000.50,
  deadline: '2026-06-30 18:00:00',
  tags: ['tag1', 'tag3'],
  attachments: []
}

// ==========================================
// 初始化：自动注册 mock 表单配置到 configRegistry
// 这样表格页面点击"新增/编辑"时能找到这个表单
// 将来接后端后，这里替换为后端 API 返回的配置
// ==========================================
registerConfig('form_standard_demo', '表单标准页面', 'form', mockFormPageConfig)
