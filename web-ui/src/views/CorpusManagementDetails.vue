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
                                 :label="country.name" :value="country.name"/>
                    </el-option-group>
                    <el-option-group label="东盟">
                      <el-option v-for="country in aseanCountries" :key="country.code"
                                 :label="country.name" :value="country.name"/>
                    </el-option-group>
                    <el-option-group label="其他">
                      <el-option v-for="country in otherCountries" :key="country.code"
                                 :label="country.name" :value="country.name"/>
                    </el-option-group>
                  </el-select>
                </el-form-item>
                <el-form-item label="语料名称" prop="collectionName">
                  <el-input v-model="editForm.collectionName" placeholder="请填写语料名称"/>
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
                  <el-select v-model="editForm.dataFormat" filterable placeholder="请选择数据模态"
                             multiple>
                    <el-option v-for="dataFormat in dataFormats" :key="dataFormat.dataFormat"
                               :label="dataFormat.dataFormat"
                               :value="dataFormat.dataFormat"></el-option>
                  </el-select>
                </el-form-item>


              </div>
              <div class="form-column">
                <el-form-item label="数据分类" prop="classification">
                  <!-- <el-input v-model="editForm.classification" placeholder="请输入数据分类" /> -->
                  <el-select v-model="editForm.classification" filterable
                             placeholder="请选择数据分类">
                    <el-option v-for="classification in classifications"
                               :key="classification.classificationName"
                               :label="classification.classificationName"
                               :value="classification.classificationName"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="文件数量" prop="dataVolume">
                  <el-input v-model.number="editForm.dataVolume" type="number" disabled
                            placeholder="请输入文件数量"/>
                </el-form-item>
                <!-- <el-form-item label="文件数量单位" prop="volumeUnit">
                    <el-input v-model="editForm.volumeUnit" placeholder="如：条、份、GB、小时" />
                </el-form-item> -->
                <el-form-item label="容量估算(GB)" prop="estimatedCapacityGb">
                  <el-input v-model="editForm.estimatedCapacityGb" type="number" disabled
                            placeholder="请输入容量估算"/>
                </el-form-item>
                <el-form-item label="数据年份" prop="dataYear">
                  <el-date-picker v-model="editForm.dataYear" type="year"
                                  placeholder="请选择数据年份"
                                  format="YYYY" value-format="YYYY"/>
                </el-form-item>
                <!-- <el-form-item label="数据来源机构" prop="sourceLocation">
                    <el-input v-model="editForm.sourceLocation" placeholder="请输入数据来源机构" />
                </el-form-item> -->
                <el-form-item label="数据来源" prop="dataSource">
                  <el-input v-model="editForm.dataSource"
                            placeholder="提供具体数据网站或者具体来源描述"/>
                </el-form-item>
                <el-form-item label="数据提供方" prop="provider">
                  <el-input v-model="editForm.provider" placeholder="请输入数据提供方"/>
                </el-form-item>
                <el-form-item label="提供方联系方式" prop="providerContact">
                  <el-input v-model="editForm.providerContact"
                            placeholder="联系方式（手机号或座机号：区号-电话号码）"/>
                </el-form-item>
                <el-form-item label="备注说明" class="full-row">
                  <el-input v-model="editForm.remarks" type="textarea" :rows="3"
                            placeholder="请输入备注说明"/>
                </el-form-item>
              </div>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 文件详情信息 -->
      <div v-loading="filesLoading" class="section-container">
        <div class="section-title">
          <h3>文件详情信息（标注文件内容要和模板相似，可在下方下载模板）</h3>
          <div style="float: right; margin-top: -38px">
            <el-button @click="downloadAllOriginalFiles">打包原始文件</el-button>
            <el-button @click="handleDownloadAllAnnotations">打包标注文件</el-button>
          </div>
        </div>

        <el-table :data="fileList" style="width: 100%" v-loading="loading">
          <!-- 原始文件名 -->
          <el-table-column prop="fileName" label="原始文件名" min-width="120">
            <template #default="{ row }">
              <i class="el-icon-document"></i> {{ row.fileName }}
              <a :href="getFileDownloadUrl(row)"
                 @click="showFileDownloadMessage(row)" title="下载文件" download>
                下载
              </a>
            </template>
          </el-table-column>

          <!-- 文件大小 -->
          <el-table-column prop="fileSize" label="大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>

          <!-- 标注状态 -->
          <el-table-column label="标注文件" min-width="120">
            <template #default="{ row }">
              <div v-for="annotationFile in row.annotationFiles" class="annotation-info"
                   :key="annotationFile.id">
                      <span class="text-truncate" :title="annotationFile.title">
                        {{ annotationFile.title }}
                      </span>
                <a :href="getAnnotationDownloadUrl(annotationFile.id)" style="margin-left: 4px"
                   @click="showAnnotationFileDownloadMessage(annotationFile)" title="下载文件"
                   download>
                  下载
                </a>
              </div>
            </template>
          </el-table-column>

          <!-- QA对数量 (从后端返回的数据中获取) -->
          <el-table-column prop="qaPairTotal" label="问答对" width="110"></el-table-column>

          <!-- 操作列 -->
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div>
                <el-button link @click="openUploadDialog(row)" type="primary">上传标注</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 隐藏的文件上传输入框 -->
        <input type="file" ref="annotationInputRef" style="display: none" multiple accept=".txt"
               @change="handleFileSelect"/>
      </div>

      <div class="section-actions">
        <el-button @click="downloadTemplate" type="primary">下载标注文件模板</el-button>
        <el-button @click="cancelEdit">返回上一页</el-button>
      </div>
    </div>
    <el-dialog v-model="uploadDialogVisible" title="上传标注文件" width="50%"
               :close-on-click-modal="false" @close="clearUploadData">
      <div class="upload-dialog-content">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          multiple
          action="#"
          :auto-upload="false"
          accept=".txt"
          :file-list="fileListToUpload"
          :before-upload="beforeAvatarUpload"
          :on-remove="handleFileRemove"
          :on-change="handleUploadChange"
        >
          <el-icon class="el-icon--upload">
            <upload-filled/>
          </el-icon>
          <div class="el-upload__text">
            <div>仅限 .txt 格式文件, 文件最大值10G，标注文件名和原文件名一致，支持多选，如：</div>
            <div>
              《{{ getBaseName(currentUploadRow?.fileName) }}》、
              《{{ getBaseName(currentUploadRow?.fileName) }}(1)》、
              《{{ getBaseName(currentUploadRow?.fileName) }}(...)》
            </div>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              当前选择：{{ totalSelectedFiles }} 个文件，共 {{ formatFileSize(totalSelectedSize) }}
            </div>
          </template>
        </el-upload>
        <div v-if="isUploading" class="upload-progress-section">
