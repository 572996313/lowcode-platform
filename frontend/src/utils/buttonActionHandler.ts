import { ElMessage, ElMessageBox } from 'element-plus'
import type { ButtonConfig } from '@/api/button'
import router from '@/router'
import { request } from './request'

/**
 * 按钮动作配置接口
 */
export interface ActionConfig {
  // dialog 配置
  formId?: number
  title?: string
  width?: string
  mode?: 'add' | 'edit'
  dataUrl?: string
  submitUrl?: string
  successAction?: {
    type: 'close' | 'refresh' | 'redirect'
    message?: string
    redirectUrl?: string
  }

  // drawer 配置
  size?: 'small' | 'medium' | 'large'

  // route 配置
  path?: string
  openType?: 'push' | 'replace'
  query?: Record<string, any>

  // API 配置
  url?: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  params?: {
    fromRow?: boolean
    custom?: Record<string, any>
  }

  // confirm 配置
  action?: ActionConfig
}

/**
 * 确认框配置接口
 */
export interface ConfirmConfig {
  title?: string
  message?: string
  type?: 'warning' | 'info' | 'success' | 'error'
  confirmButtonText?: string
  cancelButtonText?: string
}

/**
 * 按钮动作处理器
 */
export class ButtonActionHandler {
  private context: any
  private refreshCallback?: () => void

  constructor(context?: any, refreshCallback?: () => void) {
    this.context = context || {}
    this.refreshCallback = refreshCallback
  }

  /**
   * 处理按钮点击
   */
  async handle(config: ButtonConfig): Promise<void> {
    try {
      // 如果有确认框配置，先显示确认框
      if (config.actionType === 'confirm' && config.confirmConfig) {
        const confirmConfig: ConfirmConfig = JSON.parse(config.confirmConfig)
        await this.showConfirm(confirmConfig)

        // 确认后执行实际动作
        if (config.actionConfig) {
          const actionConfig: ActionConfig = JSON.parse(config.actionConfig)
          await this.executeAction(actionConfig.action || actionConfig)
        }
      } else if (config.actionConfig) {
        // 直接执行动作
        const actionConfig: ActionConfig = JSON.parse(config.actionConfig)
        await this.executeAction(actionConfig)
      } else if (config.actionType === 'custom') {
        // 自定义代码执行
        await this.executeCustomCode(config.actionConfig || '')
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('按钮动作执行失败:', error)
        ElMessage.error(error.message || '操作失败')
      }
    }
  }

  /**
   * 显示确认框
   */
  private async showConfirm(config: ConfirmConfig): Promise<void> {
    return ElMessageBox.confirm(
      config.message || '确定要执行此操作吗？',
      config.title || '提示',
      {
        type: config.type || 'warning',
        confirmButtonText: config.confirmButtonText || '确定',
        cancelButtonText: config.cancelButtonText || '取消'
      }
    )
  }

  /**
   * 执行动作
   */
  private async executeAction(config: ActionConfig): Promise<void> {
    const actionType = this.inferActionType(config)

    switch (actionType) {
      case 'dialog':
        await this.openDialog(config)
        break
      case 'drawer':
        await this.openDrawer(config)
        break
      case 'route':
        await this.navigateToRoute(config)
        break
      case 'api':
        await this.callApi(config)
        break
      case 'link':
        this.openLink(config)
        break
      default:
        console.warn('未知的动作类型:', actionType)
    }
  }

  /**
   * 推断动作类型
   */
  private inferActionType(config: ActionConfig): string {
    if (config.formId) return 'dialog'
    if (config.path) return 'route'
    if (config.url) return 'api'
    return 'unknown'
  }

  /**
   * 打开表单弹窗
   */
  private async openDialog(config: ActionConfig): Promise<void> {
    ElMessage.info('打开弹窗功能待实现')
    // TODO: 实现弹窗打开逻辑
    // 可以通过事件总线或全局状态管理触发弹窗
  }

