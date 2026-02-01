<template>
  <div class="layout-top-bottom" :style="containerStyle">
    <!-- 顶部工具栏区域（支持多个，按 order 排序） -->
    <template v-for="area in topToolbarAreas" :key="area.id">
      <div
        v-if="$slots[`area-${area.id}`]"
        :class="['layout-toolbar', 'layout-toolbar-top', area.layoutHints?.className]"
        :style="getAreaStyle(area)"
      >
        <slot :name="`area-${area.id}`" :area="area" />
      </div>
      <!-- 兼容旧版 slot -->
      <div
        v-else-if="area.type === 'toolbar' && $slots.toolbar"
        :class="['layout-toolbar', 'layout-toolbar-top', area.layoutHints?.className]"
        :style="getAreaStyle(area)"
      >
        <slot name="toolbar" :area="area" />
      </div>
    </template>

    <!-- 查询区域 -->
    <div
      v-if="searchArea && $slots[`area-${searchArea.id}`]"
      class="layout-search"
      :style="getAreaStyle(searchArea)"
    >
      <slot :name="`area-${searchArea.id}`" :area="searchArea" />
    </div>
    <!-- 兼容旧版 slot -->
    <div
      v-else-if="searchArea && $slots.search"
      class="layout-search"
      :style="getAreaStyle(searchArea)"
    >
      <slot name="search" :area="searchArea" />
    </div>

    <!-- 内容区容器 -->
    <div class="layout-content-wrapper" :style="contentWrapperStyle">
      <!-- 内容区顶部工具栏（支持多个，按 order 排序） -->
      <template v-for="area in contentTopToolbars" :key="area.id">
        <div
          v-if="$slots[`area-${area.id}`]"
          class="layout-toolbar layout-toolbar-content-top"
          :style="getAreaStyle(area)"
        >
          <slot :name="`area-${area.id}`" :area="area" />
        </div>
      </template>

      <!-- 主内容区 -->
      <div
        v-if="mainContentArea && $slots[`area-${mainContentArea.id}`]"
        class="layout-content"
        :style="getAreaStyle(mainContentArea)"
      >
        <slot :name="`area-${mainContentArea.id}`" :area="mainContentArea" />
      </div>
      <!-- 兼容旧版 slot -->
      <div
        v-else-if="mainContentArea && $slots.content"
        class="layout-content"
        :style="getAreaStyle(mainContentArea)"
      >
        <slot name="content" :area="mainContentArea" />
      </div>

      <!-- 底部面板区域（支持多个，按 order 排序） -->
      <template v-for="area in bottomAreas" :key="area.id">
        <div
          v-if="$slots[`area-${area.id}`]"
          :class="['layout-bottom-panel', area.layoutHints?.className]"
          :style="getAreaStyle(area)"
        >
          <slot :name="`area-${area.id}`" :area="area" />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { NormalizedAreaV3 } from '@/api/page'

interface Props {
  layoutConfig?: {
    gap?: string
    padding?: string
    background?: string
  }
  areas?: NormalizedAreaV3[]
}

const props = withDefaults(defineProps<Props>(), {
  layoutConfig: () => ({}),
  areas: () => []
})

// 容器样式
const containerStyle = computed(() => {
  const { gap = '16px', padding = '0', background = '' } = props.layoutConfig
  return {
    gap,
    padding,
    ...(background ? { background } : {})
  }
})

// 内容区容器样式
const contentWrapperStyle = computed(() => ({
  display: 'flex',
  flexDirection: 'column',
  gap: '16px',
  flex: '1',
  minHeight: '0'
}))

// 按位置分组区域
const topToolbarAreas = computed(() =>
  props.areas
    .filter(a => a.enabled !== false && a.type === 'toolbar' && a.position === 'top')
    .sort((a, b) => (a.layoutHints?.order || 0) - (b.layoutHints?.order || 0))
)

const searchArea = computed(() =>
  props.areas.find(a => a.enabled !== false && a.type === 'search' && a.position === 'top')
)

const contentTopToolbars = computed(() =>
  props.areas
    .filter(a => a.enabled !== false && a.type === 'toolbar' && a.position === 'main')
    .sort((a, b) => (a.layoutHints?.order || 0) - (b.layoutHints?.order || 0))
)

const mainContentArea = computed(() =>
  props.areas.find(a => a.enabled !== false && a.type === 'content' && a.position === 'main')
)

const bottomAreas = computed(() =>
  props.areas
    .filter(a => a.enabled !== false && a.position === 'bottom')
    .sort((a, b) => (a.layoutHints?.order || 0) - (b.layoutHints?.order || 0))
)

// 获取区域样式
const getAreaStyle = (area: NormalizedAreaV3) => {
  const hints = area.layoutHints || {}
  const style: Record<string, string> = {}

  if (hints.width) style.width = hints.width
  if (hints.height) style.height = hints.height
  if (hints.flex) style.flex = String(hints.flex)

  // 可滚动
  if (hints.scrollable) {
    style.overflow = 'auto'
  }

  // 自定义样式
  if (hints.style) {
    Object.assign(style, hints.style)
  }

  return style
}
</script>

<style scoped>
.layout-top-bottom {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.layout-search {
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}

.layout-toolbar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 4px;
}

.layout-toolbar-top {
  /* 顶部工具栏默认样式 */
}

.layout-toolbar-content-top {
  /* 内容区顶部工具栏默认样式 */
  margin-bottom: 8px;
}

.layout-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.layout-content {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  flex: 1;
  min-height: 0;
}

.layout-bottom-panel {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  border-top: 1px solid #e4e7ed;
}
</style>
