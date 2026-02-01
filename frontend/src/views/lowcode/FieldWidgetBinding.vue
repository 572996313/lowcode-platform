<template>
  <div class="field-widget-binding">
    <div class="page-header">
      <h2>字段-控件绑定配置</h2>
    </div>

    <div class="content-container">
      <!-- 左侧树形结构 -->
      <div class="table-tree-panel">
        <div class="panel-title">数据表列表</div>
        <el-tree
          :data="tableTreeData"
          :props="{ children: 'children', label: 'label' }"
          node-key="id"
          :highlight-current="true"
          :expand-on-click-node="false"
          @node-click="handleTableNodeClick"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <el-icon><Folder /></el-icon>
              <span class="node-label">{{ node.label }}</span>
              <el-tag v-if="data.fieldCount" size="small" type="info">
                {{ data.fieldCount }} 字段
              </el-tag>
            </div>
          </template>
        </el-tree>
      </div>

      <!-- 右侧字段列表 -->
      <div class="field-list-panel">
        <el-empty v-if="!selectedTableId" description="请从左侧选择数据表" />

        <template v-else>
          <!-- 操作按钮区 -->
          <div class="action-bar">
            <el-button type="primary" :loading="applyLoading" @click="handleApplyDefaults">
              <el-icon><MagicStick /></el-icon>应用默认映射
            </el-button>
            <el-button type="success" :loading="saveLoading" @click="handleBatchSave">
              <el-icon><Check /></el-icon>全部保存
            </el-button>
          </div>

          <el-empty v-if="loading" description="加载中..." />
          <el-empty v-else-if="fieldList.length === 0" description="暂无字段，请先同步表字段" />
          <div v-else class="field-list-content">
            <!-- 表头 -->
            <div class="field-list-header">
              <div class="header-field">字段信息</div>
              <div class="header-config">控件配置</div>
            </div>

            <!-- 字段列表 -->
            <div class="field-list-body">
              <div
                v-for="item in fieldList"
                :key="item.field.id"
                class="config-item"
              >
                <div class="field-info">
                  <div class="field-name">{{ item.field.fieldName }}</div>
                  <div class="field-meta">
                    <el-tag size="small" type="info">{{ item.field.fieldType }}</el-tag>
                    <span class="field-label">{{ item.field.fieldLabel || item.field.fieldComment || '-' }}</span>
                  </div>
                </div>

                <div class="widget-config">
                  <el-select
                    v-model="item.config.widgetType"
                    placeholder="选择控件类型"
                    style="width: 140px"
                    @change="handleWidgetTypeChange(item)"
                  >
                    <el-option label="单行文本 (input)" value="input" />
                    <el-option label="多行文本 (textarea)" value="textarea" />
                    <el-option label="下拉选择 (select)" value="select" />
                    <el-option label="单选框 (radio)" value="radio" />
                    <el-option label="复选框 (checkbox)" value="checkbox" />
                    <el-option label="开关 (switch)" value="switch" />
                    <el-option label="日期 (date)" value="date" />
                    <el-option label="日期时间 (datetime)" value="datetime" />
                    <el-option label="时间 (time)" value="time" />
                    <el-option label="数字 (number)" value="number" />
                    <el-option label="文件上传 (upload)" value="upload" />
                    <el-option label="级联选择 (cascader)" value="cascader" />
                  </el-select>

                  <el-input
                    v-model="parsedConfig(item.config).label"
                    placeholder="显示标签"
                    style="width: 140px; margin-left: 8px"
                    @input="updateWidgetConfig(item, 'label', $event)"
                  />

                  <el-input
                    v-model="parsedConfig(item.config).placeholder"
                    placeholder="占位提示"
                    style="width: 160px; margin-left: 8px"
                    @input="updateWidgetConfig(item, 'placeholder', $event)"
                  />

                  <el-input-number
                    v-model="parsedConfig(item.config).span"
                    :min="1"
                    :max="24"
                    placeholder="列宽"
                    style="width: 100px; margin-left: 8px"
                    @change="updateWidgetConfig(item, 'span', $event)"
                  />

                  <el-checkbox
                    v-model="parsedConfig(item.config).required"
                    style="margin-left: 8px"
                    @change="updateWidgetConfig(item, 'required', $event)"
                  >
                    必填
                  </el-checkbox>

                  <!-- 数据来源配置（仅对 select/radio/checkbox/cascader 显示） -->
                  <div v-if="needsDataSource(item.config.widgetType)" class="data-source-config">
                    <el-select
                      v-model="getDataSource(item).type"
                      placeholder="数据来源"
                      style="width: 100px"
                      @change="handleDataSourceTypeChange(item)"
                    >
                      <el-option label="静态数据" value="static" />
                      <el-option label="数据字典" value="dict" />
                      <el-option label="API接口" value="api" />
                    </el-select>

                    <!-- 静态数据编辑器 -->
                    <div v-if="getDataSource(item).type === 'static'" class="static-editor">
                      <el-button size="small" @click="addStaticOption(item)">+ 添加选项</el-button>
                      <div v-for="(opt, idx) in getStaticOptions(item)" :key="idx" class="option-row">
                        <el-input v-model="opt.label" placeholder="显示文本" size="small" style="width: 120px" />
                        <el-input v-model="opt.value" placeholder="值" size="small" style="width: 100px" />
                        <el-button size="small" type="danger" link @click="removeStaticOption(item, idx)">删除</el-button>
                      </div>
                    </div>

                    <!-- 字典编码输入 -->
                    <div v-if="getDataSource(item).type === 'dict'" class="dict-input">
                      <el-tree-select
                        v-model="getDataSource(item).dict!.code"
                        :data="dictCategoryTree"
                        :props="{ label: 'categoryName', value: 'categoryCode', children: 'children' }"
                        placeholder="请选择字典分类"
                        filterable
                        check-strictly
                        style="width: 200px"
                      />
                    </div>

                    <!-- API 配置 -->
                    <div v-if="getDataSource(item).type === 'api'" class="api-config">
                      <el-input v-model="getDataSource(item).api!.url" placeholder="API地址" style="width: 200px" />
                      <el-select v-model="getDataSource(item).api!.method" style="width: 80px">
                        <el-option label="GET" value="GET" />
                        <el-option label="POST" value="POST" />
                      </el-select>
                      <el-input v-model="getDataSource(item).api!.labelField" placeholder="标签字段" style="width: 100px" />
                      <el-input v-model="getDataSource(item).api!.valueField" placeholder="值字段" style="width: 100px" />
                    </div>
                  </div>

                  <el-button
                    type="danger"
                    size="small"
                    link
                    style="margin-left: 8px"
                    @click="handleResetConfig(item)"
                  >
                    重置
                  </el-button>
                </div>

                <!-- 可用模板区域 -->
                <div v-if="item.templates.length > 0" class="templates-area">
                  <div class="templates-header">
                    <span class="templates-title">可用模板 ({{ item.templates.length }})</span>
                    <el-button
                      type="primary"
                      size="small"
                      @click="handleCreateTemplate(item)"
                    >
                      <el-icon><Plus /></el-icon>添加模板
                    </el-button>
                  </div>
                  <div class="templates-list">
                    <div
                      v-for="template in item.templates"
                      :key="template.id"
                      class="template-item"
                    >
                      <div class="template-info">
                        <el-tag size="small" type="success">{{ template.widgetType }}</el-tag>
                        <span class="template-name">{{ template.templateName }}</span>
                        <span v-if="template.description" class="template-desc">{{ template.description }}</span>
                      </div>
                      <div class="template-actions">
                        <el-button
                          type="primary"
                          size="small"
                          link
                          @click="handleApplyTemplate(template)"
                        >
                          设为主要配置
                        </el-button>
                        <el-button
                          type="primary"
                          size="small"
                          link
                          @click="handleEditTemplate(template)"
                        >
                          编辑
                        </el-button>
                        <el-button
                          type="danger"
                          size="small"
                          link
                          @click="handleDeleteTemplate(template)"
                        >
                          删除
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="templates-area-empty">
                  <el-button
                    type="primary"
                    size="small"
                    link
                    @click="handleCreateTemplate(item)"
                  >
                    <el-icon><Plus /></el-icon>添加模板
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- 创建/编辑模板对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="templateFormMode === 'create' ? '创建模板' : '编辑模板'"
      width="600px"
    >
      <el-form :model="templateForm" label-width="100px">
        <el-form-item label="模板名称" required>
          <el-input v-model="templateForm.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码">
          <el-input v-model="templateForm.templateCode" placeholder="可选，用于程序化引用" />
        </el-form-item>
        <el-form-item label="控件类型" required>
          <el-select v-model="templateForm.widgetType" placeholder="请选择控件类型" style="width: 100%">
            <el-option label="单行文本 (input)" value="input" />
            <el-option label="多行文本 (textarea)" value="textarea" />
            <el-option label="下拉选择 (select)" value="select" />
            <el-option label="单选框 (radio)" value="radio" />
            <el-option label="复选框 (checkbox)" value="checkbox" />
            <el-option label="开关 (switch)" value="switch" />
            <el-option label="日期 (date)" value="date" />
            <el-option label="日期时间 (datetime)" value="datetime" />
            <el-option label="时间 (time)" value="time" />
            <el-option label="数字 (number)" value="number" />
            <el-option label="文件上传 (upload)" value="upload" />
            <el-option label="级联选择 (cascader)" value="cascader" />
          </el-select>
        </el-form-item>
        <el-form-item label="控件配置">
          <el-input
            v-model="templateForm.widgetConfig"
            type="textarea"
            :rows="6"
            placeholder='JSON 格式，例如：{"label": "状态", "placeholder": "请选择"}'
          />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入模板描述"
          />
        </el-form-item>
        <el-form-item label="排序序号">
          <el-input-number v-model="templateForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="templateForm.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTemplate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Folder } from '@element-plus/icons-vue'
