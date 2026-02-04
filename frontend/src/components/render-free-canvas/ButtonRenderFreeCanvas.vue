<template>
  <div
    v-if="componentVisible"
    class="button-render-free-canvas"
    :style="componentStyle"
  >
    <div class="button-group" :class="`align-${config.align || 'left'}`">
      <el-button
        v-for="btn in config.buttons"
        :key="btn.id"
        :type="btn.type"
        :icon="btn.icon"
        :size="config.size || 'default'"
        @click="handleButtonClick(btn)"
      >
        {{ btn.name }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import type { ComponentPosition, ComponentStyle, ButtonGroupComponentConfig, ButtonConfig } from '@/types/page-free-canvas'

interface Props {
  config: ButtonGroupComponentConfig
  position: ComponentPosition
  style?: ComponentStyle
}

const props = defineProps<Props>()

// 组件可见性
const componentVisible = computed(() => true)

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
 * 处理按钮点击
 */
async function handleButtonClick(btn: ButtonConfig) {
  console.log('按钮点击:', btn)

  // 确认消息
  if (btn.confirmMessage) {
    try {
      await ElMessageBox.confirm(btn.confirmMessage, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return
    }
  }

  // 执行动作
  const action = btn.action

  switch (action.type) {
    case 'add':
      ElMessage.success('新增')
      // TODO: 打开新增对话框
      break

    case 'edit':
      ElMessage.success('编辑')
      // TODO: 打开编辑对话框
      break

    case 'delete':
      ElMessage.success('删除')
      // TODO: 执行删除操作
      break

    case 'export':
      ElMessage.success('导出')
      // TODO: 执行导出操作
      break

    case 'refresh':
      ElMessage.success('刷新')
      // TODO: 刷新数据
      break

    case 'submit':
      ElMessage.success('提交')
      // TODO: 提交表单
      break

    case 'reset':
      ElMessage.success('重置')
      // TODO: 重置表单
      break

    case 'custom':
      if (action.apiEndpoint) {
        // TODO: 调用自定义 API
        console.log('调用自定义 API:', action.apiEndpoint)
      }
      if (action.redirectUrl) {
        // TODO: 跳转页面
        console.log('跳转到:', action.redirectUrl)
      }
      break
  }
}
</script>

<style scoped lang="scss">
.button-render-free-canvas {
  position: absolute;
  overflow: visible;

  .button-group {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;

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
}
</style>
