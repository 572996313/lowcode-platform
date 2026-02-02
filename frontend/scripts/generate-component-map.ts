/**
 * 组件路径映射生成脚本
 * 扫描 src/views 目录下的所有 .vue 文件，自动生成 componentMap.ts
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const VIEWS_DIR = path.join(__dirname, '../src/views')
const OUTPUT_FILE = path.join(__dirname, '../src/router/componentMap.ts')

/**
 * 递归扫描目录，获取所有 .vue 文件
 */
function scanVueFiles(dir: string, baseDir: string = dir): string[] {
  const files: string[] = []

  if (!fs.existsSync(dir)) {
    return files
  }

  const entries = fs.readdirSync(dir, { withFileTypes: true })

  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name)

    if (entry.isDirectory()) {
      // 跳过 node_modules 等目录
      if (entry.name === 'node_modules' || entry.name.startsWith('.')) {
        continue
      }
      files.push(...scanVueFiles(fullPath, baseDir))
    } else if (entry.isFile() && entry.name.endsWith('.vue')) {
      // 获取相对于 views 目录的路径
      const relativePath = path.relative(baseDir, fullPath)
      // 转换为 Unix 风格路径
      const unixPath = relativePath.replace(/\\/g, '/')
      // 添加 /views/ 前缀
      files.push(`/views/${unixPath}`)
    }
  }

  return files
}

/**
 * 生成 componentMap.ts 文件内容
 */
function generateComponentMap(vueFiles: string[]): string {
  const sortedFiles = vueFiles.sort()

  const imports = sortedFiles.map(file => {
    const relativePath = file.replace(/^\/views\//, '')
    return `  '${file}': () => import('@/views/${relativePath}'),`
  }).join('\n')

  return `/**
 * 组件路径映射，用于动态路由加载
 * ⚠️ 此文件由 scripts/generate-component-map.ts 自动生成，请勿手动修改！
 * 如需更新映射，请运行: npm run gen:component-map
 */
export const componentMap: Record<string, () => Promise<any>> = {
${imports}
}

// 获取组件
export const getComponent = (componentPath: string) => {
  // 支持带斜杠和不带斜杠的路径
  const normalizedPath = componentPath.startsWith('/') ? componentPath : \`/\${componentPath}\`
  return componentMap[normalizedPath] || null
}

// 辅助函数：从 componentPath 提取路由路径
// /views/lowcode/PageManage.vue -> lowcode/PageManage
export const extractRoutePath = (fullPath: string): string => {
  // 移除 /views/ 前缀
  let path = fullPath.replace(/^\\/views\\//, '')
  // 移除 .vue 后缀
  path = path.replace(/\\.vue$/, '')
  return path
}
`
}

/**
 * 主函数
 */
function main() {
  console.log('🔍 扫描 views 目录...')

  const vueFiles = scanVueFiles(VIEWS_DIR, VIEWS_DIR)

  if (vueFiles.length === 0) {
    console.log('⚠️  未找到任何 .vue 文件')
    return
  }

  console.log(`✅ 找到 ${vueFiles.length} 个 Vue 组件:`)
  vueFiles.forEach(file => console.log(`   - ${file}`))

  console.log('\n📝 生成 componentMap.ts...')

  const content = generateComponentMap(vueFiles)
  fs.writeFileSync(OUTPUT_FILE, content, 'utf-8')

  console.log(`✅ 已生成: ${OUTPUT_FILE}`)
  console.log('\n📌 提示: 数据库菜单表 sys_menu 中的 component_path 字段值必须与上述路径一致')
}

main()