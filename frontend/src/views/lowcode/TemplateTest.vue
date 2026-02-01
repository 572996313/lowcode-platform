<template>
  <div class="template-test-page">
    <el-page-header @back="goBack" title="模板测试">
      <template #content>
        <span style="font-size: 20px; font-weight: bold;">
          基于模板的可视化拖拽编辑（V5）
        </span>
      </template>
    </el-page-header>

    <div class="template-grid">
      <div
        v-for="template in TEMPLATES"
        :key="template.id"
        class="template-card"
        @click="selectTemplate(template)"
      >
        <div class="card-preview">
          <el-icon class="preview-icon"><Document /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">{{ template.name }}</div>
          <div class="card-desc">{{ template.description }}</div>
          <div class="card-meta">
            <el-tag size="small" type="info">{{ template.category }}</el-tag>
            <el-tag size="small" :type="getTypeColor(template.type)">
              {{ getTypeLabel(template.type) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import { TEMPLATES } from '@/config/templates/registry'

const router = useRouter()

const goBack = () => {
  router.back()
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    list: 'success',
    form: 'warning',
    tree: 'danger',
    custom: 'info'
  }
  return colorMap[type] || 'info'
}

const getTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    list: '列表页',
    form: '表单页',
    tree: '树形页',
    custom: '自定义'
  }
  return labelMap[type] || type
}

const selectTemplate = (template: any) => {
  router.push({
    path: '/lowcode/PageDesignerV5',
    query: {
      template: template.id
    }
  })
}
</script>

<style scoped>
.template-test-page {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.template-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.template-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
}

.card-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 180px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.preview-icon {
  font-size: 64px;
  color: #fff;
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.card-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 16px;
  min-height: 42px;
}

.card-meta {
  display: flex;
  gap: 8px;
}
</style>
