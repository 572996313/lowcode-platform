<template>
  <div class="table-standard-page" v-loading="loading">
    <ConfigToolbar
      :title="pageConfig?.pageName || '表格标准页面'"
      :buttons="pageConfig?.toolbar?.buttons || []"
      @action="handleToolbarAction"
    />

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <ConfigSearch
        :fields="pageConfig?.searchFields || []"
        v-model="searchParams"
        @search="handleSearch"
        @reset="handleReset"
      />
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <ConfigTable
        :columns="pageConfig?.tableColumns || []"
        :data="tableData"
        :config="tableConfig"
        :total="pagination.total"
        :loading="loading"
        :page="pagination.current"
        :page-size="pagination.size"
        @action="handleAction"
        @page-change="handlePageChange"
      />
    </el-card>

    <!-- 表单弹窗（可替换为 drawer / 新页面 等任意打开方式） -->
    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '编辑' : '新增'"
      width="500px"
      @close="handleFormClose"
    >
      <ConfigForm
        ref="configFormRef"
        :fields="pageConfig?.formFields || []"
        v-model="formData"
        :is-edit="isEdit"
      />
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ConfigTable from './components/ConfigTable.vue'
import ConfigForm from './components/ConfigForm.vue'
import ConfigSearch from './components/ConfigSearch.vue'
import ConfigToolbar from './components/ConfigToolbar.vue'
import {
  getTableStandardConfig,
  getTableStandardPage,
  createTableStandard,
  updateTableStandard,
  deleteTableStandard,
  type PageConfigResponse,
  type SearchFieldConfig
} from '@/api/table-standard'

const loading = ref(false)
const saving = ref(false)
const formVisible = ref(false)
const isEdit = ref(false)
const configFormRef = ref<InstanceType<typeof ConfigForm>>()

// 页面配置（从后端获取）
const pageConfig = ref<PageConfigResponse | null>(null)
const tableConfig = computed(() => pageConfig.value?.tableConfig || {
  border: true, stripe: true, showPagination: true, pageSize: 10, pageSizes: [10, 20, 50, 100]
})

// 搜索参数
const searchParams = reactive<Record<string, any>>({})

// 分页
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 表格数据
const tableData = ref<Record<string, any>[]>([])

// 表单数据
const formData = ref<Record<string, any>>({})

// 初始化搜索参数默认值
const initSearchParams = (fields: SearchFieldConfig[]) => {
  for (const field of fields) {
    searchParams[field.field] = field.defaultValue ?? undefined
  }
}

// 加载页面配置
const loadPageConfig = async () => {
  try {
    pageConfig.value = await getTableStandardConfig()
    initSearchParams(pageConfig.value.searchFields)
    pagination.size = tableConfig.value.pageSize || 10
  } catch (error: any) {
    ElMessage.error('加载页面配置失败: ' + error.message)
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await getTableStandardPage({
      ...searchParams,
      current: pagination.current,
      size: pagination.size
    })
    tableData.value = result.records
    pagination.total = result.total
  } catch (error: any) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  if (pageConfig.value?.searchFields) {
    initSearchParams(pageConfig.value.searchFields)
  }
  pagination.current = 1
  loadData()
}

// 分页变化
const handlePageChange = (current: number, size: number) => {
  pagination.current = current
  pagination.size = size
  loadData()
}

// 工具栏操作分发
const handleToolbarAction = (action: string) => {
  if (action === 'add') {
    openForm(false)
  } else if (action === 'export') {
    ElMessage.info('导出功能待实现')
  }
}

// 表格操作分发
const handleAction = (action: string, row: Record<string, any>) => {
  if (action === 'edit') {
    openForm(true, row)
  } else if (action === 'delete') {
    handleDelete(row)
  }
}

// 打开表单（弹窗方式，可替换为 drawer / router.push 等）
const openForm = (edit: boolean, row?: Record<string, any>) => {
  isEdit.value = edit
  formData.value = edit ? { ...row } : {}
  formVisible.value = true
}

// 关闭表单
const handleFormClose = () => {
  configFormRef.value?.resetFields()
}

// 保存
const handleSave = async () => {
  if (!configFormRef.value) return
  const valid = await configFormRef.value.validate()
  if (!valid) return

  saving.value = true
  try {
    const data = configFormRef.value.getFormData()
    if (isEdit.value && data.id) {
      await updateTableStandard(data.id, data)
      ElMessage.success('更新成功')
    } else {
      await createTableStandard(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = async (row: Record<string, any>) => {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.name || row.id}」吗？`, '提示', { type: 'warning' })
    await deleteTableStandard(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 取消
  }
}

onMounted(async () => {
  await loadPageConfig()
  loadData()
})
</script>

<style scoped lang="scss">
.table-standard-page {
  padding: 20px;

  .search-card {
    margin-bottom: 16px;
  }
}
</style>
