/**
 * 自由画布组件实例
 * ComponentInstance for Free Canvas Designer
 */
<template>
  <div
    :class="['component-instance', { selected, dragging }]"
    :style="instanceStyle"
    @mousedown="handleMouseDown"
    @click.stop="handleClick"
  >
    <!-- 组件内容预览 -->
    <div class="instance-content">
      <!-- 组件类型图标 -->
      <div class="instance-type-icon">{{ componentIcon }}</div>

      <!-- 组件名称 -->
      <div class="instance-name">{{ instance.name }}</div>

      <!-- 预览内容 -->
      <div class="instance-preview">
        <div class="simple-preview">{{ previewText }}</div>
      </div>
    </div>

    <!-- 选中状态的控制点 -->
    <template v-if="selected">
      <!-- 尺寸调整手柄 -->
      <div class="resize-handle handle-nw" @mousedown.stop="startResize('nw', $event)" />
      <div class="resize-handle handle-ne" @mousedown.stop="startResize('ne', $event)" />
      <div class="resize-handle handle-sw" @mousedown.stop="startResize('sw', $event)" />
      <div class="resize-handle handle-se" @mousedown.stop="startResize('se', $event)" />
    </template>

    <!-- 删除按钮 -->
    <div v-if="selected" class="delete-btn" @click.stop="handleDelete">
      <el-icon><Close /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, shallowRef, onMounted, onUnmounted } from 'vue'
import { Close } from '@element-plus/icons-vue'
import type { ComponentInstance as CompInstance, CardComponentConfig } from '@/types/page-free-canvas'

interface Props {
  instance: CompInstance
  selected?: boolean
  canvasWidth?: number
  canvasHeight?: number
}

interface Emits {
  (e: 'select', id: string): void
  (e: 'update', instance: CompInstance): void
  (e: 'delete', id: string): void
  (e: 'dragStart', id: string, event: MouseEvent): void
}

const props = withDefaults(defineProps<Props>(), {
  canvasWidth: 1200,
  canvasHeight: 800
})
const emit = defineEmits<Emits>()

const dragging = shallowRef(false)

// 组件图标
const componentIcon = computed(() => {
  const iconMap: Record<string, string> = {
    'tree': '🌲',
    'search-form': '🔍',
    'table': '📊',
    'button-group': '🔘',
    'form': '📝',
    'chart': '📈',
    'tabs': '📑',
    'card': '📦',
    'divider': '➖',
    'spacer': '⬜'
  }
  return iconMap[props.instance.type] || '📦'
})

// 实例样式
const instanceStyle = computed(() => {
  const { position, style, enabled } = props.instance
  const customStyle = style?.customStyles || {}

  return {
    left: `${position.x}px`,
    top: `${position.y}px`,
    width: `${position.width}px`,
    height: `${position.height}px`,
    zIndex: position.zIndex || 1,
    opacity: enabled === false ? 0.5 : 1,
    pointerEvents: enabled === false ? 'none' : 'auto',
    border: style?.border || '1px solid #e4e7ed',
    borderRadius: style?.borderRadius || '4px',
    backgroundColor: style?.backgroundColor || '#fff',
    boxShadow: style?.boxShadow || 'none',
    ...customStyle
  }
})

// 预览文本
const previewText = computed(() => {
  const type = props.instance.type
  const config = props.instance.config as any

  const previewMap: Record<string, string> = {
    'tree': '树形结构',
    'search-form': '查询条件',
    'table': '表格数据',
    'button-group': '按钮组',
    'form': '表单内容',
    'chart': '图表展示',
    'tabs': '标签页',
    'divider': '---',
    'spacer': ''
  }

  if (type === 'card') {
    return (config as CardComponentConfig)?.title || '卡片'
  }

  return previewMap[type] || '组件'
})

/**
 * 处理点击
 */
function handleClick() {
  emit('select', props.instance.id)
}

/**
 * 处理鼠标按下（拖拽）
 */
function handleMouseDown(event: MouseEvent) {
  // 如果点击的是 resize handle，不触发拖拽
  if ((event.target as HTMLElement).classList.contains('resize-handle')) {
    return
  }
  emit('select', props.instance.id)
  dragging.value = true
  emit('dragStart', props.instance.id, event)
}

/**
 * 开始调整大小
 */
