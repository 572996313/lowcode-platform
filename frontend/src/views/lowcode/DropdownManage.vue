<template>
  <div class="dropdown-manage">
    <div class="page-header">
      <h2>下拉数据管理</h2>
    </div>

    <div class="content-container">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.configName"
          placeholder="请输入配置名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.configCode"
          placeholder="请输入配置编码"
          clearable
          style="width: 200px; margin-left: 12px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.dropdownType"
          placeholder="下拉类型"
          clearable
          style="width: 120px; margin-left: 12px"
        >
          <el-option label="普通" value="flat" />
          <el-option label="树形" value="tree" />
          <el-option label="级联" value="cascader" />
        </el-select>
        <el-select
          v-model="searchForm.enabled"
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
          <el-icon><Plus /></el-icon>新增
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
        <el-table-column prop="configName" label="配置名称" min-width="150" />
        <el-table-column prop="configCode" label="配置编码" min-width="150" />
        <el-table-column label="下拉类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.dropdownType === 'flat'" type="primary">普通</el-tag>
            <el-tag v-else-if="row.dropdownType === 'tree'" type="success">树形</el-tag>
            <el-tag v-else-if="row.dropdownType === 'cascader'" type="warning">级联</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-width="120px"
          >
            <el-form-item label="配置名称" prop="configName">
              <el-input v-model="formData.configName" placeholder="请输入配置名称" />
            </el-form-item>
            <el-form-item label="配置编码" prop="configCode">
              <el-input v-model="formData.configCode" placeholder="请输入配置编码（唯一）" />
            </el-form-item>
            <el-form-item label="下拉类型" prop="dropdownType">
              <el-select v-model="formData.dropdownType" placeholder="请选择下拉类型">
                <el-option label="普通" value="flat" />
                <el-option label="树形" value="tree" />
                <el-option label="级联" value="cascader" />
              </el-select>
            </el-form-item>
            <el-form-item label="描述">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入描述"
              />
            </el-form-item>
            <el-form-item label="启用状态">
              <el-switch v-model="formData.enabled" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="formData.sortOrder" :min="0" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- SQL 配置 -->
        <el-tab-pane label="SQL 配置" name="sql">
          <el-form ref="sqlFormRef" :model="formData" label-width="120px">
            <el-form-item label="SQL 模板" required>
              <MonacoEditor
                v-model="formData.sqlTemplate"
                language="sql"
                height="200px"
                theme="vs"
                :options="{ minimap: { enabled: false } }"
              />
              <div class="form-tip">使用 #{paramName} 作为参数占位符</div>
            </el-form-item>
            <el-form-item label="Label 字段" required>
              <el-input v-model="formData.labelField" placeholder="请输入显示标签字段名" />
            </el-form-item>
            <el-form-item label="Value 字段" required>
              <el-input v-model="formData.valueField" placeholder="请输入值字段名" />
            </el-form-item>
            <el-form-item
              v-if="formData.dropdownType === 'tree' || formData.dropdownType === 'cascader'"
              label="Parent 字段"
              required
            >
              <el-input v-model="formData.parentField" placeholder="请输入父级字段名" />
            </el-form-item>
            <el-form-item label="额外字段">
              <el-input
                v-model="formData.extraFields"
                placeholder="请输入额外字段，逗号分隔"
              />
              <div class="form-tip">如：dept_name,email</div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 参数配置 -->
        <el-tab-pane label="参数配置" name="params">
          <div class="params-container">
            <el-button type="primary" size="small" @click="handleAddParam">
              <el-icon><Plus /></el-icon>添加参数
            </el-button>
            <el-table :data="paramList" border style="width: 100%; margin-top: 12px">
              <el-table-column label="参数名称" width="180">
                <template #default="{ row, $index }">
                  <el-input v-model="row.name" placeholder="参数名" />
                </template>
              </el-table-column>
              <el-table-column label="参数类型" width="120">
                <template #default="{ row }">
                  <el-select v-model="row.type" placeholder="类型">
                    <el-option label="String" value="String" />
                    <el-option label="Long" value="Long" />
                    <el-option label="Integer" value="Integer" />
                    <el-option label="Boolean" value="Boolean" />
                    <el-option label="Double" value="Double" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="必填" width="80" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.required" />
                </template>
              </el-table-column>
              <el-table-column label="默认值" width="150">
                <template #default="{ row }">
                  <el-input v-model="row.defaultValue" placeholder="默认值" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="200">
                <template #default="{ row }">
                  <el-input v-model="row.description" placeholder="参数说明" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" link @click="handleRemoveParam($index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 测试 -->
        <el-tab-pane label="测试" name="test">
          <div class="test-container">
            <el-form label-width="100px">
              <el-form-item
                v-for="param in paramList"
                :key="param.name"
                :label="`${param.name} (${param.type})`"
              >
                <el-input
                  v-if="param.type === 'String'"
                  v-model="testParams[param.name]"
                  :placeholder="`默认值: ${param.defaultValue || '无'}`"
                />
                <el-input-number
                  v-else-if="param.type === 'Long' || param.type === 'Integer'"
                  v-model="testParams[param.name]"
                  :placeholder="`默认值: ${param.defaultValue || '无'}`"
                />
                <el-switch v-else-if="param.type === 'Boolean'" v-model="testParams[param.name]" />
                <el-input-number v-else v-model="testParams[param.name]" />
              </el-form-item>
            </el-form>
            <el-button type="primary" @click="handleTest" :loading="testing">
              <el-icon><VideoPlay /></el-icon>测试 SQL
            </el-button>
            <div v-if="testResult" class="test-result">
              <h4>测试结果 ({{ testResult.length }} 条)</h4>
              <el-table :data="testResult" border max-height="300">
                <el-table-column
                  v-for="col in getTestColumns()"
                  :key="col"
                  :prop="col"
                  :label="col"
                  min-width="120"
                />
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDropdownList,
  getDropdownConfig,
  createDropdown,
  updateDropdown,
  deleteDropdown,
  testSql,
  type DropdownConfig,
  type ParamConfig
} from '@/api/dropdown'
import MonacoEditor from '@/components/MonacoEditor.vue'

