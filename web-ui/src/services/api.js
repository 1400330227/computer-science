import axios from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie';

// Create an axios instance with default config
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000, // 30 seconds timeout
  withCredentials: true, // 重要：允许跨域请求携带cookies（Session需要）
  headers: {
    'Content-Type': 'application/json',
  }
})

// Request interceptor
api.interceptors.request.use(
  config => {
    const xsrfToken = Cookies.get('XSRF-TOKEN')
    if (xsrfToken) {
      // 默认头名称是X-XSRF-TOKEN，与后端Spring Security默认期望的一致
      config.headers['X-XSRF-TOKEN'] = xsrfToken
    }
    // 在发送请求之前做些什么
    // 注意：我们使用Session认证，不需要在这里添加Token
    // Session会自动通过Cookie传递
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    return response
  },
  error => {
    // 对响应错误做点什么
    if (error.response) {
      // 服务器响应但状态码不是 2xx
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 清除登录状态
          localStorage.removeItem('isLoggedIn')
          localStorage.removeItem('username')
          localStorage.removeItem('userId') 
          localStorage.removeItem('userType')
          localStorage.removeItem('phone')
          // 使用Vue Router进行跳转，而不是直接修改location
          setTimeout(() => {
            window.location.reload()
          }, 1000)
          break
        case 403:
          ElMessage.error('没有权限访问此资源')
          break
        case 404:
          ElMessage.warning('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求失败 (${error.response.status})`)
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('网络错误，无法连接到服务器')
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

// HDFS 文件管理 API
export const hdfsApi = {
  // 获取文件列表
  getFileList(path = '') {
    return api.get('/hdfs/files', {
      params: path ? { path } : {}
    })
  },

  // 上传文件
  uploadFile(file, destPath = '') {
    const formData = new FormData()
    formData.append('file', file)
    if (destPath) {
      formData.append('destPath', destPath)
    }

    return api.post('/hdfs/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 下载文件
  downloadFile(filePath) {
    return api.get('/hdfs/file', {
      params: {
        filePath,
        flag: true
      },
      responseType: 'blob'
    })
  },

  // 下载语料库
  downloadCorpus(corpusId) {
    return api.get(`/corpus/download/${corpusId}`, {
      responseType: 'blob'
    })
  },

  // 删除文件
  deleteFile(filePath) {
    return api.delete('/hdfs/file', {
      params: { filePath }
    })
  },

  // 创建目录
  createDirectory(dirPath) {
    return api.post('/hdfs/directory', null, {
      params: { dirPath }
    })
  },

  // 重命名文件/目录
  renameFile(oldPath, newPath) {
    return api.put('/hdfs/rename', null, {
      params: { oldPath, newPath }
    })
  }
}

export default api