<template>
  <el-dialog
    v-model="dialogVisible"
    title="配置按钮动作"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <!-- 动作类型 -->
      <el-form-item label="动作类型" prop="actionType">
        <el-radio-group v-model="formData.actionType" @change="handleActionTypeChange">
          <el-radio label="dialog">弹窗</el-radio>
          <el-radio label="drawer">抽屉</el-radio>
          <el-radio label="api">API请求</el-radio>
          <el-radio label="route">路由跳转</el-radio>
          <el-radio label="link">打开链接</el-radio>
          <el-radio label="confirm">确认操作</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 组件类型(仅弹窗/抽屉) -->
      <template v-if="isContainerType">
        <el-form-item label="组件类型" prop="componentType">
          <el-radio-group v-model="formData.componentType" @change="handleComponentTypeChange">
            <el-radio label="form">表单</el-radio>
            <el-radio label="table">表格</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 表单选择 -->
        <template v-if="formData.componentType === 'form'">
          <el-form-item label="选择表单" prop="configId" required>
            <el-select
              v-model="formData.configId"
              placeholder="请选择表单"
              filterable
              style="width: 100%"
              :loading="formListLoading"
            >
              <el-option
                v-for="form in formList"
                :key="form.id"
                :label="form.formName"
                :value="form.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="弹窗标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入弹窗标题" />
          </el-form-item>

          <el-form-item label="弹窗尺寸" prop="width">
            <el-select v-model="formData.width" placeholder="请选择尺寸" style="width: 100%">
              <el-option label="小 (500px)" value="500px" />
              <el-option label="中 (600px)" value="600px" />
              <el-option label="大 (800px)" value="800px" />
              <el-option label="超大 (1000px)" value="1000px" />
            </el-select>
          </el-form-item>

          <el-form-item label="操作模式" prop="mode">
            <el-radio-group v-model="formData.mode">
              <el-radio label="add">新增</el-radio>
              <el-radio label="edit">编辑</el-radio>
              <el-radio label="view">查看</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="formData.mode === 'edit'" label="数据接口" prop="dataUrl">
            <el-input v-model="formData.dataUrl" placeholder="/api/user/{{row.id}}" />
            <div class="form-tip">支持模板变量: {{row.id}}</div>
          </el-form-item>

          <el-form-item label="提交接口" prop="submitUrl">
            <el-input v-model="formData.submitUrl" placeholder="/api/user" />
          </el-form-item>

          <el-form-item label="成功提示" prop="successMessage">
            <el-input v-model="formData.successMessage" placeholder="操作成功" />
          </el-form-item>
        </template>

        <!-- 表格选择 -->
        <template v-if="formData.componentType === 'table'">
          <el-form-item label="选择表格" prop="configId" required>
            <el-select
              v-model="formData.configId"
              placeholder="请选择表格"
              filterable
              style="width: 100%"
              :loading="tableListLoading"
            >
              <el-option
                v-for="table in tableList"
                :key="table.id"
                :label="table.tableName"
                :value="table.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="弹窗标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入弹窗标题" />
          </el-form-item>

          <el-form-item label="弹窗尺寸" prop="width">
            <el-select v-model="formData.width" placeholder="请选择尺寸" style="width: 100%">
              <el-option label="中 (800px)" value="800px" />
              <el-option label="大 (900px)" value="900px" />
              <el-option label="超大 (1200px)" value="1200px" />
            </el-select>
          </el-form-item>

          <el-divider content-position="left">选择模式配置</el-divider>

          <el-form-item label="操作模式" prop="mode">
            <el-radio-group v-model="formData.mode">
              <el-radio label="select">选择数据</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="是否多选" prop="multiple">
            <el-switch v-model="formData.multiple" />
          </el-form-item>

          <el-form-item label="返回字段" prop="selectField">
            <el-input v-model="formData.selectField" placeholder="id" />
            <div class="form-tip">选中行后提取的字段,如 id、name</div>
          </el-form-item>

          <el-divider content-position="left">确认操作配置</el-divider>

          <el-form-item label="回调方式" prop="onConfirmAction">
            <el-radio-group v-model="formData.onConfirmAction">
              <el-radio label="callback">回填表单</el-radio>
              <el-radio label="api">调用API</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="formData.onConfirmAction === 'callback'" label="回填字段" prop="targetField">
            <el-input v-model="formData.targetField" placeholder="userId" />
            <div class="form-tip">父表单中接收数据的字段名</div>
          </el-form-item>

          <el-form-item v-if="formData.onConfirmAction === 'api'" label="回调接口" prop="callbackUrl">
            <el-input v-model="formData.callbackUrl" placeholder="/api/order/add-products" />
            <div class="form-tip">将选中的数据POST到此接口</div>
          </el-form-item>
        </template>
      </template>

      <!-- API请求配置 -->
      <template v-if="formData.actionType === 'api'">
        <el-form-item label="请求地址" prop="url">
          <el-input v-model="formData.url" placeholder="/api/user" />
        </el-form-item>

        <el-form-item label="请求方法" prop="method">
          <el-radio-group v-model="formData.method">
            <el-radio label="GET">GET</el-radio>
            <el-radio label="POST">POST</el-radio>
            <el-radio label="PUT">PUT</el-radio>
            <el-radio label="DELETE">DELETE</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="使用行数据">
          <el-switch v-model="formData.useRowData" />
        </el-form-item>

        <el-form-item label="成功提示" prop="successMessage">
          <el-input v-model="formData.successMessage" placeholder="操作成功" />
        </el-form-item>
      </template>

      <!-- 路由跳转配置 -->
      <template v-if="formData.actionType === 'route'">
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="formData.path" placeholder="/user/detail" />
        </el-form-item>

        <el-form-item label="跳转方式" prop="openType">
          <el-radio-group v-model="formData.openType">
            <el-radio label="push">push</el-radio>
            <el-radio label="replace">replace</el-radio>
          </el-radio-group>
        </el-form-item>
      </template>

      <!-- 打开链接配置 -->
      <template v-if="formData.actionType === 'link'">
        <el-form-item label="链接地址" prop="url">
          <el-input v-model="formData.url" placeholder="https://example.com" />
        </el-form-item>
      </template>

      <!-- 确认操作配置 -->
      <template v-if="formData.actionType === 'confirm'">
        <el-form-item label="确认标题" prop="confirmTitle">
          <el-input v-model="formData.confirmTitle" placeholder="提示" />
        </el-form-item>

        <el-form-item label="确认消息" prop="confirmMessage">
          <el-input
            v-model="formData.confirmMessage"
            type="textarea"
            :rows="2"
            placeholder="确定要执行此操作吗?"
          />
        </el-form-item>

        <el-form-item label="消息类型" prop="confirmType">
          <el-select v-model="formData.confirmType" placeholder="请选择">
            <el-option label="警告" value="warning" />
            <el-option label="信息" value="info" />
            <el-option label="成功" value="success" />
            <el-option label="错误" value="error" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">确认后的操作</el-divider>
        <el-form-item label="操作类型">
          <el-radio-group v-model="formData.afterConfirmAction">
            <el-radio label="api">API请求</el-radio>
            <el-radio label="route">路由跳转</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- API 请求配置 -->
        <template v-if="formData.afterConfirmAction === 'api'">
          <el-form-item label="请求地址">
            <el-input v-model="formData.afterConfirmUrl" placeholder="/api/user/delete" />
          </el-form-item>
          <el-form-item label="请求方法">
            <el-radio-group v-model="formData.afterConfirmMethod">
              <el-radio label="POST">POST</el-radio>
              <el-radio label="GET">GET</el-radio>
              <el-radio label="PUT">PUT</el-radio>
              <el-radio label="DELETE">DELETE</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- 路由跳转配置 -->
        <template v-if="formData.afterConfirmAction === 'route'">
          <el-form-item label="路由路径">
            <el-input v-model="formData.afterConfirmPath" placeholder="/user/list" />
          </el-form-item>
        </template>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getFormList } from '@/api/form'
