<template>
  <div v-if="downloads.length > 0" class="download-progress-container">
    <div class="download-header">
      <span class="download-title">下载进度</span>
      <el-button 
        text 
        size="small" 
        @click="minimized = !minimized"
        :icon="minimized ? ArrowDown : ArrowUp"
      />
    </div>
    
    <div v-if="!minimized" class="download-list">
      <div
        v-for="download in downloads"
        :key="download.id"
        class="download-item"
        :class="{
          'completed': download.status === 'completed',
          'error': download.status === 'error',
          'just-completed': download.justCompleted,
          'just-errored': download.justErrored,
          'removing': download.removing
        }"
      >
        <div class="download-info">
          <div class="file-name">{{ download.fileName }}</div>
          <div class="download-stats">
            <span class="speed" v-if="download.status === 'downloading'">
              {{ formatSpeed(download.speed) }}
            </span>
            <span class="size">
              {{ formatSize(download.loaded) }} / {{ formatSize(download.total) }}
            </span>
            <span class="percentage">{{ download.percentage }}%</span>
          </div>
        </div>
        
        <div class="download-progress">
          <el-progress 
            :percentage="download.percentage" 
            :status="getProgressStatus(download.status)"
            :show-text="false"
            :stroke-width="4"
          />
        </div>
        
        <div class="download-actions">
          <el-button 
            v-if="download.status === 'downloading'" 
            text 
            size="small" 
            @click="cancelDownload(download.id)"
            type="danger"
          >
            取消
          </el-button>
          <el-button 
            v-if="download.status === 'completed' || download.status === 'error'" 
            text 
            size="small" 
            @click="removeDownload(download.id)"
          >
            移除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'

const downloads = ref([])
const minimized = ref(false)

// 添加下载任务
function addDownload(downloadInfo) {
  const download = {
    id: downloadInfo.id || Date.now() + Math.random(),
    fileName: downloadInfo.fileName,
    total: downloadInfo.total || 0,
    loaded: downloadInfo.loaded || 0,
    percentage: 0,
    speed: 0,
    status: 'downloading', // downloading, completed, error, cancelled
    startTime: Date.now(),
    lastTime: Date.now(),
    lastLoaded: 0
  }
  downloads.value.push(download)
  console.log('添加下载任务:', download)
  return download.id
}

// 更新下载进度
function updateProgress(downloadId, loaded, total) {
  const download = downloads.value.find(d => d.id === downloadId)
  if (!download) return

  const now = Date.now()
  const timeDiff = now - download.lastTime
  
  if (timeDiff > 500) { // 每500ms更新一次速度
    const loadedDiff = loaded - download.lastLoaded
    download.speed = loadedDiff / (timeDiff / 1000) // bytes per second
    download.lastTime = now
    download.lastLoaded = loaded
  }

  download.loaded = loaded
  download.total = total
  download.percentage = total > 0 ? Math.round((loaded / total) * 100) : 0
}

// 完成下载
function completeDownload(downloadId) {
  const download = downloads.value.find(d => d.id === downloadId)
  if (download) {
    download.status = 'completed'
    download.percentage = 100
    download.speed = 0

    // 添加完成动画效果
    download.justCompleted = true
    setTimeout(() => {
      if (download) {
        download.justCompleted = false
      }
    }, 1500)
  }
}

// 下载出错
function errorDownload(downloadId, error) {
  const download = downloads.value.find(d => d.id === downloadId)
  if (download) {
    download.status = 'error'
    download.error = error
    download.speed = 0

    // 添加错误动画效果
    download.justErrored = true
    setTimeout(() => {
      if (download) {
        download.justErrored = false
      }
    }, 2500)
  }
}

// 取消下载
function cancelDownload(downloadId) {
  const download = downloads.value.find(d => d.id === downloadId)
  if (download) {
    download.status = 'cancelled'
    download.speed = 0
  }
  // 这里可以添加实际的取消逻辑
}

// 移除下载记录
function removeDownload(downloadId) {
  const index = downloads.value.findIndex(d => d.id === downloadId)
  if (index > -1) {
    const download = downloads.value[index]

    // 添加移除动画
    download.removing = true

    setTimeout(() => {
      const currentIndex = downloads.value.findIndex(d => d.id === downloadId)
      if (currentIndex > -1) {
        downloads.value.splice(currentIndex, 1)
      }
    }, 300) // 300ms 动画时间
  }
}

// 格式化文件大小
function formatSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

// 格式化下载速度
function formatSpeed(bytesPerSecond) {
  return formatSize(bytesPerSecond) + '/s'
}

// 获取进度条状态
function getProgressStatus(status) {
  switch (status) {
    case 'completed': return 'success'
    case 'error': return 'exception'
    case 'cancelled': return 'warning'
    default: return null
  }
}

// 暴露方法给父组件
defineExpose({
  addDownload,
  updateProgress,
  completeDownload,
  errorDownload,
  cancelDownload,
  removeDownload
})
</script>

<style scoped>
.download-progress-container {
  position: fixed;
  top: 80px;
  right: 20px;
  width: 380px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid #e4e7ed;
  z-index: 1000;
  backdrop-filter: blur(8px);
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.download-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
  border-radius: 8px 8px 0 0;
}

.download-title {
  font-weight: 500;
  color: #303133;
}

.download-list {
  max-height: 400px;
  overflow-y: auto;
}

.download-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f5;
  transition: background-color 0.3s;
}

.download-item:hover {
  background-color: #f9f9f9;
}

.download-item.completed {
  background-color: #f0f9ff;
}

.download-item.error {
  background-color: #fef0f0;
}

.download-item.just-completed {
  background-color: #f0f9ff;
  animation: successPulse 1.5s ease-out;
}

.download-item.just-errored {
  background-color: #fef0f0;
  animation: errorShake 0.5s ease-out;
}

.download-item.removing {
  animation: slideOutRight 0.3s ease-in forwards;
}

@keyframes successPulse {
  0% {
    background-color: #f0f9ff;
    transform: scale(1);
  }
  50% {
    background-color: #dbeafe;
    transform: scale(1.02);
  }
  100% {
    background-color: #f0f9ff;
    transform: scale(1);
  }
}

@keyframes errorShake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

@keyframes slideOutRight {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
}

.download-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.file-name {
  font-weight: 500;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}

.download-stats {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.speed {
  color: #409eff;
  font-weight: 500;
}

.download-progress {
  margin-bottom: 8px;
}

.download-actions {
  display: flex;
  justify-content: flex-end;
}
</style>
