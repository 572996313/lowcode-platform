<template>
  <div class="area-config tree-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>基本配置</span>
      </div>
      <div class="section-body">
        <el-form-item label="标题">
          <el-input v-model="localConfig.title" placeholder="树形区标题" />
        </el-form-item>

        <el-form-item label="数据URL">
          <el-input v-model="localConfig.dataUrl" placeholder="API接口地址" />
        </el-form-item>

        <el-form-item label="标签字段">
          <el-input v-model="localConfig.labelField" placeholder="默认: name" />
        </el-form-item>

        <el-form-item label="子节点字段">
          <el-input v-model="localConfig.childrenField" placeholder="默认: children" />
        </el-form-item>

        <el-form-item label="ID字段">
          <el-input v-model="localConfig.idField" placeholder="默认: id" />
        </el-form-item>

        <el-form-item label="显示图标">
          <el-switch v-model="localConfig.showIcon" />
        </el-form-item>

        <el-form-item label="可搜索">
          <el-switch v-model="localConfig.filterable" />
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
    title: '分类树',
    dataUrl: '',
    labelField: 'name',
    childrenField: 'children',
    idField: 'id',
    showIcon: false,
    filterable: false
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
}
</style>
