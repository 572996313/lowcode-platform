/**
 * 自由画布组件库
 * ComponentLibrary for Free Canvas Designer
 */
<template>
  <div class="free-canvas-component-library">
    <div class="library-header">
      <h4>组件库</h4>
    </div>

    <div class="library-content">
      <div class="component-grid">
        <div
          v-for="item in components"
          :key="item.type"
          class="library-item"
          draggable="true"
          @dragstart="handleDragStart($event, item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-info">
            <div class="item-label">{{ item.label }}</div>
            <div class="item-desc">{{ item.description }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ComponentLibraryItem } from '@/types/page-free-canvas'

const emit = defineEmits<{
  (e: 'dragstart', event: DragEvent, item: ComponentLibraryItem): void
}>()

// 当前阶段只需要标准表格和标准表单
const components: ComponentLibraryItem[] = [
  {
    type: 'table-standard',
    label: '标准表格',
    icon: '📋',
    description: '工具栏+搜索+表格',
    defaultSize: { width: 900, height: 500 },
    category: 'data'
  },
  {
    type: 'form-standard',
    label: '标准表单',
    icon: '📝',
    description: '工具栏+表单分组',
    defaultSize: { width: 700, height: 400 },
    category: 'form'
  }
]

function handleDragStart(event: DragEvent, item: ComponentLibraryItem) {
  if (event.dataTransfer) {
    const dragData = {
      source: 'free-canvas-library',
      componentType: item.type,
      defaultSize: item.defaultSize
    }
    event.dataTransfer.setData('application/json', JSON.stringify(dragData))
    event.dataTransfer.setData('text/plain', JSON.stringify(dragData))
    event.dataTransfer.effectAllowed = 'copy'
  }
  emit('dragstart', event, item)
}
</script>

<style scoped lang="scss">
.free-canvas-component-library {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;

  .library-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #e4e7ed;
    flex-shrink: 0;

    h4 {
      margin: 0;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
  }

  .library-content {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
  }

  .component-grid {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .library-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    cursor: move;
    transition: all 0.2s;

    &:hover {
      background: #ecf5ff;
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    &:active {
      cursor: grabbing;
      opacity: 0.85;
    }

    .item-icon {
      font-size: 28px;
      flex-shrink: 0;
    }

    .item-info {
      flex: 1;
      min-width: 0;
    }

    .item-label {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
    }

    .item-desc {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }
}
</style>
