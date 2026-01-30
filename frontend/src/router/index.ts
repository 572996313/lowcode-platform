import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { getComponent } from './componentMap'
import { getPublishedPages } from '@/api/page'

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
      {
        path: 'lowcode/PageDesigner',
        name: 'PageDesigner',
        component: () => import('@/views/lowcode/PageDesigner.vue'),
        meta: { title: '页面设计器' }
      },
      {
        path: 'lowcode/form/designer',
        name: 'FormDesigner',
        component: () => import('@/views/lowcode/FormDesigner.vue'),
        meta: { title: '表单设计器' }
      },
      {
        path: 'lowcode/table/designer',
        name: 'TableDesigner',
        component: () => import('@/views/lowcode/TableDesigner.vue'),
        meta: { title: '表格设计器' }
      },
      {
        path: 'page/preview',
        name: 'PagePreview',
        component: () => import('@/views/lowcode/PageRender.vue'),
        meta: { title: '页面预览' }
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

  const buildRoutes = (menus: any[]): any[] => {
    const result: any[] = []
    for (const menu of menus) {
      if (!menu.status && menu.status !== null) continue

      if (menu.menuType === 2 && menu.routePath) {
        // 菜单类型 - 添加到路由
        // 尝试获取组件
        let component = null
        if (menu.componentPath) {
          component = getComponent(menu.componentPath)
        }

        // 如果找不到组件，使用占位组件
        if (!component) {
          component = getComponent('/views/common/PlaceholderPage.vue')
          console.warn(`菜单 ${menu.menuName} 未配置组件或组件不存在，使用占位页面`)
        }

        // routePath 可能是 /system/menu 或 system/menu
        // Vue Router 的子路由不需要前缀 /
        let routePath = menu.routePath
        if (routePath.startsWith('/')) {
          // 移除开头的 /
          routePath = routePath.substring(1)
        }

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
            isPlaceholder: !menu.componentPath // 标记是否为占位页面
          }
        })
      }

      if (menu.children && menu.children.length > 0) {
        result.push(...buildRoutes(menu.children))
      }
    }
    return result
  }

  const dynamicRoutes = buildRoutes(menus)
  console.log('动态路由:', dynamicRoutes)

  // 将动态路由添加到 Layout 下
  dynamicRoutes.forEach(route => {
    router.addRoute('Layout', route)
  })

  // 加载已发布的低代码页面路由
  try {
    const publishedPages = await getPublishedPages()
    console.log('加载已发布页面数:', publishedPages.length)

    publishedPages.forEach(page => {
      if (page.routePath && page.id) {
        // routePath 可能是 /user/list 或 user/list
        let routePath = page.routePath
        if (routePath.startsWith('/')) {
          // 移除开头的 /
          routePath = routePath.substring(1)
        }

        router.addRoute('Layout', {
          path: routePath,
          name: `Page_${page.id}`,
          component: () => import('@/views/lowcode/PageRender.vue'),
          meta: {
            title: page.pageName,
            pageId: page.id
          }
        })
      }
    })
  } catch (error) {
    console.error('加载已发布页面路由失败:', error)
  }
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
