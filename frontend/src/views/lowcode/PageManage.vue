<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="页面名称">
          <el-input v-model="queryParams.pageName" placeholder="请输入页面名称" clearable />
        </el-form-item>
        <el-form-item label="页面类型">
          <el-select v-model="queryParams.pageType" placeholder="请选择" clearable>
            <el-option label="列表页" value="list" />
            <el-option label="表单页" value="form" />
            <el-option label="详情页" value="detail" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增页面
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="pageName" label="页面名称" min-width="150" />
      <el-table-column prop="pageCode" label="页面编码" width="150" />
      <el-table-column prop="pageType" label="页面类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ pageTypeMap[row.pageType] || row.pageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="layoutType" label="布局类型" width="120">
        <template #default="{ row }">
          {{ layoutTypeMap[row.layoutType] || row.layoutType }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.current"
      v-model:page-size="queryParams.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
      style="margin-top: 16px; justify-content: flex-end;"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  pageName: '',
  pageType: ''
})

const pageTypeMap: Record<string, string> = {
  list: '列表页',
  form: '表单页',
  detail: '详情页',
  custom: '自定义'
}

const layoutTypeMap: Record<string, string> = {
  'tree-table': '左树右表',
  'top-bottom': '上下布局',
  'left-right': '左右布局',
  tabs: 'Tab页签',
  custom: '自定义'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/page/list', queryParams)
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  loadData()
}

const handleReset = () => {
  queryParams.pageName = ''
  queryParams.pageType = ''
  queryParams.current = 1
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增页面功能开发中...')
}

const handleEdit = (row: any) => {
  ElMessage.info('编辑页面功能开发中...')
}

const handleDesign = (row: any) => {
  ElMessage.info('页面设计器开发中...')
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该页面配置吗？', '提示', { type: 'warning' })
    await request.delete(`/page/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}
</style>
