<template>
  <div class="tree-table-template" style="display: flex; gap: 16px; height: 100%; padding: 16px;">
    <!-- 左侧树形区 -->
    <DesignArea
      id="tree"
      title="树形导航"
      :required="true"
      :accept-types="['tree']"
      :max-components="1"
      v-model:components="pageConfig.value.components.tree"
      :area-style="{ flex: '0 0 250px', minWidth: '200px' }"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <div class="tree-preview">
          <el-tree :data="mockTreeData" />
        </div>
      </template>
    </DesignArea>

    <!-- 右侧内容区 -->
    <DesignArea
      id="content"
      title="数据列表"
      :required="true"
      :accept-types="['table', 'form']"
      :max-components="1"
      v-model:components="pageConfig.value.components.content"
      :area-style="{ flex: '1' }"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <TableRenderDesign
          v-if="component.type === 'table'"
          :table-config="{ id: component.refId, tableName: component.name, tableCode: '' }"
          :design-mode="true"
        />
        <FormRenderDesign
          v-else-if="component.type === 'form'"
          :form-config="{ id: component.refId, formName: component.name, formCode: '' }"
          :design-mode="true"
        />
      </template>
    </DesignArea>
  </div>
</template>

<script setup lang="ts">
import { inject, ref } from 'vue'
import DesignArea from '@/components/designer/DesignArea.vue'
import TableRenderDesign from '@/components/designer/TableRenderDesign.vue'
import FormRenderDesign from '@/components/designer/FormRenderDesign.vue'
import type { ComponentRef, PageConfigData } from '@/types/template'

// 定义默认配置
const defaultPageConfig: PageConfigData = {
  version: 'v5',
  templateId: 'tree-table',
  components: {
    tree: [],
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
if (injectedPageConfig && injectedPageConfig.templateId !== 'tree-table') {
  injectedPageConfig.templateId = 'tree-table'
}

// 模拟树形数据
const mockTreeData = ref([
  {
    label: '一级 1',
    children: [
      {
        label: '二级 1-1',
        children: [
          { label: '三级 1-1-1' }
        ]
      }
    ]
  },
  {
    label: '一级 2',
    children: [
      { label: '二级 2-1' }
    ]
  }
])
</script>

<style scoped>
.tree-table-template {
  background-color: #fff;
  border-radius: 8px;
}

.tree-preview {
  padding: 12px;
}
</style>
