/**
 * 占位符渲染器（运行时）
 */
<template>
  <div class="spacer-render-wrapper" :style="wrapperStyle" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { SpacerComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config?: SpacerComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => ({
  ...props.style
}))
</script>

<style scoped lang="scss">
.spacer-render-wrapper {
  width: 100%;
  height: 100%;
}
</style>
