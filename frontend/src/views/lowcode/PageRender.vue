<template>
  <div v-loading="loading" class="page-render">
    <!-- 错误状态 -->
    <div v-if="error" class="page-error">
      <el-result icon="error" title="页面加载失败" :sub-title="error">
        <template #extra>
          <el-button type="primary" @click="loadPageConfig">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <!-- 正常内容 -->
    <div v-else-if="pageConfig" class="page-content">
      <!-- 页面标题 -->
      <div v-if="showPageTitle" class="page-header">
        <h2 class="page-title">{{ pageConfig.pageName }}</h2>
        <div v-if="pageConfig.description" class="page-description">
          {{ pageConfig.description }}
        </div>
      </div>

      <!-- 上下布局（列表页） -->
      <TopBottomLayout
        v-if="layoutType === 'top-bottom' || layoutType === 'list'"
      >
        <template #search>
          <SearchAreaRender
            v-if="showSearchArea"
            :fields="searchAreaFields"
            :model="searchFormData"
            @search="handleSearch"
            @reset="handleReset"
          />
        </template>
        <template #toolbar>
          <div v-if="showToolbar" class="page-toolbar">
            <ButtonRenderer
              v-for="btn in toolbarButtons"
              :key="btn.id"
              :config="btn"
              :context="{ selection: tableSelection }"
              @click="handleButtonAction"
            />
          </div>
        </template>
        <template #content>
          <TableRender
            v-if="contentType === 'table'"
            :ref="el => contentRef = el"
            :config-id="contentConfigId"
            :search-form-data="searchFormData"
            :auto-load="false"
            @selection-change="handleSelectionChange"
            @refresh="handleRefresh"
          />
          <FormRender
            v-else-if="contentType === 'form'"
            :ref="el => contentRef = el"
            :config-id="contentConfigId"
            :mode="formMode"
            @submit="handleFormSubmit"
            @refresh="handleRefresh"
          />
        </template>
      </TopBottomLayout>

      <!-- 左树右表布局 -->
      <TreeTableLayout
        v-else-if="layoutType === 'tree-table'"
      >
        <template #tree>
          <div class="tree-container">
            <div class="tree-header">
              <span>{{ treeAreaConfig?.title || '树形结构' }}</span>
            </div>
            <el-tree
              :data="treeData"
              :props="treeProps"
              :node-key="treeNodeKey"
              @node-click="handleTreeNodeClick"
            />
          </div>
        </template>
        <template #content>
          <TableRender
            :ref="el => contentRef = el"
            :config-id="contentConfigId"
            :search-form-data="searchFormData"
          />
        </template>
      </TreeTableLayout>

      <!-- 标签页布局 -->
      <TabsLayout
        v-else-if="layoutType === 'tabs'"
        :tabs="tabsConfig"
      />

      <!-- 仪表盘布局 -->
      <DashboardLayout
        v-else-if="layoutType === 'dashboard'"
        :widgets="dashboardWidgets"
      />

      <!-- 自定义布局 -->
      <CustomLayout
        v-else-if="layoutType === 'custom'"
        :components="customComponents"
      />

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无页面配置" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, provide } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { PageConfig } from '@/api/page'
import type { ButtonConfig } from '@/api/button'
import { getPage } from '@/api/page'
import { getButtonsByPageId, getButtonsByIds } from '@/api/button'
import SearchAreaRender from '@/components/render/SearchAreaRender.vue'
import TableRender from '@/components/render/TableRender.vue'
import FormRender from '@/components/render/FormRender.vue'
import ButtonRenderer from '@/components/render/ButtonRenderer.vue'
import TopBottomLayout from '@/components/layout/TopBottomLayout.vue'
import TreeTableLayout from '@/components/layout/TreeTableLayout.vue'
import TabsLayout from '@/components/layout/TabsLayout.vue'
import DashboardLayout from '@/components/layout/DashboardLayout.vue'
import CustomLayout from '@/components/layout/CustomLayout.vue'
import { createButtonActionHandler } from '@/utils/buttonActionHandler'

const route = useRoute()

const loading = ref(false)
const error = ref<string | null>(null)
const pageConfig = ref<PageConfig | null>(null)
const contentRef = ref<any>()

// 查询表单数据
const searchFormData = reactive<Record<string, any>>({})
// 表格选中项
const tableSelection = ref<any[]>([])

// 树形数据
const treeData = ref<any[]>([])
const treeProps = {
  children: 'children',
  label: 'label'
}
const treeNodeKey = 'id'

