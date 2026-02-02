<template>
  <div class="page-layout-designer">
    <div class="page-header">
      <div class="header-left">
        <el-button link @click="handleGoBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>{{ isEdit ? '编辑页面布局' : '新建页面布局' }}</h2>
      </div>
      <div class="header-right">
        <el-button @click="handleGoBack">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </div>
    </div>

    <div class="content-container">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="designer-form"
      >
        <el-card class="config-card" header="基本信息">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="布局名称" prop="layoutName">
                <el-input v-model="formData.layoutName" placeholder="请输入布局名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="布局编码" prop="layoutCode">
                <el-input
                  v-model="formData.layoutCode"
                  placeholder="请输入布局编码（英文）"
                  :disabled="isEdit && formData.published"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="布局类型" prop="layoutType">
                <el-select
                  v-model="formData.layoutType"
                  placeholder="请选择布局类型"
                  :disabled="isEdit && formData.published"
                  style="width: 100%"
                >
                  <el-option label="上下布局" value="top-bottom" />
                  <el-option label="左右布局" value="left-right" />
                  <el-option label="树表布局" value="tree-table" />
                  <el-option label="标签页布局" value="tabs" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="路由路径" prop="routePath">
                <el-input
                  v-model="formData.routePath"
                  placeholder="请输入路由路径，如 /page/demo"
                  :disabled="isEdit && formData.published"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="状态">
                <el-switch v-model="formData.status" active-text="启用" inactive-text="禁用" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="config-card" header="区域配置">
          <div class="area-config">
            <div
              v-for="(area, index) in areas"
              :key="index"
              class="area-item"
            >
              <div class="area-header">
                <span class="area-title">区域 {{ index + 1 }}</span>
                <el-button
                  type="danger"
                  size="small"
                  link
                  @click="removeArea(index)"
                >
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </div>
              <el-form-item label="区域ID">
                <el-input v-model="area.id" placeholder="如 toolbar/search/content" />
              </el-form-item>
              <el-form-item label="区域标题">
                <el-input v-model="area.title" placeholder="如 工具栏/搜索区/内容区" />
              </el-form-item>
              <el-form-item label="组件类型">
                <el-select v-model="area.componentType" placeholder="请选择组件类型" style="width: 100%">
                  <el-option label="按钮组" value="button" />
                  <el-option label="表单" value="form" />
                  <el-option label="表格" value="table" />
                </el-select>
              </el-form-item>
            </div>
            <el-button type="dashed" @click="addArea" style="width: 100%">
              <el-icon><Plus /></el-icon> 添加区域
            </el-button>
          </div>
        </el-card>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue'
import {
  getPageLayout,
  createPageLayout,
  updatePageLayout,
  type PageLayout,
  type AreaConfig
} from '@/api/page-layout'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const saving = ref(false)
const isEdit = ref(false)

// 表单数据
const formData = reactive<PageLayout>({
  layoutName: '',
  layoutCode: '',
  layoutType: 'top-bottom',
  routePath: '',
  status: true,
  published: false
})

// 区域配置
const areas = ref<AreaConfig[]>([
  { id: 'toolbar', title: '工具栏', componentType: 'button' },
  { id: 'search', title: '搜索区', componentType: 'form' },
  { id: 'content', title: '内容区', componentType: 'table' }
])

// 表单验证规则
const formRules = {
  layoutName: [{ required: true, message: '请输入布局名称', trigger: 'blur' }],
  layoutCode: [
    { required: true, message: '请输入布局编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '布局编码只能包含字母、数字、下划线和横线', trigger: 'blur' }
  ],
  layoutType: [{ required: true, message: '请选择布局类型', trigger: 'change' }]
}

// 添加区域
const addArea = () => {
  areas.value.push({
    id: `area_${areas.value.length + 1}`,
    title: `区域${areas.value.length + 1}`,
    componentType: 'table'
  })
}

// 删除区域
const removeArea = (index: number) => {
  areas.value.splice(index, 1)
}

// 返回列表
const handleGoBack = () => {
  router.push({ name: 'PageLayoutManage' })
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value?.validate()

    saving.value = true

    // 构建区域配置JSON
    const areaConfig = JSON.stringify({ areas: areas.value })

    const data = {
      ...formData,
      areaConfig
    }

    if (isEdit.value && formData.id) {
      await updatePageLayout(formData.id, data)
      ElMessage.success('更新成功')
    } else {
      const id = await createPageLayout(data)
      ElMessage.success('创建成功')
      // 跳转到编辑模式
      router.push({
        name: 'PageLayoutDesigner',
        query: { id: id.toString() }
      })
      return
    }

    handleGoBack()
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    }
  } finally {
    saving.value = false
  }
}

// 加载数据
const loadData = async () => {
  const id = route.query.id as string
  if (id) {
    isEdit.value = true
    try {
      const data = await getPageLayout(Number(id))
      Object.assign(formData, data)

      // 解析区域配置
      if (data.areaConfig) {
        try {
          const config = JSON.parse(data.areaConfig)
          areas.value = config.areas || areas.value
        } catch (e) {
          console.error('解析区域配置失败', e)
        }
      }
    } catch (error) {
      ElMessage.error('加载布局数据失败')
      handleGoBack()
    }
  }
}

// 页面挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-layout-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .content-container {
    flex: 1;
    padding: 24px;
    overflow: auto;

    .designer-form {
      max-width: 1000px;
      margin: 0 auto;
    }

    .config-card {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .area-config {
      .area-item {
        padding: 16px;
        border: 1px solid #e6e6e6;
        border-radius: 4px;
        margin-bottom: 16px;
        background-color: #fafafa;

        .area-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #e6e6e6;

          .area-title {
            font-weight: 500;
            color: #303133;
          }
        }
      }
    }
  }
}
</style>
