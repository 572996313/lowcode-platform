<template>
  <div class="area-config tabs-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>标签页配置</span>
        <el-button
          type="primary"
          size="small"
          @click="addTab"
        >
          添加标签页
        </el-button>
      </div>
      <div class="section-body">
        <div ref="draggableContainer" class="draggable-wrapper">
          <div v-if="!localConfig.tabs?.length" class="empty-hint">
            暂无标签页，点击上方按钮添加
          </div>
          <div v-else class="tab-list">
            <div
              v-for="(tab, index) in localConfig.tabs"
              :key="tab.id"
              class="tab-item"
            >
              <el-icon class="drag-handle">
                <Rank />
              </el-icon>
              <div class="tab-info">
                <div class="tab-label">{{ tab.label }}</div>
                <div class="tab-name">{{ tab.name }}</div>
              </div>
              <div class="tab-actions">
                <el-button
                  size="small"
                  text
                  type="primary"
                  @click="editTab(tab, index)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  text
                  type="danger"
                  @click="removeTab(index)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="config-section">
      <div class="section-header">
        <span>布局设置</span>
      </div>
      <div class="section-body">
        <el-form-item label="默认标签">
          <el-input-number
            v-model="localConfig.defaultTab"
            :min="0"
            :max="(localConfig.tabs?.length || 1) - 1"
          />
        </el-form-item>

        <el-form-item label="标签位置">
          <el-radio-group v-model="localConfig.position">
            <el-radio value="top">顶部</el-radio>
            <el-radio value="right">右侧</el-radio>
            <el-radio value="bottom">底部</el-radio>
            <el-radio value="left">左侧</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="可拉伸">
          <el-switch v-model="localConfig.stretch" />
        </el-form-item>
      </div>
    </div>

    <!-- 标签页编辑对话框 -->
    <el-dialog
      v-model="tabDialogVisible"
      title="编辑标签页"
      width="600px"
    >
      <el-form :model="editingTab" label-width="100px">
        <el-form-item label="标签标题">
          <el-input v-model="editingTab.label" placeholder="标签显示名称" />
        </el-form-item>
        <el-form-item label="标签名称">
          <el-input v-model="editingTab.name" placeholder="标签唯一标识" />
        </el-form-item>
        <el-form-item label="内容类型">
          <el-select v-model="editingTab.contentType" placeholder="选择类型">
            <el-option label="表格" value="table" />
            <el-option label="表单" value="form" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置ID">
          <el-input v-model="editingTab.configId" placeholder="关联的配置ID" type="number" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="editingTab.icon" placeholder="图标名称" />
        </el-form-item>
        <el-form-item label="禁用">
          <el-switch v-model="editingTab.disabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tabDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTab">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Rank } from '@element-plus/icons-vue'
import type { NormalizedArea } from '@/api/page'

interface Props {
  area: NormalizedArea
}

interface Emits {
  (e: 'update', area: NormalizedArea): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 拖拽容器引用
const draggableContainer = ref<HTMLElement | null>(null)

// 默认配置
const defaultConfig = {
  tabs: [],
  defaultTab: 0,
  position: 'top',
  stretch: false
}

// 本地配置 - 使用只读 computed
const localConfig = computed(() => {
  const config = props.area.config
  if (!config) {
    return defaultConfig
  }
  return config
})

// 标签页编辑
const tabDialogVisible = ref(false)
const editingTabIndex = ref(-1)
const editingTab = ref<any>({})

const addTab = () => {
  const newTab = {
    id: `tab_${Date.now()}`,
    label: '新标签页',
    name: `tab_${Date.now()}`,
    contentType: 'custom',
    configId: null,
    icon: '',
    disabled: false
  }

  const currentConfig = localConfig.value
  const updatedConfig = {
    ...currentConfig,
    tabs: [...(currentConfig.tabs || []), newTab]
  }

  emit('update', { ...props.area, config: updatedConfig })
}

const editTab = (tab: any, index: number) => {
  editingTabIndex.value = index
  editingTab.value = { ...tab }
  tabDialogVisible.value = true
}

const saveTab = () => {
  if (editingTabIndex.value >= 0) {
    const currentConfig = localConfig.value
    const updatedTabs = [...(currentConfig.tabs || [])]
    updatedTabs[editingTabIndex.value] = { ...editingTab.value }

    const updatedConfig = {
      ...currentConfig,
      tabs: updatedTabs
    }

    emit('update', { ...props.area, config: updatedConfig })
  }
  tabDialogVisible.value = false
}

const removeTab = (index: number) => {
  const currentConfig = localConfig.value
  const updatedTabs = [...(currentConfig.tabs || [])]
  updatedTabs.splice(index, 1)

  const updatedConfig = {
    ...currentConfig,
    tabs: updatedTabs
  }

  // 调整默认标签索引
  if (updatedConfig.defaultTab >= updatedConfig.tabs.length) {
    updatedConfig.defaultTab = Math.max(0, updatedConfig.tabs.length - 1)
  }

  emit('update', { ...props.area, config: updatedConfig })
}
</script>

<style scoped lang="scss">
.area-config {
  .config-section {
    margin-bottom: 20px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    overflow: hidden;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 15px;
      background: var(--el-fill-color-light);
      border-bottom: 1px solid var(--el-border-color);
      font-weight: 500;
    }

    .section-body {
      padding: 15px;
    }
  }

  .empty-hint {
    text-align: center;
    padding: 30px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }

  .draggable-wrapper {
    min-height: 60px;
  }

  .tab-list {
    display: flex;
    flex-direction: column;
  }

  .tab-item {
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 8px;
    background: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .drag-handle {
      margin-right: 10px;
      cursor: move;
      color: var(--el-text-color-secondary);
    }

    .tab-info {
      flex: 1;

      .tab-label {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }

      .tab-name {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }

    .tab-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