<!--          <div class="upload-status">-->
<!--            <el-icon class="uploading-icon" :class="{ 'success': uploadStatus === 'success', 'error': uploadStatus === 'error' }">-->
<!--              <component-->
<!--                :is="uploadStatus === 'success' ? 'CircleCheckFilled' :-->
<!--                      uploadStatus === 'error' ? 'CircleCloseFilled' : 'Loading'"-->
<!--              />-->
<!--            </el-icon>-->
<!--            <span class="status-text">-->
<!--              {{ getStatusText() }}-->
<!--            </span>-->
<!--          </div>-->

          <!-- 整体进度条 -->
          <div class="overall-progress">
            <div class="progress-header">
              <span>整体进度</span>
              <span>{{ uploadedFilesCount }}/{{ fileListToUpload.length }} 文件</span>
            </div>
            <el-progress
              :percentage="overallPercentage"
              :status="getProgressStatus()"
              :stroke-width="8"
              :show-text="false"
            />
            <div class="progress-info">
              <span>已上传：{{ formatFileSize(uploadedSize) }}/{{ formatFileSize(totalSelectedSize) }}</span>
              <span>{{ overallPercentage }}%</span>
            </div>
          </div>

          <!-- 当前文件上传详情 -->
          <div v-if="currentUploadFile" class="current-file-progress">
            <div class="current-file-header">
              <span>正在上传：{{ currentUploadFile.name }}</span>
              <span>{{ currentFilePercentage }}%</span>
            </div>
            <el-progress
              :percentage="currentFilePercentage"
              :stroke-width="6"
              :color="getProgressColor(currentFilePercentage)"
            />
            <div class="file-progress-info">
              <span>大小：{{ formatFileSize(currentUploadFile.size) }}</span>
              <span v-if="uploadSpeed > 0">速度：{{ formatFileSize(uploadSpeed) }}/s</span>
              <span v-if="timeRemaining > 0">剩余：{{ formatTime(timeRemaining) }}</span>
            </div>
          </div>

          <!-- 上传文件列表 -->
          <div v-if="uploadedFiles.length > 0" class="uploaded-files-list">
            <div class="files-list-header">
              <span>已上传文件</span>
              <el-button
                type="text"
                size="small"
                @click="showAllFiles = !showAllFiles"
              >
                {{ showAllFiles ? '收起' : '展开' }}
              </el-button>
            </div>
            <el-collapse-transition>
              <div v-show="showAllFiles" class="files-list-content">
                <div
                  v-for="file in uploadedFiles"
                  :key="file.name"
                  class="file-item"
                  :class="{ 'success': file.status === 'success', 'error': file.status === 'error' }"
                >
                  <el-icon class="file-status-icon">
                    <component :is="file.status === 'success' ? 'Check' : 'Close'"/>
                  </el-icon>
                  <span class="file-name" :title="file.name">{{ file.name }}</span>
                  <span class="file-size">{{ formatFileSize(file.size) }}</span>
                </div>
              </div>
            </el-collapse-transition>
          </div>
        </div>
      </div>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="cancelUpload">取消</el-button>
                    <el-button type="primary" :loading="annotationUploading"
                               @click="submitBatchUpload" :disabled="fileListToUpload.length === 0">
                        开始上传 ({{ fileListToUpload.length }})
                    </el-button>
                </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted, inject, computed, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {getCorpusByIdAsAdmin, getCorpusFilesAsAdmin} from '../services/corpus'
