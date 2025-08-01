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
  
  // JWT Token
  const token = ref('')
  
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
    
    // 存储JWT Token
    if (userData.token) {
      token.value = userData.token
      localStorage.setItem('token', userData.token)
    }
    
    // 同步到 localStorage（保持原有逻辑）
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
    token.value = ''
    
    // 清除 localStorage（保持原有逻辑 + token）
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('userType')
    localStorage.removeItem('phone')
    localStorage.removeItem('token')
  }
  
  // 从 localStorage 恢复用户状态
  const restoreFromStorage = () => {
    const storedLoginStatus = localStorage.getItem('isLoggedIn')
    const storedToken = localStorage.getItem('token')
    
    if (storedLoginStatus === 'true') {
      isLoggedIn.value = true
      userInfo.value = {
        userId: localStorage.getItem('userId') || '',
        username: localStorage.getItem('username') || '',
        account: localStorage.getItem('username') || '', // 使用 username 作为 account
        userType: localStorage.getItem('userType') || '',
        phone: localStorage.getItem('phone') || ''
      }
      
      // 恢复token
      if (storedToken) {
        token.value = storedToken
      }
    }
  }
  
  return {
    userInfo,
    isLoggedIn,
    isAuthenticated,
    displayName,
    token,
    login,
    logout,
    restoreFromStorage
  }
}) 