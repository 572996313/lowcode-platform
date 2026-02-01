<template>
  <div class="simple-form-template">
    <!-- 固定元素：标题 -->
    <div class="template-header">
      <h3>{{ title || '基本信息' }}</h3>
    </div>

    <!-- 固定元素：分割线 -->
    <el-divider />

    <!-- 字段槽位：基础字段区域 -->
    <div class="field-slot" data-slot-id="basic">
      <slot name="basic">
        <div class="slot-placeholder">拖拽业务字段到这里</div>
      </slot>
    </div>

    <!-- 固定元素：分割线 -->
    <el-divider />

    <!-- 字段槽位：扩展字段区域 -->
    <div class="field-slot" data-slot-id="extend">
      <slot name="extend">
        <div class="slot-placeholder">拖拽自定义控件到这里</div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
}>()

// 模板元数据（用于设计器识别）
defineExpose({
  templateCode: 'simple',
  templateName: '简单表单模板',
  fieldSlots: [
    { id: 'basic', label: '基础信息区域', minFields: 0, maxFields: 10 },
    { id: 'extend', label: '扩展信息区域', minFields: 0, maxFields: 20 }
  ]
})
</script>

<style scoped lang="scss">
.simple-form-template {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;

  .template-header {
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }
  }

  .field-slot {
    min-height: 80px;
    border: 1px dashed var(--el-border-color);
    border-radius: 4px;
    padding: 16px;
    margin: 16px 0;
    transition: all 0.3s;

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