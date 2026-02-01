<template>
  <div class="table-designer" v-loading="loading">
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <span class="title">表格设计器{{ tableId ? ' - 编辑模式' : ' - 新建模式' }}</span>
      </div>
      <div class="header-right">
        <el-button @click="handlePreview">
          <el-icon><View /></el-icon>预览
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          <el-icon><Check /></el-icon>保存
        </el-button>
      </div>
    </div>

    <div class="designer-body">
      <!-- 左侧配置 -->
      <div class="config-panel">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基础配置" name="basic">
            <el-form label-width="100px" size="small">
              <el-form-item label="表格名称">
                <el-input v-model="tableConfig.tableName" placeholder="请输入表格名称" />
              </el-form-item>
              <el-form-item label="表格编码">
                <el-input v-model="tableConfig.tableCode" placeholder="请输入表格编码" />
              </el-form-item>
              <el-form-item label="组件分类">
                <el-radio-group v-model="tableConfig.componentCategory">
                  <el-radio label="common">通用组件库</el-radio>
                  <el-radio label="business">业务组件库</el-radio>
                </el-radio-group>
                <div class="form-tip">
                  通用组件可在所有页面复用，业务组件仅用于特定业务场景
                </div>
              </el-form-item>
              <el-form-item label="组件标签">
                <el-input
                  v-model="tableConfig.componentTags"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入标签，用逗号分隔（如：system,list,report）"
                />
              </el-form-item>
              <el-form-item label="数据接口">
                <el-input v-model="tableConfig.apiUrl" placeholder="请输入API地址" />
              </el-form-item>
              <el-form-item label="请求方式">
                <el-select v-model="tableConfig.apiMethod" style="width: 100%">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                </el-select>
              </el-form-item>
              <el-form-item label="是否分页">
                <el-switch v-model="tableConfig.pagination" />
              </el-form-item>
              <el-form-item label="每页条数" v-if="tableConfig.pagination">
                <el-input-number v-model="tableConfig.pageSize" :min="5" :max="100" />
              </el-form-item>
              <el-form-item label="显示序号">
                <el-switch v-model="tableConfig.showIndex" />
              </el-form-item>
              <el-form-item label="显示多选">
                <el-switch v-model="tableConfig.selection" />
              </el-form-item>
              <el-form-item label="显示边框">
                <el-switch v-model="tableConfig.border" />
              </el-form-item>
              <el-form-item label="斑马纹">
                <el-switch v-model="tableConfig.stripe" />
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="列配置" name="columns">
            <div class="column-list">
              <div class="column-header">
                <span>列配置</span>
                <el-dropdown @command="handleAddColumnType">
                  <el-button type="primary" size="small">
                    <el-icon><Plus /></el-icon>添加列
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="text">普通文本</el-dropdown-item>
                      <el-dropdown-item command="image">图片</el-dropdown-item>
                      <el-dropdown-item command="tag">标签</el-dropdown-item>
                      <el-dropdown-item command="datetime">日期时间</el-dropdown-item>
                      <el-dropdown-item command="date">日期</el-dropdown-item>
                      <el-dropdown-item command="switch">开关</el-dropdown-item>
                      <el-dropdown-item command="link">链接</el-dropdown-item>
                      <el-dropdown-item command="progress">进度条</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              <div
                v-for="(column, index) in tableConfig.columns"
                :key="column.id"
                class="column-item"
                :class="{ active: selectedColumn?.id === column.id }"
                @click="selectColumn(column)"
              >
                <div class="column-info">
                  <span class="column-label">{{ column.label }}</span>
                  <span class="column-code">{{ column.columnCode || column.prop }}</span>
                  <el-tag v-if="column.type" size="small" type="info">{{ getColumnTypeLabel(column.type) }}</el-tag>
                </div>
                <div class="column-actions">
                  <el-icon @click.stop="moveColumnUp(index)" v-if="index > 0"><Top /></el-icon>
                  <el-icon @click.stop="moveColumnDown(index)" v-if="index < tableConfig.columns.length - 1"><Bottom /></el-icon>
                  <el-icon @click.stop="removeColumn(index)"><Delete /></el-icon>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 中间预览区域 -->
      <div class="preview-panel">
        <div class="panel-title">预览效果</div>
        <div class="preview-area">
          <el-table
            :data="mockData"
            :border="tableConfig.border"
            :stripe="tableConfig.stripe"
            style="width: 100%"
          >
            <el-table-column v-if="tableConfig.selection" type="selection" width="55" />
            <el-table-column v-if="tableConfig.showIndex" type="index" label="序号" width="60" />
            <el-table-column
              v-for="column in tableConfig.columns"
              :key="column.id"
              :prop="column.columnCode || column.prop"
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :sortable="column.sortable"
              :fixed="column.fixed"
              :align="column.align"
            >
              <template #default="{ row }">
                <!-- 预览图片 -->
                <template v-if="column.type === 'image' && column.imageConfig">
                  <el-image
                    v-if="row[column.prop]"
                    :src="row[column.prop]"
                    :style="{
                      width: `${column.imageConfig.width}px`,
                      height: `${column.imageConfig.height}px`,
                      borderRadius: column.imageConfig.radius
                    }"
                    :fit="column.imageConfig.fit"
                  />
                  <span v-else>-</span>
                </template>
                <!-- 预览标签 -->
                <template v-else-if="column.type === 'tag' && column.tagConfig">
                  <el-tag :type="getPreviewTagType(row[column.prop], column)">
                    {{ getPreviewTagText(row[column.prop], column) }}
                  </el-tag>
                </template>
                <!-- 预览开关 -->
                <template v-else-if="column.type === 'switch'">
                  <el-switch :model-value="!!row[column.prop]" disabled />
                </template>
                <!-- 预览日期时间 -->
                <template v-else-if="column.type === 'datetime' || column.type === 'date'">
                  {{ row[column.prop] || '-' }}
                </template>
                <!-- 预览进度条 -->
                <template v-else-if="column.type === 'progress'">
                  <el-progress :percentage="Number(row[column.prop]) || 0" />
                </template>
                <!-- 普通文本 -->
                <template v-else>
                  {{ row[column.columnCode || column.prop] || '-' }}
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="tableConfig.pagination"
            :page-size="tableConfig.pageSize"
            :total="100"
            layout="total, sizes, prev, pager, next"
            style="margin-top: 16px; justify-content: flex-end;"
          />
        </div>
      </div>

      <!-- 右侧属性配置 -->
      <div class="property-panel">
        <div class="panel-title">列属性</div>
        <div class="property-content">
          <template v-if="selectedColumn">
            <el-form label-width="80px" size="small">
              <el-form-item label="列类型">
                <el-select v-model="selectedColumn.type" style="width: 100%">
                  <el-option label="普通文本" value="text" />
                  <el-option label="图片" value="image" />
                  <el-option label="标签" value="tag" />
                  <el-option label="日期时间" value="datetime" />
                  <el-option label="日期" value="date" />
                  <el-option label="开关" value="switch" />
                  <el-option label="链接" value="link" />
                  <el-option label="进度条" value="progress" />
                </el-select>
              </el-form-item>

              <el-form-item label="列标题">
                <el-input v-model="selectedColumn.label" />
              </el-form-item>

              <el-form-item label="字段名">
                <el-input v-model="selectedColumn.prop" placeholder="V4 格式字段名" />
              </el-form-item>

              <el-form-item label="列宽度">
                <el-input-number v-model="selectedColumn.width" :min="0" />
              </el-form-item>

              <el-form-item label="最小宽度">
                <el-input-number v-model="selectedColumn.minWidth" :min="0" />
              </el-form-item>

              <el-form-item label="对齐方式">
                <el-select v-model="selectedColumn.align" style="width: 100%">
                  <el-option label="左对齐" value="left" />
                  <el-option label="居中" value="center" />
                  <el-option label="右对齐" value="right" />
                </el-select>
              </el-form-item>

              <el-form-item label="固定列">
                <el-select v-model="selectedColumn.fixed" style="width: 100%" clearable>
                  <el-option label="左侧固定" value="left" />
                  <el-option label="右侧固定" value="right" />
                </el-select>
              </el-form-item>

              <el-form-item label="可排序">
                <el-switch v-model="selectedColumn.sortable" />
              </el-form-item>

              <el-form-item label="溢出提示">
                <el-switch v-model="selectedColumn.showOverflowTooltip" />
              </el-form-item>

              <!-- 图片配置 -->
              <template v-if="selectedColumn.type === 'image'">
                <el-divider content-position="left">图片配置</el-divider>
                <el-form-item label="图片宽度">
                  <el-input-number v-model="selectedColumn.imageConfig.width" :min="10" />
                </el-form-item>
                <el-form-item label="图片高度">
                  <el-input-number v-model="selectedColumn.imageConfig.height" :min="10" />
                </el-form-item>
                <el-form-item label="填充方式">
                  <el-select v-model="selectedColumn.imageConfig.fit" style="width: 100%">
                    <el-option label="Cover" value="cover" />
                    <el-option label="Contain" value="contain" />
                    <el-option label="Fill" value="fill" />
                    <el-option label="None" value="none" />
                    <el-option label="Scale Down" value="scale-down" />
                  </el-select>
                </el-form-item>
                <el-form-item label="圆角">
                  <el-input v-model="selectedColumn.imageConfig.radius" placeholder="如 50% 或 4px" />
                </el-form-item>
                <el-form-item label="支持预览">
                  <el-switch v-model="selectedColumn.imageConfig.preview" />
                </el-form-item>
              </template>

              <!-- 标签配置 -->
              <template v-if="selectedColumn.type === 'tag'">
                <el-divider content-position="left">标签配置</el-divider>
                <div v-for="(tag, key) in selectedColumn.tagConfig" :key="key" class="tag-config-item">
                  <el-form-item :label="`值: ${key}`">
                    <div style="display: flex; gap: 8px; align-items: center;">
                      <el-input v-model="tag.text" placeholder="显示文本" style="width: 120px;" />
                      <el-select v-model="tag.type" style="width: 100px;">
                        <el-option label="成功" value="success" />
                        <el-option label="警告" value="warning" />
                        <el-option label="危险" value="danger" />
                        <el-option label="信息" value="info" />
                        <el-option label="主要" value="primary" />
                      </el-select>
                      <el-button size="small" type="danger" @click="removeTagConfig(key)">删除</el-button>
                    </div>
                  </el-form-item>
                </div>
                <el-button size="small" @click="addTagConfig">添加标签映射</el-button>
              </template>

              <!-- 日期配置 -->
              <template v-if="selectedColumn.type === 'datetime' || selectedColumn.type === 'date'">
                <el-divider content-position="left">日期配置</el-divider>
                <el-form-item label="格式">
                  <el-select v-model="selectedColumn.dateConfig.format" style="width: 100%">
                    <el-option label="YYYY-MM-DD HH:mm:ss" value="YYYY-MM-DD HH:mm:ss" />
                    <el-option label="YYYY-MM-DD" value="YYYY-MM-DD" />
                    <el-option label="YYYY/MM/DD HH:mm:ss" value="YYYY/MM/DD HH:mm:ss" />
                    <el-option label="YYYY/MM/DD" value="YYYY/MM/DD" />
                    <el-option label="MM-DD HH:mm:ss" value="MM-DD HH:mm:ss" />
                  </el-select>
                </el-form-item>
              </template>

              <!-- 开关配置 -->
              <template v-if="selectedColumn.type === 'switch'">
                <el-divider content-position="left">开关配置</el-divider>
                <el-form-item label="激活值">
                  <el-input v-model="selectedColumn.switchConfig.activeValue" placeholder="默认: true" />
                </el-form-item>
                <el-form-item label="未激活值">
                  <el-input v-model="selectedColumn.switchConfig.inactiveValue" placeholder="默认: false" />
                </el-form-item>
                <el-form-item label="是否禁用">
                  <el-switch v-model="selectedColumn.switchConfig.disabled" />
                </el-form-item>
              </template>

              <!-- 链接配置 -->
              <template v-if="selectedColumn.type === 'link'">
                <el-divider content-position="left">链接配置</el-divider>
                <el-form-item label="打开方式">
                  <el-select v-model="selectedColumn.linkConfig.target" style="width: 100%">
                    <el-option label="_blank" value="_blank" />
                    <el-option label="_self" value="_self" />
                  </el-select>
                </el-form-item>
                <el-form-item label="链接地址">
                  <el-input v-model="selectedColumn.linkConfig.href" placeholder="支持变量 {value}" />
                </el-form-item>
              </template>

              <!-- 进度条配置 -->
              <template v-if="selectedColumn.type === 'progress'">
                <el-divider content-position="left">进度条配置</el-divider>
                <el-form-item label="进度条宽度">
                  <el-input-number v-model="selectedColumn.progressConfig.strokeWidth" :min="2" :max="20" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="selectedColumn.progressConfig.status" style="width: 100%" clearable>
                    <el-option label="成功" value="success" />
                    <el-option label="警告" value="warning" />
                    <el-option label="异常" value="exception" />
                  </el-select>
                </el-form-item>
              </template>
            </el-form>
          </template>
          <div v-else class="empty-tip">
            <p>请选择一个列进行配置</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createTable, updateTable, getTableConfig, type TableConfig, type TableColumn as ApiTableColumn } from '@/api/table'
