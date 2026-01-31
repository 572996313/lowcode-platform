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

        <!-- 组件库切换标签 -->
        <el-tabs v-model="activeLibrary" type="card" size="small" class="library-tabs">
          <el-tab-pane label="通用组件" name="common" />
          <el-tab-pane label="业务组件" name="business" />
        </el-tabs>

        <!-- 组件库内容 -->
        <div v-if="!selectedAreaId" class="empty-hint-panel">
          <el-empty description="请先选择一个区域进行配置" :image-size="80" />
        </div>

        <!-- 使用组件库面板组件 -->
        <component-library-panel
          v-else
          :library-type="activeLibrary as 'common' | 'business'"
          :area-type="selectedArea?.type"
          @select="handleComponentSelect"
          @dragstart="handleDragStart"
        />
      </div>

      <!-- 中间设计画板区域 -->
      <div class="center-panel">
        <div class="design-canvas">
          <!-- 动态渲染区域 -->
          <div
            v-for="area in templateAreas"
            :key="area.id"
            class="area-preview"
            :class="{
              'disabled': !pageAreas[area.id]?.enabled,
              'selected': selectedAreaId === area.id
            }"
            @click="selectArea(area.id)"
          >
            <div class="area-header">
              <el-icon><component :is="getAreaIcon(area.type)" /></el-icon>
              <span class="area-name">{{ area.name }}</span>
              <el-switch
                v-if="pageAreas[area.id]"
                v-model="pageAreas[area.id].enabled"
                :disabled="area.required"
                size="small"
                @click.stop
              />
              <el-tag v-if="area.required" type="danger" size="small">
                必需
              </el-tag>
            </div>
            <div class="area-content" v-show="pageAreas[area.id]?.enabled">
              <!-- 查询区预览 -->
              <div v-if="area.type === 'search'">
                <div v-if="!pageAreas[area.id]?.config?.fields?.length" class="empty-hint">
                  从左侧添加查询字段或点击区域配置
                </div>
                <div v-else class="field-list">
                  <div
                    v-for="(field, index) in pageAreas[area.id]?.config?.fields || []"
                    :key="field.id"
                    class="field-item"
                  >
                    <span class="field-label">{{ field.label }}</span>
                  </div>
                </div>
              </div>

              <!-- 内容区预览 -->
              <div v-else-if="area.type === 'content'">
                <div v-if="!pageAreas[area.id]?.config?.configId" class="empty-hint" @click="selectArea(area.id)">
                  从左侧选择表格或表单组件
                </div>
                <div v-else class="content-component">
                  <el-icon>
                    <Grid v-if="pageAreas[area.id]?.config?.componentType === 'table'" />
                    <Document v-else />
                  </el-icon>
                  <div class="component-info">
                    <div class="component-title">{{ pageAreas[area.id]?.config?.title }}</div>
                    <div class="component-type">
                      {{ pageAreas[area.id]?.config?.componentType === 'table' ? '表格' : '表单' }}
                      配置ID: {{ pageAreas[area.id]?.config?.configId }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- 其他区域预览 -->
              <div v-else class="generic-hint">
                <el-icon><component :is="getAreaIcon(area.type)" /></el-icon>
                <span>{{ area.name }}配置</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="right-panel">
        <div class="panel-title">属性配置</div>

        <!-- 页面属性 -->
        <el-form v-if="isPageSelected" label-width="80px" size="small">
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

        <!-- 区域配置 -->
        <div v-else-if="selectedArea" class="area-config-container">
          <div class="form-section">{{ selectedArea.name }}配置</div>

          <!-- 查询区配置 -->
          <SearchAreaConfig
            v-if="selectedArea.type === 'search'"
            :key="`search-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />

          <!-- 内容区配置 -->
          <ContentAreaConfig
            v-else-if="selectedArea.type === 'content'"
            :key="`content-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />

          <!-- 树形区配置 -->
          <TreeAreaConfig
            v-else-if="selectedArea.type === 'tree'"
            :key="`tree-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />

          <!-- 工具栏配置 -->
          <ToolbarAreaConfig
            v-else-if="selectedArea.type === 'toolbar'"
            :key="`toolbar-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />

          <!-- 标签页配置 -->
          <TabsAreaConfig
            v-else-if="selectedArea.type === 'tabs'"
            :key="`tabs-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />

          <!-- 自定义区配置 -->
          <CustomAreaConfig
            v-else
            :key="`custom-${selectedArea.id}`"
            :area="selectedArea"
            @update="updateArea"
          />
        </div>

        <div v-else class="empty-hint">请在中间区域选择要配置的组件</div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Edit,
  List,
  Calendar,
  Timer,
  Histogram,
  Grid,
  Document,
  ArrowLeft,
  View,
  Check,
  CircleClose,
  Rank,
  Files
} from '@element-plus/icons-vue'
import type { PageConfig, SearchField, NormalizedArea, AreaInfo } from '@/api/page'
import { getPage, createPage, updatePage, getPageTemplate, getPageTemplateAreas } from '@/api/page'
import {
  detectVersion,
  v1ToV2,
  v2ToV1,
  mergeTemplateAndPageAreas,
  validateConfig,
  type V2Config
} from '@/utils/configConverter'
import ComponentLibraryPanel from '@/components/designer/ComponentLibraryPanel.vue'
import {
  SearchAreaConfig,
  ContentAreaConfig,
  TreeAreaConfig,
  ToolbarAreaConfig,
  TabsAreaConfig,
  CustomAreaConfig
} from '@/components/designer/area-config'

