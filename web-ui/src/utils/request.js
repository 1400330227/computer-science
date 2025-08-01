import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080',  // 后端API基础URL
  timeout: 5000  // 请求超时时间
})

// 请求拦截器 - 自动添加Token
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    
    // 如果有token，自动添加到请求头
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理Token过期等情况
request.interceptors.response.use(
  response => {
    return response
  },
  error => {
    const userStore = useUserStore()
    
    // 如果返回401，说明token过期或无效
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      userStore.logout()
      router.push('/login')
      return Promise.reject(error)
    }
    
    // 其他错误直接抛出
    return Promise.reject(error)
  }
)

export default request 