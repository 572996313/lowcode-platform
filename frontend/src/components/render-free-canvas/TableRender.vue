/**
 * 表格渲染器（运行时）
 */
<template>
  <div class="table-render-wrapper" :style="wrapperStyle">
    <el-table
      :data="displayData"
      :stripe="config.stripe"
      :border="config.border"
      :show-header="true"
      :height="tableHeight"
      class="table-render"
      @row-click="handleRowClick"
    >
      <el-table-column v-if="config.showIndex" type="index" label="序号" width="60" align="center" />
      <el-table-column
        v-if="config.selectionMode && config.selectionMode !== 'none'"
        type="selection"
        width="55"
      />
      <el-table-column
        v-for="col in config.columns"
        :key="col.id"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align"
        :fixed="col.fixed"
        :sortable="col.sortable"
      />
    </el-table>
    <div v-if="config.pagination" class="table-pagination">
      <el-pagination
        small
        layout="prev, pager, next, sizes, total"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="config.pageSize || 10"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { TableComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: TableComponentConfig
  style?: Record<string, any>
  searchParams?: Record<string, any>
}

interface Emits {
  (e: 'row-click', row: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const wrapperStyle = computed(() => props.style || {})
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(props.config.pageSize || 10)

const tableHeight = computed(() => {
  const hasPagination = props.config.pagination
  return '100%'
})

const displayData = computed(() => {
  if (props.config.dataSource?.type === 'static') {
    return props.config.dataSource.static || []
  }
  return []
})

function handleRowClick(row: any) {
  emit('row-click', row)
}

function handleSizeChange(size: number) {
  pageSize.value = size
}

function handleCurrentChange(page: number) {
  currentPage.value = page
}
</script>

<style scoped lang="scss">
.table-render-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;

  .table-render {
    flex: 1;
  }

  .table-pagination {
    padding: 8px 16px;
    border-top: 1px solid #ebeef5;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
