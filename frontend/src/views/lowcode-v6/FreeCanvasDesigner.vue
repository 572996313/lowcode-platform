/**
 * 自由画布页面设计器
 * FreeCanvasDesigner - Free Canvas Page Designer
 */
<template>
  <div class="free-canvas-designer">
    <!-- 顶部工具栏 -->
    <header class="designer-header">
      <div class="header-left">
        <el-icon class="back-icon" @click="handleBack">
          <ArrowLeft />
        </el-icon>
        <h1 class="page-title">自由画布设计器</h1>
        <el-input
          v-if="pageConfig"
          v-model="pageConfig.pageInfo.pageName"
          placeholder="页面名称"
          size="small"
          class="page-name-input"
          @change="handlePageNameChange"
        />
      </div>

      <div class="header-right">
        <el-button @click="handlePreview">
          <el-icon><View /></el-icon>
          预览
        </el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
        <el-button type="success" :loading="publishing" @click="handlePublish">
          <el-icon><Position /></el-icon>
          发布
        </el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="designer-main">
      <!-- 中间画布区域 -->
      <div class="canvas-area">
        <FreeCanvas
          v-if="pageConfig"
          v-model:config="pageConfig"
          v-model:selected-id="selectedId"
        />
        <div v-else class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <aside class="property-panel-panel">
        <PropertyPanel
          :component="selectedComponent"
          :canvas-config="pageConfig?.canvas"
          @update="handleComponentUpdate"
          @delete="handleComponentDelete"
        />
      </aside>
    </main>

    <!-- 底部组件库 -->
    <footer class="component-library-panel">
      <ComponentLibrary />
    </footer>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="页面预览"
      :width="1200"
      destroy-on-close
    >
      <FreeCanvasRender
        v-if="previewVisible && pageConfig"
        :config="pageConfig"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft, View, Refresh, Check, Position, Loading
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ComponentLibrary from '@/components/designer-free-canvas/ComponentLibrary.vue'
import FreeCanvas from '@/components/designer-free-canvas/FreeCanvas.vue'
import PropertyPanel from '@/components/designer-free-canvas/PropertyPanel.vue'
import FreeCanvasRender from '@/components/render-free-canvas/FreeCanvasRender.vue'
import type { FreeCanvasPageConfig, ComponentInstance } from '@/types/page-free-canvas'
import { createEmptyPageConfig } from '@/types/page-free-canvas'

const route = useRoute()
const router = useRouter()

// 页面配置
const pageConfig = ref<FreeCanvasPageConfig | null>(null)
const selectedId = ref<string | null>(null)

// 状态
const saving = ref(false)
const publishing = ref(false)
const previewVisible = ref(false)

// 选中的组件
const selectedComponent = computed(() => {
  if (!selectedId.value || !pageConfig.value) return null
  return pageConfig.value.components.find(c => c.id === selectedId.value) || null
})

// 页面 ID（从路由参数获取）
const pageId = ref<number | null>(null)

/**
 * 初始化
 */
onMounted(async () => {
  const id = route.params.pageId
  if (id && id !== 'new') {
    pageId.value = Number(id)
    await loadPageConfig(pageId.value)
  } else {
    // 创建新页面
    pageConfig.value = createEmptyPageConfig()
    pageConfig.value.pageInfo.pageName = '新页面'
    pageConfig.value.pageInfo.pageCode = `page_${Date.now()}`
  }
})

/**
 * 加载页面配置
 */
async function loadPageConfig(id: number) {
  try {
    // TODO: 调用 API 加载页面配置
    // const data = await getFreeCanvasPage(id)
    // pageConfig.value = data

    // 临时：使用空配置
    pageConfig.value = createEmptyPageConfig()
    pageConfig.value.pageInfo.pageName = '示例页面'
  } catch (error: any) {
    ElMessage.error('加载页面失败: ' + (error.message || '未知错误'))
  }
}

/**
 * 处理返回
 */
function handleBack() {
  router.push('/lowcode-v6/PageManageV6')
}

/**
 * 处理页面名称变化
 */
function handlePageNameChange() {
  // 页面名称变化时自动更新
}

/**
 * 处理组件更新
 */
function handleComponentUpdate(component: ComponentInstance) {
  if (!pageConfig.value) return

  const index = pageConfig.value.components.findIndex(c => c.id === component.id)
  if (index > -1) {
    pageConfig.value.components[index] = component
  }
}

/**
 * 处理组件删除
 */
function handleComponentDelete(id: string) {
  if (!pageConfig.value) return
  pageConfig.value.components = pageConfig.value.components.filter(c => c.id !== id)
  selectedId.value = null
}

/**
 * 处理预览
 */
function handlePreview() {
  if (!pageConfig.value) return
  previewVisible.value = true
}

/**
 * 处理重置
 */
async function handleReset() {
  try {
    await ElMessageBox.confirm(
      '确定要重置页面吗？所有未保存的更改将丢失。',
      '确认重置',
      {
        type: 'warning',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }
    )

    if (pageId.value) {
      await loadPageConfig(pageId.value)
    } else {
      pageConfig.value = createEmptyPageConfig()
    }
    selectedId.value = null
    ElMessage.success('已重置')
  } catch {
    // 用户取消
  }
}

/**
 * 处理保存
 */
async function handleSave() {
  if (!pageConfig.value) return

  if (!pageConfig.value.pageInfo.pageName) {
    ElMessage.warning('请输入页面名称')
    return
  }

  saving.value = true
  try {
    // TODO: 调用 API 保存页面配置
    // if (pageId.value) {
    //   await updateFreeCanvasPage(pageId.value, pageConfig.value)
    // } else {
    //   const result = await createFreeCanvasPage(pageConfig.value)
    //   pageId.value = result.id
    // }

    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 500))

    console.log('保存配置:', pageConfig.value)
    ElMessage.success('保存成功')
  } catch (error: any) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

/**
 * 处理发布
 */
async function handlePublish() {
  if (!pageConfig.value) return

  if (!pageConfig.value.pageInfo.pageName) {
    ElMessage.warning('请输入页面名称')
    return
  }

  publishing.value = true
  try {
    // TODO: 调用 API 发布页面
    // await publishFreeCanvasPage(pageId.value || 0, pageConfig.value)

    // 模拟发布
    await new Promise(resolve => setTimeout(resolve, 500))

    pageConfig.value.pageInfo.published = true
    console.log('发布配置:', pageConfig.value)
    ElMessage.success('发布成功')
  } catch (error: any) {
    ElMessage.error('发布失败: ' + (error.message || '未知错误'))
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped lang="scss">
.free-canvas-designer {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
  overflow: hidden;
}

.designer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .back-icon {
      font-size: 20px;
      cursor: pointer;
      color: #606266;
      transition: color 0.2s;

      &:hover {
        color: #409eff;
      }
    }

    .page-title {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }

    .page-name-input {
      width: 200px;
    }
  }

  .header-right {
    display: flex;
    gap: 12px;
  }
}

.designer-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.component-library-panel {
  height: 180px;
  flex-shrink: 0;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  overflow: hidden;
}

.canvas-area {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #909399;
    gap: 12px;

    .el-icon {
      font-size: 32px;
    }
  }
}

.property-panel-panel {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  overflow: hidden;
}

:deep(.el-dialog__body) {
  padding: 0;
}
</style>
