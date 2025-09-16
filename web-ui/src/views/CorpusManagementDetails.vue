<template>
    <div class="corpus-details-page">
        <div class="corpus-details-container">
            <!-- 语料详情信息 -->
            <div v-loading="loading" class="section-container">
                <h3 class="section-title">语料详情信息</h3>

                <div class="corpus-edit-form">
                    <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="140px"
                        class="edit-form-grid">
                        <div class="form-content">
                            <div class="form-column">
                                <el-form-item label="国家" prop="country">
                                    <!-- <el-input v-model="editForm.country" placeholder="请填写国家" /> -->
                                    <el-select v-model="editForm.country" filterable placeholder="请选择国家">
                                        <el-option-group label="默认">
                                            <el-option v-for="country in defaultCountries" :key="country.code"
                                                :label="country.name" :value="country.name" />
                                        </el-option-group>
                                        <el-option-group label="东盟">
                                            <el-option v-for="country in aseanCountries" :key="country.code"
                                                :label="country.name" :value="country.name" />
                                        </el-option-group>
                                        <el-option-group label="其他">
                                            <el-option v-for="country in otherCountries" :key="country.code"
                                                :label="country.name" :value="country.name" />
                                        </el-option-group>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="语料名称" prop="collectionName">
                                    <el-input v-model="editForm.collectionName" placeholder="请填写语料名称" />
                                </el-form-item>
                                <el-form-item label="所属领域" prop="domain">
                                    <el-radio-group v-model="editForm.domain" class="domain-radio-group">
                                        <el-radio v-for="domain in domains" :key="domain.domainName"
                                            :label="domain.domainName" class="domain-radio-item">
                                            {{ domain.domainName }}
                                        </el-radio>
                                    </el-radio-group>
                                    <!-- <el-select v-model="editForm.domain" filterable placeholder="请选择所属领域">
                                        <el-option v-for="domain in domains" :key="domain.domainName"
                                            :label="domain.domainName" :value="domain.domainName"></el-option>
                                    </el-select> -->
                                </el-form-item>
                                <el-form-item label="语言" prop="language">
                                    <el-select v-model="editForm.language" filterable placeholder="请选择语言">
                                        <el-option v-for="language in languages" :key="language.language"
                                            :label="language.language" :value="language.language"></el-option>
                                    </el-select>
                                    <!-- <el-input v-model="editForm.language" placeholder="请输入语言" /> -->
                                </el-form-item>
                                <el-form-item label="数据模态" prop="dataFormat">
                                    <!-- <el-input v-model="editForm.dataFormat" placeholder="例如：文本、语音" /> -->
                                    <el-select v-model="editForm.dataFormat" filterable placeholder="请选择数据模态" multiple>
                                        <el-option v-for="dataFormat in dataFormats" :key="dataFormat.dataFormat"
                                            :label="dataFormat.dataFormat" :value="dataFormat.dataFormat"></el-option>
                                    </el-select>
                                </el-form-item>


                            </div>
                            <div class="form-column">
                                <el-form-item label="数据分类" prop="classification">
                                    <!-- <el-input v-model="editForm.classification" placeholder="请输入数据分类" /> -->
                                    <el-select v-model="editForm.classification" filterable placeholder="请选择数据分类">
                                        <el-option v-for="classification in classifications"
                                            :key="classification.classificationName"
                                            :label="classification.classificationName"
                                            :value="classification.classificationName"></el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="文件数量" prop="dataVolume">
                                    <el-input v-model.number="editForm.dataVolume" type="number" disabled
                                        placeholder="请输入文件数量" />
                                </el-form-item>
                                <!-- <el-form-item label="文件数量单位" prop="volumeUnit">
                                    <el-input v-model="editForm.volumeUnit" placeholder="如：条、份、GB、小时" />
                                </el-form-item> -->
                                <el-form-item label="容量估算(GB)" prop="estimatedCapacityGb">
                                    <el-input v-model="editForm.estimatedCapacityGb" type="number" disabled
                                        placeholder="请输入容量估算" />
                                </el-form-item>
                                <el-form-item label="数据年份" prop="dataYear">
                                    <el-date-picker v-model="editForm.dataYear" type="year" placeholder="请选择数据年份"
                                        format="YYYY" value-format="YYYY" />
                                </el-form-item>
                                <!-- <el-form-item label="数据来源机构" prop="sourceLocation">
                                    <el-input v-model="editForm.sourceLocation" placeholder="请输入数据来源机构" />
                                </el-form-item> -->
                                <el-form-item label="数据来源" prop="dataSource">
                                    <el-input v-model="editForm.dataSource" placeholder="提供具体数据网站或者具体来源描述" />
                                </el-form-item>
                                <el-form-item label="数据提供方" prop="provider">
                                    <el-input v-model="editForm.provider" placeholder="请输入数据提供方" />
                                </el-form-item>
                                <el-form-item label="提供方联系方式" prop="providerContact">
                                    <el-input v-model="editForm.providerContact" placeholder="联系方式（手机号或座机号：区号-电话号码）" />
                                </el-form-item>
                                <el-form-item label="备注说明" class="full-row">
                                    <el-input v-model="editForm.remarks" type="textarea" :rows="3"
                                        placeholder="请输入备注说明" />
                                </el-form-item>
                            </div>
                        </div>
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
            </div>

            <div class="section-actions">
                <el-button @click="cancelEdit">返回上一页</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, inject, computed, watch } from 'vue'