const router = useRouter()
const route = useRoute()

// 当前编辑的页面ID
const pageId = ref<number | null>(null)
const isEdit = computed(() => !!pageId.value)

// 模板相关
const templateId = ref<number | null>(null)
const currentTemplate = ref<any>(null)
const templateAreas = ref<AreaInfo[]>([])
const pageAreas = ref<Record<string, NormalizedArea>>({})

// 选中的区域
const selectedAreaId = ref<string | null>(null)
const selectedAreaIdValue = computed(() => selectedAreaId.value)

// 组件选择弹窗

// 默认配置对象（v1格式，向后兼容）
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
  templateId: undefined,
  layoutType: undefined,
  configJsonObject: { ...defaultConfigJson },
  layoutConfigObject: { ...defaultLayoutConfig }
})

// 获取区域图标
const getAreaIcon = (areaType: string) => {
  const iconMap: Record<string, any> = {
    search: Edit,
    content: Document,
    tree: List,
    toolbar: Grid,
    tabs: Files,
    stats: Histogram,
    charts: Histogram,
    header: View,
    custom: Edit
  }
  return iconMap[areaType] || Grid
}

// 计算属性：当前选中的区域
const selectedArea = computed(() => {
  if (!selectedAreaIdValue.value) return null
  return pageAreas.value[selectedAreaIdValue.value] || null
})

// 计算属性：是否选中页面属性
const isPageSelected = computed(() => selectedAreaIdValue.value === null)

// 更新区域配置
const updateArea = (area: NormalizedArea) => {
  pageAreas.value[area.id] = area
}

// 打开查询字段编辑器
const openSearchFieldEditor = () => {
  if (selectedArea.value?.type === 'search') {
    // 添加一个新字段
    const newField = {
      id: generateId(),
      label: '新字段',
      fieldCode: '',
      type: 'input',
      placeholder: '',
      required: false
    }
    if (!selectedArea.value.config) {
      selectedArea.value.config = { title: '查询条件', collapsible: true, fields: [] }
    }
    if (!selectedArea.value.config.fields) {
      selectedArea.value.config.fields = []
    }
    selectedArea.value.config.fields.push(newField)
    updateArea(selectedArea.value)
  }
}

