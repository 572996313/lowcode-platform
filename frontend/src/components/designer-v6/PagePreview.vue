<template>
  <div class="page-preview">
    <div class="preview-header">
      <h3>页面预览</h3>
      <el-tag size="small" type="info">点击区域或组件可编辑</el-tag>
    </div>

    <div class="preview-content">
      <!-- 工具栏区域 -->
      <div
        class="preview-area toolbar-area"
        :class="{
          'active': selected?.type === 'toolbar',
          'drag-over': dragOverArea === 'toolbar'
        }"
        @click="handleAreaClick('toolbar')"
        @dragover.prevent
        @dragleave="handleDragLeave('toolbar')"
        @drop="handleDrop($event, 'toolbar')"
      >
        <div class="area-label">工具栏</div>
        <ToolbarPreview
          :selected="selected"
          :config="pageConfig.toolbar"
          @update:selected="emit('update:selected', $event)"
        />
      </div>

      <!-- 查询区区域 -->
      <div
        class="preview-area search-area"
        :class="{
          'active': selected?.type === 'search',
          'drag-over': dragOverArea === 'search'
        }"
        @click="handleAreaClick('search')"
        @dragover.prevent
        @dragleave="handleDragLeave('search')"
        @drop="handleDrop($event, 'search')"
      >
        <div class="area-label">查询区</div>
        <SearchPreview
          :selected="selected"
          :config="pageConfig.search"
          @update:selected="emit('update:selected', $event)"
        />
      </div>

      <!-- 表格区域 -->
      <div
        class="preview-area table-area"
        :class="{
          'active': selected?.type === 'table',
          'drag-over': dragOverArea === 'table'
        }"
        @click="handleAreaClick('table')"
        @dragover.prevent
        @dragleave="handleDragLeave('table')"
        @drop="handleDrop($event, 'table')"
      >
        <div class="area-label">表格</div>
        <TablePreview
          :selected="selected"
          :config="pageConfig.table"
          @update:selected="emit('update:selected', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ToolbarPreview from '@/components/designer-v6/toolbar/ToolbarPreview.vue'
import SearchPreview from '@/components/designer-v6/search/SearchPreview.vue'
import TablePreview from '@/components/designer-v6/table/TablePreview.vue'
import type {
  NewPageConfig,
  SelectedObject,
  DragData
} from '@/types/page-v6'

// Props
interface Props {
  pageConfig: NewPageConfig
  selected?: SelectedObject | null
}

// Emits
interface Emits {
  (e: 'update:pageConfig', value: NewPageConfig): void
  (e: 'update:selected', value: SelectedObject | null): void
  (e: 'drop', area: string, data: DragData): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 拖拽状态
const dragOverArea = ref<string | null>(null)

/**
 * 处理区域点击
 */
function handleAreaClick(area: 'toolbar' | 'search' | 'table') {
  // 如果点击的是已选中的区域，则取消选中
  if (props.selected?.type === area) {
    emit('update:selected', null)
    return
  }

  // 选中区域
  switch (area) {
    case 'toolbar':
      emit('update:selected', {
        type: 'toolbar',
        data: props.pageConfig.toolbar
      })
      break
    case 'search':
      emit('update:selected', {
        type: 'search',
        data: props.pageConfig.search
      })
      break
    case 'table':
      emit('update:selected', {
        type: 'table',
        data: props.pageConfig.table
      })
      break
  }
}

/**
 * 处理拖拽离开
 */
function handleDragLeave(area: string) {
  dragOverArea.value = null
}

/**
 * 处理放置
 */
function handleDrop(event: DragEvent, area: string) {
  event.preventDefault()
  dragOverArea.value = null

  // 获取拖拽数据
  const data = event.dataTransfer?.getData('application/json')
  if (!data) {
    return
  }

  try {
    const dragData = JSON.parse(data) as DragData
    emit('drop', area, dragData)
  } catch (error) {
    console.error('解析拖拽数据失败:', error)
  }
}
</script>

<style scoped lang="scss">
.page-preview {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;

  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 500;
    color: #303133;
  }
}

.preview-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}

.preview-area {
  position: relative;
  background: #fff;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  margin-bottom: 16px;
  min-height: 60px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    border-color: #c0c4cc;
  }

  &.active {
    border-color: #409eff;
    border-style: solid;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  }

  &.drag-over {
    border-color: #67c23a;
    border-style: solid;
    background: rgba(103, 194, 58, 0.05);
  }

  &:last-child {
    margin-bottom: 0;
  }

  .area-label {
    position: absolute;
    top: -10px;
    left: 12px;
    padding: 0 8px;
    background: #fff;
    font-size: 12px;
    color: #909399;
    border-radius: 2px;
  }
}

.toolbar-area {
  min-height: 60px;
}

.search-area {
  min-height: 80px;
}

.table-area {
  min-height: 300px;
}
</style>
