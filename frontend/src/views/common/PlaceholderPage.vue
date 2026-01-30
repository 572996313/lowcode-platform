<template>
  <div class="placeholder-page">
    <el-empty description="页面开发中">
      <template #image>
        <el-icon :size="100" class="placeholder-icon">
          <Tools />
        </el-icon>
      </template>
      <template #description>
        <h2>{{ menuName || '页面开发中' }}</h2>
        <p class="tip">该功能正在开发中,敬请期待...</p>
        <p class="code" v-if="menuCode">菜单编码: {{ menuCode }}</p>
        <p class="route" v-if="routePath">路由路径: {{ routePath }}</p>
      </template>
      <el-button type="primary" @click="goHome">
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 从路由 meta 中获取菜单信息
const menuName = computed(() => route.meta.menuName as string || '未知页面')
const menuCode = computed(() => route.meta.menuCode as string)
const routePath = computed(() => route.path)

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped lang="scss">
.placeholder-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 40px 20px;

  .placeholder-icon {
    color: var(--el-color-primary);
    opacity: 0.6;
  }

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0 0 12px 0;
  }

  .tip {
    font-size: 16px;
    color: var(--el-text-color-regular);
    margin: 0 0 16px 0;
  }

  .code,
  .route {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    font-family: 'Courier New', monospace;
    background: var(--el-fill-color-light);
    padding: 4px 12px;
    border-radius: 4px;
    display: inline-block;
    margin: 4px 0;
  }

  .route {
    margin-top: 8px;
  }

  :deep(.el-empty) {
    padding: 40px 0;
  }

  :deep(.el-button) {
    margin-top: 20px;
  }
}
</style>
