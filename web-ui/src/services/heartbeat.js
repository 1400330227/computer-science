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
    console.log('💓 执行心跳检测...')
    try {
      const response = await api.get('/users/heartbeat')
      console.log('💓 心跳响应:', response.data)

      // 心跳正常
      if (response.data.success) {
        console.log('💓 心跳正常')
        return true
      }
    } catch (error) {
      console.log('❌ 心跳检测异常:', error.response)

      // 检查是否是被踢出的错误
      if (error.response && error.response.status === 401) {
        const errorData = error.response.data
        console.log('🔍 检查401错误数据:', errorData)

        if (errorData && errorData.code === 'SESSION_KICKED_OUT') {
          // 被踢出，立即通知用户
          console.log('🚨 心跳检测发现账号被踢出:', errorData.message)
          this.handleKickedOut(errorData.message)
          return false
        } else {
          console.log('ℹ️ 401错误但不是踢出错误:', errorData)
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
    console.log('🛑 开始处理踢出逻辑...')

    // 停止心跳检测
    this.stop()

    console.log('📢 准备显示踢出弹窗...')

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
          console.log('👆 用户点击了重新登录按钮')

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
    ).then(() => {
      console.log('✅ 踢出弹窗已显示')
    }).catch((error) => {
      console.error('❌ 显示踢出弹窗失败:', error)
    })

    console.log('📢 踢出弹窗调用完成')
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
