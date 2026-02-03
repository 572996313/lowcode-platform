/**
 * 自由画布
 * FreeCanvas for Free Canvas Designer
 */
<template>
  <div class="free-canvas-wrapper" @wheel.prevent="handleWheel">
    <div
      ref="canvasContainerRef"
      :class="['canvas-container', { 'is-panning': isPanning }]"
      :style="containerStyle"
      @mousedown="handleContainerMouseDown"
    >
      <div
        ref="canvasRef"
        :class="['free-canvas', { 'is-dragging': isDragging }]"
        :style="canvasStyle"
        @drop="handleDrop"
        @dragover.prevent="handleDragOver"
        @dragleave="handleDragLeave"
        @mousedown="handleCanvasMouseDown"
      >
        <!-- 网格背景 -->
        <svg v-if="canvasConfig.gridSize" class="canvas-grid" :width="canvasConfig.width" :height="canvasHeight">
          <defs>
            <pattern
              :id="`grid-pattern-${instanceId}`"
              :width="canvasConfig.gridSize"
              :height="canvasConfig.gridSize"
              patternUnits="userSpaceOnUse"
            >
              <path
                d="M 100 0 L 0 0 0 100"
                fill="none"
                stroke="var(--el-border-color-lighter)"
                stroke-width="0.5"
              />
            </pattern>
          </defs>
          <rect
            :width="canvasConfig.width"
            :height="canvasHeight"
            :fill="`url(#grid-pattern-${instanceId})`"
          />
        </svg>

        <!-- 组件实例 -->
        <ComponentInstance
          v-for="comp in components"
          :key="comp.id"
          :instance="comp"
          :selected="selectedId === comp.id"
          :canvas-width="canvasConfig.width"
          :canvas-height="canvasHeight"
          @select="handleSelect"
          @update="handleUpdate"
          @delete="handleDelete"
          @drag-start="handleInstanceDragStart"
        />

        <!-- 拖拽预览 -->
        <div
          v-if="dragPreview.show"
          class="drag-preview"
          :style="{
            left: `${dragPreview.x}px`,
            top: `${dragPreview.y}px`,
            width: `${dragPreview.width}px`,
            height: `${dragPreview.height}px`
          }"
        >
          <div class="drag-preview-content">
            {{ dragPreview.label }}
          </div>
        </div>
      </div>
    </div>

    <!-- 缩放控制面板 -->
    <div class="zoom-controls">
      <el-button-group>
        <el-button :icon="ZoomOut" size="small" @click="zoomOut" :disabled="zoom <= minZoom" />
        <el-button size="small" disabled class="zoom-display">{{ Math.round(zoom * 100) }}%</el-button>
        <el-button :icon="ZoomIn" size="small" @click="zoomIn" :disabled="zoom >= maxZoom" />
      </el-button-group>
      <el-button :icon="RefreshRight" size="small" @click="resetView">重置视图</el-button>
    </div>

    <!-- 画布信息 -->
    <div class="canvas-info">
      <span>{{ components.length }} 个组件</span>
      <span v-if="canvasConfig.snapToGrid">网格: {{ canvasConfig.gridSize }}px</span>
      <span class="shortcut-hint" @click="showShortcuts = !showShortcuts">
        按 ? 查看快捷键
      </span>
    </div>

    <!-- 快捷键帮助 -->
    <div v-if="showShortcuts" class="shortcuts-panel" @click.stop="showShortcuts = false">
      <div class="shortcuts-content" @click.stop>
        <h4>键盘快捷键</h4>
        <div class="shortcut-list">
          <div class="shortcut-item">
            <kbd>W</kbd><kbd>A</kbd><kbd>S</kbd><kbd>D</kbd> 或方向键
            <span>移动画布</span>
          </div>
          <div class="shortcut-item">
            <kbd>Space</kbd> + 拖拽
            <span>平移画布</span>
          </div>
          <div class="shortcut-item">
            <kbd>Ctrl</kbd> + 滚轮
            <span>缩放画布 (100%-150%)</span>
          </div>
          <div class="shortcut-item">
            <kbd>滚轮</kbd>
            <span>上下移动画布</span>
          </div>
          <div class="shortcut-item">
            <kbd>Shift</kbd> + 滚轮
            <span>左右移动画布（上滚往左，下滚往右）</span>
          </div>
          <div class="shortcut-item">
            <kbd>Delete</kbd> / <kbd>Backspace</kbd>
            <span>删除选中组件</span>
          </div>
          <div class="shortcut-item">
            <kbd>Esc</kbd>
            <span>取消选择</span>
          </div>
          <div class="shortcut-item">
            <kbd>?</kbd>
            <span>显示/隐藏帮助</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, shallowRef, watch } from 'vue'
