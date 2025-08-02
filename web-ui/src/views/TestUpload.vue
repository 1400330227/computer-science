<template>
  <div class="test-upload-page">
    <h2>文件上传测试页面</h2>
    
    <div class="test-section">
      <h3>1. 测试创建语料记录</h3>
      <el-button @click="testCreateCorpus" :loading="creating">测试创建语料</el-button>
      <div v-if="corpusResult" class="result">
        <p>结果: {{ corpusResult }}</p>
      </div>
    </div>

    <div class="test-section">
      <h3>2. 测试文件上传</h3>
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
      >
        <el-button type="primary">选择文件</el-button>
      </el-upload>
      
      <el-button @click="testUpload" :loading="uploading" :disabled="!selectedFile">
        测试上传文件
      </el-button>
      
      <div v-if="uploadResult" class="result">
        <p>结果: {{ uploadResult }}</p>
      </div>
    </div>

    <div class="test-section">
      <h3>3. 测试用户状态</h3>
      <el-button @click="testUserStatus">检查用户状态</el-button>
      <div v-if="userStatus" class="result">
        <p>用户状态: {{ userStatus }}</p>
      </div>
    </div>

    <div class="logs">
      <h3>调试日志</h3>
      <div class="log-content">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          {{ log }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../services/api'

const creating = ref(false)
const uploading = ref(false)
const corpusResult = ref('')
const uploadResult = ref('')
const userStatus = ref('')
const selectedFile = ref(null)
const fileList = ref([])
const logs = ref([])

const addLog = (message) => {
  const timestamp = new Date().toLocaleTimeString()
  logs.value.push(`[${timestamp}] ${message}`)
}

// 测试创建语料记录
const testCreateCorpus = async () => {
  creating.value = true
  corpusResult.value = ''
  
  try {
    addLog('开始测试创建语料记录...')
    
    const testData = {
      country: '测试国家',
      collectionName: '测试语料集',
      domain: '测试领域',
      language: '测试语种',
      dataFormat: '测试格式',
      classification: '测试分类',
      dataYear: '2024',
      sourceLocation: '测试地区',
      dataSource: '测试来源'
    }
    
    addLog('发送请求到 /corpus')
    const response = await api.post('/corpus', testData)
    
    addLog('请求成功，响应: ' + JSON.stringify(response.data))
    corpusResult.value = '成功: ' + JSON.stringify(response.data)
    
  } catch (error) {
    addLog('请求失败: ' + error.message)
    addLog('错误状态: ' + (error.response?.status || 'N/A'))
    addLog('错误数据: ' + JSON.stringify(error.response?.data || 'N/A'))
    
    corpusResult.value = '失败: ' + (error.response?.data || error.message)
  } finally {
    creating.value = false
  }
}

// 处理文件选择
const handleFileChange = (file, uploadFileList) => {
  selectedFile.value = file.raw
  fileList.value = uploadFileList
  addLog('选择了文件: ' + file.name)
}

// 测试文件上传
const testUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择文件')
    return
  }
  
  uploading.value = true
  uploadResult.value = ''
  
  try {
    addLog('开始测试文件上传...')
    
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('corpusId', '1') // 使用测试ID
    
    addLog('发送请求到 /corpus/upload')
    const response = await api.post('/corpus/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    addLog('上传成功，响应: ' + JSON.stringify(response.data))
    uploadResult.value = '成功: ' + JSON.stringify(response.data)
    
  } catch (error) {
    addLog('上传失败: ' + error.message)
    addLog('错误状态: ' + (error.response?.status || 'N/A'))
    addLog('错误数据: ' + JSON.stringify(error.response?.data || 'N/A'))
    
    uploadResult.value = '失败: ' + (error.response?.data || error.message)
  } finally {
    uploading.value = false
  }
}

// 测试用户状态
const testUserStatus = async () => {
  try {
    addLog('检查用户状态...')
    
    const response = await api.get('/users/current')
    
    addLog('用户状态检查成功: ' + JSON.stringify(response.data))
    userStatus.value = '已登录: ' + JSON.stringify(response.data)
    
  } catch (error) {
    addLog('用户状态检查失败: ' + error.message)
    addLog('错误状态: ' + (error.response?.status || 'N/A'))
    
    userStatus.value = '未登录或错误: ' + (error.response?.data || error.message)
  }
}
</script>

<style scoped>
.test-upload-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.result {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.logs {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.log-content {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f8f8f8;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
}

.log-item {
  margin-bottom: 5px;
  word-break: break-all;
}
</style>