// 生成唯一ID
const generateId = () => `field_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

// 选择区域
const selectArea = (areaId: string | null) => {
  selectedAreaId.value = areaId
}

// 组件库类型切换
const activeLibrary = ref<'common' | 'business'>('common')

// 处理从组件库选择组件
const handleComponentSelect = (type: string, component: any) => {
  if (!selectedArea.value) {
    ElMessage.warning('请先选择一个区域')
    return
  }

  if (type === 'button') {
    // 添加按钮到当前区域 - 创建新的对象引用以确保响应式更新
    const currentButtons = selectedArea.value.config?.buttons || []
    const updatedArea = {
      ...selectedArea.value,
      config: {
        ...selectedArea.value.config,
        buttons: [
          ...currentButtons,
          {
            id: `btn_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
            buttonId: component.id,  // 引用按钮ID
            label: component.buttonName,
            buttonCode: component.buttonCode
          }
        ]
      }
    }
    updateArea(updatedArea)
    ElMessage.success(`已添加按钮：${component.buttonName}`)
  } else if (type === 'form') {
    const updatedArea = {
      ...selectedArea.value,
      config: {
        componentType: 'form',
        configId: component.id,
        title: component.formName,
        formCode: component.formCode
      }
    }
    updateArea(updatedArea)
    ElMessage.success(`已添加表单：${component.formName}`)
  } else if (type === 'table') {
    const updatedArea = {
      ...selectedArea.value,
      config: {
        componentType: 'table',
        configId: component.id,
        title: component.tableName,
        tableCode: component.tableCode
      }
    }
    updateArea(updatedArea)
    ElMessage.success(`已添加表格：${component.tableName}`)
  }
}

