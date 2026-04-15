<template>
  <div class="form-preview-panel">
    <div class="preview-toolbar">
      <span class="preview-label">实时预览</span>
      <el-radio-group v-model="previewMode" size="small">
        <el-radio-button value="add">新增</el-radio-button>
        <el-radio-button value="edit">编辑</el-radio-button>
        <el-radio-button value="view">查看</el-radio-button>
      </el-radio-group>
    </div>

    <ConfigToolbar
      :title="config.pageName || '表单标准页面'"
      :buttons="filteredButtons"
      @action="handleToolbarAction"
    />

    <el-card shadow="never">
      <ConfigFormPage
        ref="configFormPageRef"
        :config="config"
        v-model="formData"
        :mode="previewMode"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import ConfigToolbar from '../components/ConfigToolbar.vue'
import ConfigFormPage from '../components/ConfigFormPage.vue'
import type { FormPageConfigResponse, FormPageMode, FormToolbarButton } from '@/api/form-standard'

const props = defineProps<{
  config: FormPageConfigResponse
}>()

const previewMode = ref<FormPageMode>('add')
const formData = ref<Record<string, any>>({})
const configFormPageRef = ref()

// 根据模式过滤按钮
const filteredButtons = computed(() => {
  return (props.config.toolbar?.buttons || []).filter((btn: FormToolbarButton) => {
    if (!btn.showInModes) return true
    return btn.showInModes.includes(previewMode.value)
  })
})

const handleToolbarAction = (action: string) => {
  if (action === 'save') {
    ElMessage.success('保存成功（预览模式）')
  } else if (action === 'submit') {
    ElMessage.success('提交成功（预览模式）')
  } else if (action === 'edit') {
    previewMode.value = 'edit'
  } else if (action === 'back') {
    ElMessage.info('返回（预览模式）')
  } else {
    ElMessage.info(`操作: ${action}（预览模式）`)
  }
}
</script>

<style scoped lang="scss">
.form-preview-panel {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  min-height: 400px;

  .preview-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .preview-label {
      font-size: 12px;
      color: #909399;
      padding: 4px 8px;
      background: #e4e7ed;
      border-radius: 4px;
    }
  }
}
</style>
