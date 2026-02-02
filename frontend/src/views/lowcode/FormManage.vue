<template>
  <div class="form-manage">
    <div class="page-header">
      <h2>表单管理</h2>
    </div>

    <div class="content-container">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.formName"
          placeholder="请输入表单名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.formCode"
          placeholder="请输入表单编码"
          clearable
          style="width: 200px; margin-left: 12px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.sourceType"
          placeholder="来源类型"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="模板" value="template" />
          <el-option label="自定义" value="custom" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="handleSearch" style="margin-left: 12px">
          查询
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建表单
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="formName" label="表单名称" min-width="150" />
        <el-table-column prop="formCode" label="表单编码" min-width="150" />
        <el-table-column prop="tableId" label="关联表" width="120">
          <template #default="{ row }">
            {{ getTableName(row.tableId) }}
          </template>
        </el-table-column>
        <el-table-column label="来源类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.sourceType === 'template' ? 'success' : 'info'" size="small">
              {{ row.sourceType === 'template' ? '模板' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="formType" label="表单类型" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="字段数量" width="100">
          <template #default="{ row }">
            {{ row.fields?.length || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button
              type="success"
              size="small"
              link
              @click="handleFieldBinding(row)"
            >
              <el-icon><Link /></el-icon>字段绑定
            </el-button>
            <el-button
              type="warning"
              size="small"
              link
              @click="handleStyleTemplate(row)"
            >
              <el-icon><Brush /></el-icon>样式模板
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 样式模板选择对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      title="选择样式模板"
      width="500px"
    >
      <el-form :model="templateForm" label-width="100px">
        <el-form-item label="当前模板">
          <span>{{ getCurrentTemplateName(rowStyleTemplate?.styleTemplateId) }}</span>
        </el-form-item>
        <el-form-item label="选择模板">
          <el-select v-model="templateForm.styleTemplateId" placeholder="请选择样式模板" style="width: 100%">
            <el-option label="不使用模板" :value="null" />
            <el-option
              v-for="template in styleTemplates"
              :key="template.id"
              :label="template.templateName"
              :value="template.id"
            >
              <span>{{ template.templateName }}</span>
              <span style="color: var(--el-text-color-secondary); font-size: 12px; margin-left: 8px">
                {{ template.templateCode }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模板描述">
          <span style="color: var(--el-text-color-secondary)">{{ getSelectedTemplateDesc() }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveStyleTemplate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Link, Brush } from '@element-plus/icons-vue'
import { getFormList, deleteForm, updateForm, type FormConfig } from '@/api/form'
import { getAllFormStyleTemplates, type FormStyleTemplate } from '@/api/form-style-template'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  formName: '',
  formCode: '',
  sourceType: undefined as string | undefined,
  status: undefined as boolean | undefined
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<FormConfig[]>([])
const loading = ref(false)

// 数据库表映射
const tableMap = ref<Record<number, string>>({})

// 样式模板相关
const styleTemplates = ref<FormStyleTemplate[]>([])
const templateDialogVisible = ref(false)
const rowStyleTemplate = ref<FormConfig | null>(null)
const templateForm = reactive({
  styleTemplateId: null as number | null
})

// 获取表名
const getTableName = (tableId?: number) => {
  if (!tableId) return '-'
  return tableMap.value[tableId] || `表${tableId}`
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const result = await getFormList(params)
    tableData.value = result.records
    pagination.total = result.total

    // 构建表ID映射（这里简化处理，实际应从库表管理获取）
    result.records.forEach((item: any) => {
      if (item.tableId && item.tableName) {
        tableMap.value[item.tableId] = item.tableName
      }
    })
  } catch (error) {
    ElMessage.error('加载表单列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.formName = ''
  searchForm.formCode = ''
  searchForm.sourceType = undefined
  searchForm.status = undefined
  pagination.current = 1
  loadData()
}

// 新建表单
const handleCreate = () => {
  router.push({ name: 'FormDesigner' })
}

// 编辑表单
const handleEdit = (row: FormConfig) => {
  router.push({
    name: 'FormDesigner',
    query: { id: row.id?.toString() }
  })
}

// 字段绑定
const handleFieldBinding = (row: FormConfig) => {
  router.push({
    name: 'FormFieldBinding',
    query: { formId: row.id?.toString() }
  })
}

// 删除表单
const handleDelete = (row: FormConfig) => {
  ElMessageBox.confirm('确定要删除该表单吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteForm(row.id!)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 加载样式模板列表
const loadStyleTemplates = async () => {
  try {
    styleTemplates.value = await getAllFormStyleTemplates()
  } catch (error) {
    console.error('加载样式模板失败', error)
  }
}

// 打开样式模板选择对话框
const handleStyleTemplate = (row: FormConfig) => {
  rowStyleTemplate.value = row
  templateForm.styleTemplateId = row.styleTemplateId || null
  templateDialogVisible.value = true
}

// 获取当前模板名称
const getCurrentTemplateName = (templateId?: number) => {
  if (!templateId) return '未设置'
  const template = styleTemplates.value.find(t => t.id === templateId)
  return template?.templateName || '未知模板'
}

// 获取选中模板的描述
const getSelectedTemplateDesc = () => {
  if (!templateForm.styleTemplateId) return '-'
  const template = styleTemplates.value.find(t => t.id === templateForm.styleTemplateId)
  return template?.description || '-'
}

// 保存样式模板
const handleSaveStyleTemplate = async () => {
  if (!rowStyleTemplate.value?.id) return
  try {
    await updateForm(rowStyleTemplate.value.id, {
      styleTemplateId: templateForm.styleTemplateId || undefined
    })
    ElMessage.success('样式模板应用成功')
    templateDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('样式模板应用失败')
  }
}

// 页面挂载时加载数据
onMounted(() => {
  loadData()
  loadStyleTemplates()
})
</script>

<style lang="scss" scoped>
.form-manage {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    padding: 20px 24px;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
      color: #303133;
    }
  }

  .content-container {
    flex: 1;
    padding: 24px;
    overflow: auto;

    .search-bar {
      display: flex;
      align-items: center;
      background-color: #fff;
      padding: 16px 20px;
      border-radius: 4px;
      margin-bottom: 16px;
    }

    .toolbar {
      margin-bottom: 16px;
    }

    .pagination {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
