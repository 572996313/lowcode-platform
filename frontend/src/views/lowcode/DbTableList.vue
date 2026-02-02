<template>
  <div class="db-table-list">
    <div class="page-header">
      <h2>库表管理</h2>
    </div>

    <div class="main-content">
      <!-- 左侧分组树 -->
      <div class="group-panel">
        <div class="panel-header">
          <span>分组管理</span>
          <el-button size="small" type="primary" link @click="handleAddGroup(null)">
            <el-icon><Plus /></el-icon>
          </el-button>
        </div>

        <!-- 固定节点 -->
        <div class="fixed-nodes">
          <div
            class="tree-node"
            :class="{ active: selectedGroupId === 'all' }"
            @click="handleSelectGroup('all')"
          >
            <el-icon><Grid /></el-icon>
            <span>全部表</span>
            <span class="count">({{ totalTableCount }})</span>
          </div>
          <div
            class="tree-node"
            :class="{ active: selectedGroupId === 'ungrouped' }"
            @click="handleSelectGroup('ungrouped')"
          >
            <el-icon><Folder /></el-icon>
            <span>未分组</span>
            <span class="count">({{ ungroupedCount }})</span>
          </div>
        </div>

        <el-divider style="margin: 8px 0" />

        <!-- 分组树 -->
        <div class="group-tree">
          <el-tree
            ref="groupTreeRef"
            :data="groupTreeData"
            node-key="id"
            :highlight-current="true"
            :expand-on-click-node="false"
            :default-expand-all="true"
            @node-click="handleGroupClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node-content">
                <el-icon><FolderOpened /></el-icon>
                <span class="node-label">{{ data.groupName }}</span>
                <span class="count">({{ data.tableCount || 0 }})</span>
                <el-dropdown
                  trigger="click"
                  @command="(cmd: string) => handleGroupCommand(cmd, data)"
                  @click.stop
                >
                  <el-icon class="more-icon" @click.stop><MoreFilled /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="addChild">
                        <el-icon><Plus /></el-icon>添加子分组
                      </el-dropdown-item>
                      <el-dropdown-item command="edit">
                        <el-icon><Edit /></el-icon>编辑
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" divided>
                        <el-icon><Delete /></el-icon>删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧表格区域 -->
      <div class="table-panel">
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
          <el-button
            type="success"
            :disabled="selectedRows.length === 0"
            @click="handleBatchMoveToGroup"
          >
            <el-icon><FolderAdd /></el-icon>移动到分组
          </el-button>
        </div>

        <!-- 表格 -->
        <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
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
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" link @click="handleSyncFields(row)">
                <el-icon><Refresh /></el-icon>同步字段
              </el-button>
              <el-button type="success" size="small" link @click="handleViewFields(row)">
                <el-icon><View /></el-icon>查看字段
              </el-button>
              <el-button type="warning" size="small" link @click="handleMoveToGroup(row)">
                <el-icon><FolderAdd /></el-icon>分组
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

    <!-- 分组编辑对话框 -->
    <el-dialog
      v-model="groupDialogVisible"
      :title="groupDialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form :model="groupForm" label-width="80px">
        <el-form-item label="分组名称" required>
          <el-input v-model="groupForm.groupName" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="分组编码">
          <el-input v-model="groupForm.groupCode" placeholder="请输入分组编码（可选）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="groupForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分组描述"
          />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="groupForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGroup" :loading="groupSaveLoading">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 移动到分组对话框 -->
    <el-dialog
      v-model="moveGroupDialogVisible"
      title="移动到分组"
      width="400px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="目标分组">
          <el-tree-select
            v-model="targetGroupId"
            :data="groupSelectOptions"
            placeholder="选择目标分组（不选则移至未分组）"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moveGroupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmMoveGroup" :loading="moveGroupLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTableList,
  getTableFields,
  scanAllTables,
  syncTableFields,
  deleteTable,
  batchUpdateFieldLabels,
  setTableGroup,
  batchSetTableGroup,
  type DbTable,
  type DbTableField
} from '@/api/db-table'
import {
  getGroupTree,
  createGroup,
  updateGroup,
  deleteGroup,
  type DbTableGroup
} from '@/api/db-table-group'

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
const selectedRows = ref<DbTable[]>([])

