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
                                <!-- <el-form-item label="数据模态" prop="dataFormat">
                                    <el-select v-model="editForm.dataFormat" filterable placeholder="请选择数据模态" multiple>
                                        <el-option v-for="dataFormat in dataFormats" :key="dataFormat.dataFormat"
                                            :label="dataFormat.dataFormat" :value="dataFormat.dataFormat"></el-option>
                                    </el-select>
                                </el-form-item> -->


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
                                    <el-input v-model="editForm.dataSource" placeholder="提供具体数据网站或者描述数据具体来源，如广西大学新闻网：https://news.gxu.edu.cn/info/42978.htm" type="textarea"/>
                                </el-form-item>
                                <el-form-item label="数据提供方" prop="provider">
                                    <el-input v-model="editForm.provider" disabled placeholder="请输入数据提供方" />
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
                <div class="file-upload-bar">
                    <el-upload class="upload-inline" :auto-upload="false" :file-list="uploadFiles"
                        :on-change="handleUploadChange" :on-remove="handleUploadRemove" multiple>
                        <template #trigger>
                            <el-button>继续上传文件</el-button>
                        </template>
                        <el-button type="success" :disabled="uploadFiles.length === 0" :loading="uploading"
                            @click.stop="uploadSelectedFiles"
                            style="margin-left: 20px;top: -2px;position: relative;">上传</el-button>
                    </el-upload>

                </div>
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

                <!-- 标注文件上传 -->
                <div class="annotation-upload">
                    <el-form :inline="true" label-width="120px">
                        <el-form-item label="选择原始文件">
                            <el-select v-model="selectedOriginalFileId" placeholder="请选择要标注的文件" style="min-width: 260px">
                                <el-option v-for="file in fileList" :key="file.fileId" :label="file.fileName" :value="file.fileId" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="标注文件(.txt)">
                            <el-upload
                                class="upload-inline"
                                :auto-upload="false"
                                :limit="1"
                                :on-change="handleAnnotationUploadChange"
                                :file-list="annotationUploadList"
                                accept=".txt"
                            >
                                <template #trigger>
                                    <el-button>选择标注文件</el-button>
                                </template>
                                <el-button type="primary" :disabled="annotationUploadList.length === 0" :loading="annotationUploading"
                                           @click.stop="submitAnnotationUpload" style="margin-left: 12px;">
                                    上传标注
                                </el-button>
                            </el-upload>
                            <div class="upload-tip">文件名必须与原始文件主名一致，扩展名为 .txt；内容每3行一组问答。</div>
                        </el-form-item>
                    </el-form>
                </div>
            </div>

            <div class="section-actions">
                <el-button @click="cancelEdit">返回上一页</el-button>
                <el-button type="primary" :loading="saving" @click="saveCorpusDetails">更新语料信息</el-button>
                <el-button type="danger" :loading="deleting" @click="confirmDeleteCorpus">删除语料</el-button>
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
import { deleteCorpus } from '../services/corpus'
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
        { required: true, message: '请输入数据来源', trigger: 'blur' }
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

