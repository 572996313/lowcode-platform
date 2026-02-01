<template>
  <div
    class="design-area"
    :class="{
      'required': required,
      'drag-over': isDragOver,
      'has-component': components.length > 0,
      'empty': components.length === 0
    }"
    :style="areaStyle"
    @dragover.prevent="handleDragOver"
    @dragleave="handleDragLeave"
    @drop="handleDrop"
  >
    <!-- 区域头部 -->
    <div v-if="showHeader" class="area-header">
      <span class="area-title">{{ title }}</span>
      <el-tag v-if="required" type="danger" size="small">必需</el-tag>
      <span v-if="components.length > 0 && maxComponents" class="component-count">
        {{ components.length }} / {{ maxComponents }}
      </span>
    </div>

    <!-- 已放置的组件（所见即所得渲染） -->
    <div class="area-content">
      <template v-for="(component, index) in components" :key="component.id">
        <div
          class="component-wrapper"
          :class="{ 'selected': selectedComponentId === component.id }"
          @click="selectComponent(component)"
        >
          <!-- 使用插槽渲染真实组件 -->
          <slot :component="component" :index="index" />

          <!-- 组件操作按钮（悬浮显示） -->
          <div class="component-actions">
            <el-button
              v-if="!required || components.length > 1"
              type="danger"
              size="small"
              circle
              @click.stop="removeComponent(index)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </template>
    </div>

    <!-- 空状态提示 -->
    <div v-if="components.length === 0" class="empty-hint">
      <el-icon class="empty-icon"><Plus /></el-icon>
      <span class="empty-text">
        拖拽 {{ acceptTypes.join(' 或 ') }} 到此处
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'
import type { ComponentRef } from '@/types/template'

interface Props {
  id: string
  title: string
  required?: boolean
  acceptTypes: string[]
  maxComponents?: number
  showHeader?: boolean
  components: ComponentRef[]
}

const props = withDefaults(defineProps<Props>(), {
  required: false,
  maxComponents: 0,
  showHeader: true
})

const emit = defineEmits<{
  'update:components': [components: ComponentRef[]]
  select?: [component: ComponentRef]
}>()

// 拖拽状态
const isDragOver = ref(false)
const selectedComponentId = ref<string | null>(null)

// 区域样式
const areaStyle = computed(() => {
  return {
    minHeight: props.required ? '200px' : '80px',
    ...props.areaStyle
  }
})

// 拖拽处理
const handleDragOver = (e: DragEvent) => {
  e.dataTransfer!.dropEffect = 'copy'
  isDragOver.value = true
}

const handleDragLeave = (e: DragEvent) => {
  // 防止子元素触发 dragleave
  const relatedTarget = e.relatedTarget as HTMLElement
  if (!relatedTarget || !e.currentTarget!.contains(relatedTarget)) {
    isDragOver.value = false
  }
}

const handleDrop = (e: DragEvent) => {
  e.preventDefault()
  isDragOver.value = false

  const data = e.dataTransfer!.getData('application/json')
  if (!data) return

  const draggedComponent = JSON.parse(data) as ComponentRef

  // 验证类型
  if (!props.acceptTypes.includes(draggedComponent.type)) {
    ElMessage.warning(`${props.title} 不支持 ${draggedComponent.type} 组件`)
    return
  }

  // 验证数量
  if (props.maxComponents && props.components.length >= props.maxComponents) {
    ElMessage.warning(`${props.title} 最多放置 ${props.maxComponents} 个组件`)
    return
  }

  // 添加组件
  const newComponents = [...props.components, {
    ...draggedComponent,
    id: `comp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }]
  emit('update:components', newComponents)
}

const removeComponent = (index: number) => {
  const newComponents = props.components.filter((_, i) => i !== index)
  emit('update:components', newComponents)
}

const selectComponent = (component: ComponentRef) => {
  selectedComponentId.value = component.id
  emit('select', component)
}
</script>

<style scoped>
.design-area {
  position: relative;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 16px;
  min-height: 80px;
  transition: all 0.3s;
  background-color: #fafafa;
}

.design-area.drag-over {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.design-area.has-component {
  border-style: solid;
  border-color: #dcdfe6;
  background-color: #fff;
}

.design-area.required .area-title::after {
  content: '*';
  color: #f56c6c;
  margin-left: 4px;
}

.area-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 12px;
}

.area-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.component-count {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.area-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.component-wrapper {
  position: relative;
  cursor: pointer;
  padding: 12px;
  border-radius: 4px;
  transition: all 0.2s;
  background-color: #fff;
  border: 1px solid transparent;
}

.component-wrapper:hover {
  background-color: #f5f7fa;
}

.component-wrapper:hover .component-actions {
  opacity: 1;
}

.component-wrapper.selected {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.component-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  gap: 4px;
}

.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  padding: 32px;
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 8px;
  color: #c0c4cc;
}

.empty-text {
  font-size: 14px;
}
</style>