// 分组相关
const groupTreeRef = ref()
const groupTreeData = ref<DbTableGroup[]>([])
const selectedGroupId = ref<string | number>('all')
const totalTableCount = ref(0)
const ungroupedCount = ref(0)

// 分组对话框
const groupDialogVisible = ref(false)
const groupDialogTitle = ref('新建分组')
const groupForm = reactive<DbTableGroup>({
  groupName: '',
  groupCode: '',
  description: '',
  sortOrder: 0,
  parentId: 0
})
const editingGroupId = ref<number | null>(null)
const groupSaveLoading = ref(false)

// 移动分组对话框
const moveGroupDialogVisible = ref(false)
const targetGroupId = ref<number | null>(null)
const moveGroupLoading = ref(false)
const movingTableIds = ref<number[]>([])

// 字段对话框
const fieldDialogVisible = ref(false)
const fieldLoading = ref(false)
const saveLoading = ref(false)
const currentTable = ref<DbTable | null>(null)
const fieldList = ref<DbTableField[]>([])

// 分组选择器选项
const groupSelectOptions = computed(() => {
  const buildOptions = (groups: DbTableGroup[]): any[] => {
    return groups.map(g => ({
      value: g.id,
      label: g.groupName,
      children: g.children && g.children.length > 0 ? buildOptions(g.children) : undefined
    }))
  }
  return buildOptions(groupTreeData.value)
})

