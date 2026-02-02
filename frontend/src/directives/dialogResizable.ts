import type { Directive } from 'vue'

/**
 * 弹窗调整大小指令
 *
 * 使用方式：
 * <el-dialog v-dialog-resizable v-model="dialogVisible">
 *   <!-- 内容 -->
 * </el-dialog>
 *
 * 配置项：
 * @param {boolean|ResizeOptions} value - 是否启用调整或配置选项
 */
export interface ResizeOptions {
  minWidth?: string
  maxWidth?: string
  minHeight?: string
  maxHeight?: string
  enabled?: boolean
}

const dialogResizable: Directive<HTMLElement, boolean | ResizeOptions | undefined> = {
  mounted(el, binding) {
    const options: Required<ResizeOptions> = {
      enabled: true,
      minWidth: '300px',
      maxWidth: '90vw',
      minHeight: '200px',
      maxHeight: '90vh',
      ...(typeof binding.value === 'object' ? binding.value : {})
    }

    // 检查是否启用
    if (options.enabled === false) {
      return
    }

    // 获取对话框 wrapper 和内容
    const dialogEl = el.querySelector('.el-dialog') as HTMLElement
    if (!dialogEl) {
      return
    }

    // 创建调整手柄
    const handles = ['n', 'e', 's', 'w', 'ne', 'se', 'sw', 'nw']
    const handleElements: HTMLElement[] = []

    handles.forEach(direction => {
      const handle = document.createElement('div')
      handle.className = `dialog-resize-handle resize-${direction}`
      handle.dataset.direction = direction

      // 设置手柄样式
      const style = handle.style
      style.position = 'absolute'
      style.zIndex = '1'
      style.backgroundColor = 'transparent'

      // 根据方向设置位置和尺寸
      switch (direction) {
        case 'n':
          style.top = '0'
          style.left = '10px'
          style.right = '10px'
          style.height = '6px'
          style.cursor = 'ns-resize'
          break
        case 's':
          style.bottom = '0'
          style.left = '10px'
          style.right = '10px'
          style.height = '6px'
          style.cursor = 'ns-resize'
          break
        case 'e':
          style.top = '10px'
          style.right = '0'
          style.bottom = '10px'
          style.width = '6px'
          style.cursor = 'ew-resize'
          break
        case 'w':
          style.top = '10px'
          style.left = '0'
          style.bottom = '10px'
          style.width = '6px'
          style.cursor = 'ew-resize'
          break
        case 'ne':
          style.top = '0'
          style.right = '0'
          style.width = '10px'
          style.height = '10px'
          style.cursor = 'nesw-resize'
          break
        case 'se':
          style.bottom = '0'
          style.right = '0'
          style.width = '10px'
          style.height = '10px'
          style.cursor = 'nwse-resize'
          break
        case 'sw':
          style.bottom = '0'
          style.left = '0'
          style.width = '10px'
          style.height = '10px'
          style.cursor = 'nesw-resize'
          break
        case 'nw':
          style.top = '0'
          style.left = '0'
          style.width = '10px'
          style.height = '10px'
          style.cursor = 'nwse-resize'
          break
      }

      dialogEl.appendChild(handle)
      handleElements.push(handle)
    })

    // 状态变量
    let activeHandle: HTMLElement | null = null
    let startX = 0
    let startY = 0
    let startRect: DOMRect | null = null
    let startLeft = 0
    let startTop = 0

    // 鼠标按下事件
    handleElements.forEach(handle => {
      handle.addEventListener('mousedown', (e: MouseEvent) => {
        if (e.button !== 0) {
          return
        }

        e.preventDefault()
        e.stopPropagation()

        activeHandle = handle
        startX = e.clientX
        startY = e.clientY

        const rect = dialogEl.getBoundingClientRect()
        startRect = rect
        startLeft = rect.left
        startTop = rect.top

        document.body.style.cursor = handle.style.cursor
        document.body.style.userSelect = 'none'

        document.addEventListener('mousemove', onMouseMove)
        document.addEventListener('mouseup', onMouseUp)
      })
    })

    function onMouseMove(e: MouseEvent) {
      if (!activeHandle || !startRect) {
        return
      }

      e.preventDefault()

      const dx = e.clientX - startX
      const dy = e.clientY - startY
      const direction = activeHandle.dataset.direction || ''

      let newWidth = startRect.width
      let newHeight = startRect.height
      let newLeft = startLeft
      let newTop = startTop

      // 根据方向调整尺寸和位置
      if (direction.includes('e')) {
        newWidth = startRect.width + dx
      }
      if (direction.includes('s')) {
        newHeight = startRect.height + dy
      }
      if (direction.includes('w')) {
        newWidth = startRect.width - dx
        newLeft = startLeft + dx
      }
      if (direction.includes('n')) {
        newHeight = startRect.height - dy
        newTop = startTop + dy
      }

      // 转换约束值
      const parseConstraint = (value: string, total: number): number => {
        if (value.endsWith('vw')) {
          return (parseFloat(value) / 100) * window.innerWidth
        }
        if (value.endsWith('vh')) {
          return (parseFloat(value) / 100) * window.innerHeight
        }
        return parseFloat(value)
      }

      const minW = parseConstraint(options.minWidth, window.innerWidth) || 300
      const maxW = parseConstraint(options.maxWidth, window.innerWidth) || window.innerWidth
      const minH = parseConstraint(options.minHeight, window.innerHeight) || 200
      const maxH = parseConstraint(options.maxHeight, window.innerHeight) || window.innerHeight

      // 应用约束
      newWidth = Math.max(minW, Math.min(newWidth, maxW))
      newHeight = Math.max(minH, Math.min(newHeight, maxH))

      // 更新样式
      dialogEl.style.width = `${newWidth}px`
      dialogEl.style.maxWidth = 'none'

      if (direction.includes('n') || direction.includes('s')) {
        dialogEl.style.height = `${newHeight}px`
      }

      if (direction.includes('w') || direction.includes('e')) {
        // 更新外层容器的位置
        el.style.marginLeft = '0'
        el.style.marginTop = '0'
        el.style.left = `${newLeft}px`
      }

      if (direction.includes('n')) {
        el.style.top = `${newTop}px`
      }
    }

    function onMouseUp() {
      activeHandle = null
      startRect = null

      document.body.style.cursor = ''
      document.body.style.userSelect = ''

      document.removeEventListener('mousemove', onMouseMove)
      document.removeEventListener('mouseup', onMouseUp)
    }

    // 清理函数
    el._cleanup = () => {
      handleElements.forEach(handle => handle.remove())
    }
  },

  unmounted(el) {
    if (el._cleanup) {
      el._cleanup()
    }
  }
}

export default dialogResizable
