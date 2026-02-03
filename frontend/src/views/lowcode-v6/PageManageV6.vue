<template>
  <div class="page-manage-v6">
    <div class="page-header">
      <h3>页面管理</h3>
      <el-button type="primary" :icon="Plus" @click="handleCreate">
        新建页面
      </el-button>
    </div>

    <div class="page-content">
      <el-table
        v-loading="loading"
        :data="pageList"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="pageName" label="页面名称" min-width="150" />
        <el-table-column prop="pageCode" label="页面编码" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.published" type="success">已发布</el-tag>
            <el-tag v-else type="info">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" text @click="handlePreview(row)">
              预览
            </el-button>
            <el-button
              v-if="!row.published"
              type="warning"
              size="small"
              text
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.published"
              type="warning"
              size="small"
              text
              @click="handleUnpublish(row)"
            >
              取消发布
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="页面预览"
      width="90%"
      :fullscreen="isFullscreen"
      destroy-on-close
    >
      <PageRenderV6
        v-if="previewVisible && currentPageConfig"
        :page-config="currentPageConfig"
      />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button :icon="FullScreen" @click="toggleFullscreen">
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, FullScreen } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageRenderV6 from './PageRenderV6.vue'
import type { NewPageConfig } from '@/types/page-v6'
import { getPageList, publishPage, deletePage, unpublishPage } from '@/api/page'

const router = useRouter()

// 列表数据
const loading = ref(false)
const pageList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 预览
const previewVisible = ref(false)
const isFullscreen = ref(false)
const currentPageConfig = ref<NewPageConfig | null>(null)

// 初始化
onMounted(() => {
  loadPageList()
})

/**
 * 分页变化
 */
function handlePageChange(page: number) {
  currentPage.value = page
  loadPageList()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadPageList()
}

/**
 * 加载页面列表
 */
async function loadPageList() {
  loading.value = true
  try {
    const res = await getPageList({
      current: currentPage.value,
      size: pageSize.value
    })
    // 响应拦截器已经返回了 data 对象，直接使用 res 而不是 res.data
    pageList.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载页面列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

/**
 * 新建页面
 */
function handleCreate() {
  router.push('/lowcode/FreeCanvasDesigner/new')
}

/**
 * 编辑页面
 */
function handleEdit(row: any) {
  router.push(`/lowcode/FreeCanvasDesigner/${row.id}`)
}

/**
 * 预览页面
 */
async function handlePreview(row: any) {
  try {
    // TODO: 调用 API 加载页面配置
    // const res = await getPageConfig(row.id)
    // currentPageConfig.value = res.data
    // previewVisible.value = true

    ElMessage.info('预览功能待实现')
  } catch (error) {
    ElMessage.error('加载页面配置失败')
    console.error(error)
  }
}

/**
 * 发布页面
 */
async function handlePublish(row: any) {
  try {
    // 弹出对话框输入路由路径
    const { value: routePath } = await ElMessageBox.prompt(
      '请输入页面路由路径（如：/pages/custom）',
      '发布页面',
      {
        confirmButtonText: '确认发布',
        cancelButtonText: '取消',
        inputPattern: /^\/.+/,
        inputErrorMessage: '路由路径必须以 / 开头'
      }
    )

    // 调用发布 API
    await publishPage(row.id, { routePath })

    // 更新本地状态
    row.published = true
    row.routePath = routePath
    ElMessage.success('发布成功')

    // 重新加载列表以获取最新状态
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
      console.error(error)
    }
  }
}

/**
 * 取消发布页面
 */
async function handleUnpublish(row: any) {
  try {
    await ElMessageBox.confirm(
      '取消发布后页面将不再可访问，是否继续？',
      '确认取消发布',
      { type: 'warning' }
    )

    await unpublishPage(row.id)
    row.published = false
    row.routePath = null
    ElMessage.success('已取消发布')
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消发布失败')
      console.error(error)
    }
  }
}

/**
 * 删除页面
 */
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      '删除后无法恢复，是否继续？',
      '确认删除',
      { type: 'warning' }
    )

    await deletePage(row.id)
    ElMessage.success('删除成功')
    loadPageList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

/**
 * 切换全屏
 */
function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}
</script>

<style scoped lang="scss">
.page-manage-v6 {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 40px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
    color: #303133;
  }
}

.page-content {
  :deep(.el-pagination) {
    display: flex;
  }
}
</style>
