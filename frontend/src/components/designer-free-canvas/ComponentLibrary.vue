/**
 * 自由画布组件库
 * ComponentLibrary for Free Canvas Designer
 */
<template>
  <div class="free-canvas-component-library">
    <div class="library-header">
      <h4>组件库</h4>
      <el-tabs v-model="activeCategory" class="library-tabs">
        <el-tab-pane label="数据" name="data" />
        <el-tab-pane label="表单" name="form" />
        <el-tab-pane label="展示" name="display" />
        <el-tab-pane label="布局" name="layout" />
      </el-tabs>
    </div>

    <div class="library-content">
      <!-- 数据组件 -->
      <div v-show="activeCategory === 'data'" class="component-grid">
        <div
          v-for="item in dataComponents"
          :key="item.type"
          class="library-item"
          draggable="true"
          @dragstart="handleDragStart($event, item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-label">{{ item.label }}</div>
        </div>
      </div>

      <!-- 表单组件 -->
      <div v-show="activeCategory === 'form'" class="component-grid">
        <div
          v-for="item in formComponents"
          :key="item.type"
          class="library-item"
          draggable="true"
          @dragstart="handleDragStart($event, item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-label">{{ item.label }}</div>
        </div>
      </div>

      <!-- 展示组件 -->
      <div v-show="activeCategory === 'display'" class="component-grid">
        <div
          v-for="item in displayComponents"
          :key="item.type"
          class="library-item"
          draggable="true"
          @dragstart="handleDragStart($event, item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-label">{{ item.label }}</div>
        </div>
      </div>

      <!-- 布局组件 -->
      <div v-show="activeCategory === 'layout'" class="component-grid">
        <div
          v-for="item in layoutComponents"
          :key="item.type"
          class="library-item"
          draggable="true"
          @dragstart="handleDragStart($event, item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ComponentLibraryItem, ComponentType } from '@/types/page-free-canvas'
import { getComponentLibraryItems } from '@/types/page-free-canvas'

const emit = defineEmits<{
  (e: 'dragstart', event: DragEvent, item: ComponentLibraryItem): void
}>()

const activeCategory = ref('data')

// 获取所有组件库项
const allItems = getComponentLibraryItems()

// 按类别分组
const dataComponents = allItems.filter(item => item.category === 'data')
const formComponents = allItems.filter(item => item.category === 'form')
const displayComponents = allItems.filter(item => item.category === 'display')
const layoutComponents = allItems.filter(item => item.category === 'layout')

/**
 * 处理拖拽开始
 */
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
    gap: 16px;
    padding: 8px 16px;
    border-bottom: 1px solid #e4e7ed;
    flex-shrink: 0;

    h4 {
      margin: 0;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      white-space: nowrap;
    }

    .library-tabs {
      flex: 1;

      :deep(.el-tabs__header) {
        margin: 0;
        padding: 0;
      }

      :deep(.el-tabs__nav-wrap::after) {
        height: 1px;
      }

      :deep(.el-tabs__item) {
        padding: 0 12px;
        font-size: 13px;
        height: 32px;
        line-height: 32px;
      }
    }
  }

  .library-content {
    flex: 1;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 12px 16px;
  }

  .component-grid {
    display: flex;
    gap: 12px;
    height: 100%;
  }

  .library-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-width: 90px;
    width: 90px;
    height: 80px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    cursor: move;
    transition: all 0.2s;
    flex-shrink: 0;

    &:hover {
      background: #ecf5ff;
      border-color: #409eff;
      transform: translateY(-2px);
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    &:active {
      cursor: grabbing;
    }

    .item-icon {
      font-size: 24px;
      margin-bottom: 6px;
    }

    .item-label {
      font-size: 12px;
      color: #606266;
      text-align: center;
    }
  }
}
</style>
