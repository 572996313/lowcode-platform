<template>
  <div class="page-layout-manage">
    <div class="page-header">
      <h2>页面布局管理</h2>
    </div>

    <div class="content-container">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.layoutName"
          placeholder="请输入布局名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.layoutCode"
          placeholder="请输入布局编码"
          clearable
          style="width: 200px; margin-left: 12px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.layoutType"
          placeholder="布局类型"
          clearable
          style="width: 150px; margin-left: 12px"
        >
          <el-option label="上下布局" value="top-bottom" />
          <el-option label="左右布局" value="left-right" />
          <el-option label="树表布局" value="tree-table" />
          <el-option label="标签页布局" value="tabs" />
        </el-select>
        <el-select
          v-model="searchForm.published"
          placeholder="发布状态"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="已发布" :value="true" />
          <el-option label="未发布" :value="false" />
        </el-select>
        <el-button type="primary" @click="handleSearch" style="margin-left: 12px">
          查询
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建布局
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="layoutName" label="布局名称" min-width="150" />
        <el-table-column prop="layoutCode" label="布局编码" min-width="150" />
        <el-table-column label="布局类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getLayoutTypeName(row.layoutType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.published ? 'success' : 'info'" size="small">
              {{ row.published ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="routePath" label="路由路径" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button
              v-if="!row.published"
              type="success"
              size="small"
              link
              @click="handlePublish(row)"
            >
              <el-icon><Upload /></el-icon>发布
            </el-button>
            <el-button
              v-else
              type="warning"
              size="small"
              link
              @click="handleUnpublish(row)"
            >
              <el-icon><Download /></el-icon>取消发布
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Upload, Download } from '@element-plus/icons-vue'
import {
  getPageLayoutList,
  deletePageLayout,
  publishPageLayout,
  unpublishPageLayout,
  type PageLayout
} from '@/api/page-layout'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  layoutName: '',
  layoutCode: '',
  layoutType: undefined as string | undefined,
  published: undefined as boolean | undefined
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<PageLayout[]>([])
const loading = ref(false)

// 布局类型映射
const layoutTypeMap: Record<string, string> = {
  'top-bottom': '上下布局',
  'left-right': '左右布局',
  'tree-table': '树表布局',
  'tabs': '标签页布局'
}

const getLayoutTypeName = (type: string) => {
  return layoutTypeMap[type] || type
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const result = await getPageLayoutList(params)
    tableData.value = result.records
    pagination.total = result.total
  } catch (error) {
    ElMessage.error('加载布局列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.layoutName = ''
  searchForm.layoutCode = ''
  searchForm.layoutType = undefined
  searchForm.published = undefined
  pagination.current = 1
  loadData()
}

// 新建布局
const handleCreate = () => {
  router.push({ name: 'PageLayoutDesigner' })
}

// 编辑布局
const handleEdit = (row: PageLayout) => {
  router.push({
    name: 'PageLayoutDesigner',
    query: { id: row.id?.toString() }
  })
}

// 发布布局
const handlePublish = (row: PageLayout) => {
  ElMessageBox.confirm('确定要发布该布局吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await publishPageLayout(row.id!)
      ElMessage.success('发布成功')
      loadData()
    } catch (error) {
      ElMessage.error('发布失败')
      console.error(error)
    }
  })
}

// 取消发布布局
const handleUnpublish = (row: PageLayout) => {
  ElMessageBox.confirm('确定要取消发布该布局吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await unpublishPageLayout(row.id!)
      ElMessage.success('取消发布成功')
      loadData()
    } catch (error) {
      ElMessage.error('取消发布失败')
      console.error(error)
    }
  })
}

// 删除布局
const handleDelete = (row: PageLayout) => {
  ElMessageBox.confirm('确定要删除该布局吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePageLayout(row.id!)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error((error as Error).message || '删除失败')
    }
  })
}

// 页面挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-layout-manage {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    padding: 20px 24px;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
      color: #303133;
    }
  }

  .content-container {
    flex: 1;
    padding: 24px;
    overflow: auto;

    .search-bar {
      display: flex;
      align-items: center;
      background-color: #fff;
      padding: 16px 20px;
      border-radius: 4px;
      margin-bottom: 16px;
    }

    .toolbar {
      margin-bottom: 16px;
    }

    .pagination {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
