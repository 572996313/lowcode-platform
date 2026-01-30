<template>
  <div class="table-designer" v-loading="loading">
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <span class="title">表格设计器{{ tableId ? ' - 编辑模式' : ' - 新建模式' }}</span>
      </div>
      <div class="header-right">
        <el-button @click="handlePreview">
          <el-icon><View /></el-icon>预览
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          <el-icon><Check /></el-icon>保存
        </el-button>
      </div>
    </div>

    <div class="designer-body">
      <!-- 左侧配置 -->
      <div class="config-panel">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基础配置" name="basic">
            <el-form label-width="80px" size="small">
              <el-form-item label="表格名称">
                <el-input v-model="tableConfig.tableName" placeholder="请输入表格名称" />
              </el-form-item>
              <el-form-item label="表格编码">
                <el-input v-model="tableConfig.tableCode" placeholder="请输入表格编码" />
              </el-form-item>
              <el-form-item label="数据接口">
                <el-input v-model="tableConfig.apiUrl" placeholder="请输入API地址" />
              </el-form-item>
              <el-form-item label="请求方式">
                <el-select v-model="tableConfig.apiMethod" style="width: 100%">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                </el-select>
              </el-form-item>
              <el-form-item label="是否分页">
                <el-switch v-model="tableConfig.pagination" />
              </el-form-item>
              <el-form-item label="每页条数" v-if="tableConfig.pagination">
                <el-input-number v-model="tableConfig.pageSize" :min="5" :max="100" />
              </el-form-item>
              <el-form-item label="显示序号">
                <el-switch v-model="tableConfig.showIndex" />
              </el-form-item>
              <el-form-item label="显示多选">
                <el-switch v-model="tableConfig.selection" />
              </el-form-item>
              <el-form-item label="显示边框">
                <el-switch v-model="tableConfig.border" />
              </el-form-item>
              <el-form-item label="斑马纹">
                <el-switch v-model="tableConfig.stripe" />
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="列配置" name="columns">
            <div class="column-list">
              <div class="column-header">
                <span>列配置</span>
                <el-button type="primary" size="small" @click="addColumn">
                  <el-icon><Plus /></el-icon>添加列
                </el-button>
              </div>
              <div
                v-for="(column, index) in tableConfig.columns"
                :key="column.id"
                class="column-item"
                :class="{ active: selectedColumn?.id === column.id }"
                @click="selectColumn(column)"
              >
                <div class="column-info">
                  <span class="column-label">{{ column.label }}</span>
                  <span class="column-code">{{ column.columnCode }}</span>
                </div>
                <div class="column-actions">
                  <el-icon @click.stop="moveColumnUp(index)" v-if="index > 0"><Top /></el-icon>
                  <el-icon @click.stop="moveColumnDown(index)" v-if="index < tableConfig.columns.length - 1"><Bottom /></el-icon>
                  <el-icon @click.stop="removeColumn(index)"><Delete /></el-icon>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="按钮配置" name="buttons">
            <div class="buttons-config">
              <el-collapse v-model="activeCollapse">
                <el-collapse-item title="工具栏按钮" name="toolbar">
                  <template #title>
                    <div style="display: flex; align-items: center; justify-content: space-between; width: 100%; padding-right: 16px;">
                      <span>工具栏按钮</span>
                      <el-button type="primary" size="small" @click.stop="addToolbarButton">
                        <el-icon><Plus /></el-icon>添加
                      </el-button>
                    </div>
                  </template>
                  <div class="button-list">
                    <div
                      v-for="(button, index) in toolbarButtons"
                      :key="button.id"
                      class="button-item"
                      :class="{ active: selectedButton?.id === button.id }"
                      @click="selectButton(button)"
                    >
                      <div class="button-info">
                        <el-tag :type="button.buttonType || 'default'" size="small">{{ button.buttonName }}</el-tag>
                        <span class="button-code">{{ button.buttonCode }}</span>
                      </div>
                      <div class="button-actions">
                        <el-icon @click.stop="moveButtonUp('toolbar', index)" v-if="index > 0"><Top /></el-icon>
                        <el-icon @click.stop="moveButtonDown('toolbar', index)" v-if="index < toolbarButtons.length - 1"><Bottom /></el-icon>
                        <el-icon @click.stop="removeButton('toolbar', index)" class="danger"><Delete /></el-icon>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
                <el-collapse-item title="操作列按钮" name="row">
                  <template #title>
                    <div style="display: flex; align-items: center; justify-content: space-between; width: 100%; padding-right: 16px;">
                      <span>操作列按钮</span>
                      <el-button type="primary" size="small" @click.stop="addRowButton">
                        <el-icon><Plus /></el-icon>添加
                      </el-button>
                    </div>
                  </template>
                  <div class="button-list">
                    <div
                      v-for="(button, index) in rowButtons"
                      :key="button.id"
                      class="button-item"
                      :class="{ active: selectedButton?.id === button.id }"
                      @click="selectButton(button)"
                    >
                      <div class="button-info">
                        <el-tag :type="button.buttonType || 'default'" size="small">{{ button.buttonName }}</el-tag>
                        <span class="button-code">{{ button.buttonCode }}</span>
                      </div>
                      <div class="button-actions">
                        <el-icon @click.stop="moveButtonUp('row', index)" v-if="index > 0"><Top /></el-icon>
                        <el-icon @click.stop="moveButtonDown('row', index)" v-if="index < rowButtons.length - 1"><Bottom /></el-icon>
                        <el-icon @click.stop="removeButton('row', index)" class="danger"><Delete /></el-icon>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
              <el-divider />
              <template v-if="selectedButton">
                <div class="selected-button-title">
                  <el-tag :type="selectedButton.buttonType || 'default'">{{ selectedButton.buttonName }}</el-tag>
                  <span>按钮配置</span>
                </div>
                <el-form label-width="90px" size="small">
                  <el-form-item label="按钮名称">
                    <el-input v-model="selectedButton.buttonName" />
                  </el-form-item>
                  <el-form-item label="按钮编码">
                    <el-input v-model="selectedButton.buttonCode" />
                  </el-form-item>
                  <el-form-item label="按钮类型">
                    <el-select v-model="selectedButton.buttonType">
                      <el-option label="默认" value="default" />
                      <el-option label="主要" value="primary" />
                      <el-option label="成功" value="success" />
                      <el-option label="警告" value="warning" />
                      <el-option label="危险" value="danger" />
                      <el-option label="信息" value="info" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="按钮尺寸">
                    <el-select v-model="selectedButton.buttonSize">
                      <el-option label="默认" value="default" />
                      <el-option label="大" value="large" />
                      <el-option label="小" value="small" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="图标">
                    <el-input v-model="selectedButton.icon" placeholder="例如: Search, Edit, Delete" />
                  </el-form-item>
                  <el-form-item label="朴素按钮">
                    <el-switch v-model="selectedButton.plain" />
                  </el-form-item>
                  <el-form-item label="圆角按钮">
                    <el-switch v-model="selectedButton.round" />
                  </el-form-item>
                  <el-form-item label="动作类型">
                    <el-select v-model="selectedButton.actionType" @change="handleActionTypeChange">
                      <el-option label="API请求" value="api" />
                      <el-option label="打开弹窗" value="dialog" />
                      <el-option label="打开抽屉" value="drawer" />
                      <el-option label="路由跳转" value="route" />
                      <el-option label="打开链接" value="link" />
                      <el-option label="自定义代码" value="custom" />
                      <el-option label="确认框" value="confirm" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="动作配置" v-if="selectedButton.actionType !== 'custom'">
                    <el-input
                      v-model="selectedButton.actionConfig"
                      type="textarea"
                      :rows="5"
                      placeholder='JSON格式，例如: {"url": "/api/submit", "method": "POST"}'
                    />
                  </el-form-item>
                  <el-form-item label="自定义代码" v-if="selectedButton.actionType === 'custom'">
                    <el-input
                      v-model="selectedButton.actionConfig"
                      type="textarea"
                      :rows="8"
                      placeholder="JavaScript代码，可使用 row, selection 等上下文变量"
                    />
                  </el-form-item>
                  <el-form-item label="确认框配置" v-if="selectedButton.actionType === 'confirm'">
                    <el-input
                      v-model="selectedButton.confirmConfig"
                      type="textarea"
                      :rows="4"
                      placeholder='JSON格式，例如: {"title": "确认", "message": "确定要删除吗？"}'
                    />
                  </el-form-item>
                  <el-form-item label="权限标识">
                    <el-input v-model="selectedButton.perms" placeholder="例如: user:add" />
                  </el-form-item>
                  <el-form-item label="显示条件">
                    <el-input
                      v-model="selectedButton.showCondition"
                      placeholder="JavaScript表达式，例如: row.status === 1"
                    />
                  </el-form-item>
                  <el-form-item label="是否显示">
                    <el-switch v-model="selectedButton.visible" />
                  </el-form-item>
                </el-form>
              </template>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 中间预览区域 -->
      <div class="preview-panel">
        <div class="panel-title">预览效果</div>
        <div class="preview-area">
          <el-table
            :data="mockData"
            :border="tableConfig.border"
            :stripe="tableConfig.stripe"
            style="width: 100%"
          >
            <el-table-column v-if="tableConfig.selection" type="selection" width="55" />
            <el-table-column v-if="tableConfig.showIndex" type="index" label="序号" width="60" />
            <el-table-column
              v-for="column in tableConfig.columns"
              :key="column.id"
              :prop="column.columnCode"
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :sortable="column.sortable"
              :fixed="column.fixed"
              :align="column.align"
            />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default>
                <template v-if="rowButtons.length > 0">
                  <el-button
                    v-for="button in rowButtons"
                    :key="button.id"
                    :type="button.buttonType || 'default'"
                    :size="button.buttonSize || 'small'"
                    :icon="button.icon"
                    :plain="button.plain"
                    link
                  >
                    {{ button.buttonName }}
                  </el-button>
                </template>
                <template v-else>
                  <el-button type="primary" link>编辑</el-button>
                  <el-button type="danger" link>删除</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="tableConfig.pagination"
            :page-size="tableConfig.pageSize"
            :total="100"
            layout="total, sizes, prev, pager, next"
            style="margin-top: 16px; justify-content: flex-end;"
          />
        </div>
      </div>

      <!-- 右侧属性配置 -->
      <div class="property-panel">
        <div class="panel-title">列属性</div>
        <div class="property-content">
          <template v-if="selectedColumn">
            <el-form label-width="80px" size="small">
              <el-form-item label="列标题">
                <el-input v-model="selectedColumn.label" />
              </el-form-item>
              <el-form-item label="字段名">
                <el-input v-model="selectedColumn.columnCode" />
              </el-form-item>
              <el-form-item label="列宽度">
                <el-input-number v-model="selectedColumn.width" :min="0" />
              </el-form-item>
              <el-form-item label="最小宽度">
                <el-input-number v-model="selectedColumn.minWidth" :min="0" />
              </el-form-item>
              <el-form-item label="对齐方式">
                <el-select v-model="selectedColumn.align" style="width: 100%">
                  <el-option label="左对齐" value="left" />
                  <el-option label="居中" value="center" />
                  <el-option label="右对齐" value="right" />
                </el-select>
              </el-form-item>
              <el-form-item label="固定列">
                <el-select v-model="selectedColumn.fixed" style="width: 100%" clearable>
                  <el-option label="左侧固定" value="left" />
                  <el-option label="右侧固定" value="right" />
                </el-select>
              </el-form-item>
              <el-form-item label="可排序">
                <el-switch v-model="selectedColumn.sortable" />
              </el-form-item>
              <el-form-item label="显示">
                <el-switch v-model="selectedColumn.visible" />
              </el-form-item>
            </el-form>
          </template>
          <div v-else class="empty-tip">
            <p>请选择一个列进行配置</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createTable, updateTable, getTableConfig, type TableConfig, type TableColumn as ApiTableColumn } from '@/api/table'
