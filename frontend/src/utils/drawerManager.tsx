import { ref, shallowRef, markRaw, h, type Component } from 'vue'
import { ElMessage } from 'element-plus'
import { request } from './request'

/**
 * 抽屉配置接口
 */
export interface DrawerConfig {
  id: string
  title: string
  size?: 'small' | 'default' | 'large'
  direction?: 'rtl' | 'ltr' | 'ttb' | 'btt'
  component?: Component
  componentProps?: Record<string, any>
  mode?: 'add' | 'edit' | 'view' | 'detail' | 'select'  // 新增 select
  dataUrl?: string
  submitUrl?: string
  submitMethod?: 'POST' | 'PUT' | 'PATCH'
  showFooter?: boolean
  confirmText?: string
  cancelText?: string
  onSuccess?: () => void
  onConfirm?: (data?: any) => void  // 新增
  onClose?: () => void
}

/**
 * 抽屉状态
 */
const drawerVisible = ref(false)
const drawerConfig = shallowRef<DrawerConfig | null>(null)
const drawerLoading = ref(false)
const formData = ref<Record<string, any>>({})

/**
 * 抽屉管理器
 */
export const useDrawerManager = () => {
  /**
   * 打开抽屉
   */
  const openDrawer = async (config: DrawerConfig) => {
    drawerConfig.value = config
    drawerVisible.value = true

    // 如果是编辑模式且有数据URL，加载数据
    if (config.mode === 'edit' && config.dataUrl) {
      drawerLoading.value = true
      try {
        const res = await request.get(config.dataUrl)
        formData.value = res
      } catch (error: any) {
        ElMessage.error(error.message || '加载数据失败')
      } finally {
        drawerLoading.value = false
      }
    } else if (config.mode === 'add' || config.mode === 'select') {
      // 新增或选择模式，清空表单数据
      formData.value = {}
    }
  }

  /**
   * 关闭抽屉
   */
  const closeDrawer = () => {
    drawerVisible.value = false
    drawerConfig.value?.onClose?.()
    // 清空数据
    formData.value = {}
  }

  /**
   * 提交表单
   */
  const submitDrawer = async (formRef: any) => {
    if (!formRef) return false

    try {
      // 验证表单
      const valid = await formRef.validate()
      if (!valid) return false

      const config = drawerConfig.value!
      drawerLoading.value = true

      await request.request({
        url: config.submitUrl,
        method: (config.submitMethod || 'POST') as any,
        data: formData.value
      })

      const modeText = config.mode === 'add' ? '添加' : '保存'
      ElMessage.success(`${modeText}成功`)

      closeDrawer()
      config.onSuccess?.()

      return true
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
      return false
    } finally {
      drawerLoading.value = false
    }
  }

  /**
   * 处理选择确认(用于表格选择模式)
   */
  const handleSelectionConfirm = (selectedData: any) => {
    const config = drawerConfig.value!
    if (config.onConfirm) {
      config.onConfirm(selectedData)
    }
    closeDrawer()
  }

  return {
    drawerVisible,
    drawerConfig,
    drawerLoading,
    formData,
    openDrawer,
    closeDrawer,
    submitDrawer,
    handleSelectionConfirm
  }
}

/**
 * 创建全局抽屉容器组件
 */
export const DrawerContainer = {
  name: 'DrawerContainer',
  setup() {
    const {
      drawerVisible,
      drawerConfig,
      drawerLoading,
      formData,
      closeDrawer,
      submitDrawer,
      handleSelectionConfirm
    } = useDrawerManager()

    const formRef = ref()
    const tableRef = ref()

    const handleConfirm = () => {
      const config = drawerConfig.value
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
        submitDrawer(formRef.value)
      }
    }

    return () => {
      if (!drawerConfig.value) return null

      const showFooter = drawerConfig.value.showFooter !== false

      return h('el-drawer', {
        modelValue: drawerVisible.value,
        title: drawerConfig.value.title,
        size: drawerConfig.value.size || 'default',
        direction: drawerConfig.value.direction || 'rtl',
        onClose: closeDrawer,
        destroyOnClose: true,
        vSlots: showFooter ? {
          footer: () => [
            h('div', { class: 'drawer-footer' }, [
              h('el-button', { onClick: closeDrawer }, drawerConfig.value.cancelText || '取消'),
              h('el-button', {
                type: 'primary',
                loading: drawerLoading.value,
                onClick: handleConfirm
              }, drawerConfig.value.confirmText || '确定')
            ])
          ]
        } : undefined
      }, [
        h('div', {
          vLoading: drawerLoading.value,
          class: 'drawer-content'
        }, drawerConfig.value.component ? [
          h(drawerConfig.value.component, {
            ...drawerConfig.value.componentProps,
            modelValue: formData.value,
            'onUpdate:modelValue': (val: any) => formData.value = val,
            ref: drawerConfig.value.mode === 'select' ? tableRef : formRef,
            onConfirm: drawerConfig.value.mode === 'select' ? handleConfirm : undefined,
            onCancel: drawerConfig.value.mode === 'select' ? closeDrawer : undefined
          })
        ] : undefined)
      ])
    }
  }
}
