<template>
  <div class="visual-layout-editor">
    <div class="editor-header">
      <div class="header-title">
        <el-icon><Rank /></el-icon>
        <span>可视化布局编辑器</span>
      </div>
      <div class="header-actions">
        <el-button size="small" @click="handleReset">重置</el-button>
        <el-button size="small" type="primary" @click="handleSave">应用布局</el-button>
      </div>
    </div>

    <div class="editor-canvas">
      <!-- 顶部区域 -->
      <div
        class="drop-zone drop-zone-top"
        @dragover.prevent
        @drop.prevent="handleDrop($event, 'top')"
      >
        <div class="zone-label">
          <el-icon><Top /></el-icon>
          顶部区域
        </div>
        <draggable
          v-model="topAreas"
          :group="{ name: 'areas', pull: true, put: true }"
          item-key="id"
          class="zone-content"
          @change="handleAreaChange"
        >
          <template #item="{ element }">
            <AreaCard :area="element" @remove="removeArea(element.id)" />
          </template>
        </draggable>
      </div>

      <!-- 中间区域（左侧 + 主内容 + 右侧） -->
      <div class="drop-zone drop-zone-middle">
        <!-- 左侧区域 -->
        <div
          class="drop-zone drop-zone-left"
          @dragover.prevent
          @drop.prevent="handleDrop($event, 'left')"
        >
          <div class="zone-label">
            <el-icon><Back /></el-icon>
            左侧
          </div>
          <draggable
            v-model="leftAreas"
            :group="{ name: 'areas', pull: true, put: true }"
            item-key="id"
            class="zone-content"
            @change="handleAreaChange"
          >
            <template #item="{ element }">
              <AreaCard :area="element" @remove="removeArea(element.id)" />
            </template>
          </draggable>
        </div>

        <!-- 主内容区 -->
        <div
          class="drop-zone drop-zone-main"
          @dragover.prevent
          @drop.prevent="handleDrop($event, 'main')"
        >
          <div class="zone-label">
            <el-icon><Grid /></el-icon>
            主内容区
          </div>
          <draggable
            v-model="mainAreas"
            :group="{ name: 'areas', pull: true, put: true }"
            item-key="id"
            class="zone-content"
            @change="handleAreaChange"
          >
            <template #item="{ element }">
              <AreaCard :area="element" @remove="removeArea(element.id)" />
            </template>
          </draggable>
        </div>

        <!-- 右侧区域 -->
        <div
          class="drop-zone drop-zone-right"
          @dragover.prevent
          @drop.prevent="handleDrop($event, 'right')"
        >
          <div class="zone-label">
            <el-icon><Right /></el-icon>
            右侧
          </div>
          <draggable
            v-model="rightAreas"
            :group="{ name: 'areas', pull: true, put: true }"
            item-key="id"
            class="zone-content"
            @change="handleAreaChange"
          >
            <template #item="{ element }">
              <AreaCard :area="element" @remove="removeArea(element.id)" />
            </template>
          </draggable>
        </div>
      </div>

      <!-- 底部区域 -->
      <div
        class="drop-zone drop-zone-bottom"
        @dragover.prevent
        @drop.prevent="handleDrop($event, 'bottom')"
      >
        <div class="zone-label">
          <el-icon><Bottom /></el-icon>
          底部区域
        </div>
        <draggable
          v-model="bottomAreas"
          :group="{ name: 'areas', pull: true, put: true }"
          item-key="id"
          class="zone-content"
          @change="handleAreaChange"
        >
          <template #item="{ element }">
            <AreaCard :area="element" @remove="removeArea(element.id)" />
          </template>
        </draggable>
      </div>
    </div>

    <!-- 未使用区域 -->
    <div v-if="unusedAreas.length > 0" class="unused-areas">
      <div class="unused-title">未使用的区域（可拖拽到上方区域）</div>
      <div class="unused-grid">
        <draggable
          v-model="unusedAreas"
          :group="{ name: 'areas', pull: true, put: true }"
          item-key="id"
          class="unused-content"
          @change="handleAreaChange"
        >
          <template #item="{ element }">
            <AreaCard :area="element" is-draggable @remove="removeArea(element.id)" />
          </template>
        </draggable>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import draggable from 'vuedraggable'
