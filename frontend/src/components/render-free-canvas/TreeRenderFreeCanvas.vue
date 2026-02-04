<template>
  <div
    v-if="componentVisible"
    class="tree-render-free-canvas"
    :style="componentStyle"
  >
    <el-tree
      :data="treeData"
      :props="treeProps"
      :node-key="config.idField || 'id'"
      :default-expand-all="config.defaultExpandAll"
      :expand-on-click-node="!config.draggable"
      :show-checkbox="config.showCheckbox"
      :draggable="config.draggable"
      :highlight-current="true"
      @node-click="handleNodeClick"
      @check-change="handleCheckChange"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <span v-if="config.showIcon" class="node-icon">
            <el-icon><Folder /></el-icon>
          </span>
          <span class="node-label">{{ node.label }}</span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { ComponentPosition, ComponentStyle, TreeComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: TreeComponentConfig
  position: ComponentPosition
  style?: ComponentStyle
}

const props = defineProps<Props>()

// 树数据
const treeData = ref<any[]>([])

// 组件可见性（enabled 属性）
const componentVisible = computed(() => {
  // 如果父组件传递了 enabled 属性，可以根据它控制
  // 这里我们假设组件已经在上层被过滤了，所以默认为 true
  return true
})

/**
 * 计算组件样式
 */
const componentStyle = computed(() => {
  const baseStyle: Record<string, any> = {
    left: props.position.x + 'px',
    top: props.position.y + 'px',
    width: props.position.width + 'px',
    height: props.position.height + 'px',
    zIndex: props.position.zIndex || 1
  }

  // 合并自定义样式
  if (props.style) {
    Object.assign(baseStyle, props.style.customStyles)
    if (props.style.backgroundColor) baseStyle.backgroundColor = props.style.backgroundColor
    if (props.style.borderRadius) baseStyle.borderRadius = props.style.borderRadius
    if (props.style.border) baseStyle.border = props.style.border
    if (props.style.padding) baseStyle.padding = props.style.padding
    if (props.style.margin) baseStyle.margin = props.style.margin
    if (props.style.boxShadow) baseStyle.boxShadow = props.style.boxShadow
    if (props.style.opacity !== undefined) baseStyle.opacity = props.style.opacity
  }

  return baseStyle
})

/**
 * 树节点属性映射
 */
const treeProps = computed(() => ({
  label: props.config.displayField || 'name',
  children: props.config.childrenField || 'children'
}))

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.config.dataSource) {
    treeData.value = []
    return
  }

  try {
    const { type, api, sql, static: staticData } = props.config.dataSource

    if (type === 'static' && staticData) {
      // 静态数据
      treeData.value = staticData
    } else if (type === 'api' && api) {
      // API 数据
      const res = await fetch(api.url, {
        method: api.method,
        headers: api.headers || {},
        body: api.method === 'POST' ? JSON.stringify(api.params || {}) : undefined
      })
      const data = await res.json()
      treeData.value = data.data || data
    }
  } catch (error) {
    console.error('加载树数据失败:', error)
  }
}

/**
 * 节点点击事件
 */
function handleNodeClick(data: any) {
  console.log('节点点击:', data)
}

/**
 * 复选框状态改变事件
 */
function handleCheckChange(data: any, checked: boolean) {
  console.log('复选框状态改变:', data, checked)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.tree-render-free-canvas {
  position: absolute;
  overflow: auto;
  background: #fff;
  border-radius: 4px;

  .el-tree {
    background: transparent;
  }
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;

  .node-icon {
    color: #409eff;
  }

  .node-label {
    font-size: 14px;
  }
}
</style>
