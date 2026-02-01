<template>
  <div class="form-page-template">
    <!-- 表单内容区 -->
    <DesignArea
      id="content"
      title="表单内容"
      :required="true"
      :accept-types="['form']"
      :max-components="1"
      v-model:components="pageConfig.value.components.content"
      @select="handleComponentSelect"
    >
      <template #default="{ component }">
        <FormRenderDesign
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
import FormRenderDesign from '@/components/designer/FormRenderDesign.vue'
import type { ComponentRef, PageConfigData } from '@/types/template'

// 定义默认配置
const defaultPageConfig: PageConfigData = {
  version: 'v5',
  templateId: 'form-page',
  components: {
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
if (injectedPageConfig && injectedPageConfig.templateId !== 'form-page') {
  injectedPageConfig.templateId = 'form-page'
}
</script>

<style scoped>
.form-page-template {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  min-height: 400px;
}
</style>
