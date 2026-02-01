<template>
  <div class="base-template">
    <!-- 子组件实现画布 -->
  </div>
</template>

<script setup lang="ts">
import { ref, provide } from 'vue'
import type { PageConfigData, ComponentLibrary } from '@/types/template'
import type { DragState } from '@/types/component'

// 模板 Props
interface TemplateProps {
  pageId?: number              // 页面ID（编辑时传入）
  readOnly?: boolean           // 只读模式
}

const props = withDefaults(defineProps<TemplateProps>(), {
  readOnly: false
})

// 页面配置数据
const pageConfig = ref<PageConfigData>({
  version: 'v5',
  templateId: '',
  components: {}
})

// 组件库数据
const componentLibrary = ref<ComponentLibrary>({
  forms: [],
  tables: []
})

// 拖拽状态
const dragState = ref<DragState>({
  isDragging: false,
  draggedComponent: null,
  targetArea: null
})

// 提供给子组件使用
provide('pageConfig', pageConfig)
provide('componentLibrary', componentLibrary)
provide('dragState', dragState)
provide('updateConfig', updateConfig)

// 更新配置
const updateConfig = (areaId: string, components: any[]) => {
  pageConfig.value.components[areaId] = components
}

// 加载页面配置
const loadPageConfig = async (id: number) => {
  // 在实际实现中，这里会调用API获取页面配置
  // const page = await getPage(id)
  // const config = JSON.parse(page.configTemplate || '{}')
  // pageConfig.value = config
  return pageConfig.value
}

// 保存配置
const saveConfig = async () => {
  // 在实际实现中，这里会调用API保存页面配置
  // await updatePage(props.pageId!, {
  //   configTemplate: JSON.stringify(pageConfig.value)
  // })
}

// 设置模板ID
const setTemplateId = (id: string) => {
  pageConfig.value.templateId = id
}

// 暴露给父组件
defineExpose({
  loadPageConfig,
  saveConfig,
  getConfig: () => pageConfig.value,
  setTemplateId,
  pageConfig
})
</script>

<style scoped>
.base-template {
  min-height: 100%;
  background-color: #fff;
  border-radius: 8px;
}
</style>
