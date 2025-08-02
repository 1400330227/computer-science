<template>
    <div class="corpus-details-page">
        <div class="corpus-details-container">
            <div class="page-header">
                <div class="action-bar">
                    <el-button @click="goBack" type="default">
                        <el-icon>
                            <Back />
                        </el-icon>
                        返回
                    </el-button>
                </div>
            </div>

            <!-- 语料详情信息 -->
            <div v-loading="loading" class="section-container">
                <h2 class="section-title">语料详情信息</h2>
                <div class="corpus-info-table">
                    <table class="info-table">
                        <tr>
                            <td class="info-label">国家</td>
                            <td class="info-value">{{ corpusData.country || '-' }}</td>
                            <td class="info-label">语料集名称</td>
                            <td class="info-value">{{ corpusData.collectionName || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">所属领域</td>
                            <td class="info-value">{{ corpusData.domain || '-' }}</td>
                            <td class="info-label">语种</td>
                            <td class="info-value">{{ corpusData.language || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">数据形式</td>
                            <td class="info-value">{{ corpusData.dataFormat || '-' }}</td>
                            <td class="info-label">数据分类</td>
                            <td class="info-value">{{ corpusData.classification || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">数据量</td>
                            <td class="info-value">{{ formatDataVolume(corpusData.dataVolume, corpusData.volumeUnit) }}
                            </td>
                            <td class="info-label">容量估算（GB）</td>
                            <td class="info-value">{{ corpusData.estimatedCapacityGb || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">数据年份</td>
                            <td class="info-value">{{ corpusData.dataYear || '-' }}</td>
                            <td class="info-label">来源归属地</td>
                            <td class="info-value">{{ corpusData.sourceLocation || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">数据来源</td>
                            <td class="info-value">{{ corpusData.dataSource || '-' }}</td>
                            <td class="info-label">数据提供方</td>
                            <td class="info-value">{{ corpusData.provider || '-' }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">提供方联系方式</td>
                            <td class="info-value">{{ corpusData.providerContact || '-' }}</td>
                            <td class="info-label">创建时间</td>
                            <td class="info-value">{{ formatDateTime(corpusData.createdAt) }}</td>
                        </tr>
                        <tr>
                            <td class="info-label">备注说明</td>
                            <td class="info-value" colspan="3">{{ corpusData.remarks || '-' }}</td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- 文件详情信息 -->
            <div v-loading="filesLoading" class="section-container">
                <h2 class="section-title">文件详情信息</h2>
                <div class="file-list" v-if="fileList.length > 0">
                    <div v-for="(file, index) in fileList" :key="index" class="file-item">
                        <div class="file-name">{{ file.fileName }}</div>
                        <div class="file-actions">
                            <span class="file-size" v-if="file.fileSize">{{ formatFileSize(file.fileSize) }}</span>
                            <el-button
                                type="primary"
                                link
                                @click="downloadFile(file)"
                                :loading="file.downloading"
                                :disabled="file.downloading"
                            >
                                <el-icon>
                                    <component :is="file.downloading ? 'Loading' : 'Download'" />
                                </el-icon>下载
                            </el-button>
                        </div>
                    </div>
                </div>
                <el-empty v-else description="暂无文件" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { Download, Back, Loading } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../services/api'
import { startDownload } from '../utils/downloadManager'

const route = useRoute()
const router = useRouter()
const corpusId = ref(route.params.id)
const corpusData = ref({})
const fileList = ref([])
const loading = ref(false)
const filesLoading = ref(false)

// 获取全局面包屑管理工具
const breadcrumb = inject('breadcrumb')

onMounted(() => {
    // 设置当前页面的面包屑
    breadcrumb.setBreadcrumb([
        { title: '首页', path: '/' },
        { title: '语料清单', path: '/file-list' },
        { title: '语料详情', path: `/corpus-details/${corpusId.value}` }
    ])

    // 加载语料详情
    loadCorpusDetails()
})

// 格式化文件大小
function formatFileSize(size) {
    if (!size) return '未知大小'

    const kb = 1024
    const mb = kb * 1024
    const gb = mb * 1024

    if (size < kb) {
        return size + ' B'
    } else if (size < mb) {
        return (size / kb).toFixed(2) + ' KB'
    } else if (size < gb) {
        return (size / mb).toFixed(2) + ' MB'
    } else {
        return (size / gb).toFixed(2) + ' GB'
    }
}

// 加载语料详情
function loadCorpusDetails() {
    loading.value = true
    api.get(`/corpus/user/${corpusId.value}`)
        .then(response => {
            corpusData.value = response.data
            loading.value = false
            // 加载文件列表
            loadFileList()
        })
        .catch(error => {
            console.error('获取语料详情失败:', error)
            if (error.response?.status === 403) {
                ElMessage.error('无权限访问该语料信息')
            } else if (error.response?.status === 401) {
                ElMessage.error('请先登录')
            } else {
                ElMessage.error('获取语料详情失败，请稍后重试')
            }
            loading.value = false
        })
}

// 格式化数据量显示
function formatDataVolume(volume, unit) {
    if (!volume) return '-'
    return `${volume}${unit || ''}`
}

// 格式化日期时间显示
function formatDateTime(dateTime) {
    if (!dateTime) return '-'
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
}

// 加载文件列表
function loadFileList() {
    filesLoading.value = true
    api.get(`/files/corpus/${corpusId.value}`)
        .then(response => {
            // 为每个文件添加下载状态标志
            const files = response.data || []
            fileList.value = files.map(file => ({
                ...file,
                downloading: false
            }))
            filesLoading.value = false
        })
        .catch(error => {
            console.error('获取文件列表失败:', error)
            ElMessage.error('获取文件列表失败，请稍后重试')
            filesLoading.value = false
        })
}

// 下载文件
async function downloadFile(file) {
    if (file.downloading) return

    // 设置下载状态
    const fileIndex = fileList.value.findIndex(f => f.fileId === file.fileId)
    if (fileIndex !== -1) {
        fileList.value[fileIndex].downloading = true
    }

    try {
        // 构建下载URL
        const downloadUrl = `${api.defaults.baseURL}/hdfs/file?filePath=${encodeURIComponent(file.filePath)}&flag=true`

        // 使用下载管理器开始下载
        await startDownload(downloadUrl, file.fileName || file.name || 'download', {
            headers: {
                'Authorization': api.defaults.headers.common['Authorization'] || ''
            }
        })

        ElMessage.success('下载已开始，请查看右上角进度')

    } catch (error) {
        console.error('文件下载失败:', error)
        ElMessage.error('文件下载失败，请稍后重试')
    } finally {
        // 重置下载状态
        if (fileIndex !== -1) {
            fileList.value[fileIndex].downloading = false
        }
    }
}

// 返回上一页
function goBack() {
    router.go(-1)
}
</script>

<style scoped>
.corpus-details-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    background-color: #fff;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.action-bar {
    display: flex;
    gap: 10px;
}

.breadcrumb-nav {
    font-size: 14px;
    color: #666;
    margin-bottom: 20px;
}

.section-container {
    margin-bottom: 30px;
}

.section-title {
    font-size: 18px;
    font-weight: bold;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 20px;
}

.corpus-info-table {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;
}

.info-table {
    width: 100%;
    border-collapse: collapse;
}

.info-table tr {
    border-bottom: 1px solid #ebeef5;
}

.info-table tr:last-child {
    border-bottom: none;
}

.info-label {
    width: 150px;
    font-weight: bold;
    color: #333;
    text-align: right;
    padding: 12px 20px;
    background-color: #fafafa;
}

.info-value {
    color: #666;
    padding: 12px 20px;
}

.file-list {
    border: 1px solid #ebeef5;
    border-radius: 4px;
}

.file-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    border-bottom: 1px solid #ebeef5;
}

.file-item:last-child {
    border-bottom: none;
}

.file-name {
    font-size: 14px;
    color: #333;
}

.file-actions {
    display: flex;
    align-items: center;
    gap: 15px;
}

.file-size {
    color: #909399;
    font-size: 12px;
}

.file-download {
    cursor: pointer;
    color: #409eff;
}
</style>