import { ZoomIn, ZoomOut, RefreshRight } from '@element-plus/icons-vue'
import ComponentInstance from './ComponentInstance.vue'
import type { FreeCanvasPageConfig, ComponentInstance as CompInstance, ComponentType } from '@/types/page-free-canvas'
import { createComponentInstance } from '@/types/page-free-canvas'

interface Props {
  config: FreeCanvasPageConfig
  selectedId?: string | null
}

interface Emits {
  (e: 'update:config', config: FreeCanvasPageConfig): void
  (e: 'update:selectedId', id: string | null): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const canvasRef = ref<HTMLElement>()
const canvasContainerRef = ref<HTMLElement>()
const instanceId = ref(Math.random().toString(36).substr(2, 9))
const isDragging = ref(false)
const isPanning = ref(false)
const showShortcuts = ref(false)

// 画布平移偏移
const panOffset = ref({ x: 0, y: 0 })
const panStart = ref({ x: 0, y: 0 })

// 缩放
const zoom = ref(1)
const minZoom = computed(() => props.config.canvas.minZoom ?? 1)
const maxZoom = computed(() => props.config.canvas.maxZoom ?? 1.5)

// 拖拽状态
const dragState = ref({
  isDragging: false,
  componentId: '',
  startX: 0,
  startY: 0,
  originalX: 0,
  originalY: 0
})

// 拖拽预览
const dragPreview = ref({
  show: false,
  x: 0,
  y: 0,
  width: 100,
  height: 100,
  label: ''
})

// 键盘按键状态
const keysPressed = ref<Set<string>>(new Set())

// 画布配置
const canvasConfig = computed(() => props.config.canvas)

// 组件列表
const components = computed(() => props.config.components)

// 画布高度
const canvasHeight = computed(() => {
  if (canvasConfig.value.height) {
    return canvasConfig.value.height
  }
  // 计算最大高度以容纳所有组件
  const maxY = components.value.reduce((max, comp) => {
    return Math.max(max, comp.position.y + comp.position.height)
  }, 600)
  return maxY + 100
})

// 画布样式
const canvasStyle = computed(() => {
  return {
    width: `${canvasConfig.value.width}px`,
    height: typeof canvasConfig.value.height === 'number'
      ? `${canvasConfig.value.height}px`
      : 'auto',
    backgroundColor: canvasConfig.value.backgroundColor || '#f5f7fa',
    backgroundImage: canvasConfig.value.backgroundImage
      ? `url(${canvasConfig.value.backgroundImage})`
      : undefined,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '600px'
  }
})

// 容器样式（支持平移 + 缩放）
const containerStyle = computed(() => {
  return {
    transform: `translate(${panOffset.value.x}px, ${panOffset.value.y}px) scale(${zoom.value})`,
    transformOrigin: '0 0',
    transition: isPanning.value ? 'none' : 'transform 0.15s ease-out'
  }
})

// 同步缩放到配置
watch(zoom, (newZoom) => {
  emit('update:config', {
    ...props.config,
    canvas: {
      ...props.config.canvas,
      zoom: newZoom
    }
  })
})

/**
 * 缩放相关方法
 */
function zoomIn() {
  zoom.value = Math.min(zoom.value + 0.1, maxZoom.value)
}

function zoomOut() {
  zoom.value = Math.max(zoom.value - 0.1, minZoom.value)
}

function resetView() {
  zoom.value = 1
  // 重置到视图中心
  setTimeout(() => {
    const wrapper = document.querySelector('.free-canvas-wrapper') as HTMLElement
    if (wrapper && canvasRef.value) {
      const wrapperRect = wrapper.getBoundingClientRect()
      const canvasWidth = canvasConfig.value.width
      panOffset.value = {
        x: Math.max(20, (wrapperRect.width - canvasWidth) / 2),
        y: 20
      }
    }
  }, 0)
}

/**
 * 处理选择
 */
function handleSelect(id: string) {
  emit('update:selectedId', id)
}

/**
 * 处理组件更新
 */
function handleUpdate(instance: CompInstance) {
  const newComponents = [...props.config.components]
  const index = newComponents.findIndex(c => c.id === instance.id)
  if (index > -1) {
    newComponents[index] = instance
    emit('update:config', {
      ...props.config,
      components: newComponents
    })
  }
}

/**
 * 处理删除
 */
function handleDelete(id: string) {
  const newComponents = props.config.components.filter(c => c.id !== id)
  emit('update:config', {
    ...props.config,
    components: newComponents
  })
  if (props.selectedId === id) {
    emit('update:selectedId', null)
  }
}

/**
 * 处理拖拽开始（组件实例）
 */
function handleInstanceDragStart(id: string, event: MouseEvent) {
  const comp = components.value.find(c => c.id === id)
  if (!comp) return

  dragState.value = {
    isDragging: true,
    componentId: id,
    startX: event.clientX,
    startY: event.clientY,
    originalX: comp.position.x,
    originalY: comp.position.y
  }
  isDragging.value = true
}

/**
 * 处理容器鼠标按下（平移开始）
 */
function handleContainerMouseDown(event: MouseEvent) {
  // 空格键按下 或 中键点击 -> 开始平移
  if (keysPressed.value.has(' ') || event.button === 1) {
    event.preventDefault()
    isPanning.value = true
    panStart.value = {
      x: event.clientX - panOffset.value.x,
      y: event.clientY - panOffset.value.y
    }
  }
}

/**
 * 处理滚轮（平移 + 缩放）
 */
function handleWheel(event: WheelEvent) {
  const ctrlKey = event.ctrlKey || event.metaKey

  if (ctrlKey) {
    // Ctrl + 滚轮 -> 缩放
    const delta = event.deltaY > 0 ? -0.1 : 0.1
    const newZoom = Math.round((zoom.value + delta) * 10) / 10
    zoom.value = Math.max(minZoom.value, Math.min(maxZoom.value, newZoom))
  } else {
    // 普通滚轮 -> 平移画布（降低敏感度）
    const scaleFactor = 0.25

    // Shift + 滚轮 -> 水平平移（反向：向上滚往左移，向下滚往右移）
    if (event.shiftKey) {
      panOffset.value.x -= event.deltaY * scaleFactor
    } else {
      // 普通滚轮 -> 垂直平移（反向：向上滚画布向上移）
      panOffset.value.y -= event.deltaY * scaleFactor
    }

    // 水平滚动（触摸板）也支持
    if (event.deltaX !== 0) {
      panOffset.value.x -= event.deltaX * scaleFactor
    }

    // 限制平移范围，确保画布不会完全移出视口
    clampPanOffset()
  }
}

/**
 * 限制平移范围
 */
function clampPanOffset() {
  if (!canvasContainerRef.value) return

  const wrapper = document.querySelector('.free-canvas-wrapper') as HTMLElement
  if (!wrapper) return

  const wrapperRect = wrapper.getBoundingClientRect()
  const canvasWidth = canvasConfig.value.width * zoom.value
  const canvasHeight = canvasHeight.value * zoom.value

  // 至少保留 100px 的画布在视口内可见
  const minVisible = 100

  // X 轴限制
  const maxPanX = minVisible
  const minPanX = wrapperRect.width - canvasWidth - minVisible
  panOffset.value.x = Math.max(minPanX, Math.min(maxPanX, panOffset.value.x))

  // Y 轴限制
  const maxPanY = minVisible
  const minPanY = wrapperRect.height - canvasHeight - minVisible
  panOffset.value.y = Math.max(minPanY, Math.min(maxPanY, panOffset.value.y))
}

/**
 * 处理键盘按下
 */
function handleKeyDown(event: KeyboardEvent) {
  keysPressed.value.add(event.key)

  // 显示快捷键帮助
  if (event.key === '?') {
    showShortcuts.value = !showShortcuts.value
    return
  }

  // 删除选中的组件
  if ((event.key === 'Delete' || event.key === 'Backspace') && selectedId.value) {
    handleDelete(selectedId.value)
    return
  }

  // ESC 取消选择
  if (event.key === 'Escape') {
    emit('update:selectedId', null)
    return
  }

  // WASD 或方向键移动画布
  const step = event.shiftKey ? 50 : 20
  switch (event.key) {
    case 'w':
    case 'W':
    case 'ArrowUp':
      panOffset.value.y += step
      clampPanOffset()
      event.preventDefault()
      break
    case 's':
    case 'S':
    case 'ArrowDown':
      panOffset.value.y -= step
      clampPanOffset()
      event.preventDefault()
      break
    case 'a':
    case 'A':
    case 'ArrowLeft':
      panOffset.value.x += step
      clampPanOffset()
      event.preventDefault()
      break
    case 'd':
    case 'D':
    case 'ArrowRight':
      panOffset.value.x -= step
      clampPanOffset()
      event.preventDefault()
      break
  }
}

/**
 * 处理键盘释放
 */
function handleKeyUp(event: KeyboardEvent) {
  keysPressed.value.delete(event.key)
}

/**
 * 处理画布鼠标按下
 */
function handleCanvasMouseDown(event: MouseEvent) {
  // 如果点击的是画布本身（不是组件），取消选择
  if (event.target === canvasRef.value || (event.target as HTMLElement).classList.contains('canvas-grid')) {
    emit('update:selectedId', null)
  }
}

/**
 * 处理拖拽悬停
 */
function handleDragOver(event: DragEvent) {
  if (!canvasRef.value) return

  const rect = canvasRef.value.getBoundingClientRect()
  const x = (event.clientX - rect.left) / zoom.value
  const y = (event.clientY - rect.top) / zoom.value

  // 解析拖拽数据
  const dataStr = event.dataTransfer?.getData('application/json')
  if (!dataStr) return

  try {
    const data = JSON.parse(dataStr)
    if (data.source === 'free-canvas-library') {
      // 更小的默认尺寸
      const sizeMap: Record<string, { width: number; height: number }> = {
        'tree': { width: 200, height: 300 },
        'search-form': { width: 600, height: 80 },
        'table': { width: 700, height: 250 },
        'button-group': { width: 300, height: 40 },
        'form': { width: 400, height: 200 },
        'chart': { width: 400, height: 250 },
        'tabs': { width: 600, height: 200 },
        'card': { width: 300, height: 150 },
        'divider': { width: 500, height: 16 },
        'spacer': { width: 80, height: 40 }
      }
      const size = sizeMap[data.componentType] || { width: 150, height: 150 }

      // 对齐网格
      let targetX = x - size.width / 2
      let targetY = y - size.height / 2
      if (canvasConfig.value.snapToGrid && canvasConfig.value.gridSize) {
        targetX = Math.round(targetX / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
        targetY = Math.round(targetY / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
      }

      // 限制在画布内
      targetX = Math.max(0, Math.min(targetX, canvasConfig.value.width - size.width))
      targetY = Math.max(0, targetY)

      const labelMap: Record<string, string> = {
        'tree': '🌲 树组件',
        'search-form': '🔍 查询表单',
        'table': '📊 表格',
        'button-group': '🔘 按钮组',
        'form': '📝 表单',
        'chart': '📈 图表',
        'tabs': '📑 标签页',
        'card': '📦 卡片',
        'divider': '➖ 分割线',
        'spacer': '⬜ 占位符'
      }

      dragPreview.value = {
        show: true,
        x: targetX,
        y: targetY,
        width: size.width,
        height: size.height,
        label: labelMap[data.componentType] || '组件'
      }
    }
  } catch (e) {
    // ignore
  }
}

/**
 * 处理拖拽离开
 */
function handleDragLeave() {
  dragPreview.value.show = false
}

/**
 * 处理放置
 */
function handleDrop(event: DragEvent) {
  dragPreview.value.show = false

  if (!canvasRef.value) return

  const rect = canvasRef.value.getBoundingClientRect()
  const x = (event.clientX - rect.left) / zoom.value
  const y = (event.clientY - rect.top) / zoom.value

  // 解析拖拽数据
  const dataStr = event.dataTransfer?.getData('application/json')
  if (!dataStr) return

  try {
    const data = JSON.parse(dataStr)
    if (data.source === 'free-canvas-library') {
      // 更小的默认尺寸
      const sizeMap: Record<string, { width: number; height: number }> = {
        'tree': { width: 200, height: 300 },
        'search-form': { width: 600, height: 80 },
        'table': { width: 700, height: 250 },
        'button-group': { width: 300, height: 40 },
        'form': { width: 400, height: 200 },
        'chart': { width: 400, height: 250 },
        'tabs': { width: 600, height: 200 },
        'card': { width: 300, height: 150 },
        'divider': { width: 500, height: 16 },
        'spacer': { width: 80, height: 40 }
      }
      const size = sizeMap[data.componentType] || { width: 150, height: 150 }

      // 对齐网格
      let targetX = x - size.width / 2
      let targetY = y - size.height / 2
      if (canvasConfig.value.snapToGrid && canvasConfig.value.gridSize) {
        targetX = Math.round(targetX / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
        targetY = Math.round(targetY / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
      }

      // 限制在画布内
      targetX = Math.max(0, Math.min(targetX, canvasConfig.value.width - size.width))
      targetY = Math.max(0, targetY)

      // 推开重叠的组件
      pushAwayCollidingComponents(targetX, targetY, size.width, size.height)

      // 创建新组件
      const newInstance = createComponentInstance(
        data.componentType as ComponentType,
        targetX,
        targetY
      )

      emit('update:config', {
        ...props.config,
        components: [...props.config.components, newInstance]
      })

      // 选中新创建的组件
      emit('update:selectedId', newInstance.id)
    }
  } catch (e) {
    console.error('Failed to parse drop data:', e)
  }
}

/**
 * 检查两个矩形是否重叠
 */
function checkCollision(
  x1: number, y1: number, w1: number, h1: number,
  x2: number, y2: number, w2: number, h2: number
): boolean {
  return !(x1 + w1 <= x2 || x2 + w2 <= x1 || y1 + h1 <= y2 || y2 + h2 <= y1)
}

/**
 * 查找与指定矩形重叠的所有组件
 */
function findCollidingComponents(
  targetX: number, targetY: number, targetWidth: number, targetHeight: number,
  excludeId: string = ''
): CompInstance[] {
  return components.value.filter(comp => {
    if (comp.id === excludeId) return false
    return checkCollision(
      targetX, targetY, targetWidth, targetHeight,
      comp.position.x, comp.position.y, comp.position.width, comp.position.height
    )
  })
}

/**
 * 推开重叠的组件
 */
function pushAwayCollidingComponents(
  targetX: number, targetY: number, targetWidth: number, targetHeight: number,
  excludeId: string = ''
) {
  const colliding = findCollidingComponents(targetX, targetY, targetWidth, targetHeight, excludeId)
  const targetCenterX = targetX + targetWidth / 2
  const targetCenterY = targetY + targetHeight / 2

  colliding.forEach(comp => {
    const compCenterX = comp.position.x + comp.position.width / 2
    const compCenterY = comp.position.y + comp.position.height / 2

    // 计算方向
    const dx = compCenterX - targetCenterX
    const dy = compCenterY - targetCenterY

    // 计算移动距离（包含一定的缓冲间距）
    const buffer = 10 // 缓冲间距
    let newX = comp.position.x
    let newY = comp.position.y

    // 判断主要移动方向（X或Y）
    const overlapX = (targetWidth + comp.position.width) / 2 - Math.abs(dx)
    const overlapY = (targetHeight + comp.position.height) / 2 - Math.abs(dy)

    if (overlapX < overlapY) {
      // 主要在X方向推开
      if (dx > 0) {
        newX = targetX + targetWidth + buffer
      } else {
        newX = targetX - comp.position.width - buffer
      }
    } else {
      // 主要在Y方向推开
      if (dy > 0) {
        newY = targetY + targetHeight + buffer
      } else {
        newY = targetY - comp.position.height - buffer
      }
    }

    // 对齐网格
    if (canvasConfig.value.snapToGrid && canvasConfig.value.gridSize) {
      newX = Math.round(newX / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
      newY = Math.round(newY / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
    }

    // 限制在画布内
    newX = Math.max(0, Math.min(newX, canvasConfig.value.width - comp.position.width))
    newY = Math.max(0, newY)

    // 更新组件位置
    handleUpdate({
      ...comp,
      position: {
        ...comp.position,
        x: newX,
        y: newY
      }
    })
  })
}

// 全局鼠标移动处理（组件拖拽 + 画布平移）
function handleMouseMove(event: MouseEvent) {
  // 处理画布平移
  if (isPanning.value) {
    panOffset.value.x = event.clientX - panStart.value.x
    panOffset.value.y = event.clientY - panStart.value.y
    clampPanOffset()
    return
  }

  // 处理组件拖拽
  if (!dragState.value.isDragging) return

  const deltaX = (event.clientX - dragState.value.startX) / zoom.value
  const deltaY = (event.clientY - dragState.value.startY) / zoom.value

  let newX = dragState.value.originalX + deltaX
  let newY = dragState.value.originalY + deltaY

  // 对齐网格
  if (canvasConfig.value.snapToGrid && canvasConfig.value.gridSize) {
    newX = Math.round(newX / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
    newY = Math.round(newY / canvasConfig.value.gridSize) * canvasConfig.value.gridSize
  }

  const comp = components.value.find(c => c.id === dragState.value.componentId)
  if (comp) {
    // 限制在画布内
    newX = Math.max(0, Math.min(newX, canvasConfig.value.width - comp.position.width))
    newY = Math.max(0, newY)

    // 先推开其他组件
    pushAwayCollidingComponents(
      newX, newY, comp.position.width, comp.position.height,
      comp.id
    )

    handleUpdate({
      ...comp,
      position: {
        ...comp.position,
        x: newX,
        y: newY
      }
    })
  }
}

// 全局鼠标释放处理
function handleMouseUp() {
  if (dragState.value.isDragging) {
    dragState.value.isDragging = false
    isDragging.value = false
  }
  if (isPanning.value) {
    isPanning.value = false
  }
}

onMounted(() => {
  // 初始化画布位置到视图中心
  setTimeout(() => {
    const wrapper = document.querySelector('.free-canvas-wrapper') as HTMLElement
    if (wrapper && canvasRef.value) {
      const wrapperRect = wrapper.getBoundingClientRect()
      const canvasWidth = canvasConfig.value.width
      panOffset.value.x = Math.max(20, (wrapperRect.width - canvasWidth) / 2)
      panOffset.value.y = 20
    }
  }, 0)

  // 从配置加载缩放
  if (props.config.canvas.zoom) {
    zoom.value = props.config.canvas.zoom
  }

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  document.addEventListener('keydown', handleKeyDown)
  document.addEventListener('keyup', handleKeyUp)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  document.removeEventListener('keydown', handleKeyDown)
  document.removeEventListener('keyup', handleKeyUp)
})
</script>

<style scoped lang="scss">
.free-canvas-wrapper {
  position: relative;
  flex: 1;
  overflow: hidden;
  background: #e8ecf1;
  cursor: default;

  &.is-panning {
    cursor: grab;
  }

  &:active.is-panning {
    cursor: grabbing;
  }
}

.canvas-container {
  position: absolute;
  top: 0;
  left: 0;

  &.is-panning {
    transition: none;
  }
}

.free-canvas {
  position: relative;
  margin: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: opacity 0.2s;

  &.is-dragging {
    opacity: 0.9;
  }

  .canvas-grid {
    position: absolute;
    top: 0;
    left: 0;
    pointer-events: none;
    opacity: 0.5;
  }

  .drag-preview {
    position: absolute;
    border: 2px dashed #409eff;
    background: rgba(64, 158, 255, 0.1);
    border-radius: 4px;
    pointer-events: none;
    z-index: 1000;

    .drag-preview-content {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-size: 12px;
      color: #409eff;
      white-space: nowrap;
    }
  }
}

// 缩放控制面板
.zoom-controls {
  position: absolute;
  bottom: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;

  .zoom-display {
    min-width: 50px;
    font-family: monospace;
  }

  :deep(.el-button-group .el-button) {
    border-radius: 4px;

    &:first-child {
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;
    }

    &:last-child {
      border-top-right-radius: 4px;
      border-bottom-right-radius: 4px;
    }
  }
}

.canvas-info {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 16px;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 20px;
  font-size: 12px;
  pointer-events: none;
  z-index: 100;

  .shortcut-hint {
    pointer-events: auto;
    cursor: pointer;
    opacity: 0.8;

    &:hover {
      opacity: 1;
      text-decoration: underline;
    }
  }
}

// 快捷键帮助面板
.shortcuts-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .shortcuts-content {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    min-width: 320px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);

    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      color: #303133;
      text-align: center;
    }

    .shortcut-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .shortcut-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        kbd {
          display: inline-block;
          padding: 2px 6px;
          font-size: 11px;
          font-family: monospace;
          background: #f5f7fa;
          border: 1px solid #dcdfe6;
          border-radius: 4px;
          color: #606266;
          margin-right: 2px;

          &:not(:last-child)::after {
            content: '+';
            margin-left: 4px;
            color: #909399;
          }

          &:last-child::after {
            content: '';
          }
        }

        span {
          font-size: 13px;
          color: #606266;
        }
      }
    }
  }
}
</style>
