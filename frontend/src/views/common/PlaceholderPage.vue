<template>
  <div class="placeholder-page">
    <el-result
      icon="warning"
      title="页面开发中"
      :sub-title="`组件路径: ${componentPath}`"
    >
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const componentPath = route.meta?.menuName
  ? `菜单: ${route.meta.menuName} (未配置组件路径)`
  : '未找到对应组件'

function goBack() {
  router.back()
}
</script>

<style scoped lang="scss">
.placeholder-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}
</style>
