<template>
  <div class="common-manage-table">
    <!-- 搜索表单 -->
    <el-form v-if="searchFields && searchFields.length > 0" :model="searchForm" :inline="true" class="search-form">
      <el-form-item
        v-for="field in searchFields"
        :key="field.prop"
        :label="field.label"
      >
        <el-input
          v-if="field.type === 'input'"
          v-model="searchForm[field.prop]"
          :placeholder="`请输入${field.label}`"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select
          v-else-if="field.type === 'select'"
          v-model="searchForm[field.prop]"
          :placeholder="`请选择${field.label}`"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="option in field.options"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        <el-date-picker
          v-else-if="field.type === 'date'"
          v-model="searchForm[field.prop]"
          type="date"
          :placeholder="`请选择${field.label}`"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <div class="toolbar">
      <slot name="header"></slot>
      <el-button v-if="showRefresh" type="primary" :icon="Refresh" @click="handleRefresh">刷新</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" />
      <el-table-column
        v-for="col in columns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :align="col.align || 'left'"
      >
        <template #default="{ row }">
          <span v-if="col.formatter">{{ col.formatter(row) }}</span>
          <span v-else>{{ row[col.prop] }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="actions && actions.length > 0" label="操作" :width="actionWidth" fixed="right">
        <template #default="{ row }">
          <el-button
            v-for="action in actions"
            :key="action.label"
            :type="action.type || 'primary'"
            :icon="action.icon"
            size="small"
            :disabled="action.show ? !action.show(row) : false"
            @click="action.handler(row)"
          >
            {{ action.label }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="showPagination"
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface SearchField {
  prop: string
  label: string
  type: 'input' | 'select' | 'date'
  options?: Array<{ label: string; value: any }>
}

interface Column {
  prop: string
  label: string
  width?: number
  align?: string
  formatter?: (row: any) => string
}

interface Action {
  label: string
  icon?: any
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  show?: (row: any) => boolean
  handler: (row: any) => void
}

interface Props {
  searchFields?: SearchField[]
  columns: Column[]
  actions?: Action[]
  loadData: (params: any) => Promise<any>
  deleteData?: (id: number) => Promise<void>
  showSelection?: boolean
  showRefresh?: boolean
  showPagination?: boolean
  actionWidth?: number
}

const props = withDefaults(defineProps<Props>(), {
  showSelection: false,
  showRefresh: true,
  showPagination: true,
  actionWidth: 200
})

const searchForm = reactive<Record<string, any>>({})
const tableData = ref<any[]>([])
const loading = ref(false)
const selectedRows = ref<any[]>([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 初始化搜索表单
if (props.searchFields) {
  props.searchFields.forEach(field => {
    searchForm[field.prop] = undefined
  })
}

// 加载数据
async function fetchData() {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const result = await props.loadData(params)
    tableData.value = result.records || []
    pagination.total = result.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pagination.current = 1
  fetchData()
}

// 重置
function handleReset() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = undefined
  })
  pagination.current = 1
  fetchData()
}

// 刷新
function handleRefresh() {
  fetchData()
}

// 分页变化
function handleSizeChange(size: number) {
  pagination.size = size
  pagination.current = 1
  fetchData()
}

function handleCurrentChange(current: number) {
  pagination.current = current
  fetchData()
}

// 选择变化
function handleSelectionChange(selection: any[]) {
  selectedRows.value = selection
}

// 初始化
onMounted(() => {
  fetchData()
})

// 暴露刷新方法
defineExpose({
  refresh: fetchData
})
</script>

<style scoped lang="scss">
.common-manage-table {
  padding: 20px;

  .search-form {
    background: #f5f7fa;
    padding: 18px 18px 0;
    border-radius: 4px;
    margin-bottom: 20px;

    :deep(.el-form-item) {
      margin-bottom: 18px;
    }
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
}
</style>
