/**
 * 格式化工具函数
 */

/**
 * 格式化日期
 */
export function formatDate(date: string | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''

  const d = typeof date === 'string' ? new Date(date) : date

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化数字
 */
export function formatNumber(num: number, decimals: number = 2): string {
  if (num === null || num === undefined) return ''
  return num.toFixed(decimals)
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}

/**
 * 格式化金额
 */
export function formatMoney(amount: number): string {
  if (amount === null || amount === undefined) return ''
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount)
}

/**
 * 获取标签类型
 */
export function getTagType(value: any, typeMap?: Record<string, string>): string {
  if (typeMap && typeMap[value]) {
    return typeMap[value]
  }

  // 默认映射
  if (value === 1 || value === '1' || value === true) return 'success'
  if (value === 0 || value === '0' || value === false) return 'info'
  return ''
}

/**
 * 获取标签文本
 */
export function getTagLabel(value: any, labelMap?: Record<string, string>): string {
  if (labelMap && labelMap[value]) {
    return labelMap[value]
  }
  return String(value)
}
