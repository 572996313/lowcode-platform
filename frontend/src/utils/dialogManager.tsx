import { ref, shallowRef, markRaw, h, type Component } from 'vue'
import { ElMessage } from 'element-plus'
import { request } from './request'

/**
 * 弹窗配置接口
 */
export interface DialogConfig {
  id: string
  title: string
  width?: string
  component?: Component
  componentProps?: Record<string, any>
  mode?: 'add' | 'edit' | 'view' | 'select'  // 新增 select
  dataUrl?: string
  submitUrl?: string
  submitMethod?: 'POST' | 'PUT' | 'PATCH'
  showFooter?: boolean
  confirmText?: string
  cancelText?: string
  onSuccess?: () => void
  onConfirm?: (data?: any) => void  // 新增,用于选择模式
  onClose?: () => void
}

/**
 * 弹窗状态
 */
const dialogVisible = ref(false)
const dialogConfig = shallowRef<DialogConfig | null>(null)
const dialogLoading = ref(false)
const formData = ref<Record<string, any>>({})

/**
 * 弹窗管理器
 */
export const useDialogManager = () => {
  /**
   * 打开弹窗
   */
  const openDialog = async (config: DialogConfig) => {
    dialogConfig.value = config
    dialogVisible.value = true

    // 如果是编辑模式且有数据URL，加载数据
    if (config.mode === 'edit' && config.dataUrl) {
      dialogLoading.value = true
      try {
        const res = await request.get(config.dataUrl)
        formData.value = res
      } catch (error: any) {
        ElMessage.error(error.message || '加载数据失败')
      } finally {
        dialogLoading.value = false
      }
    } else if (config.mode === 'add') {
      // 新增模式，清空表单数据
      formData.value = {}
    } else if (config.mode === 'select') {
      // 选择模式，清空数据
      formData.value = {}
    }
  }

  /**
   * 关闭弹窗
   */
  const closeDialog = () => {
    dialogVisible.value = false
    dialogConfig.value?.onClose?.()
    // 清空数据
    formData.value = {}
  }

  /**
   * 提交表单
   */
  const submitDialog = async (formRef: any) => {
    if (!formRef) return false

    try {
      // 验证表单
      const valid = await formRef.validate()
      if (!valid) return false

      const config = dialogConfig.value!
      dialogLoading.value = true

      await request.request({
        url: config.submitUrl,
        method: (config.submitMethod || 'POST') as any,
        data: formData.value
      })

      const modeText = config.mode === 'add' ? '添加' : '保存'
      ElMessage.success(`${modeText}成功`)

      closeDialog()
      config.onSuccess?.()

      return true
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
      return false
    } finally {
      dialogLoading.value = false
    }
  }

  /**
   * 处理选择确认(用于表格选择模式)
   */
  const handleSelectionConfirm = (selectedData: any) => {
    const config = dialogConfig.value!
    if (config.onConfirm) {
      config.onConfirm(selectedData)
    }
    closeDialog()
  }

  return {
    dialogVisible,
    dialogConfig,
    dialogLoading,
    formData,
    openDialog,
    closeDialog,
    submitDialog,
    handleSelectionConfirm
  }
}

/**
 * 创建全局弹窗容器组件
 */
export const DialogContainer = {
  name: 'DialogContainer',
  setup() {
    const {
      dialogVisible,
      dialogConfig,
      dialogLoading,
      formData,
      closeDialog,
      submitDialog,
      handleSelectionConfirm
    } = useDialogManager()

    const formRef = ref()
    const tableRef = ref()

    const handleConfirm = () => {
      const config = dialogConfig.value
      if (config?.mode === 'select') {
        // 选择模式: 从 Table 获取选中数据
        const selection = tableRef.value?.getSelectionRows?.()
        if (selection && selection.length > 0) {
          const data = config.componentProps?.multiple ? selection : selection[0]
          handleSelectionConfirm(data)
        } else {
          ElMessage.warning('请选择数据')
        }
      } else {
        // 表单模式: 提交表单
        submitDialog(formRef.value)
      }
    }

    return () => {
      if (!dialogConfig.value) return null

      const showFooter = dialogConfig.value.showFooter !== false

      return h('el-dialog', {
        modelValue: dialogVisible.value,
        title: dialogConfig.value.title,
        width: dialogConfig.value.width || '600px',
        onClose: closeDialog,
        destroyOnClose: true,
        closeOnClickModal: false,
        vSlots: showFooter ? {
          footer: () => [
            h('el-button', { onClick: closeDialog }, dialogConfig.value.cancelText || '取消'),
            h('el-button', {
              type: 'primary',
              loading: dialogLoading.value,
              onClick: handleConfirm
            }, dialogConfig.value.confirmText || '确定')
          ]
        } : undefined
      }, [
        h('div', {
          vLoading: dialogLoading.value,
          class: 'dialog-content'
        }, dialogConfig.value.component ? [
          h(dialogConfig.value.component, {
            ...dialogConfig.value.componentProps,
            modelValue: formData.value,
            'onUpdate:modelValue': (val: any) => formData.value = val,
            ref: dialogConfig.value.mode === 'select' ? tableRef : formRef,
            onConfirm: dialogConfig.value.mode === 'select' ? handleConfirm : undefined,
            onCancel: dialogConfig.value.mode === 'select' ? closeDialog : undefined
          })
        ] : undefined)
      ])
    }
  }
}
