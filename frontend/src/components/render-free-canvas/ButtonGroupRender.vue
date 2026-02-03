/**
 * 按钮组渲染器（运行时）
 */
<template>
  <div
    :class="['button-group-render-wrapper', `align-${config.align || 'left'}`, `direction-${config.direction || 'horizontal'}`]"
    :style="wrapperStyle"
  >
    <el-button
      v-for="btn in config.buttons"
      :key="btn.id"
      :type="btn.type"
      :size="config.size || 'default'"
      :icon="btn.icon"
      @click="handleClick(btn)"
    >
      {{ btn.name }}
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { ButtonGroupComponentConfig, ButtonConfig } from '@/types/page-free-canvas'

interface Props {
  config: ButtonGroupComponentConfig
  style?: Record<string, any>
}

interface Emits {
  (e: 'action', action: any, button: ButtonConfig): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'flex',
  gap: '8px'
}))

function handleClick(btn: ButtonConfig) {
  if (btn.confirmMessage) {
    ElMessageBox.confirm(btn.confirmMessage, '确认操作', {
      type: 'warning'
    }).then(() => {
      emit('action', btn.action, btn)
    }).catch(() => {
      // 用户取消
    })
  } else {
    emit('action', btn.action, btn)
  }
}
</script>

<style scoped lang="scss">
.button-group-render-wrapper {
  padding: 8px;

  &.align-left {
    justify-content: flex-start;
  }

  &.align-center {
    justify-content: center;
  }

  &.align-right {
    justify-content: flex-end;
  }

  &.direction-horizontal {
    flex-direction: row;
  }

  &.direction-vertical {
    flex-direction: column;
  }
}
</style>
