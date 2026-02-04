<template>
  <div class="free-canvas-render" v-loading="loading">
    <!-- 加载状态 -->
    <div v-if="loading && !pageConfig" class="loading-placeholder">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-placeholder">
      <el-icon><Warning /></el-icon>
      <span>{{ error }}</span>
      <el-button type="primary" @click="loadPageConfig">重新加载</el-button>
    </div>

    <!-- 正常渲染 -->
    <div
      v-else-if="pageConfig"
      class="canvas-container"
      :style="canvasStyle"
    >
      <!-- 动态渲染组件 -->
      <component
        v-for="comp in enabledComponents"
        :key="comp.id"
        :is="getComponentRenderer(comp.type)"
        :config="comp.config"
        :position="comp.position"
        :style="comp.style"
        class="canvas-component"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPageConfig } from '@/api/page-v6'
import type { FreeCanvasPageConfig, ComponentType, ComponentInstance } from '@/types/page-free-canvas'
import TreeRenderFreeCanvas from '@/components/render-free-canvas/TreeRenderFreeCanvas.vue'
import TableRenderFreeCanvas from '@/components/render-free-canvas/TableRenderFreeCanvas.vue'
import FormRenderFreeCanvas from '@/components/render-free-canvas/FormRenderFreeCanvas.vue'
import ButtonRenderFreeCanvas from '@/components/render-free-canvas/ButtonRenderFreeCanvas.vue'

// Props - 当直接使用组件时传入
interface Props {
  pageConfig?: FreeCanvasPageConfig
}

const props = defineProps<Props>()

const route = useRoute()

// 状态
const loading = ref(false)
const error = ref('')
const dynamicPageConfig = ref<FreeCanvasPageConfig | null>(null)

// 实际使用的页面配置：优先使用 props，其次使用动态加载的
const pageConfig = computed(() => {
  return props.pageConfig || dynamicPageConfig.value
})

/**
 * 从路由获取 pageId
 */
const getPageIdFromRoute = (): number | null => {
  // 优先从 route.params 获取（如 /page/preview/:id）
  const paramId = route.params.id
  if (paramId) {
    const id = Number(paramId)
    if (!isNaN(id) && id > 0) {
      return id
    }
  }

  // 其次从 route.meta 获取（菜单路由）
  const metaPageId = route.meta.pageId
  if (metaPageId) {
    const id = Number(metaPageId)
    if (!isNaN(id) && id > 0) {
      return id
    }
  }

  return null
}

/**
 * 画布样式
 */
const canvasStyle = computed(() => {
  if (!pageConfig.value) return {}

  const canvas = pageConfig.value.canvas
  const style: Record<string, any> = {
    width: canvas.width + 'px',
    height: canvas.height || 'auto',
    backgroundColor: canvas.backgroundColor || '#f5f7fa'
  }

  // 背景图
  if (canvas.backgroundImage) {
    style.backgroundImage = canvas.backgroundImage
    style.backgroundSize = 'cover'
    style.backgroundRepeat = 'no-repeat'
    style.backgroundPosition = 'center'
  }

  // 网格
  if (canvas.gridSize && canvas.gridSize > 0) {
    style.backgroundImage = `linear-gradient(rgba(0,0,0,0.05) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0,0,0,0.05) 1px, transparent 1px)`
    style.backgroundSize = `${canvas.gridSize}px ${canvas.gridSize}px`
  }

  return style
})

/**
 * 只渲染启用的组件
 */
const enabledComponents = computed(() => {
  if (!pageConfig.value) return []
  return pageConfig.value.components.filter((c: ComponentInstance) => c.enabled !== false)
})

/**
 * 根据组件类型获取渲染组件
 */
function getComponentRenderer(type: ComponentType) {
  const rendererMap: Record<ComponentType, any> = {
    'tree': TreeRenderFreeCanvas,
    'table': TableRenderFreeCanvas,
    'form': FormRenderFreeCanvas,
    'button-group': ButtonRenderFreeCanvas,
    'search-form': ButtonRenderFreeCanvas, // 暂时用按钮组件代替
    'chart': null, // TODO: 待实现
    'tabs': null, // TODO: 待实现
    'card': null, // TODO: 待实现
    'divider': null, // TODO: 待实现
    'spacer': null, // TODO: 待实现
    'custom': null // TODO: 待实现
  }

  return rendererMap[type] || null
}

/**
 * 加载页面配置
 */
const loadPageConfig = async () => {
  // 如果已经有 pageConfig prop，不需要加载
  if (props.pageConfig) {
    return
  }

  const pageId = getPageIdFromRoute()
  if (!pageId) {
    error.value = '无法获取页面ID'
    return
  }

  loading.value = true
  error.value = ''

  try {
    // 后端返回 LowPageConfig 实体
    const pageData = await getPageConfig(pageId)
    console.log('后端返回的页面配置:', pageData)

    // configJson 或 configTemplate 是 JSON 字符串，需要解析
    const configJson = pageData.configJson || pageData.configTemplate
    if (!configJson) {
      error.value = '页面配置为空，请先在页面设计器中配置页面'
      return
    }

    // 解析 JSON 字符串为 FreeCanvasPageConfig 对象
    let pageConfigData: FreeCanvasPageConfig
    try {
      pageConfigData = JSON.parse(configJson)
    } catch (e) {
      console.error('解析页面配置 JSON 失败:', e)
      error.value = '页面配置格式错误'
      return
    }

    // 验证是否为自由画布配置
    if (pageConfigData.version !== 'free-canvas') {
      error.value = `此页面不是自由画布类型 (version: ${pageConfigData.version})`
      return
    }

    dynamicPageConfig.value = pageConfigData
    console.log('加载自由画布配置成功:', pageId, pageConfigData)
  } catch (error: any) {
    console.error('加载页面配置失败:', error)
    error.value = error.message || '加载页面配置失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(async () => {
  await loadPageConfig()
})
</script>

<style scoped lang="scss">
.free-canvas-render {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: #f0f2f5;
}

.loading-placeholder,
.error-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 12px;
  color: #606266;
}

.error-placeholder {
  color: #f56c6c;
}

.canvas-container {
  position: relative;
  margin: 20px auto;
  min-height: 600px;
  transition: all 0.3s ease;
}

.canvas-component {
  position: absolute;
}
</style>
