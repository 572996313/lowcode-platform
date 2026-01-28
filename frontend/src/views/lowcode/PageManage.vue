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
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="页面名称" prop="pageName">
          <el-input v-model="formData.pageName" placeholder="请输入页面名称" />
        </el-form-item>
        <el-form-item label="页面编码" prop="pageCode">
          <el-input v-model="formData.pageCode" placeholder="请输入页面编码" />
        </el-form-item>
        <el-form-item label="页面类型" prop="pageType">
          <el-select v-model="formData.pageType" placeholder="请选择">
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getPageList, createPage, updatePage, deletePage, type PageConfig } from '@/api/page'

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

const formRules = {
  pageName: [{ required: true, message: '请输入页面名称', trigger: 'blur' }],
  pageCode: [{ required: true, message: '请输入页面编码', trigger: 'blur' }],
  pageType: [{ required: true, message: '请选择页面类型', trigger: 'change' }]
}

const pageTypeMap: Record<string, string> = {
  list: '列表页',
  form: '表单页',
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

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.pageType = ''
  queryParams.current = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增页面'
  editId.value = null
  Object.assign(formData, {
    pageName: '',
    pageCode: '',
    pageType: 'list',
    status: true,
    configJsonObject: {
      searchArea: { enabled: true, title: '查询条件', fields: [] },
      contentArea: { type: 'table', configId: 0, title: '', showToolbar: true }
    },
    layoutConfigObject: { searchAreaHeight: '80px', contentAreaFlex: 1 }
  })
  dialogVisible.value = true
}

const handleEdit = (row: PageConfig) => {
  isEdit.value = true
  dialogTitle.value = '编辑页面'
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

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      // 准备提交的数据 - 使用 configJsonObject 和 layoutConfigObject（api 会自动转为 JSON 字符串）
      const submitData: Partial<PageConfig> = {
        pageName: formData.pageName!,
        pageCode: formData.pageCode!,
        pageType: formData.pageType,
        status: formData.status,
        configJsonObject: formData.configJsonObject || {
          searchArea: { enabled: true, title: '查询条件', fields: [] },
          contentArea: { type: 'table', configId: 0, title: '', showToolbar: true }
        },
        layoutConfigObject: formData.layoutConfigObject || {
          searchAreaHeight: '80px',
          contentAreaFlex: 1
        }
      }

      if (isEdit.value && editId.value) {
        // 编辑页面
        await updatePage(editId.value, submitData)
        ElMessage.success('更新成功')
      } else {
        // 新增页面 - 创建后直接跳转到设计器
        const pageId = await createPage(submitData as PageConfig)
        ElMessage.success('创建成功')
        dialogVisible.value = false
        // 跳转到设计器
        router.push({
          path: '/lowcode/PageDesigner',
          query: { id: pageId, newlyCreated: 'true' }
        })
        return
      }

      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存失败:', error)
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
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
</style>
