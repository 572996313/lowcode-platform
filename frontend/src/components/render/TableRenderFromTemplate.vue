<template>
  <div class="table-render-from-template">
    <!-- 表格 -->
    <el-table
      :data="tableData"
      :border="border"
      :stripe="stripe"
      style="width: 100%"
    >
      <!-- 渲染 V4 格式的列 -->
      <TableColumnRender
        v-for="column in columns"
        :key="column.prop"
        :column="column"
        @action-click="handleActionClick"
      />
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="pagination"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end;"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { TableColumnV4, TableConfigV4, ActionButton } from '@/types/table'
import TableColumnRender from './TableColumnRender.vue'

interface Props {
  columns: TableColumnV4[]
  tableConfig?: TableConfigV4
  title?: string
  apiUrl?: string
}

const props = withDefaults(defineProps<Props>(), {
  tableConfig: () => ({})
})

const emit = defineEmits<{
  (e: 'action-click', action: ActionButton, row: any): void
}>()

// 从 tableConfig 获取配置
const border = computed(() => props.tableConfig?.border ?? true)
const stripe = computed(() => props.tableConfig?.stripe ?? true)
const pagination = computed(() => props.tableConfig?.pagination ?? true)
const pageSize = computed(() => props.tableConfig?.pageSize ?? 20)
const pageSizes = computed(() => props.tableConfig?.pageSizes ?? [10, 20, 50, 100])

// 表格数据
const tableData = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)

// 模拟数据（用于演示）
const mockData = ref([
  {
    id: 1,
    avatar: 'https://i.pravatar.cc/150?img=1',
    userName: '张三',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    department: '技术部',
    position: '前端工程师',
    status: '1',
    registerTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    avatar: 'https://i.pravatar.cc/150?img=2',
    userName: '李四',
    phone: '13800138002',
    email: 'lisi@example.com',
    department: '产品部',
    position: '产品经理',
    status: '0',
    registerTime: '2024-02-20 14:20:00'
  },
  {
    id: 3,
    avatar: 'https://i.pravatar.cc/150?img=3',
    userName: '王五',
    phone: '13800138003',
    email: 'wangwu@example.com',
    department: '设计部',
    position: 'UI设计师',
    status: '1',
    registerTime: '2024-03-10 09:15:00'
  },
  {
    id: 4,
    avatar: 'https://i.pravatar.cc/150?img=4',
    userName: '赵六',
    phone: '13800138004',
    email: 'zhaoliu@example.com',
    department: '技术部',
    position: '后端工程师',
    status: '1',
    registerTime: '2024-03-25 16:45:00'
  },
  {
    id: 5,
    avatar: 'https://i.pravatar.cc/150?img=5',
    userName: '孙七',
    phone: '13800138005',
    email: 'sunqi@example.com',
    department: '运营部',
    position: '运营专员',
    status: '0',
    registerTime: '2024-04-05 11:20:00'
  }
])

// 加载数据
const loadData = async () => {
  // 如果配置了 API 地址，调用 API
  if (props.apiUrl) {
    // TODO: 实现真实的 API 调用
    ElMessage.info('API 调用功能开发中...')
    return
  }

  // 否则使用模拟数据
  tableData.value = mockData.value
  total.value = mockData.value.length
}

// 处理页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadData()
}

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  currentPage.value = 1
  loadData()
}

// 处理操作按钮点击
const handleActionClick = (action: ActionButton, row: any) => {
  ElMessage.info(`操作: ${action.label} - 数据: ${row.userName || row.id}`)
  emit('action-click', action, row)
}

// 刷新数据
const refresh = () => {
  currentPage.value = 1
  loadData()
}

// 暴露方法供父组件调用
defineExpose({
  loadData,
  refresh
})

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.table-render-from-template {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
