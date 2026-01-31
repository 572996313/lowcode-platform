<template>
  <div class="component-library-panel">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 工具栏区域：显示按钮列表 -->
    <div v-else-if="areaType === 'toolbar'" class="component-section">
      <div class="section-header">
        <span class="section-title">按钮组件</span>
        <el-tag size="small" type="info">{{ buttons.length }} 个</el-tag>
      </div>
      <div class="component-list">
        <div
          v-for="btn in buttons"
          :key="btn.id"
          class="component-item button-item"
          draggable="true"
          @dragstart="handleDragStart($event, btn)"
          @click="handleComponentSelect('button', btn)"
        >
          <el-icon v-if="btn.icon">
            <component :is="btn.icon" />
          </el-icon>
          <span class="component-name">{{ btn.buttonName }}</span>
          <el-tag size="small" type="info">{{ btn.buttonCode }}</el-tag>
          <div class="component-tags">
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
        <el-empty v-if="buttons.length === 0" description="暂无按钮组件" :image-size="60" />
      </div>
    </div>

    <!-- 内容区域：显示表单和表格 -->
    <div v-else-if="areaType === 'content'" class="component-section">
      <!-- 表格组件 -->
      <div class="section-header">
        <span class="section-title">表格组件</span>
        <el-tag size="small" type="info">{{ tables.length }} 个</el-tag>
      </div>
      <div class="component-list">
        <div
          v-for="table in tables"
          :key="table.id"
          class="component-item table-item"
          draggable="true"
          @dragstart="handleDragStart($event, table, 'table')"
          @click="handleComponentSelect('table', table)"
        >
          <el-icon><Grid /></el-icon>
          <span class="component-name">{{ table.tableName }}</span>
          <div class="component-tags">
            <el-tag
              v-for="tag in parseTags(table.componentTags)"
              :key="tag"
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        <el-empty v-if="tables.length === 0" description="暂无表格组件" :image-size="60" />
      </div>

      <!-- 表单组件 -->
      <div class="section-header">
        <span class="section-title">表单组件</span>
        <el-tag size="small" type="info">{{ forms.length }} 个</el-tag>
      </div>
      <div class="component-list">
        <div
          v-for="form in forms"
          :key="form.id"
          class="component-item form-item"
          draggable="true"
          @dragstart="handleDragStart($event, form, 'form')"
          @click="handleComponentSelect('form', form)"
        >
          <el-icon><Document /></el-icon>
          <span class="component-name">{{ form.formName }}</span>
          <div class="component-tags">
            <el-tag
              v-for="tag in parseTags(form.componentTags)"
              :key="tag"
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        <el-empty v-if="forms.length === 0" description="暂无表单组件" :image-size="60" />
      </div>
    </div>

    <!-- 搜索区域：显示搜索表单 -->
    <div v-else-if="areaType === 'search'" class="component-section">
      <div class="section-header">
        <span class="section-title">搜索表单</span>
        <el-tag size="small" type="info">{{ searchForms.length }} 个</el-tag>
      </div>
      <div class="component-list">
        <div
          v-for="form in searchForms"
          :key="form.id"
          class="component-item form-item"
          @click="handleComponentSelect('form', form)"
        >
          <el-icon><Search /></el-icon>
          <span class="component-name">{{ form.formName }}</span>
        </div>
        <el-empty v-if="searchForms.length === 0" description="暂无搜索表单" :image-size="60" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { getLibraryComponents } from '@/api/library'
import { Grid, Document, Search, Loading } from '@element-plus/icons-vue'

interface Component {
  id: number
  componentTags?: string
  [key: string]: any
}

const props = defineProps<{
  libraryType: 'common' | 'business'
  areaType?: string
}>()

const emit = defineEmits<{
  (e: 'select', type: string, component: any): void
  (e: 'dragstart', event: DragEvent, component: any, type?: string): void
}>()

const loading = ref(true)
const buttons = ref<Component[]>([])
const forms = ref<Component[]>([])
const tables = ref<Component[]>([])

// 搜索表单（筛选 formType='search' 的表单）
const searchForms = computed(() => {
  return forms.value.filter((form) => form.formType === 'search')
})

// 将区域类型映射为组件类型
const mapAreaTypeToComponentType = (areaType: string | undefined): string | undefined => {
  const typeMap: Record<string, string> = {
    toolbar: 'button',    // 工具栏区域 -> 按钮组件
    // content 区域返回所有类型（form + table），不需要映射
    // search 区域使用表单组件，通过前端筛选 formType='search'
  }
  return typeMap[areaType || ''] || undefined
}

// 加载组件库数据
const loadComponents = async () => {
  loading.value = true
  try {
    // 将区域类型转换为组件类型
    const componentType = mapAreaTypeToComponentType(props.areaType)
    const data = await getLibraryComponents(props.libraryType, componentType)
    buttons.value = data.buttons || []
    forms.value = data.forms || []
    tables.value = data.tables || []
  } catch (error) {
    console.error('加载组件库失败:', error)
  } finally {
    loading.value = false
  }
}

// 解析标签JSON
const parseTags = (tagsStr: string | undefined) => {
  if (!tagsStr) return []
  try {
    return JSON.parse(tagsStr)
  } catch {
    return []
  }
}

// 处理组件选择
const handleComponentSelect = (type: string, component: any) => {
  emit('select', type, component)
}

// 处理拖拽开始
const handleDragStart = (event: DragEvent, component: any, type?: string) => {
  emit('dragstart', event, component, type)
}

// 监听 libraryType 变化，重新加载组件
watch(
  () => props.libraryType,
  () => {
    loadComponents()
  }
)

// 监听 areaType 变化，重新加载组件
watch(
  () => props.areaType,
  () => {
    loadComponents()
  }
)

onMounted(() => {
  loadComponents()
})
</script>

<style scoped lang="scss">
.component-library-panel {
  height: 100%;
  overflow-y: auto;
  padding: 12px;

  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: var(--el-text-color-secondary);
    gap: 8px;
  }

  .component-section {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid var(--el-border-color-light);
  }

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .component-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .component-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 12px;
    background-color: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background-color: var(--el-color-primary-light-9);
      border-color: var(--el-color-primary);
      transform: translateX(4px);
    }

    .el-icon {
      font-size: 18px;
      color: var(--el-color-primary);
    }
  }

  .component-name {
    flex: 1;
    font-size: 13px;
    color: var(--el-text-color-regular);
  }

  .component-tags {
    display: flex;
    gap: 4px;
    flex-wrap: wrap;

    .tag-item {
      font-size: 11px;
      height: 18px;
      line-height: 18px;
    }
  }

  .button-item {
    .el-tag {
      font-size: 11px;
      height: 18px;
      line-height: 18px;
    }
  }
}
</style>
