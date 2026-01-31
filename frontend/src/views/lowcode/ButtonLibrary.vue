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
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
  type FormInstance,
  type FormRules
} from '@element-plus/icons-vue'
import { getButtonLibrary } from '@/api/library'

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
  } else {
    isEdit.value = false
    resetForm()
  }
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.value = {
    buttonName: '',
    buttonCode: '',
    buttonType: 'default',
    actionType: 'custom',
    position: 'toolbar',
    componentCategory: activeLibrary.value
  }
  tagsInput.value = ''
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 将标签输入转换为 JSON 数组
        const tags = tagsInput.value
          .split(',')
          .map((tag) => tag.trim())
          .filter((tag) => tag)
        formData.value.componentTags = JSON.stringify(tags)

        // TODO: 调用保存 API
        // await saveButtonConfig(formData.value)

        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadButtons()
      } catch (error) {
        ElMessage.error('保存失败')
        console.error(error)
      }
    }
  })
}

// 删除按钮
const deleteButton = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个按钮吗？', '提示', {
      type: 'warning'
    })

    // TODO: 调用删除 API
    // await deleteButtonConfig(id)

    ElMessage.success('删除成功')
    loadButtons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
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
</style>
