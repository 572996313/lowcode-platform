<template>
  <div class="dict-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据字典管理</span>
          <el-button type="primary" @click="openCreateCategoryDialog">
            新建分类
          </el-button>
        </div>
      </template>

      <!-- 左侧分类树 -->
      <el-container>
        <el-aside width="300px">
          <el-tree
            :data="categoryTree"
            :props="{ children: 'children', label: 'categoryName' }"
            node-key="id"
            highlight-current
            @node-click="handleCategoryClick"
          >
            <template #default="{ node, data }">
              <span>{{ data.categoryName }}</span>
              <el-dropdown style="margin-left: 10px" @command="(cmd) => handleCategoryCommand(cmd, data)">
                <span @click.stop>
                  <el-icon><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="addItem">
                      <el-icon><Plus /></el-icon>
                      添加项
                    </el-dropdown-item>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </span>
            </el-template>
          </el-tree>
        </el-aside>

        <!-- 右侧字典项列表 -->
        <el-main>
          <el-card shadow="never">
            <template #header>
              <span>{{ currentCategory?.categoryName || '选择分类查看字典项' }}</span>
            </template>

            <el-table :data="dictItems" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="itemLabel" label="字典项标签" />
              <el-table-column prop="itemValue" label="字典项值" />
              <el-table-column prop="cssClass" label="颜色" width="120">
                <template #default="{ row }">
                  <el-color-picker
                    v-model="row.cssClass"
                    size="small"
                    @change="updateItemColor(row)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="sortOrder" label="排序" width="100" align="center" />
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" @click="editItem(row)">
                    编辑
                  </el-button>
                  <el-button link type="danger" @click="handleDeleteItem(row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-card>

    <!-- 新建/编辑分类对话框 -->
    <el-dialog
      v-model="showCategoryDialog"
      :title="categoryDialogMode === 'create' ? '新建分类' : '编辑分类'"
      width="500px"
    >
      <el-form :model="categoryForm" label-width="120px" :rules="categoryRules" ref="categoryFormRef">
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="如：user_status" />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="如：用户状态" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新建/编辑字典项对话框 -->
    <el-dialog
      v-model="showItemDialog"
      :title="itemDialogMode === 'create' ? '新建字典项' : '编辑字典项'"
      width="500px"
    >
      <el-form :model="itemForm" label-width="120px" :rules="itemRules" ref="itemFormRef">
        <el-form-item label="字典项标签" prop="itemLabel">
          <el-input v-model="itemForm.itemLabel" placeholder="如：启用" />
        </el-form-item>
        <el-form-item label="字典项值" prop="itemValue">
          <el-input v-model="itemForm.itemValue" placeholder="如：1" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="itemForm.cssClass" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="itemForm.icon" placeholder="图标名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="itemForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showItemDialog = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getCategoryTree,
  getItemTree,
  createCategory,
  updateCategory,
  deleteCategory,
  createItem,
  updateItem,
  deleteItem,
  type DictCategory,
  type DictItem
} from '@/api/dict'

// 数据
const categoryTree = ref<DictCategory[]>([])
const dictItems = ref<DictItem[]>([])
const currentCategory = ref<DictCategory>()

// 加载状态
const loading = ref(false)

// 对话框
const showCategoryDialog = ref(false)
const showItemDialog = ref(false)
const categoryDialogMode = ref<'create' | 'edit'>('create')
const itemDialogMode = ref<'create' | 'edit'>('create')

// 表单引用
const categoryFormRef = ref<FormInstance>()
const itemFormRef = ref<FormInstance>()

// 分类表单
const categoryForm = reactive({
  id: undefined as number | undefined,
  categoryCode: '',
  categoryName: '',
  sortOrder: 0,
  description: ''
})

// 字典项表单
const itemForm = reactive({
  id: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  itemLabel: '',
  itemValue: '',
  cssClass: '',
  icon: '',
  sortOrder: 0
})

