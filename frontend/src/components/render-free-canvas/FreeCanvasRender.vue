/**
 * 页面渲染器（运行时）
 * 主组件直接渲染，副组件按需从后端加载
 */
<template>
  <div class="page-render">
    <!-- 主组件区域 -->
    <div class="main-components">
      <template v-for="comp in mainComponents" :key="comp.id">
        <TableStandardRenderFreeCanvas
          v-if="comp.type === 'table-standard'"
          :config="comp.config as TableStandardComponentConfig"
          :comp-style="comp.style"
          :page-id="pageId"
          :component-id="comp.id"
          class="rendered-component"
          @action="handleAction($event, comp)"
        />
        <FormStandardRenderFreeCanvas
          v-else-if="comp.type === 'form-standard'"
          :config="comp.config as FormStandardComponentConfig"
          :comp-style="comp.style"
          class="rendered-component"
          @action="handleAction($event, comp)"
        />
      </template>
    </div>

    <!-- 副组件弹窗（按需加载） -->
    <el-dialog
      v-for="targetId in activeDialogIds"
      :key="targetId"
      v-model="dialogVisibleMap[targetId]"
      :title="getDialogTitle(targetId)"
      width="700px"
      destroy-on-close
      @closed="onDialogClosed(targetId)"
    >
      <div v-if="dialogLoading[targetId]" v-loading="true" style="min-height: 200px" />
      <template v-else-if="loadedLinkedConfig[targetId]">
        <FormStandardRenderFreeCanvas
          v-if="loadedLinkedConfig[targetId].type === 'form-standard'"
          :config="loadedLinkedConfig[targetId].config as FormStandardComponentConfig"
          :comp-style="loadedLinkedConfig[targetId].style"
          :mode="dialogDataMap[targetId]?.mode || 'add'"
          :initial-data="dialogDataMap[targetId]?.data"
          @submit="handleFormSubmit($event, targetId)"
          @cancel="dialogVisibleMap[targetId] = false"
        />
        <TableStandardRenderFreeCanvas
          v-else-if="loadedLinkedConfig[targetId].type === 'table-standard'"
          :config="loadedLinkedConfig[targetId].config as TableStandardComponentConfig"
          :comp-style="loadedLinkedConfig[targetId].style"
        />
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '@/utils/request'
import TableStandardRenderFreeCanvas from './TableStandardRenderFreeCanvas.vue'
import FormStandardRenderFreeCanvas from './FormStandardRenderFreeCanvas.vue'
import type {
  FreeCanvasPageConfig,
  TableStandardComponentConfig,
  FormStandardComponentConfig,
  ComponentInstance
} from '@/types/page-free-canvas'

interface Props {
  config: FreeCanvasPageConfig
  pageId?: number
}

const props = defineProps<Props>()

// 主组件：role !== 'linked'
const mainComponents = computed(() => {
  return (props.config?.components || []).filter((c: ComponentInstance) => c.enabled !== false && c.role !== 'linked')
})

// 弹窗可见性
const dialogVisibleMap = reactive<Record<string, boolean>>({})
// 弹窗加载状态
const dialogLoading = reactive<Record<string, boolean>>({})
// 弹窗数据：mode + 表单初始数据
const dialogDataMap = reactive<Record<string, { mode: string; data?: any }>>({})
// 已加载的副组件配置（从后端获取）
const loadedLinkedConfig = reactive<Record<string, any>>({})

// 当前打开的弹窗 ID 列表（用于 v-for）
const activeDialogIds = computed(() => Object.keys(dialogVisibleMap).filter(id => dialogVisibleMap[id]))

const modeLabels: Record<string, string> = {
  add: '新增', edit: '编辑', view: '查看', detail: '查看', custom: ''
}

function getDialogTitle(targetId: string): string {
  const comp = loadedLinkedConfig[targetId]
  const modeLabel = modeLabels[dialogDataMap[targetId]?.mode] || ''
  const name = comp?.name || '组件'
  return modeLabel ? `${modeLabel} - ${name}` : name
}

function onDialogClosed(targetId: string) {
  delete dialogDataMap[targetId]
  delete loadedLinkedConfig[targetId]
  delete dialogLoading[targetId]
}

// 按钮动作处理
async function handleAction(actionData: { action: string; actionConfig?: any; row?: any }, _comp: any) {
  const { action, actionConfig, row } = actionData

  if (action === 'delete') {
    handleDelete(actionConfig, row)
    return
  }

  if (!actionConfig) return
  const targetId = actionConfig.targetCode
  if (!targetId) return

  if (actionConfig.type === 'openForm' || actionConfig.type === 'openTable') {
    let mode = action === 'detail' ? 'view' : action
    dialogDataMap[targetId] = { mode, data: row ? { ...row } : undefined }
    dialogVisibleMap[targetId] = true
    dialogLoading[targetId] = true

    try {
      // 1. 从后端获取副组件配置
      const compDetail = await request.post<any>('/page-component/detail', {
        pageId: props.pageId,
        data: { componentId: targetId }
      })
      loadedLinkedConfig[targetId] = compDetail

      // 2. 编辑/查看模式：获取详情数据
      if ((mode === 'edit' || mode === 'view') && row?.id != null) {
        const detailUrl = compDetail?.config?.apiUrl || '/table-data/detail'
        const detailData = await request.post<any>(detailUrl, {
          pageId: props.pageId,
          data: { componentId: targetId, mode, id: row.id }
        })
        dialogDataMap[targetId] = { mode, data: detailData }
      }
    } catch (e: any) {
      ElMessage.error('加载组件配置失败: ' + (e.message || ''))
      dialogVisibleMap[targetId] = false
    } finally {
      dialogLoading[targetId] = false
    }
  }
}

async function handleDelete(actionConfig: any, row: any) {
  try {
    await ElMessageBox.confirm(
      actionConfig?.confirmText || '确定删除该条数据吗？',
      '提示',
      { type: 'warning' }
    )
    const url = actionConfig?.apiUrl || '/table-data/delete'
    await request.post(url, { pageId: props.pageId, data: row })
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

async function handleFormSubmit(formData: { mode: string; data: Record<string, any> }, targetId: string) {
  const compConfig = loadedLinkedConfig[targetId]

  // 从副组件按钮中找保存按钮的 apiUrl
  let saveUrl = '/table-data/save'
  const buttons = compConfig?.config?.toolbar?.buttons || []
  for (const btn of buttons) {
    if (btn.actionConfig?.apiUrl) {
      saveUrl = btn.actionConfig.apiUrl
      break
    }
  }

  try {
    await request.post(saveUrl, {
      pageId: props.pageId,
      data: { componentId: targetId, ...formData.data }
    })
    ElMessage.success(formData.mode === 'edit' ? '保存成功' : '新增成功')
    dialogVisibleMap[targetId] = false
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}
</script>

<style scoped lang="scss">
.page-render {
  padding: 16px;
  min-height: 100%;
  background: #f5f7fa;
}

.main-components {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
}

.rendered-component {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}
</style>
