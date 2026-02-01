<template>
  <div class="form-render-design">
    <el-form :model="formData" inline>
      <el-form-item
        v-for="field in fields"
        :key="field.id"
        :label="field.fieldName"
      >
        <!-- 输入框 -->
        <el-input
          v-if="field.fieldType === 'input'"
          v-model="formData[field.fieldCode]"
          :placeholder="field.placeholder || `请输入${field.fieldName}`"
        />
        <!-- 下拉选择 -->
        <el-select
          v-else-if="field.fieldType === 'select'"
          v-model="formData[field.fieldCode]"
          :placeholder="`请选择${field.fieldName}`"
          clearable
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
          v-model="formData[field.fieldCode]"
          type="date"
          :placeholder="`请选择${field.fieldName}`"
        />
        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.fieldType === 'number'"
          v-model="formData[field.fieldCode]"
          :placeholder="`请输入${field.fieldName}`"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary">查询</el-button>
        <el-button>重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FormConfig } from '@/api/form'

interface Props {
  formConfig: FormConfig
  designMode?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  designMode: true
})

// 模拟字段数据
const fields = ref([
  { id: 1, fieldCode: 'name', fieldName: '名称', fieldType: 'input', placeholder: '请输入名称' },
  { id: 2, fieldCode: 'status', fieldName: '状态', fieldType: 'select', options: [{label: '启用', value: 1}, {label: '禁用', value: 0}] },
  { id: 3, fieldCode: 'createTime', fieldName: '创建时间', fieldType: 'date' }
])

const formData = ref<Record<string, any>>({})
</script>

<style scoped>
.form-render-design {
  padding: 12px;
}
</style>
