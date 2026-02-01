<template>
  <div class="button-library-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <el-tabs v-model="activeLibrary" @tab-change="handleLibraryChange">
        <el-tab-pane label="通用按钮库" name="common" />
        <el-tab-pane label="业务按钮库" name="business" />
      </el-tabs>

      <div class="toolbar-actions">
        <el-input
          v-model="searchText"
          placeholder="搜索按钮名称或编码"
          clearable
          style="width: 250px; margin-right: 12px"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="openButtonEditor()">
          <el-icon><Plus /></el-icon>
          新增按钮
        </el-button>
        <el-button @click="loadButtons">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 按钮列表 -->
    <div class="button-list">
      <el-card
        v-for="btn in filteredButtons"
        :key="btn.id"
        class="button-card"
        shadow="hover"
      >
        <template #header>
          <div class="card-header">
            <div class="button-info">
              <el-icon v-if="btn.icon" :size="20">
                <component :is="btn.icon" />
              </el-icon>
              <span class="button-name">{{ btn.buttonName }}</span>
            </div>
            <div class="card-actions">
              <el-button link type="success" @click="handleSyncToPages(btn)">
                <el-icon><Connection /></el-icon>
                同步
              </el-button>
              <el-button link type="primary" @click="openButtonEditor(btn)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button link type="danger" @click="deleteButton(btn.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </template>

        <div class="card-body">
          <div class="button-field">
            <span class="field-label">按钮编码：</span>
            <el-tag size="small">{{ btn.buttonCode }}</el-tag>
          </div>

          <div class="button-field">
            <span class="field-label">按钮类型：</span>
            <el-tag :type="getButtonTypeColor(btn.buttonType)" size="small">
              {{ btn.buttonType }}
            </el-tag>
          </div>

          <div class="button-field">
            <span class="field-label">动作类型：</span>
            <el-tag type="info" size="small">{{ btn.actionType }}</el-tag>
          </div>

          <div class="button-field">
            <span class="field-label">位置：</span>
            <el-tag type="warning" size="small">{{ btn.position }}</el-tag>
          </div>

          <div v-if="btn.componentTags" class="button-field">
            <span class="field-label">标签：</span>
            <div class="tags-container">
              <el-tag
                v-for="tag in parseTags(btn.componentTags)"
                :key="tag"
                size="small"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <div v-if="btn.perms" class="button-field">
            <span class="field-label">权限：</span>
            <el-tag type="success" size="small">{{ btn.perms }}</el-tag>
          </div>
        </div>
      </el-card>

      <el-empty
        v-if="filteredButtons.length === 0"
        description="暂无按钮配置"
        :image-size="120"
      />
    </div>

    <!-- 按钮编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑按钮' : '新增按钮'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="按钮名称" prop="buttonName">
          <el-input v-model="formData.buttonName" placeholder="请输入按钮名称" />
        </el-form-item>

        <el-form-item label="按钮编码" prop="buttonCode">
          <el-input v-model="formData.buttonCode" placeholder="请输入按钮编码" />
        </el-form-item>

        <el-form-item label="组件分类" prop="componentCategory">
          <el-radio-group v-model="formData.componentCategory">
            <el-radio label="common">通用组件库</el-radio>
            <el-radio label="business">业务组件库</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="按钮类型" prop="buttonType">
          <el-select v-model="formData.buttonType" placeholder="请选择按钮类型">
            <el-option label="主要按钮" value="primary" />
            <el-option label="成功按钮" value="success" />
            <el-option label="警告按钮" value="warning" />
            <el-option label="危险按钮" value="danger" />
            <el-option label="信息按钮" value="info" />
            <el-option label="默认按钮" value="default" />
          </el-select>
        </el-form-item>

        <el-form-item label="动作类型" prop="actionType">
          <el-select v-model="formData.actionType" placeholder="请选择动作类型">
            <el-option label="API 请求" value="api" />
            <el-option label="打开对话框" value="dialog" />
            <el-option label="打开抽屉" value="drawer" />
            <el-option label="跳转路由" value="route" />
            <el-option label="跳转链接" value="link" />
            <el-option label="自定义" value="custom" />
            <el-option label="确认操作" value="confirm" />
          </el-select>
        </el-form-item>

        <el-form-item label="位置" prop="position">
          <el-select v-model="formData.position" placeholder="请选择位置">
            <el-option label="工具栏" value="toolbar" />
            <el-option label="行操作" value="row" />
            <el-option label="表单内" value="form" />
            <el-option label="对话框" value="dialog" />
            <el-option label="底部" value="footer" />
          </el-select>
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入图标名称 (如: Plus)" />
        </el-form-item>

        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="formData.perms" placeholder="请输入权限标识 (如: low:button:add)" />
        </el-form-item>

        <el-form-item label="组件标签" prop="componentTags">
          <el-input
            v-model="tagsInput"
            type="textarea"
            :rows="2"
            placeholder="请输入标签，用逗号分隔 (如: system,create,crud)"
          />
        </el-form-item>

        <el-form-item label="动作配置">
          <el-button @click="openActionConfigDialog">
            <el-icon><Setting /></el-icon>
            {{ hasActionConfig ? '修改动作配置' : '配置动作' }}
          </el-button>
          <el-tag v-if="hasActionConfig" type="success" style="margin-left: 8px">已配置</el-tag>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 按钮动作配置对话框 -->
    <ButtonActionConfig
      v-model:visible="actionConfigVisible"
      :config="actionConfigData"
      @confirm="handleActionConfigConfirm"
    />

    <!-- 同步到页面对话框 -->
    <el-dialog
      v-model="syncDialogVisible"
      title="同步模板到页面"
      width="700px"
    >
      <div v-loading="loadingReferences" class="sync-dialog-content">
        <el-alert
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        >
          <template #title>
            <div>当前模板：{{ currentSyncButton?.buttonName }}</div>
          </template>
        </el-alert>

        <el-empty
          v-if="!loadingReferences && referencedPages.length === 0"
          description="暂无页面引用此模板"
        />

        <div v-else class="pages-selection">
          <div class="selection-header">
            <el-checkbox
              v-model="selectAllPages"
              :indeterminate="isIndeterminate"
              @change="handleSelectAllPages"
            >
              全选（已选择 {{ selectedPageIds.length }} / {{ referencedPages.length }} 个页面）
            </el-checkbox>
          </div>

          <el-checkbox-group v-model="selectedPageIds" class="pages-list">
            <div
              v-for="page in referencedPages"
              :key="page.pageId"
              class="page-item"
            >
              <el-checkbox :label="page.pageId" class="page-checkbox">
                <div class="page-info">
                  <div class="page-name">{{ page.pageName }}</div>
                  <div class="page-meta">
                    <el-tag size="small" type="info">{{ page.pageCode }}</el-tag>
                    <span class="reference-path">路径：{{ page.referencePath }}</span>
                    <el-tag
                      v-if="page.isLinked"
                      size="small"
                      type="success"
                    >
                      已引用
                    </el-tag>
                    <el-tag
                      v-else
                      size="small"
                      type="warning"
                    >
                      独立副本
                    </el-tag>
                  </div>
                </div>
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <div v-if="referencedPages.length > 0" class="sync-strategy">
          <div class="strategy-label">同步策略：</div>
          <el-radio-group v-model="syncStrategy">
            <el-radio label="merge">
              <div class="strategy-option">
                <div class="strategy-title">合并（推荐）</div>
                <div class="strategy-desc">保留页面自定义配置，仅更新模板基础配置</div>
              </div>
            </el-radio>
            <el-radio label="replace">
              <div class="strategy-option">
                <div class="strategy-title">替换</div>
                <div class="strategy-desc">清空页面自定义配置，完全使用模板配置</div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>

      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="syncing"
          :disabled="selectedPageIds.length === 0"
          @click="handleExecuteSync"
        >
          同步到 {{ selectedPageIds.length }} 个页面
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Refresh,
  Edit,
  Delete,
  Setting,
  Connection,
  type FormInstance,
  type FormRules
} from '@element-plus/icons-vue'
import { getButtonLibrary, getTemplateReferences, syncTemplateToPages } from '@/api/library'
import { createButton, updateButton, deleteButton as deleteButtonApi } from '@/api/button'
import type { TemplateReferenceResult, PageReference } from '@/types/page-v6'
import ButtonActionConfig from '@/components/designer/ButtonActionConfig.vue'
import type { ActionConfig } from '@/utils/buttonActionHandler'

