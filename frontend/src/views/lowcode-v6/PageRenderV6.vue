<template>
  <div class="page-render-v6" v-loading="configLoading">
    <div v-if="configError" class="error-message">
      <el-empty :description="configError">
        <el-button type="primary" @click="loadPageConfig">重新加载</el-button>
      </el-empty>
    </div>
    <template v-else-if="pageConfig">
      <!-- 工具栏 -->
      <ToolbarRenderV6
        v-if="pageConfig.toolbar.enabled"
        :config="pageConfig.toolbar"
        @action="handleToolbarAction"
      />

      <!-- 查询区 -->
      <SearchRenderV6
        v-if="pageConfig.search.enabled"
        :config="pageConfig.search"
        @search="handleSearch"
        @reset="handleReset"
      />

      <!-- 表格 -->
      <TableRenderV6
        :config="pageConfig.table"
        :data="tableData"
        :loading="loading"
        @action="handleTableAction"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import ToolbarRenderV6 from '@/components/render-v6/ToolbarRenderV6.vue'
import SearchRenderV6 from '@/components/render-v6/SearchRenderV6.vue'
import TableRenderV6 from '@/components/render-v6/TableRenderV6.vue'
import type { NewPageConfig } from '@/types/page-v6'
import { getPageConfig } from '@/api/page-v6'
import { createEmptyPageConfig } from '@/types/page-v6'

// Props - 当直接使用组件时传入
interface Props {
  pageConfig?: NewPageConfig
}

const props = defineProps<Props>()

const route = useRoute()

// 表格数据
const loading = ref(false)
const configLoading = ref(false)
const configError = ref<string>('')
const tableData = ref<any[]>([])

// 动态加载的页面配置
const dynamicPageConfig = ref<NewPageConfig | null>(null)

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
 * 加载页面配置
 */
const loadPageConfig = async () => {
  // 如果已经有 pageConfig prop，不需要加载
  if (props.pageConfig) {
    return
  }

  const pageId = getPageIdFromRoute()
  if (!pageId) {
    configError.value = '无法获取页面ID'
    return
  }

  configLoading.value = true
  configError.value = ''

  try {
    // 后端返回 LowPageConfig 实体
    const pageData = await getPageConfig(pageId)
    console.log('后端返回的页面配置:', pageData)

    // configTemplate 或 configJson 是 JSON 字符串，需要解析
    const configTemplate = pageData.configTemplate || pageData.configJson
    if (!configTemplate) {
      configError.value = '页面配置为空，请先在页面设计器中配置页面'
      return
    }

    // 解析 JSON 字符串为 NewPageConfig 对象
    let pageConfigData: NewPageConfig
    try {
      pageConfigData = JSON.parse(configTemplate)
    } catch (e) {
      console.error('解析页面配置 JSON 失败:', e)
      configError.value = '页面配置格式错误'
      return
    }

    // 设置 pageId
    pageConfigData.pageId = pageData.id

    dynamicPageConfig.value = pageConfigData
    console.log('加载页面配置成功:', pageId, pageConfigData)
  } catch (error: any) {
    console.error('加载页面配置失败:', error)
    configError.value = error.message || '加载页面配置失败'
    ElMessage.error(configError.value)
  } finally {
    configLoading.value = false
  }
}

// 初始化
onMounted(async () => {
  await loadPageConfig()
  if (pageConfig.value) {
    loadData()
  }
})

/**
 * 加载数据
 */
async function loadData() {
  loading.value = true
  try {
    // TODO: 调用 API 加载数据
    // const res = await request({
    //   url: pageConfig.value.globalConfig.apiEndpoint,
    //   method: 'get',
    //   params: searchParams.value
    // })
    // tableData.value = res.data

    // 模拟数据
    tableData.value = [
      { id: 1, name: '张三', age: 25, status: 'active' },
      { id: 2, name: '李四', age: 30, status: 'inactive' }
    ]
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 工具栏操作
 */
function handleToolbarAction(action: any) {
  console.log('工具栏操作:', action)
}

/**
 * 查询
 */
function handleSearch(params: any) {
  console.log('查询参数:', params)
  loadData()
}

/**
 * 重置
 */
function handleReset() {
  console.log('重置查询')
  loadData()
}

/**
 * 表格操作
 */
function handleTableAction(action: any, row: any) {
  console.log('表格操作:', action, row)
}
</script>

<style scoped lang="scss">
.page-render-v6 {
  padding: 20px;
  background: #fff;
  min-height: 100%;
}

.error-message {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}
</style>
