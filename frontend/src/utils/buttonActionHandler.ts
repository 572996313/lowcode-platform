import { ElMessage, ElMessageBox } from 'element-plus'
import { markRaw } from 'vue'
import type { ButtonConfig } from '@/api/button'
import router from '@/router'
import { request } from './request'
import { useDialogManager } from './dialogManager'
import { useDrawerManager } from './drawerManager'
import FormRender from '@/components/render/FormRender.vue'
import TableRender from '@/components/render/TableRender.vue'

/**
 * 按钮动作配置接口
 */
export interface ActionConfig {
  // 通用配置
  componentType?: 'form' | 'table'  // 组件类型(新增)
  configId?: number  // 表单/表格ID(新增,统一使用configId)

  // === dialog/drawer 配置 ===
  title?: string
  width?: string
  size?: 'small' | 'medium' | 'large'

  // 表单模式配置
  mode?: 'add' | 'edit' | 'view' | 'select' | 'detail'  // 新增 select 和 detail
  dataUrl?: string
  submitUrl?: string

  // 表格选择模式配置(新增)
  multiple?: boolean  // 是否多选
  selectField?: string  // 返回字段(如 'id')
  onConfirm?: {
    action: 'callback' | 'api'  // 回调类型
    targetField?: string  // callback: 填充到父表单字段
    url?: string  // api: 回调接口
  }

  successAction?: {
    type: 'close' | 'refresh' | 'redirect'
    message?: string
    redirectUrl?: string
  }

  // === route 配置 ===
  path?: string
  openType?: 'push' | 'replace'
  query?: Record<string, any>

  // === API 配置 ===
  url?: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  params?: {
    fromRow?: boolean
    custom?: Record<string, any>
  }

  // === confirm 配置 ===
  action?: ActionConfig

