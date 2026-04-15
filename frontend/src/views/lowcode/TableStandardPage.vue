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
        @selection-change="handleSelectionChange"
      />
    </el-card>

    <!-- 动态表单：弹窗模式 -->
    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      :width="currentFormConfig ? '700px' : '500px'"
      @close="handleFormClose"
    >
      <ConfigFormPage
        v-if="currentFormConfig"
        ref="configFormPageRef"
        :config="currentFormConfig"
        v-model="formData"
        :mode="isEdit ? 'edit' : 'add'"
      />
      <ConfigForm
        v-else
        ref="configFormRef"
        :fields="currentFormFields"
        v-model="formData"
        :is-edit="isEdit"
      />
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 动态表单：抽屉模式 -->
    <el-drawer
      v-model="drawerVisible"
      :title="formTitle"
      size="600px"
      @close="handleFormClose"
    >
      <ConfigFormPage
        v-if="currentFormConfig"
        ref="configFormPageRef2"
        :config="currentFormConfig"
        v-model="formData"
        :mode="isEdit ? 'edit' : 'add'"
      />
      <ConfigForm
        v-else
        ref="configFormRef2"
        :fields="currentFormFields"
        v-model="formData"
        :is-edit="isEdit"
      />
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ConfigTable from './components/ConfigTable.vue'
import ConfigForm from './components/ConfigForm.vue'
import ConfigFormPage from './components/ConfigFormPage.vue'
import ConfigSearch from './components/ConfigSearch.vue'
import ConfigToolbar from './components/ConfigToolbar.vue'
import {
  getTableStandardConfig,
  getTableStandardPage,
  createTableStandard,
  updateTableStandard,
  deleteTableStandard,
  type PageConfigResponse,
  type SearchFieldConfig,
  type ButtonActionConfig,
  type ToolbarButton,
  type ActionButton
} from '@/api/table-standard'
import { getConfig } from '@/utils/configRegistry'
import type { FormPageConfigResponse } from '@/api/form-standard'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const formVisible = ref(false)
const drawerVisible = ref(false)
const isEdit = ref(false)

// Refs
const configFormRef = ref<InstanceType<typeof ConfigForm>>()
const configFormRef2 = ref<InstanceType<typeof ConfigForm>>()
const configFormPageRef = ref<InstanceType<typeof ConfigFormPage>>()
const configFormPageRef2 = ref<InstanceType<typeof ConfigFormPage>>()

// 页面配置
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

// 动态表单状态
const formData = ref<Record<string, any>>({})
const currentFormConfig = ref<FormPageConfigResponse | null>(null)
const currentFormFields = ref<any[]>([])

const formTitle = computed(() => isEdit.value ? '编辑' : '新增')

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

// ---- 动作执行（核心逻辑） ----

const executeAction = (actionConfig: ButtonActionConfig | undefined, edit: boolean, row?: Record<string, any>) => {
  if (!actionConfig) {
    ElMessage.warning('未配置按钮动作')
    return
  }

  switch (actionConfig.type) {
    case 'openForm':
      openTargetForm(actionConfig, edit, row)
      break
    case 'openTable':
      openTargetTable(actionConfig, row)
      break
    case 'route':
      navigateToRoute(actionConfig, row)
      break
    case 'submit':
      handleSubmit(actionConfig)
      break
    case 'custom':
      ElMessage.info('自定义动作（待实现）')
      break
    default:
      ElMessage.info(`动作: ${actionConfig.type}`)
  }
}

// 打开目标表单
const openTargetForm = (actionConfig: ButtonActionConfig, edit: boolean, row?: Record<string, any>) => {
  if (!actionConfig.targetCode) {
    ElMessage.warning('未配置目标表单')
    return
  }

  const targetConfig = getConfig(actionConfig.targetCode)
  if (!targetConfig) {
    ElMessage.warning(`未找到表单配置: ${actionConfig.targetCode}`)
    return
  }

  const openMode = actionConfig.openMode || 'dialog'
  isEdit.value = edit
  formData.value = edit && row ? { ...row } : {}

  if (openMode === 'page') {
    // 整页面跳转
    const query: Record<string, string> = { mode: edit ? 'edit' : 'add' }
    if (edit && row?.id) query.id = String(row.id)
    router.push({ path: '/form-standard', query })
    return
  }

  // dialog / drawer 模式：加载表单配置到当前页面
  currentFormConfig.value = targetConfig as FormPageConfigResponse
  currentFormFields.value = []

  if (openMode === 'drawer') {
    drawerVisible.value = true
  } else {
    formVisible.value = true
  }
}

