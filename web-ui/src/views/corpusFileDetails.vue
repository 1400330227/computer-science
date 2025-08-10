<template>
    <div class="corpus-details-page">
        <div class="corpus-details-container">
            <!-- 语料详情信息 -->
            <div v-loading="loading" class="section-container">
                <h3 class="section-title">语料详情信息</h3>
                <div class="corpus-edit-form">
                    <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="140px"
                        class="edit-form-grid">
                        <el-form-item label="国家" prop="country">
                            <el-input v-model="editForm.country" placeholder="请填写国家" />
                        </el-form-item>
                        <el-form-item label="语料集名称" prop="collectionName">
                            <el-input v-model="editForm.collectionName" placeholder="请填写语料集名称" />
                        </el-form-item>
                        <el-form-item label="所属领域" prop="domain">
                            <el-input v-model="editForm.domain" placeholder="请输入所属领域" />
                        </el-form-item>
                        <el-form-item label="语种" prop="language">
                            <el-input v-model="editForm.language" placeholder="请输入语种" />
                        </el-form-item>
                        <el-form-item label="数据形式" prop="dataFormat">
                            <el-input v-model="editForm.dataFormat" placeholder="例如：文本、语音" />
                        </el-form-item>
                        <el-form-item label="数据分类" prop="classification">
                            <el-input v-model="editForm.classification" placeholder="请输入数据分类" />
                        </el-form-item>
                        <el-form-item label="数据量" prop="dataVolume">
                            <el-input v-model.number="editForm.dataVolume" type="number" placeholder="请输入数据量" />
                        </el-form-item>
                        <el-form-item label="数据量单位" prop="volumeUnit">
                            <el-input v-model="editForm.volumeUnit" placeholder="如：条、份、GB、小时" />
                        </el-form-item>
                        <el-form-item label="容量估算(GB)" prop="estimatedCapacityGb">
                            <el-input v-model="editForm.estimatedCapacityGb" type="number" placeholder="请输入容量估算" />
                        </el-form-item>
                        <el-form-item label="数据年份" prop="dataYear">
                            <el-date-picker v-model="editForm.dataYear" type="year" placeholder="请选择数据年份" format="YYYY"
                                value-format="YYYY" />
                        </el-form-item>
                        <el-form-item label="来源归属地" prop="sourceLocation">
                            <el-input v-model="editForm.sourceLocation" placeholder="请输入来源归属地" />
                        </el-form-item>
                        <el-form-item label="数据来源" prop="dataSource">
                            <el-input v-model="editForm.dataSource" placeholder="请输入数据来源" />
                        </el-form-item>
                        <el-form-item label="数据提供方" prop="provider">
                            <el-input v-model="editForm.provider" placeholder="请输入数据提供方" />
                        </el-form-item>
                        <el-form-item label="提供方联系方式" prop="providerContact">
                            <el-input v-model="editForm.providerContact" placeholder="联系方式（手机号或座机号：区号-电话号码）" />
                        </el-form-item>
                        <el-form-item label="备注说明" class="full-row">
                            <el-input v-model="editForm.remarks" type="textarea" :rows="3" placeholder="请输入备注说明" />
                        </el-form-item>
                    </el-form>
                </div>
            </div>

            <!-- 文件详情信息 -->
            <div v-loading="filesLoading" class="section-container">
                <h3 class="section-title">文件详情信息</h3>
                <div class="file-list" v-if="fileList.length > 0">
                    <div v-for="(file, index) in fileList" :key="index" class="file-item">
                        <div class="file-name">
                            <el-icon :size="16" :class="getFileIconClass(file.fileName)">
                                <component :is="getFileIcon(file.fileName)" />
                            </el-icon>
                            {{ file.fileName }}
                        </div>
                        <div class="file-actions">
                            <span class="file-size" v-if="file.fileSize">{{ formatFileSize(file.fileSize) }}</span>
                            <a :href="getFileDownloadUrl(file)" class="file-download-link"
                                @click="showFileDownloadMessage(file)" title="下载文件" download>
                                <el-icon>
                                    <Download />
                                </el-icon>
                                下载
                            </a>
                        </div>
                    </div>
                </div>
                <el-empty v-else description="暂无文件" />
                <div class="file-upload-bar">
                    <el-upload class="upload-inline" :auto-upload="false" :file-list="uploadFiles"
                        :on-change="handleUploadChange" :on-remove="handleUploadRemove" multiple>
                        <el-button type="primary">选择文件</el-button>
                        <el-button type="success" :disabled="uploadFiles.length === 0" :loading="uploading"
                            @click="uploadSelectedFiles">开始上传</el-button>
                    </el-upload>

                </div>


            </div>

            <div class="section-actions">
                <el-button @click="cancelEdit">返回上一页</el-button>
                <el-button type="primary" :loading="saving" @click="saveCorpusDetails">更新语料信息</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { Download, Back, Document, Folder, Headset, VideoCamera, Picture } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../services/api'
