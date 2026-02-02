import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { getComponent } from './componentMap'

const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 页面预览（V6 运行时 - 不显示在菜单中）
      {
        path: 'page/preview/:id',
        name: 'PagePreview',
        component: () => import('@/views/lowcode-v6/PageRenderV6.vue'),
        meta: { title: '页面预览', hideInMenu: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

// 动态添加路由
export const addDynamicRoutes = async () => {
  const menuStore = useMenuStore()
  await menuStore.loadMenus()

  const menus = menuStore.getSidebarMenus()
  console.log('加载菜单数:', menus.length)

  // 用于去重的路由路径集合
  const registeredRoutes = new Set<string>()

  const buildRoutes = (menus: any[]): any[] => {
    const result: any[] = []
    for (const menu of menus) {
      if (!menu.status && menu.status !== null) continue

      if (menu.menuType === 2 && menu.routePath) {
        // 菜单类型 - 添加到路由
        console.log(`处理菜单: ${menu.menuName}`, JSON.stringify(menu, null, 2))

        // 尝试获取组件
        let component = null
        if (menu.componentPath) {
          component = getComponent(menu.componentPath)
        }

        // 如果找不到组件，但有 pageId，则使用 PageRenderV6 渲染低代码页面
        if (!component && menu.pageId) {
          component = getComponent('/views/lowcode-v6/PageRenderV6.vue')
          console.log(`✓ 菜单 ${menu.menuName} 使用 PageRenderV6 渲染页面 pageId=${menu.pageId}`)
        }

        // 如果找不到组件也没有 pageId，使用占位组件
        if (!component) {
          component = getComponent('/views/common/PlaceholderPage.vue')
          console.warn(`✗ 菜单 ${menu.menuName} 使用占位页面`)
          console.warn(`  - componentPath: ${menu.componentPath}`)
          console.warn(`  - pageId: ${menu.pageId}`)
        }

        // routePath 可能是 /system/menu 或 system/menu
        // Vue Router 的子路由不需要前缀 /
        let routePath = menu.routePath
        if (routePath.startsWith('/')) {
          // 移除开头的 /
          routePath = routePath.substring(1)
        }

        // 检查路由是否已注册
        if (registeredRoutes.has(routePath)) {
          console.warn(`路由 ${routePath} 已注册，跳过菜单路由: ${menu.menuName}`)
        } else {
          registeredRoutes.add(routePath)
          result.push({
            path: routePath,
            name: menu.menuCode,
            component: component,
            meta: {
              title: menu.menuName,
              icon: menu.icon,
              // 传递菜单信息给占位组件
              menuName: menu.menuName,
              menuCode: menu.menuCode,
              pageId: menu.pageId,
              isPlaceholder: !menu.componentPath
            }
          })
        }
      }

      if (menu.children && menu.children.length > 0) {
        result.push(...buildRoutes(menu.children))
      }
    }
    return result
  }

  const dynamicRoutes = buildRoutes(menus)
  console.log('动态路由:', dynamicRoutes)

  // 将动态路由添加到 Layout 下（菜单路由优先）
  dynamicRoutes.forEach(route => {
    router.addRoute('Layout', route)
  })
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 登录页面直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 检查 token
  const hasToken = localStorage.getItem('token')

  if (!hasToken) {
    next('/login')
    return
  }

  // 如果还没有加载动态路由，则加载
  const menuStore = useMenuStore()
  if (!menuStore.loaded) {
    try {
      await addDynamicRoutes()
      menuStore.loaded = true
      // 重新跳转到目标路由
      next({ ...to, replace: true })
      return
    } catch (error) {
      console.error('加载动态路由失败:', error)
    }
  }

  next()
})

export default router