import type { TableColumnV4, ImageConfig, TagConfig, DateConfig, SwitchConfig, LinkConfig, ProgressConfig } from '@/types/table'

interface DesignerColumn {
  id: string
  type: string
  label: string
  prop?: string
  columnCode?: string  // 兼容旧格式
  width?: number
  minWidth?: number
  align?: string
  fixed?: string
  sortable?: boolean
  showOverflowTooltip?: boolean
  visible?: boolean
  imageConfig?: ImageConfig
  tagConfig?: TagConfig
  dateConfig?: DateConfig
  switchConfig?: SwitchConfig
  linkConfig?: LinkConfig
  progressConfig?: ProgressConfig
}

const route = useRoute()
const router = useRouter()
const tableId = ref<number | null>(route.query.id ? Number(route.query.id) : null)

const activeTab = ref('basic')
const selectedColumn = ref<DesignerColumn | null>(null)
const loading = ref(false)

const tableConfig = reactive({
  tableName: '',
  tableCode: '',
  componentCategory: 'business' as 'common' | 'business',
  componentTags: '' as string,
  apiUrl: '',
  apiMethod: 'GET',
  pagination: true,
  pageSize: 10,
  showIndex: true,
  selection: false,
  border: true,
  stripe: true,
  columns: [] as DesignerColumn[]
})

