<template>
    <div class="file-manager">
      <el-container>
        <el-aside width="200px" class="sidebar">
          <el-menu default-active="1" class="el-menu-vertical-demo">
            <el-menu-item index="1">全部文件</el-menu-item>
            <el-menu-item index="2">图片</el-menu-item>
            <el-menu-item index="3">文档</el-menu-item>
            <el-menu-item index="4">视频</el-menu-item>
            <el-menu-item index="5">其他</el-menu-item>
            <el-menu-item index="6">回收站</el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <div class="toolbar">
            <el-button type="primary" @click="triggerFileInput">上传文件</el-button>
            <input
              ref="fileInput"
              type="file"
              style="display: none"
              @change="onFileChange"
            />
            <el-button>新建文件夹</el-button>
            <el-button>下载</el-button>
            <el-button type="danger">删除</el-button>
            <el-button>移动到</el-button>
          </div>
          <el-table
            :data="fileList"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            ref="fileTable"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="文件名" />
            <el-table-column prop="size" label="大小" width="100" />
            <el-table-column prop="date" label="修改日期" width="180" />
            <el-table-column prop="uploader" label="上传人" width="120" />
          </el-table>
        </el-main>
      </el-container>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  
  const fileList = ref([])
  const fileInput = ref(null)
  
  onMounted(() => {
    // 页面加载时获取文件列表
    axios.get('/hdfs/files', { params: { path: '/user/root' } }).then(res => {
      fileList.value = res.data
    })
  })
  
  const selectedFiles = ref([])

  function triggerFileInput() {
    fileInput.value.click()
  }

  // 处理文件选择
  function onFileChange(event) {
    const file = event.target.files[0]
    if (!file) return
    handleUpload(file)
    // 清空 input，否则连续上传同一个文件不会触发 change
    event.target.value = ''
  }

  // 上传文件
  function handleUpload(file) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('destPath', '/user/root')
    axios.post('/hdfs/upload', formData).then(res => {
      // 上传成功后刷新文件列表
      axios.get('/hdfs/files', { params: { path: '/user/root' } }).then(res => {
        fileList.value = res.data
      })
    })
  }

  function handleSelectionChange(val) {
    selectedFiles.value = val
  }

  function handleDelete(filePath) {
    axios.delete('/api/file', { params: { path: filePath } }).then(res => {
      // 删除成功后刷新文件列表
      axios.get('/api/files', { params: { path: '/user/root' } }).then(res => {
        fileList.value = res.data
      })
    })
  }
  </script>
  
  <style scoped>
  .file-manager {
    height: 100vh;
    background: #f5f7fa;
  }
  .sidebar {
    background: #fff;
    min-height: 100vh;
    border-right: 1px solid #ebeef5;
  }
  .toolbar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }
  </style>