<template>
  <div class="form-designer" v-loading="loading">
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <span class="title">表单设计器{{ formId ? ' - 编辑模式' : ' - 新建模式' }}</span>
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
      <!-- 左侧组件库 -->
      <div class="component-panel">
        <div class="panel-title">组件库</div>
        <div class="component-list">
          <div class="component-group">
            <div class="group-title">基础组件</div>
            <div class="component-items">
              <div
                v-for="item in basicComponents"
                :key="item.type"
                class="component-item"
                draggable="true"
                @dragstart="handleDragStart($event, item)"
              >
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
          <div class="component-group">
            <div class="group-title">高级组件</div>
            <div class="component-items">
              <div
                v-for="item in advancedComponents"
                :key="item.type"
                class="component-item"
                draggable="true"
                @dragstart="handleDragStart($event, item)"
              >
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间设计区域 -->
      <div class="design-panel">
        <div class="panel-title">设计区域</div>
        <div
          class="design-area"
          @dragover.prevent
          @drop="handleDrop"
        >
          <el-form
            v-if="formFields.length > 0"
            :label-width="formConfig.labelWidth + 'px'"
            :label-position="formConfig.labelPosition"
          >
            <el-row :gutter="20">
              <el-col
                v-for="(field, index) in formFields"
                :key="field.id"
                :span="field.span || 12"
              >
                <div
                  class="field-wrapper"
                  :class="{ active: selectedField?.id === field.id }"
                  @click="selectField(field)"
                >
                  <el-form-item :label="field.label" :required="field.required">
                    <!-- 输入框 -->
                    <el-input
                      v-if="field.fieldType === 'input'"
                      v-model="field.defaultValue"
                      :placeholder="field.placeholder"
                      :disabled="field.disabled"
                    />
                    <!-- 文本域 -->
                    <el-input
                      v-else-if="field.fieldType === 'textarea'"
                      v-model="field.defaultValue"
                      type="textarea"
                      :placeholder="field.placeholder"
                      :rows="3"
                    />
                    <!-- 下拉框 -->
                    <el-select
                      v-else-if="field.fieldType === 'select'"
                      v-model="field.defaultValue"
                      :placeholder="field.placeholder"
                      style="width: 100%"
                    >
                      <el-option
                        v-for="opt in field.options"
                        :key="opt.value"
                        :label="opt.label"
                        :value="opt.value"
                      />
                    </el-select>
                    <!-- 日期选择 -->
                    <el-date-picker
                      v-else-if="field.fieldType === 'date'"
                      v-model="field.defaultValue"
                      type="date"
                      :placeholder="field.placeholder"
                      style="width: 100%"
                    />
                    <!-- 开关 -->
                    <el-switch
                      v-else-if="field.fieldType === 'switch'"
                      v-model="field.defaultValue"
                    />
                    <!-- 单选框 -->
                    <el-radio-group
                      v-else-if="field.fieldType === 'radio'"
                      v-model="field.defaultValue"
                    >
                      <el-radio
                        v-for="opt in field.options"
                        :key="opt.value"
                        :value="opt.value"
                      >{{ opt.label }}</el-radio>
                    </el-radio-group>
                    <!-- 多选框 -->
                    <el-checkbox-group
                      v-else-if="field.fieldType === 'checkbox'"
                      v-model="field.defaultValue"
                    >
                      <el-checkbox
                        v-for="opt in field.options"
                        :key="opt.value"
                        :value="opt.value"
                      >{{ opt.label }}</el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                  <div class="field-actions">
                    <el-icon @click.stop="moveUp(index)" v-if="index > 0"><Top /></el-icon>
                    <el-icon @click.stop="moveDown(index)" v-if="index < formFields.length - 1"><Bottom /></el-icon>
                    <el-icon @click.stop="removeField(index)"><Delete /></el-icon>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-form>
          <div v-else class="empty-tip">
            <el-icon :size="48"><DocumentAdd /></el-icon>
            <p>从左侧拖拽组件到此处</p>
          </div>
        </div>
      </div>

      <!-- 右侧属性配置 -->
      <div class="property-panel">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="字段属性" name="field">
            <template v-if="selectedField">
              <el-form label-width="80px" size="small">
                <el-form-item label="字段标签">
                  <el-input v-model="selectedField.label" />
                </el-form-item>
                <el-form-item label="字段编码">
                  <el-input v-model="selectedField.fieldCode" />
                </el-form-item>
                <el-form-item label="占位提示">
                  <el-input v-model="selectedField.placeholder" />
                </el-form-item>
                <el-form-item label="默认值">
                  <el-input v-model="selectedField.defaultValue" />
                </el-form-item>
                <el-form-item label="栅格宽度">
                  <el-slider v-model="selectedField.span" :min="6" :max="24" :step="6" show-stops />
                </el-form-item>
                <el-form-item label="是否必填">
                  <el-switch v-model="selectedField.required" />
                </el-form-item>
                <el-form-item label="是否禁用">
                  <el-switch v-model="selectedField.disabled" />
                </el-form-item>
              </el-form>
            </template>
            <div v-else class="empty-tip">
              <p>请选择一个字段</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="表单属性" name="form">
            <el-form label-width="80px" size="small">
              <el-form-item label="表单名称">
                <el-input v-model="formConfig.formName" />
              </el-form-item>
              <el-form-item label="表单编码">
                <el-input v-model="formConfig.formCode" />
              </el-form-item>
              <el-form-item label="标签宽度">
                <el-input-number v-model="formConfig.labelWidth" :min="60" :max="200" />
              </el-form-item>
              <el-form-item label="标签位置">
                <el-select v-model="formConfig.labelPosition">
                  <el-option label="右对齐" value="right" />
                  <el-option label="左对齐" value="left" />
                  <el-option label="顶部" value="top" />
                </el-select>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="按钮配置" name="buttons">
            <div class="buttons-config">
              <div class="config-header">
                <el-button type="primary" size="small" @click="addFormButton">
                  <el-icon><Plus /></el-icon>添加按钮
                </el-button>
              </div>
              <div class="button-list">
                <div
                  v-for="(button, index) in formButtons"
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
                    <el-icon @click.stop="moveButtonUp(index)" v-if="index > 0"><Top /></el-icon>
                    <el-icon @click.stop="moveButtonDown(index)" v-if="index < formButtons.length - 1"><Bottom /></el-icon>
                    <el-icon @click.stop="removeButton(index)" class="danger"><Delete /></el-icon>
                  </div>
                </div>
              </div>
              <template v-if="selectedButton">
                <el-divider />
                <el-form label-width="90px" size="small">
                  <el-form-item label="按钮名称">
                    <el-input v-model="selectedButton.buttonName" />
                  </el-form-item>
                  <el-form-item label="按钮编码">
                    <el-input v-model="selectedButton.buttonCode" />
                  </el-form-item>
                  <el-form-item label="按钮位置">
                    <el-select v-model="selectedButton.position">
                      <el-option label="表单内" value="form" />
                      <el-option label="表单底部" value="footer" />
                      <el-option label="弹窗内" value="dialog" />
                    </el-select>
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
                    <el-input v-model="selectedButton.icon" placeholder="例如: Search" />
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
                      placeholder="JavaScript代码，可使用 row, form, selection 等上下文变量"
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createForm, updateForm, getFormConfig, type FormConfig, type FormField as ApiFormField } from '@/api/form'
import type { ButtonConfig } from '@/api/button'
import { batchSaveButtonsByFormId, getButtonsByFormId } from '@/api/button'

