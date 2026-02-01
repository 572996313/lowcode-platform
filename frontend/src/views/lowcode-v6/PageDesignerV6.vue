<template>
  <div class="page-designer-v6">
    <!-- 顶部工具栏 -->
    <div class="designer-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
        <el-divider direction="vertical" />
        <span class="page-title">{{ pageTitle }}</span>
      </div>
      <div class="header-right">
        <el-button :icon="View" @click="handlePreview">预览</el-button>
        <el-button :icon="Check" type="primary" @click="handleSave">保存</el-button>
        <el-button :icon="Promotion" type="success" @click="handlePublish">发布</el-button>
      </div>
    </div>

    <!-- 主体区域 -->
    <div class="designer-body">
      <!-- 中间预览区 -->
      <div class="preview-area">
        <PagePreview
          :page-config="pageConfig"
          :selected="selectedObject"
          @update:page-config="pageConfig = $event"
          @update:selected="selectedObject = $event"
          @drop="handleDrop"
        />
      </div>

      <!-- 右侧属性面板 -->
      <div class="property-panel">
        <PropertyPanelV6
          :selected="selectedObject"
          :page-config="pageConfig"
          @update:selected="selectedObject = $event"
          @update:page-config="pageConfig = $event"
          @update="handleUpdateConfig"
        />
      </div>
    </div>

    <!-- 底部组件库 -->
    <div class="component-library">
      <ComponentLibraryV6 @dragstart="handleDragStart" />
    </div>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="页面预览"
      width="90%"
      :fullscreen="isFullscreen"
      destroy-on-close
    >
      <PageRenderV6
        v-if="previewVisible"
        :page-config="pageConfig"
      />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button :icon="FullScreen" @click="toggleFullscreen">
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft, View, Check, Promotion, FullScreen
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PagePreview from '@/components/designer-v6/PagePreview.vue'
import PropertyPanelV6 from '@/components/designer-v6/PropertyPanelV6.vue'
import ComponentLibraryV6 from '@/components/designer-v6/ComponentLibraryV6.vue'
import PageRenderV6 from '@/views/lowcode-v6/PageRenderV6.vue'
import type {
  NewPageConfig,
  DragData,
  TemplateDragData,
  SelectedObject
} from '@/types/page-v6'
import {
  createEmptyPageConfig,
  generateId
} from '@/types/page-v6'

const route = useRoute()
const router = useRouter()

// 页面配置
const pageConfig = ref<NewPageConfig>(createEmptyPageConfig())

// 选中对象
const selectedObject = ref<SelectedObject>(null)

// 预览对话框
const previewVisible = ref(false)
const isFullscreen = ref(false)

// 页面标题
const pageTitle = computed(() => {
  const pageId = route.params.id
  return pageId ? '编辑页面' : '新建页面'
})

// 初始化
onMounted(async () => {
  const pageId = route.params.id as string
  if (pageId) {
    await loadPageConfig(parseInt(pageId))
  }
})

/**
 * 加载页面配置
 */
async function loadPageConfig(pageId: number) {
  try {
    // TODO: 调用 API 加载配置
    // const res = await getPageConfig(pageId)
    // pageConfig.value = res.data
    console.log('加载页面配置:', pageId)
  } catch (error) {
    ElMessage.error('加载页面配置失败')
    console.error(error)
  }
}

/**
 * 返回
 */
function handleBack() {
  router.back()
}

/**
 * 预览
 */
function handlePreview() {
  previewVisible.value = true
  isFullscreen.value = false
}

/**
 * 切换全屏
 */
function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}

/**
 * 保存
 */
async function handleSave() {
  try {
    // 验证
    if (!pageConfig.value.pageName) {
      ElMessage.warning('请输入页面名称')
      return
    }
    if (!pageConfig.value.pageCode) {
      ElMessage.warning('请输入页面编码')
      return
    }

    // TODO: 调用 API 保存配置
    // const res = await savePageConfig(pageConfig.value)
    // pageConfig.value.pageId = res.data.pageId

    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
    console.error(error)
  }
}

/**
 * 发布
 */
async function handlePublish() {
  try {
    await ElMessageBox.confirm(
      '发布后页面将正式生效，是否继续？',
      '确认发布',
      {
        type: 'warning'
      }
    )

    // TODO: 调用 API 发布
    // await publishPage(pageConfig.value.pageId!)

    pageConfig.value.published = true
    ElMessage.success('发布成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
      console.error(error)
    }
  }
}

/**
 * 拖拽开始
 */
function handleDragStart(data: DragData) {
  // 设置拖拽数据（由 ComponentLibraryV6 处理）
  console.log('拖拽开始:', data)
}

/**
 * 放置组件
 */
