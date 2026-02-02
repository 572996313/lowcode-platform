<template>
  <div class="table-column-binding">
    <div class="page-header">
      <div class="header-left">
        <el-button link @click="handleGoBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>表格列绑定 - {{ tableInfo.tableName }}</h2>
      </div>
      <div class="header-right">
        <el-button @click="handleGoBack">关闭</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存绑定
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <!-- 关联数据库表选择 -->
      <el-card class="config-card" header="关联数据库表">
        <el-form label-width="120px">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="选择数据库表">
                <el-select
                  v-model="selectedTableId"
                  placeholder="请选择数据库表"
                  filterable
                  @change="handleTableChange"
                  style="width: 100%"
                >
                  <el-option
                    v-for="table in dbTables"
                    :key="table.id"
                    :label="`${table.tableComment || table.tableName} (${table.tableName})`"
                    :value="table.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 列绑定列表 -->
      <el-card class="config-card" header="列绑定配置">
        <div v-if="!selectedTableId" class="empty-tip">
          <el-empty description="请先选择数据库表" />
        </div>
        <div v-else>
          <div class="binding-header">
            <span>拖拽调整排序，勾选选择需要绑定的列</span>
            <el-button size="small" @click="handleBindAll">
              全选
            </el-button>
            <el-button size="small" @click="handleUnbindAll">
              全不选
            </el-button>
          </div>

          <el-table
            ref="bindingTableRef"
            :data="availableFields"
            border
            stripe
            row-key="id"
            style="width: 100%"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="fieldName" label="字段名" width="150" />
            <el-table-column prop="fieldLabel" label="列标题" width="150">
              <template #default="{ row }">
                <el-input
                  v-model="row._columnLabel"
                  placeholder="列标题"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column prop="fieldType" label="字段类型" width="100" />
            <el-table-column label="列类型" width="130">
              <template #default="{ row }">
                <el-select
                  v-model="row._columnType"
                  placeholder="列类型"
                  size="small"
                >
                  <el-option label="文本" value="text" />
                  <el-option label="图片" value="image" />
                  <el-option label="标签" value="tag" />
                  <el-option label="开关" value="switch" />
                  <el-option label="链接" value="link" />
                  <el-option label="日期" value="date" />
                  <el-option label="数字" value="number" />
                  <el-option label="字典" value="dict" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="宽度" width="120">
              <template #default="{ row }">
                <el-input-number
                  v-model="row._width"
                  :min="50"
                  :max="500"
                  :step="10"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column label="对齐" width="100">
              <template #default="{ row }">
                <el-select
                  v-model="row._align"
                  placeholder="对齐"
                  size="small"
                >
                  <el-option label="左" value="left" />
                  <el-option label="居中" value="center" />
                  <el-option label="右" value="right" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="排序" width="80">
              <template #default="{ row }">
                <el-switch v-model="row._sortable" />
              </template>
            </el-table-column>
            <el-table-column label="排序" width="80">
              <template #default="{ row }">
                <el-input-number
                  v-model="row._sortOrder"
                  :min="0"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column label="显示" width="80">
              <template #default="{ row }">
                <el-switch v-model="row._visible" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import {
  getBindingsByTableConfigId,
  batchSaveBindings,
  type TableColumnBinding
} from '@/api/table-column-binding'
import { getTableList, getTableFields, type DbTable, type DbTableField } from '@/api/db-table'
import { getTableConfig, type TableConfig } from '@/api/table'

const router = useRouter()
const route = useRoute()

const bindingTableRef = ref()
const saving = ref(false)
const selectedTableId = ref<number>()
const tableConfigId = ref<number>(0)

// 表格信息
const tableInfo = reactive<TableConfig>({
  tableName: '',
  tableCode: '',
  sourceTableId: undefined
})

// 数据库表列表
const dbTables = ref<DbTable[]>([])

// 可用字段列表
const availableFields = ref<any[]>([])

// 已绑定的字段ID集合
const boundFieldIds = ref<Set<number>>(new Set())

// 初始化字段扩展属性
const initFieldExtProps = (field: any) => {
  field._columnLabel = field._columnLabel || field.fieldLabel || field.fieldComment || field.fieldName
  field._columnType = field._columnType || 'text'
  field._width = field._width || undefined
  field._align = field._align || 'center'
  field._sortable = field._sortable !== undefined ? field._sortable : false
  field._sortOrder = field._sortOrder !== undefined ? field._sortOrder : 0
  field._visible = field._visible !== undefined ? field._visible : true
  return field
}