// 加载分组树
const loadGroupTree = async () => {
  try {
    const tree = await getGroupTree()
    groupTreeData.value = tree

    // 计算总表数和未分组数
    const calcTotalCount = (groups: DbTableGroup[]): number => {
      return groups.reduce((sum, g) => {
        const childCount = g.children ? calcTotalCount(g.children) : 0
        return sum + (g.tableCount || 0) - childCount + childCount
      }, 0)
    }
    const groupedCount = calcTotalCount(tree)

    // 获取全部表数量
    const allResult = await getTableList({ current: 1, size: 1 })
    totalTableCount.value = allResult.total

    // 计算未分组数量
    ungroupedCount.value = totalTableCount.value - groupedCount
  } catch (error) {
    console.error('加载分组树失败', error)
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }

    // 根据选中的分组过滤
    if (selectedGroupId.value === 'ungrouped') {
      params.groupId = 'null'
    } else if (selectedGroupId.value !== 'all' && typeof selectedGroupId.value === 'number') {
      params.groupId = selectedGroupId.value
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

// 选择分组
const handleSelectGroup = (groupId: string | number) => {
  selectedGroupId.value = groupId
  pagination.current = 1
  loadData()
}

// 点击分组树节点
const handleGroupClick = (data: DbTableGroup) => {
  selectedGroupId.value = data.id!
  pagination.current = 1
  loadData()
}

// 分组操作命令
const handleGroupCommand = (command: string, data: DbTableGroup) => {
  switch (command) {
    case 'addChild':
      handleAddGroup(data.id!)
      break
    case 'edit':
      handleEditGroup(data)
      break
    case 'delete':
      handleDeleteGroup(data)
      break
  }
}

// 新建分组
const handleAddGroup = (parentId: number | null) => {
  editingGroupId.value = null
  groupDialogTitle.value = parentId ? '新建子分组' : '新建分组'
  groupForm.groupName = ''
  groupForm.groupCode = ''
  groupForm.description = ''
  groupForm.sortOrder = 0
  groupForm.parentId = parentId || 0
  groupDialogVisible.value = true
}

// 编辑分组
const handleEditGroup = (data: DbTableGroup) => {
  editingGroupId.value = data.id!
  groupDialogTitle.value = '编辑分组'
  groupForm.groupName = data.groupName
  groupForm.groupCode = data.groupCode || ''
  groupForm.description = data.description || ''
  groupForm.sortOrder = data.sortOrder || 0
  groupForm.parentId = data.parentId || 0
  groupDialogVisible.value = true
}

// 删除分组
const handleDeleteGroup = (data: DbTableGroup) => {
  ElMessageBox.confirm(
    `确定要删除分组"${data.groupName}"吗？删除后该分组下的表将变为未分组状态。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteGroup(data.id!)
      ElMessage.success('删除成功')
      await loadGroupTree()
      // 如果删除的是当前选中的分组，切换到全部
      if (selectedGroupId.value === data.id) {
        selectedGroupId.value = 'all'
      }
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {})
}

// 保存分组
const handleSaveGroup = async () => {
  if (!groupForm.groupName.trim()) {
    ElMessage.warning('请输入分组名称')
    return
  }

  groupSaveLoading.value = true
  try {
    const data: DbTableGroup = {
      groupName: groupForm.groupName,
      groupCode: groupForm.groupCode || undefined,
      description: groupForm.description || undefined,
      sortOrder: groupForm.sortOrder,
      parentId: groupForm.parentId
    }

    if (editingGroupId.value) {
      await updateGroup(editingGroupId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createGroup(data)
      ElMessage.success('创建成功')
    }

    groupDialogVisible.value = false
    await loadGroupTree()
  } catch (error) {
    ElMessage.error('保存失败')
    console.error(error)
  } finally {
    groupSaveLoading.value = false
  }
}

// 表格选择变化
const handleSelectionChange = (rows: DbTable[]) => {
  selectedRows.value = rows
}

// 单个表移动到分组
const handleMoveToGroup = (row: DbTable) => {
  movingTableIds.value = [row.id!]
  targetGroupId.value = row.groupId || null
  moveGroupDialogVisible.value = true
}

// 批量移动到分组
const handleBatchMoveToGroup = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要移动的表')
    return
  }
  movingTableIds.value = selectedRows.value.map(r => r.id!)
  targetGroupId.value = null
  moveGroupDialogVisible.value = true
}

// 确认移动分组
const handleConfirmMoveGroup = async () => {
  moveGroupLoading.value = true
  try {
    if (movingTableIds.value.length === 1) {
      await setTableGroup(movingTableIds.value[0], targetGroupId.value)
    } else {
      await batchSetTableGroup(movingTableIds.value, targetGroupId.value)
    }
    ElMessage.success('移动成功')
    moveGroupDialogVisible.value = false
    await loadGroupTree()
    loadData()
  } catch (error) {
    ElMessage.error('移动失败')
    console.error(error)
  } finally {
    moveGroupLoading.value = false
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
      await loadGroupTree()
      loadData()
    } catch (error) {
      ElMessage.error('扫描失败')
      console.error(error)
    } finally {
      scanLoading.value = false
    }
  }).catch(() => {})
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
  }).catch(() => {})
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
      fieldLabel: f.fieldLabel || f.fieldComment || ''
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
      await loadGroupTree()
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {})
}

// 页面挂载时加载数据
onMounted(async () => {
  await loadGroupTree()
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

  .main-content {
    flex: 1;
    display: flex;
    padding: 16px;
    gap: 16px;
    overflow: hidden;
  }

  .group-panel {
    width: 280px;
    background-color: #fff;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid #ebeef5;
      font-weight: 500;
      color: #303133;
    }

    .fixed-nodes {
      padding: 8px 0;

      .tree-node {
        display: flex;
        align-items: center;
        padding: 8px 16px;
        cursor: pointer;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f5f7fa;
        }

        &.active {
          background-color: #ecf5ff;
          color: #409eff;
        }

        .el-icon {
          margin-right: 8px;
        }

        .count {
          margin-left: auto;
          color: #909399;
          font-size: 12px;
        }
      }
    }

    .group-tree {
      flex: 1;
      overflow: auto;
      padding: 0 8px 8px;

      :deep(.el-tree-node__content) {
        height: 36px;
      }

      .tree-node-content {
        flex: 1;
        display: flex;
        align-items: center;
        padding-right: 8px;

        .el-icon {
          margin-right: 6px;
          color: #e6a23c;
        }

        .node-label {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .count {
          margin-left: 4px;
          color: #909399;
          font-size: 12px;
        }

        .more-icon {
          margin-left: 8px;
          color: #909399;
          opacity: 0;
          transition: opacity 0.2s;
          cursor: pointer;

          &:hover {
            color: #409eff;
          }
        }
      }

      :deep(.el-tree-node__content:hover) {
        .more-icon {
          opacity: 1;
        }
      }
    }
  }

  .table-panel {
    flex: 1;
    background-color: #fff;
    border-radius: 4px;
    padding: 16px;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .search-bar {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
    }

    .toolbar {
      margin-bottom: 16px;
    }

    .el-table {
      flex: 1;
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
