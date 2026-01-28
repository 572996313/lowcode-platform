import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMenuTree, type Menu } from '@/api/menu'

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<Menu[]>([])
  const loaded = ref(false)

  // 加载菜单
  const loadMenus = async () => {
    if (loaded.value) return
    try {
      menus.value = await getMenuTree()
      loaded.value = true
    } catch (error) {
      console.error('加载菜单失败:', error)
    }
  }

  // 将后端菜单转换为路由配置
  const menuToRoutes = async (menuList: Menu[]): Promise<any[]> => {
    const routes: any[] = []
    for (const menu of menuList) {
      if (menu.menuType === 2 || menu.menuType === 3) {
        // 菜单或按钮类型才需要路由
        if (menu.routePath && menu.componentPath) {
          routes.push({
            path: menu.routePath.startsWith('/') ? menu.routePath.slice(1) : menu.routePath,
            name: menu.menuCode,
            component: () => import(`@/views/${menu.componentPath}.vue`),
            meta: {
              title: menu.menuName,
              icon: menu.icon,
              id: menu.id
            }
          })
        }
      }
      if (menu.children && menu.children.length > 0) {
        const childRoutes = await menuToRoutes(menu.children)
        routes.push(...childRoutes)
      }
    }
    return routes
  }

  // 获取侧边栏菜单
  const getSidebarMenus = (): Menu[] => {
    return menus.value.filter(m => m.status !== false)
  }

  // 重置
  const reset = () => {
    menus.value = []
    loaded.value = false
  }

  return {
    menus,
    loaded,
    loadMenus,
    menuToRoutes,
    getSidebarMenus,
    reset
  }
})
