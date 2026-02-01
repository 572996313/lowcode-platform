<template>
  <div class="dict-manage">
    <div class="page-header">
      <h2>字典管理</h2>
    </div>

    <div class="content-container">
      <!-- 左侧：字典分类树 -->
      <div class="category-tree-panel">
        <div class="panel-title">
          <span>字典分类</span>
          <el-button size="small" @click="handleAddCategory">
            <el-icon><Plus /></el-icon>新增
          </el-button>
        </div>
        <el-tree
          :data="categoryTreeData"
          :props="{ children: 'children', label: 'categoryName' }"
          node-key="id"
          :highlight-current="true"
          :expand-on-click-node="false"
          @node-click="handleCategoryClick"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span class="node-label">{{ node.label }}</span>
              <el-tag v-if="data.categoryCode" size="small" type="info">
                {{ data.categoryCode }}
              </el-tag>
            </div>
          </template>
        </el-tree>
      </div>

      <!-- 右侧：字典明细列表 -->
      <div class="item-list-panel">
        <el-empty v-if="!selectedCategoryId" description="请从左侧选择字典分类" />

        <template v-else>
          <!-- 操作工具栏 -->
          <div class="toolbar-area">
            <el-button type="primary" @click="handleAddItem">
              <el-icon><Plus /></el-icon>新增字典项
            </el-button>
            <el-button type="danger" :disabled="selectedItemIds.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
          </div>

          <!-- 字典明细表格（支持树形显示） -->
          <el-table
            :data="itemList"
            row-key="id"
            border
            default-expand-all
            :tree-props="{ children: 'children' }"
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="itemLabel" label="字典项标签" min-width="180" />
            <el-table-column prop="itemValue" label="字典项值" width="150" />
            <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
            <el-table-column prop="cssClass" label="CSS样式" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.cssClass" size="small">{{ row.cssClass }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status ? 'success' : 'info'">
                  {{ row.status ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleAddChildItem(row)">
                  添加子项
                </el-button>
                <el-button type="primary" size="small" link @click="handleEditItem(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" link @click="handleDeleteItem(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </div>
    </div>

    <!-- 新增/编辑分类对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="categoryFormMode === 'create' ? '新增字典分类' : '编辑字典分类'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="上级分类" prop="parentId">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="categoryOptions"
            :props="{ label: 'categoryName', value: 'id', children: 'children' }"
            placeholder="请选择上级分类（不选则为根分类）"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="请输入分类编码，如：sys_status" />
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="categoryForm.status" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCategory" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑字典项对话框 -->
    <el-dialog
      v-model="itemDialogVisible"
      :title="itemFormMode === 'create' ? '新增字典项' : '编辑字典项'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="上级项" prop="parentId">
          <el-tree-select
            v-model="itemForm.parentId"
            :data="itemOptions"
            :props="{ label: 'itemLabel', value: 'id', children: 'children' }"
            placeholder="请选择上级项（不选则为根项）"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="字典项标签" prop="itemLabel">
          <el-input v-model="itemForm.itemLabel" placeholder="请输入显示文本" />
        </el-form-item>
        <el-form-item label="字典项值" prop="itemValue">
          <el-input v-model="itemForm.itemValue" placeholder="请输入字典值，如：1" />
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number v-model="itemForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="itemForm.status" />
        </el-form-item>
        <el-form-item label="CSS样式">
          <el-input v-model="itemForm.cssClass" placeholder="如：tag-success" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="itemForm.icon" placeholder="图标名称或路径" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveItem" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  getItemTree,
  createItem,
  updateItem,
  deleteItem,
  batchDeleteItems,
  type DictCategory,
  type DictItem
} from '@/api/dict'

// 分类树数据
const categoryTreeData = ref<DictCategory[]>([])
const selectedCategoryId = ref<number | null>(null)
const loading = ref(false)
const submitLoading = ref(false)

// 字典明细列表
const itemList = ref<DictItem[]>([])
const selectedItemIds = ref<number[]>([])

// 分类对话框
const categoryDialogVisible = ref(false)
const categoryFormMode = ref<'create' | 'edit'>('create')
const categoryFormRef = ref<FormInstance>()
const categoryForm = reactive<Partial<DictCategory>>({
  parentId: 0,
  categoryName: '',
  categoryCode: '',
  sortOrder: 0,
  status: true,
  description: ''
})
const categoryRules: FormRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  categoryCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
}