// 表单验证规则
const categoryRules: FormRules = {
  categoryCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const itemRules: FormRules = {
  itemLabel: [{ required: true, message: '请输入字典项标签', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入字典项值', trigger: 'blur' }]
}

// 加载分类列表
async function loadCategoryList() {
  try {
    const categories = await getCategoryTree()
    categoryTree.value = categories
  } catch (error: any) {
    ElMessage.error(error.message || '加载分类列表失败')
  }
}

// 分类节点点击
async function handleCategoryClick(data: DictCategory) {
  currentCategory.value = data
  await loadDictItems(data.id)
}

// 加载字典项列表
async function loadDictItems(categoryId: number) {
  loading.value = true
  try {
    const items = await getItemTree(categoryId)
    dictItems.value = items
  } catch (error: any) {
    ElMessage.error(error.message || '加载字典项失败')
  } finally {
    loading.value = false
  }
}

// 分类操作
async function handleCategoryCommand(command: string, data: DictCategory) {
  switch (command) {
    case 'addItem':
      openCreateItemDialog(data.id!)
      break
    case 'edit':
      openEditCategoryDialog(data)
      break
    case 'delete':
      await handleDeleteCategory(data)
      break
  }
}

// 打开新建分类对话框
function openCreateCategoryDialog() {
  categoryDialogMode.value = 'create'
  Object.assign(categoryForm, {
    id: undefined,
    categoryCode: '',
    categoryName: '',
    sortOrder: 0,
    description: ''
  })
  showCategoryDialog.value = true
}

// 打开编辑分类对话框
function openEditCategoryDialog(data: DictCategory) {
  categoryDialogMode.value = 'edit'
  Object.assign(categoryForm, {
    id: data.id,
    categoryCode: data.categoryCode,
    categoryName: data.categoryName,
    sortOrder: data.sortOrder,
    description: data.description || ''
  })
  showCategoryDialog.value = true
}

// 保存分类
async function saveCategory() {
  if (!categoryFormRef.value) return

  await categoryFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const data = {
        categoryName: categoryForm.categoryName,
        categoryCode: categoryForm.categoryCode,
        sortOrder: categoryForm.sortOrder,
        description: categoryForm.description,
        parentId: 0
      }

      if (categoryDialogMode.value === 'create') {
        await createCategory(data)
      } else {
        await updateCategory(categoryForm.id!, data)
      }

      ElMessage.success('保存成功')
      showCategoryDialog.value = false
      loadCategoryList()
    } catch (error: any) {
      ElMessage.error(error.message || '保存失败')
    }
  })
}

// 删除分类
async function handleDeleteCategory(data: DictCategory) {
  try {
    await ElMessageBox.confirm(`确定要删除分类"${data.categoryName}"吗？`, '提示', {
      type: 'warning'
    })

    await deleteCategory(data.id!)
    ElMessage.success('删除成功')
    loadCategoryList()
  } catch (error) {
    // 用户取消
  }
}

// 打开新建字典项对话框
function openCreateItemDialog(categoryId: number) {
  itemDialogMode.value = 'create'
  Object.assign(itemForm, {
    id: undefined,
    categoryId,
    itemLabel: '',
    itemValue: '',
    cssClass: '',
    icon: '',
    sortOrder: 0
  })
  showItemDialog.value = true
}

// 打开编辑字典项对话框
function openEditItemDialog(data: DictItem) {
  itemDialogMode.value = 'edit'
  Object.assign(itemForm, {
    id: data.id,
    categoryId: data.categoryId,
    itemLabel: data.itemLabel,
    itemValue: data.itemValue,
    cssClass: data.cssClass || '',
    icon: data.icon || '',
    sortOrder: data.sortOrder
  })
  showItemDialog.value = true
}

// 编辑字典项
function editItem(row: DictItem) {
  openEditItemDialog(row)
}

// 保存字典项
async function saveItem() {
  if (!itemFormRef.value) return

  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const data = {
        categoryId: itemForm.categoryId!,
        itemLabel: itemForm.itemLabel,
        itemValue: itemForm.itemValue,
        cssClass: itemForm.cssClass,
        icon: itemForm.icon,
        sortOrder: itemForm.sortOrder,
        description: ''
      }

      if (itemDialogMode.value === 'create') {
        await createItem(data)
      } else {
        await updateItem(itemForm.id!, data)
      }

      ElMessage.success('保存成功')
      showItemDialog.value = false
      if (currentCategory.value) {
        loadDictItems(currentCategory.value.id!)
      }
    } catch (error: any) {
      ElMessage.error(error.message || '保存失败')
    }
  })
}

// 删除字典项
async function handleDeleteItem(row: DictItem) {
  try {
    await ElMessageBox.confirm(`确定要删除字典项"${row.itemLabel}"吗？`, '提示', {
      type: 'warning'
    })

    await deleteItem(row.id!)
    ElMessage.success('删除成功')
    if (currentCategory.value) {
      loadDictItems(currentCategory.value.id!)
    }
  } catch (error) {
    // 用户取消
  }
}

// 更新字典项颜色
async function updateItemColor(row: DictItem) {
  try {
    await updateItem(row.id!, {
      ...row,
      cssClass: row.cssClass
    })
    ElMessage.success('颜色更新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  }
}

onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.dict-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-container {
  height: 600px;
}

.el-aside {
  border-right: 1px solid #dcdfe6;
  padding-right: 20px;
}

.el-main {
  padding-left: 20px;
}
</style>
