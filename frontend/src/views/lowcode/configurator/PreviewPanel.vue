<template>
  <div class="preview-panel">
    <div class="preview-label">实时预览</div>

    <ConfigToolbar
      :title="config.pageName || '表格标准页面'"
      :buttons="config.toolbar?.buttons || []"
      @action="handleToolbarAction"
    />

    <!-- 搜索栏预览 -->
    <el-card v-if="config.searchFields?.length" shadow="never" class="search-card">
      <ConfigSearch
        :fields="config.searchFields"
        v-model="searchParams"
        @search="handleSearch"
        @reset="handleReset"
      />
    </el-card>

    <!-- 主表格预览 -->
    <el-card shadow="never">
      <ConfigTable
        :columns="config.tableColumns || []"
        :data="mockData"
        :config="tableConfig"
        :total="mockData.length"
        :loading="false"
        :page="1"
        :page-size="tableConfig.pageSize || 10"
        @action="handleAction"
        @page-change="() => {}"
      />
    </el-card>

    <!-- 打开表单：弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="700px">
      <ConfigFormPage
        v-if="currentFormConfig"
        ref="configFormPageRef"
        :config="currentFormConfig"
        v-model="formData"
        :mode="isEdit ? 'edit' : 'add'"
      />
      <div v-else class="no-tip">未找到目标表单配置，请检查按钮的动作配置</div>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary">保存</el-button>
      </template>
    </el-dialog>

    <!-- 打开表单：抽屉 -->
    <el-drawer v-model="formDrawerVisible" :title="formTitle" size="600px">
      <ConfigFormPage
        v-if="currentFormConfig"
        ref="configFormPageRef2"
        :config="currentFormConfig"
        v-model="formData"
        :mode="isEdit ? 'edit' : 'add'"
      />
      <div v-else class="no-tip">未找到目标表单配置</div>
      <template #footer>
        <el-button @click="formDrawerVisible = false">取消</el-button>
        <el-button type="primary">保存</el-button>
      </template>
    </el-drawer>

    <!-- 打开表格：弹窗 -->
    <el-dialog v-model="tableDialogVisible" :title="tableDialogTitle" width="80%">
      <ConfigTable
        v-if="currentTableConfig"
        :columns="currentTableConfig.tableColumns || []"
        :data="targetMockData"
        :config="currentTableConfig.tableConfig || {}"
        :total="targetMockData.length"
        :loading="false"
        :page="1"
        :page-size="10"
        @action="() => ElMessage.info('嵌套操作（预览模式）')"
        @page-change="() => {}"
      />
      <div v-else class="no-tip">未找到目标表格配置，请检查按钮的动作配置</div>
      <template #footer>
        <el-button @click="tableDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 打开表格：抽屉 -->
    <el-drawer v-model="tableDrawerVisible" :title="tableDialogTitle" size="70%">
      <ConfigTable
        v-if="currentTableConfig"
        :columns="currentTableConfig.tableColumns || []"
        :data="targetMockData"
        :config="currentTableConfig.tableConfig || {}"
        :total="targetMockData.length"
        :loading="false"
        :page="1"
        :page-size="10"
        @action="() => ElMessage.info('嵌套操作（预览模式）')"
        @page-change="() => {}"
      />
      <div v-else class="no-tip">未找到目标表格配置</div>
      <template #footer>
        <el-button @click="tableDrawerVisible = false">关闭</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import ConfigToolbar from '../components/ConfigToolbar.vue'
import ConfigSearch from '../components/ConfigSearch.vue'
import ConfigTable from '../components/ConfigTable.vue'
import ConfigFormPage from '../components/ConfigFormPage.vue'
import { generateMockData } from '@/utils/mockDataGenerator'
import type { PageConfigResponse } from '@/api/table-standard'
import { getConfig } from '@/utils/configRegistry'
import type { FormPageConfigResponse } from '@/api/form-standard'

const props = defineProps<{
  config: PageConfigResponse
}>()

// ---- 搜索 ----
const searchParams = reactive<Record<string, any>>({})

watch(() => props.config.searchFields, (fields) => {
  if (!fields) return
  for (const field of fields) {
    if (searchParams[field.field] === undefined) {
      searchParams[field.field] = field.defaultValue ?? undefined
    }
  }
}, { immediate: true })

const handleSearch = () => ElMessage.info('搜索（预览模式）')
const handleReset = () => {
  for (const field of props.config.searchFields || []) {
    searchParams[field.field] = field.defaultValue ?? undefined
  }
}

