<template>
  <div class="form-style-template-manage" v-loading="loading">
    <div class="page-header">
      <h2>表单样式模板管理</h2>
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
        <el-table-column label="样式配置" width="300">
          <template #default="{ row }">
            <el-tag v-for="(value, key) in parseStyleConfig(row.styleConfig)" :key="key" size="small" style="margin: 2px">
              {{ key }}: {{ value }}
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

        <el-form-item label="标签宽度">
          <el-input-number v-model="styleConfig.labelWidth" :min="40" :max="200" :step="10" />
          <span style="margin-left: 8px; color: var(--el-text-color-secondary)">px</span>
        </el-form-item>
        <el-form-item label="标签位置">
          <el-select v-model="styleConfig.labelPosition" placeholder="请选择">
            <el-option label="左侧" value="left" />
            <el-option label="右侧" value="right" />
            <el-option label="顶部" value="top" />
          </el-select>
        </el-form-item>
        <el-form-item label="表单列数">
          <el-select v-model="styleConfig.layoutCols" placeholder="请选择">
            <el-option label="1列" :value="1" />
            <el-option label="2列" :value="2" />
            <el-option label="3列" :value="3" />
            <el-option label="4列" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="组件尺寸">
          <el-select v-model="styleConfig.size" placeholder="请选择">
            <el-option label="大" value="large" />
            <el-option label="默认" value="default" />
            <el-option label="小" value="small" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示边框">
          <el-switch v-model="styleConfig.border" />
        </el-form-item>
        <el-form-item label="背景颜色">
          <el-color-picker v-model="styleConfig.backgroundColor" show-alpha />
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
  getFormStyleTemplatePage,
  createFormStyleTemplate,
  updateFormStyleTemplate,
  deleteFormStyleTemplate,
  toggleFormStyleTemplateStatus,
  type FormStyleTemplate
} from '@/api/form-style-template'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const templates = ref<FormStyleTemplate[]>([])

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

const formData = reactive<FormStyleTemplate>({
  templateName: '',
  templateCode: '',
  description: '',
  sortOrder: 0,
  status: true,
  styleConfig: ''
})

const styleConfig = reactive({
  labelWidth: 100,
  labelPosition: 'right',
  layoutCols: 2,
  size: 'default',
  border: false,
  backgroundColor: ''
})

const formRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [
    { required: true, message: '请输入模板编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '模板编码只能包含字母、数字和下划线', trigger: 'blur' }
  ]
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
    const result = await getFormStyleTemplatePage({
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
    labelWidth: 100,
    labelPosition: 'right',
    layoutCols: 2,
    size: 'default',
    border: false,
    backgroundColor: ''
  })
  dialogVisible.value = true
}

const handleEdit = (template: FormStyleTemplate) => {
  isEdit.value = true
  Object.assign(formData, template)
  const config = parseStyleConfig(template.styleConfig)
  Object.assign(styleConfig, {
    labelWidth: config.labelWidth || 100,
    labelPosition: config.labelPosition || 'right',
    layoutCols: config.layoutCols || 2,
    size: config.size || 'default',
    border: config.border || false,
    backgroundColor: config.backgroundColor || ''
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
      if (styleConfig.labelWidth !== 100) config.labelWidth = styleConfig.labelWidth
      if (styleConfig.labelPosition !== 'right') config.labelPosition = styleConfig.labelPosition
      if (styleConfig.layoutCols !== 2) config.layoutCols = styleConfig.layoutCols
      if (styleConfig.size !== 'default') config.size = styleConfig.size
      if (styleConfig.border) config.border = true
      if (styleConfig.backgroundColor) config.backgroundColor = styleConfig.backgroundColor

      formData.styleConfig = JSON.stringify(config)

      if (isEdit.value && formData.id) {
        await updateFormStyleTemplate(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createFormStyleTemplate(formData)
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

const handleToggleStatus = async (template: FormStyleTemplate) => {
  try {
    await toggleFormStyleTemplateStatus(template.id!)
    ElMessage.success('状态更新成功')
    loadTemplates()
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
    // 恢复原状态
    template.status = !template.status
  }
}

const handleDelete = async (template: FormStyleTemplate) => {
  try {
    await ElMessageBox.confirm(`确定要删除模板「${template.templateName}」吗？`, '提示', {
      type: 'warning'
    })
    await deleteFormStyleTemplate(template.id!)
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
.form-style-template-manage {
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
