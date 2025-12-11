<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <h1>欢迎使用广西大学东盟语料库管理与标注平台</h1>
    </div>
    <div class="function-cards">
      <div class="card" @click="navigateTo('/file-list')">
        <div class="card-icon">📁</div>
        <div class="card-content">
          <h2>我的语料集</h2>
          <p>浏览、上传、下载和管理您的语料集</p>
        </div>
      </div>

      <div class="card" @click="navigateTo('/file-upload')">
        <div class="card-icon">⬆️</div>
        <div class="card-content">
          <h2>上传语料集</h2>
          <p>快速上传文件到HDFS存储系统</p>
        </div>
      </div>

      <div class="card" @click="navigateTo('/my-files')">
        <div class="card-icon">👤</div>
        <div class="card-content">
          <h2>我的文件</h2>
          <p>查看和管理我的文件</p>
        </div>
      </div>

      <div class="card" @click="navigateTo('/all-files')">
        <div class="card-icon">👥</div>
        <div class="card-content">
          <h2>所有文件</h2>
          <p>查看所有文件</p>
        </div>
      </div>
    </div>
    <!-- 文档下载区域 -->
    <div class="document-section">
      <h2>相关文档</h2>
      <div class="document-cards">
        <div class="document-card">
<!--          <div class="document-icon">📋</div>-->
          <div class="document-content">
            <h3>广西大学东盟语料库建设方案</h3>
<!--            <p>了解语料库建设的详细方案和规划</p>-->
            <a href="/广西大学东盟语料库建设方案.docx" download>
              <div class="download-btn">点击下载</div>
            </a>
          </div>
        </div>
        <div class="document-card">
<!--          <div class="document-icon">📖</div>-->
          <div class="document-content">
            <h3>广西大学东盟语料库管理与标注平台系统操作手册</h3>
<!--            <p>详细的操作指南和使用说明</p>-->
            <a href="/广西大学东盟语料库管理与标注平台系统操作手册.docx" download>
              <div class="download-btn">点击下载</div>
            </a>
          </div>
        </div>
        <div class="document-card">
<!--          <div class="document-icon">📊</div>-->
          <div class="document-content">
            <h3>东盟国家价值观相关数据初步整理说明</h3>
<!--            <p>东盟国家价值观相关数据的整理说明和初步分析</p>-->
            <a href="/东盟国家价值观相关数据初步整理说明.pdf" download>
              <div class="download-btn">点击下载</div>
            </a>
          </div>
      </div>
      </div>
    </div>

    <div class="document-section">
      <h2>相关应用</h2>

      <div>
        <el-link href="http://172.21.44.162:9089" target="_blank">广西大学东盟大语言模型</el-link></div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  // loadCorpora()
})

// 导航到指定路由
function navigateTo(path) {
  if (path === '/all-files') {
    if (userStore.user?.userType === 'admin') {
      router.push(path);
    } else {
      ElMessage.warning('目前没有权限查看其他人的文件');
    }
  } else {
    router.push(path);
  }
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
  margin-bottom: 30px;
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 16px;
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

/* 文档下载区域样式 */
.document-section {
  margin-bottom: 30px;
}

.document-section h2 {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 20px;
}

.document-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.document-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 25px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.document-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  background-color: #f9fbff;
}

.document-icon {
  font-size: 40px;
  margin-right: 20px;
  color: #4b6cb7;
}

.document-content {
  flex: 1;
}

.document-content h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.document-content p {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
}

.download-btn {
  display: inline-block;
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.document-card:hover .download-btn {
  background: linear-gradient(135deg, #5a7bc7 0%, #2a3a58 100%);
  transform: scale(1.05);
}
</style>
