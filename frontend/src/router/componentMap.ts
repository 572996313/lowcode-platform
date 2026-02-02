/**
 * 组件路径映射，用于动态路由加载
 * ⚠️ 此文件由 scripts/generate-component-map.ts 自动生成，请勿手动修改！
 * 如需更新映射，请运行: npm run gen:component-map
 */
export const componentMap: Record<string, () => Promise<any>> = {
  '/views/common/PlaceholderPage.vue': () => import('@/views/common/PlaceholderPage.vue'),
  '/views/home/index.vue': () => import('@/views/home/index.vue'),
  '/views/layout/index.vue': () => import('@/views/layout/index.vue'),
  '/views/login/index.vue': () => import('@/views/login/index.vue'),
  '/views/lowcode-v6/PageDesignerV6.vue': () => import('@/views/lowcode-v6/PageDesignerV6.vue'),
  '/views/lowcode-v6/PageManageV6.vue': () => import('@/views/lowcode-v6/PageManageV6.vue'),
  '/views/lowcode-v6/PageRenderV6.vue': () => import('@/views/lowcode-v6/PageRenderV6.vue'),
  '/views/lowcode/ButtonLibrary.vue': () => import('@/views/lowcode/ButtonLibrary.vue'),
  '/views/lowcode/DbTableList.vue': () => import('@/views/lowcode/DbTableList.vue'),
  '/views/lowcode/DropdownManage.vue': () => import('@/views/lowcode/DropdownManage.vue'),
  '/views/lowcode/FieldWidgetBinding.vue': () => import('@/views/lowcode/FieldWidgetBinding.vue'),
  '/views/lowcode/FormDesigner.vue': () => import('@/views/lowcode/FormDesigner.vue'),
  '/views/lowcode/FormList.vue': () => import('@/views/lowcode/FormList.vue'),
  '/views/lowcode/FormManage.vue': () => import('@/views/lowcode/FormManage.vue'),
  '/views/lowcode/FormFieldBinding.vue': () => import('@/views/lowcode/FormFieldBinding.vue'),
  '/views/lowcode/FormStyleTemplateManage.vue': () => import('@/views/lowcode/FormStyleTemplateManage.vue'),
  '/views/lowcode/FormTemplateManage.vue': () => import('@/views/lowcode/FormManage.vue'),
  '/views/lowcode/PageLayoutDesigner.vue': () => import('@/views/lowcode/PageLayoutDesigner.vue'),
  '/views/lowcode/PageLayoutManage.vue': () => import('@/views/lowcode/PageLayoutManage.vue'),
  '/views/lowcode/TableDesigner.vue': () => import('@/views/lowcode/TableDesigner.vue'),
  '/views/lowcode/TableList.vue': () => import('@/views/lowcode/TableList.vue'),
  '/views/lowcode/TableManage.vue': () => import('@/views/lowcode/TableManage.vue'),
  '/views/lowcode/TableColumnBinding.vue': () => import('@/views/lowcode/TableColumnBinding.vue'),
  '/views/lowcode/TableStyleTemplateManage.vue': () => import('@/views/lowcode/TableStyleTemplateManage.vue'),
  '/views/lowcode/TemplateManage.vue': () => import('@/views/lowcode/TemplateManage.vue'),
  '/views/lowcode/TemplateTest.vue': () => import('@/views/lowcode/TemplateTest.vue'),
  '/views/system/DictManage.vue': () => import('@/views/system/DictManage.vue'),
  '/views/system/MenuManage.vue': () => import('@/views/system/MenuManage.vue'),
}

// 获取组件
export const getComponent = (componentPath: string) => {
  // 支持带斜杠和不带斜杠的路径
  const normalizedPath = componentPath.startsWith('/') ? componentPath : `/${componentPath}`
  return componentMap[normalizedPath] || null
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
