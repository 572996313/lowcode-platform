<template>
  <div class="layout-custom">
    <div
      v-for="component in components"
      :key="component.id"
      class="custom-component"
    >
      <!-- 自定义组件内容待实现 -->
      <div class="component-placeholder">
        <el-icon :size="32"><Grid /></el-icon>
        <div>{{ component.name || '未命名组件' }}</div>
        <div class="component-type">{{ component.type }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Grid } from '@element-plus/icons-vue'

interface CustomComponent {
  id: string
  type: string
  name?: string
  props?: Record<string, any>
}

interface Props {
  components: CustomComponent[]
}

defineProps<Props>()
</script>

<style scoped>
.layout-custom {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  min-height: 400px;
}

.custom-component {
  margin-bottom: 16px;
}

.component-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #f5f7fa;
  border-radius: 4px;
  color: #909399;
}

.component-type {
  margin-top: 8px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
