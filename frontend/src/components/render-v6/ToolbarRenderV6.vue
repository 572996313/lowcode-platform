<template>
  <div class="toolbar-render-v6" :class="`align-${config.align}`">
    <el-button
      v-for="btn in mergedButtons"
      :key="btn.id"
      :type="btn.type"
      :icon="btn.icon"
      :disabled="btn.disabled"
      @click="handleClick(btn)"
    >
      {{ btn.name }}
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ToolbarConfig, ToolbarButton } from '@/types/page-v6'

// Props
interface Props {
  config: ToolbarConfig
}

// Emits
interface Emits {
  (e: 'action', action: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

/**
 * 合并模板配置
 * 对于引用模式的按钮，合并模板配置和 overwrite
 */
const mergedButtons = computed(() => {
  return props.config.buttons.map(btn => mergeButtonConfig(btn))
})

/**
 * 合并按钮配置
 */
function mergeButtonConfig(btn: ToolbarButton): ToolbarButton {
  // 如果是引用模式且有 overwrite
  if (btn.isLinked && btn.overwrite) {
    return {
      ...btn,
      ...btn.overwrite
    }
  }
  // 独立模式或没有 overwrite，直接返回
  return btn
}

/**
 * 按钮点击
 */
function handleClick(btn: any) {
  emit('action', btn.action)
}
</script>

<style scoped lang="scss">
.toolbar-render-v6 {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;

  &.align-left {
    justify-content: flex-start;
  }

  &.align-center {
    justify-content: center;
  }

  &.align-right {
    justify-content: flex-end;
  }
}
</style>