// 布局类型
const layoutType = computed(() => {
  return pageConfig.value?.layoutType || pageConfig.value?.pageType || 'top-bottom'
})

// 是否显示页面标题
const showPageTitle = computed(() => {
  return layoutType.value !== 'list'
})

// 查询区配置
const searchAreaConfig = computed(() => {
  if (!pageConfig.value?.configJsonObject?.searchArea) {
    return null
  }
  return pageConfig.value.configJsonObject.searchArea
})

// 是否显示查询区
const showSearchArea = computed(() => {
  return searchAreaConfig.value?.enabled === true
})

// 查询区字段
const searchAreaFields = computed(() => {
  return searchAreaConfig.value?.fields || []
})

// 内容区配置
const contentAreaConfig = computed(() => {
  if (!pageConfig.value?.configJsonObject?.contentArea) {
    console.warn('内容区域配置不存在:', pageConfig.value)
    return null
  }
  return pageConfig.value.configJsonObject.contentArea
})

// 内容类型
const contentType = computed(() => {
  return contentAreaConfig.value?.type || 'table'
})

// 内容配置ID
const contentConfigId = computed(() => {
  return contentAreaConfig.value?.configId
})

// 是否显示工具栏
const showToolbar = computed(() => {
  return contentAreaConfig.value?.showToolbar !== false
})

// 表单模式
const formMode = computed(() => {
  return 'add'
})

// 树形区域配置
const treeAreaConfig = computed(() => {
  if (!pageConfig.value?.configJsonObject) return null
  return pageConfig.value.configJsonObject.treeArea
})

// 标签页配置
const tabsConfig = computed(() => {
  if (!pageConfig.value?.configJsonObject) return []
  return pageConfig.value.configJsonObject.tabs || []
})

// 仪表盘配置
const dashboardWidgets = computed(() => {
  if (!pageConfig.value?.configJsonObject) return []
  return pageConfig.value.configJsonObject.widgets || []
})

// 自定义组件配置
const customComponents = computed(() => {
  if (!pageConfig.value?.configJsonObject) return []
  return pageConfig.value.configJsonObject.components || []
})

// 按钮配置
const toolbarButtons = ref<ButtonConfig[]>([])
// 按钮配置缓存（按ID存储）
const buttonMap = ref<Map<number, ButtonConfig>>(new Map())

// 提供按钮配置给子组件
provide('buttonMap', buttonMap)

