/**
 * 卡片渲染器（运行时）
 */
<template>
  <div class="card-render-wrapper" :style="wrapperStyle">
    <el-card :shadow="config.shadow || 'always'" class="card-render">
      <template v-if="config.showHeader !== false" #header>
        <div class="card-header">
          <span v-if="config.title">{{ config.title }}</span>
          <span v-else class="card-header-placeholder">卡片标题</span>
        </div>
      </template>
      <div v-if="config.content" class="card-content">{{ config.content }}</div>
      <div v-else class="card-content-placeholder">卡片内容</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CardComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: CardComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'block'
}))
</script>

<style scoped lang="scss">
.card-render-wrapper {
  width: 100%;
  height: 100%;

  .card-render {
    height: 100%;

    :deep(.el-card__body) {
      height: calc(100% - 60px);
      overflow: auto;
    }

    .card-header {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }

    .card-header-placeholder {
      color: #909399;
    }

    .card-content {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
    }

    .card-content-placeholder {
      color: #909399;
      text-align: center;
      padding: 20px;
    }
  }
}
</style>