// 模拟数据
const mockData = ref([
  { id: 1, name: '张三', age: 28, email: 'zhangsan@example.com', status: '1', avatar: 'https://via.placeholder.com/40', progress: 75 },
  { id: 2, name: '李四', age: 32, email: 'lisi@example.com', status: '0', avatar: 'https://via.placeholder.com/40', progress: 50 },
  { id: 3, name: '王五', age: 25, email: 'wangwu@example.com', status: '1', avatar: 'https://via.placeholder.com/40', progress: 90 }
])

// 获取列类型标签
const getColumnTypeLabel = (type: string): string => {
  const labels: Record<string, string> = {
    text: '文本',
    image: '图片',
    tag: '标签',
    datetime: '日期时间',
    date: '日期',
    switch: '开关',
    link: '链接',
    progress: '进度条'
  }
  return labels[type] || type
}

// 添加指定类型的列
const handleAddColumnType = (type: string) => {
  addColumn(type)
}

const addColumn = (type: string = 'text') => {
  const newColumn: DesignerColumn = {
    id: Date.now().toString(),
    type,
    label: `列${tableConfig.columns.length + 1}`,
    prop: `column_${tableConfig.columns.length + 1}`,
    columnCode: `column_${tableConfig.columns.length + 1}`,
    align: 'left',
    sortable: false,
    visible: true,
    showOverflowTooltip: false
  }

  // 根据类型添加默认配置
  if (type === 'image') {
    newColumn.imageConfig = {
      width: 40,
      height: 40,
      fit: 'cover',
      radius: '4px',
      preview: true
    }
    newColumn.width = 80
  } else if (type === 'tag') {
    newColumn.tagConfig = {
      '1': { text: '启用', type: 'success' },
      '0': { text: '禁用', type: 'info' }
    }
    newColumn.width = 100
  } else if (type === 'datetime' || type === 'date') {
    newColumn.dateConfig = {
      format: type === 'datetime' ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD'
    }
    newColumn.width = 180
  } else if (type === 'switch') {
    newColumn.switchConfig = {
      activeValue: true,
      inactiveValue: false,
      disabled: false
    }
    newColumn.width = 80
    newColumn.align = 'center'
  } else if (type === 'link') {
    newColumn.linkConfig = {
      target: '_blank',
      href: '{value}'
    }
  } else if (type === 'progress') {
    newColumn.progressConfig = {
      strokeWidth: 6
    }
    newColumn.width = 150
  }

  tableConfig.columns.push(newColumn)
  selectedColumn.value = newColumn
}

