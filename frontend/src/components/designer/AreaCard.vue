<template>
  <div class="area-card" :class="{ 'is-draggable': isDraggable }">
    <div class="card-header">
      <div class="card-title">
        <el-icon>
          <component :is="getIcon(area.type)" />
        </el-icon>
        <span>{{ area.name }}</span>
      </div>
      <div class="card-actions">
        <el-tag v-if="area.required" type="danger" size="small">必需</el-tag>
        <el-switch
          v-model="area.enabled"
          size="small"
          @click.stop
          @change="handleToggle"
        />
        <el-button
          type="danger"
          icon="Delete"
          size="small"
          text
          @click="handleRemove"
        >
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>
    <div class="card-body">
      <div class="area-meta">
        <span class="meta-label">类型:</span>
        <el-tag size="small">{{ area.type }}</el-tag>
      </div>
      <div v-if="area.position" class="area-meta">
        <span class="meta-label">位置:</span>
        <el-tag size="small" type="success">{{ positionLabel }}</el-tag>
      </div>
      <div v-if="area.layoutHints?.width || area.layoutHints?.height" class="area-meta">
        <span class="meta-label">尺寸:</span>
        <span class="meta-value">
          {{ area.layoutHints.width }} × {{ area.layoutHints.height }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Delete, Edit, Document, List, Grid, Files, Histogram, View } from '@element-plus/icons-vue'
import type { NormalizedAreaV3 } from '@/api/page'

interface Props {
  area: NormalizedAreaV3
  isDraggable?: boolean
}

interface Emits {
  (e: 'remove', id: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const positionLabel = computed(() => {
  const labels: Record<string, string> = {
    top: '顶部',
    bottom: '底部',
    left: '左侧',
    right: '右侧',
    main: '主内容区'
  }
  return labels[props.area.position || ''] || props.area.position
})

const getIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    search: Edit,
    content: Document,
    tree: List,
    toolbar: Grid,
    tabs: Files,
    stats: Histogram,
    charts: Histogram,
    header: View,
    custom: Edit
  }
  return iconMap[type] || Grid
}

const handleToggle = () => {
  // 切换已通过 v-model 处理
}

const handleRemove = () => {
  if (props.area.required) {
    ElMessage.warning('必需区域不能删除')
    return
  }
  emit('remove', props.area.id)
}
</script>

<script lang="ts">
import { ElMessage } from 'element-plus'

export default {
  name: 'AreaCard'
}
</script>

<style scoped>
.area-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  cursor: default;
  transition: all 0.3s;
}

.area-card.is-draggable {
  cursor: move;
}

.area-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
}

.area-meta {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-label {
  color: #909399;
}

.meta-value {
  color: #606266;
}
</style>
