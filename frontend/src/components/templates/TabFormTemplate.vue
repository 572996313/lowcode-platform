<template>
  <div class="tab-form-template">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane
        v-for="slotMeta in fieldSlots"
        :key="slotMeta.id"
        :label="slotMeta.label"
        :name="slotMeta.id"
      >
        <div class="field-slot" :data-slot-id="slotMeta.id">
          <slot :name="slotMeta.id">
            <div class="slot-placeholder">拖拽业务字段到这里</div>
          </slot>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('tab-basic')

const fieldSlots = [
  { id: 'tab-basic', label: '基础信息', minFields: 0, maxFields: 10 },
  { id: 'tab-detail', label: '详细信息', minFields: 0, maxFields: 10 },
  { id: 'tab-other', label: '其他信息', minFields: 0, maxFields: 10 }
]

// 模板元数据（用于设计器识别）
defineExpose({
  templateCode: 'tab',
  templateName: '页签表单模板',
  fieldSlots
})
</script>

<style scoped lang="scss">
.tab-form-template {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;

  .field-slot {
    min-height: 120px;
    border: 1px dashed var(--el-border-color);
    border-radius: 4px;
    padding: 16px;

    &.drag-over {
      border-color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
    }
  }

  .slot-placeholder {
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-placeholder);
    font-size: 14px;
  }
}
</style>