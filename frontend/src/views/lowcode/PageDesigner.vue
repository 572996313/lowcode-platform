<template>
  <div class="page-designer">
    <!-- 顶部工具栏 -->
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack" icon="ArrowLeft">返回</el-button>
        <span class="page-title">{{ isEdit ? '编辑页面' : '新建页面' }}</span>
      </div>
      <div class="header-right">
        <el-button @click="handlePreview" icon="View">预览</el-button>
        <el-button type="primary" @click="handleSave" icon="Check">保存</el-button>
      </div>
    </div>

    <!-- 主设计区域 - 三面板布局 -->
    <div class="designer-main">
      <!-- 左侧组件库面板 -->
      <div class="left-panel">
        <div class="panel-title">组件库</div>

        <!-- 查询区组件 -->
        <div class="component-section">
          <div class="section-title">查询区</div>
          <div class="component-list">
            <div class="component-item" @click="addSearchField('input')">
              <el-icon><Edit /></el-icon>
              <span>输入框</span>
            </div>
            <div class="component-item" @click="addSearchField('select')">
              <el-icon><List /></el-icon>
              <span>下拉框</span>
            </div>
            <div class="component-item" @click="addSearchField('date')">
              <el-icon><Calendar /></el-icon>
              <span>日期</span>
            </div>
            <div class="component-item" @click="addSearchField('dateRange')">
              <el-icon><Timer /></el-icon>
              <span>日期范围</span>
            </div>
            <div class="component-item" @click="addSearchField('number')">
              <el-icon><Histogram /></el-icon>
              <span>数字</span>
            </div>
          </div>
        </div>

        <!-- 内容区组件 -->
        <div class="component-section">
          <div class="section-title">内容区</div>
          <div class="component-list">
            <div class="component-item" @click="openComponentSelector('table')">
              <el-icon><Grid /></el-icon>
              <span>表格</span>
            </div>
            <div class="component-item" @click="openComponentSelector('form')">
              <el-icon><Document /></el-icon>
              <span>表单</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间设计画板区域 -->
      <div class="center-panel">
        <div class="design-canvas">
          <!-- 查询条件区域 -->
          <div class="search-area-preview">
            <div class="area-header">
              <span>查询条件</span>
              <el-switch v-model="pageConfig.configJsonObject!.searchArea.enabled" size="small" />
            </div>
            <div class="area-content" v-show="pageConfig.configJsonObject!.searchArea.enabled">
              <div v-if="pageConfig.configJsonObject!.searchArea.fields.length === 0" class="empty-hint">
                从左侧拖入查询条件组件
              </div>
              <div v-else class="field-list">
                <div
                  v-for="(field, index) in pageConfig.configJsonObject!.searchArea.fields"
                  :key="field.id"
                  class="field-item"
                  :class="{ active: selectedFieldId === field.id }"
                  @click="selectField(field.id, 'search')"
                >
                  <span class="field-label">{{ field.label }}</span>
                  <el-icon class="delete-icon" @click.stop="removeSearchField(index)">
                    <CircleClose />
                  </el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 内容区域 -->
          <div class="content-area-preview">
            <div class="area-header">
              <span>内容区域</span>
            </div>
            <div class="area-content">
              <div
                v-if="!pageConfig.configJsonObject!.contentArea.configId"
                class="empty-hint"
                @click="openComponentSelector()"
              >
                从左侧选择表格或表单组件
              </div>
              <div v-else class="content-component" :class="{ active: selectedArea === 'content' }" @click="selectArea('content')">
                <el-icon>
                  <Grid v-if="pageConfig.configJsonObject!.contentArea.type === 'table'" />
                  <Document v-else />
                </el-icon>
                <div class="component-info">
                  <div class="component-title">{{ pageConfig.configJsonObject!.contentArea.title }}</div>
                  <div class="component-type">
                    {{ pageConfig.configJsonObject!.contentArea.type === 'table' ? '表格' : '表单' }}
                    配置ID: {{ pageConfig.configJsonObject!.contentArea.configId }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="right-panel">
        <div class="panel-title">属性配置</div>

        <!-- 页面属性 -->
        <el-form v-if="selectedArea === 'page'" label-width="80px" size="small">
          <div class="form-section">页面属性</div>
          <el-form-item label="页面名称">
            <el-input v-model="pageConfig.pageName" placeholder="请输入页面名称" />
          </el-form-item>
          <el-form-item label="页面编码">
            <el-input v-model="pageConfig.pageCode" placeholder="请输入页面编码" />
          </el-form-item>
          <el-form-item label="页面类型">
            <el-select v-model="pageConfig.pageType" placeholder="请选择">
              <el-option label="列表页" value="list" />
              <el-option label="表单页" value="form" />
              <el-option label="自定义" value="custom" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="pageConfig.status" />
          </el-form-item>
        </el-form>

        <!-- 查询字段属性 -->
        <el-form v-else-if="selectedArea === 'search' && selectedField" label-width="80px" size="small">
          <div class="form-section">查询字段属性</div>
          <el-form-item label="字段标签">
            <el-input v-model="selectedField.label" />
          </el-form-item>
          <el-form-item label="字段编码">
            <el-input v-model="selectedField.fieldCode" />
          </el-form-item>
          <el-form-item label="字段类型">
            <el-select v-model="selectedField.type">
              <el-option label="输入框" value="input" />
              <el-option label="下拉框" value="select" />
              <el-option label="日期" value="date" />
              <el-option label="日期范围" value="dateRange" />
              <el-option label="数字" value="number" />
            </el-select>
          </el-form-item>
          <el-form-item label="占位符">
            <el-input v-model="selectedField.placeholder" />
          </el-form-item>
          <el-form-item label="必填">
            <el-switch v-model="selectedField.required" />
          </el-form-item>
        </el-form>

        <!-- 内容区属性 -->
        <el-form v-else-if="selectedArea === 'content'" label-width="80px" size="small">
          <div class="form-section">内容区域属性</div>
          <el-form-item label="标题">
            <el-input v-model="pageConfig.configJsonObject!.contentArea.title" />
          </el-form-item>
          <el-form-item label="显示工具栏">
            <el-switch v-model="pageConfig.configJsonObject!.contentArea.showToolbar" />
          </el-form-item>
        </el-form>

        <div v-else class="empty-hint">请在中间区域选择要配置的组件</div>
      </div>
    </div>

    <!-- 组件选择弹窗 -->
    <ComponentSelector
      v-model="selectorVisible"
      :type="selectorType"
      @select="handleComponentSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { PageConfig, SearchField } from '@/api/page'
import { getPage, createPage, updatePage } from '@/api/page'
import ComponentSelector from '@/components/designer/ComponentSelector.vue'

const router = useRouter()
const route = useRoute()

// 当前编辑的页面ID
const pageId = ref<number | null>(null)
const isEdit = computed(() => !!pageId.value)

// 选中的区域：page, search, content
const selectedArea = ref<'page' | 'search' | 'content'>('page')
const selectedFieldId = ref<string>('')

// 组件选择弹窗
const selectorVisible = ref(false)
const selectorType = ref<'table' | 'form'>('table')

// 默认配置对象
const defaultConfigJson = {
  searchArea: {
    enabled: true,
    title: '查询条件',
    fields: [] as SearchField[]
  },
  contentArea: {
    type: 'table' as 'table' | 'form',
    configId: 0,
    title: '',
    showToolbar: true
  }
}

const defaultLayoutConfig = {
  searchAreaHeight: '80px',
  contentAreaFlex: 1
}

// 页面配置
const pageConfig = reactive<PageConfig>({
  pageName: '',
  pageCode: '',
  pageType: 'list',
  status: true,
  configJsonObject: { ...defaultConfigJson },
  layoutConfigObject: { ...defaultLayoutConfig }
})

// 选中的查询字段
const selectedField = computed<SearchField | null>(() => {
  return pageConfig.configJsonObject!.searchArea.fields.find(f => f.id === selectedFieldId.value) || null
})

// 生成唯一ID
const generateId = () => `field_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

// 添加查询字段
const addSearchField = (type: 'input' | 'select' | 'date' | 'dateRange' | 'number') => {
  const newField: SearchField = {
    id: generateId(),
    label: type === 'input' ? '输入框' : type === 'select' ? '下拉框' : type === 'date' ? '日期' : type === 'dateRange' ? '日期范围' : '数字',
    fieldCode: `field_${type}`,
    type,
    placeholder: `请${type === 'select' ? '选择' : '输入'}`,
    required: false
  }
  pageConfig.configJsonObject!.searchArea.fields.push(newField)
  selectedFieldId.value = newField.id
  selectedArea.value = 'search'
}

// 删除查询字段
const removeSearchField = (index: number) => {
  const fieldId = pageConfig.configJsonObject!.searchArea.fields[index].id
  pageConfig.configJsonObject!.searchArea.fields.splice(index, 1)
  if (selectedFieldId.value === fieldId) {
    selectedFieldId.value = ''
    selectedArea.value = 'page'
  }
}

// 选择字段
const selectField = (fieldId: string, area: 'search' | 'content') => {
  selectedFieldId.value = fieldId
  selectedArea.value = area
}

// 选择区域
const selectArea = (area: 'page' | 'search' | 'content') => {
  selectedArea.value = area
  if (area === 'search' && pageConfig.configJsonObject!.searchArea.fields.length > 0) {
    selectedFieldId.value = pageConfig.configJsonObject!.searchArea.fields[0].id
  } else if (area === 'content') {
    selectedFieldId.value = ''
  } else {
    selectedFieldId.value = ''
  }
}

// 打开组件选择弹窗
const openComponentSelector = (type?: 'table' | 'form') => {
  if (type) {
    selectorType.value = type
  }
  selectorVisible.value = true
}

// 处理组件选择
const handleComponentSelect = (config: { id: number; name: string; type: 'table' | 'form' }) => {
  pageConfig.configJsonObject!.contentArea = {
    type: config.type,
    configId: config.id,
    title: config.name,
    showToolbar: true
  }
  selectedArea.value = 'content'
  ElMessage.success(`已选择${config.type === 'table' ? '表格' : '表单'}: ${config.name}`)
}

// 返回
const handleBack = () => {
  router.back()
}

// 预览
const handlePreview = () => {
  ElMessage.info('预览功能开发中...')
}

// 保存
const handleSave = async () => {
  // 验证
  if (!pageConfig.pageName) {
    ElMessage.error('请输入页面名称')
    return
  }
  if (!pageConfig.pageCode) {
    ElMessage.error('请输入页面编码')
    return
  }

  try {
    if (isEdit.value && pageId.value) {
      await updatePage(pageId.value, pageConfig)
      ElMessage.success('更新成功')
    } else {
      const id = await createPage(pageConfig)
      pageId.value = id
      ElMessage.success('创建成功')
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 加载页面配置
const loadPageConfig = async (id: number) => {
  try {
    const data = await getPage(id)

    // 复制基本属性
    pageConfig.id = data.id
    pageConfig.pageName = data.pageName || ''
    pageConfig.pageCode = data.pageCode || ''
    pageConfig.pageType = data.pageType || 'list'
    pageConfig.status = data.status ?? true

    // 解析 configJson
    if (data.configJsonObject) {
      pageConfig.configJsonObject = data.configJsonObject
    } else if (data.configJson) {
      try {
        pageConfig.configJsonObject = JSON.parse(data.configJson)
      } catch (e) {
        console.error('解析 configJson 失败:', e)
        pageConfig.configJsonObject = { ...defaultConfigJson }
      }
    } else {
      pageConfig.configJsonObject = { ...defaultConfigJson }
    }

    // 解析 layoutConfig
    if (data.layoutConfigObject) {
      pageConfig.layoutConfigObject = data.layoutConfigObject
    } else if (data.layoutConfig) {
      try {
        pageConfig.layoutConfigObject = JSON.parse(data.layoutConfig)
      } catch (e) {
        console.error('解析 layoutConfig 失败:', e)
        pageConfig.layoutConfigObject = { ...defaultLayoutConfig }
      }
    } else {
      pageConfig.layoutConfigObject = { ...defaultLayoutConfig }
    }
  } catch (error) {
    console.error('加载页面配置失败:', error)
    ElMessage.error('加载页面配置失败')
  }
}

onMounted(() => {
  const id = route.query.id as string
  if (id) {
    pageId.value = parseInt(id)
    loadPageConfig(parseInt(id))
  }
})
</script>

<style lang="scss" scoped>
.page-designer {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.designer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .page-title {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .header-right {
    display: flex;
    gap: 12px;
  }
}

.designer-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.left-panel,
.right-panel {
  width: 280px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.right-panel {
  border-right: none;
  border-left: 1px solid #e4e7ed;
}

.panel-title {
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fafafa;
}

.component-section {
  padding: 12px 16px;

  .section-title {
    font-size: 12px;
    color: #909399;
    margin-bottom: 8px;
  }
}

.component-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.component-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background-color: #409eff;
    border-color: #409eff;
    color: #fff;
  }

  .el-icon {
    font-size: 20px;
    margin-bottom: 4px;
  }

  span {
    font-size: 12px;
  }
}

.center-panel {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background-color: #e8ecf1;
}

.design-canvas {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  min-height: 500px;
}

.search-area-preview,
.content-area-preview {
  margin-bottom: 20px;

  .area-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    background-color: #f5f7fa;
    border-radius: 4px;
    margin-bottom: 8px;
    font-size: 13px;
    color: #606266;
  }

  .area-content {
    padding: 16px;
    background-color: #fafafa;
    border: 2px dashed #dcdfe6;
    border-radius: 4px;
    min-height: 60px;
  }
}

.field-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.field-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
  }

  &.active {
    border-color: #409eff;
    background-color: #ecf5ff;
  }

  .field-label {
    font-size: 12px;
  }

  .delete-icon {
    color: #f56c6c;
    cursor: pointer;
    &:hover {
      color: #ff0000;
    }
  }
}

.content-component {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #fff;
  border: 2px dashed #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
  }

  &.active {
    border-color: #409eff;
    background-color: #ecf5ff;
  }

  .el-icon {
    font-size: 32px;
    color: #409eff;
  }

  .component-info {
    .component-title {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .component-type {
      font-size: 12px;
      color: #909399;
    }
  }
}

.empty-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  color: #909399;
  font-size: 12px;
  cursor: pointer;

  &:hover {
    color: #409eff;
  }
}

.form-section {
  padding: 8px 12px;
  margin-bottom: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #606266;
}
</style>
