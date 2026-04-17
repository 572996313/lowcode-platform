/**
 * 页面设计器组件实例（卡片形式）
 */
<template>
  <div
    :class="['comp-card', { selected, linked: isLinked }]"
    @click.stop="handleClick"
  >
    <!-- 角标 -->
    <span v-if="isLinked" class="role-badge">副</span>
    <span v-else class="role-badge main-badge">主</span>

    <!-- 删除按钮 -->
    <div v-if="selected" class="delete-btn" @click.stop="handleDelete">
      <el-icon><Close /></el-icon>
    </div>

    <!-- 内容 -->
    <div class="card-body">
      <div class="card-icon">{{ componentIcon }}</div>
      <div class="card-info">
        <div class="card-name">{{ instance.name }}</div>
        <div class="card-desc">{{ previewText }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Close } from '@element-plus/icons-vue'
import type { ComponentInstance as CompInstance } from '@/types/page-free-canvas'

interface Props {
  instance: CompInstance
  selected?: boolean
}

interface Emits {
  (e: 'select', id: string): void
  (e: 'update', instance: CompInstance): void
  (e: 'delete', id: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const isLinked = computed(() => props.instance.role === 'linked')

const componentIcon = computed(() => {
  const map: Record<string, string> = {
    'table-standard': '📋',
    'form-standard': '📝'
  }
  return map[props.instance.type] || '📦'
})

const previewText = computed(() => {
  const map: Record<string, string> = {
    'table-standard': '工具栏 + 搜索 + 表格',
    'form-standard': '工具栏 + 表单分组'
  }
  return map[props.instance.type] || '组件'
})

function handleClick() {
  emit('select', props.instance.id)
}

function handleDelete() {
  emit('delete', props.instance.id)
}
</script>

<style scoped lang="scss">
.comp-card {
  position: relative;
  width: 200px;
  padding: 12px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  }

  &.selected {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }

  &.linked {
    border-style: dashed;
    background: #fdfaf3;

    &:hover, &.selected {
      border-color: #e6a23c;
      box-shadow: 0 2px 8px rgba(230, 162, 60, 0.15);
    }

    &.selected {
      box-shadow: 0 0 0 2px rgba(230, 162, 60, 0.2);
    }
  }

  .role-badge {
    position: absolute;
    top: -8px;
    left: 12px;
    font-size: 11px;
    padding: 1px 8px;
    border-radius: 8px;
    color: #fff;
    background: #e6a23c;
  }

  .main-badge {
    background: #409eff;
  }

  .delete-btn {
    position: absolute;
    top: -8px;
    right: -8px;
    width: 20px;
    height: 20px;
    background: #f56c6c;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 12px;
    cursor: pointer;
    z-index: 2;

    &:hover {
      background: #f78989;
    }
  }

  .card-body {
    display: flex;
    align-items: center;
    gap: 10px;

    .card-icon {
      font-size: 28px;
      flex-shrink: 0;
    }

    .card-info {
      flex: 1;
      min-width: 0;

      .card-name {
        font-size: 13px;
        font-weight: 500;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .card-desc {
        font-size: 11px;
        color: #909399;
        margin-top: 2px;
      }
    }
  }
}
</style>