// 标注上传
const annotationUploadList = ref([])
const annotationUploading = ref(false)
const selectedOriginalFileId = ref(null)

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
    api.get(`/corpus/user/${corpusId.value}`)
        .then(response => {
            corpusData.value = response.data
            // 进入页面时为编辑表单赋初值
            editForm.value = {
                country: response.data.country || '',
                collectionName: response.data.collectionName || '',
                domain: response.data.domain || '',
                language: response.data.language || '',
                dataFormat: parseDataFormatToArray(response.data.dataFormat),
                classification: response.data.classification || '',
                dataVolume: response.data.dataVolume ?? null,
                volumeUnit: response.data.volumeUnit || '',
                estimatedCapacityGb: response.data.estimatedCapacityGb || 0,
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
        dataFormat: parseDataFormatToArray(corpusData.value.dataFormat),
        classification: corpusData.value.classification || '',
        dataVolume: corpusData.value.dataVolume ?? null,
        volumeUnit: corpusData.value.volumeUnit || '',
        estimatedCapacityGb: corpusData.value.estimatedCapacityGb || 0,
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

const dataFormatText = computed(() => {
    if (Array.isArray(editForm.dataFormat)) {
        return editForm.dataFormat.join('、');
    }
    return editForm.dataFormat || '';
})

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
        const submitData = { ...editForm.value }
        if (Array.isArray(submitData.dataFormat)) {
            if (submitData.dataFormat.length > 0) {
                submitData.dataFormat = submitData.dataFormat.join('、')
            } else {
                delete submitData.dataFormat
            }
        } else if (typeof submitData.dataFormat === 'string' && submitData.dataFormat.trim() === '') {
            delete submitData.dataFormat
        }
        const payload = { ...submitData }

        // 调试信息：检查发送的数据
        console.log('=== 详情页面发送数据调试信息 ===')
        console.log('payload:', payload)
        console.log('estimatedCapacityGb值:', payload.estimatedCapacityGb)
        console.log('estimatedCapacityGb类型:', typeof payload.estimatedCapacityGb)
        console.log('================================')

        // 尝试 RESTful 更新
        await api.put(`/corpus/${corpusId.value}`, payload)
        ElMessage.success('保存成功')
        editing.value = false
        loadCorpusDetails()
    } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败，请稍后重试')
    } finally {
        saving.value = false
    }
}

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
    api.get(`/files/corpus/${corpusId.value}`)
        .then(response => {
            // 直接设置文件列表
            fileList.value = response.data || []
            filesLoading.value = false
            updateDataMetricsFromFiles()
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

// 标注文件上传：选择
function handleAnnotationUploadChange(file, fileListParam) {
    annotationUploadList.value = fileListParam
    if (!file.name.toLowerCase().endsWith('.txt')) {
        ElMessage.error('请上传 .txt 格式的标注文件')
        annotationUploadList.value = []
    }
}

// 执行标注上传
async function submitAnnotationUpload() {
    if (annotationUploading.value) return
    if (!selectedOriginalFileId.value) {
        ElMessage.error('请选择要标注的原始文件')
        return
    }
    if (annotationUploadList.value.length === 0) {
        ElMessage.error('请先选择标注文件（.txt）')
        return
    }

    const f = annotationUploadList.value[0]
    if (!f.raw) {
        ElMessage.error('标注文件选择异常，请重新选择')
        return
    }

    annotationUploading.value = true
    try {
        const form = new FormData()
        form.append('file', f.raw)
        form.append('originalFileId', selectedOriginalFileId.value)
        const res = await api.post('/qa-annotations/upload', form, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
        ElMessage.success(`标注上传成功，问答对数量：${res.data.qaPairCount ?? '未知'}`)
        // 清理
        annotationUploadList.value = []
        selectedOriginalFileId.value = null
    } catch (error) {
        console.error('标注上传失败:', error)
        const msg = error.response?.data || '标注上传失败，请稍后重试'
        ElMessage.error(msg)
    } finally {
        annotationUploading.value = false
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

// 确认删除语料
async function confirmDeleteCorpus() {
    try {
        await ElMessageBox.confirm(
            `确定要删除语料"${corpusData.value.collectionName}"吗？\n\n删除后将无法恢复，包括所有相关文件。`,
            '确认删除',
            {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                type: 'warning',
                dangerouslyUseHTMLString: true
            }
        )

        // 用户确认删除
        await deleteCorpusAction()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除操作失败')
        }
    }
}

// 执行删除语料
async function deleteCorpusAction() {
    if (deleting.value) return

    deleting.value = true
    try {
        const response = await deleteCorpus(corpusId.value)

        if (response.data.success) {
            ElMessage.success('语料删除成功')
            // 删除成功后跳转到语料列表页面
            router.push('/file-list')
        } else {
            ElMessage.error(response.data.message || '删除失败')
        }
    } catch (error) {
        console.error('删除语料失败:', error)
        ElMessage.error('删除语料失败，请稍后重试')
    } finally {
        deleting.value = false
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

.annotation-upload {
    margin-top: 16px;
}

.upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 6px;
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
</style>
