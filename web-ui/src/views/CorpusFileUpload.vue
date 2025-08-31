<template>
  <div class="upload-form-page">
    <!-- 上传进度条 -->
    <div v-if="uploadProgress.show" class="upload-progress-overlay">
      <div class="progress-container">
        <div class="progress-header">
          <h3>{{ uploadProgress.title }}</h3>
          <el-button type="text" @click="cancelUpload" v-if="!uploadProgress.completed">取消</el-button>
        </div>

        <div class="progress-content">
          <div class="overall-progress">
            <div class="progress-info">
              <span>总进度: {{ uploadProgress.currentFile }}/{{ uploadProgress.totalFiles }}</span>
              <span>{{ uploadProgress.overallPercent }}%</span>
            </div>
            <el-progress :percentage="uploadProgress.overallPercent" :stroke-width="8" :show-text="false" />
          </div>

          <div v-if="uploadProgress.currentFileName" class="current-file">
            <div class="file-info">
              <span>当前文件: {{ uploadProgress.currentFileName }}</span>
              <span>{{ uploadProgress.currentPercent }}%</span>
            </div>
            <el-progress :percentage="uploadProgress.currentPercent" :stroke-width="6" :show-text="false" />
          </div>

          <div class="status-text">{{ uploadProgress.status }}</div>
        </div>
      </div>
    </div>

    <div class="upload-form">
      <div class="form-container">
        <el-form ref="uploadForm" :model="formData" :rules="rules" label-width="140px" class="upload-form">
          <!-- <h3>提示信息</h3>
          <div class="divider"></div>
          <div class="info-box">
            <p class="info-text">
              <strong>国家：</strong>填写相关国家，如老挝、泰国
            </p>
            <p class="info-text">
              <strong>语料集名称：</strong>需要能间接明了反映出语料集的关键信息，如：老挝国家图书馆书籍电子文本语料
            </p>
            <p class="info-text">
              <strong>所属领域：</strong>如：医疗、教育、民生、经济、环境、社会和政府政策、法律等
            </p>
            <p class="info-text">
              <strong>语种：</strong>填写语料对应语言：如老挝语、泰国语、印尼语等东南亚国家语言
            </p>
            <p class="info-text">
              <strong>数据模态：</strong>纸质、可编辑电子文本、扫描电子文本、语音、视频、图像
            </p>
            <p class="info-text">
              <strong>数据分类：</strong>包括：基础语料、预训练语料、SFT语料、强化学习语料、平行语料、价值观语料、ASR语料、TTS语料，其中除原始语料之外的都属于加工语料。
            </p>
            <p class="info-text">
              <strong>文件数量：</strong>对应的文件数量数字表述,如1000
            </p>
            <p class="info-text">
              <strong>文件数量单位：</strong>对应的文件数量表述单位，如GB、条、份、本、小时、篇等
            </p>
            <p class="info-text">
              <strong>容量估算（GB）：</strong>按电子格式存储的数据容量估算，此处按GB计算
            </p>
            <p class="info-text">
              <strong>数据年份：</strong>数据归属的年份范围，如2000年以后
            </p>
            <p class="info-text">
              <strong>数据来源机构：</strong>注明数据来源机构区，如广西xxx、老挝xx部、xx学院
            </p>
            <p class="info-text">
              <strong>数据来源：</strong>说明数据的来源，如文本的，老挝公开网站，语音的应注明是什么地方什么类型人士录制
            </p>
            <p class="info-text">
              <strong>数据提供方：</strong>填写本条数据集的提供单位名称，如广西民族大学
            </p>
            <p class="info-text">
              <strong>数据提供方联系方式：</strong>联系人（联系电话）
            </p>
          </div> -->
          <h3>语料集信息</h3>
          <div class="divider"></div>
          <div class="form-content">
            <!-- 左侧表单 -->
            <div class="form-column">
              <el-form-item label="国家" prop="country">
                <!-- <el-input v-model="formData.country" placeholder="请填写国家"></el-input> -->
                <el-select v-model="formData.country" filterable placeholder="请选择国家">
                  <el-option-group label="默认">
                    <el-option v-for="country in defaultCountries" :key="country.code" :label="country.name"
                      :value="country.name" />
                  </el-option-group>
                  <el-option-group label="东盟">
                    <el-option v-for="country in aseanCountries" :key="country.code" :label="country.name"
                      :value="country.name" />
                  </el-option-group>
                  <el-option-group label="其他">
                    <el-option v-for="country in otherCountries" :key="country.code" :label="country.name"
                      :value="country.name" />
                  </el-option-group>
                </el-select>
              </el-form-item>
              <el-form-item label="语料集名称" prop="collectionName">
                <el-input v-model="formData.collectionName" placeholder="如：中国国家图书馆书籍电子文本语料"
                  @blur="checkCollectionNameDuplicate"></el-input>
                <div v-if="collectionNameStatus.show" class="name-check-status">
                  <span :class="collectionNameStatus.type">
                    {{ collectionNameStatus.message }}
                  </span>
                </div>
              </el-form-item>

              <el-form-item label="所属领域" prop="domain">
                <el-radio-group v-model="formData.domain" class="domain-radio-group">
                  <el-radio v-for="domain in domains" :key="domain.domainName" :label="domain.domainName"
                    class="domain-radio-item">
                    {{ domain.domainName }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="语种" prop="language">
                <el-input v-model="formData.language" placeholder="如：中文、老挝语、泰国语"></el-input>
              </el-form-item>

              <el-form-item label="数据模态" prop="dataFormat">
                <el-select v-model="formData.dataFormat" filterable placeholder="请选择数据模态" multiple>
                  <el-option v-for="dataFormat in dataFormats" :key="dataFormat.dataFormat"
                    :label="dataFormat.dataFormat" :value="dataFormat.dataFormat"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="数据分类" prop="classification">
                <el-select v-model="formData.classification" filterable placeholder="请选择数据分类">
                  <el-option v-for="classification in classifications" :key="classification.classificationName"
                    :label="classification.classificationName" :value="classification.classificationName"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="文件数量" prop="dataVolume">
                <el-input v-model="formData.dataVolume" type="number" placeholder="请填写文件数量" disabled></el-input>
              </el-form-item>

              <el-form-item label="文件数量单位" prop="volumeUnit">
                <el-select v-model="formData.volumeUnit" filterable placeholder="请选择文件数量单位">
                  <el-option v-for="volumeUnit in volumeUnits" :key="volumeUnit.volumeUnit"
                    :label="volumeUnit.volumeUnit" :value="volumeUnit.volumeUnit"></el-option>
                </el-select>
              </el-form-item>
            </div>

            <!-- 右侧表单 -->
            <div class="form-column">
              <el-form-item label="容量估算 (GB)" prop="estimatedCapacityGb">
                <el-input v-model="formData.estimatedCapacityGb" type="number" placeholder="请填写容量估算"
                  disabled></el-input>
              </el-form-item>

              <el-form-item label="数据年份" prop="dataYear">
                <el-date-picker v-model="formData.dataYear" type="year" placeholder="请选择数据年份" format="YYYY"
                  value-format="YYYY">
                </el-date-picker>
              </el-form-item>

              <el-form-item label="数据来源机构" prop="sourceLocation">
                <el-input v-model="formData.sourceLocation" placeholder="如：广西xxx、老挝xx部、xx学院，注明数据来源机构"></el-input>
              </el-form-item>

              <el-form-item label="数据来源渠道" prop="dataSource">
                <el-input v-model="formData.dataSource" placeholder="如：文本来源于老挝公开网站，语音由XX录制"></el-input>
              </el-form-item>

              <el-form-item label="数据提供方" prop="provider">
                <el-input v-model="formData.provider" placeholder="如：广西大学，填写本条数据集的提供单位名称"></el-input>
              </el-form-item>

              <el-form-item label="提供方联系方式" prop="providerContact">
                <el-input v-model="formData.providerContact" placeholder="填写人联系方式（手机号或座机号：区号-电话号码）">
                </el-input>
              </el-form-item>

              <el-form-item label="备注说明">
                <el-input v-model="formData.remarks" placeholder="请填写备注说明（选填）" type="textarea" :rows="3"></el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 附件上传 -->
          <div class="attachment-section">
            <h3>附件</h3>
            <div class="divider"></div>

            <div class="file-upload-container">
              <div class="upload-area">
                <el-upload class="upload-demo" drag :auto-upload="false" :before-upload="beforeUpload"
                  :on-change="handleFileChange" :file-list="fileList" multiple :on-remove="removeFile">
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">
                    可同时选择多个文件，上限 10.00GB
                  </div>
                </el-upload>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <el-button type="primary" @click="saveForm" :loading="isSubmitting">上传</el-button>
            <el-button @click="saveAndCreate" :loading="isSubmitting">上传并继续新增</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, inject, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import api from '../services/api'
import corpus from '../assets/corpus.json'
import { useUserStore } from '@/stores/user'


const router = useRouter()
const userStore = useUserStore()
const uploadForm = ref(null)
const isSubmitting = ref(false)
const countries = corpus.countries.sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
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
const classifications = corpus.classifications
const volumeUnits = corpus.volumeUnits
const dataFormats = corpus.dataFormats
const validCountryNames = new Set(countries.map(country => country.name))
// 基于国家名称到语言的映射
const countryNameToLanguage = new Map(corpus.countries.map(c => [c.name, c.language]))
// 上传进度状态
const uploadProgress = reactive({
  show: false,
  title: '正在上传...',
  currentFile: 0,
  totalFiles: 0,
  overallPercent: 0,
  currentFileName: '',
  currentPercent: 0,
  status: '准备中...',
  completed: false
})

// 取消上传的控制器
let uploadAbortController = null

// 获取全局面包屑管理工具
const breadcrumb = inject('breadcrumb')

onMounted(() => {
  // 设置当前页面的面包屑
  breadcrumb.setBreadcrumb([
    { title: '首页', path: '/' },
    { title: '语料清单', path: '/file-list' },
    { title: '语料详细信息', path: '/upload' }
  ])

  // 若“数据提供方”为空，则尝试用当前用户的学院填充
  if (!formData.provider && userStore.college) {
    formData.provider = userStore.college
  }

  // 若“提供方联系方式”为空，则尝试用当前用户的手机号填充
  if (!formData.providerContact) {
    const phoneFromStore = (userStore.userInfo && userStore.userInfo.phone) || ''
    const phoneFromLocal = localStorage.getItem('phone') || ''
    const phone = phoneFromStore || phoneFromLocal
    if (phone) {
      formData.providerContact = phone
    }
  }

  // 初始化语言：根据默认国家填充
  if (!formData.language && formData.country) {
    const lang = countryNameToLanguage.get(formData.country)
    if (lang) {
      formData.language = lang
    }
  }
})

// 表单验证规则
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
    { required: false, message: '请输入数据年份', trigger: 'blur' }
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
        // 匹配中国大陆手机号码 (11位数字，以1开头)
        const mobilePattern = /^1[3-9]\d{9}$/;
        // 匹配中国大陆座机号码 (区号3-4位数字 + 连接符可选 + 7-8位数字)
        const landlinePattern = /^(0\d{2,3})-?(\d{7,8})$/;

        if (!value) {
          callback();
        } else if (mobilePattern.test(value) || landlinePattern.test(value)) {
          callback();
        } else {
          callback(new Error('请输入有效的手机号或座机号（座机格式：区号-电话号码）'));
        }
      },
      trigger: 'blur'
    }
  ]
}

