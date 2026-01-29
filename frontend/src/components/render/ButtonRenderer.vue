<template>
  <el-button
    v-if="visible"
    :type="config.buttonType || 'default'"
    :size="config.buttonSize || 'default'"
    :icon="config.icon"
    :plain="config.plain"
    :round="config.round"
    :circle="config.circle"
    :loading="config.loading || loading"
    :disabled="config.disabled || disabled"
    :link="link"
    @click="handleClick"
  >
    {{ config.buttonName }}
  </el-button>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { ButtonConfig } from '@/api/button'

interface Props {
  config: ButtonConfig
  context?: {
    row?: any
    form?: any
    selection?: any[]
  }
  link?: boolean
}

interface Emits {
  (e: 'click', event: MouseEvent, config: ButtonConfig): void
}

const props = withDefaults(defineProps<Props>(), {
  link: false
})

const emit = defineEmits<Emits>()

const loading = ref(false)
const disabled = ref(false)

// 计算按钮是否可见
const visible = computed(() => {
  if (!props.config.visible) return true

  // 如果有显示条件，计算条件是否满足
  if (props.config.showCondition) {
    try {
      const context = {
        row: props.context?.row,
        form: props.context?.form,
        selection: props.context?.selection
      }
      const func = new Function('row', 'form', 'selection', `return ${props.config.showCondition}`)
      return func(context.row, context.form, context.selection)
    } catch (e) {
      console.error('计算按钮显示条件失败:', e)
      return true
    }
  }

  return true
})

const handleClick = (event: MouseEvent) => {
  emit('click', event, props.config)
}
</script>