import {
  getTableList,
  getTableFields,
  type DbTable,
  type DbTableField
} from '@/api/db-table'
import {
  getConfigsByTableId,
  batchSaveConfigs,
  applyDefaultsToTable,
  getTemplatesByFieldId,
  createTemplate,
  updateTemplate,
  deleteTemplate,
  applyTemplateAsPrimary,
  type DbFieldWidgetConfig,
  type DbFieldWidgetTemplate,
  type DataSource,
  type DataSourceType
} from '@/api/db-field-widget'
import { getCategoryTree, type DictCategory } from '@/api/dict'

// 控件配置接口
interface WidgetConfig {
  label?: string
  placeholder?: string
  defaultValue?: string
  required?: boolean
  span?: number
  disabled?: boolean
  readonly?: boolean
  visible?: boolean
  dataSource?: DataSource
}

// 树节点数据类型
interface TableTreeNode {
  id: number
  label: string
  type: 'table'
  tableId: number
  fieldCount: number
  children: TableTreeNode[]
}

// 字段配置项
interface FieldConfigItem {
  field: DbTableField
  config: DbFieldWidgetConfig
  templates: DbFieldWidgetTemplate[]
  originalConfig?: string
}

// 数据
const tableList = ref<DbTable[]>([])
const selectedTableId = ref<number | null>(null)
const fieldList = ref<FieldConfigItem[]>([])
const loading = ref(false)
const applyLoading = ref(false)
const saveLoading = ref(false)

