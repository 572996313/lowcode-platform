<template>
  <div class="db-table-list">
    <div class="page-header">
      <h2>库表管理</h2>
    </div>

    <div class="content-container">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.tableName"
          placeholder="请输入表名"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.tableType"
          placeholder="表类型"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="表" value="TABLE" />
          <el-option label="视图" value="VIEW" />
        </el-select>
        <el-select
          v-model="searchForm.syncStatus"
          placeholder="同步状态"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="未同步" :value="0" />
          <el-option label="已同步" :value="1" />
        </el-select>
        <el-button type="primary" @click="handleSearch" style="margin-left: 12px">
          查询
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleScanAll" :loading="scanLoading">
          <el-icon><Refresh /></el-icon>扫描数据库
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
        <el-table-column prop="tableName" label="表名" min-width="150" />
        <el-table-column prop="tableType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.tableType === 'TABLE' ? 'primary' : 'success'" size="small">
              {{ row.tableType === 'TABLE' ? '表' : '视图' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tableComment" label="表注释" min-width="200" show-overflow-tooltip />
        <el-table-column label="同步状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.syncStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.syncStatus === 1 ? '已同步' : '未同步' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="syncTime" label="同步时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleSyncFields(row)">
              <el-icon><Refresh /></el-icon>同步字段
            </el-button>
            <el-button type="success" size="small" link @click="handleViewFields(row)">
              <el-icon><View /></el-icon>查看字段
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

    <!-- 字段管理对话框 -->
    <el-dialog
      v-model="fieldDialogVisible"
      :title="`${currentTable?.tableName} - 字段列表`"
      width="900px"
      destroy-on-close
    >
      <el-table
        :data="fieldList"
        v-loading="fieldLoading"
        border
        stripe
        max-height="500"
        style="width: 100%"
      >
        <el-table-column prop="fieldName" label="字段名" width="150" fixed />
        <el-table-column prop="fieldType" label="字段类型" width="120" />
        <el-table-column prop="fieldLength" label="长度" width="80" />
        <el-table-column label="可空" width="60" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isNullable === 1 ? 'success' : 'danger'" size="small">
              {{ row.isNullable === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主键" width="60" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isPrimaryKey === 1" type="warning" size="small">PK</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fieldComment" label="注释" min-width="150" show-overflow-tooltip />
        <el-table-column label="显示标签" width="180">
          <template #default="{ row }">
            <el-input
              v-model="row.fieldLabel"
              placeholder="可编辑显示标签"
              size="small"
              clearable
            />
          </template>
        </el-table-column>
        <el-table-column prop="ordinalPosition" label="位置" width="60" align="center" />
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="fieldDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveFieldLabels" :loading="saveLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTableList,
  getTableFields,
  scanAllTables,
  syncTableFields,
  deleteTable,
  batchUpdateFieldLabels,
  type DbTable,
  type DbTableField
} from '@/api/db-table'

// 搜索表单
const searchForm = reactive({
  tableName: '',
  tableType: '',
  syncStatus: undefined as number | undefined
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<DbTable[]>([])
const loading = ref(false)
const scanLoading = ref(false)

// 字段对话框
const fieldDialogVisible = ref(false)
const fieldLoading = ref(false)
const saveLoading = ref(false)
const currentTable = ref<DbTable | null>(null)
const fieldList = ref<DbTableField[]>([])

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
  } catch (error) {
    ElMessage.error('加载库表列表失败')
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
  searchForm.tableType = ''
  searchForm.syncStatus = undefined
  pagination.current = 1
  loadData()
}

// 扫描数据库
const handleScanAll = () => {
  ElMessageBox.confirm('确定要扫描数据库吗？这将更新所有表和视图列表。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    scanLoading.value = true
    try {
      await scanAllTables()
      ElMessage.success('扫描成功')
      loadData()
    } catch (error) {
      ElMessage.error('扫描失败')
      console.error(error)
    } finally {
      scanLoading.value = false
    }
  }).catch(() => {
    // 取消扫描
  })
}

// 同步字段
const handleSyncFields = (row: DbTable) => {
  ElMessageBox.confirm(
    `确定要同步表 ${row.tableName} 的字段信息吗？这将覆盖现有字段数据。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await syncTableFields(row.id!)
      ElMessage.success('同步成功')
      loadData()
    } catch (error) {
      ElMessage.error('同步失败')
      console.error(error)
    }
  }).catch(() => {
    // 取消同步
  })
}

// 查看字段
const handleViewFields = async (row: DbTable) => {
  currentTable.value = row
  fieldDialogVisible.value = true
  fieldLoading.value = true
  try {
    const fields = await getTableFields(row.id!)
    fieldList.value = fields.map(f => ({
      ...f,
      fieldLabel: f.fieldLabel || f.fieldComment || '' // 默认使用注释
    }))
  } catch (error) {
    ElMessage.error('加载字段列表失败')
    console.error(error)
  } finally {
    fieldLoading.value = false
  }
}

// 保存字段标签
const handleSaveFieldLabels = async () => {
  saveLoading.value = true
  try {
    const fieldUpdates = fieldList.value.map(f => ({
      id: f.id,
      fieldLabel: f.fieldLabel || ''
    }))
    await batchUpdateFieldLabels(fieldUpdates)
    ElMessage.success('保存成功')
    fieldDialogVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败')
    console.error(error)
  } finally {
    saveLoading.value = false
  }
}

// 删除表
const handleDelete = (row: DbTable) => {
  ElMessageBox.confirm(
    `确定要删除表 ${row.tableName} 吗？删除后将同时删除所有关联的字段数据。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
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
.db-table-list {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
