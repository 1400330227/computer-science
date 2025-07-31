<template>
  <div class="upload-form-page">
    <!-- 顶部导航栏 -->


    

    <div class="upload-form">
      <div class="page-header">
        <h1>语料详细信息</h1>
        <div class="divider"></div>
      </div>

      <div class="form-container">
        <el-form ref="uploadForm" :model="formData" label-width="140px" class="upload-form">
          <div class="form-content">
            <!-- 左侧表单 -->
            <div class="form-column">
              <el-form-item label="国家">
                <el-input v-model="formData.country"></el-input>
              </el-form-item>

              <el-form-item label="语料集名称">
                <el-input v-model="formData.datasetName"></el-input>
              </el-form-item>

              <el-form-item label="所属领域">
                <el-input v-model="formData.domain"></el-input>
              </el-form-item>

              <el-form-item label="语种">
                <el-input v-model="formData.language"></el-input>
              </el-form-item>

              <el-form-item label="数据形式">
                <el-input v-model="formData.dataFormat"></el-input>
              </el-form-item>

              <el-form-item label="数据分类">
                <el-input v-model="formData.dataCategory"></el-input>
              </el-form-item>

              <el-form-item label="数据量数据单位">
                <el-input v-model="formData.dataUnit"></el-input>
              </el-form-item>
            </div>

            <!-- 右侧表单 -->
            <div class="form-column">
              <el-form-item label="容量估算 (GB)">
                <el-input v-model="formData.estimatedCapacity"></el-input>
              </el-form-item>

              <el-form-item label="数据年份">
                <el-input v-model="formData.dataYear"></el-input>
              </el-form-item>

              <el-form-item label="来源归属地">
                <el-input v-model="formData.sourceLocation"></el-input>
              </el-form-item>

              <el-form-item label="数据来源">
                <el-input v-model="formData.dataSource"></el-input>
              </el-form-item>

              <el-form-item label="数据提供方">
                <el-input v-model="formData.dataProvider"></el-input>
              </el-form-item>

              <el-form-item label="数据提供方联系方式">
                <el-input v-model="formData.providerContact"></el-input>
              </el-form-item>

              <el-form-item label="备注说明">
                <el-input v-model="formData.remark"></el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 附件上传 -->
          <div class="attachment-section">
            <h3>附件</h3>
            <div class="divider"></div>

            <div class="file-upload-container">
              <div class="upload-area">
                <el-upload class="upload" action="#" :on-change="handleFileChange" :before-upload="beforeUpload"
                  :file-list="fileList" :auto-upload="false" multiple>
                  <el-button type="primary">选择文件</el-button>
                  <div class="upload-tip">可同时选择多个文件，上限 10.00GB</div>
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
            <el-button type="primary" @click="saveForm">保存</el-button>
            <el-button @click="saveAndCreate">再新增</el-button>
            <el-button @click="goBack">返回</el-button>
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

const router = useRouter()

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

// 移除文件
const removeFile = (file) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 保存表单
const saveForm = () => {
  // 在这里处理表单提交
  ElMessage.success('保存成功')
}

// 保存并创建新表单
const saveAndCreate = () => {
  saveForm()
  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = ''
  })
  fileList.value = []
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.upload-form-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 顶部导航样式 */
.top-nav {
  background-color: #5a8de1;
  padding: 0 20px;
  height: 50px;
  display: flex;
  align-items: center;
  color: white;
}

.nav-links {
  display: flex;
}

.nav-link {
  padding: 0 20px;
  color: white;
  text-decoration: none;
  height: 50px;
  line-height: 50px;
  transition: background-color 0.2s;
}

.nav-link:hover,
.nav-link.active {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 面包屑导航样式 */
.breadcrumb {
  padding: 10px 20px;
  color: #666;
  background-color: white;
  font-size: 12px;
}

.breadcrumb .active {
  color: #333;
  font-weight: 500;
}

.upload-form {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
}

.divider {
  height: 1px;
  background-color: #ddd;
  margin: 10px 0;
}

.form-container {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
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

.file-upload-container {
  margin-top: 20px;
  border: 1px dashed #ccc;
  padding: 20px;
  border-radius: 4px;
}

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
