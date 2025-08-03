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
          <h3>语料集信息</h3>
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
              <strong>数据分类：</strong>包括：基础语料、预训练语料、SFT语料、强化学习语料、平行语料、价值观语料、ASR语料、TTS语料，其中除原始语料之外的都属于加工语料。
            </p>
            <p class="info-text">
              <strong>数据量：</strong>对应的数据量数字表述,如1000
            </p>
            <p class="info-text">
              <strong>数据量单位：</strong>对应的数据量表述单位，如GB、条、份、本、小时、篇等
            </p>
            <p class="info-text">
              <strong>容量估算（GB）：</strong>按电子格式存储的数据容量估算，此处按GB计算
            </p>
            <p class="info-text">
              <strong>数据年份：</strong>数据归属的年份范围，如2000年以后
            </p>
            <p class="info-text">
              <strong>来源归属地：</strong>注明数据来源地区，如广西xxx、老挝xx部、xx学院
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
          </div>
          <div class="form-content">
            <!-- 左侧表单 -->
            <div class="form-column">
              <el-form-item label="国家" prop="country">
                <el-input v-model="formData.country" placeholder="请填写国家"></el-input>
              </el-form-item>

              <el-form-item label="语料集名称" prop="collectionName">
                <el-input v-model="formData.collectionName" placeholder="请填写语料集名称"></el-input>
              </el-form-item>

              <el-form-item label="所属领域" prop="domain">
                <el-input v-model="formData.domain" placeholder="请填写所属领域"></el-input>
              </el-form-item>

              <el-form-item label="语种" prop="language">
                <el-input v-model="formData.language" placeholder="请填写语种"></el-input>
              </el-form-item>

              <el-form-item label="数据形式" prop="dataFormat">
                <el-input v-model="formData.dataFormat" placeholder="请填写数据形式"></el-input>
              </el-form-item>

              <el-form-item label="数据分类" prop="classification">
                <el-input v-model="formData.classification" placeholder="请填写数据分类"></el-input>
              </el-form-item>

              <el-form-item label="数据量" prop="dataVolume">
                <el-input v-model="formData.dataVolume" type="number" placeholder="请填写数据量"></el-input>
              </el-form-item>

              <el-form-item label="数据量单位" prop="volumeUnit">
                <el-input v-model="formData.volumeUnit" placeholder="请填写数据量单位"></el-input>
              </el-form-item>
            </div>

            <!-- 右侧表单 -->
            <div class="form-column">
              <el-form-item label="容量估算 (GB)" prop="estimatedCapacityGb">
                <el-input v-model="formData.estimatedCapacityGb" type="number" placeholder="请填写容量估算"
                  disabled></el-input>
              </el-form-item>

              <el-form-item label="数据年份" prop="dataYear">
                <el-input v-model="formData.dataYear" placeholder="请填写数据年份"></el-input>
              </el-form-item>

              <el-form-item label="来源归属地" prop="sourceLocation">
                <el-input v-model="formData.sourceLocation" placeholder="请填写来源归属地"></el-input>
              </el-form-item>

              <el-form-item label="数据来源" prop="dataSource">
                <el-input v-model="formData.dataSource" placeholder="请填写数据来源"></el-input>
              </el-form-item>

              <el-form-item label="数据提供方" prop="provider">
                <el-input v-model="formData.provider" placeholder="请填写数据提供方"></el-input>
              </el-form-item>

              <el-form-item label="提供方联系方式" prop="providerContact">
                <el-input v-model="formData.providerContact" placeholder="请填写数据提供方联系方式"></el-input>
              </el-form-item>

              <el-form-item label="备注说明">
                <el-input v-model="formData.remarks" placeholder="请填写备注说明（选填）"></el-input>
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
                  :on-change="handleFileChange" :file-list="fileList" multiple>
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">
                    可同时选择多个文件，上限 10.00GB
                  </div>
                </el-upload>
              </div>

              <!-- <div class="file-list">
                <div v-for="(file, index) in fileList" :key="index" class="file-item">
                  <div class="file-number">{{ index + 1 }}.</div>
                  <div class="file-name">{{ file.name }}</div>
                  <a class="delete-link" @click="removeFile(file)">删除</a>
                </div>
              </div> -->
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <el-button type="primary" @click="saveForm" :loading="isSubmitting">保存</el-button>
            <el-button @click="saveAndCreate" :loading="isSubmitting">上传并新增</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, inject, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { useRouter } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import api from '../services/api'

