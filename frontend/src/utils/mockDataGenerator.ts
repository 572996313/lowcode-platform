import type { TableColumnConfig } from '@/api/table-standard'

/**
 * 根据表格列配置自动生成预览数据
 * @param columns 列配置
 * @param count 生成行数
 */
export function generateMockData(
  columns: TableColumnConfig[],
  count: number = 8
): Record<string, any>[] {
  const rows: Record<string, any>[] = []

  for (let i = 0; i < count; i++) {
    const row: Record<string, any> = { id: i + 1 }

    for (const col of columns) {
      // 特殊列类型不生成数据
      if (col.type === 'index' || col.type === 'selection' || col.type === 'action') continue
      if (!col.prop) continue

      row[col.prop] = generateFieldValue(col, i)
    }

    rows.push(row)
  }

  return rows
}

function generateFieldValue(col: TableColumnConfig, index: number): any {
  const prop = col.prop || ''

  // tag 类型：从 mapping 中取值
  if (col.type === 'tag' && col.tagConfig?.mapping) {
    const keys = Object.keys(col.tagConfig.mapping)
    return keys[index % keys.length]
  }

  // date 类型
  if (col.type === 'date' || prop.toLowerCase().includes('time') || prop.toLowerCase().includes('date')) {
    const d = new Date(2026, 0, 15 + index, 10 + index, 30, 0)
    return d.toISOString().replace('T', ' ').substring(0, 19)
  }

  // 根据 prop 名推断数据
  const lowerProp = prop.toLowerCase()

  if (lowerProp.includes('name') || lowerProp.includes('名称')) {
    return `示例${index + 1}`
  }
  if (lowerProp.includes('code') || lowerProp.includes('编码')) {
    return `CODE_${String(index + 1).padStart(3, '0')}`
  }
  if (lowerProp.includes('status') || lowerProp.includes('状态')) {
    return index % 2 === 0 ? 1 : 0
  }
  if (lowerProp.includes('category') || lowerProp.includes('分类')) {
    const cats = ['分类A', '分类B', '分类C']
    return cats[index % cats.length]
  }
  if (lowerProp.includes('desc') || lowerProp.includes('描述') || lowerProp.includes('remark') || lowerProp.includes('备注')) {
    return `这是第${index + 1}条数据的${col.label || '描述'}信息`
  }
  if (lowerProp.includes('amount') || lowerProp.includes('金额') || lowerProp.includes('price') || lowerProp.includes('价格')) {
    return Math.round((100 + index * 123.45) * 100) / 100
  }
  if (lowerProp.includes('type') || lowerProp.includes('类型')) {
    const types = ['类型A', '类型B', '类型C']
    return types[index % types.length]
  }

  // 默认
  return `${col.label || prop}_${index + 1}`
}
