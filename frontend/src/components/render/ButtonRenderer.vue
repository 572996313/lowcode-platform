<template>
  <el-button
    v-if="visible"
    :type="buttonConfig.buttonType || 'default'"
    :size="buttonConfig.buttonSize || 'default'"
    :icon="buttonConfig.icon"
    :plain="buttonConfig.plain"
    :round="buttonConfig.round"
    :circle="buttonConfig.circle"
    :loading="buttonConfig.loading || loading"
    :disabled="buttonConfig.disabled || disabled"
    :link="link"
    @click="handleClick"
  >
    {{ buttonConfig.buttonName }}
  </el-button>
</template>

<script setup lang="ts">
import { ref, computed, inject } from 'vue'
import type { ButtonConfig } from '@/api/button'

interface Props {
  config: ButtonConfig | { buttonId?: number }  // 支持完整配置或buttonId引用
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

// 注入按钮缓存
const buttonMap = inject<Map<number, ButtonConfig>>('buttonMap', new Map())

// 计算实际的按钮配置（从缓存中获取完整配置）
const buttonConfig = computed(() => {
  // 如果config中只有buttonId，从缓存中获取完整配置
  if ('buttonId' in props.config && props.config.buttonId) {
    const cached = buttonMap.value.get(props.config.buttonId)
    if (cached) {
      return { ...cached, ...props.config } // 合并缓存配置和本地配置
    }
    // 如果缓存中没有，返回基础配置
    return {
      buttonName: '按钮',
      buttonType: 'default',
      ...props.config
    } as ButtonConfig
  }
  // 直接返回完整配置
  return props.config as ButtonConfig
})

// 计算按钮是否可见
const visible = computed(() => {
  if (!buttonConfig.value.visible) return true

  // 如果有显示条件，计算条件是否满足
  if (buttonConfig.value.showCondition) {
    try {
      const context = {
        row: props.context?.row,
        form: props.context?.form,
        selection: props.context?.selection
      }
      const func = new Function('row', 'form', 'selection', `return ${buttonConfig.value.showCondition}`)
      return func(context.row, context.form, context.selection)
    } catch (e) {
      console.error('计算按钮显示条件失败:', e)
      return true
    }
  }

  return true
})

const handleClick = (event: MouseEvent) => {
  emit('click', event, buttonConfig.value)
}
</script>
