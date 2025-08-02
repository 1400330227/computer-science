import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({
    userId: '',
    username: '',
    account: '',
    userType: '',
    phone: ''
  })
  
  // 登录状态
  const isLoggedIn = ref(false)
  
  // 计算属性：是否已认证
  const isAuthenticated = computed(() => isLoggedIn.value)
  
  // 计算属性：用户显示名称
  const displayName = computed(() => userInfo.value.account || userInfo.value.username || '未知用户')
  
  // 登录方法
  const login = (userData) => {
    userInfo.value = {
      userId: userData.userId || '',
      username: userData.username || '',
      account: userData.account || '',
      userType: userData.userType || '',
      phone: userData.phone || ''
    }
    isLoggedIn.value = true
    
    // 同步到 localStorage
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('username', userData.account || userData.username || '')
    localStorage.setItem('userId', userData.userId || '')
    localStorage.setItem('userType', userData.userType || '')
    localStorage.setItem('phone', userData.phone || '')
  }
  
  // 登出方法
  const logout = () => {
    userInfo.value = {
      userId: '',
      username: '',
      account: '',
      userType: '',
      phone: ''
    }
    isLoggedIn.value = false
    
    // 清除 localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('userType')
    localStorage.removeItem('phone')
    // 确保清除任何可能残留的token
    localStorage.removeItem('token')
  }
  
  // 从服务器验证用户状态（推荐方式）
  const validateUserSession = async () => {
    try {
      // 调用后端接口验证当前用户状态
      const response = await fetch('/api/users/current', {
        method: 'GET',
        credentials: 'include', // 重要：包含cookies
        headers: {
          'Accept': 'application/json'
        }
      })

      if (response.ok) {
        const userData = await response.json()
        // 如果服务器确认用户已登录，更新前端状态
        login(userData)
        return true
      } else {
        // 如果服务器返回未登录，清除前端状态
        logout()
        return false
      }
    } catch (error) {
      console.error('验证用户会话失败:', error)
      // 网络错误时，尝试从localStorage恢复（降级方案）
      restoreFromStorageAsFallback()
      return false
    }
  }

  // 从 localStorage 恢复用户状态（降级方案）
  const restoreFromStorageAsFallback = () => {
    const storedLoginStatus = localStorage.getItem('isLoggedIn')

    if (storedLoginStatus === 'true') {
      isLoggedIn.value = true
      userInfo.value = {
        userId: localStorage.getItem('userId') || '',
        username: localStorage.getItem('username') || '',
        account: localStorage.getItem('username') || '', // 使用 username 作为 account
        userType: localStorage.getItem('userType') || '',
        phone: localStorage.getItem('phone') || ''
      }
    } else {
      // 确保状态为false
      isLoggedIn.value = false
    }
  }

  // 主要的状态恢复方法
  const restoreFromStorage = async () => {
    // 优先从服务器验证，失败时使用localStorage
    await validateUserSession()
  }
  
  return {
    userInfo,
    isLoggedIn,
    isAuthenticated,
    displayName,
    login,
    logout,
    restoreFromStorage,
    validateUserSession
  }
}) 