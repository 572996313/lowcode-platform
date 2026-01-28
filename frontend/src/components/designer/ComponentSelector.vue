<template>
  <el-dialog
    v-model="visible"
    :title="type === 'table' ? '选择表格配置' : '选择表单配置'"
    width="700px"
    @close="handleClose"
  >
    <el-tabs v-model="activeTab">
      <!-- 表格配置 -->
      <el-tab-pane label="表格配置" name="table">
        <el-table
          :data="tableList"
          border
          max-height="400"
          @row-click="handleSelectRow"
          highlight-current-row
        >
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="tableName" label="表格名称" min-width="120" />
          <el-table-column prop="tableCode" label="表格编码" min-width="120" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click.stop="handleSelect(row, 'table')">选择</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="tableParams.current"
          v-model:page-size="tableParams.size"
          :page-sizes="[10, 20, 50]"
          :total="tableTotal"
          layout="total, sizes, prev, pager, next"
          style="margin-top: 12px; justify-content: center;"
          @size-change="loadTableList"
          @current-change="loadTableList"
        />
      </el-tab-pane>

      <!-- 表单配置 -->
      <el-tab-pane label="表单配置" name="form">
        <el-table
          :data="formList"
          border
          max-height="400"
          @row-click="handleSelectRow"
          highlight-current-row
        >
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="formName" label="表单名称" min-width="120" />
          <el-table-column prop="formCode" label="表单编码" min-width="120" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click.stop="handleSelect(row, 'form')">选择</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="formParams.current"
          v-model:page-size="formParams.size"
          :page-sizes="[10, 20, 50]"
          :total="formTotal"
          layout="total, sizes, prev, pager, next"
          style="margin-top: 12px; justify-content: center;"
          @size-change="loadFormList"
          @current-change="loadFormList"
        />
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { TableConfig } from '@/api/table'
import type { FormConfig } from '@/api/form'
import { getTableList } from '@/api/table'
import { getFormList } from '@/api/form'

interface Props {
  modelValue: boolean
  type?: 'table' | 'form'
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'select', config: { id: number; name: string; type: 'table' | 'form' }): void
}

const props = withDefaults(defineProps<Props>(), {
  type: 'table'
})

const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const activeTab = ref<'table' | 'form'>(props.type)

const tableList = ref<TableConfig[]>([])
const tableTotal = ref(0)
const tableParams = ref({
  current: 1,
  size: 10
})

const formList = ref<FormConfig[]>([])
const formTotal = ref(0)
const formParams = ref({
  current: 1,
  size: 10
})

const loadTableList = async () => {
  try {
    const res = await getTableList(tableParams.value)
    tableList.value = res.records || []
    tableTotal.value = res.total || 0
  } catch (error) {
    console.error('加载表格列表失败:', error)
    tableList.value = []
  }
}

const loadFormList = async () => {
  try {
    const res = await getFormList(formParams.value)
    formList.value = res.records || []
    formTotal.value = res.total || 0
  } catch (error) {
    console.error('加载表单列表失败:', error)
    formList.value = []
  }
}

const handleSelect = (row: TableConfig | FormConfig, type: 'table' | 'form') => {
  const config = {
    id: row.id!,
    name: type === 'table' ? (row as TableConfig).tableName : (row as FormConfig).formName,
    type
  }
  emit('select', config)
  visible.value = false
}

const handleSelectRow = (row: TableConfig | FormConfig) => {
  const type = activeTab.value
  handleSelect(row, type)
}

const handleClose = () => {
  visible.value = false
}

watch(() => props.modelValue, (val) => {
  if (val) {
    activeTab.value = props.type
    if (props.type === 'table') {
      loadTableList()
    } else {
      loadFormList()
    }
  }
})

watch(activeTab, (tab) => {
  if (tab === 'table' && tableList.value.length === 0) {
    loadTableList()
  } else if (tab === 'form' && formList.value.length === 0) {
    loadFormList()
  }
})
</script>
