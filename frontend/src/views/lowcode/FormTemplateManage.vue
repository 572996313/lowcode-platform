<template>
  <div class="form-template-manage" v-loading="loading">
    <div class="page-header">
      <h2>表单模板管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增模板
      </el-button>
    </div>

    <div class="template-grid">
      <div
        v-for="template in templates"
        :key="template.templateCode"
        class="template-card"
        :class="{ disabled: !template.status }"
      >
        <div class="template-preview">
          <el-icon :size="48"><Document /></el-icon>
        </div>
        <div class="template-info">
          <h3 class="template-name">{{ template.templateName }}</h3>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-meta">
            <el-tag :type="template.status ? 'success' : 'info'" size="small">
              {{ template.status ? '启用' : '禁用' }}
            </el-tag>
            <el-tag v-if="template.isSystem" type="warning" size="small">系统模板</el-tag>
          </div>
        </div>
        <div class="template-actions">
          <el-button size="small" @click="handleEdit(template)">编辑</el-button>
          <el-button
            size="small"
            :type="template.status ? 'warning' : 'success'"
            @click="handleToggleStatus(template)"
          >
            {{ template.status ? '禁用' : '启用' }}
          </el-button>
          <el-button
            v-if="!template.isSystem"
            size="small"
            type="danger"
            @click="handleDelete(template)"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑模板' : '新增模板'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input
            v-model="formData.templateCode"
            placeholder="请输入模板编码"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="模板描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入模板描述"
          />
        </el-form-item>
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
import { Plus, Document } from '@element-plus/icons-vue'
import { getAllTemplates, getAllTemplates as getApiTemplates, createTemplate, updateTemplate, deleteTemplate, type FormTemplate } from '@/api/form-template'
import { getAllTemplates as getRegistryTemplates, type FormTemplateMeta } from '@/components/templates/TemplateRegistry'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 合并前端注册的模板和后端数据库的模板
const templates = ref<FormTemplate[]>([])

const formData = reactive<FormTemplate>({
  templateName: '',
  templateCode: '',
  description: '',
  sortOrder: 0,
  status: true
})

const formRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }]
}

// 加载模板列表
const loadTemplates = async () => {
  loading.value = true
  try {
    // 获取前端注册的模板
    const registryTemplates = getRegistryTemplates()

    // 获取后端数据库的模板
    const apiTemplates = await getApiTemplates()

    // 合并模板信息
    templates.value = registryTemplates.map(registry => {
      const apiTemplate = apiTemplates.find(t => t.templateCode === registry.templateCode)
      return {
        templateName: registry.templateName,
        templateCode: registry.templateCode,
        description: registry.description,
        previewImage: registry.previewImage,
        isSystem: true,
        status: apiTemplate?.status ?? true,
        sortOrder: apiTemplate?.sortOrder ?? 0,
        ...apiTemplate
      }
    })

    // 添加后端自定义模板
    apiTemplates.forEach(apiTemplate => {
      if (!registryTemplates.find(t => t.templateCode === apiTemplate.templateCode)) {
        templates.value.push(apiTemplate)
      }
    })

    templates.value.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  } catch (error) {
    ElMessage.error('加载模板列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    templateName: '',
    templateCode: '',
    description: '',
    sortOrder: 0,
    status: true
  })
  dialogVisible.value = true
}

const handleEdit = (template: FormTemplate) => {
  isEdit.value = true
  Object.assign(formData, template)
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
      if (isEdit.value && formData.id) {
        await updateTemplate(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createTemplate(formData)
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

const handleToggleStatus = async (template: FormTemplate) => {
  if (template.isSystem) {
    ElMessage.warning('系统模板不能修改状态')
    return
  }

  try {
    await updateTemplate(template.id!, !template.status)
    ElMessage.success('状态更新成功')
    loadTemplates()
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  }
}

const handleDelete = async (template: FormTemplate) => {
  try {
    await ElMessageBox.confirm(`确定要删除模板「${template.templateName}」吗？`, '提示', {
      type: 'warning'
    })
    await deleteTemplate(template.id!)
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
.form-template-manage {
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

  .template-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .template-card {
    background-color: #fff;
    border: 1px solid var(--el-border-color);
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    &.disabled {
      opacity: 0.6;
    }
  }

  .template-preview {
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, var(--el-color-primary-light-9), var(--el-color-primary-light-8));
    color: var(--el-color-primary);
  }

  .template-info {
    padding: 16px;

    .template-name {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 500;
    }

    .template-desc {
      margin: 0 0 12px 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      min-height: 36px;
    }

    .template-meta {
      display: flex;
      gap: 8px;
    }
  }

  .template-actions {
    padding: 12px 16px;
    border-top: 1px solid var(--el-border-color);
    display: flex;
    gap: 8px;
  }
}
</style>