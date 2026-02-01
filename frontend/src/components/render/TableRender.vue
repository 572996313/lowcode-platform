<template>
  <div class="table-render">
    <!-- 工具栏 -->
    <div v-if="showToolbar && toolbarButtons.length > 0" class="table-toolbar">
      <ButtonRenderer
        v-for="btn in toolbarButtons"
        :key="btn.id"
        :config="btn"
        :context="{ selection: tableSelection }"
        @click="handleToolbarButtonClick"
      />
    </div>

    <!-- 表格 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="tableData"
      :border="tableConfig.border"
      :stripe="tableConfig.stripe"
      :height="tableConfig.height"
      :max-height="tableConfig.maxHeight"
      :empty-text="tableConfig.emptyText"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" />
      <el-table-column v-if="tableConfig.showIndex" type="index" label="序号" width="60" />

      <TableColumnRender
        v-for="column in tableConfig.columns"
        :key="column.id"
        :column="column"
        @action-click="handleColumnActionClick"
      />

      <!-- 操作列 -->
      <el-table-column
        v-if="rowButtons.length > 0 && mode !== 'select'"
        label="操作"
        :width="operatingColumnWidth"
        fixed="right"
      >
        <template #default="{ row }">
          <template v-if="rowButtons.length > 0">
            <ButtonRenderer
              v-for="btn in rowButtons"
              :key="btn.id"
              :config="btn"
              :context="{ row, selection: tableSelection }"
              @click="(e, c) => handleRowButtonClick(e, c, row)"
            />
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 选择模式底部操作栏 -->
    <div v-if="mode === 'select'" class="table-select-footer">
      <div class="select-info">
        <el-tag type="info">已选择 {{ tableSelection.length }} 项</el-tag>
      </div>
      <div class="select-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="tableConfig.pagination && mode !== 'select'"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { TableInstance } from 'element-plus'
import type { TableConfig, TableColumn } from '@/api/table'
import type { ButtonConfig } from '@/api/button'
import type { PageResult } from '@/api/table'
import { getTableConfig, getButtonsByTableId } from '@/api/table'
import TableColumnRender from './TableColumnRender.vue'
import ButtonRenderer from './ButtonRenderer.vue'
import { createButtonActionHandler } from '@/utils/buttonActionHandler'
import { request } from '@/utils/request'

interface Props {
  configId: number
  searchFormData?: Record<string, any>
  autoLoad?: boolean
  mode?: 'view' | 'select'  // 新增模式属性
  multiple?: boolean  // 新增多选属性
}

interface Emits {
  (e: 'selection-change', selection: any[]): void
  (e: 'row-click', row: any): void
  (e: 'refresh'): void
  (e: 'confirm', data: any): void  // 新增确认事件
  (e: 'cancel'): void  // 新增取消事件
}

const props = withDefaults(defineProps<Props>(), {
  autoLoad: true,
  mode: 'view',
  multiple: false
})

const emit = defineEmits<Emits>()

const tableRef = ref<TableInstance>()
const loading = ref(false)
const tableConfig = ref<TableConfig & { columns?: TableColumn[] }>({})
const tableData = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = computed(() => {
  if (tableConfig.value.pageSizes) {
    try {
      return JSON.parse(tableConfig.value.pageSizes)
    } catch {
      return [10, 20, 50, 100]
    }
  }
  return [10, 20, 50, 100]
})

const tableSelection = ref<any[]>([])
const toolbarButtons = ref<ButtonConfig[]>([])
const rowButtons = ref<ButtonConfig[]>([])

// 是否显示工具栏
const showToolbar = computed(() => {
  return props.mode === 'select' ? false : (
    tableConfig.value.configJson
      ? JSON.parse(tableConfig.value.configJson).showToolbar !== false
      : true
  )
})

// 是否显示选择列
const showSelection = computed(() => {
  return props.mode === 'select' || tableConfig.value.selection
})