// 返回列表
const handleGoBack = () => {
  router.push({ name: 'TableManage' })
}

// 加载表格信息
const loadTableInfo = async () => {
  const id = route.query.tableConfigId as string
  if (id) {
    tableConfigId.value = Number(id)
    try {
      const data = await getTableConfig(tableConfigId.value)
      Object.assign(tableInfo, data)

      // 如果表格已关联表，自动选中
      if (data.sourceTableId) {
        selectedTableId.value = data.sourceTableId
        await handleTableChange(data.sourceTableId)
      }

      // 加载已有绑定
      const bindings = await getBindingsByTableConfigId(tableConfigId.value)
      boundFieldIds.value = new Set(bindings.map(b => b.fieldId))

      // 将已有绑定配置合并到字段
      bindings.forEach(binding => {
        const field = availableFields.value.find(f => f.id === binding.fieldId)
        if (field) {
          Object.assign(field, {
            _columnLabel: binding.columnLabel,
            _columnType: binding.columnType,
            _width: binding.width,
            _align: binding.align,
            _sortable: binding.sortable,
            _sortOrder: binding.sortOrder,
            _visible: binding.visible
          })
        }
      })
    } catch (error) {
      ElMessage.error('加载表格信息失败')
    }
  }
}

// 加载数据库表列表
const loadDbTables = async () => {
  try {
    const result = await getTableList({ current: 1, size: 1000 })
    dbTables.value = result.records
  } catch (error) {
    ElMessage.error('加载数据库表列表失败')
  }
}

// 处理表选择变化
const handleTableChange = async (tableId: number) => {
  selectedTableId.value = tableId
  try {
    const fields = await getTableFields(tableId)
    // 为每个字段添加默认配置
    availableFields.value = fields.map((field, index) => ({
      ...field,
      _columnLabel: field.fieldLabel || field.fieldComment || field.fieldName,
      _columnType: getDefaultColumnType(field.fieldType),
      _width: undefined,
      _align: 'center',
      _sortable: false,
      _sortOrder: index,
      _visible: true
    }))
  } catch (error) {
    ElMessage.error('获取字段列表失败')
    availableFields.value = []
  }
}

// 根据字段类型获取默认列类型
const getDefaultColumnType = (fieldType: string): string => {
  const type = fieldType.toUpperCase()
  if (type.includes('DATE') || type.includes('TIMESTAMP') || type.includes('DATETIME')) {
    return 'date'
  }
  if (type.includes('TINYINT') || type.includes('BIT')) {
    return 'switch'
  }
  if (type.includes('INT') || type.includes('DECIMAL') || type.includes('DOUBLE') || type.includes('FLOAT')) {
    return 'number'
  }
  if (type.includes('TEXT')) {
    return 'text'
  }
  return 'text'
}

// 全选
const handleBindAll = () => {
  bindingTableRef.value?.toggleAllSelection()
}

// 全不选
const handleUnbindAll = () => {
  bindingTableRef.value?.clearSelection()
}

// 保存绑定
const handleSave = async () => {
  if (!tableConfigId.value) {
    ElMessage.error('表格配置ID不存在')
    return
  }

  const selectedRows = bindingTableRef.value?.getSelectionRows() || []

  if (selectedRows.length === 0) {
    ElMessage.warning('请至少选择一个字段')
    return
  }

  saving.value = true
  try {
    const bindings: TableColumnBinding[] = selectedRows.map((row, index) => ({
      tableConfigId: tableConfigId.value!,
      fieldId: row.id,
      columnLabel: row._columnLabel || row.fieldLabel || row.fieldName,
      columnType: row._columnType || 'text',
      width: row._width,
      align: row._align || 'center',
      sortable: row._sortable || false,
      showOverflow: true,
      sortOrder: row._sortOrder !== undefined ? row._sortOrder : index,
      visible: row._visible !== undefined ? row._visible : true
    }))

    await batchSaveBindings(tableConfigId.value, bindings)
    ElMessage.success('保存成功')
    handleGoBack()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 页面挂载时加载数据
onMounted(async () => {
  await loadDbTables()
  await loadTableInfo()
})
</script>

<style lang="scss" scoped>
.table-column-binding {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .content-container {
    flex: 1;
    padding: 24px;
    overflow: auto;

    .config-card {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .empty-tip {
      padding: 40px 0;
    }

    .binding-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding: 12px;
      background-color: #f5f7fa;
      border-radius: 4px;

      span {
        color: #606266;
        font-size: 14px;
      }
    }
  }
}
</style>
