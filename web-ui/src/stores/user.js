import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({
    userId: '',
    username: '',
    account: '',
    userType: '',
    phone: '',
    nickname: '',
    gender: '',
    college: '',
    title: '',
    major: '',
    accountStatus: '',
    address: '',
    creator: '',
    createdAt: '',
    updatedBy: '',
    updatedAt: '',
    remarks: ''
  })
  
  // 登录状态
  const isLoggedIn = ref(false)
  
  // 计算属性：是否已认证
  const isAuthenticated = computed(() => isLoggedIn.value)
  
  // 计算属性：用户显示名称
  const displayName = computed(() => {
    const nickname = userInfo.value.nickname
    const account = userInfo.value.account
    const userType = userInfo.value.userType
    
    if (nickname) {
      return `${nickname}`
    } else if (account) {
      return `${account}`
    } else {
      return '未知用户'
    }
  })

  // 计算属性：学院
  const college = computed(() => userInfo.value.college || localStorage.getItem('college') || '')
  
  // 登录方法
  const login = (userData) => {
    userInfo.value = {
      userId: userData.userId || '',
      username: userData.username || '',
      account: userData.account || '',
      userType: userData.userType || '',
      phone: userData.phone || '',
      nickname: userData.nickname || '',
      gender: userData.gender || '',
      college: userData.college || '',
      title: userData.title || '',
      major: userData.major || '',
      accountStatus: userData.accountStatus || '',
      address: userData.address || '',
      creator: userData.creator || '',
      createdAt: userData.createdAt || '',
      updatedBy: userData.updatedBy || '',
      updatedAt: userData.updatedAt || '',
      remarks: userData.remarks || ''
    }
    isLoggedIn.value = true
    
    // 同步到 localStorage
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('username', userData.account || userData.username || '')
    localStorage.setItem('userId', userData.userId || '')
    localStorage.setItem('userType', userData.userType || '')
    localStorage.setItem('phone', userData.phone || '')
    localStorage.setItem('nickname', userData.nickname || '')
    localStorage.setItem('gender', userData.gender || '')
    localStorage.setItem('college', userData.college || '')
    localStorage.setItem('title', userData.title || '')
    localStorage.setItem('major', userData.major || '')
    localStorage.setItem('accountStatus', userData.accountStatus || '')
    localStorage.setItem('address', userData.address || '')
    localStorage.setItem('creator', userData.creator || '')
    localStorage.setItem('createdAt', userData.createdAt || '')
    localStorage.setItem('updatedBy', userData.updatedBy || '')
    localStorage.setItem('updatedAt', userData.updatedAt || '')
    localStorage.setItem('remarks', userData.remarks || '')
  }
  
  // 登出方法
  const logout = () => {
    userInfo.value = {
      userId: '',
      username: '',
      account: '',
      userType: '',
      phone: '',
      nickname: '',
      gender: '',
      college: '',
      title: '',
      major: '',
      accountStatus: '',
      address: '',
      creator: '',
      createdAt: '',
      updatedBy: '',
      updatedAt: '',
      remarks: ''
    } 
    isLoggedIn.value = false
    
    // 清除 localStorage
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('userType')
    localStorage.removeItem('phone')
    localStorage.removeItem('nickname')
    localStorage.removeItem('gender')
    localStorage.removeItem('college')
    localStorage.removeItem('title')
    localStorage.removeItem('major')
    localStorage.removeItem('accountStatus')
    localStorage.removeItem('address')
    localStorage.removeItem('creator')
    localStorage.removeItem('createdAt')
    localStorage.removeItem('updatedBy')
    localStorage.removeItem('updatedAt')
    localStorage.removeItem('remarks')
    // 确保清除任何可能残留的token
    localStorage.removeItem('token')
  }
  
  // 从 localStorage 恢复用户状态
  const restoreFromStorage = () => {
    const storedLoginStatus = localStorage.getItem('isLoggedIn')
    
    if (storedLoginStatus === 'true') {
      isLoggedIn.value = true
      userInfo.value = {
        userId: localStorage.getItem('userId') || '',
        username: localStorage.getItem('username') || '',
        account: localStorage.getItem('username') || '', // 使用 username 作为 account
        userType: localStorage.getItem('userType') || '',
        phone: localStorage.getItem('phone') || '',
        nickname: localStorage.getItem('nickname') || '',
        gender: localStorage.getItem('gender') || '',
        college: localStorage.getItem('college') || '',
        title: localStorage.getItem('title') || '',
        major: localStorage.getItem('major') || '',
        accountStatus: localStorage.getItem('accountStatus') || '',
        address: localStorage.getItem('address') || '',
        creator: localStorage.getItem('creator') || '',
        createdAt: localStorage.getItem('createdAt') || '',
        updatedBy: localStorage.getItem('updatedBy') || '',
        updatedAt: localStorage.getItem('updatedAt') || '',
        remarks: localStorage.getItem('remarks') || ''
      }
    } else {
      // 确保状态为false
      isLoggedIn.value = false
    }
  }
  
  // 更新用户信息方法
  const updateUserInfo = (updatedInfo) => {
    userInfo.value = { ...userInfo.value, ...updatedInfo }
    
    // 同步到 localStorage
    Object.keys(updatedInfo).forEach(key => {
      if (updatedInfo[key] !== undefined) {
        localStorage.setItem(key, updatedInfo[key] || '')
      }
    })
  }
  
  return {
    user: userInfo,
    userInfo,
    isLoggedIn,
    isAuthenticated,
    displayName,
    college,
    login,
    logout,
    restoreFromStorage,
    updateUserInfo
  }
}) 