// 打开目标表格（钻取场景）
const openTargetTable = (actionConfig: ButtonActionConfig, row?: Record<string, any>) => {
  if (!actionConfig.targetCode) {
    ElMessage.warning('未配置目标表格')
    return
  }
  // TODO: 实现表格钻取
  ElMessage.info(`打开表格: ${actionConfig.targetCode}`)
}

// 路由跳转
const navigateToRoute = (actionConfig: ButtonActionConfig, row?: Record<string, any>) => {
  if (!actionConfig.routePath) {
    ElMessage.warning('未配置路由路径')
    return
  }

  let query: Record<string, string> = {}
  if (actionConfig.routeQuery) {
    for (const [key, template] of Object.entries(actionConfig.routeQuery)) {
      // 支持 ${fieldName} 模板变量
      if (template.startsWith('${') && template.endsWith('}') && row) {
        const fieldName = template.slice(2, -1)
        query[key] = String(row[fieldName] ?? '')
      } else {
        query[key] = template
      }
    }
  }

  router.push({ path: actionConfig.routePath, query })
}

// 提交数据
const selectedRows = ref<Record<string, any>[]>([])

const handleSubmit = async (actionConfig: ButtonActionConfig) => {
  const mode = actionConfig.selectionMode || 'none'

  if (mode === 'single' && selectedRows.value.length !== 1) {
    ElMessage.warning('请选择一条数据')
    return
  }
  if (mode === 'multiple' && selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条数据')
    return
  }

  const confirmMsg = actionConfig.confirmText
    || (mode === 'none'
      ? '确定要提交吗？'
      : `确定要提交选中的 ${selectedRows.value.length} 条数据吗？`)

  try {
    await ElMessageBox.confirm(confirmMsg, '提示', { type: 'warning' })
    // TODO: 调用后端提交接口
    ElMessage.success('提交成功（mock）')
    selectedRows.value = []
    loadData()
  } catch {
    // 取消
  }
}

const handleSelectionChange = (rows: Record<string, any>[]) => {
  selectedRows.value = rows
}

// ---- 操作分发 ----

// 查找按钮的 actionConfig
const findToolbarActionConfig = (action: string): ButtonActionConfig | undefined => {
  const btn = pageConfig.value?.toolbar?.buttons?.find(b => b.action === action)
  return btn?.actionConfig
}

const findRowActionConfig = (action: string): ButtonActionConfig | undefined => {
  for (const col of pageConfig.value?.tableColumns || []) {
    if (col.type === 'action' && col.actionConfig?.buttons) {
      const btn = col.actionConfig.buttons.find(b => b.action === action)
      if (btn?.actionConfig) return btn.actionConfig
    }
  }
  return undefined
}

const handleToolbarAction = (action: string) => {
  if (action === 'delete') {
    handleDelete({ id: 0 }) // batch delete placeholder
    return
  }

  const actionConfig = findToolbarActionConfig(action)
  executeAction(actionConfig, false)
}

const handleAction = (action: string, row: Record<string, any>) => {
  if (action === 'delete') {
    handleDelete(row)
    return
  }

  const actionConfig = findRowActionConfig(action)
  executeAction(actionConfig, action === 'edit', row)
}

// 关闭表单
const handleFormClose = () => {
  configFormRef.value?.resetFields()
  configFormRef2.value?.resetFields()
  currentFormConfig.value = null
  currentFormFields.value = []
}

// 保存
const handleSave = async () => {
  let valid = false
  let data: Record<string, any> = {}

  if (currentFormConfig.value) {
    const ref = formVisible.value ? configFormPageRef.value : configFormPageRef2.value
    if (!ref) return
    valid = await ref.validate()
    if (!valid) return
    data = ref.getFormData()
  } else {
    const ref = formVisible.value ? configFormRef.value : configFormRef2.value
    if (!ref) return
    valid = await ref.validate()
    if (!valid) return
    data = ref.getFormData()
  }

  saving.value = true
  try {
    if (isEdit.value && data.id) {
      await updateTableStandard(data.id, data)
      ElMessage.success('更新成功')
    } else {
      await createTableStandard(data)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    drawerVisible.value = false
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