const selectColumn = (column: DesignerColumn) => {
  selectedColumn.value = column
  activeTab.value = 'columns'
}

const removeColumn = (index: number) => {
  const removed = tableConfig.columns.splice(index, 1)[0]
  if (selectedColumn.value?.id === removed.id) {
    selectedColumn.value = null
  }
}

const moveColumnUp = (index: number) => {
  if (index > 0) {
    const temp = tableConfig.columns[index]
    tableConfig.columns[index] = tableConfig.columns[index - 1]
    tableConfig.columns[index - 1] = temp
  }
}

const moveColumnDown = (index: number) => {
  if (index < tableConfig.columns.length - 1) {
    const temp = tableConfig.columns[index]
    tableConfig.columns[index] = tableConfig.columns[index + 1]
    tableConfig.columns[index + 1] = temp
  }
}

// 添加标签配置
const addTagConfig = () => {
  if (!selectedColumn.value) return
  if (!selectedColumn.value.tagConfig) {
    selectedColumn.value.tagConfig = {}
  }
  const key = `value_${Object.keys(selectedColumn.value.tagConfig).length + 1}`
  selectedColumn.value.tagConfig[key] = { text: '标签文本', type: 'info' }
}

// 删除标签配置
const removeTagConfig = (key: string) => {
  if (!selectedColumn.value?.tagConfig) return
  delete selectedColumn.value.tagConfig[key]
}

