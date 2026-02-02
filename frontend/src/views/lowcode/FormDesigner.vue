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
        <el-button @click="handleLoadFromBinding">
          <el-icon><Link /></el-icon>从字段绑定加载
        </el-button>
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
        <div class="panel-title">
          <span v-if="currentTemplate">{{ currentTemplate.templateName }}</span>
          <span v-else>设计区域</span>
        </div>

        <!-- 基于 Template 的 Slot 树形结构 -->
        <div v-if="currentTemplate" class="slot-tree-container">
          <div
            v-for="slot in templateSlots"
            :key="slot.id"
            class="slot-node"
            :class="{ 'slot-active': activeSlotId === slot.id }"
            @click="selectSlot(slot.id)"
          >
            <!-- Slot 头部 -->
            <div class="slot-header" @click.stop="toggleSlot(slot.id)">
              <el-icon class="expand-icon" :class="{ 'is-expanded': expandedSlots.has(slot.id) }">
                <ArrowRight />
              </el-icon>
              <span class="slot-name">[{{ slot.id }}] {{ slot.label }}</span>
              <span class="field-count">({{ getSlotFieldCount(slot.id) }})</span>
            </div>

            <!-- Slot 内容区域 -->
            <div v-show="expandedSlots.has(slot.id)" class="slot-content">
              <!-- 字段列表 -->
              <div
                v-for="(field, index) in slotFields[slot.id]"
                :key="field.id"
                class="field-item"
                :class="{ 'field-active': selectedField?.id === field.id }"
                @click.stop="selectField(field)"
              >
                <div class="field-info">
                  <el-icon class="field-icon">
                    <component :is="getFieldIcon(field.fieldType)" />
                  </el-icon>
                  <span class="field-label">{{ field.label }}</span>
                  <span class="field-code">{{ field.fieldCode }}</span>
                </div>
                <div class="field-actions">
                  <el-icon @click.stop="moveFieldInSlot(slot.id, index, 'up')" v-if="index > 0" title="上移">
                    <Top />
                  </el-icon>
                  <el-icon
                    @click.stop="moveFieldInSlot(slot.id, index, 'down')"
                    v-if="index < (slotFields[slot.id]?.length || 0) - 1"
                    title="下移"
                  >
                    <Bottom />
                  </el-icon>
                  <el-icon @click.stop="removeFieldFromSlot(slot.id, field.id)" title="删除">
                    <Delete />
                  </el-icon>
                </div>
              </div>

              <!-- 拖放区域 -->
              <div
                class="drop-zone"
                :class="{ 'drop-zone-active': activeSlotId === slot.id }"
                @dragover.prevent
                @drop="handleDropToSlot($event, slot.id)"
              >
                <el-icon class="drop-icon"><Plus /></el-icon>
                <span class="drop-hint">拖拽组件到此处</span>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="templateSlots.length === 0" class="empty-slots">
            <el-icon :size="48"><Warning /></el-icon>
            <p>当前模板没有定义槽位</p>
          </div>
        </div>

        <!-- 兼容旧版：没有模板时显示通用表单预览 -->
        <div v-else class="design-area">
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
            <el-form label-width="100px" size="small">
              <el-form-item label="表单名称">
                <el-input v-model="formConfig.formName" placeholder="请输入表单名称" />
              </el-form-item>
              <el-form-item label="表单编码">
                <el-input v-model="formConfig.formCode" placeholder="请输入表单编码" />
              </el-form-item>
              <el-form-item label="组件分类">
                <el-radio-group v-model="formConfig.componentCategory">
                  <el-radio label="common">通用组件库</el-radio>
                  <el-radio label="business">业务组件库</el-radio>
                </el-radio-group>
                <div class="form-tip">
                  通用组件可在所有页面复用，业务组件仅用于特定业务场景
                </div>
              </el-form-item>
              <el-form-item label="组件标签">
                <el-input
                  v-model="formConfig.componentTags"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入标签，用逗号分隔（如：system,user,create）"
                />
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
        </el-tabs>
      </div>
    </div>

    <!-- 从字段绑定加载对话框 -->
    <el-dialog
      v-model="bindingDialogVisible"
      title="从字段绑定加载"
      width="800px"
      destroy-on-close
    >
      <el-select
        v-model="selectedBindingTableId"
        placeholder="请选择数据表"
        filterable
        style="width: 100%; margin-bottom: 16px"
        @change="handleBindingTableChange"
      >
        <el-option
          v-for="table in bindingTableList"
          :key="table.id"
          :label="`${table.tableName} - ${table.tableComment || '无注释'}`"
          :value="table.id"
        />
      </el-select>

      <el-table
        :data="bindingFieldList"
        v-loading="bindingLoading"
        max-height="400"
        @selection-change="handleBindingSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="field.fieldName" label="字段名" width="120" />
        <el-table-column prop="field.fieldType" label="字段类型" width="100" />
        <el-table-column label="选择模板" width="160">
          <template #default="{ row }">
            <el-select v-model="row.selectedTemplate" placeholder="默认配置" clearable style="width: 100%">
              <el-option label="默认配置" :value="undefined" />
              <el-option
                v-for="template in row.templates"
                :key="template.id"
                :label="template.templateName"
                :value="template.templateCode || template.id?.toString()"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="config.widgetType" label="控件类型" width="100" />
        <el-table-column prop="field.fieldLabel" label="显示标签">
          <template #default="{ row }">
            {{ row.field.fieldLabel || row.field.fieldComment || '-' }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bindingDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleConfirmLoadBinding"
            :disabled="selectedBindingFields.length === 0"
          >
            加载选中 ({{ selectedBindingFields.length }})
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createForm, updateForm, getFormConfig, type FormConfig, type FormField as ApiFormField } from '@/api/form'
import {
  getTableList,
  getTableFields,
  type DbTable,
  type DbTableField
} from '@/api/db-table'
import {
  getConfigsByTableId,
  getTemplatesByFieldId,
  type DbFieldWidgetConfig,
  type DbFieldWidgetTemplate
} from '@/api/db-field-widget'
import { getTemplateByCode, type FormTemplate as ApiFormTemplate } from '@/api/form-template'
import {
  getTemplateByCode as getRegistryTemplate,
  getTemplateSlots,
  getTemplateMeta,
  formTemplateRegistry,
  type FormTemplateMeta,
  type FormSlotMeta
} from '@/components/templates/TemplateRegistry'

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
  slotId?: string  // 所属槽位ID
  sortOrder?: number
  options?: { label: string; value: any }[]
}