  // === 向后兼容(已废弃) ===
  formId?: number  // 已废弃,请使用 componentType + configId
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
  private dialogManager = useDialogManager()
  private drawerManager = useDrawerManager()

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
    // 优先检查新格式: componentType + configId
    if (config.componentType && config.configId) {
      return config.componentType === 'table' && config.mode === 'select' ? 'table-select' : 'dialog'
    }
    // 向后兼容: formId 存在则认为是表单弹窗
    if (config.formId) return 'dialog'
    if (config.path) return 'route'
    if (config.url) return 'api'
    return 'unknown'
  }

  /**
   * 打开弹窗(表单/表格)
   */
  private async openDialog(config: ActionConfig): Promise<void> {
    // 确定组件类型和配置ID(向后兼容)
    const componentType = config.componentType || 'form'
    let configId = config.configId
    if (!configId && config.formId) {
      configId = config.formId
      config.componentType = 'form'  // 向后兼容,自动设置
    }

    if (!configId) {
      throw new Error('缺少配置ID: 请指定 configId 或 formId')
    }

    // 表格选择模式
    if (componentType === 'table' && config.mode === 'select') {
      await this.openTableSelectDialog(config, configId)
      return
    }

    // 表单模式(新增/编辑/查看)
    if (componentType === 'form') {
      await this.openFormDialog(config, configId)
      return
    }

    throw new Error(`不支持的组件类型: ${componentType}`)
  }

  /**
   * 打开表单弹窗
   */
  private async openFormDialog(config: ActionConfig, configId: number): Promise<void> {
    // 构建数据URL
    let dataUrl = config.dataUrl
    if (config.mode === 'edit' && config.dataUrl) {
      // 支持模板字符串 {{row.id}}
      dataUrl = this.evaluateTemplate(config.dataUrl)
    }

    // 构建提交URL
    let submitUrl = config.submitUrl || ''
    if (submitUrl) {
      submitUrl = this.evaluateTemplate(submitUrl)
    }

    await this.dialogManager.openDialog({
      id: `dialog_${Date.now()}`,
      title: config.title || '表单',
      width: config.width || '600px',
      component: markRaw(FormRender),
      componentProps: {
        configId,
        mode: config.mode || 'add'
      },
      mode: config.mode || 'add',
      dataUrl,
      submitUrl,
      onSuccess: () => {
        if (config.successAction?.type === 'close') {
          // 默认行为
        } else if (config.successAction?.type === 'refresh' && this.refreshCallback) {
          this.refreshCallback()
        } else if (config.successAction?.type === 'redirect' && config.successAction.redirectUrl) {
          router.push(config.successAction.redirectUrl)
        } else {
          // 默认刷新
          this.refreshCallback?.()
        }

        if (config.successAction?.message) {
          ElMessage.success(config.successAction.message)
        }
      }
    })
  }

  /**
   * 打开表格选择弹窗
   */
  private async openTableSelectDialog(config: ActionConfig, configId: number): Promise<void> {
    await this.dialogManager.openDialog({
      id: `dialog_select_${Date.now()}`,
      title: config.title || '选择数据',
      width: config.width || '800px',
      component: markRaw(TableRender),
      componentProps: {
        configId,
        mode: 'select',
        multiple: config.multiple || false,
        autoLoad: true
      },
      mode: 'select',
      showFooter: false,  // TableRender 自己处理底部操作栏
      onConfirm: (selectedData: any) => {
        this.handleSelectionConfirm(config, selectedData)
      }
    })
  }

  /**
   * 处理选择确认
   */
  private async handleSelectionConfirm(config: ActionConfig, selectedData: any): Promise<void> {
    if (!selectedData || (Array.isArray(selectedData) && selectedData.length === 0)) {
      ElMessage.warning('请选择数据')
      return
    }

    // 单选或多选数据处理
    const selection = Array.isArray(selectedData) ? selectedData : [selectedData]
    const selectField = config.selectField || 'id'

    // 提取返回值
    const values = selection.map(item => item[selectField])
    const result = config.multiple ? values : values[0]

    // 处理回调
    if (config.onConfirm) {
      if (config.onConfirm.action === 'callback' && config.onConfirm.targetField) {
        // 回填到父表单字段
        if (this.context?.form) {
          this.context.form[config.onConfirm.targetField] = result
          ElMessage.success('已选择数据')
        } else {
          console.warn('父表单上下文不存在,无法回填数据')
        }
      } else if (config.onConfirm.action === 'api' && config.onConfirm.url) {
        // 调用API
        try {
          await request.request({
            url: config.onConfirm.url,
            method: 'POST',
            data: config.multiple ? selection : selection[0]
          })
          ElMessage.success('操作成功')
          this.refreshCallback?.()
        } catch (error: any) {
          ElMessage.error(error.message || '操作失败')
        }
      }
    }

    // 关闭弹窗
    this.dialogManager.closeDialog()
  }

  /**
   * 打开抽屉(表单/表格)
   */
  private async openDrawer(config: ActionConfig): Promise<void> {
    // 确定组件类型和配置ID(向后兼容)
    const componentType = config.componentType || 'form'
    let configId = config.configId
    if (!configId && config.formId) {
      configId = config.formId
      config.componentType = 'form'
    }

    if (!configId) {
      throw new Error('缺少配置ID: 请指定 configId 或 formId')
    }

    // 表格选择模式
    if (componentType === 'table' && config.mode === 'select') {
      await this.openTableSelectDrawer(config, configId)
      return
    }

    // 表单模式
    if (componentType === 'form') {
      await this.openFormDrawer(config, configId)
      return
    }

    throw new Error(`不支持的组件类型: ${componentType}`)
  }

  /**
   * 打开表单抽屉
   */
  private async openFormDrawer(config: ActionConfig, configId: number): Promise<void> {
    // 构建数据URL
    let dataUrl = config.dataUrl
    if (config.mode === 'edit' && config.dataUrl) {
      dataUrl = this.evaluateTemplate(config.dataUrl)
    }

    // 构建提交URL
    let submitUrl = config.submitUrl || ''
    if (submitUrl) {
      submitUrl = this.evaluateTemplate(submitUrl)
    }

    await this.drawerManager.openDrawer({
      id: `drawer_${Date.now()}`,
      title: config.title || '详情',
      size: config.size || 'default',
      component: markRaw(FormRender),
      componentProps: {
        configId,
        mode: config.mode || 'add'
      },
      mode: config.mode || 'add',
      dataUrl,
      submitUrl,
      onSuccess: () => {
        if (config.successAction?.type === 'close') {
          // 默认行为
        } else if (config.successAction?.type === 'refresh' && this.refreshCallback) {
          this.refreshCallback()
        } else if (config.successAction?.type === 'redirect' && config.successAction.redirectUrl) {
          router.push(config.successAction.redirectUrl)
        } else {
          // 默认刷新
          this.refreshCallback?.()
        }

        if (config.successAction?.message) {
          ElMessage.success(config.successAction.message)
        }
      }
    })
  }

  /**
   * 打开表格选择抽屉
   */
  private async openTableSelectDrawer(config: ActionConfig, configId: number): Promise<void> {
    await this.drawerManager.openDrawer({
      id: `drawer_select_${Date.now()}`,
      title: config.title || '选择数据',
      size: config.size || 'large',
      component: markRaw(TableRender),
      componentProps: {
        configId,
        mode: 'select',
        multiple: config.multiple || false,
        autoLoad: true
      },
      mode: 'select',
      showFooter: false,
      onConfirm: (selectedData: any) => {
        this.handleSelectionConfirm(config, selectedData)
      }
    })
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