  /**
   * 打开抽屉
   */
  private async openDrawer(config: ActionConfig): Promise<void> {
    ElMessage.info('打开抽屉功能待实现')
    // TODO: 实现抽屉打开逻辑
  }

  /**
   * 路由跳转
   */
  private async navigateToRoute(config: ActionConfig): Promise<void> {
    if (!config.path) {
      throw new Error('路由路径不能为空')
    }

    const query = this.buildParams(config.query)

    if (config.openType === 'replace') {
      router.replace({ path: config.path, query })
    } else {
      router.push({ path: config.path, query })
    }
  }

  /**
   * API 请求
   */
  private async callApi(config: ActionConfig): Promise<void> {
    if (!config.url || !config.method) {
      throw new Error('API请求配置不完整')
    }

    const params = this.buildParams(config.params)

    try {
      const response = await request.request({
        url: config.url,
        method: config.method,
        params: config.method === 'GET' ? params : undefined,
        data: config.method !== 'GET' ? params : undefined
      })

      // 处理成功回调
      if (config.successAction) {
        if (config.successAction.message) {
          ElMessage.success(config.successAction.message)
        }

        if (config.successAction.type === 'refresh' && this.refreshCallback) {
          this.refreshCallback()
        } else if (config.successAction.type === 'redirect' && config.successAction.redirectUrl) {
          router.push(config.successAction.redirectUrl)
        }
      } else {
        ElMessage.success('操作成功')
        if (this.refreshCallback) {
          this.refreshCallback()
        }
      }
    } catch (error: any) {
      throw new Error(error.message || '请求失败')
    }
  }

  /**
   * 打开链接
   */
  private openLink(config: ActionConfig): Promise<void> {
    const url = config.path || config.url
    if (!url) {
      throw new Error('链接地址不能为空')
    }

    const query = this.buildParams(config.query)
    const queryString = new URLSearchParams(query).toString()
    const fullUrl = queryString ? `${url}?${queryString}` : url

    window.open(fullUrl, '_blank')
    return Promise.resolve()
  }

  /**
   * 执行自定义代码
   */
  private async executeCustomCode(code: string): Promise<void> {
    try {
      const context = {
        row: this.context.row,
        form: this.context.form,
        selection: this.context.selection,
        router,
        ElMessage,
        ElMessageBox
      }

      const func = new Function(...Object.keys(context), code)
      await func(...Object.values(context))
    } catch (error: any) {
      throw new Error(`自定义代码执行失败: ${error.message}`)
    }
  }

  /**
   * 构建参数
   */
  private buildParams(config?: { fromRow?: boolean; custom?: Record<string, any> }): Record<string, any> {
    const params: Record<string, any> = {}

    if (!config) return params

    // 从行数据获取参数
    if (config.fromRow && this.context.row) {
      Object.assign(params, this.context.row)
    }

    // 自定义参数
    if (config.custom) {
      Object.assign(params, config.custom)
    }

    // 支持模板字符串 {{row.id}}
    const result: Record<string, any> = {}
    for (const [key, value] of Object.entries(params)) {
      if (typeof value === 'string' && value.includes('{{')) {
        result[key] = this.evaluateTemplate(value)
      } else {
        result[key] = value
      }
    }

    return result
  }

  /**
   * 求值模板字符串
   */
  private evaluateTemplate(template: string): string {
    return template.replace(/\{\{(.*?)\}\}/g, (_, expr) => {
      try {
        const context = {
          row: this.context.row,
          form: this.context.form,
          selection: this.context.selection
        }
        const func = new Function(...Object.keys(context), `return ${expr}`)
        return func(...Object.values(context))
      } catch (e) {
        console.warn('模板求值失败:', expr, e)
        return template
      }
    })
  }
}

/**
 * 创建按钮动作处理器实例
 */
export const createButtonActionHandler = (context?: any, refreshCallback?: () => void) => {
  return new ButtonActionHandler(context, refreshCallback)
}
