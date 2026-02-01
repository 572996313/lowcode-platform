// 组件路径映射，用于动态路由加载
// key 是数据库中 componentPath 的值
export const componentMap: Record<string, () => Promise<any>> = {
  // 系统管理
  '/views/system/MenuManage.vue': () => import('@/views/system/MenuManage.vue'),
  // 低代码配置（V6 版本）
  '/views/lowcode-v6/PageManageV6.vue': () => import('@/views/lowcode-v6/PageManageV6.vue'),
  '/views/lowcode-v6/PageDesignerV6.vue': () => import('@/views/lowcode-v6/PageDesignerV6.vue'),
  '/views/lowcode-v6/PageRenderV6.vue': () => import('@/views/lowcode-v6/PageRenderV6.vue'),
  // 兼容旧路径（指向 V6）
  '/views/lowcode/PageManage.vue': () => import('@/views/lowcode-v6/PageManageV6.vue'),
  '/views/lowcode/PageDesigner.vue': () => import('@/views/lowcode-v6/PageDesignerV6.vue'),
  '/views/lowcode/PageRender.vue': () => import('@/views/lowcode-v6/PageRenderV6.vue'),
  // 保留的其他设计器
  '/views/lowcode/FormDesigner.vue': () => import('@/views/lowcode/FormDesigner.vue'),
  '/views/lowcode/TableDesigner.vue': () => import('@/views/lowcode/TableDesigner.vue'),
  '/views/lowcode/FormList.vue': () => import('@/views/lowcode/FormList.vue'),
  '/views/lowcode/TableList.vue': () => import('@/views/lowcode/TableList.vue'),
  '/views/lowcode/ButtonLibrary.vue': () => import('@/views/lowcode/ButtonLibrary.vue'),
  'views/lowcode/ButtonLibrary.vue': () => import('@/views/lowcode/ButtonLibrary.vue'),
  '/views/lowcode/TemplateManage.vue': () => import('@/views/lowcode/TemplateManage.vue'),
  '/views/lowcode/TemplateTest.vue': () => import('@/views/lowcode/TemplateTest.vue'),
  '/views/lowcode/DropdownManage.vue': () => import('@/views/lowcode/DropdownManage.vue'),
  // 其他
  '/views/home/index.vue': () => import('@/views/home/index.vue'),
  '/views/common/PlaceholderPage.vue': () => import('@/views/common/PlaceholderPage.vue'),
}

// 获取组件
export const getComponent = (componentPath: string) => {
  return componentMap[componentPath] || null
}

// 辅助函数：从 componentPath 提取路由路径
// /views/lowcode/PageManage.vue -> lowcode/PageManage
export const extractRoutePath = (fullPath: string): string => {
  // 移除 /views/ 前缀
  let path = fullPath.replace(/^\/views\//, '')
  // 移除 .vue 后缀
  path = path.replace(/\.vue$/, '')
  return path
}