// ---- 主表格数据 ----
const tableConfig = computed(() => props.config.tableConfig || {
  border: true, stripe: true, showPagination: true, pageSize: 10, pageSizes: [10, 20, 50, 100]
})

const mockData = computed(() =>
  generateMockData(props.config.tableColumns || [], 8)
)

// ---- 表单弹窗/抽屉 ----
const formVisible = ref(false)
const formDrawerVisible = ref(false)
const isEdit = ref(false)
const formData = ref<Record<string, any>>({})
const currentFormConfig = ref<FormPageConfigResponse | null>(null)
const formTitle = computed(() => isEdit.value ? '编辑' : '新增')

// ---- 表格弹窗/抽屉 ----
const tableDialogVisible = ref(false)
const tableDrawerVisible = ref(false)
const currentTableConfig = ref<PageConfigResponse | null>(null)
const tableDialogTitle = computed(() => currentTableConfig.value?.pageName || '表格')
const targetMockData = computed(() =>
  generateMockData(currentTableConfig.value?.tableColumns || [], 5)
)

// ---- 查找 actionConfig ----
const findToolbarActionConfig = (action: string) => {
  const btn = props.config.toolbar?.buttons?.find(b => b.action === action)
  return btn?.actionConfig
}

const findRowActionConfig = (action: string) => {
  for (const col of props.config.tableColumns || []) {
    if (col.type === 'action' && col.actionConfig?.buttons) {
      const btn = col.actionConfig.buttons.find(b => b.action === action)
      if (btn?.actionConfig) return btn.actionConfig
    }
  }
  return undefined
}

// ---- 打开目标 ----
const openTarget = (actionConfig: any, edit: boolean, row?: Record<string, any>) => {
  const type = actionConfig?.type
  const targetCode = actionConfig?.targetCode
  const openMode = actionConfig?.openMode || 'dialog'

  console.log('[PreviewPanel] openTarget:', { type, targetCode, openMode, edit })

  if (type === 'openForm') {
    // 打开表单
    isEdit.value = edit
    formData.value = edit && row ? { ...row } : {}

    if (targetCode) {
      const cfg = getConfig(targetCode)
      currentFormConfig.value = (cfg as FormPageConfigResponse) || null
      console.log('[PreviewPanel] form config found:', !!cfg, 'has groups:', !!(cfg as any)?.groups?.length)
    } else {
      currentFormConfig.value = null
    }

    if (openMode === 'drawer') {
      formDrawerVisible.value = true
    } else {
      formVisible.value = true
    }
  } else if (type === 'openTable') {
    // 打开表格
    if (targetCode) {
      const cfg = getConfig(targetCode)
      currentTableConfig.value = (cfg as PageConfigResponse) || null
      console.log('[PreviewPanel] table config found:', !!cfg, 'has columns:', !!(cfg as any)?.tableColumns?.length)
    } else {
      currentTableConfig.value = null
    }

    if (openMode === 'drawer') {
      tableDrawerVisible.value = true
    } else {
      tableDialogVisible.value = true
    }
  } else if (type === 'submit') {
    // 提交数据
    const mode = actionConfig?.selectionMode || 'none'
    const text = actionConfig?.confirmText || `确定要提交吗？（选择模式: ${mode === 'none' ? '无需选择' : mode === 'single' ? '单选' : '多选'}）`
    ElMessage.info(`[预览] 提交数据 — ${text}`)
  } else {
    ElMessage.info(`操作: ${type || '未配置'}（预览模式）`)
  }
}

// ---- 事件处理 ----
const handleToolbarAction = (action: string) => {
  const actionConfig = findToolbarActionConfig(action)
  console.log('[PreviewPanel] toolbar action:', action, 'config:', JSON.stringify(actionConfig))
  if (actionConfig) {
    openTarget(actionConfig, false)
  } else {
    ElMessage.info(`操作: ${action}（预览模式）`)
  }
}

const handleAction = (action: string, row: Record<string, any>) => {
  if (action === 'delete') {
    ElMessage.info('删除（预览模式）')
    return
  }
  const actionConfig = findRowActionConfig(action)
  if (actionConfig) {
    openTarget(actionConfig, action === 'edit', row)
  } else {
    ElMessage.info(`操作: ${action}（预览模式）`)
  }
}
</script>

<style scoped lang="scss">
.preview-panel {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  min-height: 400px;

  .preview-label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 12px;
    padding: 4px 8px;
    background: #e4e7ed;
    border-radius: 4px;
    display: inline-block;
  }

  .search-card {
    margin-bottom: 16px;
  }
}

.no-tip {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
}
</style>
