import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
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
      // 系统管理
      {
        path: 'system/menu',
        name: 'MenuManage',
        component: () => import('@/views/system/MenuManage.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      // 低代码配置
      {
        path: 'lowcode/page',
        name: 'PageManage',
        component: () => import('@/views/lowcode/PageManage.vue'),
        meta: { title: '页面管理', icon: 'Document' }
      },
      {
        path: 'lowcode/form',
        name: 'FormDesigner',
        component: () => import('@/views/lowcode/FormDesigner.vue'),
        meta: { title: '表单设计', icon: 'EditPen' }
      },
      {
        path: 'lowcode/table',
        name: 'TableDesigner',
        component: () => import('@/views/lowcode/TableDesigner.vue'),
        meta: { title: '表格设计', icon: 'Grid' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