const formData = reactive({
  country: '中国',
  collectionName: '',
  domain: '教育',
  language: '',
  dataFormat: ['文本'],
  classification: '基础语料',
  dataVolume: 0,
  volumeUnit: '份',
  estimatedCapacityGb: '0.000000',
  dataYear: new Date().getFullYear().toString(),
  sourceLocation: '',
  dataSource: '',
  provider: '',
  providerContact: '',
  remarks: ''
})

// 计算属性：将dataFormat数组转换为文本
const dataFormatText = computed(() => {
  if (Array.isArray(formData.dataFormat)) {
    return formData.dataFormat.join('、');
  }
  return formData.dataFormat || '';
})

// 监听国家变化，自动填充语言（仅当当前语言为空或与国家不匹配时更新）
// 注意：该 watcher 必须放在 formData 定义之后，避免 TDZ 错误
watch(
  () => formData.country,
  (newCountry) => {
    if (!newCountry) return
    const languageByCountry = countryNameToLanguage.get(newCountry)
    if (!languageByCountry) return

    // 若语言为空或当前语言不属于所选国家，才进行覆盖
    if (!formData.language || formData.language !== languageByCountry) {
      formData.language = languageByCountry
    }
  }
)

const fileList = ref([])

// 语料名称检查状态
const collectionNameStatus = reactive({
  show: false,
  type: '', // 'success' 或 'error'
  message: ''
})



