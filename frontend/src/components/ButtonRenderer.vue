<template>
  <el-button
    v-if="isVisible"
    :type="config.buttonType || 'default'"
    :size="config.buttonSize || 'default'"
    :icon="config.icon"
    :plain="config.plain"
    :round="config.round"
    :circle="config.circle"
    :loading="config.loading"
    :disabled="config.disabled"
    @click="handleClick"
  >
    {{ config.buttonName }}
  </el-button>
</template>

<script setup lang="ts">
import type { ButtonConfig } from '@/api/button'

interface Props {
  config: ButtonConfig
  context?: any // 上下文数据（row, form, selection等）
}

const props = defineProps<Props>()

const emit = defineEmits<{
  click: [event: MouseEvent, config: ButtonConfig]
}>()

// 检查权限
const hasPermission = computed(() => {
  if (!props.config.perms) return true
  // TODO: 实现权限检查逻辑
  return true
})

// 检查显示条件
const isVisible = computed(() => {
  if (!props.config.visible) return false
  if (!props.config.showCondition) return true

  try {
    const context = props.context || {}
    // 安全的求值方式
    const func = new Function('row', 'form', 'selection', `return ${props.config.showCondition}`)
    return func(context.row, context.form, context.selection)
  } catch (e) {
    console.error('显示条件求值失败:', e)
    return true
  }
})

const handleClick = (event: MouseEvent) => {
  if (!hasPermission.value) {
    ElMessage.warning('您没有权限执行此操作')
    return
  }

  // 发出点击事件
  emit('click', event, props.config)
}
</script>

<script lang="ts">
export default {
  name: 'ButtonRenderer'
}
</script>

<style scoped lang="scss">
</style>
