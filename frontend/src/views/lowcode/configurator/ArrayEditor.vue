<template>
  <div class="array-editor">
    <div class="editor-header">
      <span class="editor-title">{{ title }}</span>
      <el-button type="primary" size="small" @click="handleAdd">+ 添加</el-button>
    </div>

    <div class="editor-list">
      <div v-for="(item, index) in modelValue" :key="index" class="editor-item">
        <div class="item-info">
          <span class="item-label">{{ getItemLabel(item) }}</span>
          <el-tag size="small" type="info">{{ getItemType(item) }}</el-tag>
        </div>
        <div class="item-actions">
          <el-button size="small" :icon="ArrowUp" circle :disabled="index === 0" @click="moveUp(index)" />
          <el-button size="small" :icon="ArrowDown" circle :disabled="index === modelValue.length - 1" @click="moveDown(index)" />
          <el-button size="small" type="primary" :icon="Edit" circle @click="handleEdit(index)" />
          <el-button size="small" type="danger" :icon="Delete" circle @click="handleRemove(index)" />
        </div>
      </div>

      <el-empty v-if="!modelValue?.length" description="暂无数据，点击上方添加" :image-size="60" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" append-to-body>
      <slot name="form" :data="editData" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ArrowUp, ArrowDown, Edit, Delete } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  modelValue: any[]
  title: string
  dialogTitle?: string
  getItemLabel: (item: any) => string
  getItemType: (item: any) => string
  createDefault: () => any
}>(), {
  dialogTitle: '编辑'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: any[]): void
}>()

const dialogVisible = ref(false)
const editIndex = ref(-1)
const editData = ref<any>({})

const handleAdd = () => {
  editIndex.value = -1
  editData.value = props.createDefault()
  dialogVisible.value = true
}

const handleEdit = (index: number) => {
  editIndex.value = index
  editData.value = { ...props.modelValue[index] }
  dialogVisible.value = true
}

const handleRemove = (index: number) => {
  const arr = [...props.modelValue]
  arr.splice(index, 1)
  emit('update:modelValue', arr)
}

const moveUp = (index: number) => {
  if (index <= 0) return
  const arr = [...props.modelValue]
  ;[arr[index - 1], arr[index]] = [arr[index], arr[index - 1]]
  emit('update:modelValue', arr)
}

const moveDown = (index: number) => {
  if (index >= props.modelValue.length - 1) return
  const arr = [...props.modelValue]
  ;[arr[index], arr[index + 1]] = [arr[index + 1], arr[index]]
  emit('update:modelValue', arr)
}

const handleConfirm = () => {
  const arr = [...props.modelValue]
  if (editIndex.value === -1) {
    arr.push({ ...editData.value })
  } else {
    arr[editIndex.value] = { ...editData.value }
  }
  emit('update:modelValue', arr)
  dialogVisible.value = false
}
</script>

<style scoped lang="scss">
.array-editor {
  .editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .editor-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
    }
  }

  .editor-list {
    .editor-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 12px;
      margin-bottom: 6px;
      background: #f5f7fa;
      border-radius: 6px;
      border: 1px solid #ebeef5;
      transition: border-color 0.2s;

      &:hover {
        border-color: #409eff;
      }

      .item-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .item-label {
          font-size: 13px;
          color: #303133;
          max-width: 120px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .item-actions {
        display: flex;
        gap: 4px;
        flex-shrink: 0;
      }
    }
  }
}
</style>
