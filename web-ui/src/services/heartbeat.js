import api from './api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

class HeartbeatService {
  constructor() {
    this.intervalId = null
    this.isRunning = false
    this.interval = 3000 // 3秒检测一次
    this.userStore = null
  }

  /**
   * 启动心跳检测
   */
  start() {
    if (this.isRunning) {
      return
    }

    this.userStore = useUserStore()
    
    // 只有登录状态下才启动心跳检测
    if (!this.userStore.isAuthenticated) {
      return
    }

    console.log('🔄 启动心跳检测服务...')
    this.isRunning = true
    
    this.intervalId = setInterval(async () => {
      await this.checkHeartbeat()
    }, this.interval)
  }

  /**
   * 停止心跳检测
   */
  stop() {
    if (this.intervalId) {
      clearInterval(this.intervalId)
      this.intervalId = null
    }
    this.isRunning = false
    console.log('🛑 停止心跳检测服务')
  }

  /**
   * 检查心跳
   */
  async checkHeartbeat() {
    try {
      const response = await api.get('/users/heartbeat')
      
      // 心跳正常
      if (response.data.success) {
        // console.log('💓 心跳正常')
        return true
      }
    } catch (error) {
      // 检查是否是被踢出的错误
      if (error.response && error.response.status === 401) {
        const errorData = error.response.data
        
        if (errorData && errorData.code === 'SESSION_KICKED_OUT') {
          // 被踢出，立即通知用户
          console.log('🚨 心跳检测发现账号被踢出:', errorData.message)
          this.handleKickedOut(errorData.message)
          return false
        }
      }
      
      // 其他错误，可能是网络问题，暂时忽略
      console.warn('心跳检测失败:', error.message)
    }
    
    return false
  }

  /**
   * 处理被踢出的情况
   */
  handleKickedOut(message) {
    console.log('🚨 检测到账号被踢出:', message)
    
    // 停止心跳检测
    this.stop()
    
    // 立即显示弹窗通知
    ElMessageBox.alert(
      message || '您的账号在其他地方登录，当前会话已被强制下线',
      '登录状态异常',
      {
        confirmButtonText: '重新登录',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false,
        callback: () => {
          // 清除登录状态
          this.userStore.logout()
          
          // 跳转到登录页
          router.push('/login')
          
          // 刷新页面
          setTimeout(() => {
            window.location.reload()
          }, 100)
        }
      }
    )
  }

  /**
   * 设置检测间隔
   * @param {number} interval 间隔时间（毫秒）
   */
  setInterval(interval) {
    this.interval = interval
    
    // 如果正在运行，重启服务
    if (this.isRunning) {
      this.stop()
      this.start()
    }
  }

  /**
   * 检查是否正在运行
   */
  isActive() {
    return this.isRunning
  }
}

// 创建单例实例
const heartbeatService = new HeartbeatService()

export default heartbeatService 