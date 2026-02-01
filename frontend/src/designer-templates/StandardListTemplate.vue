<template>
  <div class="standard-list-template">
    <!-- 查询区 -->
    <DesignArea
      id="search"
      title="查询区"
      :required="false"
      :accept-types="['form']"
      :max-components="1"
      v-model:components="pageConfig.value.components.search"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <FormRenderDesign
          :form-config="{ id: component.refId, formName: component.name, formCode: '' }"
          :design-mode="true"
        />
      </template>
    </DesignArea>

    <!-- 工具栏 -->
    <DesignArea
      id="toolbar"
      title="工具栏"
      :required="false"
      :accept-types="['button']"
      v-model:components="pageConfig.value.components.toolbar"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <el-button
          :type="component.props?.type || 'default'"
          :icon="component.props?.icon"
        >
          {{ component.name }}
        </el-button>
      </template>
    </DesignArea>

    <!-- 内容区（表格） -->
    <DesignArea
      id="content"
      title="数据列表"
      :required="true"
      :accept-types="['table']"
      :max-components="1"
      v-model:components="pageConfig.value.components.content"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <TableRenderDesign
          :table-config="{ id: component.refId, tableName: component.name, tableCode: '' }"
          :design-mode="true"
        />
      </template>
    </DesignArea>
  </div>
</template>

<script setup lang="ts">
import { inject, ref } from 'vue'
import DesignArea from '@/components/designer/DesignArea.vue'
import FormRenderDesign from '@/components/designer/FormRenderDesign.vue'
import TableRenderDesign from '@/components/designer/TableRenderDesign.vue'
import type { ComponentRef, PageConfigData } from '@/types/template'

// 定义默认配置
const defaultPageConfig: PageConfigData = {
  version: 'v5',
  templateId: 'standard-list',
  components: {
    search: [],
    toolbar: [],
    content: []
  }
}

// 从父组件获取配置，如果未获取到则创建默认配置
const injectedPageConfig = inject<PageConfigData | null>('pageConfig', null)
const pageConfig = ref(injectedPageConfig || defaultPageConfig)

const emit = defineEmits<{
  'component-select': [component: ComponentRef]
}>()

const handleComponentSelect = (component: ComponentRef) => {
  emit('component-select', component)
}

// 设置模板ID
if (injectedPageConfig && injectedPageConfig.templateId !== 'standard-list') {
  injectedPageConfig.templateId = 'standard-list'
}
</script>

<style scoped>
.standard-list-template {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  min-height: 600px;
}
</style>
