/**
 * 标签页渲染器（运行时）
 */
<template>
  <div class="tabs-render-wrapper" :style="wrapperStyle">
    <el-tabs
      :tab-position="config.tabPosition || 'top'"
      :type="config.type"
      class="tabs-render"
    >
      <el-tab-pane
        v-for="tab in config.tabs"
        :key="tab.id"
        :label="tab.label"
      >
        <template #label>
          <span class="tab-label">
            <span v-if="tab.icon" class="tab-icon">{{ tab.icon }}</span>
            <span>{{ tab.label }}</span>
          </span>
        </template>
        <div v-if="tab.content" class="tab-content">{{ tab.content }}</div>
        <div v-else class="tab-content-placeholder">标签页内容</div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TabsComponentConfig } from '@/types/page-free-canvas'

interface Props {
  config: TabsComponentConfig
  style?: Record<string, any>
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => ({
  ...props.style,
  display: 'block'
}))
</script>

<style scoped lang="scss">
.tabs-render-wrapper {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;

  .tabs-render {
    height: 100%;

    :deep(.el-tabs__content) {
      height: calc(100% - 40px);
      overflow: auto;
    }

    .tab-label {
      display: flex;
      align-items: center;
      gap: 4px;

      .tab-icon {
        font-size: 14px;
      }
    }

    .tab-content {
      padding: 16px;
    }

    .tab-content-placeholder {
      padding: 16px;
      color: #909399;
      text-align: center;
    }
  }
}
</style>