import type { ButtonConfig } from '@/api/button'
import { batchSaveButtonsByTableId, getButtonsByTableId } from '@/api/button'

interface TableColumn {
  id: string
  columnCode: string
  label: string
  width?: number
  minWidth?: number
  sortable?: boolean
  fixed?: string
  align?: string
  visible?: boolean
}

interface TableButton extends ButtonConfig {
  id: string
}

const route = useRoute()
const router = useRouter()
const tableId = ref<number | null>(route.query.id ? Number(route.query.id) : null)

const activeTab = ref('basic')
const activeCollapse = ref(['toolbar', 'row'])
const selectedColumn = ref<TableColumn | null>(null)
const selectedButton = ref<TableButton | null>(null)
const loading = ref(false)

const tableConfig = reactive({
  tableName: '',
  tableCode: '',
  apiUrl: '',
  apiMethod: 'GET',
  pagination: true,
  pageSize: 10,
  showIndex: true,
  selection: false,
  border: true,
  stripe: true,
  columns: [] as TableColumn[]
})

// 按钮配置
const toolbarButtons = ref<TableButton[]>([])
const rowButtons = ref<TableButton[]>([])

// 模拟数据
const mockData = ref([
  { id: 1, name: '张三', age: 28, email: 'zhangsan@example.com', status: '启用' },
  { id: 2, name: '李四', age: 32, email: 'lisi@example.com', status: '启用' },
  { id: 3, name: '王五', age: 25, email: 'wangwu@example.com', status: '停用' }
])

