<template>
  <div class="table-render-v6">
    <el-table
      v-loading="loading"
      :data="data"
      :stripe="config.stripe"
      :border="config.border"
      :highlight-current-row="config.highlightCurrentRow"
      :show-header="config.showHeader"
      style="width: 100%"
    >
      <el-table-column
        v-for="col in config.columns"
        :key="col.id"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align"
        :fixed="col.fixed"
        :sortable="col.sortable ? 'custom' : false"
        :show-overflow-tooltip="col.showOverflowTooltip"
      >
        <template #default="{ row }">
          <ColumnRender :column="col" :value="row[col.prop]" />
        </template>
      </el-table-column>

      <!-- 行操作列 -->
      <el-table-column
        v-if="config.rowActions && config.rowActions.length > 0"
        label="操作"
        :width="config.rowActions.length * 70"
        fixed="right"
        align="center"
      >
        <template #default="{ row }">
          <el-button
            v-for="action in config.rowActions"
            :key="action.id"
            :type="action.type"
            size="small"
            text
            @click="handleAction(action, row)"
          >
            {{ action.name }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="config.pagination" class="pagination-wrapper">
      <el-pagination
        :current-page="currentPage"
        :page-size="config.pageSize"
        :page-sizes="config.pageSizes"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ColumnRender from './ColumnRender.vue'
import type { TableConfig } from '@/types/page-v6'

// Props
interface Props {
  config: TableConfig
  data: any[]
  loading?: boolean
}

// Emits
interface Emits {
  (e: 'action', action: any, row: any): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})
const emit = defineEmits<Emits>()

const currentPage = ref(1)
const total = ref(0)

/**
 * 行操作
 */
function handleAction(action: any, row: any) {
  emit('action', action, row)
}

/**
 * 页码变化
 */
function handlePageChange(page: number) {
  currentPage.value = page
}

/**
 * 每页条数变化
 */
function handleSizeChange(size: number) {
  console.log('每页条数:', size)
}
</script>

<style scoped lang="scss">
.table-render-v6 {
  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
