/**
 * 配置注册表 - 纯前端配置仓库
 * 表格/表单配置器保存时注册到这里，运行时按钮动作通过 getConfig 取出目标配置。
 * 将来接后端时，getConfig 改为调 API，对外接口不变。
 */

export interface ConfigEntry {
  code: string
  name: string
  type: 'table' | 'form'
  config: any
}

const registry = new Map<string, ConfigEntry>()

/** 注册一个配置 */
export function registerConfig(code: string, name: string, type: 'table' | 'form', config: any): void {
  registry.set(code, { code, name, type, config })
}

/** 获取配置 */
export function getConfig(code: string): any | undefined {
  return registry.get(code)?.config
}

/** 获取配置条目（含元信息） */
export function getConfigEntry(code: string): ConfigEntry | undefined {
  return registry.get(code)
}

/** 获取所有已注册配置（用于下拉选择） */
export function getAllConfigs(): ConfigEntry[] {
  return Array.from(registry.values())
}

/** 按类型获取配置列表 */
export function getConfigsByType(type: 'table' | 'form'): ConfigEntry[] {
  return Array.from(registry.values()).filter(e => e.type === type)
}

/** 移除配置 */
export function removeConfig(code: string): void {
  registry.delete(code)
}

/** 清空所有配置 */
export function clearConfigs(): void {
  registry.clear()
}
