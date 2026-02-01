<template>
  <el-dialog
    v-model="visible"
    title="选择页面模板"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="template-select-dialog">
      <el-empty
        v-if="templates.length === 0"
        description="暂无可用模板"
      />

      <div v-else class="template-list">
        <div
          v-for="template in templates"
          :key="template.id"
          class="template-item"
          :class="{ 'selected': selectedTemplateId === template.id }"
          @click="selectTemplate(template.id)"
        >
          <!-- 预览图 -->
          <div class="template-preview">
            <img
              v-if="template.previewImage"
              :src="template.previewImage"
              :alt="template.name"
            />
            <div v-else class="preview-placeholder">
              <el-icon><Document /></el-icon>
              <span>{{ template.name }}</span>
            </div>
          </div>

          <!-- 模板信息 -->
          <div class="template-info">
            <div class="template-name">{{ template.name }}</div>
            <div class="template-description">{{ template.description }}</div>
            <div class="template-meta">
              <el-tag size="small" type="info">{{ template.category }}</el-tag>
              <el-tag size="small" :type="getTypeColor(template.type)">
                {{ getTypeLabel(template.type) }}
              </el-tag>
            </div>
          </div>

          <!-- 选中标记 -->
          <div v-if="selectedTemplateId === template.id" class="selected-mark">
            <el-icon><Check /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :disabled="!selectedTemplateId"
          @click="handleConfirm"
        >
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Document, Check } from '@element-plus/icons-vue'
import { TEMPLATES } from '@/config/templates/registry'
import type { TemplateMeta } from '@/types/template'

interface Props {
  modelValue: boolean
  templateType?: string
}

const props = withDefaults(defineProps<Props>(), {
  templateType: ''
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'confirm': [template: TemplateMeta]
}>()

const visible = ref(false)
const selectedTemplateId = ref<string>('')

// 根据类型过滤模板
const templates = ref<TemplateMeta[]>([])

// 获取类型颜色
const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    list: 'success',
    form: 'warning',
    tree: 'danger',
    custom: 'info'
  }
  return colorMap[type] || 'info'
}

// 获取类型标签
const getTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    list: '列表页',
    form: '表单页',
    tree: '树形页',
    custom: '自定义'
  }
  return labelMap[type] || type
}

// 选择模板
const selectTemplate = (id: string) => {
  selectedTemplateId.value = id
}

// 确认选择
const handleConfirm = () => {
  if (!selectedTemplateId.value) return

  const template = TEMPLATES.find(t => t.id === selectedTemplateId.value)
  if (template) {
    emit('confirm', template)
    handleClose()
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  selectedTemplateId.value = ''
  emit('update:modelValue', false)
}

// 监听外部 v-model 变化
watch(() => props.modelValue, (val) => {
  visible.value = val

  if (val) {
    // 过滤模板
    if (props.templateType) {
      templates.value = TEMPLATES.filter(t => t.type === props.templateType)
    } else {
      templates.value = TEMPLATES
    }
  }
})

// 监听内部 visible 变化，同步到外部
watch(visible, (val) => {
  if (val !== props.modelValue) {
    emit('update:modelValue', val)
  }
})
</script>

<style scoped>
.template-select-dialog {
  max-height: 60vh;
  overflow-y: auto;
}

.template-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.template-item {
  position: relative;
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.template-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.template-item.selected {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.template-preview {
  width: 120px;
  height: 90px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f5f7fa;
}

.template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.preview-placeholder .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.preview-placeholder span {
  font-size: 12px;
}

.template-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.template-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.template-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.template-meta {
  display: flex;
  gap: 6px;
  margin-top: auto;
}

.selected-mark {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background-color: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
