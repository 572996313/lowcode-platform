<template>
  <div class="card-form-template">
    <el-card
      v-for="slotMeta in fieldSlots"
      :key="slotMeta.id"
      class="field-card"
      shadow="hover"
    >
      <template #header>
        <div class="card-header">
          <span>{{ slotMeta.label }}</span>
        </div>
      </template>
      <div class="field-slot" :data-slot-id="slotMeta.id">
        <slot :name="slotMeta.id">
          <div class="slot-placeholder">拖拽业务字段到这里</div>
        </slot>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const fieldSlots = [
  { id: 'card-basic', label: '基础信息', minFields: 0, maxFields: 10 },
  { id: 'card-detail', label: '详细信息', minFields: 0, maxFields: 10 }
]

// 模板元数据（用于设计器识别）
defineExpose({
  templateCode: 'card',
  templateName: '卡片表单模板',
  fieldSlots
})
</script>

<style scoped lang="scss">
.card-form-template {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;

  .field-card {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    .card-header {
      font-weight: 500;
    }
  }

  .field-slot {
    min-height: 80px;
    border: 1px dashed var(--el-border-color);
    border-radius: 4px;
    padding: 16px;

    &.drag-over {
      border-color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
    }
  }

  .slot-placeholder {
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-placeholder);
    font-size: 14px;
  }
}
</style>