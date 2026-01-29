<template>
  <el-table-column
    :prop="column.columnCode"
    :label="column.label"
    :width="column.width"
    :min-width="column.minWidth"
    :sortable="column.sortable ? 'custom' : false"
    :fixed="column.fixed"
    :align="column.align || 'left'"
    :header-align="column.headerAlign"
    :resizable="column.resizable"
    :show-overflow-tooltip="column.showOverflowTooltip"
  >
    <template #default="{ row }">
      <!-- 普通文本 -->
      <template v-if="!column.formatterType">
        {{ row[column.columnCode] }}
      </template>

      <!-- 字典翻译 -->
      <template v-else-if="column.formatterType === 'dict'">
        {{ formatDict(row[column.columnCode], column) }}
      </template>

      <!-- 日期格式化 -->
      <template v-else-if="column.formatterType === 'date'">
        {{ formatDate(row[column.columnCode], column) }}
      </template>

      <!-- 日期时间格式化 -->
      <template v-else-if="column.formatterType === 'datetime'">
        {{ formatDate(row[column.columnCode], column) }}
      </template>

      <!-- 数字格式化 -->
      <template v-else-if="column.formatterType === 'number'">
        {{ formatNumber(row[column.columnCode], column) }}
      </template>

      <!-- 金额格式化 -->
      <template v-else-if="column.formatterType === 'currency'">
        {{ formatCurrency(row[column.columnCode], column) }}
      </template>

      <!-- 百分比格式化 -->
      <template v-else-if="column.formatterType === 'percent'">
        {{ formatPercent(row[column.columnCode], column) }}
      </template>

      <!-- 标签展示 -->
      <template v-else-if="column.formatterType === 'tag'">
        <el-tag
          :type="getTagType(row[column.columnCode], column)"
          :effect="column.props?.effect || 'light'"
        >
          {{ formatTagLabel(row[column.columnCode], column) }}
        </el-tag>
      </template>

      <!-- 开关展示 -->
      <template v-else-if="column.formatterType === 'switch'">
        <el-switch
          :model-value="!!row[column.columnCode]"
          :disabled="true"
        />
      </template>

      <!-- 进度条 -->
      <template v-else-if="column.formatterType === 'progress'">
        <el-progress
          :percentage="Number(row[column.columnCode]) || 0"
          :stroke-width="column.props?.strokeWidth || 6"
          :status="getProgressStatus(row[column.columnCode], column)"
        />
      </template>

      <!-- 图片展示 -->
      <template v-else-if="column.formatterType === 'image'">
        <el-image
          v-if="row[column.columnCode]"
          :src="row[column.columnCode]"
          :preview-src-list="[row[column.columnCode]]"
          :style="{
            width: column.props?.width || '40px',
            height: column.props?.height || '40px',
            borderRadius: '4px'
          }"
          fit="cover"
          preview-teleported
        />
        <span v-else class="text-gray-400">无</span>
      </template>

      <!-- 链接展示 -->
      <template v-else-if="column.formatterType === 'link'">
        <el-link
          type="primary"
          :href="row[column.columnCode]"
          target="_blank"
          :disabled="!row[column.columnCode]"
        >
          {{ row[column.columnCode] || '-' }}
        </el-link>
      </template>

      <!-- 操作链接 -->
      <template v-else-if="column.formatterType === 'action'">
        <el-link
          type="primary"
          @click="handleActionClick(row, column)"
        >
          {{ column.props?.label || '查看' }}
        </el-link>
      </template>

      <!-- 自定义渲染函数 -->
      <template v-else-if="column.formatterType === 'custom'">
        <component
          :is="column.props?.customComponent"
          v-if="column.props?.customComponent"
          :row="row"
          :column="column"
          :value="row[column.columnCode]"
        />
        <span v-else>{{ formatCustom(row, column) }}</span>
      </template>
    </template>
  </el-table-column>
</template>

<script setup lang="ts">
import type { TableColumn } from '@/api/table'

interface Props {
  column: TableColumn
}

