<template>
  <div class="tree-table-render" style="display: flex; gap: 16px; padding: 16px;">
    <!-- 左侧树形区 -->
    <div v-if="hasComponent('tree')" class="tree-area">
      <el-card shadow="never">
        <div class="tree-placeholder">
          <el-icon><Operation /></el-icon>
          <span>{{ getComponentName('tree') }}</span>
        </div>
      </el-card>
    </div>

    <!-- 右侧内容区 -->
    <div class="content-area" style="flex: 1;">
      <el-card shadow="never">
        <template v-if="getComponent('content')?.type === 'table'">
          <div class="table-placeholder">
            <el-icon><Grid /></el-icon>
            <span>{{ getComponentName('content') }}</span>
          </div>
        </template>
        <template v-else-if="getComponent('content')?.type === 'form'">
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
import { Operation, Grid, Document } from '@element-plus/icons-vue'
import type { PageConfigData } from '@/types/template'

interface Props {
  pageConfig: PageConfigData
}

const props = defineProps<Props>()

const hasComponent = (areaId: string) => {
  return props.pageConfig.components?.[areaId]?.length > 0
}

const getComponent = (areaId: string) => {
  return props.pageConfig.components?.[areaId]?.[0]
}

const getComponentName = (areaId: string) => {
  return getComponent(areaId)?.name || '未命名组件'
}
</script>

<style scoped>
.tree-table-render {
  background-color: #fff;
  border-radius: 8px;
}

.tree-area {
  flex: 0 0 250px;
  min-width: 200px;
}

.tree-placeholder,
.form-placeholder,
.table-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  color: #909399;
}

.tree-placeholder .el-icon,
.form-placeholder .el-icon,
.table-placeholder .el-icon {
  font-size: 32px;
}
</style>
