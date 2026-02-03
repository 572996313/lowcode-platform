/**
 * 自由画布属性面板
 * PropertyPanel for Free Canvas Designer
 */
<template>
  <div class="property-panel-free-canvas">
    <div class="panel-header">
      <h3>属性配置</h3>
    </div>

    <div class="panel-content">
      <el-empty v-if="!selectedComponent" description="请选择组件进行配置" />

      <!-- 组件配置 -->
      <div v-else class="config-form">
        <!-- 组件基本信息 -->
        <section class="config-section">
          <div class="section-title">基本信息</div>
          <el-form label-position="top" size="small">
            <el-form-item label="组件名称">
              <el-input v-model="componentName" @change="handleNameChange" />
            </el-form-item>
            <el-form-item label="组件类型">
              <el-tag>{{ componentTypeLabel }}</el-tag>
            </el-form-item>
            <el-form-item label="是否启用">
              <el-switch v-model="componentEnabled" @change="handleEnabledChange" />
            </el-form-item>
          </el-form>
        </section>

        <!-- 位置配置 -->
        <section class="config-section">
          <div class="section-title">位置</div>
          <el-form label-position="top" size="small">
            <div class="position-inputs">
              <el-form-item label="X">
                <el-input-number
                  v-model="position.x"
                  :min="0"
                  :step="canvasConfig.gridSize || 1"
                  @change="handlePositionChange"
                />
              </el-form-item>
              <el-form-item label="Y">
                <el-input-number
                  v-model="position.y"
                  :min="0"
                  :step="canvasConfig.gridSize || 1"
                  @change="handlePositionChange"
                />
              </el-form-item>
              <el-form-item label="宽度">
                <el-input-number
                  v-model="position.width"
                  :min="50"
                  :step="10"
                  @change="handlePositionChange"
                />
              </el-form-item>
              <el-form-item label="高度">
                <el-input-number
                  v-model="position.height"
                  :min="30"
                  :step="10"
                  @change="handlePositionChange"
                />
              </el-form-item>
            </div>
            <el-form-item label="层级">
              <el-input-number
                v-model="position.zIndex"
                :min="1"
                :max="999"
                @change="handlePositionChange"
              />
            </el-form-item>
          </el-form>
        </section>

        <!-- 数据源配置 -->
        <section v-if="hasDataSource" class="config-section">
          <div class="section-title">数据源</div>
          <el-form label-position="top" size="small">
            <el-form-item label="数据源类型">
              <el-select v-model="dataSourceType" @change="handleDataSourceTypeChange">
                <el-option label="静态数据" value="static" />
                <el-option label="API接口" value="api" />
                <el-option label="SQL查询" value="sql" />
              </el-select>
            </el-form-item>

            <!-- 静态数据 -->
            <template v-if="dataSourceType === 'static'">
              <el-form-item label="数据（JSON）">
                <el-input
                  v-model="staticDataJson"
                  type="textarea"
                  :rows="4"
                  placeholder='[{"id": 1, "name": "示例"}]'
                  @blur="handleStaticDataChange"
                />
              </el-form-item>
            </template>

            <!-- API配置 -->
            <template v-if="dataSourceType === 'api'">
              <el-form-item label="接口地址">
                <el-input v-model="apiUrl" placeholder="/api/xxx" @change="handleApiConfigChange" />
              </el-form-item>
              <el-form-item label="请求方法">
                <el-select v-model="apiMethod" @change="handleApiConfigChange">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                  <el-option label="PUT" value="PUT" />
                  <el-option label="DELETE" value="DELETE" />
                </el-select>
              </el-form-item>
            </template>

            <!-- SQL配置 -->
            <template v-if="dataSourceType === 'sql'">
              <el-form-item label="SQL语句">
                <el-input
                  v-model="sqlContent"
                  type="textarea"
                  :rows="4"
                  placeholder="SELECT * FROM table"
                  @change="handleSqlConfigChange"
                />
              </el-form-item>
            </template>
          </el-form>
        </section>

        <!-- 树组件配置 -->
        <section v-if="selectedComponent.type === 'tree'" class="config-section">
          <div class="section-title">树配置</div>
          <el-form label-position="top" size="small">
            <el-form-item label="显示字段">
              <el-input v-model="treeConfig.displayField" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="子节点字段">
              <el-input v-model="treeConfig.childrenField" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="ID字段">
              <el-input v-model="treeConfig.idField" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="默认展开">
              <el-switch v-model="treeConfig.defaultExpandAll" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="显示图标">
              <el-switch v-model="treeConfig.showIcon" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="显示复选框">
              <el-switch v-model="treeConfig.showCheckbox" @change="handleConfigChange" />
            </el-form-item>
          </el-form>
        </section>

        <!-- 表格组件配置 -->
        <section v-if="selectedComponent.type === 'table'" class="config-section">
          <div class="section-title">表格配置</div>
          <el-form label-position="top" size="small">
            <el-form-item label="显示序号">
              <el-switch v-model="tableConfig.showIndex" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="斑马纹">
              <el-switch v-model="tableConfig.stripe" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="边框">
              <el-switch v-model="tableConfig.border" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="分页">
              <el-switch v-model="tableConfig.pagination" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item v-if="tableConfig.pagination" label="每页条数">
              <el-input-number v-model="tableConfig.pageSize" :min="10" :max="100" @change="handleConfigChange" />
            </el-form-item>
            <el-form-item label="选择模式">
              <el-select v-model="tableConfig.selectionMode" @change="handleConfigChange">
                <el-option label="无" value="none" />
                <el-option label="单选" value="single" />
                <el-option label="多选" value="multiple" />
              </el-select>
            </el-form-item>
          </el-form>
        </section>

        <!-- 查询表单配置 -->
        <section v-if="selectedComponent.type === 'search-form'" class="config-section">
          <div class="section-title">查询表单配置</div>
          <el-form label-position="top" size="small">
            <el-form-item label="列数">
              <el-radio-group v-model="searchFormConfig.layoutCols" @change="handleConfigChange">
                <el-radio-button :value="1">1列</el-radio-button>
                <el-radio-button :value="2">2列</el-radio-button>
                <el-radio-button :value="3">3列</el-radio-button>
                <el-radio-button :value="4">4列</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="按钮对齐">
              <el-radio-group v-model="searchFormConfig.buttonAlign" @change="handleConfigChange">
                <el-radio-button label="left">左对齐</el-radio-button>
                <el-radio-button label="center">居中</el-radio-button>
                <el-radio-button label="right">右对齐</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </section>

        <!-- 样式配置 -->
        <section class="config-section">
          <div class="section-title">样式</div>
          <el-form label-position="top" size="small">
            <el-form-item label="背景颜色">
              <el-color-picker v-model="style.backgroundColor" @change="handleStyleChange" />
            </el-form-item>
            <el-form-item label="边框">
              <el-input v-model="style.border" placeholder="1px solid #ddd" @change="handleStyleChange" />
            </el-form-item>
            <el-form-item label="圆角">
              <el-input v-model="style.borderRadius" placeholder="4px" @change="handleStyleChange" />
            </el-form-item>
            <el-form-item label="内边距">
              <el-input v-model="style.padding" placeholder="16px" @change="handleStyleChange" />
            </el-form-item>
            <el-form-item label="阴影">
              <el-select v-model="style.boxShadow" @change="handleStyleChange">
                <el-option label="无" value="none" />
                <el-option label="小" value="0 2px 4px rgba(0,0,0,0.1)" />
                <el-option label="中" value="0 2px 8px rgba(0,0,0,0.15)" />
                <el-option label="大" value="0 4px 16px rgba(0,0,0,0.2)" />
              </el-select>
            </el-form-item>
          </el-form>
        </section>

        <!-- 操作按钮 -->
        <section class="config-section actions">
          <el-button type="danger" :icon="Delete" @click="handleDelete">
            删除组件
          </el-button>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import type { ComponentInstance, TreeComponentConfig, TableComponentConfig, SearchFormComponentConfig } from '@/types/page-free-canvas'

