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

  api.get('/corpus/my-corpus', { params })
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
      console.error('获取文件列表失败:', error)
      ElMessage.error('获取文件列表失败，请稍后重试')
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
async function downloadFile(corpus) {
  // 添加下载状态
  const corpusIndex = fileList.value.findIndex(c => c.corpusId === corpus.corpusId)
  if (corpusIndex !== -1) {
    fileList.value[corpusIndex].downloading = true
  }

  try {
    // 确定文件名
    let fileName = corpus.collectionName || `语料库_${corpus.corpusId}`

    // 构建下载URL
    const downloadUrl = `${api.defaults.baseURL}/corpus/download/${corpus.corpusId}`

    console.log('开始下载语料库:', fileName, downloadUrl)
    console.log('下载URL:', downloadUrl)

    // 方法1: 使用fetch获取blob，然后创建下载链接
    try {
      console.log('尝试使用fetch方式下载...')

      const response = await api.get(`/corpus/download/${corpus.corpusId}`, {
        responseType: 'blob'
      })

      console.log('下载响应:', response)

      // 创建blob URL
      const blob = new Blob([response.data])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', corpus.collectionName)
      link.download = fileName + '.zip'
      link.style.display = 'none'

      // 添加到DOM并点击
      document.body.appendChild(link)
      link.click()

      // 清理
      document.body.removeChild(link)

      window.URL.revokeObjectURL(url)

      console.log('下载链接已触发')
      ElMessage.success('下载已开始，请查看浏览器下载管理器')

    } catch (fetchError) {
      console.error('Fetch下载失败:', fetchError)

      // 方法2: 回退到window.open方式
      console.log('回退到window.open方式...')

      const downloadWindow = window.open(downloadUrl, '_blank')

      if (!downloadWindow) {
        console.log('弹窗被拦截，使用iframe方式...')
        // 如果被拦截，尝试使用iframe方式
        const iframe = document.createElement('iframe')
        iframe.style.display = 'none'
        iframe.src = downloadUrl
        document.body.appendChild(iframe)

        // 5秒后移除iframe
        setTimeout(() => {
          if (document.body.contains(iframe)) {
            document.body.removeChild(iframe)
          }
        }, 5000)
      } else {
        console.log('window.open成功')
        // 如果成功打开，等待一下后关闭窗口（下载会继续）
        setTimeout(() => {
          try {
            downloadWindow.close()
          } catch (e) {
            console.log('关闭窗口失败:', e)
          }
        }, 1000)
      }

      ElMessage.success('下载已开始，请查看浏览器下载管理器')
    }

  } catch (error) {
    console.error('下载语料失败:', error)

    // 根据错误类型显示不同的错误信息
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        ElMessage.error('用户未登录，请重新登录')
      } else if (status === 403) {
        ElMessage.error('无权限下载该语料')
      } else if (status === 404) {
        ElMessage.error('语料不存在或暂无文件')
      } else {
        ElMessage.error(`下载失败: ${error.response.data || error.message}`)
      }
    } else {
      ElMessage.error(`下载失败: ${error.message}`)
    }
  } finally {
    // 重置下载状态
    if (corpusIndex !== -1) {
      fileList.value[corpusIndex].downloading = false
    }
  }
}

// 查看详情
function viewDetails(corpus) {
  // 跳转到详情页面，传递语料ID作为参数
  router.push(`/corpus-details/${corpus.corpusId}`)
}

// 跳转到上传页面
function goToUpload() {
  router.push('/file-upload')
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