// ============ 搜索 ============
const searchForm = reactive({
  configName: '',
  configCode: '',
  dropdownType: '',
  enabled: undefined as boolean | undefined
})

// ============ 分页 ============
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// ============ 表格数据 ============
const tableData = ref<DropdownConfig[]>([])
const loading = ref(false)

// ============ 对话框 ============
const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑下拉配置' : '新增下拉配置')
const activeTab = ref('basic')

// ============ 表单数据 ============
const formRef = ref()
const sqlFormRef = ref()
const formData = reactive<DropdownConfig>({
  configName: '',
  configCode: '',
  dropdownType: 'flat',
  sqlTemplate: '',
  labelField: '',
  valueField: '',
  parentField: '',
  paramConfig: '',
  extraFields: '',
  description: '',
  enabled: true,
  sortOrder: 0
})

// ============ 表单验证规则 ============
const rules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
  dropdownType: [{ required: true, message: '请选择下拉类型', trigger: 'change' }]
}

// ============ 参数配置 ============
const paramList = ref<ParamConfig[]>([])

// ============ 测试 ============
const testParams = ref<Record<string, any>>({})
const testResult = ref<Record<string, any>[]>([])
const testing = ref(false)

// ============ 提交状态 ============
const submitting = ref(false)

// ============ 加载数据 ============
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const result = await getDropdownList(params)
    tableData.value = result.records
    pagination.total = result.total
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// ============ 搜索 ============
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.configName = ''
  searchForm.configCode = ''
  searchForm.dropdownType = ''
  searchForm.enabled = undefined
  pagination.current = 1
  loadData()
}

// ============ 新增 ============
const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

