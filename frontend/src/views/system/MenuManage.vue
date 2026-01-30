<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 工具栏 -->
    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增菜单
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      :data="menuTree"
      row-key="id"
      border
      default-expand-all
      :tree-props="{ children: 'children' }"
      v-loading="loading"
    >
      <el-table-column prop="menuName" label="菜单名称" min-width="180" />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="menuCode" label="菜单编码" width="150" />
      <el-table-column prop="menuType" label="类型" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 1">目录</el-tag>
          <el-tag v-else-if="row.menuType === 2" type="success">菜单</el-tag>
          <el-tag v-else type="warning">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="routePath" label="路由地址" width="180" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleAddChild(row)">新增</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="form.menuType === 2">
          <el-form-item label="组件来源">
            <el-radio-group v-model="componentSource">
              <el-radio value="static">静态组件</el-radio>
              <el-radio value="dynamic">低代码页面</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单编码" prop="menuCode">
          <el-input v-model="form.menuCode" placeholder="请输入菜单编码" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.menuType !== 3">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="组件路径" prop="componentPath" v-if="form.menuType === 2 && componentSource === 'static'">
          <el-select v-model="form.componentPath" placeholder="请选择组件路径" style="width: 100%">
            <el-option label="暂不配置(显示占位页面)" value="" />
            <el-option label="用户管理 /views/system/UserManage.vue" value="/views/system/UserManage.vue" />
            <el-option label="角色管理 /views/system/RoleManage.vue" value="/views/system/RoleManage.vue" />
            <el-option label="菜单管理 /views/system/MenuManage.vue" value="/views/system/MenuManage.vue" />
            <el-option label="页面管理 /views/lowcode/PageManage.vue" value="/views/lowcode/PageManage.vue" />
            <el-option label="表单管理 /views/lowcode/FormManage.vue" value="/views/lowcode/FormManage.vue" />
            <el-option label="表格管理 /views/lowcode/TableManage.vue" value="/views/lowcode/TableManage.vue" />
            <el-option label="按钮管理 /views/lowcode/ButtonManage.vue" value="/views/lowcode/ButtonManage.vue" />
            <el-option label="页面设计器 /views/lowcode/PageDesigner.vue" value="/views/lowcode/PageDesigner.vue" />
          </el-select>
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>如果选择"暂不配置"，访问菜单时将显示默认占位页面</span>
          </div>
        </el-form-item>
        <el-form-item label="低代码页面" prop="pageId" v-if="form.menuType === 2 && componentSource === 'dynamic'">
          <el-select
            v-model="form.pageId"
            placeholder="请选择已发布的页面"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="page in publishedPages"
              :key="page.id"
              :label="`${page.pageName} (${page.routePath})`"
              :value="page.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="路由地址" prop="routePath" v-if="form.menuType !== 3">
          <el-input
            v-model="form.routePath"
            :disabled="componentSource === 'dynamic' && form.pageId"
            placeholder="请输入路由地址"
          >
            <template #append v-if="componentSource === 'dynamic' && form.pageId">
              <el-button @click="autoFillRoutePath">自动填充</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuTree, createMenu, updateMenu, deleteMenu, type Menu } from '@/api/menu'
import { getPublishedPages, type PageConfig } from '@/api/page'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const componentSource = ref('static')

const menuTree = ref<Menu[]>([])
const publishedPages = ref<PageConfig[]>([])
const queryParams = reactive({
  menuName: ''
})

const form = reactive<Partial<Menu>>({
  parentId: 0,
  menuName: '',
  menuCode: '',
  menuType: 1,
  icon: '',
  routePath: '',
  componentPath: '',
  sortOrder: 0,
  status: true
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuCode: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const menuTreeOptions = computed(() => {
  return [{ id: 0, menuName: '根目录', children: menuTree.value }]
})

const loadMenuTree = async () => {
  loading.value = true
  try {
    menuTree.value = await getMenuTree()
  } catch (error) {
    console.error('加载菜单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadMenuTree()
}

const handleReset = () => {
  queryParams.menuName = ''
  loadMenuTree()
}

const resetForm = () => {
  Object.assign(form, {
    id: undefined,
    parentId: 0,
    menuName: '',
    menuCode: '',
    menuType: 1,
    icon: '',
    routePath: '',
    componentPath: '',
    pageId: undefined,
    sortOrder: 0,
    status: true
  })
  componentSource.value = 'static'
}

// 加载已发布的页面列表
const loadPublishedPages = async () => {
  try {
    publishedPages.value = await getPublishedPages()
  } catch (error) {
    console.error('加载已发布页面失败:', error)
  }
}

// 自动填充路由路径
const autoFillRoutePath = () => {
  const page = publishedPages.value.find(p => p.id === form.pageId)
  if (page && page.routePath) {
    form.routePath = page.routePath
    form.componentPath = '/views/lowcode/PageRender.vue'
  }
}

// 监听选择的页面变化
const handlePageChange = () => {
  if (componentSource.value === 'dynamic' && form.pageId) {
    autoFillRoutePath()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
  loadPublishedPages()
}

const handleAddChild = (row: Menu) => {
  resetForm()
  form.parentId = row.id
  dialogTitle.value = '新增子菜单'
  dialogVisible.value = true
}

const handleEdit = (row: Menu) => {
  Object.assign(form, row)
  // 如果菜单关联了低代码页面，设置为动态模式
  if (row.pageId) {
    componentSource.value = 'dynamic'
  } else {
    componentSource.value = 'static'
  }
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
  loadPublishedPages()
}

const handleDelete = async (row: Menu) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      type: 'warning'
    })
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadMenuTree()
  } catch (error) {
    // 取消删除
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updateMenu(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createMenu(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadMenuTree()
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 监听页面选择变化
watch(() => form.pageId, () => {
  if (componentSource.value === 'dynamic' && form.pageId) {
    autoFillRoutePath()
  }
})

onMounted(() => {
  loadMenuTree()
})
</script>

<style lang="scss" scoped>
.page-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);

  .el-icon {
    color: var(--el-color-primary);
  }
}
</style>
