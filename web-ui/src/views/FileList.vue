<template>
  <div class="file-list-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-links">
        <a href="/" class="nav-link">首页</a>
        <a href="/file-list" class="nav-link active">语料清单</a>
        <a href="/settings" class="nav-link">用户</a>
        <a href="/settings" class="nav-link">权限管理</a>
      </div>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>文件管理</span> > <span class="active">语料集列表</span>
    </div>

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
      <div class="table-header">
        <div class="header-row">
          <div class="header-cell">国家</div>
          <div class="header-cell">语料集名称</div>
          <div class="header-cell">所属领域</div>
          <div class="header-cell">语种</div>
          <div class="header-cell">数据形式</div>
          <div class="header-cell">数据分类</div>
          <div class="header-cell">数据年份</div>
          <div class="header-cell">来源归属地</div>
          <div class="header-cell">数据来源</div>
          <div class="header-cell">备注说明</div>
          <div class="header-cell">操作</div>
        </div>
      </div>

      <!-- 文件列表 -->
      <div class="file-rows">
        <div v-for="(file, index) in fileList" :key="index" class="file-row">
          <div class="file-cell">{{ file.country }}</div>
          <div class="file-cell">{{ file.datasetName }}</div>
          <div class="file-cell">{{ file.domain }}</div>
          <div class="file-cell">{{ file.language }}</div>
          <div class="file-cell">{{ file.dataFormat }}</div>
          <div class="file-cell">{{ file.dataCategory }}</div>
          <div class="file-cell">{{ file.dataYear }}</div>
          <div class="file-cell">{{ file.sourceLocation }}</div>
          <div class="file-cell">{{ file.dataSource }}</div>
          <div class="file-cell">{{ file.remark }}</div>
          <div class="file-cell operation">
            <a href="javascript:;" @click="downloadFile(file)" class="operation-link">下载</a>
            <a href="javascript:;" @click="viewDetails(file)" class="operation-link">详情</a>
          </div>
        </div>
      </div>

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
  {
    country: '老挝',
    datasetName: '教育书籍扫描件-v1.zip',
    domain: '所属领域',
    language: '语种',
    dataFormat: '数据形式',
    dataCategory: '数据分类',
    dataYear: '2023年',
    sourceLocation: '来源归属地',
    dataSource: '数据来源',
    remark: '备注说明',
    id: 1
  },
  {
    country: '老挝',
    datasetName: '教育书籍扫描件-v1.zip',
    domain: '所属领域',
    language: '语种',
    dataFormat: '数据形式',
    dataCategory: '数据分类',
    dataYear: '2023年',
    sourceLocation: '来源归属地',
    dataSource: '数据来源',
    remark: '备注说明',
    id: 2
  },
  {
    country: '老挝',
    datasetName: '教育书籍扫描件-v1.zip',
    domain: '所属领域',
    language: '语种',
    dataFormat: '数据形式',
    dataCategory: '数据分类',
    dataYear: '2023年',
    sourceLocation: '来源归属地',
    dataSource: '数据来源',
    remark: '备注说明',
    id: 3
  },
  {
    country: '老挝',
    datasetName: '教育书籍扫描件-v1.zip',
    domain: '所属领域',
    language: '语种',
    dataFormat: '数据形式',
    dataCategory: '数据分类',
    dataYear: '2023年',
    sourceLocation: '来源归属地',
    dataSource: '数据来源',
    remark: '备注说明',
    id: 4
  },
  {
    country: '老挝',
    datasetName: '教育书籍扫描件-v1.zip',
    domain: '所属领域',
    language: '语种',
    dataFormat: '数据形式',
    dataCategory: '数据分类',
    dataYear: '2023年',
    sourceLocation: '来源归属地',
    dataSource: '数据来源',
    remark: '备注说明',
    id: 5
  }
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

/* 顶部导航样式 */
.top-nav {
  background-color: #5a8de1;
  padding: 0 20px;
  height: 50px;
  display: flex;
  align-items: center;
  color: white;
}

.nav-links {
  display: flex;
}

.nav-link {
  padding: 0 20px;
  color: white;
  text-decoration: none;
  height: 50px;
  line-height: 50px;
  transition: background-color 0.2s;
}

.nav-link:hover,
.nav-link.active {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 面包屑导航样式 */
.breadcrumb {
  padding: 10px 20px;
  color: #666;
  background-color: white;
  font-size: 12px;
}

.breadcrumb .active {
  color: #333;
  font-weight: 500;
}

.file-list-container {
  padding: 20px;
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

/* 表格样式 */
.table-header {
  background-color: #f0f2f5;
  border: 1px solid #ddd;
  border-bottom: none;
}

.header-row {
  display: flex;
  font-weight: 500;
}

.header-cell {
  padding: 12px 8px;
  flex: 1;
  min-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-right: 1px solid #ddd;
}

.header-cell:last-child {
  border-right: none;
  flex: 0.5;
  min-width: 120px;
}

.file-rows {
  background-color: #fff;
  border: 1px solid #ddd;
}

.file-row {
  display: flex;
  border-bottom: 1px solid #eee;
}

.file-row:last-child {
  border-bottom: none;
}

.file-cell {
  padding: 12px 8px;
  flex: 1;
  min-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-right: 1px solid #eee;
}

.file-cell:last-child {
  border-right: none;
  flex: 0.5;
  min-width: 120px;
}

.operation {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.operation-link {
  color: #5a8de1;
  text-decoration: none;
  cursor: pointer;
}

.operation-link:hover {
  text-decoration: underline;
}

.no-data {
  text-align: center;
  padding: 30px;
  color: #999;
  background-color: #fff;
  border: 1px solid #ddd;
}
</style>
