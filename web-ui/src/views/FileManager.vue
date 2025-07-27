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
            <el-button @click="handleDownload" :disabled="selectedFiles.length === 0">下载选中文件</el-button>
            <el-button>新建文件夹</el-button>
            <el-button type="danger">删除</el-button>
            <el-button>移动到</el-button>
          </div>
          
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item 
              v-for="(item, index) in breadcrumb" 
              :key="index"
              @click="navigateToPath(item.path)"
              :class="{ 'clickable': index < breadcrumb.length - 1 }"
            >
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          
          <el-table
            :data="fileList"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            ref="fileTable"
            @row-dblclick="handleRowDblClick"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="path" label="文件名" min-width="200">
              <template #default="scope">
                <div class="file-item">
                  <i :class="getFileIcon(scope.row)" class="file-icon"></i>
                  <span>{{ getFileName(scope.row.path) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="大小" width="120" />
            <el-table-column prop="owner" label="所有者" width="100" />
            <el-table-column prop="permission" label="权限" width="100" />
            <el-table-column prop="modificationTime" label="修改时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.modificationTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button 
                  size="small" 
                  @click="handleDownloadSingle(scope.row)"
                  :disabled="scope.row.directory"
                >
                  下载
                </el-button>
                <el-button 
                  size="small" 
                  @click="handleOpenFolder(scope.row)"
                  v-if="scope.row.directory"
                >
                  打开
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
      </el-container>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'
  
  const fileList = ref([])
  const fileInput = ref(null)
  const currentPath = ref('/user/root')
  const breadcrumb = ref([
    { name: '根目录', path: '/' },
    { name: 'user', path: '/user' },
    { name: 'root', path: '/user/root' }
  ])
  
  onMounted(() => {
    loadFileList()
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
    formData.append('destPath', currentPath.value)
    axios.post('/hdfs/upload', formData).then(res => {
      ElMessage.success('文件上传成功')
      loadFileList()
    }).catch(error => {
      ElMessage.error('文件上传失败')
    })
  }

  // 加载文件列表
  function loadFileList() {
    axios.get('/hdfs/files', { params: { path: currentPath.value } }).then(res => {
      fileList.value = res.data
    }).catch(error => {
      ElMessage.error('获取文件列表失败')
    })
  }

  // 处理选择变化
  function handleSelectionChange(val) {
    selectedFiles.value = val
  }

  // 下载单个文件
  function handleDownloadSingle(file) {
    if (file.directory) {
      ElMessage.warning('不能下载文件夹')
      return
    }
    downloadFile(file.path)
  }

  // 下载选中的文件
  function handleDownload() {
    if (selectedFiles.value.length === 0) {
      ElMessage.warning('请选择要下载的文件')
      return
    }
    
    selectedFiles.value.forEach(file => {
      if (!file.directory) {
        downloadFile(file.path)
      }
    })
  }

  // 下载文件
  function downloadFile(filePath) {
    const link = document.createElement('a')
    link.href = `/hdfs/download?filePath=${encodeURIComponent(filePath)}`
    link.download = getFileName(filePath)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  // 打开文件夹
  function handleOpenFolder(file) {
    if (file.directory) {
      navigateToPath(file.path)
    }
  }

  // 双击行
  function handleRowDblClick(row) {
    if (row.directory) {
      navigateToPath(row.path)
    } else {
      handleDownloadSingle(row)
    }
  }

  // 导航到指定路径
  function navigateToPath(path) {
    currentPath.value = path
    updateBreadcrumb(path)
    loadFileList()
  }

  // 更新面包屑
  function updateBreadcrumb(path) {
    const parts = path.split('/').filter(part => part)
    breadcrumb.value = [
      { name: '根目录', path: '/' }
    ]
    
    let currentPath = ''
    parts.forEach(part => {
      currentPath += '/' + part
      breadcrumb.value.push({
        name: part,
        path: currentPath
      })
    })
  }

  // 获取文件名
  function getFileName(path) {
    return path.split('/').pop() || path
  }

  // 获取文件图标
  function getFileIcon(file) {
    if (file.directory) {
      return 'el-icon-folder'
    }
    return 'el-icon-document'
  }

  // 格式化日期
  function formatDate(timestamp) {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return date.toLocaleString()
  }

  function handleDelete(filePath) {
    axios.delete('/api/file', { params: { path: filePath } }).then(res => {
      // 删除成功后刷新文件列表
      loadFileList()
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
  .breadcrumb {
    margin-bottom: 20px;
    padding: 10px;
    background: #fff;
    border-radius: 4px;
  }
  .clickable {
    cursor: pointer;
    color: #409eff;
  }
  .clickable:hover {
    text-decoration: underline;
  }
  .file-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .file-icon {
    font-size: 16px;
    color: #909399;
  }
  </style>