<template>
  <div class="layout-top-bottom">
    <!-- 查询区插槽 -->
    <div v-if="$slots.search" class="layout-search">
      <slot name="search" />
    </div>

    <!-- 工具栏插槽 -->
    <div v-if="$slots.toolbar" class="layout-toolbar">
      <slot name="toolbar" />
    </div>

    <!-- 内容区插槽 -->
    <div class="layout-content">
      <slot name="content" />
    </div>
  </div>
</template>

<style scoped>
.layout-top-bottom {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.layout-search {
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}

.layout-toolbar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 4px;
}

.layout-content {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}
</style>
