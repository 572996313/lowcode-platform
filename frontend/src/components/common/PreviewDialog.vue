<template>
  <el-dialog
    :model-value="visible"
    :title="title || '预览'"
    :fullscreen="fullscreen"
    :width="fullscreen ? '100%' : '80%'"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <div v-if="renderComponent" class="preview-content">
      <component
        :is="renderComponent"
        v-if="config"
        v-bind="renderProps"
      />
      <div v-else class="empty-state">
        <el-empty description="暂无配置数据" />
      </div>
    </div>
    <div v-else class="preview-content">
      <el-empty description="请指定渲染组件" />
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button v-if="!fullscreen" type="primary" @click="toggleFullscreen">全屏</el-button>
      <el-button v-else type="primary" @click="toggleFullscreen">退出全屏</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  visible: boolean
  title?: string
  fullscreen?: boolean
  renderComponent?: any
  config?: any
  renderProps?: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  fullscreen: false,
  renderProps: () => ({})
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
}>()

const currentFullscreen = ref(props.fullscreen)

watch(() => props.fullscreen, (val) => {
  currentFullscreen.value = val
})

function handleClose() {
  emit('update:visible', false)
  currentFullscreen.value = props.fullscreen
}

function toggleFullscreen() {
  currentFullscreen.value = !currentFullscreen.value
}
</script>

<style scoped lang="scss">
.preview-content {
  min-height: 400px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;

  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
  }
}
</style>
