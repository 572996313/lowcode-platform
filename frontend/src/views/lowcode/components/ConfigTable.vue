<template>
  <el-table
    :data="data"
    :border="config.border"
    :stripe="config.stripe"
    :size="config.size"
    v-loading="loading"
    @selection-change="(val: any[]) => emit('selection-change', val)"
  >
    <!-- 序号列 -->
    <el-table-column v-if="config.showIndex" type="index" label="序号" width="60" align="center" />
    <!-- 多选列 -->
    <el-table-column v-if="config.showSelection" type="selection" width="55" align="center" />

    <template v-for="col in columns" :key="col.prop || col.label">
      <!-- 操作列 -->
      <el-table-column
        v-if="col.type === 'action'"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align || 'center'"
        :fixed="col.fixed"
      >
        <template #default="{ row }">
          <el-button
            v-for="btn in col.actionConfig?.buttons"
            :key="btn.action"
            :type="btn.btnType as any"
            :size="btn.size || 'small'"
            @click="emit('action', btn.action, row)"
          >
            {{ btn.label }}
          </el-button>
        </template>
      </el-table-column>

      <!-- Tag 列 -->
      <el-table-column
        v-else-if="col.type === 'tag'"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align"
        :fixed="col.fixed"
      >
        <template #default="{ row }">
          <el-tag
            v-if="col.tagConfig?.mapping"
            :type="col.tagConfig.mapping[String(row[col.prop!])]?.type as any"
            size="small"
          >
            {{ col.tagConfig.mapping[String(row[col.prop!])]?.text || row[col.prop!] }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 普通文本列 -->
      <el-table-column
        v-else
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :align="col.align"
        :fixed="col.fixed"
        :show-overflow-tooltip="col.showOverflowTooltip"
      />
    </template>
  </el-table>

  <!-- 分页 -->
  <el-pagination
    v-if="config.showPagination !== false"
    v-model:current-page="currentPage"
    v-model:page-size="currentSize"
    :total="total"
    :page-sizes="config.pageSizes || [10, 20, 50, 100]"
    layout="total, sizes, prev, pager, next, jumper"
    @size-change="(val: number) => emit('page-change', currentPage, val)"
    @current-change="(val: number) => emit('page-change', val, currentSize)"
    style="margin-top: 16px; justify-content: flex-end"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { TableColumnConfig, TableConfig } from '@/api/table-standard'

const props = withDefaults(defineProps<{
  columns: TableColumnConfig[]
  data: Record<string, any>[]
  config?: Partial<TableConfig>
  total?: number
  loading?: boolean
  page?: number
  pageSize?: number
}>(), {
  config: () => ({}),
  total: 0,
  loading: false,
  page: 1,
  pageSize: 10
})

const emit = defineEmits<{
  (e: 'action', action: string, row: Record<string, any>): void
  (e: 'page-change', current: number, size: number): void
  (e: 'selection-change', selection: Record<string, any>[]): void
}>()

// 内部维护分页状态，与外部双向同步
const currentPage = ref(props.page)
const currentSize = ref(props.pageSize)

watch(() => props.page, val => { currentPage.value = val })
watch(() => props.pageSize, val => { currentSize.value = val })
</script>