import { getTableList } from '@/api/table'
import type { ActionConfig } from '@/utils/buttonActionHandler'

interface Props {
  visible: boolean
  config?: ActionConfig
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'confirm', config: ActionConfig): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const formList = ref<any[]>([])
const tableList = ref<any[]>([])
const formListLoading = ref(false)
const tableListLoading = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 是否是容器类型(弹窗/抽屉)
const isContainerType = computed(() => {
  return props.config?.actionType === 'dialog' || props.config?.actionType === 'drawer'
})

const formData = ref<ActionConfig>({
  actionType: 'dialog',
  componentType: 'form',
  configId: undefined,
  title: '',
  width: '600px',
  mode: 'add',
  dataUrl: '',
  submitUrl: '',
  multiple: false,
  selectField: 'id',
  onConfirmAction: 'callback',
  targetField: '',
  callbackUrl: '',
  successMessage: '',
  url: '',
  method: 'POST',
  path: '',
  openType: 'push',
  useRowData: true,
  confirmTitle: '提示',
  confirmMessage: '确定要执行此操作吗?',
  confirmType: 'warning',
  afterConfirmAction: 'api',
  afterConfirmUrl: '',
  afterConfirmMethod: 'POST',
  afterConfirmPath: ''
})

const formRules: FormRules = {
  actionType: [{ required: true, message: '请选择动作类型', trigger: 'change' }],
  componentType: [{ required: true, message: '请选择组件类型', trigger: 'change' }],
  configId: [{ required: true, message: '请选择表单/表格', trigger: 'change' }]
}

