<template>
  <div class="config-toolbar">
    <h2>{{ title }}</h2>
    <div class="toolbar-buttons">
      <el-button
        v-for="btn in buttons"
        :key="btn.action"
        :type="btn.btnType as any || ''"
        @click="emit('action', btn.action)"
      >
        <el-icon v-if="btn.icon"><component :is="btn.icon" /></el-icon>
        {{ btn.label }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ToolbarButton } from '@/api/table-standard'

defineProps<{
  title: string
  buttons: ToolbarButton[]
}>()

const emit = defineEmits<{
  (e: 'action', action: string): void
}>()
</script>

<style scoped lang="scss">
.config-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    margin: 0;
    font-size: 20px;
    font-weight: 500;
  }

  .toolbar-buttons {
    display: flex;
    gap: 8px;
  }
}
</style>