// 操作列宽度
const operatingColumnWidth = computed(() => {
  const buttonCount = rowButtons.value.length
  return Math.max(150, buttonCount * 80)
})

// 加载表格配置
const loadTableConfig = async () => {
  loading.value = true
  try {
    const data = await getTableConfig(props.configId)

    // 处理列配置数据
    if (data.configJson) {
      try {
        const configObj = JSON.parse(data.configJson)
        tableConfig.value = { ...data, ...configObj }
      } catch (e) {
        tableConfig.value = data
      }
    } else {
      tableConfig.value = data
    }

    // 加载按钮配置
    const buttons = await getButtonsByTableId(props.configId)
    toolbarButtons.value = buttons.filter((btn: ButtonConfig) => btn.position === 'toolbar')
    rowButtons.value = buttons.filter((btn: ButtonConfig) => btn.position === 'row')

    // 自动加载数据
    if (props.autoLoad && tableConfig.value.apiUrl) {
      await loadData()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载表格配置失败')
  } finally {
    loading.value = false
  }
}

// 加载表格数据
const loadData = async () => {
  if (!tableConfig.value.apiUrl) {
    console.warn('表格未配置 API 地址')
    return
  }

  loading.value = true
  try {
    const params = {
      ...props.searchFormData,
      current: currentPage.value,
      size: pageSize.value
    }

    const response = await request.request<PageResult<any>>({
      url: tableConfig.value.apiUrl,
      method: (tableConfig.value.apiMethod || 'GET') as any,
      params: tableConfig.value.apiMethod === 'GET' ? params : undefined,
      data: tableConfig.value.apiMethod !== 'GET' ? params : undefined
    })

    tableData.value = response.records || []
    total.value = response.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载数据失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection: any[]) => {
  tableSelection.value = selection
  emit('selection-change', selection)
}

// 处理页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadData()
}

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadData()
}

// 工具栏按钮点击
const handleToolbarButtonClick = async (event: MouseEvent, config: ButtonConfig) => {
  const context = {
    selection: tableSelection.value,
    refresh: () => {
      loadData()
      emit('refresh')
    }
  }

  const actionHandler = createButtonActionHandler(context)
  await actionHandler.handle(config)
}

// 行按钮点击
const handleRowButtonClick = async (event: MouseEvent, config: ButtonConfig, row: any) => {
  const context = {
    row,
    selection: tableSelection.value,
    refresh: () => {
      loadData()
      emit('refresh')
    }
  }

  const actionHandler = createButtonActionHandler(context)
  await actionHandler.handle(config)
}

// 列操作点击
const handleColumnActionClick = (action: any, row: any) => {
  console.log('列操作点击:', action, row)
  // TODO: 根据按钮配置执行相应操作
}

// 刷新数据
const refresh = () => {
  currentPage.value = 1
  loadData()
}

// 获取选中的行
const getSelectionRows = () => {
  return tableSelection.value
}

// 清空选择
const clearSelection = () => {
  tableRef.value?.clearSelection()
}

// 处理选择模式确认
const handleConfirm = () => {
  if (tableSelection.value.length === 0) {
    ElMessage.warning('请至少选择一项数据')
    return
  }

  const data = props.multiple ? tableSelection.value : tableSelection.value[0]
  emit('confirm', data)
}

// 处理选择模式取消
const handleCancel = () => {
  emit('cancel')
}

// 监听查询表单数据变化
watch(() => props.searchFormData, () => {
  if (props.autoLoad) {
    currentPage.value = 1
    loadData()
  }
}, { deep: true })

// 暴露方法供父组件调用
defineExpose({
  loadData,
  refresh,
  getSelectionRows,
  clearSelection
})

onMounted(() => {
  loadTableConfig()
})
</script>

<style scoped>
.table-render {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-toolbar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 4px;
}

.table-select-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #dcdfe6;

  .select-info {
    display: flex;
    align-items: center;
  }

  .select-actions {
    display: flex;
    gap: 8px;
  }
}
</style>