interface FormField {
  id: string
  fieldType: string
  fieldCode: string
  label: string
  placeholder?: string
  defaultValue?: any
  required?: boolean
  disabled?: boolean
  span?: number
  options?: { label: string; value: any }[]
}

interface FormButton extends ButtonConfig {
  id: string
}

const route = useRoute()
const router = useRouter()
const formId = ref<number | null>(route.query.id ? Number(route.query.id) : null)

const activeTab = ref('field')
const selectedField = ref<FormField | null>(null)
const formFields = ref<FormField[]>([])
const loading = ref(false)

const formConfig = reactive({
  formName: '',
  formCode: '',
  labelWidth: 100,
  labelPosition: 'right' as 'right' | 'left' | 'top'
})

// 按钮配置
const formButtons = ref<FormButton[]>([])
const selectedButton = ref<FormButton | null>(null)

// 加载表单配置(编辑模式)
const loadFormConfig = async () => {
  if (!formId.value) return

  loading.value = true
  try {
    const data = await getFormConfig(formId.value)

    // 加载表单基本信息
    formConfig.formName = data.formName
    formConfig.formCode = data.formCode
    formConfig.labelWidth = data.labelWidth || 100
    formConfig.labelPosition = (data.labelPosition || 'right') as 'right' | 'left' | 'top'

    // 加载表单字段
    if (data.fields && data.fields.length > 0) {
      formFields.value = data.fields.map((field: ApiFormField) => ({
        id: field.id?.toString() || Date.now().toString(),
        fieldType: field.fieldType,
        fieldCode: field.fieldCode,
        label: field.label,
        placeholder: field.placeholder,
        defaultValue: field.defaultValue,
        required: field.required,
        disabled: field.disabled,
        span: field.span || 12,
        options: field.optionsJson ? JSON.parse(field.optionsJson) : undefined
      }))
    }

    // 加载按钮配置
    try {
      const buttons = await getButtonsByFormId(formId.value!)
      formButtons.value = buttons.map((btn: ButtonConfig) => ({
        ...btn,
        id: btn.id?.toString() || Date.now().toString()
      }))
    } catch (error) {
      console.error('加载按钮配置失败:', error)
      // 不阻断主流程，只记录错误
    }

    ElMessage.success('加载表单配置成功')
  } catch (error) {
    ElMessage.error('加载表单配置失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (formId.value) {
    loadFormConfig()
  }
})

