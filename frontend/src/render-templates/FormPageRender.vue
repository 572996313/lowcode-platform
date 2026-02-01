<template>
  <div class="form-page-render">
    <!-- 表单内容区 -->
    <div class="content-area">
      <el-card shadow="never">
        <template v-if="getComponent('content')?.type === 'form'">
          <div class="form-placeholder">
            <el-icon><Document /></el-icon>
            <span>{{ getComponentName('content') }}</span>
          </div>
        </template>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Document } from '@element-plus/icons-vue'
import type { PageConfigData } from '@/types/template'

interface Props {
  pageConfig: PageConfigData
}

const props = defineProps<Props>()

const getComponent = (areaId: string) => {
  return props.pageConfig.components?.[areaId]?.[0]
}

const getComponentName = (areaId: string) => {
  return getComponent(areaId)?.name || '未命名组件'
}
</script>

<style scoped>
.form-page-render {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.form-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 60px;
  color: #909399;
}

.form-placeholder .el-icon {
  font-size: 32px;
}
</style>
