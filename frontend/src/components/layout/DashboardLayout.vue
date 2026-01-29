<template>
  <div class="layout-dashboard">
    <el-row :gutter="16">
      <el-col
        v-for="widget in widgets"
        :key="widget.id"
        :span="widget.span || 6"
      >
        <div class="widget-card">
          <div class="widget-header">
            <span class="widget-title">{{ widget.title }}</span>
          </div>
          <div class="widget-content">
            <div class="widget-value">{{ widget.value }}</div>
            <div v-if="widget.description" class="widget-description">
              {{ widget.description }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
interface WidgetConfig {
  id: string
  title: string
  value: string | number
  description?: string
  span?: number
}

interface Props {
  widgets: WidgetConfig[]
}

defineProps<Props>()
</script>

<style scoped>
.layout-dashboard {
  background: #f5f7fa;
  border-radius: 4px;
  padding: 16px;
}

.widget-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 16px;
  transition: box-shadow 0.3s;
}

.widget-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.widget-header {
  margin-bottom: 12px;
}

.widget-title {
  font-size: 14px;
  color: #909399;
}

.widget-content {
  text-align: center;
}

.widget-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.widget-description {
  font-size: 12px;
  color: #909399;
}
</style>