// 加载表格配置(编辑模式)
const loadTableConfig = async () => {
  if (!tableId.value) return

  loading.value = true
  try {
    const data = await getTableConfig(tableId.value)

    // 加载表格基本信息
    tableConfig.tableName = data.tableName
    tableConfig.tableCode = data.tableCode
    tableConfig.apiUrl = data.apiUrl || ''
    tableConfig.apiMethod = data.apiMethod || 'GET'
    tableConfig.pagination = data.pagination ?? true
    tableConfig.pageSize = data.pageSize || 10
    tableConfig.showIndex = data.showIndex ?? true
    tableConfig.selection = data.selection ?? false
    tableConfig.border = data.border ?? true
    tableConfig.stripe = data.stripe ?? true

    // 加载表格列配置
    if (data.columns && data.columns.length > 0) {
      tableConfig.columns = data.columns.map((col: ApiTableColumn) => ({
        id: col.id?.toString() || Date.now().toString(),
        columnCode: col.columnCode,
        label: col.label,
        width: col.width,
        minWidth: col.minWidth,
        sortable: col.sortable,
        fixed: col.fixed,
        align: col.align || 'left',
        visible: col.visible ?? true
      }))
    }

    // 加载按钮配置
    try {
      const buttons = await getButtonsByTableId(tableId.value!)
      buttons.forEach((btn: ButtonConfig) => {
        const button = {
          ...btn,
          id: btn.id?.toString() || Date.now().toString()
        }
        if (btn.position === 'toolbar') {
          toolbarButtons.value.push(button)
        } else if (btn.position === 'row') {
          rowButtons.value.push(button)
        }
      })
    } catch (error) {
      console.error('加载按钮配置失败:', error)
      // 不阻断主流程，只记录错误
    }

    ElMessage.success('加载表格配置成功')
  } catch (error) {
    ElMessage.error('加载表格配置失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (tableId.value) {
    loadTableConfig()
  } else {
    // 新建模式：初始化一些默认列
    tableConfig.columns = [
      { id: '1', columnCode: 'name', label: '姓名', minWidth: 120, align: 'left', visible: true },
      { id: '2', columnCode: 'age', label: '年龄', width: 80, align: 'center', visible: true },
      { id: '3', columnCode: 'email', label: '邮箱', minWidth: 180, align: 'left', visible: true },
      { id: '4', columnCode: 'status', label: '状态', width: 100, align: 'center', visible: true }
    ]
  }
})