interface ButtonConfig {
  id?: number
  buttonName: string
  buttonCode: string
  buttonType: string
  actionType: string
  position: string
  icon?: string
  perms?: string
  componentCategory: string
  componentTags?: string
}

const activeLibrary = ref<'common' | 'business'>('common')
const searchText = ref('')
const buttons = ref<ButtonConfig[]>([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const tagsInput = ref('')

// 动作配置相关
const actionConfigVisible = ref(false)
const actionConfigData = ref<ActionConfig | undefined>(undefined)

// 同步相关
const syncDialogVisible = ref(false)
const currentSyncButton = ref<ButtonConfig | null>(null)
const loadingReferences = ref(false)
const referencedPages = ref<PageReference[]>([])
const selectedPageIds = ref<number[]>([])
const selectAllPages = ref(false)
const isIndeterminate = ref(false)
const syncStrategy = ref<'merge' | 'replace'>('merge')
const syncing = ref(false)

const formData = ref<ButtonConfig>({
  buttonName: '',
  buttonCode: '',
  buttonType: 'default',
  actionType: 'custom',
  position: 'toolbar',
  componentCategory: 'business'
})

const formRules: FormRules = {
  buttonName: [{ required: true, message: '请输入按钮名称', trigger: 'blur' }],
  buttonCode: [{ required: true, message: '请输入按钮编码', trigger: 'blur' }],
  componentCategory: [{ required: true, message: '请选择组件分类', trigger: 'change' }],
  buttonType: [{ required: true, message: '请选择按钮类型', trigger: 'change' }],
  actionType: [{ required: true, message: '请选择动作类型', trigger: 'change' }],
  position: [{ required: true, message: '请选择位置', trigger: 'change' }]
}

// 是否已配置动作
const hasActionConfig = computed(() => {
  return formData.value.actionConfig && formData.value.actionConfig !== '{}'
})

// 过滤后的按钮列表
const filteredButtons = computed(() => {
  if (!searchText.value) {
    return buttons.value
  }
  const keyword = searchText.value.toLowerCase()
  return buttons.value.filter(
    (btn) =>
      btn.buttonName.toLowerCase().includes(keyword) ||
      btn.buttonCode.toLowerCase().includes(keyword)
  )
})

// 加载按钮列表
const loadButtons = async () => {
  loading.value = true
  try {
    buttons.value = await getButtonLibrary(activeLibrary.value)
  } catch (error) {
    ElMessage.error('加载按钮列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 切换组件库
const handleLibraryChange = () => {
  loadButtons()
}

// 搜索
const handleSearch = () => {
  // 使用 computed 自动过滤
}

// 解析标签
const parseTags = (tagsStr: string | undefined) => {
  if (!tagsStr) return []
  try {
    return JSON.parse(tagsStr)
  } catch {
    return []
  }
}

// 获取按钮类型颜色
const getButtonTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    primary: 'primary',
    success: 'success',
    warning: 'warning',
    danger: 'danger',
    info: 'info',
    default: 'info'
  }
  return colorMap[type] || 'info'
}

// 打开编辑器
const openButtonEditor = (btn?: ButtonConfig) => {
  if (btn) {
    isEdit.value = true
    formData.value = { ...btn }
    tagsInput.value = parseTags(btn.componentTags).join(',')

    // 解析动作配置
    if (btn.actionConfig) {
      try {
        actionConfigData.value = JSON.parse(btn.actionConfig)
      } catch {
        actionConfigData.value = undefined
      }
    } else {
      actionConfigData.value = undefined
    }
  } else {
    isEdit.value = false
    resetForm()
  }
  dialogVisible.value = true
}

// 打开动作配置对话框
const openActionConfigDialog = () => {
  actionConfigVisible.value = true
}

// 动作配置确认
const handleActionConfigConfirm = (config: ActionConfig) => {
  actionConfigData.value = config
  // 将配置保存到 formData
  formData.value.actionConfig = JSON.stringify(config)
  ElMessage.success('动作配置已保存')
}

// 重置表单
const resetForm = () => {
  // 先重置表单字段和验证状态
  formRef.value?.resetFields()
  // 然后设置默认值
  formData.value = {
    buttonName: '',
    buttonCode: '',
    buttonType: 'default',
    actionType: 'custom',
    position: 'toolbar',
    componentCategory: activeLibrary.value
  }
  tagsInput.value = ''
  actionConfigData.value = undefined
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) {
    ElMessage.error('表单未初始化')
    return
  }

  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.warning('请检查表单填写是否完整')
    return
  }

  try {
    // 将标签输入转换为 JSON 数组
    const tags = tagsInput.value
      .split(',')
      .map((tag) => tag.trim())
      .filter((tag) => tag)
    formData.value.componentTags = JSON.stringify(tags)

    // 调用保存 API
    if (isEdit.value && formData.value.id) {
      await updateButton(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createButton(formData.value)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadButtons()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
    console.error(error)
  }
}

// 删除按钮
const deleteButton = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个按钮吗？', '提示', {
      type: 'warning'
    })

    await deleteButtonApi(id)
    ElMessage.success('删除成功')
    loadButtons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

// 同步到页面
const handleSyncToPages = async (btn: ButtonConfig) => {
  if (!btn.id) {
    ElMessage.warning('按钮ID不存在')
    return
  }

  currentSyncButton.value = btn
  syncDialogVisible.value = true
  selectedPageIds.value = []
  selectAllPages.value = false
  isIndeterminate.value = false

  await loadReferencedPages(btn.id)
}

// 加载引用的页面列表
const loadReferencedPages = async (templateId: number) => {
  loadingReferences.value = true
  try {
    const result: TemplateReferenceResult = await getTemplateReferences('button', templateId)
    referencedPages.value = result.pages || []

    // 默认只选择已引用的页面
    selectedPageIds.value = referencedPages.value
      .filter(p => p.isLinked)
      .map(p => p.pageId)

    updateSelectAllState()
  } catch (error: any) {
    ElMessage.error('加载引用页面失败: ' + (error.message || '未知错误'))
    referencedPages.value = []
  } finally {
    loadingReferences.value = false
  }
}

// 全选/取消全选
const handleSelectAllPages = (checked: boolean) => {
  selectedPageIds.value = checked ? referencedPages.value.map(p => p.pageId) : []
  isIndeterminate.value = false
}

// 更新全选状态
const updateSelectAllState = () => {
  const totalCount = referencedPages.value.length
  const selectedCount = selectedPageIds.value.length

  selectAllPages.value = selectedCount === totalCount && totalCount > 0
  isIndeterminate.value = selectedCount > 0 && selectedCount < totalCount
}

// 监听 selectedPageIds 变化，更新全选状态
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const updateSelectState = computed(() => {
  updateSelectAllState()
  return selectedPageIds.value.length
})

// 执行同步
const handleExecuteSync = async () => {
  if (selectedPageIds.value.length === 0) {
    ElMessage.warning('请选择要同步的页面')
    return
  }

  if (!currentSyncButton.value?.id) {
    ElMessage.warning('按钮ID不存在')
    return
  }

  // 确认同步
  const strategyText = syncStrategy.value === 'merge' ? '合并（保留自定义配置）' : '替换（清空自定义配置）'
  try {
    await ElMessageBox.confirm(
      `确定要同步到 ${selectedPageIds.value.length} 个页面吗？\n同步策略：${strategyText}`,
      '确认同步',
      {
        type: 'warning',
        confirmButtonText: '确认同步',
        cancelButtonText: '取消'
      }
    )
  } catch {
    return
  }

  syncing.value = true
  try {
    const result = await syncTemplateToPages(
      'button',
      currentSyncButton.value.id,
      selectedPageIds.value,
      syncStrategy.value
    )

    if (result.failedCount === 0) {
      ElMessage.success(`同步成功！已同步到 ${result.successCount} 个页面`)
    } else {
      ElMessage.warning(`同步完成，成功 ${result.successCount} 个，失败 ${result.failedCount} 个`)
      if (result.errors.length > 0) {
        console.error('同步错误详情:', result.errors)
      }
    }

    syncDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error('同步失败: ' + (error.message || '未知错误'))
    console.error(error)
  } finally {
    syncing.value = false
  }
}

onMounted(() => {
  loadButtons()
})
</script>

<style scoped lang="scss">
.button-library-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .toolbar-actions {
    display: flex;
    align-items: center;
  }
}