// 文件上传前的验证
const beforeUpload = (file) => {
  // 检查文件大小
  const isLt10G = file.size / 1024 / 1024 / 1024 < 10
  if (!isLt10G) {
    ElMessage.error('文件大小超过10GB限制！')
    return false
  }
  return true
}

// 计算并更新容量估算（GB）
const updateEstimatedCapacity = (files) => {
  let totalSizeInBytes = 0
  files.forEach(f => {
    totalSizeInBytes += f.size
  })
  const totalSizeInGB = (totalSizeInBytes / (1024 * 1024 * 1024)).toFixed(6)
  formData.estimatedCapacityGb = totalSizeInGB
}

// 处理文件变更
const handleFileChange = (file, uploadFileList) => {
  // 更新文件列表，只保留文件信息，不进行上传
  fileList.value = uploadFileList.map(uploadFile => ({
    name: uploadFile.name,
    size: uploadFile.size,
    raw: uploadFile.raw  // 保存原始文件对象，用于后续上传
  }))

  // 更新容量估算
  updateEstimatedCapacity(fileList.value)

  // 根据上传文件数量，自动填充文件数量（默认0）
  formData.dataVolume = fileList.value.length || 0
}

// 移除文件
const removeFile = (file, uploadFiles) => {
  // 使用上传组件当前的文件列表重建本地 fileList
  fileList.value = uploadFiles.map(uploadFile => ({
    name: uploadFile.name,
    size: uploadFile.size,
    raw: uploadFile.raw
  }))

  // 更新容量估算
  updateEstimatedCapacity(fileList.value)

  // 根据上传文件数量，自动填充文件数量（默认0）
  formData.dataVolume = fileList.value.length || 0
}