// 模板对话框
const templateDialogVisible = ref(false)
const templateForm = ref<DbFieldWidgetTemplate>({
  tableId: 0,
  fieldId: 0,
  templateName: '',
  templateCode: '',
  widgetType: 'input',
  widgetConfig: '',
  description: '',
  enabled: 1,
  sortOrder: 0
})
const templateFormMode = ref<'create' | 'edit'>('create')
const editingTemplateId = ref<number | null>(null)

// 字典分类树
const dictCategoryTree = ref<DictCategory[]>([])

// 构建树形数据
const tableTreeData = computed<TableTreeNode[]>(() => {
  return tableList.value.map(table => ({
    id: table.id!,
    label: `${table.tableName} - ${table.tableComment || '无注释'}`,
    type: 'table' as const,
    tableId: table.id!,
    fieldCount: selectedTableId.value === table.id ? fieldList.value.length : 0,
    children: []
  }))
})

// 解析控件配置
const parsedConfig = (config: DbFieldWidgetConfig): WidgetConfig => {
  try {
    return JSON.parse(config.widgetConfig || '{}')
  } catch {
    return {}
  }
}

// 判断控件是否需要数据来源
const needsDataSource = (widgetType: string): boolean => {
  return ['select', 'radio', 'checkbox', 'cascader'].includes(widgetType)
}

