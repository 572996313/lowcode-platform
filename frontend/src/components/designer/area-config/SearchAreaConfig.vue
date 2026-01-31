<template>
  <div class="area-config search-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>基本配置</span>
      </div>
      <div class="section-body">
        <el-form-item label="标题">
          <el-input v-model="localConfig.title" placeholder="查询区标题" />
        </el-form-item>
        <el-form-item label="可折叠">
          <el-switch v-model="localConfig.collapsible" />
        </el-form-item>
      </div>
    </div>

    <div class="config-section">
      <div class="section-header">
        <span>查询字段</span>
        <el-button
          type="primary"
          size="small"
          @click="addSearchField"
        >
          添加字段
        </el-button>
      </div>
      <div class="section-body">
        <div ref="draggableContainer" class="draggable-wrapper">
          <div v-if="!localConfig.fields?.length" class="empty-hint">
            暂无查询字段，点击上方按钮添加
          </div>
          <div v-else class="field-list">
            <div
              v-for="(field, index) in localConfig.fields"
              :key="field.id"
              class="field-item"
            >
              <el-icon class="drag-handle">
                <Rank />
              </el-icon>
              <div class="field-info">
                <div class="field-label">{{ field.label }}</div>
                <div class="field-code">{{ field.fieldCode }}</div>
              </div>
              <div class="field-actions">
                <el-button
                  size="small"
                  text
                  type="primary"
                  @click="editField(field, index)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  text
                  type="danger"
                  @click="removeField(index)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 字段编辑对话框 -->
    <el-dialog
      v-model="fieldDialogVisible"
      title="编辑查询字段"
      width="600px"
    >
      <el-form :model="editingField" label-width="100px">
        <el-form-item label="字段标签">
          <el-input v-model="editingField.label" placeholder="显示名称" />
        </el-form-item>
        <el-form-item label="字段代码">
          <el-input v-model="editingField.fieldCode" placeholder="字段名" />
        </el-form-item>
        <el-form-item label="字段类型">
          <el-select v-model="editingField.type" placeholder="选择类型">
            <el-option label="输入框" value="input" />
            <el-option label="下拉选择" value="select" />
            <el-option label="日期选择" value="date" />
            <el-option label="日期范围" value="dateRange" />
            <el-option label="数字输入" value="number" />
          </el-select>
        </el-form-item>
        <el-form-item label="占位提示">
          <el-input v-model="editingField.placeholder" placeholder="占位符" />
        </el-form-item>
        <el-form-item label="必填">
          <el-switch v-model="editingField.required" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="fieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveField">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Rank } from '@element-plus/icons-vue'
import type { NormalizedArea } from '@/api/page'

interface Props {
  area: NormalizedArea
}

interface Emits {
  (e: 'update', area: NormalizedArea): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 拖拽容器引用
const draggableContainer = ref<HTMLElement | null>(null)

// 默认配置
const defaultConfig = { title: '查询条件', collapsible: true, fields: [] }

// 本地配置 - 使用只读 computed
const localConfig = computed(() => {
  const config = props.area.config
  if (!config) {
    return defaultConfig
  }
  return config
})

// 字段编辑
const fieldDialogVisible = ref(false)
const editingFieldIndex = ref(-1)
const editingField = ref<any>({})

const addSearchField = () => {
  const newField = {
    id: `field_${Date.now()}`,
    label: '新字段',
    fieldCode: '',
    type: 'input',
    placeholder: '',
    required: false
  }

  const currentConfig = localConfig.value
  const updatedConfig = {
    ...currentConfig,
    fields: [...(currentConfig.fields || []), newField]
  }

  emit('update', { ...props.area, config: updatedConfig })
}

const editField = (field: any, index: number) => {
  editingFieldIndex.value = index
  editingField.value = { ...field }
  fieldDialogVisible.value = true
}

const saveField = () => {
  if (editingFieldIndex.value >= 0) {
    const currentConfig = localConfig.value
    const updatedFields = [...(currentConfig.fields || [])]
    updatedFields[editingFieldIndex.value] = { ...editingField.value }

    const updatedConfig = {
      ...currentConfig,
      fields: updatedFields
    }

    emit('update', { ...props.area, config: updatedConfig })
  }
  fieldDialogVisible.value = false
}

const removeField = (index: number) => {
  const currentConfig = localConfig.value
  const updatedFields = [...(currentConfig.fields || [])]
  updatedFields.splice(index, 1)

  const updatedConfig = {
    ...currentConfig,
    fields: updatedFields
  }

  emit('update', { ...props.area, config: updatedConfig })
}
</script>

<style scoped lang="scss">
.area-config {
  .config-section {
    margin-bottom: 20px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    overflow: hidden;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 15px;
      background: var(--el-fill-color-light);
      border-bottom: 1px solid var(--el-border-color);
      font-weight: 500;
    }

    .section-body {
      padding: 15px;
    }
  }

  .empty-hint {
    text-align: center;
    padding: 30px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }

  .draggable-wrapper {
    min-height: 60px;
  }

  .field-list {
    display: flex;
    flex-direction: column;
  }

  .field-item {
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 8px;
    background: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .drag-handle {
      margin-right: 10px;
      cursor: move;
      color: var(--el-text-color-secondary);
    }

    .field-info {
      flex: 1;

      .field-label {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }

      .field-code {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }

    .field-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
