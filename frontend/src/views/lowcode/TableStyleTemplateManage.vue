<template>
  <div class="table-style-template-manage" v-loading="loading">
    <div class="page-header">
      <h2>表格样式模板管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增模板
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchParams">
        <el-form-item label="模板名称">
          <el-input v-model="searchParams.templateName" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="模板编码">
          <el-input v-model="searchParams.templateCode" placeholder="请输入模板编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="请选择状态" clearable style="width: 100px">
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="templates" border stripe>
        <el-table-column prop="templateName" label="模板名称" width="150" />
        <el-table-column prop="templateCode" label="模板编码" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="样式配置" width="350">
          <template #default="{ row }">
            <el-tag v-for="(value, key) in parseStyleConfig(row.styleConfig)" :key="key" size="small" style="margin: 2px">
              {{ formatConfigKey(key) }}: {{ value }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :disabled="row.isSystem"
              @change="handleToggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isSystem ? 'warning' : ''" size="small">
              {{ row.isSystem ? '系统模板' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="!row.isSystem"
              size="small"
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadTemplates"
        @current-change="loadTemplates"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑模板' : '新增模板'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input
            v-model="formData.templateCode"
            placeholder="请输入模板编码（英文）"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="模板描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="2"
            placeholder="请输入模板描述"
          />
        </el-form-item>

        <el-divider content-position="left">样式配置</el-divider>

        <el-form-item label="显示边框">
          <el-switch v-model="styleConfig.border" />
        </el-form-item>
        <el-form-item label="斑马纹">
          <el-switch v-model="styleConfig.stripe" />
        </el-form-item>
        <el-form-item label="组件尺寸">
          <el-select v-model="styleConfig.size" placeholder="请选择">
            <el-option label="大" value="large" />
            <el-option label="默认" value="default" />
            <el-option label="小" value="small" />
          </el-select>
        </el-form-item>
        <el-form-item label="开启分页">
          <el-switch v-model="styleConfig.pagination" />
        </el-form-item>
        <el-form-item label="每页条数" v-if="styleConfig.pagination">
          <el-input-number v-model="styleConfig.pageSize" :min="5" :max="100" :step="5" />
        </el-form-item>
        <el-form-item label="显示序号">
          <el-switch v-model="styleConfig.showIndex" />
        </el-form-item>
        <el-form-item label="开启多选">
          <el-switch v-model="styleConfig.selection" />
        </el-form-item>

        <el-divider content-position="left">其他配置</el-divider>

        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getTableStyleTemplatePage,
  createTableStyleTemplate,
  updateTableStyleTemplate,
  deleteTableStyleTemplate,
  toggleTableStyleTemplateStatus,
  type TableStyleTemplate
} from '@/api/table-style-template'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const templates = ref<TableStyleTemplate[]>([])

const searchParams = reactive({
  templateName: '',
  templateCode: '',
  status: undefined as boolean | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive<TableStyleTemplate>({
  templateName: '',
  templateCode: '',
  description: '',
  sortOrder: 0,
  status: true,
  styleConfig: ''
})

const styleConfig = reactive({
  border: true,
  stripe: true,
  size: 'default',
  pagination: true,
  pageSize: 10,
  showIndex: false,
  selection: false
})

const formRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [
    { required: true, message: '请输入模板编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '模板编码只能包含字母、数字和下划线', trigger: 'blur' }
  ]
}

// 格式化配置键名
const formatConfigKey = (key: string) => {
  const map: Record<string, string> = {
    border: '边框',
    stripe: '斑马纹',
    size: '尺寸',
    pagination: '分页',
    pageSize: '每页条数',
    showIndex: '显示序号',
    selection: '多选'
  }
  return map[key] || key
}

// 解析样式配置
const parseStyleConfig = (configStr: string) => {
  try {
    return JSON.parse(configStr || '{}')
  } catch {
    return {}
  }
}

// 加载模板列表
const loadTemplates = async () => {
  loading.value = true
  try {
    const result = await getTableStyleTemplatePage({
      ...searchParams,
      current: pagination.current,
      size: pagination.size
    })
    templates.value = result.records
    pagination.total = result.total
  } catch (error) {
    ElMessage.error('加载模板列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadTemplates()
}

const handleReset = () => {
  searchParams.templateName = ''
  searchParams.templateCode = ''
  searchParams.status = undefined
  pagination.current = 1
  loadTemplates()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    templateName: '',
    templateCode: '',
    description: '',
    sortOrder: 0,
    status: true,
    styleConfig: ''
  })
  Object.assign(styleConfig, {
    border: true,
    stripe: true,
    size: 'default',
    pagination: true,
    pageSize: 10,
    showIndex: false,
    selection: false
  })
  dialogVisible.value = true
}

const handleEdit = (template: TableStyleTemplate) => {
  isEdit.value = true
  Object.assign(formData, template)
  const config = parseStyleConfig(template.styleConfig)
  Object.assign(styleConfig, {
    border: config.border ?? true,
    stripe: config.stripe ?? true,
    size: config.size || 'default',
    pagination: config.pagination ?? true,
    pageSize: config.pageSize || 10,
    showIndex: config.showIndex || false,
    selection: config.selection || false
  })
  dialogVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    saving.value = true
    try {
      // 构建 styleConfig JSON
      const config: Record<string, any> = {}
      if (styleConfig.border) config.border = true
      if (styleConfig.stripe) config.stripe = true
      if (styleConfig.size !== 'default') config.size = styleConfig.size
      if (!styleConfig.pagination) config.pagination = false
      else {
        config.pagination = true
        if (styleConfig.pageSize !== 10) config.pageSize = styleConfig.pageSize
      }
      if (styleConfig.showIndex) config.showIndex = true
      if (styleConfig.selection) config.selection = true

      formData.styleConfig = JSON.stringify(config)

      if (isEdit.value && formData.id) {
        await updateTableStyleTemplate(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createTableStyleTemplate(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadTemplates()
    } catch (error: any) {
      ElMessage.error(error.message || '保存失败')
    } finally {
      saving.value = false
    }
  })
}

const handleToggleStatus = async (template: TableStyleTemplate) => {
  try {
    await toggleTableStyleTemplateStatus(template.id!)
    ElMessage.success('状态更新成功')
    loadTemplates()
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
    // 恢复原状态
    template.status = !template.status
  }
}

const handleDelete = async (template: TableStyleTemplate) => {
  try {
    await ElMessageBox.confirm(`确定要删除模板「${template.templateName}」吗？`, '提示', {
      type: 'warning'
    })
    await deleteTableStyleTemplate(template.id!)
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped lang="scss">
.table-style-template-manage {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
    }
  }

  .search-card {
    margin-bottom: 16px;
  }
}
</style>
