<template>
  <div class="layout-tabs">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.id"
        :label="tab.title"
        :name="tab.id"
      >
        <div class="tab-content">
          <!-- 内容待实现 -->
          <div class="placeholder">Tab Content: {{ tab.title }}</div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface TabConfig {
  id: string
  title: string
  component?: string
  configId?: number
}

interface Props {
  tabs: TabConfig[]
}

const props = defineProps<Props>()

const activeTab = computed(() => {
  return props.tabs[0]?.id || ''
})
</script>

<style scoped>
.layout-tabs {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}

.tab-content {
  margin-top: 16px;
}

.placeholder {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>
