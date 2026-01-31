<template>
  <div class="area-config custom-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>基本配置</span>
      </div>
      <div class="section-body">
        <el-form-item label="允许的组件">
          <el-select
            v-model="localConfig.allowedComponents"
            multiple
            filterable
            allow-create
            placeholder="输入组件类型后回车添加"
            style="width: 100%"
          >
            <el-option label="表格" value="table" />
            <el-option label="表单" value="form" />
            <el-option label="图表" value="chart" />
            <el-option label="统计卡片" value="stats-card" />
            <el-option label="文本" value="text" />
            <el-option label="图片" value="image" />
            <el-option label="按钮组" value="button-group" />
          </el-select>
          <div class="form-tip">
            为空时表示允许所有组件类型
          </div>
        </el-form-item>

        <el-form-item label="布局方式">
          <el-radio-group v-model="localConfig.layout">
            <el-radio value="vertical">垂直</el-radio>
            <el-radio value="horizontal">水平</el-radio>
            <el-radio value="grid">网格</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="自定义样式">
          <el-input
            v-model="localConfig.customStyle"
            type="textarea"
            :rows="3"
            placeholder="输入自定义CSS样式"
          />
        </el-form-item>

        <el-form-item label="自定义类名">
          <el-input v-model="localConfig.customClass" placeholder="输入CSS类名" />
        </el-form-item>
      </div>
    </div>

    <div class="config-section" v-if="localConfig.layout === 'grid'">
      <div class="section-header">
        <span>网格配置</span>
      </div>
      <div class="section-body">
        <el-form-item label="列数">
          <el-input-number v-model="localConfig.columns" :min="1" :max="12" />
        </el-form-item>

        <el-form-item label="间距">
          <el-input-number v-model="localConfig.gap" :min="0" :max="100" />
          <span class="unit">px</span>
        </el-form-item>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { NormalizedArea } from '@/api/page'

interface Props {
  area: NormalizedArea
}

interface Emits {
  (e: 'update', area: NormalizedArea): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 本地配置
const localConfig = computed({
  get: () => props.area.config || {
    allowedComponents: [],
    layout: 'vertical',
    customStyle: '',
    customClass: '',
    columns: 3,
    gap: 16
  },
  set: (val) => {
    emit('update', { ...props.area, config: val })
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

  .unit {
    margin-left: 8px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }
}
</style>