import { Download, Back, Document, Folder, Headset, VideoCamera, Picture } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../services/api'
import { deleteCorpus, getUserCorpusById, getCorpusByIdAsAdmin, getCorpusFilesAsAdmin, uploadFileToCorpusAsAdmin } from '../services/corpus'
import { useUserStore } from '../stores/user'
import corpus from '../assets/corpus.json'

const route = useRoute()
const router = useRouter()
const corpusId = ref(route.params.id)
const corpusData = ref({})
const fileList = ref([])
const loading = ref(false)
const filesLoading = ref(false)

// 用户store
const userStore = useUserStore()

// 编辑相关
const editing = ref(false)
const saving = ref(false)
const editForm = ref({})
const editFormRef = ref(null)

// 删除相关
const deleting = ref(false)


// 添加分组所需的常量与计算属性
const DEFAULT_COUNTRY_NAME = '中国'
const ASEAN_COUNTRY_NAMES = new Set([
    '文莱',
    '柬埔寨',
    '印度尼西亚',
    '印尼',
    '老挝',
    '马来西亚',
    '缅甸',
    '菲律宾',
    '新加坡',
    '泰国',
    '越南'
])

const defaultCountries = computed(() => countries.filter(c => c.name === DEFAULT_COUNTRY_NAME))
const aseanCountries = computed(() => countries.filter(c => c.name !== DEFAULT_COUNTRY_NAME && ASEAN_COUNTRY_NAMES.has(c.name)))
const otherCountries = computed(() => countries.filter(c => c.name !== DEFAULT_COUNTRY_NAME && !ASEAN_COUNTRY_NAMES.has(c.name)))
const domains = corpus.domains
const languages = corpus.languages
const classifications = corpus.classifications
const volumeUnits = corpus.volumeUnits
const dataFormats = corpus.dataFormats
// 基于国家名称到语言的映射
const countryNameToLanguage = new Map(corpus.countries.map(c => [c.name, c.language]))

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
        { required: true, message: '请输入语言', trigger: 'blur' }
    ],
    dataFormat: [
        { required: true, message: '请输入数据模态', trigger: 'blur' }
    ],
    classification: [
        { required: true, message: '请输入数据分类', trigger: 'blur' }
    ],
    dataVolume: [
        { required: false, message: '请填写文件数量', trigger: 'blur' }
    ],
    volumeUnit: [
        { required: true, message: '请填写文件数量单位', trigger: 'blur' }
    ],
    estimatedCapacityGb: [
        { required: false, message: '请填写容量估算', trigger: 'blur' }
    ],
    dataYear: [
        { required: true, message: '请输入数据年份', trigger: 'blur' }
    ],
    sourceLocation: [
        { required: false, message: '请输入数据来源机构', trigger: 'blur' }
    ],
    dataSource: [
        { required: false, message: '请输入数据来源', trigger: 'blur' }
    ],
    provider: [
        { required: false, message: '请输入数据提供方', trigger: 'blur' }
    ],
    providerContact: [
        { required: false, message: '请输入数据提供方联系方式', trigger: 'blur' },
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
    const breadcrumbItems = [
        { title: '首页', path: '/' }
    ]

    // 根据用户类型设置不同的面包屑路径
    breadcrumbItems.push(
        { title: '语料管理', path: '/corpus-management' },
        { title: '语料详情', path: `/corpus-management-details/${corpusId.value}` }
    )

    breadcrumb.setBreadcrumb(breadcrumbItems)

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

// 将 dataFormat 从字符串转换为数组供多选控件使用
function parseDataFormatToArray(value) {
    if (!value) return []
    if (Array.isArray(value)) return value
    const parts = String(value).split(/[、,，|]/).map(s => s.trim()).filter(Boolean)
    return parts
}

watch(
    () => editForm.value.country,
    (newCountry) => {
        if (!newCountry) return
        const languageByCountry = countryNameToLanguage.get(newCountry)
        if (!languageByCountry) return

        // 若语言为空或当前语言不属于所选国家，才进行覆盖
        if (!editForm.value.language || editForm.value.language !== languageByCountry) {
            editForm.value.language = languageByCountry
        }
    }
)

// 加载语料详情
function loadCorpusDetails() {
    loading.value = true

    // 根据用户类型选择不同的接口
    getCorpusByIdAsAdmin(corpusId.value).then(response => {
        editForm.value = response.data.data
        editForm.value.dataFormat = parseDataFormatToArray(editForm.value.dataFormat)
        loading.value = false
        // 加载文件列表
        loadFileList()
    }).catch(error => {
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


function cancelEdit() {
    router.go(-1)
}

const dataFormatText = computed(() => {
    if (Array.isArray(editForm.dataFormat)) {
        return editForm.dataFormat.join('、');
    }
    return editForm.dataFormat || '';
})

// 格式化文件数量显示
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
// 基于当前文件列表自动计算文件数量与容量估算（GB）
function updateDataMetricsFromFiles() {
    try {
        const files = Array.isArray(fileList.value) ? fileList.value : []
        // 文件数量 = 文件数量
        editForm.value.dataVolume = files.length
        // 容量估算 = fileSize 求和（字节）转 GB
        let totalBytes = 0
        for (const f of files) {
            if (typeof f.fileSize === 'number' && !Number.isNaN(f.fileSize)) {
                totalBytes += f.fileSize
            }
        }
        if (totalBytes > 0) {
            const gb = totalBytes / (1024 * 1024 * 1024)
            editForm.value.estimatedCapacityGb = parseFloat(gb.toFixed(6))
        }
    } catch (e) {
        console.warn('自动更新文件数量失败：', e)
    }
}
// 加载文件列表
function loadFileList() {
    filesLoading.value = true
    getCorpusFilesAsAdmin(corpusId.value).then(response => {
        // 直接设置文件列表
        fileList.value = response.data.data || []
        filesLoading.value = false
        updateDataMetricsFromFiles()
    }).catch(error => {
        console.error('获取文件列表失败:', error)
        ElMessage.error('获取文件列表失败，请稍后重试')
        filesLoading.value = false
    })
}


// 计算文件下载URL
function getFileDownloadUrl(file) {
    return `/api/admin/download/${file.fileId}/`
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
    /* padding: 16px; */
}

.edit-form-grid>>>.el-form-item {
    margin-right: 16px;
}

.edit-form-grid {
    /* display: grid; */
    /* grid-template-columns: repeat(2, minmax(280px, 1fr)); */
    /* gap: 8px 24px; */
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

.form-content {
    display: flex;
    flex-wrap: wrap;
}

.form-column {
    flex: 1;
    min-width: 300px;
    padding: 0 15px;
}

.domain-radio-group {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 4px;
    width: 100%;
}

.domain-radio-item {
    margin: 0 !important;
    padding: 3px 1px;
    border: 1px solid #e4e7ed;
    border-radius: 2px;
    background-color: #fafafa;
    transition: all 0.3s ease;
    min-height: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.domain-radio-item:hover {
    background-color: #f0f9ff;
    border-color: #409eff;
}

.domain-radio-item.is-checked {
    background-color: #ecf5ff;
    border-color: #409eff;
    color: #409eff;
}

.domain-radio-item .el-radio__label {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 100%;
    font-size: 10px;
    line-height: 1.0;
    padding: 0;
    margin-left: 0;
}

/* 隐藏默认的单选框样式 */
.domain-radio-item .el-radio__input {
    display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .domain-radio-group {
        grid-template-columns: repeat(2, 1fr);
        gap: 3px;
    }

    .domain-radio-item {
        padding: 2px 1px;
        font-size: 9px;
        min-height: 16px;
    }
}

@media (max-width: 480px) {
    .domain-radio-group {
        grid-template-columns: repeat(1, 1fr);
        gap: 2px;
    }

    .domain-radio-item {
        padding: 1px 1px;
        font-size: 8px;
        min-height: 14px;
    }
}

/* 创建者信息样式 */
.creator-info {
    background-color: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
}

.creator-title {
    font-size: 16px;
    font-weight: 600;
    color: #495057;
    margin: 0 0 12px 0;
    padding-bottom: 8px;
    border-bottom: 1px solid #dee2e6;
}

.creator-details {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 12px;
}

.creator-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.creator-item .label {
    font-weight: 500;
    color: #6c757d;
    min-width: 60px;
}

.creator-item .value {
    color: #212529;
    font-weight: 500;
}

@media (max-width: 768px) {
    .creator-details {
        grid-template-columns: 1fr;
        gap: 8px;
    }

    .creator-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
    }

    .creator-item .label {
        min-width: auto;
    }
}
</style>
