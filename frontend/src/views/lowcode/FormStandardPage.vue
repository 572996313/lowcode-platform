<template>
  <div class="form-standard-page" v-loading="loading">
    <ConfigToolbar
      :title="pageConfig?.pageName || '表单标准页面'"
      :buttons="filteredButtons"
      @action="handleToolbarAction"
    />

    <el-card shadow="never">
      <ConfigFormPage
        ref="configFormPageRef"
        :config="pageConfig"
        v-model="formData"
        :mode="formMode"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ConfigToolbar from './components/ConfigToolbar.vue'
import ConfigFormPage from './components/ConfigFormPage.vue'
import {
  getFormStandardConfig,
  getFormStandardData,
  createFormStandard,
  updateFormStandard,
  type FormPageConfigResponse,
  type FormPageMode,
  type FormToolbarButton
} from '@/api/form-standard'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const configFormPageRef = ref<InstanceType<typeof ConfigFormPage>>()

// 页面配置
const pageConfig = ref<FormPageConfigResponse | null>(null)

// 表单模式：从路由 query 读取，默认 add
const formMode = ref<FormPageMode>('add')

// 表单数据
const formData = ref<Record<string, any>>({})

// 根据模式过滤工具栏按钮
const filteredButtons = computed(() => {
  if (!pageConfig.value?.toolbar?.buttons) return []
  return pageConfig.value.toolbar.buttons.filter((btn: FormToolbarButton) => {
    if (!btn.showInModes) return true
    return btn.showInModes.includes(formMode.value)
  })
})

// 加载页面配置
const loadPageConfig = async () => {
  try {
    pageConfig.value = await getFormStandardConfig()
  } catch (error: any) {
    ElMessage.error('加载页面配置失败: ' + error.message)
  }
}

// 加载数据（编辑/查看模式）
const loadData = async (id: number) => {
  loading.value = true
  try {
    formData.value = await getFormStandardData(id)
  } catch (error: any) {
    ElMessage.error('加载数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 工具栏操作分发
const handleToolbarAction = async (action: string) => {
  if (action === 'save') {
    await handleSave()
  } else if (action === 'submit') {
    await handleSubmit()
  } else if (action === 'edit') {
    formMode.value = 'edit'
  } else if (action === 'back') {
    router.back()
  }
}

// 保存
const handleSave = async () => {
  if (!configFormPageRef.value) return
  const valid = await configFormPageRef.value.validate()
  if (!valid) return

  saving.value = true
  try {
    const data = configFormPageRef.value.getFormData()
    if (formMode.value === 'edit' && data.id) {
      await updateFormStandard(data.id, data)
      ElMessage.success('更新成功')
    } else {
      const newId = await createFormStandard(data)
      ElMessage.success('创建成功')
      // 创建后切换到编辑模式
      formMode.value = 'edit'
      formData.value = { ...formData.value, id: newId }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 提交（先保存再提交）
const handleSubmit = async () => {
  await handleSave()
  ElMessage.success('提交成功')
}

onMounted(async () => {
  // 从路由 query 读取模式
  const mode = route.query.mode as string
  const id = route.query.id as string

  if (mode && ['add', 'edit', 'view'].includes(mode)) {
    formMode.value = mode as FormPageMode
  }

  await loadPageConfig()

  // 编辑/查看模式需要加载数据
  if (id && formMode.value !== 'add') {
    await loadData(Number(id))
  }
})
</script>

<style scoped lang="scss">
.form-standard-page {
  padding: 20px;
}
</style>
