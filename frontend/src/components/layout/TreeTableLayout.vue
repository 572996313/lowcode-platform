<template>
  <div class="layout-tree-table">
    <div class="tree-section">
      <slot name="tree" />
    </div>
    <div class="table-section">
      <div v-if="$slots.search" class="search-area">
        <slot name="search" />
      </div>
      <div v-if="$slots.toolbar" class="toolbar-area">
        <slot name="toolbar" />
      </div>
      <div class="content-area">
        <slot name="content" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.layout-tree-table {
  display: flex;
  gap: 16px;
  height: 100%;
}

.tree-section {
  width: 280px;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}

.table-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}
</style>
