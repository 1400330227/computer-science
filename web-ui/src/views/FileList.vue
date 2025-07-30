<template>
  <div class="file-list-page">
    <div class="file-list-container">
      <!-- 提示信息区域 -->
      <div class="info-box">
        <p class="info-text">
          <strong>满足以下检索条件的语料信息</strong>，可在表格中显示相关内容。如有问题，可拨打电话：<span class="highlight">13761230066</span>
        </p>
        <p class="info-text">
          <strong>检索内容：</strong>展示所有语料文件，语料包括地理、数学、历史、科学、物理、体育、社会科学的语料。
        </p>
      </div>

      <!-- 文件表格标题 -->
      <el-table v-loading="loading" :data="fileList" style="width: 100%">
        <el-table-column prop="country" label="国家" />
        <el-table-column prop="datasetName" label="语料集名称" />
        <el-table-column prop="domain" label="所属领域" />
        <el-table-column prop="language" label="语种" />
        <el-table-column prop="dataFormat" label="数据形式" />
        <el-table-column prop="dataCategory" label="数据分类" />
        <el-table-column prop="dataYear" label="数据年份" />
        <el-table-column prop="sourceLocation" label="来源归属地" />
        <el-table-column prop="dataSource" label="数据来源" />
        <el-table-column prop="remark" label="备注说明" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="downloadFile(row)">下载</el-button>
            <el-button link type="primary" @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 无数据提示 -->
      <div v-if="fileList.length === 0" class="no-data">
        暂无数据
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const fileList = ref([])
const loading = ref(false)

// 模拟数据
const mockFileList = [
]

onMounted(() => {
  loadFileList()
})

// 加载文件列表
function loadFileList() {
  loading.value = true

  // 实际项目中应调用后端接口，这里使用模拟数据
  // axios.get('/hdfs/getFiles')
  //   .then(response => {
  //     fileList.value = response.data
  //     loading.value = false
  //   })
  //   .catch(error => {
  //     console.error('获取文件列表失败:', error)
  //     alert('获取文件列表失败')
  //     loading.value = false
  //   })

  // 模拟API请求延迟
  setTimeout(() => {
    fileList.value = mockFileList
    loading.value = false
  }, 500)
}

// 下载文件
function downloadFile(file) {
  alert(`开始下载文件: ${file.datasetName}`)

  // 实际项目中应调用后端下载接口
  // axios.get(`/hdfs/download?fileName=${encodeURIComponent(file.datasetName)}`, {
  //   responseType: 'blob'
  // })
  // .then(...)
}

// 查看详情
function viewDetails(file) {
  alert(`查看文件详情: ${file.datasetName}`)
  // 实际项目中可以跳转到详情页或打开详情对话框
  // router.push(`/file-details/${file.id}`)
}
</script>

<style scoped>
.file-list-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}



.file-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 提示信息区域 */
.info-box {
  background-color: #f9f9f9;
  border: 1px solid #eaeaea;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.info-text {
  margin: 5px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.highlight {
  color: #ff6b6b;
  font-weight: 500;
}

.no-data {
  text-align: center;
  padding: 30px;
  color: #999;
  background-color: #fff;
  border: 1px solid #ddd;
}

/* Element Plus table styling overrides */
:deep(.el-table) {
  margin-top: 15px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #333;
  font-weight: 500;
}
</style>
