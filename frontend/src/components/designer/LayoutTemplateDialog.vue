<template>
  <el-dialog
    v-model="visible"
    title="选择布局模板"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="template-dialog-content">
      <!-- 模板分类标签 -->
      <el-tabs v-model="activeCategory" type="card" class="template-tabs">
        <el-tab-pane label="标准布局" name="standard">
          <div class="template-grid">
            <div
              v-for="template in standardTemplates"
              :key="template.code"
              class="template-card"
              :class="{ active: selectedTemplate?.code === template.code }"
              @click="selectTemplate(template)"
            >
              <div class="template-preview" v-html="template.preview" />
              <div class="template-info">
                <div class="template-name">{{ template.name }}</div>
                <div class="template-description">{{ template.description }}</div>
              </div>
              <el-icon v-if="selectedTemplate?.code === template.code" class="check-icon">
                <Check />
              </el-icon>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 布局配置选项 -->
      <div v-if="selectedTemplate" class="layout-config">
        <div class="config-title">布局配置</div>
        <el-form label-width="100px" size="small">
          <el-form-item label="间距">
            <el-input v-model="layoutConfig.gap" placeholder="如：16px" />
          </el-form-item>
          <el-form-item label="内边距">
            <el-input v-model="layoutConfig.padding" placeholder="如：16px" />
          </el-form-item>
          <el-form-item label="背景色">
            <el-color-picker v-model="layoutConfig.background" />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedTemplate">
        确定使用此模板
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Check } from '@element-plus/icons-vue'
import type { V3Config } from '@/api/page'

interface TemplateInfo {
  code: string
  name: string
  description: string
  preview: string
  config: Partial<V3Config>
}

interface Props {
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm', config: { template: TemplateInfo; layoutConfig: Record<string, string> }): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = ref(false)
const activeCategory = ref('standard')
const selectedTemplate = ref<TemplateInfo | null>(null)
const layoutConfig = ref<Record<string, string>>({
  gap: '16px',
  padding: '0',
  background: ''
})

// 标准模板列表
const standardTemplates: TemplateInfo[] = [
  {
    code: 'top-bottom',
    name: '上下布局（列表页）',
    description: '查询区 + 工具栏 + 内容区',
    preview: `
      <div class="preview-box">
        <div class="preview-search">查询区</div>
        <div class="preview-toolbar">工具栏</div>
        <div class="preview-content">内容区</div>
      </div>
    `,
    config: {
      version: 2,
      layoutType: 'top-bottom',
      areas: [
        {
          id: 'search',
          type: 'search',
          name: '查询区',
          enabled: true,
          required: false,
          config: { title: '查询条件', fields: [] }
        },
        {
          id: 'content',
          type: 'content',
          name: '内容区',
          enabled: true,
          required: false,
          config: { componentType: 'table', configId: null, showToolbar: true }
        }
      ]
    }
  },
  {
    code: 'tree-table',
    name: '左树右表布局',
    description: '左侧树形导航 + 右侧内容区',
    preview: `
      <div class="preview-box preview-tree-table">
        <div class="preview-tree">树形区</div>
        <div class="preview-right">
          <div class="preview-search">查询区</div>
          <div class="preview-content">内容区</div>
        </div>
      </div>
    `,
    config: {
      version: 2,
      layoutType: 'tree-table',
      areas: [
        {
          id: 'tree',
          type: 'tree',
          name: '树形区',
          enabled: true,
          required: false,
          config: { title: '分类树', dataUrl: '' }
        },
        {
          id: 'search',
          type: 'search',
          name: '查询区',
          enabled: true,
          required: false,
          config: { title: '查询条件', fields: [] }
        },
        {
          id: 'content',
          type: 'content',
          name: '内容区',
          enabled: true,
          required: false,
          config: { componentType: 'table', configId: null }
        }
      ]
    }
  }
]

// 选择模板
const selectTemplate = (template: TemplateInfo) => {
  selectedTemplate.value = template
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  selectedTemplate.value = null
}

// 确认选择
const handleConfirm = () => {
  if (selectedTemplate.value) {
    emit('confirm', {
      template: selectedTemplate.value,
      layoutConfig: layoutConfig.value
    })
    handleClose()
  }
}

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
  },
  { immediate: true }
)

// 监听 visible 变化
watch(visible, (val) => {
  emit('update:modelValue', val)
})
</script>

<style scoped>
.template-dialog-content {
  padding: 0 8px;
}

.template-tabs {
  margin-bottom: 20px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.template-card {
  position: relative;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.template-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.template-card.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.template-preview {
  margin-bottom: 12px;
}

.template-info {
  text-align: center;
}

.template-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.template-description {
  font-size: 12px;
  color: #909399;
}

.check-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #409eff;
  font-size: 20px;
}

.layout-config {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.config-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
}

/* 预览样式 */
.preview-box {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px;
  background: #fafafa;
  min-height: 100px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.preview-toolbar-top,
.preview-toolbar-content,
.preview-toolbar {
  background: #409eff;
  color: white;
  padding: 6px 8px;
  border-radius: 2px;
  font-size: 11px;
  text-align: center;
}

.preview-search {
  background: #e6f7ff;
  border: 1px dashed #409eff;
  color: #409eff;
  padding: 6px 8px;
  border-radius: 2px;
  font-size: 11px;
  text-align: center;
}

.preview-content,
.preview-content-main {
  background: #f0f9ff;
  border: 1px dashed #67c23a;
  color: #67c23a;
  padding: 12px 8px;
  border-radius: 2px;
  font-size: 11px;
  text-align: center;
  flex: 1;
}

.preview-bottom {
  background: #fff9e6;
  border: 1px dashed #e6a23c;
  color: #e6a23c;
  padding: 8px;
  border-radius: 2px;
  font-size: 11px;
  text-align: center;
}

.preview-tree-table {
  flex-direction: row;
  gap: 8px;
}

.preview-tree {
  background: #f5f7fa;
  border: 1px dashed #909399;
  color: #909399;
  padding: 8px;
  border-radius: 2px;
  font-size: 11px;
  text-align: center;
  width: 60px;
  flex-shrink: 0;
}

.preview-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.preview-tabs {
  background: #fff;
  border-bottom: 2px solid #409eff;
  padding: 6px 8px;
  font-size: 10px;
  text-align: center;
  color: #409eff;
}
</style>