.button-list {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
  padding-bottom: 20px;
}

.button-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .button-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .button-name {
        font-size: 15px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }

    .card-actions {
      display: flex;
      gap: 4px;
    }
  }

  .card-body {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .button-field {
      display: flex;
      align-items: center;
      font-size: 13px;

      .field-label {
        min-width: 80px;
        color: var(--el-text-color-secondary);
      }

      .tags-container {
        display: flex;
        gap: 4px;
        flex-wrap: wrap;

        .tag-item {
          margin: 0;
        }
      }
    }
  }
}

// 同步对话框样式
.sync-dialog-content {
  .pages-selection {
    .selection-header {
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 16px;
    }

    .pages-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-height: 300px;
      overflow-y: auto;
      padding: 8px;

      .page-item {
        .page-checkbox {
          width: 100%;
          margin: 0;

          :deep(.el-checkbox__label) {
            width: 100%;
            padding-left: 8px;
          }
        }

        .page-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 6px;

          .page-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .page-meta {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 12px;

            .reference-path {
              color: #909399;
            }
          }
        }
      }
    }
  }

  .sync-strategy {
    margin-top: 20px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 4px;

    .strategy-label {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
    }

    .el-radio-group {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .el-radio {
        margin: 0;
        padding: 12px;
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
        }

        &.is-checked {
          border-color: #409eff;
          background: #ecf5ff;
        }

        :deep(.el-radio__label) {
          width: 100%;
        }

        .strategy-option {
          margin-left: 8px;

          .strategy-title {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .strategy-desc {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }
}
</style>
