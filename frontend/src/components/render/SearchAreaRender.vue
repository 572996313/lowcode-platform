<template>
  <div class="search-area-render">
    <el-form :model="model" :inline="inline" :label-width="labelWidth">
      <el-row :gutter="gutter">
        <el-col
          v-for="field in fields"
          :key="field.id || field.fieldCode"
          :span="field.span || span"
        >
          <SearchFieldRender
            :field="field"
            :model="model"
            @change="handleChange"
          />
        </el-col>
        <el-col v-if="showActions" :span="actionSpan || 6">
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type { SearchField } from '@/api/page'
import SearchFieldRender from './SearchFieldRender.vue'

interface Props {
  fields: SearchField[]
  model: Record<string, any>
  inline?: boolean
  labelWidth?: string
  gutter?: number
  span?: number
  actionSpan?: number
  showActions?: boolean
}

interface Emits {
  (e: 'search'): void
  (e: 'reset'): void
  (e: 'change', fieldCode: string, value: any): void
}

const props = withDefaults(defineProps<Props>(), {
  inline: true,
  labelWidth: '100px',
  gutter: 20,
  span: 6,
  actionSpan: 6,
  showActions: true
})

const emit = defineEmits<Emits>()

const handleChange = (fieldCode: string, value: any) => {
  props.model[fieldCode] = value
  emit('change', fieldCode, value)
}

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  // 重置所有字段值
  props.fields.forEach(field => {
    delete props.model[field.fieldCode]
  })
  emit('reset')
}
</script>

<style scoped>
.search-area-render {
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}
</style>
