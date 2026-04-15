<template>
  <div class="array-editor">
    <div class="editor-header">
      <span class="editor-title">表单分组</span>
      <el-button type="primary" size="small" @click="handleAddGroup">+ 添加分组</el-button>
    </div>

    <div class="editor-list">
      <div v-for="(group, gi) in modelValue" :key="gi" class="group-block">
        <div class="group-header">
          <span class="group-index">分组 {{ gi + 1 }}</span>
          <el-input v-model="group.title" placeholder="分组标题（留空则不显示标题）" size="small" style="flex: 1; margin: 0 8px" />
          <div class="group-actions">
            <el-button size="small" :icon="ArrowUp" circle :disabled="gi === 0" @click="moveGroupUp(gi)" />
            <el-button size="small" :icon="ArrowDown" circle :disabled="gi === modelValue.length - 1" @click="moveGroupDown(gi)" />
            <el-button size="small" type="danger" :icon="Delete" circle @click="removeGroup(gi)" />
          </div>
        </div>

        <!-- 分组属性 -->
        <div class="group-config">
          <el-form size="small" inline>
            <el-form-item label="描述">
              <el-input v-model="group.description" placeholder="可选" style="width: 150px" />
            </el-form-item>
            <el-form-item label="可折叠">
              <el-switch v-model="group.collapsible" />
            </el-form-item>
            <el-form-item v-if="group.collapsible" label="默认折叠">
              <el-switch v-model="group.defaultCollapsed" />
            </el-form-item>
          </el-form>
        </div>

        <!-- 分组内字段列表 -->
        <div class="group-fields">
          <div v-for="(field, fi) in group.fields" :key="fi" class="field-item">
            <span class="field-label">{{ field.label || field.field }}</span>
            <el-tag size="small" type="info">{{ field.type }}</el-tag>
            <div class="field-actions">
              <el-button size="small" :icon="ArrowUp" circle :disabled="fi === 0" @click="moveField(group.fields, fi, -1)" />
              <el-button size="small" :icon="ArrowDown" circle :disabled="fi === group.fields.length - 1" @click="moveField(group.fields, fi, 1)" />
              <el-button size="small" type="primary" :icon="Edit" circle @click="editField(group.fields, fi)" />
              <el-button size="small" type="danger" :icon="Delete" circle @click="group.fields.splice(fi, 1)" />
            </div>
          </div>
          <el-button size="small" @click="addField(group)">+ 添加字段</el-button>
        </div>
      </div>

      <el-empty v-if="!modelValue?.length" description="暂无分组，点击上方添加" :image-size="60" />
    </div>

    <!-- 字段编辑弹窗 -->
    <el-dialog v-model="fieldDialogVisible" :title="fieldDialogTitle" width="520px" append-to-body>
      <el-form label-width="80px" size="default">
        <el-form-item label="字段名">
          <el-input v-model="editFieldData.field" placeholder="字段标识" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="editFieldData.label" placeholder="显示标签" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editFieldData.type" style="width: 100%">
            <el-option label="输入框" value="input" />
            <el-option label="文本域" value="textarea" />
            <el-option label="数字" value="number" />
            <el-option label="下拉选择" value="select" />
            <el-option label="单选" value="radio" />
            <el-option label="多选" value="checkbox" />
            <el-option label="开关" value="switch" />
            <el-option label="日期" value="date" />
            <el-option label="日期时间" value="datetime" />
            <el-option label="文件上传" value="upload" />
          </el-select>
        </el-form-item>
        <el-form-item label="占位文本">
          <el-input v-model="editFieldData.placeholder" placeholder="提示文本" />
        </el-form-item>
        <el-form-item label="栅格占比">
          <el-input-number v-model="editFieldData.span" :min="1" :max="24" controls-position="right" />
          <span class="field-hint">（默认根据列数自动计算，24=整行）</span>
        </el-form-item>
        <el-form-item label="必填">
          <el-switch v-model="editFieldData.required" />
        </el-form-item>
        <el-form-item label="编辑禁用">
          <el-switch v-model="editFieldData.disabledOnEdit" />
        </el-form-item>
        <el-form-item label="提示文本">
          <el-input v-model="editFieldData.tooltip" placeholder="字段下方的说明文字" />
        </el-form-item>

        <!-- textarea -->
        <el-form-item v-if="editFieldData.type === 'textarea'" label="行数">
          <el-input-number v-model="editFieldData.rows" :min="1" :max="20" controls-position="right" />
        </el-form-item>

        <!-- number -->
        <template v-if="editFieldData.type === 'number'">
          <el-form-item label="最小值">
            <el-input-number v-model="editFieldData.min" controls-position="right" />
          </el-form-item>
          <el-form-item label="最大值">
            <el-input-number v-model="editFieldData.max" controls-position="right" />
          </el-form-item>
          <el-form-item label="步长">
            <el-input-number v-model="editFieldData.step" :min="0.01" controls-position="right" />
          </el-form-item>
        </template>

        <!-- switch -->
        <template v-if="editFieldData.type === 'switch'">
          <el-form-item label="开启值">
            <el-input v-model="editFieldData.activeValue" placeholder="默认 true" />
          </el-form-item>
          <el-form-item label="关闭值">
            <el-input v-model="editFieldData.inactiveValue" placeholder="默认 false" />
          </el-form-item>
        </template>

        <!-- upload -->
        <template v-if="editFieldData.type === 'upload'">
          <el-form-item label="文件类型">
            <el-input v-model="editUploadConfig.accept" placeholder=".jpg,.png,.pdf" />
          </el-form-item>
          <el-form-item label="数量限制">
            <el-input-number v-model="editUploadConfig.limit" :min="1" :max="20" controls-position="right" />
          </el-form-item>
          <el-form-item label="大小限制">
            <el-input-number v-model="editUploadConfig.maxSize" :min="1" :max="100" controls-position="right" />
            <span class="field-hint">MB</span>
          </el-form-item>
        </template>

        <!-- select/radio/checkbox 选项 -->
        <template v-if="['select', 'radio', 'checkbox'].includes(editFieldData.type)">
          <el-divider content-position="left">选项列表</el-divider>
          <el-form-item label="选项">
            <div class="options-editor">
              <div v-for="(opt, oi) in editOptions" :key="oi" class="option-row">
                <el-input v-model="opt.label" placeholder="标签" size="small" style="width: 120px" />
                <el-input v-model="opt.value" placeholder="值" size="small" style="width: 120px" />
                <el-button size="small" type="danger" :icon="Delete" circle @click="editOptions.splice(oi, 1)" />
              </div>
              <el-button size="small" @click="editOptions.push({ label: '', value: '' })">+ 添加选项</el-button>
            </div>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="fieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFieldEdit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ArrowUp, ArrowDown, Edit, Delete } from '@element-plus/icons-vue'
