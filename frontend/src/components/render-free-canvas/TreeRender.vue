/**
 * 树组件渲染器（运行时）
 */
<template>
  <div :class="['tree-render-wrapper']" :style="wrapperStyle">
    <el-tree
      :data="displayData"
      :props="treeProps"
      :node-key="config.idField"
      :default-expand-all="config.defaultExpandAll"
      :show-checkbox="config.showCheckbox"
      :draggable="config.draggable"
      class="tree-render"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <span v-if="config.showIcon" class="node-icon">📁</span>
          <span class="node-label">{{ node.label }}</span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TreeComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: TreeComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => props.style || {})

const treeProps = computed(() => ({
  children: props.config.childrenField,
  label: props.config.displayField
}))

const displayData = computed(() => {
  if (props.config.dataSource?.type === 'static') {
    return props.config.dataSource.static || []
  }
  return []
})
</script>

<style scoped lang="scss">
.tree-render-wrapper {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: #fff;
  border-radius: 4px;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;

  .node-icon {
    font-size: 14px;
  }

  .node-label {
    font-size: 14px;
  }
}
</style>