// 检查语料集名称是否重复
const checkCollectionNameDuplicate = async () => {
  const collectionName = formData.collectionName?.trim()

  if (!collectionName) {
    collectionNameStatus.show = false
    return
  }

  try {
    // 调用后端接口检查是否重复
    const response = await api.get(`/corpus/my-corpus`, {
      params: {
        page: 1,
        size: 1,
        collectionName: collectionName,
        searchType: 'accurate'
      }
    })

    if (response.data && response.data.records && response.data.records.length > 0) {
      // 找到重复的语料
      collectionNameStatus.show = true
      collectionNameStatus.type = 'error'
      collectionNameStatus.message = '⚠️ 该语料集名称已存在，请使用其他名称'
    } else {
      // 名称可用
      collectionNameStatus.show = false
      collectionNameStatus.type = 'success'
      // collectionNameStatus.message = '✅ 语料集名称可用'
    }
  } catch (error) {
    console.error('检查语料名称失败:', error)
    collectionNameStatus.show = false
  }
}

// 保存表单
const saveForm = async () => {
  if (isSubmitting.value) return

  isSubmitting.value = true

  try {
    // 表单验证
    await uploadForm.value.validate()

    // 校验是否选择附件
    if (!fileList.value || fileList.value.length === 0) {
      ElMessage.warning('请至少选择一个附件')
      isSubmitting.value = false
      return
    }

    // 初始化进度条
    const totalFiles = fileList.value.length
    uploadProgress.show = true
    uploadProgress.currentFile = 0
    uploadProgress.totalFiles = totalFiles
    uploadProgress.overallPercent = 0
    uploadProgress.status = '正在创建语料记录...'
    uploadProgress.completed = false

    // 创建提交数据副本，将dataFormat转换为文本
    const submitData = { ...formData, dataFormat: dataFormatText.value }

    // 第一步：创建语料记录
    const corpusResponse = await api.post('/corpus', submitData)

    if (!corpusResponse.data || !corpusResponse.data.corpusId) {
      throw new Error('创建语料失败，可能是语料名称已存在')
    }

    const corpusId = corpusResponse.data.corpusId
    uploadProgress.status = '语料记录创建成功，开始上传文件...'

    // 第二步：上传文件（如果有文件）
    if (fileList.value.length > 0) {

      for (let i = 0; i < fileList.value.length; i++) {
        const file = fileList.value[i]
        if (file.raw) {
          // 更新当前文件信息
          uploadProgress.currentFile = i + 1
          uploadProgress.currentFileName = file.name
          uploadProgress.currentPercent = 0
          uploadProgress.status = `正在上传文件 ${i + 1}/${totalFiles}: ${file.name}`

          // 创建取消控制器
          const abortController = new AbortController()
          uploadAbortController = abortController

          const uploadFormData = new FormData()
          uploadFormData.append('file', file.raw)
          uploadFormData.append('corpusId', corpusId)

          await api.post('/corpus/upload', uploadFormData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            signal: abortController.signal,
            onUploadProgress: (progressEvent) => {
              if (progressEvent.lengthComputable) {
                // 更新当前文件进度
                const currentPercent = Math.round((progressEvent.loaded / progressEvent.total) * 100)
                uploadProgress.currentPercent = currentPercent

                // 计算总进度
                const completedFiles = i
                const currentFileProgress = currentPercent / 100
                const overallPercent = Math.round(((completedFiles + currentFileProgress) / totalFiles) * 100)
                uploadProgress.overallPercent = overallPercent
              }
            }
          })

          // 文件上传完成
          uploadProgress.currentPercent = 100
          ElMessage.success(`文件 ${file.name} 上传成功`)
        }
      }

      // 所有文件上传完成
      uploadProgress.overallPercent = 100
      uploadProgress.status = '所有文件上传完成！'
      uploadProgress.completed = true

      // 延迟2秒后隐藏进度条并跳转
      setTimeout(() => {
        uploadProgress.show = false
        ElMessage.success('所有操作完成！')
        router.push('/file-list')
      }, 2000)
    } else {
      // 没有文件，直接完成
      uploadProgress.overallPercent = 100
      uploadProgress.status = '语料创建完成！'
      uploadProgress.completed = true

      setTimeout(() => {
        uploadProgress.show = false
        ElMessage.success('语料创建完成！')
        router.push('/file-list')
      }, 1000)
    }

  } catch (error) {
    uploadProgress.show = false
    console.error('上传失败:', error)


    if (error.name === 'AbortError') {
      ElMessage.info('上传已取消')
    } else if (error.response == null) {
      let errorMessage = '请检查填写信息'
      ElMessage({
        message: errorMessage,
        type: 'error',
        duration: 5000, // 延长显示时间到5秒
        showClose: true // 允许手动关闭
      })
    } else if (error.response?.status !== 401) {
      // 改进错误消息显示逻辑
      let errorMessage = '上传失败，请稍后重试'

      if (error.response?.data) {
        // 如果后端返回了具体错误信息
        errorMessage = error.response.data
      } else if (error.message) {
        // 如果是前端抛出的错误（如语料创建失败）
        errorMessage = error.message
      }

      // 显示更明确的错误提示
      ElMessage({
        message: errorMessage,
        type: 'error',
        duration: 5000, // 延长显示时间到5秒
        showClose: true // 允许手动关闭
      })
    }
  } finally {
    isSubmitting.value = false
    uploadAbortController = null
  }
}

