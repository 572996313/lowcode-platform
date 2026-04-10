<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="4" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="24"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>快速开始</span>
          </template>
          <div class="quick-start">
            <el-row :gutter="20">
              <el-col :span="8" v-for="item in quickLinks" :key="item.title">
                <div class="quick-item" @click="$router.push(item.path)">
                  <el-icon :size="32" :color="item.color"><component :is="item.icon" /></el-icon>
                  <div class="quick-title">{{ item.title }}</div>
                  <div class="quick-desc">{{ item.desc }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <div class="system-info">
            <div class="info-item" v-for="item in systemInfoList" :key="item.label">
              <span class="label">{{ item.label }}：</span>
              <span class="value">{{ item.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { getDashboardStats } from '@/api/dashboard'

const stats = ref([
  { title: '页面配置', value: 0, icon: 'Document', color: '#409eff' },
  { title: '表单配置', value: 0, icon: 'EditPen', color: '#67c23a' },
  { title: '表格配置', value: 0, icon: 'Grid', color: '#e6a23c' },
  { title: '菜单配置', value: 0, icon: 'Menu', color: '#f56c6c' },
  { title: '按钮配置', value: 0, icon: 'Pointer', color: '#9b59b6' },
  { title: '数据库表', value: 0, icon: 'Coin', color: '#3498db' }
])

const quickLinks = ref([
  { title: '页面管理', desc: '管理系统页面配置', icon: 'Document', color: '#409eff', path: '/lowcode/PageManage' },
  { title: '表单管理', desc: '管理系统表单配置', icon: 'Document', color: '#67c23a', path: '/lowcode/FormManage' },
  { title: '表格管理', desc: '管理系统表格配置', icon: 'Grid', color: '#e6a23c', path: '/lowcode/TableManage' }
])

const systemInfoList = ref<{ label: string; value: string }[]>([])

const loadingStats = async () => {
  const data = await getDashboardStats()

  // 更新统计卡片
  stats.value[0].value = data.pageCount
  stats.value[1].value = data.formCount
  stats.value[2].value = data.tableCount
  stats.value[3].value = data.menuCount
  stats.value[4].value = data.buttonCount
  stats.value[5].value = data.dbTableCount

  // 更新系统信息
  systemInfoList.value = [
    { label: '系统版本', value: data.systemVersion },
    { label: '前端框架', value: data.frontendFramework },
    { label: '后端框架', value: `Spring Boot ${data.springBootVersion}` },
    { label: 'Java 版本', value: data.javaVersion },
    { label: '数据库', value: `MySQL ${data.databaseVersion}` },
    { label: '操作系统', value: `${data.osName} ${data.osArch}` },
    { label: '已发布页面', value: `${data.publishedPageCount} 个` }
  ]
}

onMounted(() => {
  loadingStats()
})
</script>

<style lang="scss" scoped>
.home-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .stat-info {
        margin-left: 16px;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .stat-title {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }

  .quick-start {
    .quick-item {
      padding: 20px;
      text-align: center;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .quick-title {
        font-size: 16px;
        font-weight: bold;
        margin-top: 12px;
        color: #303133;
      }

      .quick-desc {
        font-size: 12px;
        color: #909399;
        margin-top: 8px;
      }
    }
  }

  .system-info {
    .info-item {
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #909399;
      }

      .value {
        color: #303133;
      }
    }
  }
}
</style>
