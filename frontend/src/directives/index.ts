import type { App } from 'vue'
import dialogDraggable from './dialogDraggable'
import dialogResizable, { type ResizeOptions } from './dialogResizable'

/**
 * 注册所有自定义指令
 */
export function setupDirectives(app: App) {
  // 注册弹窗拖拽指令
  app.directive('dialog-draggable', dialogDraggable)

  // 注册弹窗调整大小指令
  app.directive('dialog-resizable', dialogResizable)
}

// 导出类型供外部使用
export type { ResizeOptions }
