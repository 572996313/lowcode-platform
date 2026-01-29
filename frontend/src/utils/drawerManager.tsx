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
  mode?: 'add' | 'edit' | 'view' | 'detail'
  dataUrl?: string
  submitUrl?: string
  submitMethod?: 'POST' | 'PUT' | 'PATCH'
  showFooter?: boolean
  confirmText?: string
  cancelText?: string
  onSuccess?: () => void
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
    } else if (config.mode === 'add') {
      // 新增模式，清空表单数据
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

  return {
    drawerVisible,
    drawerConfig,
    drawerLoading,
    formData,
    openDrawer,
    closeDrawer,
    submitDrawer
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
      submitDrawer
    } = useDrawerManager()

    const formRef = ref()

    const handleConfirm = () => {
      submitDrawer(formRef.value)
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
            ref: formRef
          })
        ] : undefined)
      ])
    }
  }
}
