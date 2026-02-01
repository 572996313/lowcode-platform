<template>
  <div class="page-render-v6">
    <!-- 工具栏 -->
    <ToolbarRenderV6
      v-if="pageConfig.toolbar.enabled"
      :config="pageConfig.toolbar"
      @action="handleToolbarAction"
    />

    <!-- 查询区 -->
    <SearchRenderV6
      v-if="pageConfig.search.enabled"
      :config="pageConfig.search"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 表格 -->
    <TableRenderV6
      :config="pageConfig.table"
      :data="tableData"
      :loading="loading"
      @action="handleTableAction"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import ToolbarRenderV6 from '@/components/render-v6/ToolbarRenderV6.vue'
import SearchRenderV6 from '@/components/render-v6/SearchRenderV6.vue'
import TableRenderV6 from '@/components/render-v6/TableRenderV6.vue'
import type { NewPageConfig } from '@/types/page-v6'

// Props
interface Props {
  pageConfig: NewPageConfig
}

const props = defineProps<Props>()

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([])

// 初始化
onMounted(() => {
  loadData()
})

/**
 * 加载数据
 */
async function loadData() {
  loading.value = true
  try {
    // TODO: 调用 API 加载数据
    // const res = await request({
    //   url: props.pageConfig.globalConfig.apiEndpoint,
    //   method: 'get',
    //   params: searchParams.value
    // })
    // tableData.value = res.data

    // 模拟数据
    tableData.value = [
      { id: 1, name: '张三', age: 25, status: 'active' },
      { id: 2, name: '李四', age: 30, status: 'inactive' }
    ]
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 工具栏操作
 */
function handleToolbarAction(action: any) {
  console.log('工具栏操作:', action)
}

/**
 * 查询
 */
function handleSearch(params: any) {
  console.log('查询参数:', params)
  loadData()
}

/**
 * 重置
 */
function handleReset() {
  console.log('重置查询')
  loadData()
}

/**
 * 表格操作
 */
function handleTableAction(action: any, row: any) {
  console.log('表格操作:', action, row)
}
</script>

<style scoped lang="scss">
.page-render-v6 {
  padding: 20px;
  background: #fff;
}
</style>