// ============ 编辑 ============
const handleEdit = async (row: DropdownConfig) => {
  try {
    const config = await getDropdownConfig(row.id!)
    resetForm()
    Object.assign(formData, config)
    // 解析参数配置
    paramList.value = parseParamConfig(config.paramConfig)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载配置失败')
  }
}

// ============ 删除 ============
const handleDelete = (row: DropdownConfig) => {
  ElMessageBox.confirm('确定要删除该配置吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDropdown(row.id!)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// ============ 重置表单 ============
const resetForm = () => {
  formData.id = undefined
  formData.configName = ''
  formData.configCode = ''
  formData.dropdownType = 'flat'
  formData.sqlTemplate = ''
  formData.labelField = ''
  formData.valueField = ''
  formData.parentField = ''
  formData.paramConfig = ''
  formData.extraFields = ''
  formData.description = ''
  formData.enabled = true
  formData.sortOrder = 0
  paramList.value = []
  testParams.value = {}
  testResult.value = []
  activeTab.value = 'basic'
}

// ============ 参数操作 ============
const handleAddParam = () => {
  paramList.value.push({
    name: '',
    type: 'String',
    required: false,
    defaultValue: '',
    description: ''
  })
}

const handleRemoveParam = (index: number) => {
  paramList.value.splice(index, 1)
}

const parseParamConfig = (jsonStr?: string): ParamConfig[] => {
  if (!jsonStr) return []
  try {
    const data = JSON.parse(jsonStr)
    return data.params || []
  } catch {
    return []
  }
}

// ============ 测试 ============
const handleTest = async () => {
  if (!formData.sqlTemplate) {
    ElMessage.warning('请先配置 SQL 模板')
    return
  }

  testing.value = true
  try {
    // 收集测试参数
    const params: Record<string, any> = {}
    paramList.value.forEach(param => {
      let value = testParams.value[param.name]
      if (value === undefined || value === '') {
        value = param.defaultValue
      }
      if (value !== undefined && value !== '') {
        params[param.name] = value
      }
    })

    const result = await testSql({ ...formData } as DropdownConfig, params)
    testResult.value = result
    ElMessage.success(`测试成功，返回 ${result.length} 条数据`)
  } catch (error: any) {
    ElMessage.error('测试失败：' + (error.message || '未知错误'))
    testResult.value = []
  } finally {
    testing.value = false
  }
}

const getTestColumns = () => {
  if (!testResult.value.length) return []
  return Object.keys(testResult.value[0])
}

// ============ 提交 ============
const handleSubmit = async () => {
  // 验证基本信息
  try {
    await formRef.value?.validate()
  } catch {
    ElMessage.warning('请填写完整的基本信息')
    return
  }

  // 验证 SQL 配置
  if (!formData.sqlTemplate) {
    ElMessage.warning('请配置 SQL 模板')
    activeTab.value = 'sql'
    return
  }
  if (!formData.labelField || !formData.valueField) {
    ElMessage.warning('请配置 Label/Value 字段')
    activeTab.value = 'sql'
    return
  }
  if ((formData.dropdownType === 'tree' || formData.dropdownType === 'cascader') && !formData.parentField) {
    ElMessage.warning('树形/级联下拉必须配置 Parent 字段')
    activeTab.value = 'sql'
    return
  }

  // 序列化参数配置
  formData.paramConfig = JSON.stringify({ params: paramList.value })

  submitting.value = true
  try {
    if (formData.id) {
      await updateDropdown(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      const id = await createDropdown(formData)
      ElMessage.success('创建成功')
      formData.id = id
    }
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error('保存失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// ============ 页面挂载 ============
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.dropdown-manage {
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.params-container {
  padding: 8px;
}

.test-container {
  padding: 8px;

  .test-result {
    margin-top: 20px;
    padding: 16px;
    background-color: #f5f7fa;
    border-radius: 4px;

    h4 {
      margin: 0 0 12px 0;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

:deep(.el-tabs--border-card) {
  .el-tabs__content {
    padding: 20px;
  }
}
</style>