// 字典项对话框
const itemDialogVisible = ref(false)
const itemFormMode = ref<'create' | 'edit'>('create')
const itemFormRef = ref<FormInstance>()
const itemForm = reactive<Partial<DictItem>>({
  categoryId: 0,
  parentId: 0,
  itemLabel: '',
  itemValue: '',
  sortOrder: 0,
  status: true
})
const itemRules: FormRules = {
  itemLabel: [{ required: true, message: '请输入字典项标签', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入字典项值', trigger: 'blur' }]
}

// 分类（树形选项）
const categoryOptions = computed<DictCategory[]>(() => {
  return [{ id: 0, parentId: 0, categoryName: '根分类', categoryCode: '', sortOrder: 0, status: true, children: categoryTreeData.value }]
})

// 字典项（树形选项）
const itemOptions = computed<DictItem[]>(() => {
  return [{ id: 0, categoryId: selectedCategoryId.value!, parentId: 0, itemLabel: '根项', itemValue: '', sortOrder: 0, status: true, children: itemList.value }]
})

// 加载分类树
const loadCategoryTree = async () => {
  loading.value = true
  try {
    categoryTreeData.value = await getCategoryTree()
  } catch (error) {
    ElMessage.error('加载字典分类失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 分类节点点击
const handleCategoryClick = (data: DictCategory) => {
  selectedCategoryId.value = data.id
  loadItemList()
}

// 加载字典明细列表
const loadItemList = async () => {
  if (!selectedCategoryId.value) {
    itemList.value = []
    return
  }

  loading.value = true
  try {
    itemList.value = await getItemTree(selectedCategoryId.value)
  } catch (error) {
    ElMessage.error('加载字典明细失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 表格选择变化
const handleSelectionChange = (selection: DictItem[]) => {
  selectedItemIds.value = selection.map(item => item.id)
}

// ===== 分类操作 =====

const handleAddCategory = () => {
  categoryFormMode.value = 'create'
  Object.assign(categoryForm, {
    parentId: 0,
    categoryName: '',
    categoryCode: '',
    sortOrder: 0,
    status: true,
    description: ''
  })
  categoryDialogVisible.value = true
}

const handleSaveCategory = async () => {
  if (!categoryFormRef.value) return

  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (categoryFormMode.value === 'create') {
          await createCategory(categoryForm)
          ElMessage.success('创建成功')
        } else {
          if (categoryForm.id) {
            await updateCategory(categoryForm.id, categoryForm)
            ElMessage.success('更新成功')
          }
        }
        categoryDialogVisible.value = false
        loadCategoryTree()
      } catch (error: any) {
        ElMessage.error(categoryFormMode.value === 'create' ? '创建失败' : '更新失败')
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// ===== 字典项操作 =====

const handleAddItem = () => {
  itemFormMode.value = 'create'
  Object.assign(itemForm, {
    categoryId: selectedCategoryId.value,
    parentId: 0,
    itemLabel: '',
    itemValue: '',
    sortOrder: 0,
    status: true,
    cssClass: '',
    icon: '',
    description: ''
  })
  itemDialogVisible.value = true
}

const handleAddChildItem = (row: DictItem) => {
  itemFormMode.value = 'create'
  Object.assign(itemForm, {
    categoryId: selectedCategoryId.value,
    parentId: row.id,
    itemLabel: '',
    itemValue: '',
    sortOrder: 0,
    status: true,
    cssClass: '',
    icon: '',
    description: ''
  })
  itemDialogVisible.value = true
}

const handleEditItem = (row: DictItem) => {
  itemFormMode.value = 'edit'
  Object.assign(itemForm, {
    ...row
  })
  itemDialogVisible.value = true
}

const handleSaveItem = async () => {
  if (!itemFormRef.value) return

  await itemFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (itemFormMode.value === 'create') {
          await createItem(itemForm)
          ElMessage.success('创建成功')
        } else {
          if (itemForm.id) {
            await updateItem(itemForm.id, itemForm)
            ElMessage.success('更新成功')
          }
        }
        itemDialogVisible.value = false
        loadItemList()
      } catch (error: any) {
        ElMessage.error(itemFormMode.value === 'create' ? '创建失败' : '更新失败')
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDeleteItem = async (row: DictItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除字典项"${row.itemLabel}"吗？`, '提示', {
      type: 'warning'
    })
    await deleteItem(row.id)
    ElMessage.success('删除成功')
    loadItemList()
  } catch (error) {
    // 取消删除
  }
}

const handleBatchDelete = async () => {
  if (selectedItemIds.value.length === 0) {
    ElMessage.warning('请选择要删除的字典项')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItemIds.value.length} 个字典项吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteItems(selectedItemIds.value)
    ElMessage.success('删除成功')
    loadItemList()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  loadCategoryTree()
})
</script>

<style lang="scss" scoped>
.dict-manage {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    padding: 20px 24px;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
      color: #303133;
    }
  }

  .content-container {
    flex: 1;
    display: flex;
    gap: 16px;
    padding: 24px;
    overflow: hidden;

    .category-tree-panel {
      width: 280px;
      background: #fff;
      border-radius: 4px;
      flex-shrink: 0;
      display: flex;
      flex-direction: column;

      .panel-title {
        padding: 16px;
        font-weight: 600;
        border-bottom: 1px solid #e6e6e6;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .tree-node {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        padding-right: 8px;

        .node-label {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .item-list-panel {
      flex: 1;
      background: #fff;
      border-radius: 4px;
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .toolbar-area {
        padding: 16px;
        border-bottom: 1px solid #e6e6e6;
        display: flex;
        gap: 12px;
      }

      .el-table {
        flex: 1;
      }
    }
  }
}
</style>