import corpus from '../assets/corpus.json'
import {getAllAnnotationsDownloadUrl, uploadAnnotation} from '../services/annotation'
const route = useRoute()
const router = useRouter()
const corpusId = ref(route.params.id)
const fileList = ref([])
const loading = ref(false)
const filesLoading = ref(false)

// 用户store

// 编辑相关
const editForm = ref({})
const editFormRef = ref(null)

const annotationInputRef = ref(null)
const currentUploadRow = ref(null)
const uploadDialogVisible = ref(false) // 控制弹窗
const fileListToUpload = ref([])       // 待上传文件列表
const annotationUploading = ref(false)

const isUploading = ref(false)
const uploadStatus = ref('') // 'uploading', 'success', 'error'
const overallPercentage = ref(0)
const uploadedFilesCount = ref(0)
const uploadedSize = ref(0)
const currentUploadFile = ref(null)
const currentFilePercentage = ref(0)
const uploadSpeed = ref(0)
const timeRemaining = ref(0)
const uploadedFiles = ref([])
const showAllFiles = ref(false)

const totalSelectedSize = computed(() => {
  return fileListToUpload.value.reduce((total, file) => {
    return total + (file.size || 0)
  }, 0)
})

const totalSelectedFiles = computed(() => {
  return fileListToUpload.value.length
})
const invalidFiles = ref(new Set())


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
const dataFormats = corpus.dataFormats
// 基于国家名称到语言的映射
const countryNameToLanguage = new Map(corpus.countries.map(c => [c.name, c.language]))

// 校验相关（与上传页保持一致的校验方式）
const countries = corpus.countries.sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
const validCountryNames = new Set(countries.map(country => country.name))

