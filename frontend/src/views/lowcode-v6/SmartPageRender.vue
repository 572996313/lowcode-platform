<template>
  <component
    :is="rendererComponent"
    v-if="rendererComponent"
    v-bind="componentProps"
  />
  <div v-else-if="error" class="error-placeholder">
    <el-icon><Warning /></el-icon>
    <span>{{ error }}</span>
    <el-button type="primary" @click="loadPageConfig">重新加载</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPageConfig } from '@/api/page-v6'
import PageRenderV6 from './PageRenderV6.vue'
import FreeCanvasRender from './FreeCanvasRender.vue'
import type { NewPageConfig } from '@/types/page-v6'
import type { FreeCanvasPageConfig } from '@/types/page-free-canvas'

// Props - 当直接使用组件时传入配置对象
interface Props {
  pageConfig?: NewPageConfig | FreeCanvasPageConfig
}

const props = defineProps<Props>()

const route = useRoute()

// 状态
const loading = ref(false)
const error = ref('')
const dynamicPageConfig = ref<any>(null)

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
 * 实际使用的页面配置
 */
const pageConfig = computed(() => {
  return props.pageConfig || dynamicPageConfig.value
})

/**
 * 智能选择渲染器组件
 */
const rendererComponent = computed(() => {
  const config = pageConfig.value

  if (!config) {
    return null
  }

  // 检查配置版本标识
  const version = config.version
  const configVersion = config.configVersion

  console.log('检测页面配置版本:', { version, configVersion })

  // 自由画布页面 (version === 'free-canvas' 或 configVersion === 10)
  if (version === 'free-canvas' || configVersion === 10) {
    console.log('使用 FreeCanvasRender 渲染器')
    return FreeCanvasRender
  }

  // V6 页面 (version === 'v6' 或 configVersion === 2)
  if (version === 'v6' || configVersion === 2) {
    console.log('使用 PageRenderV6 渲染器')
    return PageRenderV6
  }

  // 默认情况下，尝试判断配置结构
  // 如果有 canvas 和 components 字段，认为是自由画布
  if ('canvas' in config && 'components' in config) {
    console.log('根据配置结构判断为自由画布')
    return FreeCanvasRender
  }

  // 默认使用 V6 渲染器
  console.log('默认使用 PageRenderV6 渲染器')
  return PageRenderV6
})

/**
 * 组件透传 props
 */
const componentProps = computed(() => {
  return {
    pageConfig: pageConfig.value,
    ...route.meta
  }
})

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

    // 解析 JSON 字符串
    try {
      dynamicPageConfig.value = JSON.parse(configJson)
      console.log('加载页面配置成功:', pageId, dynamicPageConfig.value)
    } catch (e) {
      console.error('解析页面配置 JSON 失败:', e)
      error.value = '页面配置格式错误'
      return
    }
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
.error-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 12px;
  color: #f56c6c;
}
</style>
