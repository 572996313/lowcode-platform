/**
 * 分割线渲染器（运行时）
 */
<template>
  <div class="divider-render-wrapper" :style="wrapperStyle">
    <el-divider
      :content-position="config.contentPosition || 'center'"
      :direction="config.direction || 'horizontal'"
    >
      {{ config.text || '' }}
    </el-divider>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { DividerComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: DividerComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'flex',
  alignItems: 'center'
}))
</script>

<style scoped lang="scss">
.divider-render-wrapper {
  width: 100%;
  height: 100%;

  :deep(.el-divider) {
    margin: 8px 0;
  }

  :deep(.el-divider--vertical) {
    height: 100%;
    margin: 0 8px;
  }
}
</style>
