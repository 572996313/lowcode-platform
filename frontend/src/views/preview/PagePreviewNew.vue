<template>
  <div class="page-preview">
    <el-page-header @back="goBack" title="页面预览" />

    <el-card class="preview-config" style="margin-top: 20px;">
      <el-form :inline="true">
        <el-form-item label="选择页面">
          <el-select
            v-model="selectedPageId"
            placeholder="请选择页面"
            style="width: 300px"
            @change="handlePageChange"
          >
            <el-option
              v-for="page in pageList"
              :key="page.id"
              :label="`${page.pageName} (${page.pageCode})`"
              :value="page.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPreview" :loading="loading">
            加载预览
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 页面渲染区 -->
    <div class="preview-content" v-loading="loading">
      <page-render-new
        v-if="selectedPageId"
        :page-id="selectedPageId"
      />
      <el-empty v-else description="请选择页面进行预览" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPageConfigNew } from '@/api/dataset'

const router = useRouter()

const selectedPageId = ref<number>()
const pageList = ref<any[]>([])
const loading = ref(false)

// 返回
function goBack() {
  router.back()
}

// 加载页面列表
async function loadPageList() {
  try {
    const result = await getPageConfigNew({ current: 1, size: 100 })
    pageList.value = result.records.filter((p: any) => p.published)

    // 自动选择第一个已发布的页面
    if (pageList.value.length > 0 && !selectedPageId.value) {
      selectedPageId.value = pageList.value[0].id
      loadPreview()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载页面列表失败')
  }
}

// 页面变更
function handlePageChange() {
  loadPreview()
}

// 加载预览
function loadPreview() {
  if (!selectedPageId.value) {
    ElMessage.warning('请先选择页面')
    return
  }
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

onMounted(() => {
  loadPageList()
})
</script>

<style scoped>
.page-preview {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.preview-config {
  margin-bottom: 20px;
}

.preview-content {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  min-height: 600px;
}
</style>
