<template>
  <div class="standard-list-render">
    <!-- 查询区 -->
    <div v-if="hasComponent('search')" class="search-area">
      <el-card shadow="never">
        <template v-if="getComponent('search')?.type === 'form'">
          <div class="form-placeholder">
            <el-icon><Document /></el-icon>
            <span>{{ getComponentName('search') }}</span>
          </div>
        </template>
      </el-card>
    </div>

    <!-- 工具栏 -->
    <div v-if="hasComponent('toolbar')" class="toolbar-area">
      <el-button
        v-for="btn in getComponents('toolbar')"
        :key="btn.id"
        :type="btn.props?.type || 'default'"
      >
        {{ btn.name }}
      </el-button>
    </div>

    <!-- 内容区 -->
    <div class="content-area">
      <el-card shadow="never">
        <template v-if="getComponent('content')?.type === 'table'">
          <div class="table-placeholder">
            <el-icon><Grid /></el-icon>
            <span>{{ getComponentName('content') }}</span>
          </div>
        </template>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Document, Grid } from '@element-plus/icons-vue'
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

const getComponents = (areaId: string) => {
  return props.pageConfig.components?.[areaId] || []
}

const getComponentName = (areaId: string) => {
  return getComponent(areaId)?.name || '未命名组件'
}
</script>

<style scoped>
.standard-list-render {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.search-area {
  margin-bottom: 0;
}

.toolbar-area {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.content-area {
  margin-top: 0;
}

.form-placeholder,
.table-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #909399;
}

.form-placeholder .el-icon,
.table-placeholder .el-icon {
  font-size: 32px;
}
</style>