// 字段绑定相关类型
interface BindingFieldItem {
  field: DbTableField
  config: DbFieldWidgetConfig
  templates: DbFieldWidgetTemplate[]
  selectedTemplate?: string
}

// 按钮相关类型已移除 - 按钮现在从组件库选择

const route = useRoute()
const router = useRouter()
const formId = ref<number | null>(route.query.id ? Number(route.query.id) : null)
const templateCode = ref<string>(route.query.template as string || '')

const activeTab = ref('field')
const selectedField = ref<FormField | null>(null)
const formFields = ref<FormField[]>([])  // 兼容旧代码，实际使用 slotFields
const loading = ref(false)

// 新增：模板相关状态
const currentTemplate = ref<FormTemplateMeta | null>(null)
const templateSlots = ref<FormSlotMeta[]>([])
const slotFields = ref<Record<string, FormField[]>>({})
const expandedSlots = ref<Set<string>>(new Set())
const activeSlotId = ref<string | null>(null)  // 当前选中的槽位（用于拖放目标）

const formConfig = reactive({
  formName: '',
  formCode: '',
  componentCategory: 'business' as 'common' | 'business',
  componentTags: '' as string,
  labelWidth: 100,
  labelPosition: 'right' as 'right' | 'left' | 'top',
  templateCode: templateCode.value  // 记录当前使用的模板
})

