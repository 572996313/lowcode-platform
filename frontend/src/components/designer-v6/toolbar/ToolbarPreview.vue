<template>
  <div class="toolbar-preview">
    <div v-if="!config.enabled" class="disabled-overlay">
      <el-icon><Lock /></el-icon>
      <span>工具栏已禁用</span>
    </div>

    <div v-else-if="config.buttons.length === 0" class="empty-state">
      <el-icon><Plus /></el-icon>
      <p>暂无按钮</p>
      <span class="hint">从底部组件库拖拽按钮到此处</span>
    </div>

    <div v-else class="toolbar-content" :class="`align-${config.align}`">
      <div
        v-for="btn in config.buttons"
        :key="btn.id"
        class="button-wrapper"
        :class="{ 'is-selected': isSelected(btn) }"
        @click.stop="handleSelectButton(btn)"
      >
        <el-button
          :type="btn.type"
          :icon="btn.icon"
          :disabled="btn.disabled"
          size="small"
        >
          {{ btn.name }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Lock, Plus } from '@element-plus/icons-vue'
import type { ToolbarConfig, SelectedObject, ToolbarButton } from '@/types/page-v6'

// Props
interface Props {
  config: ToolbarConfig
  selected?: SelectedObject | null
}

// Emits
interface Emits {
  (e: 'update:selected', value: SelectedObject): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

/**
 * 判断按钮是否被选中
 */
function isSelected(btn: ToolbarButton): boolean {
  return props.selected?.type === 'button' &&
    props.selected.data.id === btn.id
}

/**
 * 选中按钮
 */
function handleSelectButton(btn: ToolbarButton) {
  emit('update:selected', {
    type: 'button',
    data: btn,
    parent: 'toolbar'
  })
}
</script>

<style scoped lang="scss">
.toolbar-preview {
  position: relative;
  padding: 16px;
  min-height: 60px;
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

.toolbar-content {
  display: flex;
  gap: 8px;

  &.align-left {
    justify-content: flex-start;
  }

  &.align-center {
    justify-content: center;
  }

  &.align-right {
    justify-content: flex-end;
  }

  .button-wrapper {
    display: inline-block;
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
}
</style>
