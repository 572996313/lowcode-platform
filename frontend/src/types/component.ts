/**
 * 组件相关类型定义
 */

import type { FormConfig, TableConfig, ComponentRef } from './template'

/**
 * 拖拽状态
 */
export interface DragState {
  isDragging: boolean
  draggedComponent: ComponentRef | null
  targetArea: string | null
}

/**
 * 区域配置
 */
export interface AreaConfig {
  id: string
  title: string
  required?: boolean
  acceptTypes: string[]
  maxComponents?: number
  defaultComponents?: ComponentRef[]
}

/**
 * 组件属性
 */
export interface ComponentProps {
  [key: string]: any
}

/**
 * 设计区域事件
 */
export interface DesignAreaEvents {
  'update:components': (components: ComponentRef[]) => void
  select?: (component: ComponentRef) => void
}
