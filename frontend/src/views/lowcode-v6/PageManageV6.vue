<template>
  <div class="page-manage-v6">
    <div class="page-header">
      <h3>页面管理</h3>
      <el-button type="primary" :icon="Plus" @click="handleCreate">
        新建页面
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="页面名称">
          <el-input
            v-model="queryParams.pageName"
            placeholder="请输入页面名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="页面编码">
          <el-input
            v-model="queryParams.pageCode"
            placeholder="请输入页面编码"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="页面类型">
          <el-select
            v-model="queryParams.pageType"
            placeholder="请选择"
            clearable
            @clear="handleSearch"
          >
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select
            v-model="queryParams.published"
            placeholder="请选择"
            clearable
            @clear="handleSearch"
          >
            <el-option label="已发布" :value="true" />
            <el-option label="草稿" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="page-content">
      <el-table
        v-loading="loading"
        :data="pageList"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="pageName" label="页面名称" min-width="150" />
        <el-table-column prop="pageCode" label="页面编码" min-width="150" />
        <el-table-column prop="pageType" label="页面类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.pageType === 'list'" type="primary">列表页</el-tag>
            <el-tag v-else-if="row.pageType === 'form'" type="success">表单页</el-tag>
            <el-tag v-else-if="row.pageType === 'detail'" type="warning">详情页</el-tag>
            <el-tag v-else type="info">自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.published" type="success">已发布</el-tag>
            <el-tag v-else type="info">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="400" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDesign(row)">
              设计
            </el-button>
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" text @click="handlePreview(row)">
              预览
            </el-button>
            <el-button
              v-if="!row.published"
              type="warning"
              size="small"
              text
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.published"
              type="warning"
              size="small"
              text
              @click="handleUnpublish(row)"
            >
              取消发布
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="页面预览"
      width="90%"
      :fullscreen="isFullscreen"
      destroy-on-close
    >
      <PageRenderV6
        v-if="previewVisible && currentPageConfig"
        :page-config="currentPageConfig"
      />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button :icon="FullScreen" @click="toggleFullscreen">
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="页面名称" prop="pageName">
          <el-input
            v-model="formData.pageName"
            placeholder="请输入页面名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="页面编码" prop="pageCode">
          <el-input
            v-model="formData.pageCode"
            placeholder="请输入页面编码（如: user_list）"
            maxlength="30"
            show-word-limit
          >
            <template #append>
              <el-tooltip content="编码用于生成路由，创建后不建议修改">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="页面类型" prop="pageType">
          <el-select v-model="formData.pageType" placeholder="请选择页面类型" style="width: 100%">
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="布局类型" prop="layoutType">
          <el-select v-model="formData.layoutType" placeholder="请选择布局类型" style="width: 100%">
            <el-option label="自由画布" value="free-canvas" />
            <el-option label="上下布局" value="top-bottom" />
            <el-option label="左右布局" value="left-right" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入页面描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ dialogMode === 'create' ? '创建并设计' : '保存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, FullScreen, Search, Refresh, QuestionFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getPageList, publishPage, deletePage, unpublishPage, getPage, createPage, updatePage } from '@/api/page'
import PageRenderV6 from './PageRenderV6.vue'
import type { NewPageConfig } from '@/types/page-v6'

const router = useRouter()

// ============ 列表数据 ============
const loading = ref(false)
const pageList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// ============ 搜索参数 ============
const queryParams = reactive({
  pageName: '',
  pageCode: '',
  pageType: '',
  published: undefined as boolean | undefined
})

// ============ 对话框 ============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogMode = ref<'create' | 'edit'>('create')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const formData = reactive({
  id: undefined as number | undefined,
  pageName: '',
  pageCode: '',
  pageType: 'custom',
  layoutType: 'free-canvas',
  remark: '',
  status: true
})