const addColumn = () => {
  const newColumn: TableColumn = {
    id: Date.now().toString(),
    columnCode: `column_${tableConfig.columns.length + 1}`,
    label: `列${tableConfig.columns.length + 1}`,
    minWidth: 100,
    align: 'left',
    sortable: false,
    visible: true
  }
  tableConfig.columns.push(newColumn)
  selectedColumn.value = newColumn
}

const selectColumn = (column: TableColumn) => {
  selectedColumn.value = column
  activeTab.value = 'columns'
}

const removeColumn = (index: number) => {
  const removed = tableConfig.columns.splice(index, 1)[0]
  if (selectedColumn.value?.id === removed.id) {
    selectedColumn.value = null
  }
}

const moveColumnUp = (index: number) => {
  if (index > 0) {
    const temp = tableConfig.columns[index]
    tableConfig.columns[index] = tableConfig.columns[index - 1]
    tableConfig.columns[index - 1] = temp
  }
}

const moveColumnDown = (index: number) => {
  if (index < tableConfig.columns.length - 1) {
    const temp = tableConfig.columns[index]
    tableConfig.columns[index] = tableConfig.columns[index + 1]
    tableConfig.columns[index + 1] = temp
  }
}

// 按钮相关操作
const addToolbarButton = () => {
  const newButton: TableButton = {
    id: Date.now().toString(),
    buttonName: '新按钮',
    buttonCode: `toolbar_${Date.now()}`,
    position: 'toolbar',
    buttonType: 'default',
    buttonSize: 'default',
    actionType: 'api',
    visible: true,
    plain: false,
    round: false,
    loading: false,
    disabled: false
  }
  toolbarButtons.value.push(newButton)
  selectedButton.value = newButton
}

