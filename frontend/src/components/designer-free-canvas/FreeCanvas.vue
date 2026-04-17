/**
 * 页面设计器画布
 * Page Designer Canvas — flow layout with main/linked zones
 */
<template>
  <div
    ref="canvasRef"
    class="page-designer-canvas"
    @drop="handleDrop"
    @dragover.prevent
    @click="handleCanvasClick"
  >
    <!-- 空状态 -->
    <div v-if="components.length === 0" class="empty-canvas">
      <div class="empty-icon">📋</div>
      <p>从左侧拖入组件开始设计</p>
      <p class="empty-hint">支持标准表格、标准表单</p>
    </div>

    <template v-else>
      <!-- 主组件区 -->
      <div v-if="mainComponents.length" class="component-zone">
        <div class="zone-header">
          <span class="zone-label">页面组件</span>
          <span class="zone-count">{{ mainComponents.length }}</span>
        </div>
        <div class="zone-content">
          <ComponentInstance
            v-for="comp in mainComponents"
            :key="comp.id"
            :instance="comp"
            :selected="selectedId === comp.id"
            @select="handleSelect"
            @update="handleUpdate"
            @delete="handleDelete"
          />
        </div>
      </div>

      <!-- 弹窗组件区 -->
      <div v-if="linkedComponents.length" class="component-zone linked-zone">
        <div class="zone-header">
          <span class="zone-label">副组件</span>
          <span class="zone-count">{{ linkedComponents.length }}</span>
        </div>
        <div class="zone-content">
          <ComponentInstance
            v-for="comp in linkedComponents"
            :key="comp.id"
            :instance="comp"
            :selected="selectedId === comp.id"
            @select="handleSelect"
            @update="handleUpdate"
            @delete="handleDelete"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
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

// 组件列表
const components = computed(() => props.config?.components || [])
const mainComponents = computed(() => components.value.filter(c => c.role !== 'linked'))
const linkedComponents = computed(() => components.value.filter(c => c.role === 'linked'))

// 选中处理
function handleSelect(id: string) {
  emit('update:selectedId', id)
}

function handleCanvasClick(e: MouseEvent) {
  if (e.target === canvasRef.value) {
    emit('update:selectedId', null)
  }
}

// 组件更新
function handleUpdate(instance: CompInstance) {
  const newComponents = [...(props.config?.components || [])]
  const index = newComponents.findIndex(c => c.id === instance.id)
  if (index > -1) {
    newComponents[index] = instance
    emit('update:config', { ...props.config, components: newComponents })
  }
}

// 组件删除
function handleDelete(id: string) {
  const newComponents = (props.config?.components || []).filter(c => c.id !== id)
  emit('update:config', { ...props.config, components: newComponents })
  if (props.selectedId === id) {
    emit('update:selectedId', null)
  }
}

// 拖入新组件
function handleDrop(event: DragEvent) {
  const dataStr = event.dataTransfer?.getData('application/json')
    || event.dataTransfer?.getData('text/plain')
  if (!dataStr) return

  try {
    const data = JSON.parse(dataStr)
    if (data.source === 'free-canvas-library') {
      const newInstance = createComponentInstance(data.componentType as ComponentType)
      emit('update:config', {
        ...props.config,
        components: [...(props.config?.components || []), newInstance]
      })
      emit('update:selectedId', newInstance.id)
    }
  } catch (e) {
    console.error('解析拖拽数据失败:', e)
  }
}

// 键盘事件
function handleKeyDown(event: KeyboardEvent) {
  if (!props.selectedId) return
  if (event.key === 'Delete' || event.key === 'Backspace') {
    const target = event.target as HTMLElement
    if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') return
    handleDelete(props.selectedId)
  }
  if (event.key === 'Escape') {
    emit('update:selectedId', null)
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
})
</script>

<style scoped lang="scss">
.page-designer-canvas {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 0;
}

.empty-canvas {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
  }

  p {
    margin: 4px 0;
    font-size: 14px;
  }

  .empty-hint {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.component-zone {
  margin-bottom: 20px;

  .zone-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .zone-label {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
    }

    .zone-count {
      font-size: 12px;
      color: #909399;
      background: #ebeef5;
      border-radius: 10px;
      padding: 1px 8px;
    }
  }

  .zone-content {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}

.linked-zone {
  .zone-header .zone-label {
    color: #e6a23c;
  }
}
</style>
