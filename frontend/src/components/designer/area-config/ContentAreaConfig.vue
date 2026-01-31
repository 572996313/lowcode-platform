<template>
  <div class="area-config content-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>基本配置</span>
      </div>
      <div class="section-body">
        <el-form-item label="标题">
          <el-input v-model="localConfig.title" placeholder="内容区标题" />
        </el-form-item>

        <el-form-item label="组件类型">
          <el-radio-group v-model="localConfig.componentType">
            <el-radio value="table">表格</el-radio>
            <el-radio value="form">表单</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="配置ID">
          <el-select
            v-model="localConfig.configId"
            placeholder="请选择配置"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in configOptions"
              :key="item.id"
              :label="item.configName"
              :value="item.id"
            />
          </el-select>
          <div class="form-tip">
            {{ localConfig.componentType === 'table' ? '选择表格配置' : '选择表单配置' }}
          </div>
        </el-form-item>

        <el-form-item label="显示工具栏">
          <el-switch v-model="localConfig.showToolbar" />
        </el-form-item>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import type { NormalizedArea } from '@/api/page'
import { getTableList } from '@/api/table'
import { getFormList } from '@/api/form'

interface Props {
  area: NormalizedArea
}

interface Emits {
  (e: 'update', area: NormalizedArea): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 配置选项
const configOptions = ref<any[]>([])

// 本地配置
const localConfig = computed({
  get: () => props.area.config || {
    title: '数据列表',
    componentType: 'table',
    configId: null,
    showToolbar: true
  },
  set: (val) => {
    emit('update', { ...props.area, config: val })
  }
})

// 监听组件类型变化，加载对应配置
watch(() => localConfig.value.componentType, async (newType) => {
  if (newType === 'table') {
    loadTableConfigs()
  } else if (newType === 'form') {
    loadFormConfigs()
  }
})

// 加载表格配置
const loadTableConfigs = async () => {
  try {
    const res = await getTableList({ current: 1, size: 100 })
    configOptions.value = res.records || res || []
  } catch (error) {
    console.error('加载表格配置失败:', error)
  }
}

// 加载表单配置
const loadFormConfigs = async () => {
  try {
    const res = await getFormList({ current: 1, size: 100 })
    configOptions.value = res.records || res || []
  } catch (error) {
    console.error('加载表单配置失败:', error)
  }
}

onMounted(() => {
  if (localConfig.value.componentType === 'table') {
    loadTableConfigs()
  } else if (localConfig.value.componentType === 'form') {
    loadFormConfigs()
  }
})
</script>

<style scoped lang="scss">
.area-config {
  .config-section {
    margin-bottom: 20px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    overflow: hidden;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 15px;
      background: var(--el-fill-color-light);
      border-bottom: 1px solid var(--el-border-color);
      font-weight: 500;
    }

    .section-body {
      padding: 15px;
    }
  }

  .form-tip {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }
}
</style>