// 取消上传
const cancelUpload = () => {
  if (uploadAbortController) {
    uploadAbortController.abort()
  }
  uploadProgress.show = false
  isSubmitting.value = false
}

// 保存并创建新表单
const saveAndCreate = async () => {
  if (isSubmitting.value) return

  isSubmitting.value = true

  try {
    // 表单验证
    await uploadForm.value.validate()

    // 校验是否选择附件
    if (!fileList.value || fileList.value.length === 0) {
      ElMessage.warning('请至少选择一个附件')
      isSubmitting.value = false
      return
    }

    // 初始化进度条
    const totalFiles = fileList.value.length
    uploadProgress.show = true
    uploadProgress.currentFile = 0
    uploadProgress.totalFiles = totalFiles
    uploadProgress.overallPercent = 0
    uploadProgress.status = '正在创建语料记录...'
    uploadProgress.completed = false
    uploadProgress.title = '上传并新增...'

    // 创建提交数据副本，将dataFormat转换为文本
    const submitData = { ...formData, dataFormat: dataFormatText.value }

    // 第一步：创建语料记录
    const corpusResponse = await api.post('/corpus', submitData)

    if (!corpusResponse.data || !corpusResponse.data.corpusId) {
      throw new Error('创建语料失败，可能是语料名称已存在')
    }

    const corpusId = corpusResponse.data.corpusId
    uploadProgress.status = '语料记录创建成功，开始上传文件...'

    // 第二步：上传文件（如果有文件）
    if (fileList.value.length > 0) {

      for (let i = 0; i < fileList.value.length; i++) {
        const file = fileList.value[i]
        if (file.raw) {
          // 更新当前文件信息
          uploadProgress.currentFile = i + 1
          uploadProgress.currentFileName = file.name
          uploadProgress.currentPercent = 0
          uploadProgress.status = `正在上传文件 ${i + 1}/${totalFiles}: ${file.name}`

          // 创建取消控制器
          const abortController = new AbortController()
          uploadAbortController = abortController

          const uploadFormData = new FormData()
          uploadFormData.append('file', file.raw)
          uploadFormData.append('corpusId', corpusId)

          await api.post('/corpus/upload', uploadFormData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            signal: abortController.signal,
            onUploadProgress: (progressEvent) => {
              if (progressEvent.lengthComputable) {
                // 更新当前文件进度
                const currentPercent = Math.round((progressEvent.loaded / progressEvent.total) * 100)
                uploadProgress.currentPercent = currentPercent

                // 计算总进度
                const completedFiles = i
                const currentFileProgress = currentPercent / 100
                const overallPercent = Math.round(((completedFiles + currentFileProgress) / totalFiles) * 100)
                uploadProgress.overallPercent = overallPercent
              }
            }
          })

          // 文件上传完成
          uploadProgress.currentPercent = 100
          ElMessage.success(`文件 ${file.name} 上传成功`)
        }
      }

      // 所有文件上传完成
      uploadProgress.overallPercent = 100
      uploadProgress.status = '所有文件上传完成！正在重置表单...'
      uploadProgress.completed = true
    } else {
      // 没有文件，直接完成
      uploadProgress.overallPercent = 100
      uploadProgress.status = '语料创建完成！正在重置表单...'
      uploadProgress.completed = true
    }

    // 重置表单
    // Object.keys(formData).forEach(key => {
    //   formData[key] = ''
    // })
    fileList.value = []

    // 重置表单校验状态
    // uploadForm.value.resetFields()

    // 延迟1秒后隐藏进度条
    setTimeout(() => {
      uploadProgress.show = false
      uploadProgress.title = '正在上传...' // 重置标题
      ElMessage.success('保存成功！可以继续新增')
    }, 1000)

  } catch (error) {
    uploadProgress.show = false
    uploadProgress.title = '正在上传...' // 重置标题
    console.error('上传失败:', error)

    if (error.name === 'AbortError') {
      ElMessage.info('上传已取消')
    } else if (error.response?.status !== 401) {
      // 改进错误消息显示逻辑
      let errorMessage = '上传失败，请稍后重试'

      if (error.response?.data) {
        // 如果后端返回了具体错误信息
        errorMessage = error.response.data
      } else if (error.message) {
        // 如果是前端抛出的错误（如语料创建失败）
        errorMessage = error.message
      }

      // 显示更明确的错误提示
      ElMessage({
        message: errorMessage,
        type: 'error',
        duration: 5000, // 延长显示时间到5秒
        showClose: true // 允许手动关闭
      })
    }
  } finally {
    isSubmitting.value = false
    uploadAbortController = null
  }
}

