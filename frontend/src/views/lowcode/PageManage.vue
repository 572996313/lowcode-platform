<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="页面名称">
          <el-input v-model="queryParams.keyword" placeholder="请输入页面名称" clearable />
        </el-form-item>
        <el-form-item label="页面类型">
          <el-select v-model="queryParams.pageType" placeholder="请选择" clearable>
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增页面
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="pageName" label="页面名称" min-width="150" />
      <el-table-column prop="pageCode" label="页面编码" width="150" />
      <el-table-column prop="pageType" label="页面类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ pageTypeMap[row.pageType] || row.pageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="published" label="发布状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.published" type="success">已发布</el-tag>
          <el-tag v-else type="info">未发布</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="routePath" label="路由路径" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button
            v-if="!row.published"
            type="success"
            link
            @click="handlePublish(row)"
          >
            发布
          </el-button>
          <el-button
            v-else
            type="warning"
            link
            @click="handleUnpublish(row)"
          >
            取消发布
          </el-button>
          <el-button type="primary" link @click="handlePreview(row)">预览</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.current"
      v-model:page-size="queryParams.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
      style="margin-top: 16px; justify-content: flex-end;"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <!-- 新增页面时显示模板选择 -->
      <div v-if="!isEdit && selectedTemplate">
        <div class="template-info">
          <div class="info-item">
            <span class="label">模板名称:</span>
            <span class="value">{{ selectedTemplate.templateName }}</span>
          </div>
          <div class="info-item">
            <span class="label">模板类型:</span>
            <el-tag size="small">{{ selectedTemplate.templateType }}</el-tag>
          </div>
          <div class="info-item">
            <span class="label">布局类型:</span>
            <el-tag size="small" type="success">{{ selectedTemplate.layoutType }}</el-tag>
          </div>
        </div>
        <el-divider />

        <el-form :model="formData" label-width="100px" :rules="formRules" ref="formRef">
          <el-form-item label="页面名称" prop="pageName">
            <el-input v-model="formData.pageName" placeholder="请输入页面名称" />
          </el-form-item>
          <el-form-item label="页面编码" prop="pageCode">
            <el-input v-model="formData.pageCode" placeholder="请输入页面编码" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="formData.status" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 新增页面显示模板选择 -->
      <div v-else-if="!isEdit && !selectedTemplate">
        <div class="template-selection">
          <p class="hint">选择一个模板开始创建页面</p>
          <div class="template-list">
            <div
              v-for="template in templates"
              :key="template.id"
              class="template-item"
              @click="selectTemplate(template)"
            >
              <div class="template-icon">
                <el-icon size="32"><Files /></el-icon>
              </div>
              <div class="template-details">
                <div class="template-name">{{ template.templateName }}</div>
                <div class="template-desc">{{ template.description }}</div>
              </div>
              <div class="template-type">
                <el-tag size="small">{{ template.templateType }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑页面 -->
      <el-form v-else label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="页面名称" prop="pageName">
          <el-input v-model="formData.pageName" placeholder="请输入页面名称" />
        </el-form-item>
        <el-form-item label="页面编码" prop="pageCode">
          <el-input v-model="formData.pageCode" placeholder="请输入页面编码" disabled />
        </el-form-item>
        <el-form-item label="页面类型" prop="pageType">
          <el-select v-model="formData.pageType" placeholder="请选择">
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">{{ isEdit ? '取消' : selectedTemplate ? '上一步' : '取消' }}</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :disabled="!isEdit && !selectedTemplate"
          >
            {{ isEdit ? '确定保存' : '下一步' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Refresh, Plus, Files } from '@element-plus/icons-vue'
import {
  getPageList,
  createPage,
  updatePage,
  deletePage,
  publishPage,
  unpublishPage,
  getPageTemplates,
  createPageFromTemplate,
  type PageConfig,
  type PageTemplate
} from '@/api/page'

const router = useRouter()

const loading = ref(false)
const tableData = ref<PageConfig[]>([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '',
  pageType: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增页面')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const formData = reactive<Partial<PageConfig>>({
  pageName: '',
  pageCode: '',
  pageType: 'list',
  status: true
})

// 模板相关
const templates = ref<PageTemplate[]>([])
const selectedTemplate = ref<PageTemplate | null>(null)
const templatesLoading = ref(false)

const formRules = computed(() => {
  if (selectedTemplate.value && !isEdit.value) {
    // 新增页面第二阶段：只需要名称必填
    return {
      pageName: [{ required: true, message: '请输入页面名称', trigger: 'blur' }],
      pageCode: [{ required: true, message: '请输入页面编码', trigger: 'blur' }]
    }
  }
  return {
    pageName: [{ required: true, message: '请输入页面名称', trigger: 'blur' }],
    pageCode: [{ required: true, message: '请输入页面编码', trigger: 'blur' }],
    pageType: [{ required: true, message: '请选择页面类型', trigger: 'change' }]
  }
})

const pageTypeMap: Record<string, string> = {
  list: '列表页',
  form: '表单页',
  detail: '详情页',
  custom: '自定义'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPageList(queryParams)
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const loadTemplates = async () => {
  templatesLoading.value = true
  try {
    const data = await getPageTemplates()
    templates.value = data || []
  } catch (error) {
    console.error('加载模板列表失败:', error)
  } finally {
    templatesLoading.value = false
  }
}

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.pageType = ''
  queryParams.current = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新建页面：选择模板'
  selectedTemplate.value = null
  editId.value = null
  Object.assign(formData, {
    pageName: '',
    pageCode: '',
    pageType: 'list',
    status: true
  })
  loadTemplates()
  dialogVisible.value = true
}

const selectTemplate = (template: PageTemplate) => {
  selectedTemplate.value = template
  dialogTitle.value = '新建页面：填写信息'
  // 自动填充页面类型
  formData.pageType = template.templateType as any
}

const handleEdit = (row: PageConfig) => {
  isEdit.value = true
  dialogTitle.value = '编辑页面'
  selectedTemplate.value = null
  editId.value = row.id!
  Object.assign(formData, {
    pageName: row.pageName,
    pageCode: row.pageCode,
    pageType: row.pageType,
    status: row.status
  })
  dialogVisible.value = true
}

const handleDesign = (row: PageConfig) => {
  router.push({
    path: '/lowcode/PageDesigner',
    query: { id: row.id }
  })
}

const handleCancel = () => {
  if (selectedTemplate.value && !isEdit.value) {
    // 返回模板选择
    dialogTitle.value = '新建页面：选择模板'
    selectedTemplate.value = null
  } else {
    dialogVisible.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (selectedTemplate.value && !isEdit.value) {
        // 从模板创建页面
        const pageCode = formData.pageCode || `page_${Date.now()}`
        const pageId = await createPageFromTemplate({
          templateCode: selectedTemplate.value.templateCode,
          pageName: formData.pageName!,
          pageCode
        })
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadData()
        // 跳转到设计器
        router.push({
          path: '/lowcode/PageDesigner',
          query: { id: pageId }
        })
        return
      }

      if (isEdit.value && editId.value) {
        // 编辑页面
        await updatePage(editId.value, {
          pageName: formData.pageName!,
          pageType: formData.pageType,
          status: formData.status
        })
        ElMessage.success('更新成功')
        dialogVisible.value = false
        loadData()
      }
    } catch (error) {
      console.error('保存失败:', error)
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  selectedTemplate.value = null
}

const handleDelete = async (row: PageConfig) => {
  try {
    await ElMessageBox.confirm('确定要删除该页面配置吗？', '提示', { type: 'warning' })
    await deletePage(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 取消删除
  }
}

// 发布页面
const handlePublish = async (row: PageConfig) => {
  try {
    const { value } = await ElMessageBox.prompt(
      '请输入路由路径（如 /user/list）',
      '发布页面',
      {
        inputValue: row.routePath || `/${row.pageCode}`,
        inputPattern: /^\/[a-zA-Z0-9/_-]+$/,
        inputErrorMessage: '路由路径格式不正确，必须以 / 开头，只包含字母、数字、下划线、连字符和斜杠'
      }
    )

    await publishPage(row.id!, { routePath: value })
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    // 取消发布
  }
}

// 取消发布
const handleUnpublish = async (row: PageConfig) => {
  try {
    await ElMessageBox.confirm('确定要取消发布该页面吗？', '提示', { type: 'warning' })
    await unpublishPage(row.id!)
    ElMessage.success('已取消发布')
    loadData()
  } catch (error) {
    // 取消操作
  }
}

// 预览页面
const handlePreview = (row: PageConfig) => {
  // 统一使用预览路由，通过 query 参数传递页面 ID
  const previewUrl = `${window.location.origin}/page/preview?id=${row.id}`
  window.open(previewUrl, '_blank')
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.template-info {
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .label {
      width: 100px;
      color: #606266;
      font-weight: 500;
    }

    .value {
      color: #303133;
    }
  }
}

.template-selection {
  .hint {
    margin: 0 0 16px 0;
    color: #909399;
    font-size: 14px;
  }

  .template-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    max-height: 400px;
    overflow-y: auto;

    .template-item {
      display: flex;
      align-items: center;
      padding: 16px;
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
        transform: translateY(-2px);
      }

      .template-icon {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        border-radius: 6px;
        margin-right: 12px;
        color: #409eff;
        flex-shrink: 0;
      }

      .template-details {
        flex: 1;
        min-width: 0;

        .template-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }

        .template-desc {
          font-size: 12px;
          color: #909399;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .template-type {
        margin-left: 8px;
      }
    }
  }
}
</style>
