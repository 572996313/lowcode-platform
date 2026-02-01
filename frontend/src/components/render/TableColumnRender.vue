<template>
  <!-- 兼容旧格式：使用 formatterType 的列 -->
  <template v-if="isLegacyFormat">
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

        <!-- 标签展示 -->
        <template v-else-if="column.formatterType === 'tag'">
          <el-tag
            :type="getTagType(row[column.columnCode], column)"
            :effect="column.props?.effect || 'light'"
          >
            {{ formatTagLabel(row[column.columnCode], column) }}
          </el-tag>
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
      </template>
    </el-table-column>
  </template>

  <!-- V4 格式：使用 type 的列 -->
  <template v-else>
    <!-- selection 类型 - 多选列 -->
    <el-table-column
      v-if="v4Column.type === 'selection'"
      type="selection"
      :width="v4Column.width || 55"
      :fixed="v4Column.fixed"
    />

    <!-- index 类型 - 序号列 -->
    <el-table-column
      v-else-if="v4Column.type === 'index'"
      type="index"
      :label="v4Column.label"
      :width="v4Column.width || 60"
      :fixed="v4Column.fixed"
      :align="v4Column.align || 'center'"
    />

    <!-- actions 类型 - 操作列 -->
    <el-table-column
      v-else-if="v4Column.type === 'actions'"
      :label="v4Column.label"
      :width="v4Column.width || 200"
      :fixed="v4Column.fixed"
      :align="v4Column.align || 'left'"
    >
      <template #default="{ row }">
        <template v-for="action in v4Column.actions" :key="action.id">
          <el-button
            v-if="action.link"
            :type="action.type || 'primary'"
            :icon="action.icon"
            link
            @click="handleActionClick(action, row)"
          >
            {{ action.label }}
          </el-button>
          <el-button
            v-else
            :type="action.type || 'default'"
            :icon="action.icon"
            size="small"
            @click="handleActionClick(action, row)"
          >
            {{ action.label }}
          </el-button>
        </template>
      </template>
    </el-table-column>

    <!-- 其他类型 - 普通列 -->
    <el-table-column
      v-else
      :prop="v4Column.prop"
      :label="v4Column.label"
      :width="v4Column.width"
      :min-width="v4Column.minWidth"
      :sortable="v4Column.sortable ? 'custom' : false"
      :fixed="v4Column.fixed"
      :align="v4Column.align || 'left'"
      :show-overflow-tooltip="v4Column.showOverflowTooltip"
    >
      <template #default="{ row }">
        <!-- image 类型 - 图片 -->
        <template v-if="v4Column.type === 'image'">
          <el-image
            v-if="row[v4Column.prop] && v4Column.imageConfig"
            :src="row[v4Column.prop]"
            :preview-src-list="[row[v4Column.prop]]"
            :style="{
              width: `${v4Column.imageConfig.width}px`,
              height: `${v4Column.imageConfig.height}px`,
              borderRadius: v4Column.imageConfig.radius
            }"
            :fit="v4Column.imageConfig.fit"
            :preview-teleported="v4Column.imageConfig.preview !== false"
          />
          <span v-else class="text-gray-400">无</span>
        </template>

        <!-- tag 类型 - 标签 -->
        <template v-else-if="v4Column.type === 'tag' && v4Column.tagConfig">
          <el-tag
            :type="getV4TagType(row[v4Column.prop])"
            effect="light"
          >
            {{ getV4TagText(row[v4Column.prop]) }}
          </el-tag>
        </template>

        <!-- switch 类型 - 开关 -->
        <template v-else-if="v4Column.type === 'switch'">
          <el-switch
            :model-value="getV4SwitchValue(row[v4Column.prop])"
            :disabled="v4Column.switchConfig?.disabled"
          />
        </template>

        <!-- datetime 类型 - 日期时间 -->
        <template v-else-if="v4Column.type === 'datetime'">
          {{ formatDateV4(row[v4Column.prop], v4Column.dateConfig?.format || 'YYYY-MM-DD HH:mm:ss') }}
        </template>

        <!-- date 类型 - 日期 -->
        <template v-else-if="v4Column.type === 'date'">
          {{ formatDateV4(row[v4Column.prop], v4Column.dateConfig?.format || 'YYYY-MM-DD') }}
        </template>

        <!-- link 类型 - 链接 -->
        <template v-else-if="v4Column.type === 'link'">
          <el-link
            type="primary"
            :href="getV4LinkHref(row[v4Column.prop])"
            :target="v4Column.linkConfig?.target || '_blank'"
            :disabled="!row[v4Column.prop]"
          >
            {{ row[v4Column.prop] || '-' }}
          </el-link>
        </template>

        <!-- progress 类型 - 进度条 -->
        <template v-else-if="v4Column.type === 'progress'">
          <el-progress
            :percentage="Number(row[v4Column.prop]) || 0"
            :stroke-width="v4Column.progressConfig?.strokeWidth || 6"
            :status="v4Column.progressConfig?.status"
          />
        </template>

        <!-- text 类型或默认 - 普通文本 -->
        <template v-else>
          {{ row[v4Column.prop] }}
        </template>
      </template>
    </el-table-column>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TableColumnV4, ActionButton } from '@/types/table'