</script>

<style scoped>
.upload-form-page {
  width: 1200px;
  margin: 0 auto;
  background-color: #ffffff;
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
  color: #8e8e8e;
  line-height: 1.5;
}

.divider {
  height: 1px;
  background-color: #ddd;
  margin: 10px 0;
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

.attachment-section {
  margin-top: 30px;
}

.attachment-section h3 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

/* .file-upload-container {
  margin-top: 20px;
  border: 1px dashed #ccc;
  padding: 20px;
  border-radius: 4px;
} */

.upload-area {
  text-align: center;
  margin-bottom: 20px;
}

.upload-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}

.file-list {
  margin-top: 20px;
}

.file-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.file-number {
  margin-right: 10px;
}

.file-name {
  flex-grow: 1;
}

.delete-link {
  color: #409eff;
  cursor: pointer;
}

.actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 上传进度条样式 */
.upload-progress-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.progress-container {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 500px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.progress-content {
  space-y: 16px;
}

.overall-progress {
  margin-bottom: 16px;
}

.current-file {
  margin-bottom: 16px;
}

.progress-info,
.file-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.status-text {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 12px;
}

/* 语料名称检查状态样式 */
.name-check-status {
  margin-top: 5px;
  font-size: 12px;
}

.name-check-status .success {
  color: #67c23a;
}

.name-check-status .error {
  color: #f56c6c;
}

/* 所属领域单选框样式 - 固定3列 */
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

/* 确保单选框在网格中居中对齐 */
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