import { Rank, Top, Bottom, Back, Right, Grid } from '@element-plus/icons-vue'
import AreaCard from './AreaCard.vue'
import type { NormalizedAreaV3 } from '@/api/page'

interface Props {
  areas: NormalizedAreaV3[]
}

interface Emits {
  (e: 'update:areas', areas: NormalizedAreaV3[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 按位置分组的区域
const topAreas = ref<NormalizedAreaV3[]>([])
const leftAreas = ref<NormalizedAreaV3[]>([])
const rightAreas = ref<NormalizedAreaV3[]>([])
const mainAreas = ref<NormalizedAreaV3[]>([])
const bottomAreas = ref<NormalizedAreaV3[]>([])
const unusedAreas = ref<NormalizedAreaV3[]>([])

// 根据位置分组区域
const groupAreasByPosition = (areas: NormalizedAreaV3[]) => {
  topAreas.value = areas.filter(a => a.position === 'top')
  leftAreas.value = areas.filter(a => a.position === 'left')
  rightAreas.value = areas.filter(a => a.position === 'right')
  mainAreas.value = areas.filter(a => a.position === 'main')
  bottomAreas.value = areas.filter(a => a.position === 'bottom')
  unusedAreas.value = areas.filter(a => !a.position || a.position === 'none')
}

// 监听 areas 变化
watch(
  () => props.areas,
  (areas) => {
    groupAreasByPosition(areas)
  },
  { immediate: true, deep: true }
)

// 处理拖拽放置
const handleDrop = (event: DragEvent, position: string) => {
  event.preventDefault()
  // draggable 会自动处理数据移动，这里只需要更新位置标记
  updateAreaPositions()
}

// 处理区域变化
const handleAreaChange = () => {
  updateAreaPositions()
  emitChanges()
}

// 更新区域位置标记
const updateAreaPositions = () => {
  const updatePosition = (areas: NormalizedAreaV3[], position: string) => {
    areas.forEach(area => {
      area.position = position as any
    })
  }

  updatePosition(topAreas.value, 'top')
  updatePosition(leftAreas.value, 'left')
  updatePosition(rightAreas.value, 'right')
  updatePosition(mainAreas.value, 'main')
  updatePosition(bottomAreas.value, 'bottom')
  updatePosition(unusedAreas.value, 'none')
}

// 触发更新事件
const emitChanges = () => {
  const allAreas = [
    ...topAreas.value,
    ...leftAreas.value,
    ...rightAreas.value,
    ...mainAreas.value,
    ...bottomAreas.value,
    ...unusedAreas.value
  ]
  emit('update:areas', allAreas)
}

// 移除区域
const removeArea = (areaId: string) => {
  const removeFrom = (list: NormalizedAreaV3[]) => {
    const index = list.findIndex(a => a.id === areaId)
    if (index > -1) {
      list.splice(index, 1)
    }
  }

  removeFrom(topAreas.value)
  removeFrom(leftAreas.value)
  removeFrom(rightAreas.value)
  removeFrom(mainAreas.value)
  removeFrom(bottomAreas.value)
  removeFrom(unusedAreas.value)

  emitChanges()
}

// 重置布局
const handleReset = () => {
  groupAreasByPosition(props.areas)
}

// 保存布局
const handleSave = () => {
  emitChanges()
  ElMessage.success('布局已应用')
}
</script>

<script lang="ts">
import { ElMessage } from 'element-plus'

export default {
  name: 'VisualLayoutEditor'
}
</script>

<style scoped>
.visual-layout-editor {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-radius: 4px;
  border-bottom: 1px solid #e4e7ed;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.editor-canvas {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  overflow-y: auto;
}

.drop-zone {
  background: #fff;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  min-height: 80px;
  transition: all 0.3s;
}

.drop-zone:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.drop-zone-middle {
  display: flex;
  gap: 12px;
  flex: 1;
  min-height: 200px;
}

.drop-zone-left,
.drop-zone-right {
  width: 200px;
  flex-shrink: 0;
}

.drop-zone-main {
  flex: 1;
}

.zone-label {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-size: 13px;
  font-weight: 500;
  color: #606266;
}

.zone-content {
  padding: 12px;
  min-height: 60px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.unused-areas {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}

.unused-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 12px;
}

.unused-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.unused-content {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
