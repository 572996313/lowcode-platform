// 组件路径映射，用于动态路由加载
// key 是数据库中 componentPath 的值
export const componentMap: Record<string, () => Promise<any>> = {
  '/views/system/MenuManage.vue': () => import('@/views/system/MenuManage.vue'),
  '/views/lowcode/PageManage.vue': () => import('@/views/lowcode/PageManage.vue'),
  '/views/lowcode/FormDesigner.vue': () => import('@/views/lowcode/FormDesigner.vue'),
  '/views/lowcode/TableDesigner.vue': () => import('@/views/lowcode/TableDesigner.vue'),
  '/views/lowcode/FormList.vue': () => import('@/views/lowcode/FormList.vue'),
  '/views/lowcode/TableList.vue': () => import('@/views/lowcode/TableList.vue'),
  '/views/home/index.vue': () => import('@/views/home/index.vue'),
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
