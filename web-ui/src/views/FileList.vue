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

      <!-- 语料表格 -->
      <el-table v-loading="loading" :data="fileList" style="width: 100%">
        <el-table-column prop="country" label="国家" />
        <el-table-column prop="collectionName" label="语料集名称" />
        <el-table-column prop="domain" label="所属领域" />
        <el-table-column prop="language" label="语种" />
        <el-table-column prop="dataFormat" label="数据形式" />
        <el-table-column prop="classification" label="数据分类" />
        <el-table-column prop="dataYear" label="数据年份" />
        <el-table-column prop="sourceLocation" label="来源归属地" />
        <el-table-column prop="dataSource" label="数据来源" />
        <el-table-column prop="remarks" label="备注说明" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="downloadFile(row)">下载</el-button>
            <el-button link type="primary" @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无数据">
            <el-button type="primary" @click="goToUpload">上传文件</el-button>
          </el-empty>
        </template>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Plus } from '@element-plus/icons-vue'
import api from '../services/api'

const router = useRouter()
const fileList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取全局面包屑管理工具
const breadcrumb = inject('breadcrumb')

onMounted(() => {
  // 设置当前页面的面包屑
  breadcrumb.setBreadcrumb([
    { title: '首页', path: '/' },
    { title: '语料清单', path: '/file-list' }
  ])
  loadFileList()
})

// 加载语料列表
function loadFileList() {
  loading.value = true
  const params = {
    page: currentPage.value,
    size: pageSize.value
  }

  api.get('/hdfs/corpus/my-corpus', { params })
    .then(response => {
      if (response.data && response.data.records) {
        fileList.value = response.data.records
        total.value = response.data.total || response.data.records.length
      } else {
        fileList.value = []
        total.value = 0
        ElMessage.warning('返回数据格式不正确')
      }
    })
    .catch(error => {
      console.error('获取语料列表失败:', error)
      if (error.response?.status === 401) {
        ElMessage.error('请先登录')
      } else {
        ElMessage.error('获取语料列表失败，请稍后重试')
      }
      fileList.value = []
      total.value = 0
    })
    .finally(() => {
      loading.value = false
    })
}

// 处理页面大小变化
function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1 // 重置到第一页
  loadFileList()
}

// 处理页码变化
function handleCurrentChange(newPage) {
  currentPage.value = newPage
  loadFileList()
}

// 下载语料
function downloadFile(corpus) {
  ElMessage.info(`开始下载语料: ${corpus.collectionName}`)

  api({
    url: `/hdfs/corpus/download/${corpus.corpusId}`,
    method: 'GET',
    responseType: 'blob'
  })
    .then(response => {
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', corpus.collectionName)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('下载完成')
    })
    .catch(error => {
      console.error('下载语料失败:', error)
      ElMessage.error('下载语料失败，请稍后重试')
    })
}

// 查看详情
function viewDetails(corpus) {
  // 跳转到详情页面，传递语料ID作为参数
  router.push(`/corpus-details/${corpus.corpusId}`)
}

// 跳转到上传页面
function goToUpload() {
  router.push('/upload')
}
</script>

<style scoped>
.file-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
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
  color: #409eff;
  font-weight: bold;
}

/* 操作栏 */
.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
