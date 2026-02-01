<template>
  <div class="property-panel">
    <div class="panel-header">
      <h3>组件属性</h3>
    </div>

    <div v-if="!selectedComponent" class="empty-state">
      <el-icon class="empty-icon"><Setting /></el-icon>
      <p>请先选择一个组件</p>
    </div>

    <div v-else class="property-content">
      <!-- 基本信息 -->
      <div class="property-section">
        <div class="section-title">基本信息</div>
        <el-form label-width="80px" size="small">
          <el-form-item label="组件ID">
            <el-input v-model="selectedComponent.id" disabled />
          </el-form-item>
          <el-form-item label="组件名称">
            <el-input v-model="selectedComponent.name" />
          </el-form-item>
          <el-form-item label="组件类型">
            <el-tag :type="getTypeTagType(selectedComponent.type)">
              {{ getTypeLabel(selectedComponent.type) }}
            </el-tag>
          </el-form-item>
          <el-form-item label="引用ID">
            <el-input v-model="selectedComponent.refId" disabled />
          </el-form-item>
        </el-form>
      </div>

      <!-- 自定义属性 -->
      <div class="property-section">
        <div class="section-title">自定义属性</div>
        <el-form label-width="80px" size="small">
          <el-form-item
            v-for="(value, key) in selectedComponent.props"
            :key="key"
            :label="key"
          >
            <el-input
              v-if="typeof value === 'string'"
              :model-value="value"
              @update:model-value="updateProperty(key, $event)"
            />
            <el-input-number
              v-else-if="typeof value === 'number'"
              :model-value="value"
              @update:model-value="updateProperty(key, $event)"
            />
            <el-switch
              v-else-if="typeof value === 'boolean'"
              :model-value="value"
              @update:model-value="updateProperty(key, $event)"
            />
          </el-form-item>

          <!-- 添加新属性 -->
          <el-form-item label="新增属性">
            <div style="display: flex; gap: 8px; width: 100%;">
              <el-input
                v-model="newPropKey"
                placeholder="属性名"
                @keyup.enter="addNewProperty"
              />
              <el-button
                type="primary"
                :disabled="!newPropKey"
                @click="addNewProperty"
              >
                添加
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <!-- 自定义样式 -->
      <div class="property-section">
        <div class="section-title">自定义样式</div>
        <el-form label-width="80px" size="small">
          <el-form-item
            v-for="(value, key) in selectedComponent.style"
            :key="key"
            :label="key"
          >
            <el-input
              :model-value="value"
              @update:model-value="updateStyle(key, $event)"
            />
          </el-form-item>

          <!-- 添加新样式 -->
          <el-form-item label="新增样式">
            <div style="display: flex; gap: 8px; width: 100%;">
              <el-input
                v-model="newStyleKey"
                placeholder="样式名"
                @keyup.enter="addNewStyle"
              />
              <el-button
                type="primary"
                :disabled="!newStyleKey"
                @click="addNewStyle"
              >
                添加
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Setting } from '@element-plus/icons-vue'
import type { ComponentRef } from '@/types/template'

const props = defineProps<{
  selectedComponent: ComponentRef | null
}>()

const emit = defineEmits<{
  update: [props: Record<string, any>]
}>()

const newPropKey = ref('')
const newStyleKey = ref('')

// 获取类型标签颜色
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    form: 'info',
    table: 'success',
    button: 'warning',
    tree: 'danger'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签文本
const getTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    form: '表单',
    table: '表格',
    button: '按钮',
    tree: '树形'
  }
  return typeMap[type] || type
}

// 更新属性
const updateProperty = (key: string, value: any) => {
  if (props.selectedComponent) {
    if (!props.selectedComponent.props) {
      props.selectedComponent.props = {}
    }
    props.selectedComponent.props[key] = value
    emit('update', props.selectedComponent.props)
  }
}

// 添加新属性
const addNewProperty = () => {
  if (!newPropKey.value || !props.selectedComponent) return

  if (!props.selectedComponent.props) {
    props.selectedComponent.props = {}
  }

  props.selectedComponent.props[newPropKey.value] = ''
  newPropKey.value = ''
  emit('update', props.selectedComponent.props)
}

// 更新样式
const updateStyle = (key: string, value: any) => {
  if (props.selectedComponent) {
    if (!props.selectedComponent.style) {
      props.selectedComponent.style = {}
    }
    props.selectedComponent.style[key] = value
    emit('update', props.selectedComponent.style)
  }
}

// 添加新样式
const addNewStyle = () => {
  if (!newStyleKey.value || !props.selectedComponent) return

  if (!props.selectedComponent.style) {
    props.selectedComponent.style = {}
  }

  props.selectedComponent.style[newStyleKey.value] = ''
  newStyleKey.value = ''
  emit('update', props.selectedComponent.style)
}
</script>

<style scoped>
.property-panel {
  width: 320px;
  background-color: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.property-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.property-section {
  margin-bottom: 24px;
}

.property-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-form-item) {
  margin-bottom: 12px;
}

:deep(.el-form-item__label) {
  font-size: 12px;
  color: #606266;
}
</style>