import corpus from '../assets/corpus.json'

const route = useRoute()
const router = useRouter()
const corpusId = ref(route.params.id)
const corpusData = ref({})
const fileList = ref([])
const loading = ref(false)
const filesLoading = ref(false)

// 编辑相关
const editing = ref(false)
const saving = ref(false)
const editForm = ref({})
const editFormRef = ref(null)

// 校验相关（与上传页保持一致的校验方式）
const countries = corpus.countries.sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
const validCountryNames = new Set(countries.map(country => country.name))

const rules = {
    country: [
        { required: true, message: '请输入国家', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (!value) {
                    callback()
                    return
                }
                const isValid = validCountryNames.has(String(value).trim())
                if (!isValid) {
                    callback(new Error('请输入合法国家名称'))
                } else {
                    callback()
                }
            },
            trigger: 'change'
        }
    ],
    collectionName: [
        { required: true, message: '请输入语料集名称', trigger: 'blur' }
    ],
    domain: [
        { required: true, message: '请输入所属领域', trigger: 'blur' }
    ],
    language: [
        { required: true, message: '请输入语种', trigger: 'blur' }
    ],
    dataFormat: [
        { required: true, message: '请输入数据形式', trigger: 'blur' }
    ],
    classification: [
        { required: true, message: '请输入数据分类', trigger: 'blur' }
    ],
    dataVolume: [
        { required: true, message: '请填写数据量', trigger: 'blur' }
    ],
    volumeUnit: [
        { required: true, message: '请填写数据量单位', trigger: 'blur' }
    ],
    estimatedCapacityGb: [
        { required: false, message: '请填写容量估算', trigger: 'blur' }
    ],
    dataYear: [
        { required: true, message: '请输入数据年份', trigger: 'blur' }
    ],
    sourceLocation: [
        { required: true, message: '请输入来源归属地', trigger: 'blur' }
    ],
    dataSource: [
        { required: true, message: '请输入数据来源', trigger: 'blur' }
    ],
    provider: [
        { required: true, message: '请输入数据提供方', trigger: 'blur' }
    ],
    providerContact: [
        { required: true, message: '请输入数据提供方联系方式', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                const mobilePattern = /^1[3-9]\d{9}$/
                const landlinePattern = /^(0\d{2,3})-?(\d{7,8})$/

                if (!value) {
                    callback()
                } else if (mobilePattern.test(value) || landlinePattern.test(value)) {
                    callback()
                } else {
                    callback(new Error('请输入有效的手机号或座机号（座机格式：区号-电话号码）'))
                }
            },
            trigger: 'blur'
        }
    ]
}

// 文件上传相关（详情页追加上传）
const uploadFiles = ref([])
const uploading = ref(false)

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
            // 进入页面时为编辑表单赋初值
            editForm.value = {
                country: response.data.country || '',
                collectionName: response.data.collectionName || '',
                domain: response.data.domain || '',
                language: response.data.language || '',
                dataFormat: response.data.dataFormat || '',
                classification: response.data.classification || '',
                dataVolume: response.data.dataVolume ?? null,
                volumeUnit: response.data.volumeUnit || '',
                estimatedCapacityGb: response.data.estimatedCapacityGb || '',
                dataYear: response.data.dataYear || '',
                sourceLocation: response.data.sourceLocation || '',
                dataSource: response.data.dataSource || '',
                provider: response.data.provider || '',
                providerContact: response.data.providerContact || '',
                remarks: response.data.remarks || ''
            }
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