import type { FormGroupConfig, FormPageFieldConfig } from '@/api/form-standard'

const props = defineProps<{
  modelValue: FormGroupConfig[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: FormGroupConfig[]): void
}>()

// ---- 分组操作 ----

const handleAddGroup = () => {
  const arr = [...props.modelValue]
  arr.push({ title: '', fields: [] })
  emit('update:modelValue', arr)
}

const removeGroup = (index: number) => {
  const arr = [...props.modelValue]
  arr.splice(index, 1)
  emit('update:modelValue', arr)
}

const moveGroupUp = (index: number) => {
  if (index <= 0) return
  const arr = [...props.modelValue]
  ;[arr[index - 1], arr[index]] = [arr[index], arr[index - 1]]
  emit('update:modelValue', arr)
}

const moveGroupDown = (index: number) => {
  if (index >= props.modelValue.length - 1) return
  const arr = [...props.modelValue]
  ;[arr[index], arr[index + 1]] = [arr[index + 1], arr[index]]
  emit('update:modelValue', arr)
}

// ---- 字段操作 ----

const moveField = (fields: any[], index: number, dir: number) => {
  const target = index + dir
  if (target < 0 || target >= fields.length) return
  ;[fields[index], fields[target]] = [fields[target], fields[index]]
  emit('update:modelValue', [...props.modelValue])
}

