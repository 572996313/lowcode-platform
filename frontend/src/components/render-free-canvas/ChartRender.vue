/**
 * 图表渲染器（运行时）
 */
<template>
  <div class="chart-render-wrapper" :style="wrapperStyle">
    <div class="chart-placeholder">
      <div class="chart-icon">{{ chartIcon }}</div>
      <div class="chart-info">
        <div class="chart-type">{{ chartTypeLabel }}</div>
        <div class="chart-hint">图表组件</div>
      </div>
    </div>
    <!-- TODO: 集成 ECharts 进行真实渲染 -->
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ChartComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: ChartComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => props.style || {})

const chartIcon = computed(() => {
  const iconMap: Record<string, string> = {
    'line': '📈',
    'bar': '📊',
    'pie': '🥧',
    'gauge': '⚡',
    'scatter': '🔵'
  }
  return iconMap[props.config.chartType] || '📈'
})

const chartTypeLabel = computed(() => {
  const labelMap: Record<string, string> = {
    'line': '折线图',
    'bar': '柱状图',
    'pie': '饼图',
    'gauge': '仪表盘',
    'scatter': '散点图'
  }
  return labelMap[props.config.chartType] || '图表'
})
</script>

<style scoped lang="scss">
.chart-render-wrapper {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;

  .chart-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    color: #909399;

    .chart-icon {
      font-size: 48px;
    }

    .chart-info {
      text-align: center;

      .chart-type {
        font-size: 14px;
        font-weight: 500;
      }

      .chart-hint {
        font-size: 12px;
        margin-top: 4px;
      }
    }
  }
}
</style>