function startResize(direction: string, event: MouseEvent) {
  event.preventDefault()
  const startX = event.clientX
  const startY = event.clientY
  const startWidth = props.instance.position.width
  const startHeight = props.instance.position.height
  const startXPos = props.instance.position.x
  const startYPos = props.instance.position.y

  const handleMouseMove = (e: MouseEvent) => {
    const deltaX = e.clientX - startX
    const deltaY = e.clientY - startY

    let newX = startXPos
    let newY = startYPos
    let newWidth = startWidth
    let newHeight = startHeight

    switch (direction) {
      case 'se': // 右下
        newWidth = Math.max(100, startWidth + deltaX)
        newHeight = Math.max(50, startHeight + deltaY)
        // 限制右边界
        if (startXPos + newWidth > props.canvasWidth) {
          newWidth = props.canvasWidth - startXPos
        }
        // 限制下边界
        if (startYPos + newHeight > props.canvasHeight) {
          newHeight = props.canvasHeight - startYPos
        }
        break
      case 'sw': // 左下
        newWidth = Math.max(100, startWidth - deltaX)
        newHeight = Math.max(50, startHeight + deltaY)
        newX = startXPos + startWidth - newWidth
        // 限制左边界
        if (newX < 0) {
          newWidth = startWidth + startXPos
          newX = 0
        }
        // 限制右边界
        if (newX + newWidth > props.canvasWidth) {
          newWidth = props.canvasWidth - newX
        }
        // 限制下边界
        if (startYPos + newHeight > props.canvasHeight) {
          newHeight = props.canvasHeight - startYPos
        }
        break
      case 'ne': // 右上
        newWidth = Math.max(100, startWidth + deltaX)
        newHeight = Math.max(50, startHeight - deltaY)
        newY = startYPos + startHeight - newHeight
        // 限制上边界
        if (newY < 0) {
          newHeight = startHeight + startYPos
          newY = 0
        }
        // 限制右边界
        if (startXPos + newWidth > props.canvasWidth) {
          newWidth = props.canvasWidth - startXPos
        }
        // 限制下边界
        if (newY + newHeight > props.canvasHeight) {
          newHeight = props.canvasHeight - newY
        }
        break
      case 'nw': // 左上
        newWidth = Math.max(100, startWidth - deltaX)
        newHeight = Math.max(50, startHeight - deltaY)
        newX = startXPos + startWidth - newWidth
        newY = startYPos + startHeight - newHeight
        // 限制左边界
        if (newX < 0) {
          newWidth = startWidth + startXPos
          newX = 0
        }
        // 限制右边界
        if (newX + newWidth > props.canvasWidth) {
          newWidth = props.canvasWidth - newX
        }
        // 限制上边界
        if (newY < 0) {
          newHeight = startHeight + startYPos
          newY = 0
        }
        // 限制下边界
        if (newY + newHeight > props.canvasHeight) {
          newHeight = props.canvasHeight - newY
        }
        break
    }

    // 确保最小尺寸
    newWidth = Math.max(100, newWidth)
    newHeight = Math.max(50, newHeight)

    // 最终边界检查
    newX = Math.max(0, Math.min(newX, props.canvasWidth - newWidth))
    newY = Math.max(0, Math.min(newY, props.canvasHeight - newHeight))
    newWidth = Math.min(newWidth, props.canvasWidth - newX)
    newHeight = Math.min(newHeight, props.canvasHeight - newY)

    emit('update', {
      ...props.instance,
      position: {
        ...props.instance.position,
        x: newX,
        y: newY,
        width: newWidth,
        height: newHeight
      }
    })
  }

  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

/**
 * 处理删除
 */
function handleDelete() {
  emit('delete', props.instance.id)
}

// 监听全局鼠标释放来重置拖拽状态
function handleGlobalMouseUp() {
  dragging.value = false
}

onMounted(() => {
  document.addEventListener('mouseup', handleGlobalMouseUp)
})

onUnmounted(() => {
  document.removeEventListener('mouseup', handleGlobalMouseUp)
})
</script>

<style scoped lang="scss">
.component-instance {
  position: absolute;
  box-sizing: border-box;
  cursor: move;
  user-select: none;
  transition: box-shadow 0.2s, border-color 0.2s;

  &:hover {
    outline: 1px dashed #409eff;
  }

  &.selected {
    outline: 2px solid #409eff;
    z-index: 100 !important;
  }

  &.dragging {
    opacity: 0.8;
    cursor: grabbing;
  }

  .instance-content {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 8px;
    overflow: hidden;

    .instance-type-icon {
      position: absolute;
      top: 4px;
      right: 4px;
      font-size: 12px;
      opacity: 0.5;
    }

    .instance-name {
      font-size: 12px;
      font-weight: 500;
      color: #606266;
      margin-bottom: 8px;
      padding-bottom: 4px;
      border-bottom: 1px dashed #e4e7ed;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .instance-preview {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fafafa;
      border-radius: 4px;
      font-size: 12px;
      color: #909399;

      .simple-preview {
        text-align: center;
      }
    }
  }

  // 调整大小手柄
  .resize-handle {
    position: absolute;
    width: 8px;
    height: 8px;
    background: #fff;
    border: 1px solid #409eff;
    border-radius: 50%;
    z-index: 10;

    &.handle-nw {
      top: -5px;
      left: -5px;
      cursor: nw-resize;
    }

    &.handle-ne {
      top: -5px;
      right: -5px;
      cursor: ne-resize;
    }

    &.handle-sw {
      bottom: -5px;
      left: -5px;
      cursor: sw-resize;
    }

    &.handle-se {
      bottom: -5px;
      right: -5px;
      cursor: se-resize;
    }
  }

  // 删除按钮
  .delete-btn {
    position: absolute;
    top: -12px;
    right: -12px;
    width: 20px;
    height: 20px;
    background: #f56c6c;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #fff;
    font-size: 12px;
    z-index: 10;

    &:hover {
      background: #f78989;
    }
  }
}
</style>
