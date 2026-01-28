<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'is-collapse': isCollapse }">
      <div class="logo">
        <img src="/vite.svg" alt="logo" />
        <span v-show="!isCollapse">低代码平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <!-- 动态菜单 -->
        <template v-for="menu in sidebarMenus" :key="menu.id">
          <!-- 目录类型 -->
          <el-sub-menu v-if="menu.menuType === 1" :index="menu.menuCode">
            <template #title>
              <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item v-if="child.menuType === 2 && child.routePath" :index="child.routePath">
                <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
                <template #title>{{ child.menuName }}</template>
              </el-menu-item>
            </template>
          </el-sub-menu>

          <!-- 菜单类型（直接在顶层） -->
          <el-menu-item v-else-if="menu.menuType === 2 && menu.routePath" :index="menu.routePath">
            <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.menuName }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 头部 -->
      <div class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区 -->
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const route = useRoute()
const router = useRouter()
const menuStore = useMenuStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const sidebarMenus = computed(() => {
  return menuStore.getSidebarMenus().filter(m => m.status !== false)
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  localStorage.removeItem('token')
  menuStore.reset()
  router.push('/login')
}

onMounted(() => {
  menuStore.loadMenus()
})
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 220px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;

  &.is-collapse {
    width: 64px;
  }

  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4a;

    img {
      width: 32px;
      height: 32px;
    }

    span {
      margin-left: 10px;
      color: #fff;
      font-size: 16px;
      font-weight: bold;
    }
  }

  .el-menu {
    border-right: none;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 50px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;

  .header-left {
    display: flex;
    align-items: center;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 15px;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin-left: 8px;
      }
    }
  }
}

.content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background-color: #f5f7fa;
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
