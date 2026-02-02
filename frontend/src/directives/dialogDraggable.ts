import type { Directive } from 'vue'

/**
 * 弹窗拖拽指令
 *
 * 使用方式：
 * <el-dialog v-dialog-draggable v-model="dialogVisible">
 *   <!-- 内容 -->
 * </el-dialog>
 *
 * 支持参数：
 * @param {boolean} value - 是否启用拖拽，默认 true
 */
const dialogDraggable: Directive<HTMLElement, boolean | undefined> = {
  mounted(el, binding) {
    // 获取是否启用拖拽
    const enabled = binding.value !== false

    if (!enabled) {
      return
    }

    // 获取弹窗的 header 元素
    const header = el.querySelector('.el-dialog__header') as HTMLElement
    if (!header) {
      return
    }

    // 设置 header 样式，表示可拖拽
    header.style.cursor = 'move'
    header.style.userSelect = 'none'

    // 保存原始样式
    const originalPosition = el.style.position

    let startX = 0
    let startY = 0
    let originalLeft = 0
    let originalTop = 0
    let isDragging = false

    header.addEventListener('mousedown', (e: MouseEvent) => {
      // 忽略右键和中间键
      if (e.button !== 0) {
        return
      }

      startX = e.clientX
      startY = e.clientY

      // 获取当前弹窗的位置
      const rect = el.getBoundingClientRect()
      originalLeft = rect.left
      originalTop = rect.top

      isDragging = true

      // 添加全局事件监听
      document.addEventListener('mousemove', onMouseMove)
      document.addEventListener('mouseup', onMouseUp)
    })

    function onMouseMove(e: MouseEvent) {
      if (!isDragging) {
        return
      }

      e.preventDefault()

      // 计算偏移量
      const dx = e.clientX - startX
      const dy = e.clientY - startY

      // 计算新位置
      let newLeft = originalLeft + dx
      let newTop = originalTop + dy

      // 边界检查
      const rect = el.getBoundingClientRect()
      const windowWidth = window.innerWidth
      const windowHeight = window.innerHeight

      // 限制在视口内
      newLeft = Math.max(0, Math.min(newLeft, windowWidth - rect.width))
      newTop = Math.max(0, Math.min(newTop, windowHeight - rect.height))

      // 应用新位置
      el.style.marginLeft = '0'
      el.style.marginTop = '0'
      el.style.left = `${newLeft}px`
      el.style.top = `${newTop}px`
    }

    function onMouseUp() {
      isDragging = false
      document.removeEventListener('mousemove', onMouseMove)
      document.removeEventListener('mouseup', onMouseUp)
    }
  }
}

export default dialogDraggable
