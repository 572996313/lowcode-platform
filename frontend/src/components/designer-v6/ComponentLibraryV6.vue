<template>
  <div class="component-library">
    <el-tabs v-model="activeTab" class="library-tabs">
      <!-- 工具栏按钮 -->
      <el-tab-pane label="工具栏按钮" name="button">
        <div class="component-list">
          <div
            v-for="btn in toolbarButtons"
            :key="btn.type"
            class="component-item"
            draggable="true"
            @dragstart="(e) => handleDragStart(e, btn, 'button')"
          >
            <el-button :type="btn.type" :icon="btn.icon" size="small">
              {{ btn.label }}
            </el-button>
            <span class="item-label">{{ btn.label }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- 查询字段 -->
      <el-tab-pane label="查询字段" name="field">
        <div class="component-list">
          <div
            v-for="field in searchFields"
            :key="field.type"
            class="component-item"
            draggable="true"
            @dragstart="(e) => handleDragStart(e, field, 'field')"
          >
            <div class="field-icon">{{ field.icon }}</div>
            <span class="item-label">{{ field.label }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- 表格列 -->
      <el-tab-pane label="表格列" name="column">
        <div class="component-list">
          <div
            v-for="col in tableColumns"
            :key="col.type"
            class="component-item"
            draggable="true"
            @dragstart="(e) => handleDragStart(e, col, 'column')"
          >
            <div class="column-icon">{{ col.icon }}</div>
            <span class="item-label">{{ col.label }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- 行操作 -->
      <el-tab-pane label="行操作" name="rowAction">
        <div class="component-list">
          <div
            v-for="action in rowActions"
            :key="action.type"
            class="component-item"
            draggable="true"
            @dragstart="(e) => handleDragStart(e, action, 'rowAction')"
          >
            <el-button :type="action.btnType" size="small">
              {{ action.label }}
            </el-button>
            <span class="item-label">{{ action.label }}</span>
          </div>
        </div>
      </el-tab-pane>

      <!-- 模板库 -->
      <el-tab-pane label="模板库" name="template">
        <el-tabs v-model="templateTab" type="border-card" class="template-tabs">
          <!-- 按钮模板 -->
          <el-tab-pane label="按钮模板" name="button">
            <div v-loading="loadingButtons" class="template-list">
              <div
                v-for="btn in buttonTemplates"
                :key="btn.id"
                class="template-item"
                draggable="true"
                @dragstart="(e) => handleTemplateDragStart(e, btn, 'button')"
              >
                <el-button :type="btn.buttonType || 'default'" size="small">
                  {{ btn.buttonName }}
                </el-button>
                <div class="template-info">
                  <div class="template-name">{{ btn.buttonName }}</div>
                  <div class="template-code">{{ btn.buttonCode }}</div>
                  <el-tag v-if="btn.componentCategory" :type="btn.componentCategory === 'common' ? 'info' : 'success'" size="small">
                    {{ btn.componentCategory === 'common' ? '通用' : '业务' }}
                  </el-tag>
                </div>
              </div>
              <el-empty v-if="!loadingButtons && buttonTemplates.length === 0" description="暂无按钮模板" />
            </div>
          </el-tab-pane>

          <!-- 列模板 -->
          <el-tab-pane label="列模板" name="column">
            <div class="template-list">
              <div
                v-for="col in columnTemplates"
                :key="col.id"
                class="template-item"
                draggable="true"
                @dragstart="(e) => handleTemplateDragStart(e, col, 'column')"
              >
                <div class="column-preview">{{ col.label }}</div>
                <div class="template-info">
                  <div class="template-name">{{ col.label }}</div>
                  <div class="template-code">{{ col.columnCode }}</div>
                  <el-tag :type="getColumnTypeColor(col.columnType)" size="small">
                    {{ col.columnType }}
                  </el-tag>
                </div>
              </div>
              <el-empty v-if="columnTemplates.length === 0" description="暂无列模板" />
            </div>
          </el-tab-pane>

          <!-- 字段模板 -->
          <el-tab-pane label="字段模板" name="field">
            <div class="template-list">
              <div
                v-for="field in fieldTemplates"
                :key="field.id"
                class="template-item"
                draggable="true"
                @dragstart="(e) => handleTemplateDragStart(e, field, 'field')"
              >
                <div class="field-preview">{{ field.label }}</div>
                <div class="template-info">
                  <div class="template-name">{{ field.label }}</div>
                  <div class="template-code">{{ field.fieldCode }}</div>
                  <el-tag :type="getFieldTypeColor(field.fieldType)" size="small">
                    {{ field.fieldType }}
                  </el-tag>
                </div>
              </div>
              <el-empty v-if="fieldTemplates.length === 0" description="暂无字段模板" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Plus, Edit, Delete, Download, Refresh,
  Search, Calendar, Select, DocumentChecked,
  Switch, Edit as EditIcon, View, Picture
} from '@element-plus/icons-vue'
import type { DragData, TemplateDragData } from '@/types/page-v6'
import { generateId } from '@/types/page-v6'
import { getButtonLibrary } from '@/api/library'
import type { ButtonConfig } from '@/api/button'
import { ElMessage } from 'element-plus'

const activeTab = ref('button')
const templateTab = ref('button')

// 模板数据
const buttonTemplates = ref<ButtonConfig[]>([])
const columnTemplates = ref<any[]>([])
const fieldTemplates = ref<any[]>([])
const loadingButtons = ref(false)

// 加载按钮模板
onMounted(async () => {
  await loadButtonTemplates()
  loadColumnTemplates()
  loadFieldTemplates()
})

async function loadButtonTemplates() {
  loadingButtons.value = true
  try {
    // 加载通用按钮库
    const commonButtons = await getButtonLibrary('common')
    // 加载业务按钮库
    const businessButtons = await getButtonLibrary('business')
    buttonTemplates.value = [...commonButtons, ...businessButtons]
  } catch (error: any) {
    ElMessage.error('加载按钮模板失败: ' + (error.message || '未知错误'))
  } finally {
    loadingButtons.value = false
  }
}

// 加载示例列模板
function loadColumnTemplates() {
  columnTemplates.value = [
    { id: 101, label: 'ID', columnCode: 'id', columnType: 'number', width: 80, align: 'center' },
    { id: 102, label: '创建时间', columnCode: 'createTime', columnType: 'datetime', width: 160 },
    { id: 103, label: '状态', columnCode: 'status', columnType: 'tag', width: 100 },
    { id: 104, label: '操作', columnCode: 'actions', columnType: 'text', width: 150 }
  ]
}

// 加载示例字段模板
function loadFieldTemplates() {
  fieldTemplates.value = [
    { id: 201, label: '名称', fieldCode: 'name', fieldType: 'input', required: true },
    { id: 202, label: '状态', fieldCode: 'status', fieldType: 'select', required: false },
    { id: 203, label: '创建时间', fieldCode: 'createTime', fieldType: 'datetime', required: false }
  ]
}

// 工具栏按钮列表
const toolbarButtons = [
  { type: 'primary', label: '新增', icon: Plus, actionType: 'add' },
  { type: 'success', label: '编辑', icon: Edit, actionType: 'edit' },
  { type: 'danger', label: '删除', icon: Delete, actionType: 'delete' },
  { type: 'info', label: '导出', icon: Download, actionType: 'export' },
  { type: 'default', label: '刷新', icon: Refresh, actionType: 'refresh' }
]

// 查询字段列表
const searchFields = [
  { type: 'input', label: '输入框', icon: '📝' },
  { type: 'textarea', label: '文本域', icon: '📄' },
  { type: 'select', label: '下拉框', icon: '🔍' },
  { type: 'date', label: '日期选择', icon: '📅' },
  { type: 'datetime', label: '日期时间', icon: '🕐' },
  { type: 'number', label: '数字输入', icon: '🔢' },
  { type: 'switch', label: '开关', icon: '🔘' }
]

// 表格列列表
const tableColumns = [
  { type: 'text', label: '文本列', icon: '📝' },
  { type: 'number', label: '数字列', icon: '🔢' },
  { type: 'date', label: '日期列', icon: '📅' },
  { type: 'tag', label: '标签列', icon: '🏷️' },
  { type: 'link', label: '链接列', icon: '🔗' },
  { type: 'image', label: '图片列', icon: '📷' },
  { type: 'switch', label: '开关列', icon: '🔘' },
  { type: 'progress', label: '进度条', icon: '📊' }
]

// 行操作按钮列表
const rowActions = [
  { btnType: 'primary', label: '编辑', actionType: 'edit' },
  { btnType: 'danger', label: '删除', actionType: 'delete' },
  { btnType: 'default', label: '查看', actionType: 'view' }
]

/**
 * 处理组件库拖拽开始
 */
function handleDragStart(e: DragEvent, item: any, itemType: string) {
  const dragData: DragData = {
    source: 'component-library',
    itemType,
    itemSubType: item.type || item.actionType,
    defaultConfig: createDefaultConfig(item, itemType)
  }

  // 设置拖拽数据
  const dataStr = JSON.stringify(dragData)
  if (e.dataTransfer) {
    e.dataTransfer.setData('application/json', dataStr)
    e.dataTransfer.setData('text/plain', dataStr)
    e.dataTransfer.effectAllowed = 'copy'
  }
}

/**
 * 处理模板库拖拽开始
 */
function handleTemplateDragStart(e: DragEvent, template: any, itemType: string) {
  // 构建模板拖拽数据
  const dragData: TemplateDragData = {
    source: 'template-library',
    itemType,
    templateId: template.id,
    templateName: template.buttonName || template.columnName || template.fieldName,
    templateConfig: template,
    defaultConfig: createTemplateDefaultConfig(template, itemType)
  }

  // 设置拖拽数据
  const dataStr = JSON.stringify(dragData)
  if (e.dataTransfer) {
    e.dataTransfer.setData('application/json', dataStr)
    e.dataTransfer.setData('text/plain', dataStr)
    e.dataTransfer.effectAllowed = 'copy'
  }
}

/**
 * 创建默认配置（组件库）
 */
function createDefaultConfig(item: any, itemType: string) {
  switch (itemType) {
    case 'button':
      return {
        id: generateId('button'),
        name: item.label,
        type: item.type,
        icon: item.icon,
        visible: true,
        disabled: false,
        action: {
          type: item.actionType,
          confirmMessage: item.actionType === 'delete' ? '确定要删除吗？' : undefined,
          successMessage: '操作成功'
        }
      }

    case 'field':
      return {
        id: generateId('field'),
        fieldCode: `field_${item.type}`,
        label: item.label,
        fieldType: item.type,
        placeholder: `请输入${item.label}`,
        required: false,
        span: 6
      }

    case 'column':
      return {
        id: generateId('column'),
        prop: `column_${item.type}`,
        label: item.label,
        type: item.type,
        width: 150,
        align: 'left',
        showOverflowTooltip: true
      }

    case 'rowAction':
      return {
        id: generateId('rowAction'),
        name: item.label,
        type: item.btnType,
        visible: true,
        action: {
          type: item.actionType,
          confirmMessage: item.actionType === 'delete' ? '确定要删除吗？' : undefined
        }
      }

    default:
      return {}
  }
}

/**
 * 创建模板默认配置（模板库）
 */
function createTemplateDefaultConfig(template: any, itemType: string) {
  switch (itemType) {
    case 'button':
      return {
        id: generateId('button'),
        name: template.buttonName,
        type: template.buttonType || 'default',
        icon: template.icon,
        visible: template.visible !== false,
        disabled: template.disabled || false,
        action: {
          type: template.actionType || 'custom',
          apiEndpoint: '',
          confirmMessage: '',
          successMessage: ''
        }
      }

    case 'column':
      return {
        id: generateId('column'),
        prop: template.columnCode,
        label: template.label,
        type: template.columnType || 'text',
        width: template.width || 150,
        align: template.align || 'left',
        showOverflowTooltip: true
      }

    case 'field':
      return {
        id: generateId('field'),
        fieldCode: template.fieldCode,
        label: template.label,
        fieldType: template.fieldType || 'input',
        placeholder: `请输入${template.label}`,
        required: template.required || false,
        span: 6
      }

    default:
      return {}
  }
}

/**
 * 获取列类型颜色
 */
function getColumnTypeColor(type: string) {
  const colorMap: Record<string, string> = {
    text: '',
    number: 'info',
    date: 'warning',
    datetime: 'warning',
    tag: 'success',
    link: 'primary',
    image: 'info',
    switch: 'success'
  }
  return colorMap[type] || ''
}

/**
 * 获取字段类型颜色
 */
function getFieldTypeColor(type: string) {
  const colorMap: Record<string, string> = {
    input: '',
    textarea: 'info',
    select: 'success',
    radio: 'success',
    checkbox: 'success',
    date: 'warning',
    datetime: 'warning',
    number: 'info',
    switch: 'success'
  }
  return colorMap[type] || ''
}
</script>

<style scoped lang="scss">
.component-library {
  background: #fff;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: #f5f7fa;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }

  :deep(.el-tabs__content) {
    padding: 16px 20px;
    max-height: 200px;
    overflow-y: auto;
  }

  .component-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .component-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px;
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: move;
    transition: all 0.3s;

    &:hover {
      background: #ecf5ff;
      border-color: #409eff;
      transform: translateY(-2px);
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    &:active {
      cursor: grabbing;
    }

    .field-icon,
    .column-icon {
      font-size: 24px;
      margin-bottom: 8px;
    }

    .item-label {
      font-size: 12px;
      color: #606266;
    }
  }

  // 模板库样式
  .template-tabs {
    :deep(.el-tabs__content) {
      max-height: 200px;
      padding: 12px;
    }
  }

  .template-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    max-height: 200px;
    overflow-y: auto;
  }

  .template-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px;
    background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
    border: 1px solid #c6e2ff;
    border-radius: 6px;
    cursor: move;
    transition: all 0.3s;
    min-width: 120px;

    &:hover {
      background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
      border-color: #409eff;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    }

    &:active {
      cursor: grabbing;
    }

    .column-preview,
    .field-preview {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 8px;
      padding: 8px 12px;
      background: #fff;
      border-radius: 4px;
      border: 1px dashed #dcdfe6;
    }

    .template-info {
      text-align: center;
      margin-top: 8px;
    }

    .template-name {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }

    .template-code {
      font-size: 11px;
      color: #909399;
      margin-bottom: 6px;
    }
  }
}
</style>