const rules = {
  country: [
    {required: true, message: '请输入国家', trigger: 'blur'},
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
    {required: true, message: '请输入语料集名称', trigger: 'blur'}
  ],
  domain: [
    {required: true, message: '请输入所属领域', trigger: 'blur'}
  ],
  language: [
    {required: true, message: '请输入语言', trigger: 'blur'}
  ],
  dataFormat: [
    {required: true, message: '请输入数据模态', trigger: 'blur'}
  ],
  classification: [
    {required: true, message: '请输入数据分类', trigger: 'blur'}
  ],
  dataVolume: [
    {required: false, message: '请填写文件数量', trigger: 'blur'}
  ],
  volumeUnit: [
    {required: true, message: '请填写文件数量单位', trigger: 'blur'}
  ],
  estimatedCapacityGb: [
    {required: false, message: '请填写容量估算', trigger: 'blur'}
  ],
  dataYear: [
    {required: true, message: '请输入数据年份', trigger: 'blur'}
  ],
  sourceLocation: [
    {required: false, message: '请输入数据来源机构', trigger: 'blur'}
  ],
  dataSource: [
    {required: false, message: '请输入数据来源', trigger: 'blur'}
  ],
  provider: [
    {required: false, message: '请输入数据提供方', trigger: 'blur'}
  ],
  providerContact: [
    {required: false, message: '请输入数据提供方联系方式', trigger: 'blur'},
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

// 获取全局面包屑管理工具
const breadcrumb = inject('breadcrumb')

onMounted(() => {
  // 设置当前页面的面包屑
  const breadcrumbItems = [
    {title: '首页', path: '/'}
  ]

  // 根据用户类型设置不同的面包屑路径
  breadcrumbItems.push(
    {title: '语料管理', path: '/corpus-management'},
    {title: '语料详情', path: `/corpus-management-details/${corpusId.value}`}
  )

  breadcrumb.setBreadcrumb(breadcrumbItems)

  // 加载语料详情
  loadCorpusDetails()
})


const getStatusText = () => {
  switch(uploadStatus.value) {
    case 'success': return '上传完成'
    case 'error': return '上传失败'
    default: return '正在上传...'
  }
}
const getProgressStatus = () => {
  switch(uploadStatus.value) {
    case 'success': return 'success'
    case 'error': return 'exception'
    default: return ''
  }
}
const getProgressColor = (percentage) => {
  if (percentage < 30) return '#e6a23c'
  if (percentage < 70) return '#409eff'
  return '#67c23a'
}
const openUploadDialog = (row) => {
  currentUploadRow.value = row
  fileListToUpload.value = [] // 清空之前的列表
  uploadDialogVisible.value = true
}


const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  if (files.length === 0) return

  // 过滤非 .txt 文件
  const validFiles = files.filter(f => f.name.toLowerCase().endsWith('.txt'))
  const invalidCount = files.length - validFiles.length

  if (invalidCount > 0) {
    ElMessage.warning(`过滤了 ${invalidCount} 个非 .txt 文件`)
  }

  if (validFiles.length > 0) {
    // 追加到待上传列表
    fileListToUpload.value.push(...validFiles)
  }
}

const clearUploadData = () => {
  fileListToUpload.value = []
  annotationUploading.value = false
  // currentUploadRow.value = null // 这里的清理看情况，通常可以保留或在 open 时覆盖
  isUploading.value = false
  uploadStatus.value = ''
  overallPercentage.value = 0
  uploadedFilesCount.value = 0
  uploadedSize.value = 0
  currentUploadFile.value = null
  currentFilePercentage.value = 0
  uploadSpeed.value = 0
  timeRemaining.value = 0
  uploadedFiles.value = []
  showAllFiles.value = false
}

const cancelUpload = () => {
  if (isUploading.value) {
    isUploading.value = false
    ElMessage.info('上传已取消')
    clearUploadData()
  } else {
    uploadDialogVisible.value = false
  }
}

// 6. 提交批量上传
const submitBatchUpload = async () => {
  if (fileListToUpload.value.length === 0) return

  // 校验原始文件ID
  if (!currentUploadRow.value || !currentUploadRow.value.fileId) {
    ElMessage.error('无法获取原始文件信息')
    return
  }

  annotationUploading.value = true
  isUploading.value = true
  uploadStatus.value = 'uploading'

  try {
    // 初始化进度数据
    uploadedFilesCount.value = 0
    uploadedSize.value = 0
    overallPercentage.value = 0
    uploadedFiles.value = []

    const totalSize = totalSelectedSize.value
    const totalFiles = fileListToUpload.value.length
    const startTime = Date.now()

    // 遍历每个文件上传
    for (let i = 0; i < totalFiles; i++) {
      const fileItem = fileListToUpload.value[i]
      const fileSize = fileItem.size

      currentUploadFile.value = fileItem
      currentFilePercentage.value = 0

      try {
        const formData = new FormData()
        formData.append('file', fileItem.raw || fileItem)
        formData.append('originalFileId', currentUploadRow.value.fileId)

        // 创建上传请求
        await uploadAnnotation(formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            if (progressEvent.lengthComputable) {
              // 更新当前文件进度
              const currentPercent = Math.round((progressEvent.loaded / progressEvent.total) * 100)
              currentFilePercentage.value = currentPercent

              // 计算总进度
              const completedFiles = i // 已完成文件数（当前文件之前的）
              const currentFileProgress = currentPercent / 100 // 当前文件完成比例
              const overallPercent = Math.round(((completedFiles + currentFileProgress) / totalFiles) * 100)
              overallPercentage.value = overallPercent

              // 更新已上传大小（估算）
              const currentUploadedSize = Math.round(progressEvent.loaded / 1024 / 1024) // 转换为MB
              const totalUploadedSize = uploadedSize.value + currentUploadedSize

              // 计算上传速度
              const elapsedTime = (Date.now() - startTime) / 1000
              if (elapsedTime > 0) {
                uploadSpeed.value = totalUploadedSize / elapsedTime
                if (uploadSpeed.value > 0) {
                  timeRemaining.value = ((totalSize / 1024 / 1024) - totalUploadedSize) / uploadSpeed.value
                }
              }
            }
          }
        })

        // 单个文件上传完成
        uploadedSize.value += Math.round(fileSize / 1024 / 1024) // 转换为MB
        uploadedFilesCount.value++
        currentFilePercentage.value = 100
        overallPercentage.value = Math.round(((i + 1) / totalFiles) * 100)

        // 添加到已上传列表
        uploadedFiles.value.push({
          name: fileItem.name,
          size: fileSize,
          status: 'success'
        })

        // 短暂延迟，让用户看到完成状态
        await new Promise(resolve => setTimeout(resolve, 300))

      } catch (error) {
        console.error(`文件 ${fileItem.name} 上传失败:`, error)
        uploadedFiles.value.push({
          name: fileItem.name,
          size: fileSize,
          status: 'error',
          message: error.message || '上传失败'
        })

        // 如果用户取消了上传，中断后续上传
        if (!isUploading.value) {
          break
        }
      }
    }

    // 所有文件上传完成后统计结果
    const successCount = uploadedFiles.value.filter(f => f.status === 'success').length
    const failCount = uploadedFiles.value.filter(f => f.status === 'error').length

    if (failCount === 0 && successCount > 0) {
      uploadStatus.value = 'success'
      overallPercentage.value = 100
      ElMessage.success(`成功上传 ${successCount} 个文件`)

      // 3秒后关闭弹窗并刷新文件列表
      setTimeout(() => {
        uploadDialogVisible.value = false
        loadFileList()
      }, 3000)
    } else if (successCount > 0) {
      uploadStatus.value = 'error'
      ElMessage.warning(`上传完成：${successCount} 个成功，${failCount} 个失败`)
    } else {
      uploadStatus.value = 'error'
      ElMessage.error('所有文件上传失败')
    }

  } catch (error) {
    console.error('上传过程发生异常:', error)
    uploadStatus.value = 'error'
    ElMessage.error('上传过程发生异常: ' + error.message)
  } finally {
    annotationUploading.value = false
    // 注意：这里不设置 isUploading.value = false，让用户能看到最终状态
  }
}



  // const uploadPromises = fileListToUpload.value.map(fileItem => {
  //   const formData = new FormData()
  //   formData.append('file', fileItem.raw)
  //   formData.append('originalFileId', currentUploadRow.value.fileId)
  //   return uploadAnnotation(formData)
  // })
  // const results = await Promise.allSettled(uploadPromises)
  // const successCount = results.filter(r => r.status === 'fulfilled').length
  // const failCount = results.filter(r => r.status === 'rejected').length
  // if (failCount === 0) {
  //   ElMessage.success(`成功上传 ${successCount} 个文件`)
  //   uploadDialogVisible.value = false // 全部成功关闭弹窗
  // } else {
  //   ElMessage.warning(`上传完成：${successCount} 个成功，${failCount} 个失败`)
  //   uploadDialogVisible.value = false
  // }
  // loadFileList()


