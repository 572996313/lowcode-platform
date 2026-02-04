<template>
  <div
    v-if="componentVisible"
    class="table-render-free-canvas"
    :style="componentStyle"
  >
    <el-table
      :data="tableData"
      :border="config.border"
      :stripe="config.stripe"
      :show-index="config.showIndex"
      :height="tableHeight"
      v-loading="loading"
    >
      <el-table-column
        v-if="config.showIndex"
        type="index"
        label="序号"
        width="60"
        align="center"
      />

      <el-table-column
        v-for="col in config.columns"
        :key="col.id"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align || 'left'"
        :fixed="col.fixed"
        :sortable="col.sortable ? 'custom' : false"
      >
        <template #default="{ row }">
          <span>{{ getCellValue(row, col) }}</span>
        </template>
      </el-table-column>

      <!-- 行操作 -->
      <el-table-column
        v-if="config.rowActions && config.rowActions.length > 0"
        label="操作"
        :width="config.rowActions.length * 80"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            v-for="action in config.rowActions"
            :key="action.id"
            :type="action.type"
            :icon="action.icon"
            size="small"
            link
            @click="handleAction(action, row)"
          >
            {{ action.name }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="config.pagination"
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { ComponentPosition, ComponentStyle, TableComponentConfig, TableColumnConfig } from '@/types/page-free-canvas'

interface Props {
  config: TableComponentConfig
  position: ComponentPosition
  style?: ComponentStyle
}

const props = defineProps<Props>()

// 表格数据
const tableData = ref<any[]>([])
const loading = ref(false)
const pagination = ref({
  current: 1,
  size: props.config.pageSize || 10,
  total: 0
})

// 组件可见性
const componentVisible = computed(() => true)

/**
 * 计算组件样式
 */
const componentStyle = computed(() => {
  const baseStyle: Record<string, any> = {
    left: props.position.x + 'px',
    top: props.position.y + 'px',
    width: props.position.width + 'px',
    height: props.position.height + 'px',
    zIndex: props.position.zIndex || 1
  }

  if (props.style) {
    Object.assign(baseStyle, props.style.customStyles)
    if (props.style.backgroundColor) baseStyle.backgroundColor = props.style.backgroundColor
    if (props.style.borderRadius) baseStyle.borderRadius = props.style.borderRadius
    if (props.style.border) baseStyle.border = props.style.border
    if (props.style.padding) baseStyle.padding = props.style.padding
    if (props.style.margin) baseStyle.margin = props.style.margin
    if (props.style.boxShadow) baseStyle.boxShadow = props.style.boxShadow
    if (props.style.opacity !== undefined) baseStyle.opacity = props.style.opacity
  }

  return baseStyle
})

/**
 * 表格高度（需要减去分页器高度）
 */
const tableHeight = computed(() => {
  const paginationHeight = props.config.pagination ? 52 : 0
  return props.position.height - paginationHeight
})

/**
 * 获取单元格值
 */
function getCellValue(row: any, col: TableColumnConfig) {
  const value = row[col.prop]

  // 格式化处理
  if (col.formatter && col.formatter.startsWith('${') && col.formatter.endsWith('}')) {
    // 简单的模板替换
    return col.formatter.replace(/\$\{(.+?)\}/g, (_, key) => row[key.trim()] || '')
  }

  // 字典映射
  if (col.dictCode) {
    // TODO: 实现字典映射
    return value
  }

  // 标签类型
  if (col.tagConfig && value) {
    const tagInfo = col.tagConfig[value]
    return tagInfo ? { text: tagInfo.text, type: tagInfo.type } : value
  }

  return value
}

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.config.dataSource) {
    tableData.value = []
    return
  }

  loading.value = true

  try {
    const { type, api, static: staticData } = props.config.dataSource

    if (type === 'static' && staticData) {
      // 静态数据
      tableData.value = staticData
      pagination.value.total = staticData.length
    } else if (type === 'api' && api) {
      // API 数据
      const params = {
        current: pagination.value.current,
        size: pagination.value.size,
        ...api.params
      }

      const res = await fetch(api.url + '?' + new URLSearchParams(params), {
        method: api.method,
        headers: api.headers || {}
      })
      const data = await res.json()

      if (data.code === 200) {
        tableData.value = data.data.records || data.data || []
        pagination.value.total = data.data.total || 0
      }
    }
  } catch (error) {
    console.error('加载表格数据失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 处理行操作
 */
function handleAction(action: any, row: any) {
  console.log('行操作:', action, row)

  if (action.action.type === 'delete') {
    // TODO: 实现删除功能
  } else if (action.action.type === 'edit') {
    // TODO: 实现编辑功能
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.table-render-free-canvas {
  position: absolute;
  overflow: auto;
  background: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;

  .el-table {
    flex: 1;
  }

  .el-pagination {
    margin-top: 16px;
    padding: 0 16px 16px;
  }
}
</style>