// 进入编辑模式
function startEdit() {
    editing.value = true
    // 浅拷贝一份用于编辑
    editForm.value = {
        country: corpusData.value.country || '',
        collectionName: corpusData.value.collectionName || '',
        domain: corpusData.value.domain || '',
        language: corpusData.value.language || '',
        dataFormat: corpusData.value.dataFormat || '',
        classification: corpusData.value.classification || '',
        dataVolume: corpusData.value.dataVolume ?? null,
        volumeUnit: corpusData.value.volumeUnit || '',
        estimatedCapacityGb: corpusData.value.estimatedCapacityGb || '',
        dataYear: corpusData.value.dataYear || '',
        sourceLocation: corpusData.value.sourceLocation || '',
        dataSource: corpusData.value.dataSource || '',
        provider: corpusData.value.provider || '',
        providerContact: corpusData.value.providerContact || '',
        remarks: corpusData.value.remarks || ''
    }
}

function cancelEdit() {
    router.go(-1)
}

async function saveCorpusDetails() {
    if (saving.value) return

    // 先进行表单校验
    try {
        await editFormRef.value.validate()
    } catch (validationError) {
        ElMessage.error('请检查填写信息')
        return
    }

    saving.value = true
    try {
        const payload = { ...editForm.value }
        // 尝试 RESTful 更新
        await api.put(`/corpus/${corpusId.value}`, payload)
        ElMessage.success('保存成功')
        editing.value = false
        loadCorpusDetails()
    } catch (error) {
        // 兼容性：如果后端不支持 PUT，尝试 POST 到可能的更新地址
        if (error.response?.status === 404) {
            try {
                await api.post('/corpus/update', { corpusId: corpusId.value, ...editForm.value })
                ElMessage.success('保存成功')
                editing.value = false
                loadCorpusDetails()
                saving.value = false
                return
            } catch (e2) {
                console.error('更新失败:', e2)
                ElMessage.error('更新失败，请稍后重试')
            }
        } else {
            console.error('更新失败:', error)
            ElMessage.error('更新失败，请稍后重试')
        }
    } finally {
        saving.value = false
    }
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
            // 直接设置文件列表
            fileList.value = response.data || []
            filesLoading.value = false
        })
        .catch(error => {
            console.error('获取文件列表失败:', error)
            ElMessage.error('获取文件列表失败，请稍后重试')
            filesLoading.value = false
        })
}

// 详情页上传：选择/移除
function handleUploadChange(file, uploadFileList) {
    uploadFiles.value = uploadFileList
}
function handleUploadRemove(file, uploadFileList) {
    uploadFiles.value = uploadFileList
}

