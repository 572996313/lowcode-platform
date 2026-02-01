<template>
  <div v-if="buttons.length > 0" class="toolbar-render" :class="`align-${align}`">
    <ButtonRenderer
      v-for="button in buttons"
      :key="button.id || button.buttonId"
      :config="button"
      @click="handleClick"
    />
  </div>
  <div v-else class="toolbar-empty">
    <span>暂无按钮</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ButtonRenderer from './ButtonRenderer.vue'
import type { ButtonConfig } from '@/api/button'

interface Props {
  buttons?: Array<ButtonConfig | { buttonId?: number }>
  align?: 'left' | 'center' | 'right'
}

interface Emits {
  (e: 'click', event: MouseEvent, config: ButtonConfig): void
}

const props = withDefaults(defineProps<Props>(), {
  buttons: () => [],
  align: 'left'
})

const emit = defineEmits<Emits>()

const handleClick = (event: MouseEvent, config: ButtonConfig) => {
  emit('click', event, config)
}
</script>

<style scoped lang="scss">
.toolbar-render {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;

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

.toolbar-empty {
  padding: 16px 12px;
  text-align: center;
  color: #909399;
  font-size: 13px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
}
</style>
