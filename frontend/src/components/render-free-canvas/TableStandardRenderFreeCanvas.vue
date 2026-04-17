<template>
  <div class="table-standard-render" :style="containerStyle">
    <!-- 工具栏 -->
    <div v-if="toolbarButtons.length" class="standard-toolbar">
      <div class="toolbar-buttons">
        <el-button
          v-for="btn in toolbarButtons"
          :key="btn.action"
          :type="(btn.btnType as any) || ''"
          size="small"
          @click="handleButtonClick(btn)"
        >
          {{ btn.label }}
        </el-button>
      </div>
    </div>

    <!-- 搜索区 -->
    <div v-if="config.searchFields?.length" class="standard-search">
      <el-form :inline="true" :model="searchParams" size="small">
        <template v-for="field in config.searchFields" :key="field.field">
          <el-form-item :label="field.label">
            <el-input
              v-if="field.type === 'input'"
              v-model="searchParams[field.field]"
              :placeholder="field.placeholder"
              :clearable="field.clearable !== false"
              :style="field.width ? { width: field.width + 'px' } : {}"
            />
            <el-select
              v-else-if="field.type === 'select'"
              v-model="searchParams[field.field]"
              :placeholder="field.placeholder"
              :clearable="field.clearable !== false"
              :style="field.width ? { width: field.width + 'px' } : {}"
            >
              <el-option v-for="opt in field.options" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="searchParams[field.field]"
              :placeholder="field.placeholder"
              :clearable="field.clearable !== false"
              value-format="YYYY-MM-DD"
              size="small"
            />
            <el-input-number
              v-else-if="field.type === 'number'"
              v-model="searchParams[field.field]"
              :placeholder="field.placeholder"
              :style="field.width ? { width: field.width + 'px' } : {}"
              size="small"
            />
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
          <el-button size="small" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区 -->
    <div class="standard-table-wrapper">
      <el-table
        :data="tableData"
        :border="config.tableConfig?.border"
        :stripe="config.tableConfig?.stripe"
        :size="config.tableConfig?.size || 'small'"
        style="width: 100%"
        height="100%"
        v-loading="loading"
        @selection-change="(rows: any[]) => selectedRows = rows"
      >
        <el-table-column v-if="config.tableConfig?.showIndex" type="index" label="序号" width="60" align="center" />
        <el-table-column v-if="config.tableConfig?.showSelection" type="selection" width="55" align="center" />

        <template v-for="col in config.tableColumns" :key="col.prop || col.label">
          <!-- Tag 列 -->
          <el-table-column
            v-if="col.type === 'tag'"
            :prop="col.prop"
            :label="col.label"
            :width="col.width"
            :min-width="col.minWidth"
            :align="col.align"
            :fixed="col.fixed"
          >
            <template #default="{ row }">
              <el-tag
                v-if="col.tagConfig && col.tagConfig[String(row[col.prop!])]"
                :type="col.tagConfig[String(row[col.prop!])].type as any"
                size="small"
              >
                {{ col.tagConfig[String(row[col.prop!])].text }}
              </el-tag>
              <span v-else>{{ row[col.prop!] }}</span>
            </template>
          </el-table-column>

          <!-- 普通文本列 -->
          <el-table-column
            v-else
            :prop="col.prop"
            :label="col.label"
            :width="col.width"
            :min-width="col.minWidth"
            :align="col.align"
            :fixed="col.fixed"
            :show-overflow-tooltip="col.showOverflowTooltip"
          />
        </template>

        <!-- 操作列（从 toolbar.buttons 中 position=table-column 的按钮自动生成） -->
        <el-table-column
          v-if="actionColumnButtons.length"
          label="操作"
          :width="actionColumnWidth"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              v-for="btn in actionColumnButtons"
              :key="btn.action"
              :type="(btn.btnType as any) || 'primary'"
              size="small"
              link
              @click="handleActionBtnClick(btn, row)"
            >
              {{ btn.label }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div v-if="config.tableConfig?.showPagination !== false" class="standard-pagination">
      <el-pagination
        small
        layout="total, sizes, prev, pager, next"
        :total="pagination.total"
        :page-sizes="config.tableConfig?.pageSizes || [10, 20, 50, 100]"
        :page-size="pagination.size"
        :current-page="pagination.current"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { request } from '@/utils/request'
import type { TableStandardComponentConfig, ComponentStyle } from '@/types/page-free-canvas'

const props = defineProps<{
  config: TableStandardComponentConfig
  compStyle?: ComponentStyle
  pageId?: number
  componentId?: string
}>()

const emit = defineEmits<{
  (e: 'action', data: { action: string; actionConfig?: any; row?: any; rows?: any[] }): void
}>()

// 数据状态
const loading = ref(false)
const tableData = ref<Record<string, any>[]>([])
const selectedRows = ref<any[]>([])
const searchParams = reactive<Record<string, any>>({})
const pagination = reactive({
  current: 1,
  size: props.config.tableConfig?.pageSize || 10,
  total: 0
})

// 初始化搜索参数默认值
if (props.config.searchFields?.length) {
  for (const field of props.config.searchFields) {
    searchParams[field.field] = field.defaultValue ?? undefined
  }
}

// 实际请求地址：配置了 apiUrl 用配置的，否则走通用数据接口
const requestUrl = computed(() => props.config.apiUrl || '/table-data/query')
const requestMethod = computed(() => (props.config.apiMethod || 'POST').toLowerCase())

// 按 position 分组按钮
const toolbarButtons = computed(() =>
  (props.config.toolbar?.buttons || []).filter(b => !b.position || b.position === 'toolbar')
)
const actionColumnButtons = computed(() =>
  (props.config.toolbar?.buttons || []).filter(b => b.position === 'table-column')
)
const actionColumnWidth = computed(() => {
  const count = actionColumnButtons.value.length
  return Math.max(100, count * 60)
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const params = {
      pageId: props.pageId,
      componentId: props.componentId,
      data: {
        current: pagination.current,
        size: pagination.size,
        filters: { ...searchParams }
      }
    }

    let result: any
    if (requestMethod.value === 'get') {
      result = await request.get(requestUrl.value, params)
    } else {
      result = await request.post(requestUrl.value, params)
    }
    applyResult(result)
  } catch (e: any) {
    console.error('表格数据加载失败:', e)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

function applyResult(result: any) {
  if (result?.records) {
    // 分页格式
    tableData.value = result.records
    pagination.total = result.total || 0
    pagination.current = result.current || 1
  } else if (Array.isArray(result)) {
    // 数组格式
    tableData.value = result
    pagination.total = result.length
  } else {
    tableData.value = []
    pagination.total = 0
  }
}

// 搜索
function handleSearch() {
  pagination.current = 1
  loadData()
}

// 重置
function handleReset() {
  if (props.config.searchFields?.length) {
    for (const field of props.config.searchFields) {
      searchParams[field.field] = field.defaultValue ?? undefined
    }
  }
  pagination.current = 1
  loadData()
}

// 分页
function handleSizeChange(size: number) {
  pagination.size = size
  pagination.current = 1
  loadData()
}

function handleCurrentChange(page: number) {
  pagination.current = page
  loadData()
}

// 工具栏按钮
function handleButtonClick(btn: any) {
  const mode = btn.actionConfig?.selectionMode
  if (mode === 'single') {
    if (selectedRows.value.length !== 1) {
      ElMessage.warning('请选择一条数据')
      return
    }
    emit('action', { action: btn.action, actionConfig: btn.actionConfig, row: selectedRows.value[0] })
    return
  }
  if (mode === 'multiple') {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请至少选择一条数据')
      return
    }
    emit('action', { action: btn.action, actionConfig: btn.actionConfig, rows: selectedRows.value })
    return
  }
  emit('action', { action: btn.action, actionConfig: btn.actionConfig })
}

// 操作列按钮
function handleActionBtnClick(btn: any, row: any) {
  emit('action', { action: btn.action, actionConfig: btn.actionConfig, row })
}

const containerStyle = computed(() => ({
  width: '100%',
  height: '100%',
  display: 'flex',
  flexDirection: 'column' as const,
  backgroundColor: props.compStyle?.backgroundColor || '#fff',
  borderRadius: props.compStyle?.borderRadius || '4px',
  padding: props.compStyle?.padding || '12px',
  border: props.compStyle?.border || '1px solid #ebeef5',
  overflow: 'hidden'
}))

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.table-standard-render {
  box-sizing: border-box;

  .standard-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    flex-shrink: 0;

    .toolbar-buttons {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }
  }

  .standard-search {
    margin-bottom: 10px;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;
    flex-shrink: 0;

    :deep(.el-form-item) {
      margin-bottom: 4px;
    }
  }

  .standard-table-wrapper {
    flex: 1;
    overflow: hidden;
    min-height: 0;
  }

  .standard-pagination {
    margin-top: 8px;
    display: flex;
    justify-content: flex-end;
    flex-shrink: 0;
  }
}
</style>