// 获取数据来源配置
const getDataSource = (item: FieldConfigItem): DataSource => {
  const config = parsedConfig(item.config)
  if (!config.dataSource) {
    config.dataSource = {
      type: 'static',
      static: { options: [] },
      dict: { code: '' },
      api: { url: '', method: 'GET', labelField: '', valueField: '' }
    }
  }
  return config.dataSource
}

// 获取静态选项列表
const getStaticOptions = (item: FieldConfigItem) => {
  const ds = getDataSource(item)
  return ds.static?.options || []
}

// 控件类型变化处理
const handleWidgetTypeChange = (item: FieldConfigItem) => {
  const config = parsedConfig(item.config)
  if (!needsDataSource(item.config.widgetType)) {
    delete config.dataSource
  } else {
    if (!config.dataSource) {
      config.dataSource = {
        type: 'static',
        static: { options: [] },
        dict: { code: '' },
        api: { url: '', method: 'GET', labelField: '', valueField: '' }
      }
    }
  }
  item.config.widgetConfig = JSON.stringify(config)
}

// 数据来源类型变化处理
const handleDataSourceTypeChange = (item: FieldConfigItem) => {
  // 数据来源类型变化时，只需更新配置
  updateWidgetConfig(item, 'dataSource', getDataSource(item))
}

// 添加静态选项
const addStaticOption = (item: FieldConfigItem) => {
  const ds = getDataSource(item)
  if (!ds.static) ds.static = { options: [] }
  ds.static.options.push({ label: '', value: '' })
  updateWidgetConfig(item, 'dataSource', ds)
}

// 删除静态选项
const removeStaticOption = (item: FieldConfigItem, index: number) => {
  const ds = getDataSource(item)
  ds.static?.options.splice(index, 1)
  updateWidgetConfig(item, 'dataSource', ds)
}

