/**
 * 配置缓存管理器
 * 用于缓存表单、表格、页面等配置数据，减少重复请求
 */

interface CacheItem<T = any> {
  data: T
  expire: number
  timestamp: number
}

class ConfigCacheManager {
  private cache = new Map<string, CacheItem>()
  private defaultTTL = 5 * 60 * 1000 // 5分钟

  /**
   * 设置缓存
   */
  set<T = any>(key: string, data: T, ttl?: number): void {
    const item: CacheItem<T> = {
      data,
      expire: Date.now() + (ttl || this.defaultTTL),
      timestamp: Date.now()
    }
    this.cache.set(key, item)
  }

  /**
   * 获取缓存
   */
  get<T = any>(key: string): T | null {
    const item = this.cache.get(key)
    if (!item) return null

    // 检查是否过期
    if (Date.now() > item.expire) {
      this.cache.delete(key)
      return null
    }

    return item.data as T
  }

  /**
   * 删除缓存
   */
  delete(key: string): void {
    this.cache.delete(key)
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    this.cache.clear()
  }

  /**
   * 批量删除缓存（支持前缀匹配）
   */
  deleteByPrefix(prefix: string): void {
    const keys = Array.from(this.cache.keys())
    keys.forEach(key => {
      if (key.startsWith(prefix)) {
        this.cache.delete(key)
      }
    })
  }

  /**
   * 获取缓存统计信息
   */
  getStats() {
    const now = Date.now()
    let expired = 0
    let active = 0

    this.cache.forEach(item => {
      if (now > item.expire) {
        expired++
      } else {
        active++
      }
    })

    return {
      total: this.cache.size,
      active,
      expired,
      keys: Array.from(this.cache.keys())
    }
  }

  /**
   * 清理过期缓存
   */
  cleanup(): void {
    const now = Date.now()
    const keys = Array.from(this.cache.keys())

    keys.forEach(key => {
      const item = this.cache.get(key)
      if (item && now > item.expire) {
        this.cache.delete(key)
      }
    })
  }
}

// 创建全局单例
export const configCache = new ConfigCacheManager()

// 定期清理过期缓存（每分钟）
if (typeof window !== 'undefined') {
  setInterval(() => {
    configCache.cleanup()
  }, 60 * 1000)
}

/**
 * 缓存键生成器
 */
export const CacheKeys = {
  // 页面配置
  page: (id: number) => `page:${id}`,
  pageList: (params: any) => `page:list:${JSON.stringify(params)}`,

  // 表单配置
  form: (id: number) => `form:${id}`,
  formList: (params: any) => `form:list:${JSON.stringify(params)}`,

  // 表格配置
  table: (id: number) => `table:${id}`,
  tableList: (params: any) => `table:list:${JSON.stringify(params)}`,

  // 按钮配置
  buttonsByPage: (pageId: number) => `buttons:page:${pageId}`,
  buttonsByForm: (formId: number) => `buttons:form:${formId}`,
  buttonsByTable: (tableId: number) => `buttons:table:${tableId}`,

  // 已发布页面
  publishedPages: 'pages:published'
}

/**
 * 带缓存的配置加载器
 */
export const loadConfigWithCache = async <T = any>(
  key: string,
  loader: () => Promise<T>,
  ttl?: number
): Promise<T> => {
  // 先从缓存读取
  const cached = configCache.get<T>(key)
  if (cached !== null) {
    console.debug('[Cache Hit]', key)
    return cached
  }

  // 缓存未命中，加载数据
  console.debug('[Cache Miss]', key)
  const data = await loader()

  // 写入缓存
  configCache.set(key, data, ttl)

  return data
}

/**
 * 清除配置缓存
 */
export const clearConfigCache = (pattern?: string) => {
  if (pattern) {
    configCache.deleteByPrefix(pattern)
  } else {
    configCache.clear()
  }
}
