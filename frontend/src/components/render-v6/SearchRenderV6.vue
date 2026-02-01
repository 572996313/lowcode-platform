<template>
  <div class="search-render-v6">
    <el-form :inline="true" :label-width="config.labelWidth + 'px'">
      <el-row :gutter="config.layout.gap">
        <el-col
          v-for="field in config.fields"
          :key="field.id"
          :span="field.span || config.layout.span"
        >
          <el-form-item :label="field.label" :required="field.required">
            <SearchFieldRender :field="field" v-model="searchValues[field.fieldCode]" />
          </el-form-item>
        </el-col>

        <el-col :span="6">
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
import { ref, watch } from 'vue'
import SearchFieldRender from './SearchFieldRender.vue'
import type { SearchConfig } from '@/types/page-v6'

// Props
interface Props {
  config: SearchConfig
}

// Emits
interface Emits {
  (e: 'search', params: any): void
  (e: 'reset'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 查询参数
const searchValues = ref<Record<string, any>>({})

/**
 * 查询
 */
function handleSearch() {
  emit('search', searchValues.value)
}

/**
 * 重置
 */
function handleReset() {
  searchValues.value = {}
  emit('reset')
}
</script>

<style scoped lang="scss">
.search-render-v6 {
  padding: 16px;
  margin-bottom: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
