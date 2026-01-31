<template>
  <div class="area-config toolbar-area-config">
    <div class="config-section">
      <div class="section-header">
        <span>工具栏配置</span>
        <el-button
          type="primary"
          size="small"
          @click="addButton"
        >
          添加按钮
        </el-button>
      </div>
      <div class="section-body">
        <div ref="draggableContainer" class="draggable-wrapper">
          <div v-if="!localConfig.buttons?.length" class="empty-hint">
            暂无按钮，点击上方按钮添加
          </div>
          <div v-else class="button-list">
            <div
              v-for="(button, index) in localConfig.buttons"
              :key="button.id"
              class="button-item"
            >
              <el-icon class="drag-handle">
                <Rank />
              </el-icon>
              <div class="button-info">
                <div class="button-label">{{ button.label }}</div>
                <div class="button-code">{{ button.buttonCode }}</div>
              </div>
              <div class="button-actions">
                <el-button
                  size="small"
                  text
                  type="primary"
                  @click="editButton(button, index)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  text
                  type="danger"
                  @click="removeButton(index)"
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
        <el-form-item label="对齐方式">
          <el-radio-group v-model="localConfig.align">
            <el-radio value="left">左对齐</el-radio>
            <el-radio value="center">居中</el-radio>
            <el-radio value="right">右对齐</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
    </div>

    <!-- 按钮编辑对话框 -->
    <el-dialog
      v-model="buttonDialogVisible"
      title="编辑按钮"
      width="600px"
    >
      <el-form :model="editingButton" label-width="100px">
        <el-form-item label="按钮标签">
          <el-input v-model="editingButton.label" placeholder="按钮显示名称" />
        </el-form-item>
        <el-form-item label="按钮代码">
          <el-input v-model="editingButton.buttonCode" placeholder="按钮唯一标识" />
        </el-form-item>
        <el-form-item label="按钮类型">
          <el-select v-model="editingButton.buttonType" placeholder="选择类型">
            <el-option label="默认" value="" />
            <el-option label="主要" value="primary" />
            <el-option label="成功" value="success" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
            <el-option label="信息" value="info" />
          </el-select>
        </el-form-item>
        <el-form-item label="按钮图标">
          <el-input v-model="editingButton.icon" placeholder="图标名称" />
        </el-form-item>
        <el-form-item label="按钮动作">
          <el-select v-model="editingButton.action" placeholder="选择动作">
            <el-option label="新增" value="add" />
            <el-option label="编辑" value="edit" />
            <el-option label="删除" value="delete" />
            <el-option label="导出" value="export" />
            <el-option label="导入" value="import" />
            <el-option label="刷新" value="refresh" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="editingButton.apiUrl" placeholder="API接口地址" />
        </el-form-item>
        <el-form-item label="确认提示">
          <el-switch v-model="editingButton.confirm" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="buttonDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveButton">确定</el-button>
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
const defaultConfig = { buttons: [], align: 'left' }

// 本地配置 - 使用 watch 而不是 computed setter
const localConfig = computed(() => {
  const config = props.area.config
  if (!config) {
    return defaultConfig
  }
  return config
})

// 按钮编辑
const buttonDialogVisible = ref(false)
const editingButtonIndex = ref(-1)
const editingButton = ref<any>({})

const addButton = () => {
  const newButton = {
    id: `btn_${Date.now()}`,
    label: '新按钮',
    buttonCode: '',
    buttonType: 'primary',
    icon: '',
    action: 'custom',
    apiUrl: '',
    confirm: false
  }

  const currentConfig = localConfig.value
  const updatedConfig = {
    ...currentConfig,
    buttons: [...(currentConfig.buttons || []), newButton]
  }

  emit('update', { ...props.area, config: updatedConfig })
}

const editButton = (button: any, index: number) => {
  editingButtonIndex.value = index
  editingButton.value = { ...button }
  buttonDialogVisible.value = true
}

const saveButton = () => {
  if (editingButtonIndex.value >= 0) {
    const currentConfig = localConfig.value
    const updatedButtons = [...(currentConfig.buttons || [])]
    updatedButtons[editingButtonIndex.value] = { ...editingButton.value }

    const updatedConfig = {
      ...currentConfig,
      buttons: updatedButtons
    }

    emit('update', { ...props.area, config: updatedConfig })
  }
  buttonDialogVisible.value = false
}

const removeButton = (index: number) => {
  const currentConfig = localConfig.value
  const updatedButtons = [...(currentConfig.buttons || [])]
  updatedButtons.splice(index, 1)

  const updatedConfig = {
    ...currentConfig,
    buttons: updatedButtons
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

  .button-list {
    display: flex;
    flex-direction: column;
  }

  .button-item {
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

    .button-info {
      flex: 1;

      .button-label {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }

      .button-code {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }

    .button-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