async function handleDrop(area: string, dragData: DragData | TemplateDragData) {
  console.log('放置组件:', area, dragData)

  // 检查是否来自模板库
  if (dragData.source === 'template-library') {
    return handleTemplateDrop(area, dragData as TemplateDragData)
  }

  // 验证是否可以放置
  if (!canDrop(area, dragData.itemType)) {
    ElMessage.warning('该组件不能放置到此区域')
    return
  }

  // 添加到配置
  addToConfig(area, dragData)
}

/**
 * 处理模板拖拽
 */
async function handleTemplateDrop(area: string, dragData: TemplateDragData) {
  // 验证是否可以放置
  if (!canDrop(area, dragData.itemType)) {
    ElMessage.warning('该组件不能放置到此区域')
    return
  }

  try {
    // 弹出对话框选择引用模式
    const result = await ElMessageBox.confirm(
      '拖拽的是模板组件，请选择引用模式',
      '模板引用',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '保持引用',
        cancelButtonText: '独立副本',
        type: 'info'
      }
    )

    // 保持引用模式
    addTemplateToConfig(area, dragData, true)
  } catch (action) {
    if (action === 'cancel') {
      // 独立副本模式
      addTemplateToConfig(area, dragData, false)
    } else {
      // 关闭对话框，不添加
      return
    }
  }
}

/**
 * 检查是否可以放置
 */
function canDrop(area: string, itemType: string): boolean {
  const rules: Record<string, string[]> = {
    toolbar: ['button'],
    search: ['field'],
    table: ['column', 'rowAction']
  }
  return rules[area]?.includes(itemType) || false
}

/**
 * 添加到配置
 */
function addToConfig(area: string, dragData: DragData) {
  const config = dragData.defaultConfig

  switch (area) {
    case 'toolbar':
      if (dragData.itemType === 'button') {
        pageConfig.value.toolbar.buttons.push({
          ...config,
          id: generateId('button')
        })
      }
      break

    case 'search':
      if (dragData.itemType === 'field') {
        pageConfig.value.search.fields.push({
          ...config,
          id: generateId('field')
        })
      }
      break

    case 'table':
      if (dragData.itemType === 'column') {
        pageConfig.value.table.columns.push({
          ...config,
          id: generateId('column')
        })
      } else if (dragData.itemType === 'rowAction') {
        pageConfig.value.table.rowActions?.push({
          ...config,
          id: generateId('rowAction')
        })
      }
      break
  }

  ElMessage.success('添加成功')
}

/**
 * 添加模板到配置
 */
function addTemplateToConfig(area: string, dragData: TemplateDragData, isLinked: boolean) {
  const defaultConfig = dragData.defaultConfig

  switch (area) {
    case 'toolbar':
      if (dragData.itemType === 'button') {
        if (isLinked) {
          // 保持引用模式
          pageConfig.value.toolbar.buttons.push({
            ...defaultConfig,
            templateId: dragData.templateId,
            templateType: 'button',
            isLinked: true,
            overwrite: {}
          })
        } else {
          // 独立副本模式（深拷贝）
          pageConfig.value.toolbar.buttons.push({
            ...JSON.parse(JSON.stringify(defaultConfig))
          })
        }
      }
      break

    case 'search':
      if (dragData.itemType === 'field') {
        if (isLinked) {
          pageConfig.value.search.fields.push({
            ...defaultConfig,
            templateId: dragData.templateId,
            templateType: 'field',
            isLinked: true,
            overwrite: {}
          })
        } else {
          pageConfig.value.search.fields.push({
            ...JSON.parse(JSON.stringify(defaultConfig))
          })
        }
      }
      break

    case 'table':
      if (dragData.itemType === 'column') {
        if (isLinked) {
          pageConfig.value.table.columns.push({
            ...defaultConfig,
            templateId: dragData.templateId,
            templateType: 'column',
            isLinked: true,
            overwrite: {}
          })
        } else {
          pageConfig.value.table.columns.push({
            ...JSON.parse(JSON.stringify(defaultConfig))
          })
        }
      }
      break
  }

  ElMessage.success(isLinked ? '添加引用成功' : '添加副本成功')
}

/**
 * 更新配置
 */
function handleUpdateConfig() {
  // 配置已通过 v-model 更新
  console.log('配置已更新')
}
</script>

<style scoped lang="scss">
.page-designer-v6 {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.designer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .page-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }

  .header-right {
    display: flex;
    gap: 8px;
  }
}

.designer-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  padding: 16px;
  gap: 16px;
}

.preview-area {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  overflow: auto;
}

.property-panel {
  width: 320px;
  background: #fff;
  border-radius: 4px;
  flex-shrink: 0;
}

.component-library {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  z-index: 10;
}
</style>
