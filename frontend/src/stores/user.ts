import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
  roles?: string[]
  permissions?: string[]
  isAdmin?: boolean
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
  }

  // 获取权限列表
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])

  const setPermissions = (permList: string[]) => {
    permissions.value = permList
  }

  const setRoles = (roleList: string[]) => {
    roles.value = roleList
  }

  // 是否是管理员
  const isAdmin = ref(false)
  const setIsAdmin = (admin: boolean) => {
    isAdmin.value = admin
  }

  return {
    userInfo,
    token,
    permissions,
    roles,
    isAdmin,
    setToken,
    setUserInfo,
    clearUserInfo,
    setPermissions,
    setRoles,
    setIsAdmin
  }
})