// 预览获取标签类型
const getPreviewTagType = (value: any, column: DesignerColumn): string => {
  if (!column.tagConfig) return 'info'
  const config = column.tagConfig[value]
  return config?.type || 'info'
}

// 预览获取标签文本
const getPreviewTagText = (value: any, column: DesignerColumn): string => {
  if (!column.tagConfig) return value || '-'
  const config = column.tagConfig[value]
  return config?.text || value || '-'
}

const handlePreview = () => {
  ElMessage.info('预览功能开发中...')
}

const handleBack = () => {
  router.back()
}

const handleSave = async () => {
  // 表单验证
  if (!tableConfig.tableName) {
    ElMessage.warning('请输入表格名称')
    return
  }
  if (!tableConfig.tableCode) {
    ElMessage.warning('请输入表格编码')
    return
  }
  if (tableConfig.columns.length === 0) {
    ElMessage.warning('请添加至少一个列')
    return
  }

  loading.value = true
  try {
    // 构建 V4 格式的列配置
    const v4Columns: TableColumnV4[] = tableConfig.columns.map((col, index) => ({
      prop: col.prop || col.columnCode || '',
      label: col.label,
      type: col.type as any,
      width: col.width,
      minWidth: col.minWidth,
      align: col.align as any,
      fixed: col.fixed as any,
      sortable: col.sortable,
      showOverflowTooltip: col.showOverflowTooltip,
      imageConfig: col.imageConfig,
      tagConfig: col.tagConfig,
      dateConfig: col.dateConfig,
      switchConfig: col.switchConfig,
      linkConfig: col.linkConfig,
      progressConfig: col.progressConfig
    }))

    // 构建保存数据（兼容旧 API 格式）
    const saveData: TableConfig = {
      tableName: tableConfig.tableName,
      tableCode: tableConfig.tableCode,
      componentCategory: tableConfig.componentCategory,
      componentTags: tableConfig.componentTags,
      apiUrl: tableConfig.apiUrl,
      apiMethod: tableConfig.apiMethod,
      pagination: tableConfig.pagination,
      pageSize: tableConfig.pageSize,
      showIndex: tableConfig.showIndex,
      selection: tableConfig.selection,
      border: tableConfig.border,
      stripe: tableConfig.stripe,
      status: true,
      // 将 V4 格式存储在 configJson 中
      configJson: JSON.stringify({
        version: 4,
        columns: v4Columns
      }),
      columns: tableConfig.columns.map((col, index) => ({
        columnName: col.label,
        columnCode: col.prop || col.columnCode || '',
        label: col.label,
        width: col.width,
        minWidth: col.minWidth,
        sortable: col.sortable || false,
        fixed: col.fixed,
        align: col.align || 'left',
        visible: col.visible ?? true,
        sortOrder: index,
        // 兼容旧格式
        formatterType: col.type === 'text' ? undefined : col.type,
        formatterConfig: JSON.stringify(getFormatterConfig(col))
      }))
    }

    if (tableId.value) {
      await updateTable(tableId.value, saveData)
      ElMessage.success('更新表格配置成功')
    } else {
      const id = await createTable(saveData)
      tableId.value = id
      ElMessage.success('创建表格配置成功')
      router.replace({ query: { id: id.toString() } })
    }
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取格式化配置（用于兼容旧格式）
const getFormatterConfig = (col: DesignerColumn): any => {
  if (col.type === 'tag' && col.tagConfig) {
    return {
      typeMap: Object.fromEntries(
        Object.entries(col.tagConfig).map(([k, v]) => [k, v.type])
      ),
      labelMap: Object.fromEntries(
        Object.entries(col.tagConfig).map(([k, v]) => [k, v.text])
      )
    }
  } else if (col.type === 'datetime' || col.type === 'date') {
    return { format: col.dateConfig?.format }
  } else if (col.type === 'image' && col.imageConfig) {
    return { props: col.imageConfig }
  }
  return {}
}

// 加载表格配置(编辑模式)
const loadTableConfig = async () => {
  if (!tableId.value) return

  loading.value = true
  try {
    const data = await getTableConfig(tableId.value)

    // 加载表格基本信息
    tableConfig.tableName = data.tableName
    tableConfig.tableCode = data.tableCode
    tableConfig.componentCategory = (data.componentCategory as any) || 'business'
    tableConfig.componentTags = (data.componentTags as any) || ''
    tableConfig.apiUrl = data.apiUrl || ''
    tableConfig.apiMethod = data.apiMethod || 'GET'
    tableConfig.pagination = data.pagination ?? true
    tableConfig.pageSize = data.pageSize || 10
    tableConfig.showIndex = data.showIndex ?? true
    tableConfig.selection = data.selection ?? false
    tableConfig.border = data.border ?? true
    tableConfig.stripe = data.stripe ?? true

    // 尝试从 configJson 加载 V4 格式
    let v4Columns: TableColumnV4[] = []
    if (data.configJson) {
      try {
        const configObj = JSON.parse(data.configJson)
        if (configObj.version === 4 && configObj.columns) {
          v4Columns = configObj.columns
        }
      } catch (e) {
        console.error('解析 configJson 失败', e)
      }
    }

    // 加载表格列配置
    if (v4Columns.length > 0) {
      // 使用 V4 格式
      tableConfig.columns = v4Columns.map((col): DesignerColumn => ({
        id: Date.now().toString() + Math.random(),
        type: col.type,
        label: col.label,
        prop: col.prop,
        columnCode: col.prop,
        width: col.width,
        minWidth: col.minWidth,
        align: col.align,
        fixed: col.fixed,
        sortable: col.sortable,
        showOverflowTooltip: col.showOverflowTooltip,
        visible: true,
        imageConfig: col.imageConfig,
        tagConfig: col.tagConfig,
        dateConfig: col.dateConfig,
        switchConfig: col.switchConfig,
        linkConfig: col.linkConfig,
        progressConfig: col.progressConfig
      }))
    } else if (data.columns && data.columns.length > 0) {
      // 使用旧格式
      tableConfig.columns = data.columns.map((col: ApiTableColumn): DesignerColumn => ({
        id: col.id?.toString() || Date.now().toString(),
        type: col.formatterType || 'text',
        label: col.label,
        prop: col.columnCode,
        columnCode: col.columnCode,
        width: col.width,
        minWidth: col.minWidth,
        align: col.align || 'left',
        fixed: col.fixed,
        sortable: col.sortable,
        showOverflowTooltip: col.showOverflowTooltip,
        visible: col.visible ?? true
      }))
    }

    ElMessage.success('加载表格配置成功')
  } catch (error) {
    ElMessage.error('加载表格配置失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (tableId.value) {
    loadTableConfig()
  } else {
    // 新建模式：初始化一些默认列
    tableConfig.columns = [
      {
        id: '1',
        type: 'text',
        label: '姓名',
        prop: 'name',
        columnCode: 'name',
        minWidth: 120,
        align: 'left',
        visible: true
      },
      {
        id: '2',
        type: 'text',
        label: '年龄',
        prop: 'age',
        columnCode: 'age',
        width: 80,
        align: 'center',
        visible: true
      }
    ]
  }
})
</script>

<style lang="scss" scoped>
.table-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .designer-header {
    height: 50px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;

      .title {
        margin-left: 16px;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }

  .designer-body {
    flex: 1;
    display: flex;
    overflow: hidden;

    .panel-title {
      padding: 12px 16px;
      font-weight: bold;
      border-bottom: 1px solid #e6e6e6;
    }

    .config-panel {
      width: 320px;
      min-width: 320px;
      flex-shrink: 0;
      background-color: #fff;
      border-right: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      :deep(.el-tabs) {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: hidden;
      }

      :deep(.el-tabs__header) {
        margin: 0;
        padding: 0 16px;
        flex-shrink: 0;
      }

      :deep(.el-tabs__content) {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .column-list {
        .column-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
        }

        .column-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 12px;
          border: 1px solid #e6e6e6;
          border-radius: 4px;
          margin-bottom: 8px;
          cursor: pointer;

          &:hover {
            border-color: #409eff;
          }

          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
          }

          .column-info {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;

            .column-label {
              font-weight: bold;
            }

            .column-code {
              font-size: 12px;
              color: #909399;
            }
          }

          .column-actions {
            display: flex;
            gap: 4px;

            .el-icon {
              cursor: pointer;
              color: #909399;

              &:hover {
                color: #409eff;
              }
            }
          }
        }
      }
    }

    .preview-panel {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      background-color: #fff;
      margin: 0 1px;
      overflow: hidden;

      .panel-title {
        flex-shrink: 0;
      }

      .preview-area {
        flex: 1;
        padding: 20px;
        overflow-x: auto;
        overflow-y: auto;
      }
    }

    .property-panel {
      width: 300px;
      min-width: 300px;
      flex-shrink: 0;
      background-color: #fff;
      border-left: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      .panel-title {
        flex-shrink: 0;
      }

      .property-content {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .empty-tip {
        text-align: center;
        color: #909399;
        padding: 40px 0;
      }

      .tag-config-item {
        padding: 8px;
        background: #f5f7fa;
        border-radius: 4px;
        margin-bottom: 8px;
      }
    }
  }

  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}
</style>
