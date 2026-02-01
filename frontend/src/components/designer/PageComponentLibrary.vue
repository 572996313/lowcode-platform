<template>
  <div class="page-component-library">
    <div class="panel-header">
      <h3>组件库</h3>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <el-tabs v-else v-model="activeTab">
      <el-tab-pane label="表单配置" name="forms">
        <div class="component-list">
          <div
            v-for="form in forms"
            :key="form.id"
            class="component-item"
            draggable="true"
            @dragstart="handleDragStart(form, 'form', $event)"
          >
            <el-icon class="component-icon"><Document /></el-icon>
            <div class="component-info">
              <div class="component-name">{{ form.formName }}</div>
              <div class="component-code">{{ form.formCode }}</div>
            </div>
          </div>

          <el-empty
            v-if="forms.length === 0"
            description="暂无表单配置"
            :image-size="80"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="表格配置" name="tables">
        <div class="component-list">
          <div
            v-for="table in tables"
            :key="table.id"
            class="component-item"
            draggable="true"
            @dragstart="handleDragStart(table, 'table', $event)"
          >
            <el-icon class="component-icon"><Grid /></el-icon>
            <div class="component-info">
              <div class="component-name">{{ table.tableName }}</div>
              <div class="component-code">{{ table.tableCode }}</div>
            </div>
          </div>

          <el-empty
            v-if="tables.length === 0"
            description="暂无表格配置"
            :image-size="80"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="按钮配置" name="buttons">
        <div class="component-list">
          <div
            v-for="btn in buttons"
            :key="btn.id"
            class="component-item"
            draggable="true"
            @dragstart="handleDragStart(btn, 'button', $event)"
          >
            <el-icon class="component-icon"><Pointer /></el-icon>
            <div class="component-info">
              <div class="component-name">{{ btn.buttonName }}</div>
              <div class="component-code">{{ btn.buttonCode }}</div>
            </div>
          </div>

          <el-empty
            v-if="buttons.length === 0"
            description="暂无按钮配置"
            :image-size="80"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Grid, Pointer, Loading } from '@element-plus/icons-vue'
import type { ComponentRef, ButtonConfig } from '@/types/template'

// API 接口
import { getFormList } from '@/api/form'
import { getTableList } from '@/api/table'
import { getButtonList } from '@/api/button'

const activeTab = ref('forms')
const loading = ref(false)

interface FormConfig {
  id?: number
  formName: string
  formCode: string
  formType?: string
}

interface TableConfig {
  id?: number
  tableName: string
  tableCode: string
}

const forms = ref<FormConfig[]>([])
const tables = ref<TableConfig[]>([])
const buttons = ref<ButtonConfig[]>([])

const emit = defineEmits<{
  'drag-start': [component: ComponentRef, e: DragEvent]
}>()

// 加载表单配置列表
const loadForms = async () => {
  try {
    const res = await getFormList({ current: 1, size: 999 })
    forms.value = res.records || []
  } catch (error) {
    console.error('加载表单列表失败:', error)
    ElMessage.error('加载表单列表失败')
  }
}

// 加载表格配置列表
const loadTables = async () => {
  try {
    const res = await getTableList({ current: 1, size: 999 })
    tables.value = res.records || []
  } catch (error) {
    console.error('加载表格列表失败:', error)
    ElMessage.error('加载表格列表失败')
  }
}

// 加载按钮配置列表
const loadButtons = async () => {
  try {
    const res = await getButtonList({ current: 1, size: 999 })
    buttons.value = res.records || []
  } catch (error) {
    console.error('加载按钮列表失败:', error)
    ElMessage.error('加载按钮列表失败')
  }
}

// 处理拖拽开始
const handleDragStart = (config: FormConfig | TableConfig | ButtonConfig, type: string, e: DragEvent) => {
  if (!config.id) {
    ElMessage.warning('组件ID不存在')
    return
  }

  const component: ComponentRef = {
    id: `comp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    type: type as 'form' | 'table' | 'button',
    refId: config.id!,
    name: type === 'form'
      ? (config as FormConfig).formName
      : type === 'table'
        ? (config as TableConfig).tableName
        : (config as ButtonConfig).buttonName
  }

  e.dataTransfer!.setData('application/json', JSON.stringify(component))
  e.dataTransfer!.effectAllowed = 'copy'

  emit('drag-start', component, e)
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadForms(), loadTables(), loadButtons()])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-component-library {
  width: 280px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  height: 100%;
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

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  gap: 8px;
}

.component-list {
  padding: 16px;
  max-height: calc(100vh - 250px);
  overflow-y: auto;
}

.component-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: grab;
  transition: all 0.2s;
}

.component-item:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.component-item:active {
  cursor: grabbing;
}

.component-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 12px;
  flex-shrink: 0;
}

.component-info {
  flex: 1;
  min-width: 0;
}

.component-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.component-code {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
