<template>
  <ArrayEditor
    :model-value="modelValue"
    title="表单字段"
    dialog-title="编辑表单字段"
    :get-item-label="(f: any) => f.label || f.field || '字段'"
    :get-item-type="(f: any) => f.type || 'input'"
    :create-default="() => ({ field: 'newField', label: '新字段', type: 'input', placeholder: '' })"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #form="{ data }">
      <el-form label-width="80px">
        <el-form-item label="字段名">
          <el-input v-model="data.field" placeholder="字段名" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="data.label" placeholder="显示标签" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="data.type" style="width: 100%">
            <el-option label="输入框" value="input" />
            <el-option label="文本域" value="textarea" />
            <el-option label="数字" value="number" />
            <el-option label="下拉选择" value="select" />
            <el-option label="开关" value="switch" />
            <el-option label="日期" value="date" />
          </el-select>
        </el-form-item>
        <el-form-item label="占位文本">
          <el-input v-model="data.placeholder" placeholder="提示文本" />
        </el-form-item>
        <el-form-item label="必填">
          <el-switch v-model="data.required" />
        </el-form-item>
        <el-form-item label="编辑禁用">
          <el-switch v-model="data.disabledOnEdit" />
        </el-form-item>
        <el-form-item label="标签宽度">
          <el-input v-model="data.labelWidth" placeholder="如: 100px" />
        </el-form-item>
        <el-form-item v-if="data.type === 'textarea'" label="行数">
          <el-input-number v-model="data.rows" :min="1" :max="20" controls-position="right" />
        </el-form-item>
        <el-form-item v-if="data.type === 'switch'" label="开启值">
          <el-input v-model="data.activeValue" placeholder="默认 true" />
        </el-form-item>
        <el-form-item v-if="data.type === 'switch'" label="关闭值">
          <el-input v-model="data.inactiveValue" placeholder="默认 false" />
        </el-form-item>

        <!-- select 类型：选项编辑 -->
        <template v-if="data.type === 'select'">
          <el-divider content-position="left">选项列表</el-divider>
          <el-form-item label="选项">
            <div class="options-editor">
              <div v-for="(opt, oi) in (data.options || [])" :key="oi" class="option-row">
                <el-input v-model="opt.label" placeholder="标签" size="small" style="width: 120px" />
                <el-input v-model="opt.value" placeholder="值" size="small" style="width: 120px" />
                <el-button size="small" type="danger" :icon="Delete" circle @click="data.options.splice(oi, 1)" />
              </div>
              <el-button size="small" @click="addOption(data)">+ 添加选项</el-button>
            </div>
          </el-form-item>
        </template>

        <!-- 校验规则 -->
        <el-divider content-position="left">校验规则</el-divider>
        <el-form-item label="自定义规则">
          <div class="rules-editor">
            <div v-for="(rule, ri) in (data.rules || [])" :key="ri" class="rule-row">
              <el-switch v-model="rule.required" size="small" active-text="必填" />
              <el-input v-model="rule.message" placeholder="提示信息" size="small" style="flex: 1" />
              <el-input v-model="rule.pattern" placeholder="正则" size="small" style="width: 100px" />
              <el-button size="small" type="danger" :icon="Delete" circle @click="data.rules.splice(ri, 1)" />
            </div>
            <el-button size="small" @click="addRule(data)">+ 添加规则</el-button>
          </div>
        </el-form-item>
      </el-form>
    </template>
  </ArrayEditor>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'
import ArrayEditor from './ArrayEditor.vue'
import type { FormFieldConfig } from '@/api/table-standard'

defineProps<{
  modelValue: FormFieldConfig[]
}>()

defineEmits<{
  (e: 'update:modelValue', value: FormFieldConfig[]): void
}>()

const addOption = (data: any) => {
  if (!data.options) data.options = []
  data.options.push({ label: '', value: '' })
}

const addRule = (data: any) => {
  if (!data.rules) data.rules = []
  data.rules.push({ message: '', trigger: 'blur' })
}
</script>

<style scoped lang="scss">
.options-editor {
  .option-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 6px;
  }
}

.rules-editor {
  .rule-row {
    display: flex;
    gap: 6px;
    align-items: center;
    margin-bottom: 6px;
  }
}
</style>
