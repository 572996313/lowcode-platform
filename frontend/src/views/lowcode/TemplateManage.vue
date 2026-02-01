<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.keyword" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="模板类型">
          <el-select v-model="queryParams.templateType" placeholder="请选择" clearable>
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="仪表板" value="dashboard" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="布局类型">
          <el-select v-model="queryParams.layoutType" placeholder="请选择" clearable>
            <el-option label="上下布局" value="top-bottom" />
            <el-option label="树表布局" value="tree-table" />
            <el-option label="标签页布局" value="tabs" />
            <el-option label="左右布局" value="left-right" />
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
        <el-icon><Plus /></el-icon>新增模板
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="templateName" label="模板名称" min-width="150" />
      <el-table-column prop="templateCode" label="模板编码" width="150" />
      <el-table-column prop="templateType" label="模板类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ templateTypeMap[row.templateType] || row.templateType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="layoutType" label="布局类型" width="120">
        <template #default="{ row }">
          <el-tag type="success">{{ layoutTypeMap[row.layoutType] || row.layoutType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="isSystem" label="系统模板" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isSystem" type="warning">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleViewAreas(row)">区域</el-button>
          <el-button
            type="danger"
            link
            @click="handleDelete(row)"
            :disabled="row.isSystem"
          >
            删除
          </el-button>
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
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" label-width="120px" :rules="formRules" ref="formRef">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input
            v-model="formData.templateCode"
            placeholder="请输入模板编码（如：list_default）"
            :disabled="isEdit"
          />
          <div class="form-tip">用于唯一标识模板，创建后不可修改</div>
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="formData.templateType" placeholder="请选择">
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="仪表板" value="dashboard" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="布局类型" prop="layoutType">
          <el-select v-model="formData.layoutType" placeholder="请选择">
            <el-option label="上下布局" value="top-bottom" />
            <el-option label="树表布局" value="tree-table" />
            <el-option label="标签页布局" value="tabs" />
            <el-option label="左右布局" value="left-right" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="预览图URL">
          <el-input v-model="formData.previewImage" placeholder="请输入预览图URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入模板描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="配置模板">
          <el-input
            v-model="formData.configTemplate"
            type="textarea"
            :rows="8"
            placeholder='请输入配置模板（JSON格式），例如：{"areas": [...]}'
          />
          <div class="form-tip">留空将使用默认配置</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 区域配置查看弹窗 -->
    <el-dialog
      v-model="areasDialogVisible"
      title="模板区域配置"
      width="800px"
    >
      <div v-if="currentTemplate">
        <div class="template-header">
          <div class="info-item">
            <span class="label">模板名称:</span>
            <span class="value">{{ currentTemplate.templateName }}</span>
          </div>
          <div class="info-item">
            <span class="label">模板编码:</span>
            <span class="value">{{ currentTemplate.templateCode }}</span>
          </div>
        </div>
        <el-divider />
        <div v-if="areas.length > 0">
          <el-table :data="areas" border>
            <el-table-column prop="id" label="区域ID" width="150" />
            <el-table-column prop="name" label="区域名称" width="150" />
            <el-table-column prop="type" label="区域类型" width="120" />
            <el-table-column prop="description" label="描述" min-width="200" />
            <el-table-column prop="required" label="必需" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.required" type="danger">是</el-tag>
                <el-tag v-else type="info">否</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-empty v-else description="该模板没有配置区域信息" />
      </div>
      <template #footer>
        <el-button @click="areasDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getPageTemplatePage,
  createPageTemplate,
  updatePageTemplate,
  deletePageTemplate,
  getPageTemplateAreas,
  type PageTemplate
} from '@/api/page'
import type { AreaInfo } from '@/api/page'

const loading = ref(false)
const tableData = ref<PageTemplate[]>([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '',
  templateType: '',
  layoutType: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增模板')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const formData = reactive<Partial<PageTemplate>>({
  templateName: '',
  templateCode: '',
  templateType: 'list',
  layoutType: 'top-bottom',
  previewImage: '',
  description: '',
  status: 1,
  isSystem: false,
  configTemplate: ''
})

const areasDialogVisible = ref(false)
const currentTemplate = ref<PageTemplate | null>(null)
const areas = ref<AreaInfo[]>([])

const formRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [
    { required: true, message: '请输入模板编码', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_]*$/, message: '编码必须以小写字母开头，只能包含小写字母、数字和下划线', trigger: 'blur' }
  ],
  templateType: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
  layoutType: [{ required: true, message: '请选择布局类型', trigger: 'change' }]
}

const templateTypeMap: Record<string, string> = {
  list: '列表页',
  form: '表单页',
  detail: '详情页',
  dashboard: '仪表板',
  custom: '自定义'
}

const layoutTypeMap: Record<string, string> = {
  'top-bottom': '上下布局',
  'tree-table': '树表布局',
  'tabs': '标签页布局',
  'left-right': '左右布局',
  custom: '自定义'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPageTemplatePage(queryParams)
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
  queryParams.templateType = ''
  queryParams.layoutType = ''
  queryParams.current = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增模板'
  editId.value = null
  Object.assign(formData, {
    templateName: '',
    templateCode: '',
    templateType: 'list',
    layoutType: 'top-bottom',
    previewImage: '',
    description: '',
    status: 1,
    isSystem: false,
    configTemplate: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: PageTemplate) => {
  isEdit.value = true
  dialogTitle.value = '编辑模板'
  editId.value = row.id
  Object.assign(formData, {
    templateName: row.templateName,
    templateCode: row.templateCode,
    templateType: row.templateType,
    layoutType: row.layoutType,
    previewImage: row.previewImage || '',
    description: row.description || '',
    status: row.status ? 1 : 0,
    isSystem: row.isSystem,
    configTemplate: row.configTemplate || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const data = {
        ...formData,
        status: formData.status === 1
      }

      if (isEdit.value && editId.value) {
        await updatePageTemplate(editId.value, data)
        ElMessage.success('更新成功')
      } else {
        await createPageTemplate(data)
        ElMessage.success('创建成功')
      }

      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      console.error('保存失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleDelete = async (row: PageTemplate) => {
  if (row.isSystem) {
    ElMessage.warning('系统模板不允许删除')
    return
  }

  try {
    await ElMessageBox.confirm('确定要删除该模板吗？', '提示', { type: 'warning' })
    await deletePageTemplate(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 取消删除
  }
}

const handleViewAreas = async (row: PageTemplate) => {
  try {
    currentTemplate.value = row
    areas.value = await getPageTemplateAreas(row.id!)
    areasDialogVisible.value = true
  } catch (error) {
    console.error('加载区域信息失败:', error)
    ElMessage.error('加载区域信息失败')
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.template-header {
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
</style>
