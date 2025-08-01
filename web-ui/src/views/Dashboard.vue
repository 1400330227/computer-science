<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <h1>欢迎使用 HDFS 文件管理系统</h1>
      <p>请选择您需要的功能</p>
    </div>

    <div class="function-cards">
      <div class="card" @click="navigateTo('/file-list')">
        <div class="card-icon">📁</div>
        <div class="card-content">
          <h2>文件管理</h2>
          <p>浏览、上传、下载和管理您的文件</p>
        </div>
      </div>

      <div class="card" @click="navigateTo('/upload')">
        <div class="card-icon">⬆️</div>
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
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  /* padding: 20px; */
}

.welcome-banner {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 30px;
  border-radius: 10px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.welcome-banner h1 {
  font-size: 28px;
  font-weight: 500;
  margin-bottom: 10px;
}

.welcome-banner p {
  font-size: 16px;
  opacity: 0.9;
}

.function-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  background-color: #f9fbff;
}

.card-icon {
  font-size: 40px;
  margin-right: 20px;
}

.card-content {
  flex: 1;
}

.card-content h2 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 5px;
  color: #303133;
}

.card-content p {
  font-size: 14px;
  color: #606266;
  margin: 0;
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