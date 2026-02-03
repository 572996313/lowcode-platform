<template>
  <template v-for="menu in menus" :key="menu.id">
    <!-- 目录类型：递归渲染子菜单 -->
    <el-sub-menu v-if="menu.menuType === 1 && hasVisibleChildren(menu)" :index="menu.menuCode">
      <template #title>
        <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
        <span>{{ menu.menuName }}</span>
      </template>
      <!-- 递归渲染子菜单 -->
      <SidebarItem :menus="menu.children || []" />
    </el-sub-menu>

    <!-- 菜单类型：渲染为菜单项 -->
    <el-menu-item
      v-else-if="menu.menuType === 2 && menu.routePath && isVisible(menu)"
      :index="menu.routePath"
    >
      <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
      <template #title>{{ menu.menuName }}</template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
interface MenuItem {
  id: number
  menuName: string
  menuCode: string
  menuType: number // 1: 目录, 2: 菜单, 3: 按钮
  routePath?: string
  icon?: string
  visible?: boolean
  status?: boolean
  children?: MenuItem[]
}

interface Props {
  menus: MenuItem[]
}

defineProps<Props>()

// 检查菜单是否可见
const isVisible = (menu: MenuItem): boolean => {
  return menu.visible !== false && menu.status !== false
}

// 检查是否有可见的子菜单
const hasVisibleChildren = (menu: MenuItem): boolean => {
  if (!menu.children || menu.children.length === 0) {
    return false
  }
  return menu.children.some(child => isVisible(child))
}
</script>
