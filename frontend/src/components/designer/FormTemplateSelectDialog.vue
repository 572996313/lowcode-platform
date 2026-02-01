<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择表单模板"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="template-select-container">
      <el-radio-group v-model="selectedTemplateCode" class="template-list">
        <div
          v-for="template in templates"
          :key="template.templateCode"
          class="template-card"
          :class="{ active: selectedTemplateCode === template.templateCode }"
          @click="selectedTemplateCode = template.templateCode"
        >
          <el-radio :value="template.templateCode" class="template-radio">
            <div class="template-content">
              <div class="template-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="template-info">
                <h4 class="template-name">{{ template.templateName }}</h4>
                <p class="template-desc">{{ template.description }}</p>
              </div>
            </div>
          </el-radio>
        </div>
      </el-radio-group>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :disabled="!selectedTemplateCode" @click="handleConfirm">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { getAllTemplates, type FormTemplateMeta } from '@/components/templates/TemplateRegistry'

const emit = defineEmits<{
  confirm: [templateCode: string]
  close: []
}>()

const dialogVisible = ref(false)
const selectedTemplateCode = ref('')

const templates = getAllTemplates()

const open = () => {
  dialogVisible.value = true
  selectedTemplateCode.value = ''
}

const handleClose = () => {
  dialogVisible.value = false
  selectedTemplateCode.value = ''
  emit('close')
}

const handleConfirm = () => {
  emit('confirm', selectedTemplateCode.value)
  handleClose()
}

defineExpose({
  open
})
</script>

<style scoped lang="scss">
.template-select-container {
  min-height: 300px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.template-card {
  border: 2px solid var(--el-border-color);
  border-radius: 8px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    border-color: var(--el-color-primary-light-5);
    background-color: var(--el-color-primary-light-9);
  }

  &.active {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }
}

.template-radio {
  width: 100%;
  margin: 0;

  :deep(.el-radio__label) {
    width: 100%;
    padding: 0;
  }
}

.template-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
}

.template-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--el-color-primary-light-9);
  border-radius: 8px;
  color: var(--el-color-primary);
  font-size: 24px;
}

.template-info {
  flex: 1;
}

.template-name {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.template-desc {
  margin: 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
</style>