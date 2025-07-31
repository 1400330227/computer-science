<template>
  <div class="upload-form-page">
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
              <strong>数据量：</strong>对应的数据量数字表述
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

              <el-form-item label="语料集名称" prop="datasetName">
                <el-input v-model="formData.datasetName" placeholder="请填写语料集名称"></el-input>
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

              <el-form-item label="数据分类" prop="dataCategory">
                <el-input v-model="formData.dataCategory" placeholder="请填写数据分类"></el-input>
              </el-form-item>

              <el-form-item label="数据量数据单位" prop="dataUnit">
                <el-input v-model="formData.dataUnit" placeholder="请填写数据量单位"></el-input>
              </el-form-item>
            </div>

            <!-- 右侧表单 -->
            <div class="form-column">
              <el-form-item label="容量估算 (GB)" prop="estimatedCapacity">
                <el-input v-model="formData.estimatedCapacity" placeholder="请填写容量估算"></el-input>
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

              <el-form-item label="数据提供方" prop="dataProvider">
                <el-input v-model="formData.dataProvider" placeholder="请填写数据提供方"></el-input>
              </el-form-item>

              <el-form-item label="数据提供方联系方式" prop="providerContact">
                <el-input v-model="formData.providerContact" placeholder="请填写联系方式"></el-input>
              </el-form-item>

              <el-form-item label="备注说明">
                <el-input v-model="formData.remark" placeholder="请填写备注说明（选填）"></el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 附件上传 -->
          <div class="attachment-section">
            <h3>附件</h3>
            <div class="divider"></div>

            <div class="file-upload-container">
              <div class="upload-area">
                <el-upload class="upload-demo" drag action="/hdfs/corpus/upload" :before-upload="beforeUpload"
                  :on-success="handleUploadSuccess" :on-error="handleUploadError" :on-change="handleFileChange"
                  :file-list="fileList" multiple>
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">
                    可同时选择多个文件，上限 10.00GB
                  </div>
                </el-upload>
              </div>

              <div class="file-list">
                <div v-for="(file, index) in fileList" :key="index" class="file-item">
                  <div class="file-number">{{ index + 1 }}.</div>
                  <div class="file-name">{{ file.name }}</div>
                  <a class="delete-link" @click="removeFile(file)">删除</a>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <el-button type="primary" @click="saveForm" :loading="isSubmitting">保存</el-button>
            <el-button @click="saveAndCreate" :loading="isSubmitting">再新增</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, inject, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import api from '../services/api'

const router = useRouter()
const uploadForm = ref(null)
const isSubmitting = ref(false)

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
  datasetName: [
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
  dataCategory: [
    { required: true, message: '请输入数据分类', trigger: 'blur' }
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
  datasetName: '',
  domain: '',
  language: '',
  dataFormat: '',
  dataCategory: '',
  dataUnit: '',
  estimatedCapacity: '',
  dataYear: '',
  sourceLocation: '',
  dataSource: '',
  dataProvider: '',
  providerContact: '',
  remark: ''
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
const handleFileChange = (file, fileList) => {
  // 更新文件列表
}

// 文件上传成功处理
const handleUploadSuccess = (response, file, fileList) => {
  ElMessage.success('文件上传成功')
}

// 文件上传失败处理
const handleUploadError = (error, file, fileList) => {
  ElMessage.error('文件上传失败，请重试')
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

    // 提交表单数据
    const response = await api.post('/hdfs/corpus', formData)

    if (response.data && response.data.success) {
      ElMessage.success('保存成功')
      router.push('/file-list')
    } else {
      ElMessage.warning(response.data?.message || '保存失败，请重试')
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(error.response?.data?.message || '提交表单失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 保存并创建新表单
const saveAndCreate = async () => {
  if (isSubmitting.value) return

  isSubmitting.value = true

  try {
    // 表单验证
    await uploadForm.value.validate()

    // 提交表单数据
    const response = await api.post('/hdfs/corpus', formData)

    if (response.data && response.data.success) {
      ElMessage.success('保存成功')

      // 重置表单
      Object.keys(formData).forEach(key => {
        formData[key] = ''
      })
      fileList.value = []

      // 重置表单校验状态
      uploadForm.value.resetFields()
    } else {
      ElMessage.warning(response.data?.message || '保存失败，请重试')
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(error.response?.data?.message || '提交表单失败，请稍后重试')
  } finally {
    isSubmitting.value = false
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
</style>
