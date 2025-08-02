<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <h1>欢迎使用 HDFS 文件管理系统</h1>
      <p>请选择您需要的功能</p>
    </div>

    <div class="function-cards">
      <div class="card" @click="navigateTo('/file-list')">
        <div class="card-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M10 4H4c-1.11 0-2 .89-2 2v12c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2h-8l-2-2z"/>
          </svg>
        </div>
        <div class="card-content">
          <h2>文件管理</h2>
          <p>浏览、上传、下载和管理您的文件</p>
        </div>
      </div>

      <div class="card" @click="navigateTo('/file-upload')">
        <div class="card-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16h6v-6h4l-7-7-7 7h4v6zm-4 2h14v2H5v-2z"/>
          </svg>
        </div>
        <div class="card-content">
          <h2>上传文件</h2>
          <p>快速上传文件到HDFS存储系统</p>
        </div>
      </div>




    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'

const router = useRouter()
const username = localStorage.getItem('username') || '用户'

onMounted(() => {
  // 页面加载时的初始化操作
})

// 导航到指定路由
function navigateTo(path) {
  router.push(path)
}

// 测试消息提示
function testMessages() {
  ElMessage.success('这是成功消息提示！现在使用蓝紫色背景')

  setTimeout(() => {
    ElMessage.error('这是错误消息提示！统一蓝紫色风格')
  }, 1000)

  setTimeout(() => {
    ElMessage.warning('这是警告消息提示！白色文字更清晰')
  }, 2000)

  setTimeout(() => {
    ElMessage.info('这是信息消息提示！与主题完美融合')
  }, 3000)

  setTimeout(() => {
    ElNotification({
      title: '通知标题',
      message: '这是一个通知消息，现在使用统一的蓝紫色渐变背景',
      type: 'success',
      duration: 4000
    })
  }, 4000)

  setTimeout(() => {
    ElMessageBox.confirm(
      '这是确认对话框，也使用了统一的蓝紫色主题',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
      ElMessage.success('您点击了确定')
    }).catch(() => {
      ElMessage.info('您点击了取消')
    })
  }, 5000)
}


</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  /* padding: 20px; */
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
  text-align: center;
}

.welcome-banner h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 12px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.welcome-banner p {
  font-size: 16px;
  color: white;
  margin: 0;
  opacity: 0.9;
}

.function-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  padding: 24px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
}

.card-icon {
  width: 56px;
  height: 56px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.card-content {
  flex: 1;
}

.card-content h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #374151;
}

.card-content p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.card:hover .card-icon {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .function-cards {
    grid-template-columns: 1fr;
  }

  .welcome-banner {
    padding: 20px;
  }

  .welcome-banner h1 {
    font-size: 24px;
  }
}
</style>