// 字段绑定相关状态
const bindingDialogVisible = ref(false)
const bindingLoading = ref(false)
const bindingTableList = ref<DbTable[]>([])
const selectedBindingTableId = ref<number | null>(null)
const bindingFieldList = ref<BindingFieldItem[]>([])
const selectedBindingFields = ref<BindingFieldItem[]>([])

// 移除按钮配置相关代码 - 按钮现在从组件库选择

// 加载表单配置(编辑模式)
const loadFormConfig = async () => {
  if (!formId.value) return

  loading.value = true
  try {
    const data = await getFormConfig(formId.value)

    // 加载表单基本信息
    formConfig.formName = data.formName
    formConfig.formCode = data.formCode
    formConfig.componentCategory = (data.componentCategory as any) || 'business'
    formConfig.componentTags = (data.componentTags as any) || ''
    formConfig.labelWidth = data.labelWidth || 100
    formConfig.labelPosition = (data.labelPosition || 'right') as 'right' | 'left' | 'top'

    // 获取模板编码
    const tmplCode = data.templateCode || templateCode.value
    formConfig.templateCode = tmplCode
    templateCode.value = tmplCode

    // 加载模板定义
    if (tmplCode) {
      const templateMeta = getTemplateMeta(tmplCode)
      if (templateMeta) {
        currentTemplate.value = templateMeta
        templateSlots.value = templateMeta.fieldSlots
        // 初始化 slot 字段数组
        templateMeta.fieldSlots.forEach(slot => {
          slotFields.value[slot.id] = []
        })
        // 默认展开第一个 slot
        if (templateMeta.fieldSlots.length > 0) {
          expandedSlots.value.add(templateMeta.fieldSlots[0].id)
        }
      }
    }

    // 加载表单字段（按 slot 分组）
    if (data.fields && data.fields.length > 0) {
      // 先填充到 formFields（兼容旧代码）
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
        slotId: field.slotId || undefined,
        options: field.optionsJson ? JSON.parse(field.optionsJson) : undefined
      }))

      // 按 slot 分组
      formFields.value.forEach(field => {
        const slotId = field.slotId || 'default'
        if (!slotFields.value[slotId]) {
          slotFields.value[slotId] = []
        }
        slotFields.value[slotId].push(field)
      })
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
onMounted(async () => {
  // 如果有模板编码，先加载模板定义
  if (templateCode.value) {
    const templateMeta = getTemplateMeta(templateCode.value)
    if (templateMeta) {
      currentTemplate.value = templateMeta
      templateSlots.value = templateMeta.fieldSlots
      // 初始化每个 slot 的字段数组
      templateMeta.fieldSlots.forEach(slot => {
        slotFields.value[slot.id] = []
      })
      // 默认展开第一个 slot
      if (templateMeta.fieldSlots.length > 0) {
        expandedSlots.value.add(templateMeta.fieldSlots[0].id)
      }
    }
  }

  // 如果是编辑模式，加载表单配置
  if (formId.value) {
    await loadFormConfig()
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

// ============ Slot 相关操作 ============
// 切换 slot 展开状态
const toggleSlot = (slotId: string) => {
  if (expandedSlots.value.has(slotId)) {
    expandedSlots.value.delete(slotId)
  } else {
    expandedSlots.value.add(slotId)
  }
}

// 选择 slot（激活拖放目标）
const selectSlot = (slotId: string) => {
  activeSlotId.value = slotId
  // 同时展开该 slot
  expandedSlots.value.add(slotId)
}

// 获取 slot 中的字段数量
const getSlotFieldCount = (slotId: string) => {
  return slotFields.value[slotId]?.length || 0
}

// ============ 组件拖放操作 ============
const handleDragStart = (e: DragEvent, component: any) => {
  dragComponent = component
}

// 处理拖放到指定 slot
const handleDropToSlot = (e: DragEvent, slotId: string) => {
  e.preventDefault()
  e.stopPropagation()
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
    slotId: slotId,  // 记录所属 slot
    options: ['select', 'radio', 'checkbox'].includes(dragComponent.type)
      ? [
          { label: '选项1', value: '1' },
          { label: '选项2', value: '2' }
        ]
      : undefined
  }

  if (!slotFields.value[slotId]) {
    slotFields.value[slotId] = []
  }
  slotFields.value[slotId].push(newField)

  // 同步到 formFields（兼容旧代码）
  formFields.value.push(newField)

  selectedField.value = newField
  activeTab.value = 'field'
  dragComponent = null
}

// 移除 slot 中的字段
const removeFieldFromSlot = (slotId: string, fieldId: string) => {
  const fields = slotFields.value[slotId]
  if (!fields) return

  const index = fields.findIndex(f => f.id === fieldId)
  if (index > -1) {
    fields.splice(index, 1)
  }

  // 同步从 formFields 中移除
  const formIndex = formFields.value.findIndex(f => f.id === fieldId)
  if (formIndex > -1) {
    formFields.value.splice(formIndex, 1)
  }

  if (selectedField.value?.id === fieldId) {
    selectedField.value = null
  }
}

// 在 slot 中移动字段位置
const moveFieldInSlot = (slotId: string, index: number, direction: 'up' | 'down') => {
  const fields = slotFields.value[slotId]
  if (!fields) return

  const targetIndex = direction === 'up' ? index - 1 : index + 1
  if (targetIndex < 0 || targetIndex >= fields.length) return

  const temp = fields[index]
  fields[index] = fields[targetIndex]
  fields[targetIndex] = temp
}

// 旧版拖放（兼容没有模板的情况）
const handleDrop = () => {
  if (!dragComponent) return

  // 如果有模板且有当前选中的 slot，拖放到该 slot
  if (currentTemplate.value && activeSlotId.value) {
    handleDropToSlot(new DragEvent('drop'), activeSlotId.value)
    return
  }

  // 否则使用默认行为（拖放到第一个 slot 或默认位置）
  const targetSlotId = currentTemplate.value?.fieldSlots[0]?.id || 'default'
  handleDropToSlot(new DragEvent('drop'), targetSlotId)
}

// 根据字段类型获取图标
const getFieldIcon = (fieldType: string) => {
  const iconMap: Record<string, string> = {
    input: 'Edit',
    textarea: 'Document',
    select: 'ArrowDown',
    radio: 'CircleCheck',
    checkbox: 'Check',
    switch: 'Switch',
    date: 'Calendar',
    datetime: 'Timer',
    upload: 'Upload',
    cascader: 'Share'
  }
  return iconMap[fieldType] || 'Edit'
}

const selectField = (field: FormField) => {
  selectedField.value = field
  activeTab.value = 'field'
  // 如果字段有 slotId，选中该 slot
  if (field.slotId) {
    activeSlotId.value = field.slotId
  }
}

const removeField = (index: number) => {
  const field = formFields.value[index]
  if (!field) return

  // 从 slot 中移除
  if (field.slotId && slotFields.value[field.slotId]) {
    const slotIndex = slotFields.value[field.slotId].findIndex(f => f.id === field.id)
    if (slotIndex > -1) {
      slotFields.value[field.slotId].splice(slotIndex, 1)
    }
  }

  // 从 formFields 中移除
  formFields.value.splice(index, 1)

  if (selectedField.value?.id === field.id) {
    selectedField.value = null
  }
}

const moveUp = (index: number) => {
  if (index > 0) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index - 1]
    formFields.value[index - 1] = temp

    // 同时更新 slot 中的顺序
    if (temp.slotId) {
      moveFieldInSlot(temp.slotId, index, 'up')
    }
  }
}

