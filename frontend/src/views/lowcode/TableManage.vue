<template>
  <div class="table-manage">
    <div class="page-header">
      <h2>表格管理</h2>
    </div>

    <div class="content-container">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.tableName"
          placeholder="请输入表格名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.tableCode"
          placeholder="请输入表格编码"
          clearable
          style="width: 200px; margin-left: 12px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" @click="handleSearch" style="margin-left: 12px">
          查询
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建表格
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
        <el-table-column prop="tableName" label="表格名称" min-width="150" />
        <el-table-column prop="tableCode" label="表格编码" min-width="150" />
        <el-table-column prop="apiUrl" label="API地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="搜索字段" width="90" align="center">
          <template #default="{ row }">
            {{ getConfigSummary(row).searchCount }}
          </template>
        </el-table-column>
        <el-table-column label="工具栏" width="80" align="center">
          <template #default="{ row }">
            {{ getConfigSummary(row).buttonCount }}
          </template>
        </el-table-column>
        <el-table-column label="列数量" width="80" align="center">
          <template #default="{ row }">
            {{ getConfigSummary(row).columnCount }}
          </template>
        </el-table-column>
        <el-table-column label="分页" width="80">
          <template #default="{ row }">
            <el-tag :type="row.pagination ? 'success' : 'info'" size="small">
              {{ row.pagination ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>设计
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getTableList, deleteTable, updateTable, type TableConfig } from '@/api/table'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  tableName: '',
  tableCode: '',
  status: undefined as boolean | undefined
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<TableConfig[]>([])
const loading = ref(false)

// 解析 configJson 获取配置摘要
const getConfigSummary = (row: TableConfig) => {
  try {
    if (row.configJson) {
      const config = JSON.parse(row.configJson)
      return {
        searchCount: config.searchFields?.length || 0,
        buttonCount: config.toolbar?.buttons?.length || 0,
        columnCount: config.tableColumns?.length || row.columns?.length || 0
      }
    }
  } catch (e) { /* ignore */ }
  return {
    searchCount: 0,
    buttonCount: 0,
    columnCount: row.columns?.length || 0
  }
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
    const result = await getTableList(params)
    tableData.value = result.records
    pagination.total = result.total

    // 构建表ID映射（这里简化处理，实际应从库表管理获取）
    result.records.forEach((item: any) => {
      if (item.sourceTableId && item.sourceTableName) {
        tableMap.value[item.sourceTableId] = item.sourceTableName
      }
    })
  } catch (error) {
    ElMessage.error('加载表格列表失败')
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
  searchForm.tableName = ''
  searchForm.tableCode = ''
  searchForm.status = undefined
  pagination.current = 1
  loadData()
}

// 新建表格
const handleCreate = () => {
  router.push({ name: 'TableDesigner' })
}

// 编辑表格
const handleEdit = (row: TableConfig) => {
  router.push({
    name: 'TableDesigner',
    query: { id: row.id?.toString() }
  })
}

// 列绑定
const handleColumnBinding = (row: TableConfig) => {
  router.push({
    name: 'TableColumnBinding',
    query: { tableConfigId: row.id?.toString() }
  })
}

// 删除表格
const handleDelete = (row: TableConfig) => {
  ElMessageBox.confirm('确定要删除该表格吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTable(row.id!)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 页面挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.table-manage {
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
