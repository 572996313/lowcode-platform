<template>
  <component :is="renderComponent" />
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import type { NormalizedAreaV3 } from '@/api/page'
import SearchAreaRender from './SearchAreaRender.vue'
import TableRender from './TableRender.vue'
import TableRenderFromTemplate from './TableRenderFromTemplate.vue'
import FormRender from './FormRender.vue'
import ToolbarRender from './ToolbarRender.vue'

interface Props {
  area: NormalizedAreaV3
}

const props = defineProps<Props>()

const renderComponent = computed(() => {
  const { area } = props
  const config = area.config || {}

  switch (area.type) {
    case 'search':
      return () => h(SearchAreaRender, {
        title: config.title || '查询条件',
        fields: config.fields || [],
        collapsible: config.collapsible
      })

    case 'content':
      if (config.componentType === 'table') {
        // 支持直接从模板配置渲染表格（V4 格式）
        if (config.columns && Array.isArray(config.columns)) {
          return () => h(TableRenderFromTemplate, {
            columns: config.columns,
            tableConfig: config.tableConfig,
            title: config.title
          })
        }
        // 从后端加载表格配置
        return () => h(TableRender, {
          configId: config.configId,
          title: config.title
        })
      } else if (config.componentType === 'form') {
        return () => h(FormRender, {
          configId: config.configId,
          title: config.title
        })
      }
      // 默认占位符
      return () => h('div', { class: 'area-placeholder' }, `内容区: ${area.name}`)

    case 'toolbar':
      return () => h(ToolbarRender, {
        buttons: config.buttons || [],
        align: config.align || 'left'
      })

    case 'tree':
      // TODO: 实现 TreeRender 组件
      return () => h('div', { class: 'area-placeholder' }, `树形区: ${area.name}`)

    case 'tabs':
      // TODO: 实现 TabsRender 组件
      return () => h('div', { class: 'area-placeholder' }, `标签页: ${area.name}`)

    case 'stats':
      // TODO: 实现 StatsRender 组件
      return () => h('div', { class: 'area-placeholder' }, `统计卡片: ${area.name}`)

    case 'charts':
      // TODO: 实现 ChartsRender 组件
      return () => h('div', { class: 'area-placeholder' }, `图表: ${area.name}`)

    default:
      return () => h('div', { class: 'area-placeholder' }, `未知区域类型: ${area.type}`)
  }
})
</script>

<style scoped>
.area-placeholder {
  padding: 32px;
  text-align: center;
  color: #909399;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
}
</style>