// 处理拖拽开始
const handleDragStart = (event: DragEvent, component: any, type?: string) => {
  if (!selectedArea.value) {
    ElMessage.warning('请先选择一个区域')
    event.preventDefault()
    return
  }

  // 设置拖拽数据
  const dragData = {
    componentType: type || (component.buttonCode ? 'button' : ''),
    componentId: component.id,
    componentName: component.buttonName || component.formName || component.tableName,
    data: component
  }
  event.dataTransfer?.setData('application/json', JSON.stringify(dragData))
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
    // 构建 v2 配置
    const v2Config: V2Config = {
      version: 2,
      templateCode: currentTemplate.value?.templateCode,
      layoutType: currentTemplate.value?.layoutType,
      areas: Object.values(pageAreas.value).filter(a => a.enabled !== false)
    }

    // 验证配置
    const validation = validateConfig(v2Config, templateAreas.value)
    if (!validation.valid) {
      validation.errors.forEach(err => ElMessage.error(err))
      return
    }

    // 生成 v1 配置（向后兼容）
    const v1Config = v2ToV1(v2Config)

    // 提交数据
    const submitData = {
      ...pageConfig,
      configTemplate: JSON.stringify(v2Config),
      configJsonObject: v1Config,
      configJson: JSON.stringify(v1Config)
    }

    if (isEdit.value && pageId.value) {
      await updatePage(pageId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      const id = await createPage(submitData)
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
    templateId.value = data.templateId || null

    // 如果有模板ID，加载模板区域定义
    if (templateId.value) {
      try {
        currentTemplate.value = await getPageTemplate(templateId.value)
        templateAreas.value = await getPageTemplateAreas(templateId.value)
      } catch (error) {
        console.error('加载模板区域失败:', error)
        // 使用默认区域
        templateAreas.value = [
          { id: 'search', type: 'search', name: '查询区', required: false },
          { id: 'content', type: 'content', name: '内容区', required: true }
        ]
      }
    } else {
      // 无模板时使用默认区域
      templateAreas.value = [
        { id: 'search', type: 'search', name: '查询区', required: false },
        { id: 'content', type: 'content', name: '内容区', required: true }
      ]
    }

    // 解析配置
    let config: any
    if (data.configTemplate) {
      try {
        config = JSON.parse(data.configTemplate)  // v2 格式
      } catch (e) {
        console.error('解析 configTemplate 失败:', e)
        config = null
      }
    }

    if (data.configJsonObject) {
      config = data.configJsonObject  // v1 格式（对象）
    } else if (data.configJson) {
      try {
        config = JSON.parse(data.configJson)  // v1 格式（字符串）
      } catch (e) {
        console.error('解析 configJson 失败:', e)
        config = null
      }
    }

    // 根据版本处理
    if (config) {
      const version = detectVersion(config)
      if (version === 2 && config.areas) {
        // v2 格式：直接使用 areas 数组
        const areasObj: Record<string, NormalizedArea> = {}
        for (const area of config.areas) {
          areasObj[area.id] = area
        }
        // 合并模板区域和页面配置
        pageAreas.value = mergeTemplateAndPageAreas(templateAreas.value, areasObj)
      } else {
        // v1 格式：转换为 v2
        const v2Config = v1ToV2(config, templateAreas.value)
        const areasObj: Record<string, NormalizedArea> = {}
        for (const area of v2Config.areas) {
          areasObj[area.id] = area
        }
        pageAreas.value = mergeTemplateAndPageAreas(templateAreas.value, areasObj)
      }
    } else {
      // 无配置时使用模板默认值
      const areasObj: Record<string, NormalizedArea> = {}
      for (const templateArea of templateAreas.value) {
        areasObj[templateArea.id] = {
          id: templateArea.id,
          type: templateArea.type,
          name: templateArea.name,
          enabled: templateArea.required ?? true,
          required: templateArea.required ?? false,
          config: {}
        }
      }
      pageAreas.value = areasObj
    }

    // 保存 v1 配置用于向后兼容显示
    if (data.configJsonObject) {
      pageConfig.configJsonObject = data.configJsonObject
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

onMounted(async () => {
  const id = route.query.id as string
  if (id) {
    pageId.value = parseInt(id)
    await loadPageConfig(parseInt(id))
  } else {
    // 新建页面：使用默认区域
    templateAreas.value = [
      { id: 'search', type: 'search', name: '查询区', required: false },
      { id: 'content', type: 'content', name: '内容区', required: true }
    ]
    pageAreas.value = {
      search: {
        id: 'search',
        type: 'search',
        name: '查询区',
        enabled: true,
        required: false,
        config: {
          title: '查询条件',
          collapsible: true,
          fields: []
        }
      },
      content: {
        id: 'content',
        type: 'content',
        name: '内容区',
        enabled: true,
        required: true,
        config: {
          title: '数据列表',
          componentType: 'table',
          configId: null,
          showToolbar: true
        }
      }
    }
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

.library-tabs {
  padding: 8px 12px 0;
  border-bottom: 1px solid #e4e7ed;

  :deep(.el-tabs__header) {
    margin: 0;
  }

  :deep(.el-tabs__nav) {
    border: none;
  }

  :deep(.el-tabs__item) {
    font-size: 12px;
    padding: 0 12px;
    height: 28px;
    line-height: 28px;
  }
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

.component-tip {
  margin-top: 12px;
  padding: 8px 12px;
  font-size: 12px;
  color: #909399;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.empty-hint-panel {
  padding: 30px 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
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

.area-preview {
  margin-bottom: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
  }

  &.selected {
    border-color: #409eff;
    background-color: #ecf5ff;
  }

  &.disabled {
    opacity: 0.6;
    border-color: #dcdfe6;
  }

  .area-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    background-color: #f5f7fa;
    border-radius: 4px 4px 0 0;
    border-bottom: 1px solid #e4e7ed;
    font-size: 13px;
    color: #606266;

    .area-name {
      flex: 1;
      font-weight: 500;
    }
  }

  .area-content {
    padding: 16px;
    background-color: #fafafa;
    min-height: 60px;
  }
}

.generic-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px;
  color: #909399;
  font-size: 13px;

  .el-icon {
    font-size: 24px;
  }
}

.field-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.area-config-container {
  padding: 0;
}

.field-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 12px;

  .field-label {
    font-weight: 500;
  }
}

.content-component {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 4px;

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
  min-height: 60px;
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
