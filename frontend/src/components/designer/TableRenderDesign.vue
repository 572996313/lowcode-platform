<template>
  <div class="table-render-design">
    <!-- 模拟表格 -->
    <el-table :data="mockData" border stripe>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default>
          <el-button link type="primary">编辑</el-button>
          <el-button link type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { TableConfig } from '@/api/table'

interface Props {
  tableConfig: TableConfig
  designMode?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  designMode: true
})

// 模拟数据
const mockData = ref([
  { name: '示例数据1', code: 'CODE001', status: true, createTime: '2024-01-01 10:00:00' },
  { name: '示例数据2', code: 'CODE002', status: false, createTime: '2024-01-02 11:00:00' },
  { name: '示例数据3', code: 'CODE003', status: true, createTime: '2024-01-03 12:00:00' }
])
</script>

<style scoped>
.table-render-design {
  padding: 12px;
}
</style>