interface Props {
  component?: ComponentInstance | null
  canvasConfig?: {
    width: number
    height?: number | null
    gridSize?: number
    snapToGrid?: boolean
  }
}

interface Emits {
  (e: 'update', component: ComponentInstance): void
  (e: 'delete', id: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 组件类型标签
const componentTypeLabel = computed(() => {
  const labelMap: Record<string, string> = {
    'tree': '树组件',
    'search-form': '查询表单',
    'table': '表格',
    'button-group': '按钮组',
    'form': '表单',
    'chart': '图表',
    'tabs': '标签页',
    'card': '卡片',
    'divider': '分割线',
    'spacer': '占位符'
  }
  return props.component ? labelMap[props.component.type] || '未知' : ''
})

// 是否有数据源配置
const hasDataSource = computed(() => {
  return props.component && ['tree', 'table', 'chart', 'form'].includes(props.component.type)
})

// 组件名称
const componentName = computed({
  get: () => props.component?.name || '',
  set: (val) => {
    if (props.component) {
      emitUpdate({ ...props.component, name: val })
    }
  }
})

// 组件启用状态
const componentEnabled = computed({
  get: () => props.component?.enabled ?? true,
  set: (val) => {
    if (props.component) {
      emitUpdate({ ...props.component, enabled: val })
    }
  }
})

// 位置
const position = computed(() => props.component?.position || {
  x: 0, y: 0, width: 200, height: 100, zIndex: 1
})

// 样式
const style = ref({
  backgroundColor: '',
  border: '',
  borderRadius: '',
  padding: '',
  boxShadow: 'none'
})

// 监听组件变化，更新样式值
watch(() => props.component?.style, (newStyle) => {
  if (newStyle) {
    style.value = {
      backgroundColor: newStyle.backgroundColor || '',
      border: newStyle.border || '',
      borderRadius: newStyle.borderRadius || '',
      padding: newStyle.padding?.toString() || '',
      boxShadow: newStyle.boxShadow || 'none'
    }
  }
}, { immediate: true })

// 数据源类型
const dataSourceType = ref('static')

// 树配置
const treeConfig = computed({
  get: () => {
    if (!props.component || props.component.type !== 'tree') return {}
    return props.component.config as TreeComponentConfig
  },
  set: (val) => {
    if (props.component && props.component.type === 'tree') {
      emitUpdate({
        ...props.component,
        config: { ...props.component.config, ...val }
      })
    }
  }
})

// 表格配置
const tableConfig = computed({
  get: () => {
    if (!props.component || props.component.type !== 'table') return {}
    return props.component.config as TableComponentConfig
  },
  set: (val) => {
    if (props.component && props.component.type === 'table') {
      emitUpdate({
        ...props.component,
        config: { ...props.component.config, ...val }
      })
    }
  }
})

// 查询表单配置
const searchFormConfig = computed({
  get: () => {
    if (!props.component || props.component.type !== 'search-form') return {}
    return props.component.config as SearchFormComponentConfig
  },
  set: (val) => {
    if (props.component && props.component.type === 'search-form') {
      emitUpdate({
        ...props.component,
        config: { ...props.component.config, ...val }
      })
    }
  }
})

// 静态数据 JSON
const staticDataJson = ref('[]')
const apiUrl = ref('')
const apiMethod = ref('GET')
const sqlContent = ref('')

// 监听组件变化，更新数据源配置
watch(() => props.component, (comp) => {
  if (comp && hasDataSource.value) {
    const dataSource = (comp.config as any).dataSource
    if (dataSource) {
      dataSourceType.value = dataSource.type || 'static'
      if (dataSource.type === 'static') {
        staticDataJson.value = JSON.stringify(dataSource.static || [])
      } else if (dataSource.type === 'api') {
        apiUrl.value = dataSource.api?.url || ''
        apiMethod.value = dataSource.api?.method || 'GET'
      } else if (dataSource.type === 'sql') {
        sqlContent.value = dataSource.sql?.content || ''
      }
    }
  }
}, { immediate: true })

/**
 * 发送更新事件
 */
function emitUpdate(component: ComponentInstance) {
  emit('update', component)
}

/**
 * 处理名称变化
 */
function handleNameChange(value: string) {
  if (props.component) {
    emitUpdate({ ...props.component, name: value })
  }
}

/**
 * 处理启用状态变化
 */
function handleEnabledChange(value: boolean) {
  if (props.component) {
    emitUpdate({ ...props.component, enabled: value })
  }
}

/**
 * 处理位置变化
 */
function handlePositionChange() {
  if (props.component) {
    emitUpdate({
      ...props.component,
      position: { ...position.value }
    })
  }
}

/**
 * 处理数据源类型变化
 */
function handleDataSourceTypeChange(value: string) {
  if (!props.component || !hasDataSource.value) return

  const config = { ...props.component.config }
  ;(config as any).dataSource = {
    type: value
  }

  if (value === 'static') {
    ;(config as any).dataSource.static = []
  } else if (value === 'api') {
    ;(config as any).dataSource.api = { url: '', method: 'GET' }
  } else if (value === 'sql') {
    ;(config as any).dataSource.sql = { content: '' }
  }

  emitUpdate({ ...props.component, config })
}

/**
 * 处理静态数据变化
 */
function handleStaticDataChange() {
  if (!props.component || !hasDataSource.value) return

  try {
    const data = JSON.parse(staticDataJson.value || '[]')
    const config = { ...props.component.config }
    ;(config as any).dataSource = {
      ...(config as any).dataSource,
      static: data
    }
    emitUpdate({ ...props.component, config })
  } catch (e) {
    // ignore invalid JSON
  }
}

/**
 * 处理 API 配置变化
 */
function handleApiConfigChange() {
  if (!props.component || !hasDataSource.value) return

  const config = { ...props.component.config }
  ;(config as any).dataSource = {
    ...(config as any).dataSource,
    type: 'api',
    api: {
      url: apiUrl.value,
      method: apiMethod.value as any
    }
  }
  emitUpdate({ ...props.component, config })
}

/**
 * 处理 SQL 配置变化
 */
function handleSqlConfigChange() {
  if (!props.component || !hasDataSource.value) return

  const config = { ...props.component.config }
  ;(config as any).dataSource = {
    ...(config as any).dataSource,
    type: 'sql',
    sql: {
      content: sqlContent.value
    }
  }
  emitUpdate({ ...props.component, config })
}

/**
 * 处理配置变化
 */
function handleConfigChange() {
  // 由于使用了 computed 的 setter，这里不需要额外处理
}

/**
 * 处理样式变化
 */
function handleStyleChange() {
  if (!props.component) return

  emitUpdate({
    ...props.component,
    style: {
      ...props.component.style,
      backgroundColor: style.value.backgroundColor || undefined,
      border: style.value.border || undefined,
      borderRadius: style.value.borderRadius || undefined,
      padding: style.value.padding || undefined,
      boxShadow: style.value.boxShadow || undefined
    }
  })
}

/**
 * 处理删除
 */
function handleDelete() {
  if (props.component) {
    emit('delete', props.component.id)
  }
}
</script>

<style scoped lang="scss">
.property-panel-free-canvas {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-left: 1px solid #e4e7ed;

  .panel-header {
    padding: 12px 16px;
    border-bottom: 1px solid #e4e7ed;

    h3 {
      margin: 0;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
  }

  .panel-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
  }

  .config-form {
    .config-section {
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      &.actions {
        display: flex;
        justify-content: center;
      }
    }

    .section-title {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
      padding-left: 8px;
      border-left: 3px solid #409eff;
    }

    .position-inputs {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 8px;
    }

    :deep(.el-form-item) {
      margin-bottom: 12px;
    }

    :deep(.el-form-item__label) {
      font-size: 12px;
      color: #606266;
    }
  }
}
</style>
