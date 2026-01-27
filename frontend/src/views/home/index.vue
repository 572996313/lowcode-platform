<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.title">
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
            <div class="info-item">
              <span class="label">系统版本：</span>
              <span class="value">1.0.0</span>
            </div>
            <div class="info-item">
              <span class="label">前端框架：</span>
              <span class="value">Vue 3 + Element Plus</span>
            </div>
            <div class="info-item">
              <span class="label">后端框架：</span>
              <span class="value">Spring Boot 3.x</span>
            </div>
            <div class="info-item">
              <span class="label">数据库：</span>
              <span class="value">MySQL 8.0</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const stats = ref([
  { title: '页面配置', value: 0, icon: 'Document', color: '#409eff' },
  { title: '表单配置', value: 0, icon: 'EditPen', color: '#67c23a' },
  { title: '表格配置', value: 0, icon: 'Grid', color: '#e6a23c' },
  { title: '菜单配置', value: 9, icon: 'Menu', color: '#f56c6c' }
])

const quickLinks = ref([
  { title: '页面管理', desc: '管理系统页面配置', icon: 'Document', color: '#409eff', path: '/lowcode/page' },
  { title: '表单管理', desc: '管理系统表单配置', icon: 'Document', color: '#67c23a', path: '/lowcode/form/list' },
  { title: '表格管理', desc: '管理系统表格配置', icon: 'Grid', color: '#e6a23c', path: '/lowcode/table/list' }
])
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