// 加载页面配置
const loadPageConfig = async () => {
  loading.value = true
  error.value = null
  try {
    const pageId = Number(route.meta.pageId || route.params.id || route.query.id)
    if (!pageId || isNaN(pageId)) {
      console.error('页面ID获取失败:', {
        meta: route.meta,
        params: route.params,
        query: route.query,
        pageId: pageId
      })
      throw new Error('无法获取页面ID，请检查路由配置')
    }

    const data = await getPage(pageId)
    pageConfig.value = data

    // 解析配置JSON
    if (data.configJson && !data.configJsonObject) {
      try {
        pageConfig.value.configJsonObject = JSON.parse(data.configJson)
      } catch (e) {
        console.error('解析页面配置JSON失败:', e)
        throw new Error('页面配置格式错误')
      }
    }

    // 初始化查询表单数据
    if (searchAreaFields.value.length > 0) {
      searchAreaFields.value.forEach((field: any) => {
        if (field.defaultValue) {
          searchFormData[field.fieldCode] = field.defaultValue
        }
      })
    }

    // 加载按钮配置（支持新旧两种格式）
    await loadButtons(data)

    // 加载树形数据
    if (layoutType.value === 'tree-table' && treeAreaConfig.value?.dataUrl) {
      await loadTreeData(treeAreaConfig.value.dataUrl)
    }
  } catch (error: any) {
    error.value = error.message || '加载页面配置失败'
    console.error('页面加载错误:', error)
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 加载按钮配置（支持新旧两种格式）
const loadButtons = async (pageConfig: PageConfig) => {
  try {
    // 优先使用新格式（configTemplate v2）
    if (pageConfig.configTemplate && pageConfig.configTemplate.areas) {
      // 收集所有 buttonId
      const buttonIds: number[] = []
      const toolbarButtonRefs: any[] = []

      pageConfig.configTemplate.areas.forEach((area: any) => {
        // 检查 area.config 中的按钮
        if (area.config?.buttons && Array.isArray(area.config.buttons)) {
          area.config.buttons.forEach((btnConfig: any) => {
            if (btnConfig.buttonId) {
              buttonIds.push(btnConfig.buttonId)
              // 如果是工具栏按钮，记录引用
              if (area.type === 'toolbar') {
                toolbarButtonRefs.push({
                  id: btnConfig.id,
                  buttonId: btnConfig.buttonId,
                  label: btnConfig.label,
                  position: 'toolbar'
                })
              }
            }
          })
        }
        // 兼容旧格式：检查 area.props 中的按钮
        if (area.props?.buttons && Array.isArray(area.props.buttons)) {
          area.props.buttons.forEach((btnConfig: any) => {
            if (btnConfig.buttonId) {
              buttonIds.push(btnConfig.buttonId)
              if (area.type === 'toolbar') {
                toolbarButtonRefs.push({
                  id: btnConfig.id,
                  buttonId: btnConfig.buttonId,
                  label: btnConfig.label,
                  position: 'toolbar'
                })
              }
            }
          })
        }
      })

      // 批量加载按钮配置
      if (buttonIds.length > 0) {
        const buttons = await getButtonsByIds(buttonIds)
        // 缓存按钮配置
        buttonMap.value = new Map(buttons.map(btn => [btn.id!, btn]))
      }

      // 设置工具栏按钮（合并引用和完整配置）
      toolbarButtons.value = toolbarButtonRefs.map(ref => ({
        ...buttonMap.value.get(ref.buttonId),
        ...ref
      }))
    } else if (pageConfig.configJsonObject) {
      // 兼容旧格式（configJsonObject v1）
      // 尝试从 configJsonObject 中读取按钮配置
      if (pageConfig.configJsonObject.components) {
        const buttons = pageConfig.configJsonObject.components.filter((c: any) => c.type === 'button')
        toolbarButtons.value = buttons
        // 缓存按钮配置
        buttonMap.value = new Map(buttons.map((btn: any) => [btn.id, btn]))
      } else if (pageConfig.id) {
        // 如果 components 为空，尝试通过 pageId 查询（向后兼容）
        const buttons = await getButtonsByPageId(pageConfig.id)
        if (buttons && buttons.length > 0) {
          toolbarButtons.value = buttons.filter((btn: ButtonConfig) => btn.position === 'toolbar')
          // 缓存按钮配置
          buttonMap.value = new Map(buttons.map(btn => [btn.id!, btn]))
        }
      }
    }
  } catch (error) {
    console.error('加载按钮配置失败:', error)
    // 不阻断主流程
  }
}

// 加载树形数据
const loadTreeData = async (url: string) => {
  try {
    // TODO: 实现树形数据加载逻辑
    treeData.value = []
  } catch (error: any) {
    console.error('加载树形数据失败:', error)
  }
}

// 查询
const handleSearch = () => {
  contentRef.value?.loadData()
}

// 重置
const handleReset = () => {
  Object.keys(searchFormData).forEach(key => {
    delete searchFormData[key]
  })
  contentRef.value?.loadData()
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  tableSelection.value = selection
}

// 刷新
const handleRefresh = () => {
  contentRef.value?.loadData()
}

// 表单提交
const handleFormSubmit = (data: Record<string, any>) => {
  console.log('表单提交:', data)
}

// 按钮动作处理
const handleButtonAction = async (event: MouseEvent, config: ButtonConfig) => {
  const context = {
    selection: tableSelection.value,
    refresh: () => handleRefresh()
  }

  const actionHandler = createButtonActionHandler(context)
  await actionHandler.handle(config)
}

// 树节点点击
const handleTreeNodeClick = (data: any) => {
  console.log('树节点点击:', data)
  // TODO: 根据树节点筛选表格数据
}

// 提供全局上下文
provide('pageConfig', pageConfig)
provide('searchFormData', searchFormData)
provide('tableSelection', tableSelection)
provide('globalRefresh', handleRefresh)

onMounted(() => {
  loadPageConfig()
})
</script>

<style scoped>
.page-render {
  min-height: 100%;
  background: #f5f7fa;
  padding: 16px;
}

.page-error {
  background: #fff;
  border-radius: 4px;
  padding: 40px 20px;
}

.page-content {
  background: #fff;
  border-radius: 4px;
  min-height: calc(100vh - 120px);
}

.page-header {
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 500;
  color: #303133;
}

.page-description {
  font-size: 14px;
  color: #909399;
}

.page-toolbar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 4px;
}

.tree-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tree-header {
  padding: 12px 16px;
  font-weight: 500;
  border-bottom: 1px solid #eee;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}
</style>
