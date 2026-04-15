<template>
  <div class="table-configurator">
    <!-- 左面板 - 配置区 -->
    <div
      class="config-panel"
      :class="{ collapsed: panelCollapsed }"
      :style="{ width: panelCollapsed ? '40px' : panelWidth + 'px' }"
    >
      <!-- 折叠按钮 -->
      <div class="collapse-btn" @click="panelCollapsed = !panelCollapsed">
        <el-icon :size="16">
          <ArrowLeft v-if="!panelCollapsed" />
          <ArrowRight v-else />
        </el-icon>
      </div>

      <!-- 面板内容 -->
      <div v-show="!panelCollapsed" class="panel-content">
        <el-tabs v-model="activeTab" class="config-tabs">
          <!-- 基本设置 -->
          <el-tab-pane label="基本设置" name="basic">
            <el-form label-width="80px" size="small">
              <el-form-item label="页面编码">
                <el-input v-model="config.pageCode" />
              </el-form-item>
              <el-form-item label="页面名称">
                <el-input v-model="config.pageName" />
              </el-form-item>
              <el-divider content-position="left">表格设置</el-divider>
              <el-form-item label="边框">
                <el-switch v-model="config.tableConfig.border" />
              </el-form-item>
              <el-form-item label="斑马纹">
                <el-switch v-model="config.tableConfig.stripe" />
              </el-form-item>
              <el-form-item label="分页">
                <el-switch v-model="config.tableConfig.showPagination" />
              </el-form-item>
              <el-form-item label="序号列">
                <el-switch v-model="config.tableConfig.showIndex" />
              </el-form-item>
              <el-form-item label="选择列">
                <el-switch v-model="config.tableConfig.showSelection" />
              </el-form-item>
              <el-form-item label="每页条数">
                <el-input-number v-model="config.tableConfig.pageSize" :min="5" :max="100" controls-position="right" />
              </el-form-item>
              <el-form-item label="组件尺寸">
                <el-select v-model="config.tableConfig.size" style="width: 100%">
                  <el-option label="大" value="large" />
                  <el-option label="默认" value="default" />
                  <el-option label="小" value="small" />
                </el-select>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 工具栏 -->
          <el-tab-pane label="工具栏" name="toolbar">
            <ToolbarButtonEditor v-model="config.toolbar.buttons" />
          </el-tab-pane>

          <!-- 搜索字段 -->
          <el-tab-pane label="搜索字段" name="search">
            <SearchFieldEditor v-model="config.searchFields" />
          </el-tab-pane>

          <!-- 表格列 -->
          <el-tab-pane label="表格列" name="columns">
            <TableColumnEditor v-model="config.tableColumns" />
          </el-tab-pane>
        </el-tabs>

        <!-- 保存按钮 -->
        <div class="save-bar">
          <el-button type="primary" @click="saveConfig" style="width: 100%">保存配置</el-button>
        </div>
      </div>

      <!-- 拖拽分隔条 -->
      <div
        v-show="!panelCollapsed"
        class="resize-handle"
        @mousedown="startResize"
      />
    </div>

    <!-- 右面板 - 预览区 -->
    <div class="preview-panel-wrapper">
      <PreviewPanel :config="config" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PreviewPanel from './configurator/PreviewPanel.vue'
import ToolbarButtonEditor from './configurator/ToolbarButtonEditor.vue'
import SearchFieldEditor from './configurator/SearchFieldEditor.vue'
import TableColumnEditor from './configurator/TableColumnEditor.vue'
import type { PageConfigResponse } from '@/api/table-standard'
import { getTableStandardConfig } from '@/api/table-standard'
import { registerConfig } from '@/utils/configRegistry'

const activeTab = ref('basic')
const panelCollapsed = ref(false)
const panelWidth = ref(400)

// 页面配置 - 从 mock 数据初始化
const config = reactive<PageConfigResponse>({
  pageCode: '',
  pageName: '',
  toolbar: { buttons: [] },
  searchFields: [],
  tableColumns: [],
  tableConfig: { border: true, stripe: true, showPagination: true, pageSize: 10, pageSizes: [10, 20, 50, 100] }
})

// 加载默认配置
getTableStandardConfig().then(cfg => {
  console.log('[TableStandardConfigurator] config loaded:', JSON.stringify(cfg.pageName), 'columns:', cfg.tableColumns?.length, 'searchFields:', cfg.searchFields?.length)
  Object.assign(config, cfg)
  console.log('[TableStandardConfigurator] config after assign:', config.tableColumns?.length, 'columns, toolbar buttons:', config.toolbar?.buttons?.length)
  // 自动注册默认配置
  registerConfig(config.pageCode, config.pageName, 'table', { ...config })
}).catch(err => {
  console.error('[TableStandardConfigurator] config load FAILED:', err)
})

// 保存配置（注册到 configRegistry）
const saveConfig = () => {
  registerConfig(config.pageCode, config.pageName, 'table', { ...config })
  ElMessage.success(`配置已保存: ${config.pageName} (${config.pageCode})`)
}

// 拖拽调整宽度
const startResize = (e: MouseEvent) => {
  e.preventDefault()
  const startX = e.clientX
  const startWidth = panelWidth.value

  const onMouseMove = (ev: MouseEvent) => {
    const delta = ev.clientX - startX
    const newWidth = Math.max(280, Math.min(startWidth + delta, window.innerWidth * 0.6))
    panelWidth.value = newWidth
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
  }

  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}
</script>

<style scoped lang="scss">
.table-configurator {
  display: flex;
  height: calc(100vh - 90px); // 减去 header 和 padding
  gap: 0;
  overflow: hidden;
}

.config-panel {
  position: relative;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-shrink: 0;
  transition: width 0.3s ease;

  &.collapsed {
    border-right: none;

    .collapse-btn {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      border: none;
      border-radius: 0;
      width: 40px;
      height: 40px;
    }
  }

  .collapse-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    z-index: 10;
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    background: #fff;
    transition: all 0.2s;

    &:hover {
      background: #ecf5ff;
      border-color: #409eff;
      color: #409eff;
    }
  }

  .panel-content {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
    padding-top: 44px; // 给折叠按钮让位
  }
}

.resize-handle {
  position: absolute;
  top: 0;
  right: -3px;
  width: 6px;
  height: 100%;
  cursor: col-resize;
  z-index: 5;

  &:hover {
    background: #409eff;
    opacity: 0.3;
  }
}

.config-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 12px;
  }

  :deep(.el-tabs__item) {
    font-size: 13px;
    padding: 0 12px;
  }

  :deep(.el-tabs__content) {
    overflow: visible;
  }
}

.save-bar {
  padding: 12px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 12px;
}

.preview-panel-wrapper {
  flex: 1;
  overflow: auto;
  padding: 0 0 0 16px;
}
</style>
