<template>
  <div class="file-list-page">
    <div class="search-container">
      <el-form :model="searchForm" :inline="true" label-position="left" label-width="auto" @submit.prevent>
        <el-form-item label="语料集">
          <el-input v-model="searchForm.collectionName" placeholder="请输入语料名称" prefix-icon="Search" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <!-- <el-form-item label="国家">
          <el-select v-model="searchForm.country" placeholder="选择国家" clearable style="width: 200px">
            <el-option label="中国" value="中国" />
            <el-option label="泰国" value="泰国" />
            <el-option label="老挝" value="老挝" />
            <el-option label="越南" value="越南" />
            <el-option label="缅甸" value="缅甸" />
            <el-option label="柬埔寨" value="柬埔寨" />
            <el-option label="马来西亚" value="马来西亚" />
            <el-option label="新加坡" value="新加坡" />
            <el-option label="印度尼西亚" value="印度尼西亚" />
            <el-option label="菲律宾" value="菲律宾" />
            <el-option label="文莱" value="文莱" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="语言">
          <el-select v-model="searchForm.language" placeholder="选择语言" clearable style="width: 200px">
            <el-option label="中文" value="中文" />
            <el-option label="泰国" value="泰国" />
            <el-option label="老挝语言" value="老挝语言" />
            <el-option label="英文" value="英文" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="分类">
          <el-select v-model="searchForm.classification" placeholder="选择分类" clearable style="width: 200px">
            <el-option label="预训练语料" value="预训练语料" />
            <el-option label="基础语料" value="基础语料" />
            <el-option label="专业语料" value="专业语料" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <div class="file-list-container">
        <!-- 语料表格 -->
        <el-table v-loading="loading" :data="fileList" style="width: 100%">
          <el-table-column prop="country" label="国家" />
          <el-table-column prop="collectionName" label="语料名称" min-width="140">
            <template #default="{ row }">
              <router-link :to="`/corpus-details/${row.corpusId}`">
                {{ row.collectionName }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="domain" label="所属领域" />
          <el-table-column prop="language" label="语言" />
<!--          <el-table-column prop="dataFormat" label="数据模态" />-->
          <el-table-column prop="classification" label="数据分类" />
          <el-table-column prop="dataYear" label="数据年份" />
          <el-table-column prop="createdAt" label="上传时间" width="110">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <!-- <el-table-column prop="dataSource" label="数据来源" /> -->
          <el-table-column prop="estimatedCapacityGb" label="容量估算GB" width="110" />
          <el-table-column label="文件数量">
            <template #default="{ row }">
              <span v-if="row.dataVolume !== undefined && row.dataVolume !== null">
                {{ row.dataVolume }} {{ row.volumeUnit }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="remarks" label="备注说明" show-overflow-tooltip /> -->
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <a :href="getDownloadUrl(row)" class="download-link" @click="showDownloadMessage(row)" title="下载语料"
                download>
                下载
              </a>
              <!-- <el-button link type="primary" @click="viewDetails(row)" style="margin-left: 10px;">详情</el-button>
              <el-button link type="danger" @click="confirmDeleteCorpus(row)" style="margin-left: 10px;">删除</el-button> -->
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
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../services/api'
import { deleteCorpus } from '../services/corpus'

const router = useRouter()
const fileList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单数据
const searchForm = ref({
  collectionName: '',
  country: '',
  language: '',
  classification: ''
})

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

  // 添加搜索参数
  if (searchForm.value.collectionName) {
    params.collectionName = searchForm.value.collectionName
  }
  if (searchForm.value.country) {
    params.country = searchForm.value.country
  }
  if (searchForm.value.language) {
    params.language = searchForm.value.language
  }
  if (searchForm.value.classification) {
    params.classification = searchForm.value.classification
  }

  api.get('/corpus/my-corpus', { params })
    .then(response => {
      if (response.data && response.data.records) {
        fileList.value = response.data.records
        total.value = response.data.total || 0
      } else if (Array.isArray(response.data)) {
        // 兼容可能的旧格式响应
        fileList.value = response.data
        total.value = response.data.length
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

// 处理搜索
function handleSearch() {
  currentPage.value = 1 // 搜索时重置到第一页
  loadFileList()
}

// 处理重置
function handleReset() {
  searchForm.value = {
    collectionName: '',
    country: '',
    language: '',
    classification: ''
  }
  currentPage.value = 1
  loadFileList()
}

// 日期格式化：YYYY-MM-DD
function formatDate(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
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

// 计算下载URL
function getDownloadUrl(corpus) {
  return `/api/corpus/download/${corpus.corpusId}`;
}

// 显示下载消息（不阻止默认的链接行为）
function showDownloadMessage(row) {
  // 不使用 event.preventDefault()，让 <a> 标签的默认下载行为正常执行
  ElMessage.success(`正在下载语料: ${row.collectionName}`);
}

// 确认删除语料
async function confirmDeleteCorpus(corpus) {
  try {
    await ElMessageBox.confirm(
      `确定要删除语料"${corpus.collectionName}"吗？\n\n删除后将无法恢复，包括所有相关文件。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )

    // 用户确认删除
    await deleteCorpusAction(corpus)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除操作失败')
    }
  }
}

// 执行删除语料
async function deleteCorpusAction(corpus) {
  try {
    const response = await deleteCorpus(corpus.corpusId)

    if (response.data.success) {
      ElMessage.success('语料删除成功')
      // 删除成功后刷新列表
      loadFileList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除语料失败:', error)
    ElMessage.error('删除语料失败，请稍后重试')
  }
}
</script>

<style scoped>
.file-list-page {
  max-width: 1200px;
  margin: 0 auto;
  background-color: #fff;
  padding: 20px;
}

.search-container {
  /* padding: 20px; */
  background-color: #fff;
}

.file-list-container {
  /* padding: 20px; */
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

/* 搜索区域 */
.search-container {
  /* background-color: #f5f7fa; */
  /* border: 1px solid #e4e7ed; */
  /* border-radius: 6px; */
  /* padding: 20px; */
  margin-bottom: 20px;
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

/* 下载链接样式 */
.download-link {
  display: inline-block;
  padding: 0;
  margin-right: 15px;
  color: #4169e1;
  text-decoration: none;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s ease;
  border: none;
  background: none;
}

.download-link:hover {
  color: #66b1ff;
}
</style>