// 加载表列表
const loadTableList = async () => {
  loading.value = true
  try {
    const result = await getTableList({ current: 1, size: 1000 })
    tableList.value = result.records
  } catch (error) {
    ElMessage.error('加载表列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 表节点点击处理
const handleTableNodeClick = (data: TableTreeNode) => {
  if (data.type === 'table') {
    selectedTableId.value = data.tableId
    handleTableChange()
  }
}

// 表切换
const handleTableChange = async () => {
  if (!selectedTableId.value) {
    fieldList.value = []
    return
  }

  loading.value = true
  try {
    // 加载字段列表
    const fields = await getTableFields(selectedTableId.value)
    // 加载绑定配置
    const configs = await getConfigsByTableId(selectedTableId.value)

    // 创建配置映射
    const configMap = new Map<number, DbFieldWidgetConfig>()
    configs.forEach(c => {
      if (c.fieldId) configMap.set(c.fieldId, c)
    })

    // 组合数据
    fieldList.value = await Promise.all(
      fields.map(async field => {
        const existingConfig = configMap.get(field.id!)
        const config: DbFieldWidgetConfig = existingConfig || {
          fieldId: field.id,
          widgetType: 'input',
          widgetConfig: JSON.stringify({
            label: field.fieldLabel || field.fieldComment || field.fieldName,
            placeholder: `请输入${field.fieldLabel || field.fieldComment || field.fieldName}`,
            required: field.isNullable === 0,
            span: 12
          })
        }

        // 加载字段的所有模板
        const templates = await getTemplatesByFieldId(field.id!)

        return {
          field,
          config,
          templates,
          originalConfig: existingConfig ? JSON.stringify(existingConfig) : ''
        }
      })
    )
  } catch (error) {
    ElMessage.error('加载字段配置失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 更新控件配置
const updateWidgetConfig = (item: FieldConfigItem, key: string, value: any) => {
  const config = parsedConfig(item.config)
  config[key] = value
  item.config.widgetConfig = JSON.stringify(config)
}

// 应用默认映射
const handleApplyDefaults = () => {
  ElMessageBox.confirm(
    '确定要应用默认映射吗？这将覆盖当前所有字段配置。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    applyLoading.value = true
    try {
      await applyDefaultsToTable(selectedTableId.value!)
      ElMessage.success('应用成功')
      handleTableChange()
    } catch (error) {
      ElMessage.error('应用失败')
      console.error(error)
    } finally {
      applyLoading.value = false
    }
  }).catch(() => {})
}

// 重置配置
const handleResetConfig = (item: FieldConfigItem) => {
  if (item.originalConfig) {
    try {
      const original = JSON.parse(item.originalConfig)
      item.config.widgetConfig = JSON.stringify({
        ...JSON.parse(item.config.widgetConfig || '{}'),
        ...original
      })
      ElMessage.success('重置成功')
    } catch {
      ElMessage.error('重置失败')
    }
  }
}

// 批量保存
const handleBatchSave = async () => {
  const configs = fieldList.value.map(item => ({
    ...item.config,
    tableId: selectedTableId.value!
  }))

  saveLoading.value = true
  try {
    await batchSaveConfigs(selectedTableId.value!, configs)
    ElMessage.success('保存成功')
    handleTableChange()
  } catch (error) {
    ElMessage.error('保存失败')
    console.error(error)
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  loadTableList()
  loadDictCategories()
})

// 加载字典分类
const loadDictCategories = async () => {
  try {
    dictCategoryTree.value = await getCategoryTree()
  } catch (error) {
    console.error('加载字典分类失败:', error)
  }
}

// ========== 模板管理 ==========

// 打开创建模板对话框
const handleCreateTemplate = (item: FieldConfigItem) => {
  templateFormMode.value = 'create'
  editingTemplateId.value = null
  templateForm.value = {
    tableId: selectedTableId.value!,
    fieldId: item.field.id!,
    templateName: '',
    templateCode: '',
    widgetType: item.config.widgetType,
    widgetConfig: item.config.widgetConfig,
    description: '',
    enabled: 1,
    sortOrder: 0
  }
  templateDialogVisible.value = true
}

// 打开编辑模板对话框
const handleEditTemplate = (template: DbFieldWidgetTemplate) => {
  templateFormMode.value = 'edit'
  editingTemplateId.value = template.id!
  templateForm.value = { ...template }
  templateDialogVisible.value = true
}

// 保存模板
const handleSaveTemplate = async () => {
  if (!templateForm.value.templateName) {
    ElMessage.error('请输入模板名称')
    return
  }
  if (!templateForm.value.widgetType) {
    ElMessage.error('请选择控件类型')
    return
  }

  try {
    if (templateFormMode.value === 'create') {
      await createTemplate(templateForm.value)
      ElMessage.success('创建成功')
    } else {
      await updateTemplate(editingTemplateId.value!, templateForm.value)
      ElMessage.success('更新成功')
    }
    templateDialogVisible.value = false
    // 刷新模板列表
    handleTableChange()
  } catch (error) {
    ElMessage.error(templateFormMode.value === 'create' ? '创建失败' : '更新失败')
    console.error(error)
  }
}

// 删除模板
const handleDeleteTemplate = (template: DbFieldWidgetTemplate) => {
  ElMessageBox.confirm(
    `确定要删除模板"${template.templateName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteTemplate(template.id!)
      ElMessage.success('删除成功')
      // 刷新模板列表
      handleTableChange()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {})
}

// 应用模板为主配置
const handleApplyTemplate = async (template: DbFieldWidgetTemplate) => {
  try {
    await applyTemplateAsPrimary(template.id!)
    ElMessage.success('应用成功')
    // 刷新配置
    handleTableChange()
  } catch (error) {
    ElMessage.error('应用失败')
    console.error(error)
  }
}
</script>

<style lang="scss" scoped>
.field-widget-binding {
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
    display: flex;
    gap: 16px;
    padding: 24px;
    overflow: hidden;

    .table-tree-panel {
      width: 280px;
      background: #fff;
      border-radius: 4px;
      flex-shrink: 0;
      display: flex;
      flex-direction: column;

      .panel-title {
        padding: 16px;
        font-weight: 600;
        border-bottom: 1px solid #e6e6e6;
      }

      .tree-node {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        padding-right: 8px;

        .node-label {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .field-list-panel {
      flex: 1;
      background: #fff;
      border-radius: 4px;
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .action-bar {
        padding: 16px;
        border-bottom: 1px solid #e6e6e6;
        display: flex;
        gap: 12px;
      }

      .field-list-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;

        .field-list-header {
          display: grid;
          grid-template-columns: 280px 1fr;
          gap: 20px;
          padding: 12px 16px;
          background: #f5f7fa;
          border-bottom: 2px solid #e6e6e6;
          font-weight: 600;
          color: #303133;
        }

        .field-list-body {
          flex: 1;
          overflow: auto;

          .config-item {
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 20px;
            padding: 16px;
            border-bottom: 1px solid #e6e6e6;

            &:hover {
              background: #fafafa;
            }

            .field-info {
              padding-right: 20px;
              border-right: 1px solid #e6e6e6;

              .field-name {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
                margin-bottom: 6px;
              }

              .field-meta {
                display: flex;
                align-items: center;
                gap: 8px;

                .field-label {
                  font-size: 13px;
                  color: #909399;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }
            }

            .widget-config {
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              gap: 4px;
            }

            .data-source-config {
              display: flex;
              align-items: center;
              gap: 8px;
              flex-wrap: wrap;
              padding: 8px;
              background: #f8f9fa;
              border-radius: 4px;
              margin-left: 8px;

              .static-editor {
                display: flex;
                flex-direction: column;
                gap: 4px;

                .option-row {
                  display: flex;
                  gap: 4px;
                  align-items: center;
                }
              }

              .dict-input,
              .api-config {
                display: flex;
                align-items: center;
                gap: 4px;
              }
            }

            .templates-area {
              grid-column: 1 / -1;
              margin-top: 12px;
              padding: 12px;
              background-color: #f8f9fa;
              border-radius: 4px;

              .templates-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 8px;

                .templates-title {
                  font-size: 13px;
                  font-weight: 500;
                  color: #606266;
                }
              }

              .templates-list {
                display: flex;
                flex-direction: column;
                gap: 8px;

                .template-item {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  padding: 8px 12px;
                  background-color: #fff;
                  border: 1px solid #e4e7ed;
                  border-radius: 4px;
                  transition: all 0.2s;

                  &:hover {
                    border-color: #409eff;
                    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
                  }

                  .template-info {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    flex: 1;

                    .template-name {
                      font-size: 14px;
                      font-weight: 500;
                      color: #303133;
                    }

                    .template-desc {
                      font-size: 12px;
                      color: #909399;
                    }
                  }

                  .template-actions {
                    display: flex;
                    gap: 4px;
                  }
                }
              }
            }

            .templates-area-empty {
              grid-column: 1 / -1;
              margin-top: 8px;
              padding: 8px;
              text-align: center;
            }
          }
        }
      }
    }
  }
}
</style>