const addField = (group: FormGroupConfig) => {
  editingFields = group.fields
  editFieldIndex = -1
  Object.assign(editFieldData, {
    field: 'newField', label: '新字段', type: 'input',
    placeholder: '', required: false, disabledOnEdit: false,
    span: undefined, rows: undefined, min: undefined, max: undefined, step: undefined,
    tooltip: '', activeValue: undefined, inactiveValue: undefined
  })
  editOptions.length = 0
  editUploadConfig.accept = ''
  editUploadConfig.limit = 5
  editUploadConfig.maxSize = 10
  fieldDialogVisible.value = true
}

const editField = (fields: FormPageFieldConfig[], index: number) => {
  editingFields = fields
  editFieldIndex = index
  const src = fields[index]
  Object.assign(editFieldData, {
    field: src.field, label: src.label, type: src.type,
    placeholder: src.placeholder || '', required: src.required || false,
    disabledOnEdit: src.disabledOnEdit || false, span: src.span,
    rows: src.rows, min: src.min, max: src.max, step: src.step,
    tooltip: src.tooltip || '', activeValue: src.activeValue,
    inactiveValue: src.inactiveValue
  })
  editOptions.length = 0
  if (src.options) editOptions.push(...src.options.map(o => ({ ...o })))
  editUploadConfig.accept = src.uploadConfig?.accept || ''
  editUploadConfig.limit = src.uploadConfig?.limit || 5
  editUploadConfig.maxSize = src.uploadConfig?.maxSize || 10
  fieldDialogVisible.value = true
}

let editingFields: any[] = []
const editFieldIndex = ref(-1)
const fieldDialogVisible = ref(false)
const fieldDialogTitle = ref('添加字段')
const editFieldData = reactive<any>({})
const editOptions = reactive<any[]>([])
const editUploadConfig = reactive<any>({ accept: '', limit: 5, maxSize: 10 })

const confirmFieldEdit = () => {
  const data: any = { ...editFieldData }

  // 选项
  if (['select', 'radio', 'checkbox'].includes(data.type)) {
    data.options = editOptions.filter(o => o.label || o.value)
  }

  // 上传配置
  if (data.type === 'upload') {
    data.uploadConfig = { ...editUploadConfig }
  }

  if (editFieldIndex.value === -1) {
    editingFields.push(data)
  } else {
    Object.assign(editingFields[editFieldIndex.value], data)
  }
  emit('update:modelValue', [...props.modelValue])
  fieldDialogVisible.value = false
}
</script>

<style scoped lang="scss">
.array-editor {
  .editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .editor-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.group-block {
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;

  .group-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .group-index {
      font-size: 13px;
      font-weight: 600;
      color: #409eff;
      white-space: nowrap;
    }

    .group-actions {
      display: flex;
      gap: 4px;
      flex-shrink: 0;
    }
  }

  .group-config {
    margin-bottom: 8px;
    padding: 4px 0;
    border-bottom: 1px dashed #ebeef5;
  }

  .group-fields {
    .field-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 6px 10px;
      margin-bottom: 4px;
      background: #fff;
      border-radius: 4px;
      border: 1px solid #f0f0f0;

      .field-label {
        flex: 1;
        font-size: 13px;
        color: #303133;
      }

      .field-actions {
        display: flex;
        gap: 2px;
        flex-shrink: 0;
      }
    }
  }
}

.field-hint {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.options-editor {
  .option-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 6px;
  }
}
</style>