const addRowButton = () => {
  const newButton: TableButton = {
    id: Date.now().toString(),
    buttonName: '新按钮',
    buttonCode: `row_${Date.now()}`,
    position: 'row',
    buttonType: 'primary',
    buttonSize: 'small',
    actionType: 'api',
    visible: true,
    plain: true,
    round: false,
    loading: false,
    disabled: false
  }
  rowButtons.value.push(newButton)
  selectedButton.value = newButton
}

const selectButton = (button: TableButton) => {
  selectedButton.value = button
  activeTab.value = 'buttons'
}

const removeButton = (position: 'toolbar' | 'row', index: number) => {
  const buttons = position === 'toolbar' ? toolbarButtons.value : rowButtons.value
  const removed = buttons.splice(index, 1)[0]
  if (selectedButton.value?.id === removed.id) {
    selectedButton.value = null
  }
}

const moveButtonUp = (position: 'toolbar' | 'row', index: number) => {
  if (index > 0) {
    const buttons = position === 'toolbar' ? toolbarButtons.value : rowButtons.value
    const temp = buttons[index]
    buttons[index] = buttons[index - 1]
    buttons[index - 1] = temp
  }
}

const moveButtonDown = (position: 'toolbar' | 'row', index: number) => {
  const buttons = position === 'toolbar' ? toolbarButtons.value : rowButtons.value
  if (index < buttons.length - 1) {
    const temp = buttons[index]
    buttons[index] = buttons[index + 1]
    buttons[index + 1] = temp
  }
}

const handleActionTypeChange = (actionType: string) => {
  if (!selectedButton.value) return

  const defaultConfigs: Record<string, any> = {
    api: { url: '/api/submit', method: 'POST' },
    dialog: { formId: null, title: '弹窗标题', width: '600px', mode: 'add' },
    drawer: { formId: null, title: '抽屉标题', size: 'medium', mode: 'add' },
    route: { path: '/', openType: 'push' },
    link: { path: 'https://example.com' },
    confirm: { title: '确认操作', message: '确定要执行此操作吗？', type: 'warning' }
  }

  if (defaultConfigs[actionType]) {
    selectedButton.value.actionConfig = JSON.stringify(defaultConfigs[actionType], null, 2)
  }
}

const handlePreview = () => {
  ElMessage.info('预览功能开发中...')
}

const handleBack = () => {
  router.back()
}

