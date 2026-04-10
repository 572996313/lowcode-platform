<template>
  <!-- 表格单元格渲染组件 -->
  <!-- 图片 -->
  <el-image
    v-if="column.formatter === 'image'"
    :src="getCellValue(row)"
    :preview-src-list="[getCellValue(row)]"
    fit="cover"
    style="width: 60px; height: 60px; border-radius: 4px"
  />

  <!-- 标签 -->
  <el-tag v-else-if="column.formatter === 'tag'" :type="getTagType(row)">
    {{ getTagLabel(row) }}
  </el-tag>

  <!-- 链接 -->
  <el-link v-else-if="column.formatter === 'link'" type="primary" :href="getCellValue(row)" target="_blank">
    {{ getCellValue(row) }}
  </el-link>

  <!-- 进度条 -->
  <el-progress
    v-else-if="column.formatter === 'progress'"
    :percentage="Number(getCellValue(row)) || 0"
    :stroke-width="column.formatterConfig?.strokeWidth || 8"
  />

  <!-- 开关 -->
  <el-switch
    v-else-if="column.formatter === 'switch'"
    :model-value="Boolean(getCellValue(row))"
    :disabled="true"
  />

  <!-- 日期格式化 -->
  <span v-else-if="column.formatter === 'date'">
    {{ formatDate(getCellValue(row), column.formatterConfig?.format || 'YYYY-MM-DD') }}
  </span>

  <!-- 日期时间格式化 -->
  <span v-else-if="column.formatter === 'datetime'">
    {{ formatDate(getCellValue(row), column.formatterConfig?.format || 'YYYY-MM-DD HH:mm:ss') }}
  </span>

  <!-- 默认文本 -->
  <span v-else>{{ getCellValue(row) }}</span>
</template>

<script setup lang="ts">
import type { ColumnDisplayConfig } from '@/types/dataset'

interface TableCellProps {
  row: any
  column: ColumnDisplayConfig
}

const props = defineProps<TableCellProps>()

// 获取单元格值
function getCellValue(row: any) {
  return row[props.column.fieldCode]
}

// 获取标签类型
function getTagType(row: any) {
  const value = getCellValue(row)
  const typeMap = props.column.formatterConfig?.typeMap || {}
  return typeMap[value] || 'info'
}

// 获取标签文本
function getTagLabel(row: any) {
  const value = getCellValue(row)
  const labelMap = props.column.formatterConfig?.labelMap || {}
  return labelMap[value] || value
}

// 格式化日期
function formatDate(date: any, format: string) {
  if (!date) return ''
  try {
    const d = new Date(date)
    if (isNaN(d.getTime())) return date
    return d.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return date
  }
}
</script>