const router = useRouter()
const uploadForm = ref(null)
const isSubmitting = ref(false)

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
})

// 表单验证规则
const rules = {
  country: [
    { required: true, message: '请输入国家', trigger: 'blur' }
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
    { required: true, message: '请填写容量估算', trigger: 'blur' }
  ],
  dataYear: [
    { required: true, message: '请输入数据年份', trigger: 'blur' }
  ],
  sourceLocation: [
    { required: true, message: '请输入来源归属地', trigger: 'blur' }
  ],
  dataSource: [
    { required: true, message: '请输入数据来源', trigger: 'blur' }
  ]
}

const formData = reactive({
  country: '',
  collectionName: '',
  domain: '',
  language: '',
  dataFormat: '',
  classification: '',
  dataVolume: null,
  volumeUnit: '',
  estimatedCapacityGb: '0.00',
  dataYear: '',
  sourceLocation: '',
  dataSource: '',
  provider: '',
  providerContact: '',
  remarks: ''
})

const fileList = ref([])



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

// 处理文件变更
const handleFileChange = (file, uploadFileList) => {
  // 更新文件列表，只保留文件信息，不进行上传
  fileList.value = uploadFileList.map(uploadFile => ({
    name: uploadFile.name,
    size: uploadFile.size,
    raw: uploadFile.raw  // 保存原始文件对象，用于后续上传
  }))

  // 计算所有文件的总大小（以GB为单位）
  let totalSizeInBytes = 0
  uploadFileList.forEach(file => {
    totalSizeInBytes += file.size
  })

  // 转换为GB并保留2位小数
  const totalSizeInGB = (totalSizeInBytes / (1024 * 1024 * 1024)).toFixed(2)

  // 自动填充容量估算字段
  formData.estimatedCapacityGb = totalSizeInGB
}

// 移除文件
const removeFile = (file) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 保存表单
const saveForm = async () => {
  if (isSubmitting.value) return

  isSubmitting.value = true

  try {
    // 表单验证
    await uploadForm.value.validate()

    // 初始化进度条
    const totalFiles = fileList.value.length
    uploadProgress.show = true
    uploadProgress.currentFile = 0
    uploadProgress.totalFiles = totalFiles
    uploadProgress.overallPercent = 0
    uploadProgress.status = '正在创建语料记录...'
    uploadProgress.completed = false

    // 第一步：创建语料记录
    const corpusResponse = await api.post('/corpus', formData)

    if (!corpusResponse.data) {
      throw new Error('创建语料失败')
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
    } else if (error.response?.status !== 401) {
      ElMessage.error(error.response?.data || error.message || '上传失败，请稍后重试')
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

    // 初始化进度条
    const totalFiles = fileList.value.length
    uploadProgress.show = true
    uploadProgress.currentFile = 0
    uploadProgress.totalFiles = totalFiles
    uploadProgress.overallPercent = 0
    uploadProgress.status = '正在创建语料记录...'
    uploadProgress.completed = false
    uploadProgress.title = '上传并新增...'

    // 第一步：创建语料记录
    const corpusResponse = await api.post('/corpus', formData)

    if (!corpusResponse.data) {
      throw new Error('创建语料失败')
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
    Object.keys(formData).forEach(key => {
      formData[key] = ''
    })
    fileList.value = []

    // 重置表单校验状态
    uploadForm.value.resetFields()

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
      ElMessage.error(error.response?.data || error.message || '上传失败，请稍后重试')
    }
  } finally {
    isSubmitting.value = false
    uploadAbortController = null
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
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
</style>