const moveDown = (index: number) => {
  if (index < formFields.value.length - 1) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index + 1]
    formFields.value[index + 1] = temp

    // 同时更新 slot 中的顺序
    if (temp.slotId) {
      moveFieldInSlot(temp.slotId, index, 'down')
    }
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
  if (!formConfig.formName) {
    ElMessage.warning('请输入表单名称')
    return
  }
  if (!formConfig.formCode) {
    ElMessage.warning('请输入表单编码')
    return
  }

  // 计算所有字段总数（包括所有 slot 中的字段）
  const totalFields = formFields.value.length
  if (totalFields === 0) {
    ElMessage.warning('请添加至少一个字段')
    return
  }

  loading.value = true
  try {
    // 构建保存数据 - 扁平化所有 slot 的字段
    const allFields: ApiFormField[] = []
    let globalSortOrder = 0

    // 按顺序遍历每个 slot，收集字段
    templateSlots.value.forEach(slot => {
      const fields = slotFields.value[slot.id] || []
      fields.forEach((field) => {
        allFields.push({
          fieldName: field.label,
          fieldCode: field.fieldCode,
          fieldType: field.fieldType,
          label: field.label,
          placeholder: field.placeholder,
          defaultValue: field.defaultValue,
          required: field.required || false,
          disabled: field.disabled || false,
          visible: true,
          sortOrder: globalSortOrder++,
          span: field.span || 12,
          slotId: field.slotId || slot.id,  // 保存 slot 关联
          optionsJson: field.options ? JSON.stringify(field.options) : undefined
        })
      })
    })

    const saveData: FormConfig = {
      formName: formConfig.formName,
      formCode: formConfig.formCode,
      templateCode: formConfig.templateCode,  // 保存模板编码
      componentCategory: formConfig.componentCategory,
      componentTags: formConfig.componentTags,
      labelWidth: formConfig.labelWidth,
      labelPosition: formConfig.labelPosition,
      layoutType: 'grid',
      status: true,
      fields: allFields
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
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 从字段绑定加载
const handleLoadFromBinding = async () => {
  bindingDialogVisible.value = true
  if (bindingTableList.value.length === 0) {
    bindingLoading.value = true
    try {
      const result = await getTableList({ current: 1, size: 1000 })
      bindingTableList.value = result.records
    } catch (error) {
      ElMessage.error('加载表列表失败')
      console.error(error)
    } finally {
      bindingLoading.value = false
    }
  }
}

// 字段绑定表切换
const handleBindingTableChange = async () => {
  if (!selectedBindingTableId.value) {
    bindingFieldList.value = []
    return
  }

  bindingLoading.value = true
  try {
    const [fields, configs] = await Promise.all([
      getTableFields(selectedBindingTableId.value),
      getConfigsByTableId(selectedBindingTableId.value)
    ])

    // 创建配置映射
    const configMap = new Map<number, DbFieldWidgetConfig>()
    configs.forEach(c => {
      if (c.fieldId) configMap.set(c.fieldId, c)
    })

    // 组合数据，只显示已配置绑定的字段，并加载每个字段的模板
    const fieldsWithConfig = fields.filter(field => configMap.has(field.id!))

    bindingFieldList.value = await Promise.all(
      fieldsWithConfig.map(async field => {
        const templates = await getTemplatesByFieldId(field.id!)
        return {
          field,
          config: configMap.get(field.id!)!,
          templates,
          selectedTemplate: undefined
        }
      })
    )

    if (bindingFieldList.value.length === 0) {
      ElMessage.warning('该表暂无字段绑定配置，请先在"字段-控件绑定"页面配置')
    }
  } catch (error) {
    ElMessage.error('加载字段配置失败')
    console.error(error)
  } finally {
    bindingLoading.value = false
  }
}

// 字段绑定选择变化
const handleBindingSelectionChange = (selection: BindingFieldItem[]) => {
  selectedBindingFields.value = selection
}

// 确认加载字段绑定
const handleConfirmLoadBinding = () => {
  if (selectedBindingFields.value.length === 0) {
    ElMessage.warning('请至少选择一个字段')
    return
  }

  // 转换为表单字段
  const newFields: FormField[] = selectedBindingFields.value.map(item => {
    // 如果选择了模板，使用模板的配置
    let configToUse = item.config

    if (item.selectedTemplate) {
      const template = item.templates.find(t =>
        t.templateCode === item.selectedTemplate || t.id?.toString() === item.selectedTemplate
      )
      if (template) {
        configToUse = {
          widgetType: template.widgetType,
          widgetConfig: template.widgetConfig
        } as any
      }
    }

    const widgetConfig = JSON.parse(configToUse.widgetConfig || '{}')

    return {
      id: Date.now().toString() + Math.random(),
      fieldType: configToUse.widgetType,
      fieldCode: item.field.fieldName,
      label: widgetConfig.label || item.field.fieldLabel || item.field.fieldComment || item.field.fieldName,
      placeholder: widgetConfig.placeholder,
      defaultValue: widgetConfig.defaultValue || '',
      required: widgetConfig.required || false,
      disabled: widgetConfig.disabled || false,
      span: widgetConfig.span || 12,
      options: widgetConfig.options
    }
  })

  // 添加到表单字段列表
  formFields.value.push(...newFields)

  ElMessage.success(`成功加载 ${newFields.length} 个字段`)
  bindingDialogVisible.value = false
  selectedBindingFields.value = []
  selectedBindingTableId.value = null
  bindingFieldList.value = []
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

      // Slot 树形容器
      .slot-tree-container {
        flex: 1;
        padding: 16px;
        overflow-y: auto;
        background-color: #f9fafc;

        .slot-node {
          margin-bottom: 12px;
          background-color: #fff;
          border: 1px solid #e6e6e6;
          border-radius: 6px;
          overflow: hidden;
          transition: all 0.2s;

          &.slot-active {
            border-color: #409eff;
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
          }

          .slot-header {
            display: flex;
            align-items: center;
            padding: 10px 12px;
            cursor: pointer;
            user-select: none;
            border-bottom: 1px solid transparent;
            transition: all 0.2s;

            &:hover {
              background-color: #f5f7fa;
            }

            .expand-icon {
              margin-right: 6px;
              font-size: 12px;
              color: #909399;
              transition: transform 0.2s;

              &.is-expanded {
                transform: rotate(90deg);
              }
            }

            .slot-name {
              flex: 1;
              font-size: 13px;
              font-weight: 500;
              color: #303133;
            }

            .field-count {
              font-size: 12px;
              color: #909399;
            }
          }

          .slot-content {
            padding: 8px;
            background-color: #fafafa;

            .field-item {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 8px 10px;
              margin-bottom: 6px;
              background-color: #fff;
              border: 1px solid #e6e6e6;
              border-radius: 4px;
              cursor: pointer;
              transition: all 0.2s;

              &:hover {
                border-color: #409eff;
              }

              &.field-active {
                border-color: #409eff;
                background-color: #ecf5ff;
              }

              .field-info {
                display: flex;
                align-items: center;
                flex: 1;
                min-width: 0;

                .field-icon {
                  margin-right: 8px;
                  color: #409eff;
                  font-size: 16px;
                }

                .field-label {
                  font-size: 13px;
                  color: #303133;
                  margin-right: 8px;
                }

                .field-code {
                  font-size: 11px;
                  color: #909399;
                }
              }

              .field-actions {
                display: none;
                align-items: center;

                .el-icon {
                  margin-left: 4px;
                  color: #909399;
                  cursor: pointer;
                  font-size: 14px;

                  &:hover {
                    color: #409eff;
                  }
                }
              }

              &:hover .field-actions {
                display: flex;
              }
            }

            .drop-zone {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              padding: 16px;
              margin-top: 8px;
              border: 1px dashed #dcdfe6;
              border-radius: 4px;
              background-color: #fff;
              transition: all 0.2s;

              &:hover,
              &.drop-zone-active {
                border-color: #409eff;
                background-color: #ecf5ff;
              }

              .drop-icon {
                font-size: 24px;
                color: #c0c4cc;
                margin-bottom: 4px;
              }

              .drop-hint {
                font-size: 12px;
                color: #909399;
              }
            }
          }
        }

        .empty-slots {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 200px;
          color: #909399;

          p {
            margin-top: 12px;
          }
        }
      }

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

    }
  }
}
</style>
