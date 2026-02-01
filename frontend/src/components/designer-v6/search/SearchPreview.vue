<template>
  <div class="search-preview">
    <div v-if="!config.enabled" class="disabled-overlay">
      <el-icon><Lock /></el-icon>
      <span>查询区已禁用</span>
    </div>

    <div v-else class="search-content">
      <div v-if="config.fields.length === 0" class="empty-state">
        <el-icon><Plus /></el-icon>
        <p>暂无查询字段</p>
        <span class="hint">从底部组件库拖拽字段到此处</span>
      </div>

      <el-form v-else :inline="true" :label-width="config.labelWidth + 'px'">
        <el-row :gutter="config.layout.gap">
          <el-col
            v-for="field in config.fields"
            :key="field.id"
            :span="field.span || config.layout.span"
          >
            <el-form-item
              :label="field.label"
              :required="field.required"
            >
              <div
                class="field-wrapper"
                :class="{ 'is-selected': isSelected(field) }"
                @click.stop="handleSelectField(field)"
              >
                <component
                  :is="getFieldComponent(field.fieldType)"
                  v-bind="getFieldProps(field)"
                />
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item>
              <el-button type="primary">查询</el-button>
              <el-button>重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'
import { Lock, Plus } from '@element-plus/icons-vue'
import {
  ElInput, ElInputNumber, ElSelect, ElDatePicker, ElSwitch
} from 'element-plus'
import type { SearchConfig, SelectedObject, SearchField, FieldType } from '@/types/page-v6'

// Props
interface Props {
  config: SearchConfig
  selected?: SelectedObject | null
}

// Emits
interface Emits {
  (e: 'update:selected', value: SelectedObject): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

/**
 * 获取字段组件
 */
function getFieldComponent(fieldType: FieldType) {
  const componentMap: Record<FieldType, any> = {
    input: ElInput,
    textarea: ElInput,
    select: ElSelect,
    radio: ElSelect,
    checkbox: ElSelect,
    date: ElDatePicker,
    datetime: ElDatePicker,
    number: ElInputNumber,
    switch: ElSwitch
  }
  return componentMap[fieldType] || ElInput
}

/**
 * 获取字段属性
 */
function getFieldProps(field: SearchField) {
  const baseProps = {
    placeholder: field.placeholder,
    style: { width: '100%' }
  }

  switch (field.fieldType) {
    case 'textarea':
      return {
        ...baseProps,
        type: 'textarea',
        rows: 2
      }

    case 'select':
    case 'radio':
    case 'checkbox':
      return {
        ...baseProps,
        children: field.options?.map(opt =>
          h('el-option', { label: opt.label, value: opt.value })
        )
      }

    case 'date':
      return {
        ...baseProps,
        type: 'date',
        clearable: true
      }

    case 'datetime':
      return {
        ...baseProps,
        type: 'datetime',
        clearable: true
      }

    case 'number':
      return {
        ...baseProps,
        min: field.numberConfig?.min,
        max: field.numberConfig?.max,
        step: field.numberConfig?.step
      }

    case 'switch':
      return {}

    default:
      return baseProps
  }
}

/**
 * 判断字段是否被选中
 */
function isSelected(field: SearchField): boolean {
  return props.selected?.type === 'field' &&
    props.selected.data.id === field.id
}

/**
 * 选中字段
 */
function handleSelectField(field: SearchField) {
  emit('update:selected', {
    type: 'field',
    data: field,
    parent: 'search'
  })
}
</script>

<style scoped lang="scss">
.search-preview {
  position: relative;
  padding: 16px;
}

.disabled-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #c0c4cc;

  .el-icon {
    font-size: 24px;
    margin-bottom: 8px;
  }

  span {
    font-size: 13px;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #909399;

  .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
    color: #c0c4cc;
  }

  p {
    margin: 0 0 4px;
    font-size: 14px;
    font-weight: 500;
  }

  .hint {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.search-content {
  .field-wrapper {
    display: inline-block;
    width: 100%;
    padding: 2px;
    border: 2px solid transparent;
    border-radius: 4px;
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      border-color: #c0c4cc;
      background: #f5f7fa;
    }

    &.is-selected {
      border-color: #409eff;
      background: #ecf5ff;
    }
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
  }
}
</style>
