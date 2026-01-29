import { debounce } from 'lodash-es'

/**
 * 数据联动工具
 * 处理树节点点击、查询条件变化、字段联动等场景
 */

/**
 * 防抖函数
 * @param fn 要防抖的函数
 * @param delay 延迟时间（毫秒）
 */
export const useDebounce = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): {
  fn: T
  cancel: () => void
} => {
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  const debouncedFn = ((...args: Parameters) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = setTimeout(() => {
      fn(...args)
    }, delay)
  }) as T

  const cancel = () => {
    if (timeoutId) {
      clearTimeout(timeoutId)
      timeoutId = null
    }
  }

  return { fn: debouncedFn, cancel }
}

/**
 * 节流函数
 * @param fn 要节流的函数
 * @param delay 延迟时间（毫秒）
 */
export const useThrottle = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): {
  fn: T
  cancel: () => void
} => {
  let timeoutId: ReturnType<typeof setTimeout> | null = null
  let lastTime = 0

  const throttledFn = ((...args: Parameters) => {
    const now = Date.now()

    if (now - lastTime >= delay) {
      fn(...args)
      lastTime = now
    } else {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
      timeoutId = setTimeout(() => {
        fn(...args)
        lastTime = Date.now()
      }, delay - (now - lastTime))
    }
  }) as T

  const cancel = () => {
    if (timeoutId) {
      clearTimeout(timeoutId)
      timeoutId = null
    }
  }

  return { fn: throttledFn, cancel }
}

/**
 * 字段联动配置
 */
export interface FieldLinkageRule {
  triggerField: string        // 触发字段
  condition: string | Function  // 触发条件
  targetField: string         // 目标字段
  action: LinkageAction      // 联动动作
  value?: any                 // 设置值（setValue 时使用）
  url?: string               // 动态选项 URL（setOptions 时使用）
}

export type LinkageAction =
  | 'setValue'        // 设置值
  | 'setVisible'       // 设置可见性
  | 'setDisabled'      // 设置禁用状态
  | 'setOptions'       // 设置选项
  | 'setRequired'      // 设置必填
  | 'clearValue'       // 清空值

/**
 * 字段联动处理器
 */
export class FieldLinkageHandler {
  private rules: FieldLinkageRule[] = []
  private formData: Record<string, any> = {}
  private onFieldChange?: (fieldCode: string, value: any) => void

  constructor(rules: FieldLinkageRule[], formData: Record<string, any>) {
    this.rules = rules
    this.formData = formData
  }

  /**
   * 设置表单数据引用
   */
  setFormData(formData: Record<string, any>) {
    this.formData = formData
  }

  /**
   * 设置字段变化回调
   */
  setOnChange(callback: (fieldCode: string, value: any) => void) {
    this.onFieldChange = callback
  }

  /**
   * 触发联动
   */
  trigger(fieldCode: string, value: any) {
    this.rules.forEach(rule => {
      if (rule.triggerField !== fieldCode) return

      const shouldTrigger = this.evaluateCondition(rule.condition, value)

      if (shouldTrigger) {
        this.executeAction(rule)
      }
    })
  }

  /**
   * 评估条件
   */
  private evaluateCondition(condition: string | Function, value: any): boolean {
    if (typeof condition === 'function') {
      try {
        return condition(value)
      } catch {
        return false
      }
    }

    if (typeof condition === 'string') {
      try {
        const func = new Function('value', 'return ' + condition)
        return func(value)
      } catch {
        return false
      }
    }

    return false
  }

  /**
   * 执行联动动作
   */
  private executeAction(rule: FieldLinkageRule) {
    switch (rule.action) {
      case 'setValue':
        if (rule.value !== undefined) {
          this.formData[rule.targetField] = rule.value
          this.onFieldChange?.(rule.targetField, rule.value)
        }
        break

      case 'clearValue':
        delete this.formData[rule.targetField]
        this.onFieldChange?.(rule.targetField, undefined)
        break

      case 'setVisible':
        // TODO: 实现字段显示/隐藏逻辑
        // 可以通过修改字段的 visible 属性实现
        console.log('设置字段可见性:', rule.targetField)
        break

      case 'setDisabled':
        // TODO: 实现字段禁用/启用逻辑
        // 可以通过修改字段的 disabled 属性实现
        console.log('设置字段禁用状态:', rule.targetField)
        break

      case 'setRequired':
        // TODO: 实现字段必填逻辑
        // 可以通过修改字段的 required 属性实现
        console.log('设置字段必填:', rule.targetField)
        break

      case 'setOptions':
        // TODO: 动态加载选项
        console.log('动态加载字段选项:', rule.targetField, rule.url)
        break
    }
  }
}

/**
 * 树节点点击联动处理器
 */
export class TreeClickHandler {
  private onDataChange?: (params: any) => void

  constructor(onChange?: (params: any) => void) {
    this.onDataChange = onChange
  }

  /**
   * 处理树节点点击
   */
  handleNodeClick(data: any, node: any) {
    const params = {
      nodeId: data.id,
      nodeName: data.label || data.name,
      ...data
    }

    this.onDataChange?.(params)
  }
}

/**
 * 查询条件变化处理器
 */
export class SearchFormHandler {
  private formData: Record<string, any> = {}
  private onSearch: () => void
  private debouncedSearch: () => void

  constructor(
    formData: Record<string, any>,
    onSearch: () => void,
    debounceDelay: number = 500
  ) {
    this.formData = formData
    this.onSearch = onSearch

    const { fn } = useDebounce(onSearch, debounceDelay)
    this.debouncedSearch = fn
  }

  /**
   * 监听字段变化
   */
  watchField(fieldCode: string, value: any) {
    this.formData[fieldCode] = value

    // 触发防抖搜索
    this.debouncedSearch()
  }

  /**
   * 手动触发搜索
   */
  triggerSearch() {
    this.onSearch()
  }

  /**
   * 重置表单
   */
  reset() {
    Object.keys(this.formData).forEach(key => {
      delete this.formData[key]
    })
  }

  /**
   * 获取查询参数
   */
  getParams(): Record<string, any> {
    return { ...this.formData }
  }
}

/**
 * 表格操作按钮处理器
 */
export class TableActionHandler {
  private selection: any[] = []
  private currentRow: any | null = null
  private onAction?: (action: string, params: any) => void

  constructor(onAction?: (action: string, params: any) => void) {
    this.onAction = onAction
  }

  /**
   * 处理选择变化
   */
  handleSelectionChange(selection: any[]) {
    this.selection = selection
  }

  /**
   * 处理行操作
   */
  handleRowAction(action: string, row: any) {
    this.currentRow = row
    this.onAction?.(action, {
      row,
      selection: this.selection
    })
  }

  /**
   * 处理工具栏操作
   */
  handleToolbarAction(action: string) {
    this.onAction?.(action, {
      selection: this.selection
    })
  }

  /**
   * 检查是否可以执行操作
   */
  canExecute(action: string, rule?: string): boolean {
    if (!rule) return true

    try {
      const func = new Function('row', 'selection', 'return ' + rule)
      return func(this.currentRow, this.selection)
    } catch {
      return true
    }
  }

  /**
   * 获取当前选中行
   */
  getCurrentRow(): any | null {
    return this.currentRow
  }

  /**
   * 获取选中项
   */
  getSelection(): any[] {
    return this.selection
  }
}