const downloadAllOriginalFiles = () => {
  const corpusId = route.params.id || route.query.id;
  ElMessage.success(`正在下载全部原始文件`);
  window.location.href = `/api/admin/corpus/${corpusId}/download`;
};

const handleDownloadAllAnnotations = () => {
  if (!corpusId.value) return
  // 使用 window.open 或 location.href 触发浏览器下载
  ElMessage.success(`正在下载全部标注文件`);
  window.location.href = getAllAnnotationsDownloadUrl(corpusId.value)
}

const escapeRegExp = (annotationName) => {
  if (typeof annotationName !== 'string') {
    return ''
  }
  return annotationName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

const getBaseName = (name) => {
  const lastDotIndex = name.lastIndexOf('.')
  return lastDotIndex > 0 ? name.substring(0, lastDotIndex) : name
}

const isValidFileName = (fileName) => {
  // 允许中文、英文、数字、下划线、括号等常见字符
  if (!fileName.toLowerCase().endsWith('.txt')) {
    return false
  }
  const nameWithoutExt = fileName.slice(0, -4)
  if (!currentUploadRow.value || !currentUploadRow.value.fileName) {
    return false
  }
  const baseFileName = getBaseName(currentUploadRow.value.fileName)
  if (nameWithoutExt === baseFileName) {
    return true
  }
  const pattern = new RegExp(`^${escapeRegExp(baseFileName)}\\((\\d+)\\)$`)
  const match = nameWithoutExt.match(pattern)
  if (match) {
    const num = parseInt(match[1], 10)
    // 数字编号应该大于0
    return num > 0
  }
  return false
}

const validateSingleFile = (file) => {
  const errors = []

  // 文件类型验证
  const isTxt = file.type === 'text/plain' || file.name.toLowerCase().endsWith('.txt')
  if (!isTxt) {
    errors.push('只能上传 .txt 格式的文件!')
  }

  // 单个文件大小限制：10GB = 10 * 1024 * 1024 * 1024 = 10737418240 字节
  const maxSize = 10 * 1024 * 1024 * 1024 // 10GB in bytes
  if (file.size > maxSize) {
    errors.push(`文件大小不能超过 10GB!`)
  }

  if (!isValidFileName(file.name)) {
    errors.push('标注文件名不符合要求')
  }
  return {
    isValid: errors.length === 0,
    errors
  }
}

const handleFileRemove = (uploadFile, uploadFiles) => {
  fileListToUpload.value = uploadFiles
}

const handleUploadChange = (uploadFile, uploadFiles) => {
  const validation = validateSingleFile(uploadFile)

  if (!validation.isValid) {
    // 标记无效文件
    invalidFiles.value.add(uploadFile.uid)

    // 显示错误
    validation.errors.forEach((error, index) => {
      if (index === 0) {
        ElMessage.error(`${uploadFile.name}: ${error}`)
      }
    })

    // 移除无效文件，保持列表只包含有效文件
    const validFiles = uploadFiles.filter(file => {
      const fileValidation = validateSingleFile(file)
      return fileValidation.isValid
    })

    fileListToUpload.value = validFiles
    return false
  }

  // 文件有效
  invalidFiles.value.delete(uploadFile.uid)

  // 检查重复文件
  const duplicateFiles = uploadFiles.filter(file =>
    file.name === uploadFile.name && file.uid !== uploadFile.uid
  )

  if (duplicateFiles.length > 0) {
    ElMessage.warning(`已存在同名文件: ${uploadFile.name}`)
  }

  // 更新文件列表
  fileListToUpload.value = uploadFiles
  return true
}


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

const formatTime = (seconds) => {
  if (seconds < 60) return `${Math.ceil(seconds)}秒`
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${minutes}分${secs}秒`
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

function downloadTemplate() {
  const link = document.createElement('a');
  link.href = '/template.txt';
  link.download = '标注模板.txt';
  link.click();
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

const getAnnotationDownloadUrl = (annotationId) => {
  // 注意：需要确保后端API路径正确，且配置了代理
  return `/api/annotation-files/download/${annotationId}`
}

// 显示文件下载消息（不阻止默认的链接行为）
function showFileDownloadMessage(file) {
  // 不使用 event.preventDefault()，让 <a> 标签的默认下载行为正常执行
  ElMessage.success(`正在下载文件: ${file.fileName}`);
}

function showAnnotationFileDownloadMessage(file) {
  // 不使用 event.preventDefault()，让 <a> 标签的默认下载行为正常执行
  ElMessage.success(`正在下载文件: ${file.title}`);
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

.edit-form-grid >>> .el-form-item {
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