const handleSave = async () => {
  // 表单验证
  if (!tableConfig.tableName) {
    ElMessage.warning('请输入表格名称')
    return
  }
  if (!tableConfig.tableCode) {
    ElMessage.warning('请输入表格编码')
    return
  }
  if (tableConfig.columns.length === 0) {
    ElMessage.warning('请添加至少一个列')
    return
  }

  loading.value = true
  try {
    // 构建保存数据
    const saveData: TableConfig = {
      tableName: tableConfig.tableName,
      tableCode: tableConfig.tableCode,
      apiUrl: tableConfig.apiUrl,
      apiMethod: tableConfig.apiMethod,
      pagination: tableConfig.pagination,
      pageSize: tableConfig.pageSize,
      showIndex: tableConfig.showIndex,
      selection: tableConfig.selection,
      border: tableConfig.border,
      stripe: tableConfig.stripe,
      status: true,
      columns: tableConfig.columns.map((col, index) => ({
        columnName: col.label,
        columnCode: col.columnCode,
        label: col.label,
        width: col.width,
        minWidth: col.minWidth,
        sortable: col.sortable || false,
        fixed: col.fixed,
        align: col.align || 'left',
        visible: col.visible ?? true,
        sortOrder: index
      }))
    }

    if (tableId.value) {
      // 更新模式
      await updateTable(tableId.value, saveData)
      ElMessage.success('更新表格配置成功')
    } else {
      // 创建模式
      const id = await createTable(saveData)
      tableId.value = id
      ElMessage.success('创建表格配置成功')

      // 更新路由参数,避免重复创建
      router.replace({ query: { id: id.toString() } })
    }

    // 保存按钮配置（工具栏和操作列按钮）
    const allButtons = [...toolbarButtons.value, ...rowButtons.value]
    if (allButtons.length > 0) {
      try {
        // 将临时ID转换为正确格式
        const buttonsToSave = allButtons.map(btn => ({
          ...btn,
          id: undefined, // 清除临时ID
          tableId: tableId.value
        }))
        await batchSaveButtonsByTableId(tableId.value!, buttonsToSave)
        ElMessage.success('保存按钮配置成功')
      } catch (error: any) {
        console.error('保存按钮配置失败:', error)
        ElMessage.warning('表格保存成功，但按钮配置保存失败')
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.table-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .designer-header {
    height: 50px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;

      .title {
        margin-left: 16px;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }

  .designer-body {
    flex: 1;
    display: flex;
    overflow: hidden;

    .panel-title {
      padding: 12px 16px;
      font-weight: bold;
      border-bottom: 1px solid #e6e6e6;
    }

    .config-panel {
      width: 300px;
      min-width: 300px;
      flex-shrink: 0;
      background-color: #fff;
      border-right: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      :deep(.el-tabs) {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: hidden;
      }

      :deep(.el-tabs__header) {
        margin: 0;
        padding: 0 16px;
        flex-shrink: 0;
      }

      :deep(.el-tabs__content) {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .column-list {
        .column-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
        }

        .column-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 12px;
          border: 1px solid #e6e6e6;
          border-radius: 4px;
          margin-bottom: 8px;
          cursor: pointer;

          &:hover {
            border-color: #409eff;
          }

          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
          }

          .column-info {
            .column-label {
              font-weight: bold;
              margin-right: 8px;
            }

            .column-code {
              font-size: 12px;
              color: #909399;
            }
          }

          .column-actions {
            display: flex;
            gap: 4px;

            .el-icon {
              cursor: pointer;
              color: #909399;

              &:hover {
                color: #409eff;
              }
            }
          }
        }
      }
    }

    .preview-panel {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      background-color: #fff;
      margin: 0 1px;
      overflow: hidden;

      .panel-title {
        flex-shrink: 0;
      }

      .preview-area {
        flex: 1;
        padding: 20px;
        overflow-x: auto;
        overflow-y: auto;
      }
    }

    .property-panel {
      width: 280px;
      min-width: 280px;
      flex-shrink: 0;
      background-color: #fff;
      border-left: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      .panel-title {
        flex-shrink: 0;
      }

      .property-content {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .empty-tip {
        text-align: center;
        color: #909399;
        padding: 40px 0;
      }

      .buttons-config {
        .button-list {
          max-height: 200px;
          overflow-y: auto;

          .button-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 8px 12px;
            margin-bottom: 8px;
            border: 1px solid #e6e6e6;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              border-color: #409eff;
            }

            &.active {
              border-color: #409eff;
              background-color: #ecf5ff;
            }

            .button-info {
              display: flex;
              align-items: center;
              gap: 8px;
              flex: 1;

              .button-code {
                font-size: 12px;
                color: #909399;
              }
            }

            .button-actions {
              display: none;
              gap: 4px;

              .el-icon {
                cursor: pointer;
                color: #909399;

                &:hover {
                  color: #409eff;
                }

                &.danger:hover {
                  color: #f56c6c;
                }
              }
            }

            &:hover .button-actions {
              display: flex;
            }
          }
        }

        .selected-button-title {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #e6e6e6;
          font-weight: bold;
        }
      }
    }
  }
}
</style>
