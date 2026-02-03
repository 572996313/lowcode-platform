/**
 * 自由画布渲染器（运行时）
 * FreeCanvasRender - Runtime renderer for free canvas pages
 */
<template>
  <div class="free-canvas-render" :style="canvasStyle">
    <template v-for="comp in enabledComponents" :key="comp.id">
      <!-- 树组件 -->
      <TreeRender
        v-if="comp.type === 'tree'"
        :config="comp.config as TreeComponentConfig"
        :style="compStyle(comp)"
      />

      <!-- 查询表单组件 -->
      <SearchFormRender
        v-else-if="comp.type === 'search-form'"
        :config="comp.config as SearchFormComponentConfig"
        :style="compStyle(comp)"
        @search="handleSearch"
        @reset="handleReset"
      />

      <!-- 表格组件 -->
      <TableRender
        v-else-if="comp.type === 'table'"
        :config="comp.config as TableComponentConfig"
        :style="compStyle(comp)"
        @row-click="handleRowClick"
      />

      <!-- 按钮组组件 -->
      <ButtonGroupRender
        v-else-if="comp.type === 'button-group'"
        :config="comp.config as ButtonGroupComponentConfig"
        :style="compStyle(comp)"
        @action="handleButtonAction"
      />

      <!-- 表单组件 -->
      <FormRender
        v-else-if="comp.type === 'form'"
        :config="comp.config as FormComponentConfig"
        :style="compStyle(comp)"
        @submit="handleFormSubmit"
      />

      <!-- 图表组件 -->
      <ChartRender
        v-else-if="comp.type === 'chart'"
        :config="comp.config as ChartComponentConfig"
        :style="compStyle(comp)"
      />

      <!-- 标签页组件 -->
      <TabsRender
        v-else-if="comp.type === 'tabs'"
        :config="comp.config as TabsComponentConfig"
        :style="compStyle(comp)"
      />

      <!-- 卡片组件 -->
      <CardRender
        v-else-if="comp.type === 'card'"
        :config="comp.config as CardComponentConfig"
        :style="compStyle(comp)"
      />

      <!-- 分割线组件 -->
      <DividerRender
        v-else-if="comp.type === 'divider'"
        :config="comp.config as DividerComponentConfig"
        :style="compStyle(comp)"
      />

      <!-- 占位符组件 -->
      <SpacerRender
        v-else-if="comp.type === 'spacer'"
        :config="comp.config as SpacerComponentConfig"
        :style="compStyle(comp)"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import TreeRender from './TreeRender.vue'
import SearchFormRender from './SearchFormRender.vue'
import TableRender from './TableRender.vue'
import ButtonGroupRender from './ButtonGroupRender.vue'
import FormRender from './FormRender.vue'
import ChartRender from './ChartRender.vue'
import TabsRender from './TabsRender.vue'
import CardRender from './CardRender.vue'
import DividerRender from './DividerRender.vue'
import SpacerRender from './SpacerRender.vue'
import type {
  FreeCanvasPageConfig,
  TreeComponentConfig,
  SearchFormComponentConfig,
  TableComponentConfig,
  ButtonGroupComponentConfig,
  FormComponentConfig,
  ChartComponentConfig,
  TabsComponentConfig,
  CardComponentConfig,
  DividerComponentConfig,
  SpacerComponentConfig
} from '@/types/page-free-canvas'

interface Props {
  config: FreeCanvasPageConfig
}

interface Emits {
  (e: 'ready'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 搜索参数
const searchParams = ref<Record<string, any>>({})

// 组件样式
function compStyle(comp: any): Record<string, any> {
  const { position, style } = comp
  const customStyle = style?.customStyles || {}

  return {
    position: 'absolute',
    left: `${position.x}px`,
    top: `${position.y}px`,
    width: `${position.width}px`,
    height: `${position.height}px`,
    zIndex: position.zIndex || 1,
    border: style?.border || 'none',
    borderRadius: style?.borderRadius || '0',
    backgroundColor: style?.backgroundColor || 'transparent',
    padding: style?.padding || '0',
    boxShadow: style?.boxShadow || 'none',
    ...customStyle
  }
}

// 启用的组件列表
const enabledComponents = computed(() => {
  return props.config.components.filter(c => c.enabled !== false)
})

// 画布样式
const canvasStyle = computed(() => {
  const canvas = props.config.canvas
  return {
    width: `${canvas.width}px`,
    minHeight: canvas.height ? `${canvas.height}px` : '600px',
    backgroundColor: canvas.backgroundColor || '#f5f7fa',
    backgroundImage: canvas.backgroundImage
      ? `url(${canvas.backgroundImage})`
      : undefined,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    position: 'relative',
    margin: '0 auto'
  }
})

/**
 * 处理搜索
 */
function handleSearch(params: Record<string, any>) {
  searchParams.value = params
}

/**
 * 处理重置
 */
function handleReset() {
  searchParams.value = {}
}

/**
 * 处理行点击
 */
function handleRowClick(row: any) {
  console.log('Row clicked:', row)
}

/**
 * 处理按钮动作
 */
function handleButtonAction(action: any) {
  console.log('Button action:', action)
}

/**
 * 处理表单提交
 */
function handleFormSubmit(data: any) {
  console.log('Form submit:', data)
}

// 组件准备好时发出事件
emit('ready')
</script>

<style scoped lang="scss">
.free-canvas-render {
  // 样式通过内联方式动态设置
}
</style>
