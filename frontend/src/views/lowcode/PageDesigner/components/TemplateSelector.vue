<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择页面模板"
    width="900px"
    :close-on-click-modal="false"
  >
    <div class="template-selector">
      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索模板"
          clearable
          style="width: 200px"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="selectedType"
          placeholder="页面类型"
          clearable
          style="width: 150px; margin-left: 10px"
          @change="handleFilter"
        >
          <el-option label="全部" value="" />
          <el-option label="列表页" value="list" />
          <el-option label="表单页" value="form" />
          <el-option label="详情页" value="detail" />
          <el-option label="自定义" value="custom" />
        </el-select>
      </div>

      <!-- 模板网格 -->
      <div class="template-grid" v-loading="loading">
        <div
          v-for="template in filteredTemplates"
          :key="template.id"
          class="template-card"
          :class="{ selected: selectedTemplate?.id === template.id }"
          @click="selectTemplate(template)"
        >
          <div class="template-preview">
            <img v-if="template.previewImage" :src="template.previewImage" :alt="template.templateName" />
            <div v-else class="preview-placeholder">
              <el-icon size="48"><Files /></el-icon>
            </div>
            <div v-if="template.isSystem" class="system-badge">系统</div>
          </div>
          <div class="template-info">
            <div class="template-name">{{ template.templateName }}</div>
            <div class="template-desc">{{ template.description }}</div>
            <div class="template-tags">
              <el-tag size="small" type="info">{{ template.templateType }}</el-tag>
              <el-tag size="small" type="success">{{ template.layoutType }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="filteredTemplates.length === 0" description="没有找到匹配的模板" />
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="!selectedTemplate">
          确认选择
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Files } from '@element-plus/icons-vue'
import { getPageTemplates, type PageTemplate } from '@/api/page'

// Props
interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'select': [template: PageTemplate]
}>()

// 状态
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
const loading = ref(false)
const templates = ref<PageTemplate[]>([])
const selectedTemplate = ref<PageTemplate | null>(null)
const keyword = ref('')
const selectedType = ref('')

// 计算筛选后的模板
const filteredTemplates = computed(() => {
  let result = templates.value

  // 关键词筛选
  if (keyword.value) {
    const kw = keyword.value.toLowerCase()
    result = result.filter(t =>
      t.templateName.toLowerCase().includes(kw) ||
      t.description?.toLowerCase().includes(kw)
    )
  }

  // 类型筛选
  if (selectedType.value) {
    result = result.filter(t => t.templateType === selectedType.value)
  }

  return result
})

// 获取模板列表
const loadTemplates = async () => {
  loading.value = true
  try {
    const data = await getPageTemplates()
    templates.value = data || []
  } catch (error) {
    console.error('加载模板列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择模板
const selectTemplate = (template: PageTemplate) => {
  selectedTemplate.value = template
}

// 搜索处理
const handleSearch = () => {
  // 使用 computed 自动处理
}

// 筛选处理
const handleFilter = () => {
  // 使用 computed 自动处理
}

// 取消
const handleCancel = () => {
  dialogVisible.value = false
  selectedTemplate.value = null
}

// 确认
const handleConfirm = () => {
  if (selectedTemplate.value) {
    emit('select', selectedTemplate.value)
    dialogVisible.value = false
    selectedTemplate.value = null
  }
}

// 组件挂载时加载模板
onMounted(() => {
  if (props.modelValue) {
    loadTemplates()
  }
})

// 监听对话框打开
watch(() => props.modelValue, (newVal) => {
  if (newVal && templates.value.length === 0) {
    loadTemplates()
  }
})
</script>

<style scoped lang="scss">
.template-selector {
  .filter-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .template-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    max-height: 500px;
    overflow-y: auto;
    padding: 4px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: #dcdfe6;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-track {
      background: #f5f7fa;
    }
  }

  .template-card {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }

    &.selected {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }

  .template-preview {
    position: relative;
    height: 160px;
    background: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .preview-placeholder {
      color: #909399;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
    }

    .system-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: #409eff;
      color: white;
      padding: 2px 8px;
      border-radius: 2px;
      font-size: 12px;
    }
  }

  .template-info {
    padding: 12px;

    .template-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 6px;
    }

    .template-desc {
      font-size: 12px;
      color: #909399;
      margin-bottom: 8px;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .template-tags {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }
  }
}
</style>
