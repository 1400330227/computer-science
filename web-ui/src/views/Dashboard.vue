<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <h1>欢迎使用广西大学东盟语料收集与管理平台</h1>
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
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../services/api'
import { useUserStore } from '../stores/user'
const router = useRouter()
const userStore = useUserStore()
// 列表与分页
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadCorpora = () => {
  loading.value = true
  const params = { page: currentPage.value, size: pageSize.value }
  api
    .get('/corpus', { params })
    .then((response) => {
      if (response.data && response.data.records) {
        tableData.value = response.data.records
        total.value = response.data.total || 0
      } else if (Array.isArray(response.data)) {
        tableData.value = response.data
        total.value = response.data.length
      } else {
        tableData.value = []
        total.value = 0
        ElMessage.warning('返回数据格式不正确')
      }
    })
    .catch(() => {
      tableData.value = []
      total.value = 0
      ElMessage.error('获取语料列表失败，请稍后重试')
    })
    .finally(() => {
      loading.value = false
    })
}

function handlePageChange(page) {
  currentPage.value = page
  loadCorpora()
}

function getDownloadUrl(corpus) {
  return `/api/corpus/download/${corpus.corpusId}`;
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadCorpora()
}

onMounted(() => {
  // loadCorpora()
})

// 导航到指定路由
function navigateTo(path) {
  router.push(path)
}

const formatDate = (value) => {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  /* padding: 20px; */
}

.dashboard-table {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
</style>