// 加载表单列表
const loadFormList = async () => {
  formListLoading.value = true
  try {
    const res = await getFormList({ current: 1, size: 1000 })
    formList.value = res.records || []
  } catch (error) {
    ElMessage.error('加载表单列表失败')
  } finally {
    formListLoading.value = false
  }
}

// 加载表格列表
const loadTableList = async () => {
  tableListLoading.value = true
  try {
    const res = await getTableList({ current: 1, size: 1000 })
    tableList.value = res.records || []
  } catch (error) {
    ElMessage.error('加载表格列表失败')
  } finally {
    tableListLoading.value = false
  }
}

// 动作类型变化
const handleActionTypeChange = () => {
  // 重置组件类型
  if (!isContainerType.value) {
    formData.value.componentType = undefined
  }
}

// 组件类型变化
const handleComponentTypeChange = () => {
  // 清空配置ID
  formData.value.configId = undefined
}

// 确认配置
const handleConfirm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 构建配置对象
    const config: ActionConfig = {
      componentType: formData.value.componentType,
      configId: formData.value.configId,
      title: formData.value.title,
      width: formData.value.width,
      mode: formData.value.mode as any,
      dataUrl: formData.value.dataUrl || undefined,
      submitUrl: formData.value.submitUrl || undefined,
      multiple: formData.value.multiple,
      selectField: formData.value.selectField,
      url: formData.value.url,
      method: formData.value.method as any,
      path: formData.value.path,
      openType: formData.value.openType as any,
      successAction: formData.value.successMessage ? {
        type: 'close',
        message: formData.value.successMessage
      } : undefined
    }

    // 添加选择模式配置
    if (formData.value.componentType === 'table' && formData.value.mode === 'select') {
      config.onConfirm = {
        action: formData.value.onConfirmAction as any,
        targetField: formData.value.targetField || undefined,
        url: formData.value.callbackUrl || undefined
      }
    }

    emit('confirm', config)
    dialogVisible.value = false
  } catch (error) {
    ElMessage.warning('请检查表单填写是否完整')
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}

// 初始化数据
const initData = () => {
  if (props.config) {
    // 从现有配置加载
    const config = props.config
    formData.value = {
      actionType: 'dialog',
      componentType: config.componentType || 'form',
      configId: config.configId,
      title: config.title || '',
      width: config.width || '600px',
      mode: config.mode || 'add',
      dataUrl: config.dataUrl || '',
      submitUrl: config.submitUrl || '',
      multiple: config.multiple || false,
      selectField: config.selectField || 'id',
      onConfirmAction: config.onConfirm?.action || 'callback',
      targetField: config.onConfirm?.targetField || '',
      callbackUrl: config.onConfirm?.url || '',
      successMessage: config.successAction?.message || '',
      url: config.url || '',
      method: config.method || 'POST',
      path: config.path || '',
      openType: config.openType || 'push',
      useRowData: true,
      confirmTitle: '提示',
      confirmMessage: '确定要执行此操作吗?',
      confirmType: 'warning',
      afterConfirmAction: 'api',
      afterConfirmUrl: '',
      afterConfirmMethod: 'POST',
      afterConfirmPath: ''
    }
  } else {
    // 默认值
    formData.value = {
      actionType: 'dialog',
      componentType: 'form',
      configId: undefined,
      title: '',
      width: '600px',
      mode: 'add',
      dataUrl: '',
      submitUrl: '',
      multiple: false,
      selectField: 'id',
      onConfirmAction: 'callback',
      targetField: '',
      callbackUrl: '',
      successMessage: '',
      url: '',
      method: 'POST',
      path: '',
      openType: 'push',
      useRowData: true,
      confirmTitle: '提示',
      confirmMessage: '确定要执行此操作吗?',
      confirmType: 'warning',
      afterConfirmAction: 'api',
      afterConfirmUrl: '',
      afterConfirmMethod: 'POST',
      afterConfirmPath: ''
    }
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    initData()
    loadFormList()
    loadTableList()
  }
})

onMounted(() => {
  if (props.visible) {
    loadFormList()
    loadTableList()
  }
})
</script>

<style scoped lang="scss">
.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
</style>
