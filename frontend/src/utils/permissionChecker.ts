import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 权限检查器
 */
export const usePermissionChecker = () => {
  const userStore = useUserStore()

  // 用户权限列表
  const permissions = computed(() => userStore.permissions || [])

  /**
   * 检查是否有权限
   * @param perm 权限标识，如 'user:add', 'user:edit'
   * @returns 是否有权限
   */
  const hasPermission = (perm?: string): boolean => {
    if (!perm) return true

    // 管理员拥有所有权限
    if (userStore.isAdmin) return true

    // 检查权限列表
    return permissions.value.includes(perm)
  }

  /**
   * 检查是否有任意一个权限
   * @param perms 权限标识数组
   * @returns 是否有任意一个权限
   */
  const hasAnyPermission = (perms: string[]): boolean => {
    if (!perms || perms.length === 0) return true
    if (userStore.isAdmin) return true

    return perms.some(perm => permissions.value.includes(perm))
  }

  /**
   * 检查是否有所有权限
   * @param perms 权限标识数组
   * @returns 是否有所有权限
   */
  const hasAllPermissions = (perms: string[]): boolean => {
    if (!perms || perms.length === 0) return true
    if (userStore.isAdmin) return true

    return perms.every(perm => permissions.value.includes(perm))
  }

  /**
   * 检查是否有角色
   * @param role 角色标识
   * @returns 是否有该角色
   */
  const hasRole = (role?: string): boolean => {
    if (!role) return true

    const roles = userStore.roles || []
    return roles.includes(role)
  }

  /**
   * 检查是否有任意一个角色
   * @param roles 角色标识数组
   * @returns 是否有任意一个角色
   */
  const hasAnyRole = (roles: string[]): boolean => {
    if (!roles || roles.length === 0) return true

    const userRoles = userStore.roles || []
    return roles.some(role => userRoles.includes(role))
  }

  /**
   * 评估按钮显示条件表达式
   * @param condition 条件表达式，如 "row.status === 1"
   * @param context 上下文数据
   * @returns 是否显示
   */
  const evaluateShowCondition = (
    condition?: string,
    context?: { row?: any; form?: any; selection?: any[] }
  ): boolean => {
    if (!condition) return true

    try {
      // 创建安全的执行环境
      const func = new Function('row', 'form', 'selection', 'return ' + condition)
      return func(context?.row, context?.form, context?.selection)
    } catch (error) {
      console.error('评估显示条件失败:', error)
      return true
    }
  }

  /**
   * 评估按钮禁用条件表达式
   * @param condition 条件表达式
   * @param context 上下文数据
   * @returns 是否禁用
   */
  const evaluateDisabledCondition = (
    condition?: string,
    context?: { row?: any; form?: any; selection?: any[] }
  ): boolean => {
    if (!condition) return false

    try {
      const func = new Function('row', 'form', 'selection', 'return ' + condition)
      return func(context?.row, context?.form, context?.selection)
    } catch (error) {
      console.error('评估禁用条件失败:', error)
      return false
    }
  }

  return {
    permissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    hasAnyRole,
    evaluateShowCondition,
    evaluateDisabledCondition
  }
}

/**
 * 权限指令
 * 用于在模板中使用 v-permission
 */
export const permissionDirective = {
  mounted(el: HTMLElement, binding: { value: string | string[] }) {
    const { hasPermission, hasAnyPermission } = usePermissionChecker()

    let hasAuth = false

    if (typeof binding.value === 'string') {
      // 单个权限
      hasAuth = hasPermission(binding.value)
    } else if (Array.isArray(binding.value)) {
      // 多个权限（OR关系）
      hasAuth = hasAnyPermission(binding.value)
    }

    if (!hasAuth) {
      // 移除元素
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 角色指令
 * 用于在模板中使用 v-role
 */
export const roleDirective = {
  mounted(el: HTMLElement, binding: { value: string | string[] }) {
    const { hasRole, hasAnyRole } = usePermissionChecker()

    let hasAuth = false

    if (typeof binding.value === 'string') {
      // 单个角色
      hasAuth = hasRole(binding.value)
    } else if (Array.isArray(binding.value)) {
      // 多个角色（OR关系）
      hasAuth = hasAnyRole(binding.value)
    }

    if (!hasAuth) {
      // 移除元素
      el.parentNode?.removeChild(el)
    }
  }
}