import type { TableColumn } from '@/api/table'

interface Props {
  column: TableColumn | TableColumnV4
}

interface Emits {
  (e: 'action-click', action: ActionButton | any, row: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 判断是否为旧格式
const isLegacyFormat = computed(() => {
  return 'columnCode' in props.column
})

// 获取 V4 格式的列配置
const v4Column = computed(() => {
  return props.column as TableColumnV4
})

// ==================== 旧格式处理函数 ====================

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

// 日期格式化（旧格式）
const formatDate = (value: any, column: TableColumn): string => {
  if (!value) return '-'

  const config = parseFormatterConfig(column.formatterConfig)
  const format = column.formatterType === 'datetime'
    ? (config.format || 'YYYY-MM-DD HH:mm:ss')
    : (config.format || 'YYYY-MM-DD')

  return formatDateV4(value, format)
}

// 获取标签类型（旧格式）
const getTagType = (value: any, column: TableColumn): any => {
  const config = parseFormatterConfig(column.formatterConfig)
  const typeMap = config.typeMap || {}

  return typeMap[value] || config.defaultType || 'default'
}

// 格式化标签文本（旧格式）
const formatTagLabel = (value: any, column: TableColumn): string => {
  const config = parseFormatterConfig(column.formatterConfig)
  const labelMap = config.labelMap || {}

  return labelMap[value] || value || '-'
}

// 获取进度条状态（旧格式）
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

// ==================== V4 格式处理函数 ====================

// 获取标签类型（V4）
const getV4TagType = (value: any): any => {
  if (!v4Column.value.tagConfig) return 'info'
  const config = v4Column.value.tagConfig[value]
  return config?.type || 'info'
}

// 获取标签文本（V4）
const getV4TagText = (value: any): string => {
  if (!v4Column.value.tagConfig) return value || '-'
  const config = v4Column.value.tagConfig[value]
  return config?.text || value || '-'
}

// 获取开关值（V4）
const getV4SwitchValue = (value: any): boolean => {
  if (!v4Column.value.switchConfig) return !!value
  const { activeValue = true, inactiveValue = false } = v4Column.value.switchConfig
  return value === activeValue
}

// 格式化日期（V4）
const formatDateV4 = (value: any, format: string): string => {
  if (!value) return '-'

  try {
    const date = new Date(value)
    if (isNaN(date.getTime())) return value

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    if (format.includes('HH:mm:ss')) {
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    } else {
      return `${year}-${month}-${day}`
    }
  } catch {
    return value
  }
}

// 获取链接地址（V4）
const getV4LinkHref = (value: any): string => {
  if (!v4Column.value.linkConfig?.href) return value
  if (typeof v4Column.value.linkConfig.href === 'function') {
    return v4Column.value.linkConfig.href({ [v4Column.value.prop]: value })
  }
  return v4Column.value.linkConfig.href
}

// 处理操作按钮点击
const handleActionClick = (action: ActionButton | any, row: any) => {
  if (action.confirm) {
    ElMessageBox.confirm(
      action.confirmMessage || '确定要执行此操作吗？',
      action.confirmTitle || '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      emit('action-click', action, row)
    }).catch(() => {
      // 用户取消
    })
  } else {
    emit('action-click', action, row)
  }
}
</script>

<style scoped>
.text-gray-400 {
  color: #9ca3af;
}
</style>