// 基础组件
const basicComponents = [
  { type: 'input', label: '输入框', icon: 'Edit' },
  { type: 'textarea', label: '文本域', icon: 'Document' },
  { type: 'select', label: '下拉框', icon: 'ArrowDown' },
  { type: 'radio', label: '单选框', icon: 'CircleCheck' },
  { type: 'checkbox', label: '多选框', icon: 'Check' },
  { type: 'switch', label: '开关', icon: 'Switch' }
]

// 高级组件
const advancedComponents = [
  { type: 'date', label: '日期选择', icon: 'Calendar' },
  { type: 'datetime', label: '日期时间', icon: 'Timer' },
  { type: 'upload', label: '文件上传', icon: 'Upload' },
  { type: 'cascader', label: '级联选择', icon: 'Share' }
]

let dragComponent: any = null

const handleDragStart = (e: DragEvent, component: any) => {
  dragComponent = component
}

const handleDrop = () => {
  if (!dragComponent) return

  const newField: FormField = {
    id: Date.now().toString(),
    fieldType: dragComponent.type,
    fieldCode: `field_${Date.now()}`,
    label: dragComponent.label,
    placeholder: `请输入${dragComponent.label}`,
    defaultValue: dragComponent.type === 'checkbox' ? [] : '',
    required: false,
    disabled: false,
    span: 12,
    options: ['select', 'radio', 'checkbox'].includes(dragComponent.type)
      ? [
          { label: '选项1', value: '1' },
          { label: '选项2', value: '2' }
        ]
      : undefined
  }

  formFields.value.push(newField)
  selectedField.value = newField
  dragComponent = null
}

const selectField = (field: FormField) => {
  selectedField.value = field
  activeTab.value = 'field'
}

const removeField = (index: number) => {
  formFields.value.splice(index, 1)
  if (selectedField.value?.id === formFields.value[index]?.id) {
    selectedField.value = null
  }
}

const moveUp = (index: number) => {
  if (index > 0) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index - 1]
    formFields.value[index - 1] = temp
  }
}

const moveDown = (index: number) => {
  if (index < formFields.value.length - 1) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index + 1]
    formFields.value[index + 1] = temp
  }
}

// 按钮相关操作
const addFormButton = () => {
  const newButton: FormButton = {
    id: Date.now().toString(),
    buttonName: '新按钮',
    buttonCode: `button_${Date.now()}`,
    position: 'footer',
    buttonType: 'default',
    buttonSize: 'default',
    actionType: 'api',
    visible: true,
    plain: false,
    round: false,
    loading: false,
    disabled: false
  }
  formButtons.value.push(newButton)
  selectedButton.value = newButton
}

const selectButton = (button: FormButton) => {
  selectedButton.value = button
  activeTab.value = 'buttons'
}

const removeButton = (index: number) => {
  formButtons.value.splice(index, 1)
  if (selectedButton.value?.id === formButtons.value[index]?.id) {
    selectedButton.value = null
  }
}

const moveButtonUp = (index: number) => {
  if (index > 0) {
    const temp = formButtons.value[index]
    formButtons.value[index] = formButtons.value[index - 1]
    formButtons.value[index - 1] = temp
  }
}

const moveButtonDown = (index: number) => {
  if (index < formButtons.value.length - 1) {
    const temp = formButtons.value[index]
    formButtons.value[index] = formButtons.value[index + 1]
    formButtons.value[index + 1] = temp
  }
}