interface Emits {
  (e: 'action-click', row: any, column: TableColumn): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 解析配置 JSON
const parseFormatterConfig = (configJson?: string): Record<string, any> => {
  if (!configJson) return {}
  try {
    return JSON.parse(configJson)
  } catch {
    return {}
  }
}

// 字典格式化
const formatDict = (value: any, column: TableColumn): string => {
  const config = parseFormatterConfig(column.formatterConfig)
  if (!config.dict) return value || '-'

  const dict = config.dict as Record<string, string>
  return dict[value] || value || '-'
}

// 日期格式化
const formatDate = (value: any, column: TableColumn): string => {
  if (!value) return '-'

  const config = parseFormatterConfig(column.formatterConfig)
  const format = column.formatterType === 'datetime'
    ? (config.format || 'YYYY-MM-DD HH:mm:ss')
    : (config.format || 'YYYY-MM-DD')

  try {
    const date = new Date(value)
    if (isNaN(date.getTime())) return value

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    if (column.formatterType === 'datetime') {
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    } else {
      return `${year}-${month}-${day}`
    }
  } catch {
    return value
  }
}

// 数字格式化
const formatNumber = (value: any, column: TableColumn): string => {
  if (value === null || value === undefined || value === '') return '-'

  const config = parseFormatterConfig(column.formatterConfig)
  const decimals = config.decimals !== undefined ? config.decimals : 2

  try {
    const num = Number(value)
    if (isNaN(num)) return value

    return num.toFixed(decimals)
  } catch {
    return value
  }
}

// 金额格式化
const formatCurrency = (value: any, column: TableColumn): string => {
  if (value === null || value === undefined || value === '') return '-'

  const config = parseFormatterConfig(column.formatterConfig)
  const symbol = config.symbol || '¥'
  const decimals = config.decimals !== undefined ? config.decimals : 2

  try {
    const num = Number(value)
    if (isNaN(num)) return value

    return `${symbol} ${num.toFixed(decimals)}`
  } catch {
    return value
  }
}

// 百分比格式化
const formatPercent = (value: any, column: TableColumn): string => {
  if (value === null || value === undefined || value === '') return '-'

  const config = parseFormatterConfig(column.formatterConfig)
  const decimals = config.decimals !== undefined ? config.decimals : 2
  const multiply = config.multiply !== false

  try {
    let num = Number(value)
    if (isNaN(num)) return value

    if (multiply) {
      num = num * 100
    }

    return `${num.toFixed(decimals)}%`
  } catch {
    return value
  }
}

// 获取标签类型
const getTagType = (value: any, column: TableColumn): any => {
  const config = parseFormatterConfig(column.formatterConfig)
  const typeMap = config.typeMap || {}

  return typeMap[value] || config.defaultType || 'default'
}

// 格式化标签文本
const formatTagLabel = (value: any, column: TableColumn): string => {
  const config = parseFormatterConfig(column.formatterConfig)
  const labelMap = config.labelMap || {}

  return labelMap[value] || value || '-'
}

// 获取进度条状态
const getProgressStatus = (value: any, column: TableColumn): any => {
  const config = parseFormatterConfig(column.formatterConfig)
  const num = Number(value) || 0

  if (config.successThreshold && num >= config.successThreshold) {
    return 'success'
  }
  if (config.warningThreshold && num >= config.warningThreshold) {
    return 'warning'
  }
  if (config.exceptionThreshold && num >= config.exceptionThreshold) {
    return 'exception'
  }

  return ''
}

// 自定义格式化
const formatCustom = (row: any, column: TableColumn): string => {
  const config = parseFormatterConfig(column.formatterConfig)
  const formatter = config.formatter

  if (typeof formatter === 'function') {
    try {
      return formatter(row[column.columnCode], row, column)
    } catch (e) {
      console.error('自定义格式化失败:', e)
      return row[column.columnCode]
    }
  }

  return row[column.columnCode] || '-'
}

// 操作链接点击
const handleActionClick = (row: any, column: TableColumn) => {
  emit('action-click', row, column)
}
</script>
