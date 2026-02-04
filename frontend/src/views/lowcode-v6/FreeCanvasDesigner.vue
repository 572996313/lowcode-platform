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
          v-if="pageConfig?.pageInfo"
          v-model="pageConfig.pageInfo.pageName"
          placeholder="页面名称"
          size="small"
          class="page-name-input"
          @change="handlePageNameChange"
        />
        <el-input
          v-if="pageConfig?.pageInfo"
          v-model="pageConfig.pageInfo.pageCode"
          placeholder="页面编码"
          size="small"
          class="page-code-input"
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
import request from '@/utils/request'

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
    const res = await request({
      url: `/page/${id}`,
      method: 'get'
    })

    console.log('[自由画布] 加载页面配置:', res)

    // 检查是否是自由画布页面 (响应拦截器已返回 data)
    if (res && res.configVersion === 10) {
      // 从 configJson 解析自由画布配置
      const config = JSON.parse(res.configJson || '{}')

      // ✅ 验证配置是否有效（必须包含 pageInfo）
      if (!config.pageInfo || Object.keys(config).length === 0) {
        console.warn('[自由画布] 配置为空或无效，创建默认配置')
        pageConfig.value = createEmptyPageConfig()
        // 保留数据库中的基本信息
        if (pageConfig.value.pageInfo) {
          pageConfig.value.pageInfo.pageName = res.pageName || '新页面'
          pageConfig.value.pageInfo.pageCode = res.pageCode || `page_${id}`
          pageConfig.value.pageInfo.pageType = res.pageType || 'custom'
          pageConfig.value.pageInfo.published = res.published || false
          pageConfig.value.pageInfo.routePath = res.routePath
        }
      } else {
        console.log('[自由画布] 解析配置成功:', config)
        pageConfig.value = config
      }
    } else if (res && res.configJson) {
      // 尝试从 configJson 解析配置
      try {
        const config = JSON.parse(res.configJson)
        if (config.version === 'free-canvas') {
          console.log('[自由画布] 检测到自由画布配置:', config)
          pageConfig.value = config
        } else {
          console.warn('[自由画布] 配置版本不匹配:', config.version)
          // 创建空配置，但保留基本信息
          pageConfig.value = createEmptyPageConfig()
          if (pageConfig.value.pageInfo && res.pageName) {
            pageConfig.value.pageInfo.pageName = res.pageName
          }
          if (pageConfig.value.pageInfo && res.pageCode) {
            pageConfig.value.pageInfo.pageCode = res.pageCode
          }
        }
      } catch (e) {
        console.error('[自由画布] 解析配置 JSON 失败:', e)
        pageConfig.value = createEmptyPageConfig()
        if (pageConfig.value.pageInfo && res.pageName) {
          pageConfig.value.pageInfo.pageName = res.pageName || `示例页面_${id}`
        }
        if (pageConfig.value.pageInfo) {
          pageConfig.value.pageInfo.pageCode = res.pageCode || `page_${id}`
        }
      }
    } else {
      // 不是自由画布页面，创建空配置
      console.warn('[自由画布] 不是自由画布页面，创建空配置')
      pageConfig.value = createEmptyPageConfig()
      if (pageConfig.value.pageInfo && res.pageName) {
        pageConfig.value.pageInfo.pageName = res.pageName || `示例页面_${id}`
      }
      if (pageConfig.value.pageInfo) {
        pageConfig.value.pageInfo.pageCode = res.pageCode || `page_${id}`
      }
    }
  } catch (error: any) {
    console.error('[自由画布] 加载页面失败:', error)
    ElMessage.error('加载页面失败: ' + (error.message || '未知错误'))
    // 创建空配置作为降级处理
    pageConfig.value = createEmptyPageConfig()
    if (pageConfig.value.pageInfo) {
      pageConfig.value.pageInfo.pageName = `页面_${id}`
      pageConfig.value.pageInfo.pageCode = `page_${id}`
    }
  }
}

/**
 * 处理返回
 */
function handleBack() {
  // 如果有未保存的更改，可以提示用户
  // router.push('/lowcode-v6/PageManageV6')
  router.back()  // 使用 router.back() 更灵活
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

  // 确保 pageInfo 存在
  if (!pageConfig.value.pageInfo) {
    ElMessage.warning('页面配置错误，缺少 pageInfo')
    return
  }

  if (!pageConfig.value.pageInfo.pageName) {
    ElMessage.warning('请输入页面名称')
    return
  }

  if (!pageConfig.value.pageInfo.pageCode) {
    ElMessage.warning('请输入页面编码')
    return
  }

  saving.value = true
  try {
    // 构造后端数据格式
    const backendData = {
      pageName: pageConfig.value.pageInfo.pageName,
      pageCode: pageConfig.value.pageInfo.pageCode,
      pageType: pageConfig.value.pageInfo.pageType || 'custom',
      layoutType: 'free-canvas',  // 自由画布布局类型
      configJson: JSON.stringify(pageConfig.value),  // 将完整配置序列化为 JSON
      configVersion: 10,  // 使用特殊版本号标识自由画布
      published: pageConfig.value.pageInfo.published || false,
      routePath: pageConfig.value.pageInfo.routePath,
      remark: '自由画布页面'
    }

    if (pageId.value && pageId.value > 0) {
      // 更新现有页面
      await request({
        url: `/page/${pageId.value}`,
        method: 'put',
        data: backendData
      })
      console.log('[自由画布] 更新页面成功:', pageId.value)
    } else {
      // 创建新页面
      const res = await request({
        url: '/page',
        method: 'post',
        data: backendData
      })
      pageId.value = res.id  // 保存返回的 pageId
      console.log('[自由画布] 创建页面成功:', pageId.value)
    }

    ElMessage.success(pageId.value > 0 ? '更新成功' : '保存成功')
  } catch (error: any) {
    console.error('[自由画布] 保存失败:', error)
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

/**
 * 处理发布
 */
async function handlePublish() {
  if (!pageConfig.value) return

  // 确保 pageInfo 存在
  if (!pageConfig.value.pageInfo) {
    ElMessage.warning('页面配置错误，缺少 pageInfo')
    return
  }

  if (!pageConfig.value.pageInfo.pageName) {
    ElMessage.warning('请输入页面名称')
    return
  }

  if (!pageConfig.value.pageInfo.pageCode) {
    ElMessage.warning('请输入页面编码')
    return
  }

  // 先保存
  if (!pageId.value || pageId.value < 0) {
    await handleSave()
  }

  publishing.value = true
  try {
    // 调用 API 发布页面
    await request({
      url: `/page/${pageId.value}/publish`,
      method: 'post',
      data: {
        routePath: pageConfig.value.pageInfo.routePath || `/free-canvas/${pageConfig.value.pageInfo.pageCode}`
      }
    })

    pageConfig.value.pageInfo.published = true
    ElMessage.success('发布成功')
  } catch (error: any) {
    console.error('[自由画布] 发布失败:', error)
    ElMessage.error('发布失败: ' + (error.response?.data?.message || error.message || '未知错误'))
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

    .page-code-input {
      width: 180px;
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
