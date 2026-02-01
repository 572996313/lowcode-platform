/**
 * 模板相关类型定义
 */

/**
 * 模板元数据
 */
export interface TemplateMeta {
  id: string                       // 模板唯一标识
  name: string                     // 模板名称
  type: 'list' | 'form' | 'tree' | 'custom'  // 模板类型
  category: string                 // 分类（如：常用、高级）
  description: string              // 描述
  previewImage?: string            // 预览图

  // 动态导入函数
  designer: () => Promise<{ default: any }>  // 设计器组件
  renderer: () => Promise<{ default: any }>  // 渲染器组件
}

/**
 * 页面配置数据（v5格式）
 */
export interface PageConfigData {
  version: 'v5'                    // 版本标识
  templateId: string               // 模板ID
  templateName?: string            // 模板名称（用于显示）

  // 区域组件映射
  components: {
    [areaId: string]: ComponentRef[]
  }

  // 全局配置
  globalConfig?: {
    theme?: string
    [key: string]: any
  }
}

/**
 * 组件引用
 */
export interface ComponentRef {
  id: string                       // 组件实例ID（唯一）
  type: 'form' | 'table' | 'button' | 'tree'  // 组件类型
  refId: number                    // 引用的配置ID（formId/tableId）
  name: string                     // 显示名称
  props?: Record<string, any>      // 覆盖的属性
  style?: Record<string, string>   // 自定义样式
}

/**
 * 按钮配置
 */
export interface ButtonConfig {
  id?: number
  buttonName: string
  buttonCode: string
  buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'text'
  icon?: string
  [key: string]: any
}

/**
 * 组件库数据
 */
export interface ComponentLibrary {
  forms: FormConfig[]
  tables: TableConfig[]
  buttons: ButtonConfig[]
}

/**
 * 表单配置
 */
export interface FormConfig {
  id?: number
  formName: string
  formCode: string
  [key: string]: any
}

/**
 * 表格配置
 */
export interface TableConfig {
  id?: number
  tableName: string
  tableCode: string
  [key: string]: any
}
