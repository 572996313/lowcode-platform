<template>
  <div class="form-field-binding">
    <div class="page-header">
      <div class="header-left">
        <el-button link @click="handleGoBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>表单字段绑定 - {{ formInfo.formName }}</h2>
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

      <!-- 字段绑定列表 -->
      <el-card class="config-card" header="字段绑定配置">
        <div v-if="!selectedTableId" class="empty-tip">
          <el-empty description="请先选择数据库表" />
        </div>
        <div v-else>
          <div class="binding-header">
            <span>拖拽调整排序，勾选选择需要绑定的字段</span>
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
            <el-table-column prop="fieldLabel" label="显示标签" width="150">
              <template #default="{ row }">
                <el-input
                  v-model="row.fieldLabel"
                  placeholder="显示标签"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column prop="fieldType" label="字段类型" width="120" />
            <el-table-column label="控件类型" width="150">
              <template #default="{ row }">
                <el-select
                  v-model="row._widgetType"
                  placeholder="选择控件"
                  size="small"
                  @change="handleWidgetTypeChange(row)"
                >
                  <el-option label="输入框" value="input" />
                  <el-option label="文本域" value="textarea" />
                  <el-option label="下拉框" value="select" />
                  <el-option label="单选框" value="radio" />
                  <el-option label="复选框" value="checkbox" />
                  <el-option label="开关" value="switch" />
                  <el-option label="日期" value="date" />
                  <el-option label="日期时间" value="datetime" />
                  <el-option label="数字" value="number" />
                  <el-option label="上传" value="upload" />
                  <el-option label="级联选择" value="cascader" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="必填" width="80">
              <template #default="{ row }">
                <el-switch v-model="row._required" />
              </template>
            </el-table-column>
            <el-table-column label="栅格" width="100">
              <template #default="{ row }">
                <el-input-number
                  v-model="row._span"
                  :min="1"
                  :max="24"
                  size="small"
                />
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
  getBindingsByFormId,
  batchSaveBindings,
  type FormFieldBinding
} from '@/api/form-field-binding'
import { getTableList, getTableFields, type DbTable, type DbTableField } from '@/api/db-table'
import { getFormConfig, type FormConfig } from '@/api/form'

const router = useRouter()
const route = useRoute()

const bindingTableRef = ref()
const saving = ref(false)
const selectedTableId = ref<number>()
const formId = ref<number>(0)

// 表单信息
const formInfo = reactive<FormConfig>({
  formName: '',
  formCode: '',
  tableId: undefined
})

// 数据库表列表
const dbTables = ref<DbTable[]>([])

// 可用字段列表
const availableFields = ref<any[]>([])

// 已绑定的字段ID集合
const boundFieldIds = ref<Set<number>>(new Set())

// 初始化字段扩展属性
const initFieldExtProps = (field: any) => {
  field._widgetType = field._widgetType || 'input'
  field._required = field._required !== undefined ? field._required : false
  field._span = field._span || 12
  field._sortOrder = field._sortOrder !== undefined ? field._sortOrder : 0
  return field
}

// 返回列表
const handleGoBack = () => {
  router.push({ name: 'FormManage' })
}

// 加载表单信息
const loadFormInfo = async () => {
  const id = route.query.formId as string
  if (id) {
    formId.value = Number(id)
    try {
      const data = await getFormConfig(formId.value)
      Object.assign(formInfo, data)

      // 如果表单已关联表，自动选中
      if (data.tableId) {
        selectedTableId.value = data.tableId
        await handleTableChange(data.tableId)
      }

      // 加载已有绑定
      const bindings = await getBindingsByFormId(formId.value)
      boundFieldIds.value = new Set(bindings.map(b => b.fieldId))

      // 将已有绑定配置合并到字段
      bindings.forEach(binding => {
        const field = availableFields.value.find(f => f.id === binding.fieldId)
        if (field) {
          Object.assign(field, {
            _widgetType: binding.widgetType,
            _required: binding.required,
            _span: binding.span,
            _sortOrder: binding.sortOrder
          })
        }
      })
    } catch (error) {
      ElMessage.error('加载表单信息失败')
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
      _widgetType: getDefaultWidgetType(field.fieldType),
      _required: false,
      _span: 12,
      _sortOrder: index
    }))
  } catch (error) {
    ElMessage.error('获取字段列表失败')
    availableFields.value = []
  }
}

// 根据字段类型获取默认控件类型
const getDefaultWidgetType = (fieldType: string): string => {
  const type = fieldType.toUpperCase()
  if (type.includes('VARCHAR') || type.includes('TEXT')) {
    return type.includes('TEXT') ? 'textarea' : 'input'
  }
  if (type.includes('INT') || type.includes('DECIMAL') || type.includes('DOUBLE') || type.includes('FLOAT')) {
    return 'number'
  }
  if (type.includes('DATE')) {
    return type.includes('DATETIME') || type.includes('TIMESTAMP') ? 'datetime' : 'date'
  }
  if (type.includes('TINYINT') || type.includes('BIT')) {
    return 'switch'
  }
  return 'input'
}

// 控件类型变化时的处理
const handleWidgetTypeChange = (row: any) => {
  // 根据控件类型设置默认值
  if (row._widgetType === 'switch') {
    row._span = row._span || 12
  } else if (row._widgetType === 'textarea') {
    row._span = row._span || 24
  }
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
  if (!formId.value) {
    ElMessage.error('表单ID不存在')
    return
  }

  const selectedRows = bindingTableRef.value?.getSelectionRows() || []

  if (selectedRows.length === 0) {
    ElMessage.warning('请至少选择一个字段')
    return
  }

  saving.value = true
  try {
    const bindings: FormFieldBinding[] = selectedRows.map((row, index) => ({
      formId: formId.value!,
      fieldId: row.id,
      widgetType: row._widgetType || 'input',
      span: row._span || 12,
      required: row._required || false,
      visible: true,
      readonly: false,
      disabled: false,
      sortOrder: row._sortOrder !== undefined ? row._sortOrder : index
    }))

    await batchSaveBindings(formId.value, bindings)
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
  await loadFormInfo()
})
</script>

<style lang="scss" scoped>
.form-field-binding {
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