// ============ 表单验证规则 ============
const rules: FormRules = {
  pageName: [
    { required: true, message: '请输入页面名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  pageCode: [
    { required: true, message: '请输入页面编码', trigger: 'blur' },
    {
      pattern: /^[a-z][a-z0-9_]*$/,
      message: '编码必须以小写字母开头，只能包含小写字母、数字和下划线',
      trigger: 'blur'
    },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  pageType: [
    { required: true, message: '请选择页面类型', trigger: 'change' }
  ]
}

// ============ 预览 ============
const previewVisible = ref(false)
const isFullscreen = ref(false)
const currentPageConfig = ref<NewPageConfig | null>(null)

// ============ 初始化 ============
onMounted(() => {
  loadPageList()
})

// ============ 列表加载 ============
async function loadPageList() {
  loading.value = true
  try {
    const res = await getPageList({
      current: currentPage.value,
      size: pageSize.value,
      pageName: queryParams.pageName || undefined,
      pageCode: queryParams.pageCode || undefined,
      pageType: queryParams.pageType || undefined,
      published: queryParams.published
    })
    pageList.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载页面列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// ============ 搜索 ============
function handleSearch() {
  currentPage.value = 1
  loadPageList()
}

function handleReset() {
  Object.assign(queryParams, {
    pageName: '',
    pageCode: '',
    pageType: '',
    published: undefined
  })
  currentPage.value = 1
  loadPageList()
}

// ============ 分页 ============
function handlePageChange(page: number) {
  currentPage.value = page
  loadPageList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadPageList()
}

// ============ 新增 ============
function handleCreate() {
  dialogMode.value = 'create'
  dialogTitle.value = '新建页面'
  resetForm()
  dialogVisible.value = true
}

// ============ 设计 ============
function handleDesign(row: any) {
  // 根据布局类型跳转到对应的设计器
  const layoutType = row.layoutType || 'free-canvas'

  if (layoutType === 'free-canvas') {
    // 自由画布设计器
    router.push(`/lowcode/FreeCanvasDesigner/${row.id}`)
  } else {
    // 内置布局设计器（V6 版本）
    router.push(`/lowcode-v6/PageDesignerV6/${row.id}`)
  }
}

// ============ 编辑 ============
async function handleEdit(row: any) {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑页面'
  Object.assign(formData, {
    id: row.id,
    pageName: row.pageName,
    pageCode: row.pageCode,
    pageType: row.pageType || 'custom',
    layoutType: row.layoutType || 'free-canvas',
    remark: row.remark || '',
    status: row.status !== false
  })
  dialogVisible.value = true
}

// ============ 状态切换 ============
async function handleStatusChange(row: any) {
  const originalStatus = row.status
  try {
    await updatePage(row.id, { status: row.status })
    ElMessage.success('状态更新成功')
  } catch (error: any) {
    // 失败时恢复原状态
    row.status = originalStatus
    ElMessage.error(error.message || '状态更新失败')
  }
}

// ============ 表单提交 ============
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const payload = {
        pageName: formData.pageName,
        pageCode: formData.pageCode,
        pageType: formData.pageType,
        layoutType: formData.layoutType,
        configJson: '{}',
        configTemplate: '{}',
        configVersion: 10,
        remark: formData.remark,
        status: formData.status
      }

      if (dialogMode.value === 'edit' && formData.id) {
        await updatePage(formData.id, payload)
        ElMessage.success('更新成功')
        dialogVisible.value = false
        loadPageList()
      } else {
        const newId = await createPage(payload)
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadPageList()
        // 跳转到设计器
        router.push(`/lowcode/FreeCanvasDesigner/${newId}`)
      }
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// ============ 表单重置 ============
function resetForm() {
  Object.assign(formData, {
    id: undefined,
    pageName: '',
    pageCode: '',
    pageType: 'custom',
    layoutType: 'free-canvas',
    remark: '',
    status: true
  })
  formRef.value?.clearValidate()
}

// ============ 预览 ============
async function handlePreview(row: any) {
  try {
    loading.value = true
    const pageData = await getPage(row.id)

    const configJson = pageData.configJson || pageData.configTemplate
    if (!configJson) {
      ElMessage.warning('页面配置为空，无法预览')
      return
    }

    let pageConfig: NewPageConfig
    try {
      pageConfig = JSON.parse(configJson)
    } catch (e) {
      ElMessage.error('页面配置格式错误')
      return
    }

    currentPageConfig.value = pageConfig
    previewVisible.value = true
  } catch (error: any) {
    ElMessage.error('加载页面配置失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// ============ 发布 ============
async function handlePublish(row: any) {
  try {
    const { value: routePath } = await ElMessageBox.prompt(
      '请输入页面路由路径（如：/pages/custom）',
      '发布页面',
      {
        confirmButtonText: '确认发布',
        cancelButtonText: '取消',
        inputPattern: /^\/.+/,
        inputErrorMessage: '路由路径必须以 / 开头'
      }
    )

    await publishPage(row.id, { routePath })
    ElMessage.success('发布成功')
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
      console.error(error)
    }
  }
}

// ============ 取消发布 ============
async function handleUnpublish(row: any) {
  try {
    await ElMessageBox.confirm(
      '取消发布后页面将不再可访问，是否继续？',
      '确认取消发布',
      { type: 'warning' }
    )

    await unpublishPage(row.id)
    ElMessage.success('已取消发布')
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消发布失败')
      console.error(error)
    }
  }
}

// ============ 删除 ============
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除页面"${row.pageName}"吗？删除后无法恢复。`,
      '确认删除',
      { type: 'warning' }
    )

    await deletePage(row.id)
    ElMessage.success('删除成功')
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

// ============ 全屏切换 ============
function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}
</script>

<style scoped lang="scss">
.page-manage-v6 {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 40px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
  }
}

.search-form {
  margin-bottom: 16px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-content {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  :deep(.el-pagination) {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