const handleActionTypeChange = (actionType: string) => {
  if (!selectedButton.value) return

  // 根据动作类型设置默认配置
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
  router.push({ name: 'FormList' })
}

const handleSave = async () => {
  // 表单验证
  if (!formConfig.formName) {
    ElMessage.warning('请输入表单名称')
    return
  }
  if (!formConfig.formCode) {
    ElMessage.warning('请输入表单编码')
    return
  }
  if (formFields.value.length === 0) {
    ElMessage.warning('请添加至少一个字段')
    return
  }

  loading.value = true
  try {
    // 构建保存数据
    const saveData: FormConfig = {
      formName: formConfig.formName,
      formCode: formConfig.formCode,
      labelWidth: formConfig.labelWidth,
      labelPosition: formConfig.labelPosition,
      layoutType: 'grid',
      status: true,
      fields: formFields.value.map((field, index) => ({
        fieldName: field.label,
        fieldCode: field.fieldCode,
        fieldType: field.fieldType,
        label: field.label,
        placeholder: field.placeholder,
        defaultValue: field.defaultValue,
        required: field.required || false,
        disabled: field.disabled || false,
        visible: true,
        sortOrder: index,
        span: field.span || 12,
        optionsJson: field.options ? JSON.stringify(field.options) : undefined
      }))
    }

    if (formId.value) {
      // 更新模式
      await updateForm(formId.value, saveData)
      ElMessage.success('更新表单配置成功')
    } else {
      // 创建模式
      const id = await createForm(saveData)
      formId.value = id
      ElMessage.success('创建表单配置成功')

      // 更新路由参数,避免重复创建
      router.replace({ query: { id: id.toString() } })
    }

    // 保存按钮配置
    if (formButtons.value.length > 0) {
      try {
        // 将临时ID转换为正确格式
        const buttonsToSave = formButtons.value.map(btn => ({
          ...btn,
          id: undefined, // 清除临时ID
          formId: formId.value
        }))
        await batchSaveButtonsByFormId(formId.value!, buttonsToSave)
        ElMessage.success('保存按钮配置成功')
      } catch (error: any) {
        console.error('保存按钮配置失败:', error)
        ElMessage.warning('表单保存成功，但按钮配置保存失败')
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
.form-designer {
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

    .component-panel {
      width: 260px;
      background-color: #fff;
      border-right: 1px solid #e6e6e6;

      .component-list {
        padding: 16px;

        .component-group {
          margin-bottom: 16px;

          .group-title {
            font-size: 12px;
            color: #909399;
            margin-bottom: 8px;
          }

          .component-items {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .component-item {
              width: calc(50% - 4px);
              padding: 8px;
              display: flex;
              align-items: center;
              gap: 6px;
              border: 1px solid #e6e6e6;
              border-radius: 4px;
              cursor: move;
              font-size: 12px;

              &:hover {
                border-color: #409eff;
                color: #409eff;
              }
            }
          }
        }
      }
    }

    .design-panel {
      flex: 1;
      display: flex;
      flex-direction: column;
      background-color: #fff;
      margin: 0 1px;

      .design-area {
        flex: 1;
        padding: 20px;
        overflow: auto;

        .field-wrapper {
          position: relative;
          padding: 8px;
          border: 1px dashed transparent;
          border-radius: 4px;
          margin-bottom: 8px;

          &:hover {
            border-color: #409eff;
          }

          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
          }

          .field-actions {
            position: absolute;
            top: 4px;
            right: 4px;
            display: none;

            .el-icon {
              cursor: pointer;
              margin-left: 4px;
              color: #909399;

              &:hover {
                color: #409eff;
              }
            }
          }

          &:hover .field-actions,
          &.active .field-actions {
            display: flex;
          }
        }

        .empty-tip {
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #909399;

          p {
            margin-top: 16px;
          }
        }
      }
    }

    .property-panel {
      width: 300px;
      background-color: #fff;
      border-left: 1px solid #e6e6e6;

      :deep(.el-tabs__header) {
        margin: 0;
        padding: 0 16px;
      }

      :deep(.el-tabs__content) {
        padding: 16px;
      }

      .empty-tip {
        text-align: center;
        color: #909399;
        padding: 40px 0;
      }

      .buttons-config {
        .config-header {
          margin-bottom: 16px;
        }

        .button-list {
          max-height: 300px;
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
      }
    }
  }
}
</style>
