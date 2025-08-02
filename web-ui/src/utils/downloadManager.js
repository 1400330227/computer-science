import { ref } from 'vue'

// 全局下载管理器
class DownloadManager {
  constructor() {
    this.downloads = new Map()
    this.progressComponent = null
  }

  // 设置进度组件引用
  setProgressComponent(component) {
    this.progressComponent = component
  }

  // 开始下载
  async startDownload(url, fileName, options = {}) {
    const downloadId = Date.now() + Math.random()
    
    // 添加到进度显示
    if (this.progressComponent) {
      this.progressComponent.addDownload({
        fileName,
        total: 0,
        loaded: 0
      })
    }

    try {
      const response = await fetch(url, {
        method: options.method || 'GET',
        headers: options.headers || {},
        credentials: 'include' // 包含cookies
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const contentLength = response.headers.get('content-length')
      const total = contentLength ? parseInt(contentLength, 10) : 0

      // 创建可读流
      const reader = response.body.getReader()
      const chunks = []
      let loaded = 0

      // 读取数据流
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) break

        chunks.push(value)
        loaded += value.length

        // 更新进度
        if (this.progressComponent) {
          this.progressComponent.updateProgress(downloadId, loaded, total)
        }
      }

      // 合并所有数据块
      const blob = new Blob(chunks)
      
      // 创建下载链接
      const url_obj = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url_obj
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      // 释放URL对象
      window.URL.revokeObjectURL(url_obj)

      // 标记完成并自动移除
      if (this.progressComponent) {
        this.progressComponent.completeDownload(downloadId)
        // 2秒后自动移除成功的下载
        setTimeout(() => {
          this.progressComponent.removeDownload(downloadId)
        }, 2000)
      }

      return { success: true, downloadId }

    } catch (error) {
      console.error('Download failed:', error)
      
      // 标记错误并自动移除
      if (this.progressComponent) {
        this.progressComponent.errorDownload(downloadId, error.message)
        // 3秒后自动移除失败的下载
        setTimeout(() => {
          this.progressComponent.removeDownload(downloadId)
        }, 3000)
      }

      throw error
    }
  }

  // 使用XMLHttpRequest实现更精确的进度控制
  async startDownloadWithXHR(url, fileName, options = {}) {
    return new Promise((resolve, reject) => {
      const downloadId = Date.now() + Math.random()

      // 添加到进度显示
      if (this.progressComponent) {
        this.progressComponent.addDownload({
          id: downloadId,
          fileName,
          total: 0,
          loaded: 0
        })
      }

      // 设置超时处理（30秒无响应则认为失败）
      const timeout = setTimeout(() => {
        if (this.downloads.has(downloadId)) {
          const xhr = this.downloads.get(downloadId)
          xhr.abort()
          this.downloads.delete(downloadId)

          if (this.progressComponent) {
            this.progressComponent.errorDownload(downloadId, '下载超时')
            setTimeout(() => {
              this.progressComponent.removeDownload(downloadId)
            }, 3000)
          }

          reject(new Error('Download timeout'))
        }
      }, 30000) // 30秒超时

      const xhr = new XMLHttpRequest()
      
      // 进度监听
      xhr.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          if (this.progressComponent) {
            this.progressComponent.updateProgress(downloadId, event.loaded, event.total)
          }
        }
      })

      // 完成监听
      xhr.addEventListener('load', () => {
        clearTimeout(timeout) // 清除超时定时器

        if (xhr.status === 200) {
          const blob = new Blob([xhr.response])
          const url_obj = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url_obj
          link.download = fileName
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url_obj)

          if (this.progressComponent) {
            this.progressComponent.completeDownload(downloadId)
            // 2秒后自动移除成功的下载
            setTimeout(() => {
              this.progressComponent.removeDownload(downloadId)
            }, 2000)
          }

          resolve({ success: true, downloadId })
        } else {
          clearTimeout(timeout) // 清除超时定时器
          const error = new Error(`HTTP error! status: ${xhr.status}`)
          if (this.progressComponent) {
            this.progressComponent.errorDownload(downloadId, error.message)
            // 3秒后自动移除失败的下载
            setTimeout(() => {
              this.progressComponent.removeDownload(downloadId)
            }, 3000)
          }
          reject(error)
        }
      })

      // 错误监听
      xhr.addEventListener('error', () => {
        clearTimeout(timeout) // 清除超时定时器
        const error = new Error('Network error')
        if (this.progressComponent) {
          this.progressComponent.errorDownload(downloadId, error.message)
          // 3秒后自动移除失败的下载
          setTimeout(() => {
            this.progressComponent.removeDownload(downloadId)
          }, 3000)
        }
        reject(error)
      })

      // 中止监听
      xhr.addEventListener('abort', () => {
        clearTimeout(timeout) // 清除超时定时器
        if (this.progressComponent) {
          this.progressComponent.cancelDownload(downloadId)
          // 1秒后自动移除取消的下载
          setTimeout(() => {
            this.progressComponent.removeDownload(downloadId)
          }, 1000)
        }
      })

      // 存储xhr引用以便取消
      this.downloads.set(downloadId, xhr)

      // 开始请求
      xhr.open(options.method || 'GET', url)
      xhr.responseType = 'blob'
      
      // 设置请求头
      if (options.headers) {
        Object.entries(options.headers).forEach(([key, value]) => {
          xhr.setRequestHeader(key, value)
        })
      }

      xhr.withCredentials = true // 包含cookies
      xhr.send()
    })
  }

  // 取消下载
  cancelDownload(downloadId) {
    const xhr = this.downloads.get(downloadId)
    if (xhr) {
      xhr.abort()
      this.downloads.delete(downloadId)
    }
  }
}

// 创建全局实例
export const downloadManager = new DownloadManager()

// 便捷方法
export function startDownload(url, fileName, options = {}) {
  return downloadManager.startDownloadWithXHR(url, fileName, options)
}

export function setProgressComponent(component) {
  downloadManager.setProgressComponent(component)
}