// 执行上传
async function uploadSelectedFiles() {
    if (uploading.value || uploadFiles.value.length === 0) return
    uploading.value = true
    try {
        for (const f of uploadFiles.value) {
            if (!f.raw) continue
            const form = new FormData()
            form.append('file', f.raw)
            form.append('corpusId', corpusId.value)
            await api.post('/corpus/upload', form, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            ElMessage.success(`文件 ${f.name} 上传成功`)
        }
        // 清空本地选择并刷新
        uploadFiles.value = []
        loadFileList()
    } catch (error) {
        console.error('文件上传失败:', error)
        ElMessage.error('文件上传失败，请稍后重试')
    } finally {
        uploading.value = false
    }
}

// 返回上一页
function goBack() {
    router.go(-1)
}

// 计算文件下载URL
function getFileDownloadUrl(file) {
    return `/api/files/${file.fileId}/download`
}

// 显示文件下载消息（不阻止默认的链接行为）
function showFileDownloadMessage(file) {
    // 不使用 event.preventDefault()，让 <a> 标签的默认下载行为正常执行
    ElMessage.success(`正在下载文件: ${file.fileName}`);
}

// 根据文件名选择图标
function getFileIcon(fileName) {
    const lowerCaseFileName = fileName.toLowerCase();
    if (lowerCaseFileName.endsWith('.txt') || lowerCaseFileName.endsWith('.text')) {
        return Document;
    } else if (lowerCaseFileName.endsWith('.pdf')) {
        return Document;
    } else if (lowerCaseFileName.endsWith('.doc') || lowerCaseFileName.endsWith('.docx')) {
        return Document;
    } else if (lowerCaseFileName.endsWith('.xls') || lowerCaseFileName.endsWith('.xlsx')) {
        return Document;
    } else if (lowerCaseFileName.endsWith('.ppt') || lowerCaseFileName.endsWith('.pptx')) {
        return Document;
    } else if (lowerCaseFileName.endsWith('.zip') || lowerCaseFileName.endsWith('.rar')) {
        return Folder;
    } else if (lowerCaseFileName.endsWith('.mp3') || lowerCaseFileName.endsWith('.wav')) {
        return Headset;
    } else if (lowerCaseFileName.endsWith('.mp4') || lowerCaseFileName.endsWith('.avi')) {
        return VideoCamera;
    } else if (lowerCaseFileName.endsWith('.jpg') || lowerCaseFileName.endsWith('.jpeg') || lowerCaseFileName.endsWith('.png') || lowerCaseFileName.endsWith('.gif')) {
        return Picture;
    } else {
        return Document; // 默认图标
    }
}

// 根据文件名选择图标类
function getFileIconClass(fileName) {
    const lowerCaseFileName = fileName.toLowerCase();
    if (lowerCaseFileName.endsWith('.txt') || lowerCaseFileName.endsWith('.text')) {
        return 'el-icon-document';
    } else if (lowerCaseFileName.endsWith('.pdf')) {
        return 'el-icon-document';
    } else if (lowerCaseFileName.endsWith('.doc') || lowerCaseFileName.endsWith('.docx')) {
        return 'el-icon-document';
    } else if (lowerCaseFileName.endsWith('.xls') || lowerCaseFileName.endsWith('.xlsx')) {
        return 'el-icon-document';
    } else if (lowerCaseFileName.endsWith('.ppt') || lowerCaseFileName.endsWith('.pptx')) {
        return 'el-icon-document';
    } else if (lowerCaseFileName.endsWith('.zip') || lowerCaseFileName.endsWith('.rar')) {
        return 'el-icon-folder';
    } else if (lowerCaseFileName.endsWith('.mp3') || lowerCaseFileName.endsWith('.wav')) {
        return 'el-icon-headset';
    } else if (lowerCaseFileName.endsWith('.mp4') || lowerCaseFileName.endsWith('.avi')) {
        return 'el-icon-video-camera';
    } else if (lowerCaseFileName.endsWith('.jpg') || lowerCaseFileName.endsWith('.jpeg') || lowerCaseFileName.endsWith('.png') || lowerCaseFileName.endsWith('.gif')) {
        return 'el-icon-picture';
    } else {
        return 'el-icon-document'; // 默认图标
    }
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
    /* font-size: 18px; */
    /* font-weight: bold; */
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 12px;
}

.section-actions {
    display: flex;
    gap: 10px;
    justify-content: center;
    margin-bottom: 12px;
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

/* 编辑表单布局 */
.corpus-edit-form {
    padding: 16px;
}

.edit-form-grid>>>.el-form-item {
    margin-right: 16px;
}

.edit-form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(280px, 1fr));
    gap: 8px 24px;
}

.edit-form-grid .full-row {
    grid-column: 1 / -1;
}

.file-list {
    /* border: 1px solid #ebeef5; */
    /* border-radius: 4px; */
}

.file-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 10px;
    border-bottom: 1px solid #ebeef5;
}

.file-item:last-child {
    border-bottom: none;
}

.file-name {
    font-size: 16px;
    color: #333;
    display: flex;
    align-items: center;
    gap: 8px;
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

.file-download-link {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 5px 10px;
    /* background-color: #409eff; */
    color: #409eff;
    border-radius: 4px;
    text-decoration: none;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.file-download-link:hover {
    background-color: #66b1ff;
    color: #ffffff;
}

.file-download-link.downloading {
    background-color: #909399;
    cursor: not-allowed;
}

.file-upload-bar {
    /* display: flex; */
    /* align-items: center; */
    /* gap: 12px; */
    margin-bottom: 12px;
}

.upload-inline {
    /* display: inline-block; */
}
</style>