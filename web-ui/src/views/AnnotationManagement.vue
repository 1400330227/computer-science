<template>
  <div class="annotation-management">
    <div class="page-header">
      <h2>标注管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        新建标注
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-container">
      <el-form :model="searchForm" :inline="true" label-position="left" label-width="auto" @submit.prevent>
        <el-form-item label="语料ID">
          <el-input v-model.number="searchForm.corpusId" placeholder="请输入语料ID" clearable />
        </el-form-item>
        <el-form-item label="文件ID">
          <el-input v-model.number="searchForm.fileId" placeholder="请输入文件ID" clearable />
        </el-form-item>
        <el-form-item label="标注类型">
          <el-input v-model="searchForm.annotationType" placeholder="请输入标注类型" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 标注列表 -->
    <div class="annotation-list-container">
      <el-table v-loading="loading" :data="annotationList" style="width: 100%">
        <el-table-column prop="annotationId" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="corpusId" label="语料ID" width="100" />
        <el-table-column prop="fileId" label="文件ID" width="100" />
        <el-table-column prop="annotationType" label="标注类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewAnnotation(row)">查看</el-button>
            <el-button link type="primary" @click="editAnnotation(row)">编辑</el-button>
            <el-button link type="danger" @click="deleteAnnotation(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无标注数据" />
        </template>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 创建/编辑标注对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingAnnotation ? '编辑标注' : '新建标注'"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form :model="annotationForm" :rules="rules" ref="annotationFormRef" label-width="100px">
        <el-form-item label="语料ID" prop="corpusId">
          <el-input-number v-model="annotationForm.corpusId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="文件ID" prop="fileId">
          <el-input-number v-model="annotationForm.fileId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="annotationForm.title" placeholder="请输入标注标题" />
        </el-form-item>
        <el-form-item label="标注类型" prop="annotationType">
          <el-select v-model="annotationForm.annotationType" placeholder="请选择标注类型" style="width: 100%">
            <el-option
              v-for="label in availableLabels"
              :key="label.labelId"
              :label="label.labelName"
              :value="label.labelName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="annotationForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待审核" value="待审核" />
            <el-option label="已审核" value="已审核" />
            <el-option label="已发布" value="已发布" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="annotationForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入标注内容"
          />
        </el-form-item>
        <el-form-item label="文本片段">
          <el-input
            v-model="annotationForm.textSegment"
            type="textarea"
            :rows="3"
            placeholder="请输入标注的文本片段（可选）"
          />
        </el-form-item>
        <el-form-item label="起始位置">
          <el-input-number v-model="annotationForm.startPosition" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束位置">
          <el-input-number v-model="annotationForm.endPosition" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="annotationForm.remarks"
            type="textarea"
            :rows="2"
            placeholder="请输入备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ editingAnnotation ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看标注详情对话框 -->
    <el-dialog v-model="showViewDialog" title="标注详情" width="800px">
      <el-descriptions :column="2" border v-if="viewingAnnotation">
        <el-descriptions-item label="标注ID">{{ viewingAnnotation.annotationId }}</el-descriptions-item>
        <el-descriptions-item label="语料ID">{{ viewingAnnotation.corpusId }}</el-descriptions-item>
        <el-descriptions-item label="文件ID">{{ viewingAnnotation.fileId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="标注类型">{{ viewingAnnotation.annotationType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewingAnnotation.status)">{{ viewingAnnotation.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(viewingAnnotation.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ viewingAnnotation.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ viewingAnnotation.content }}</el-descriptions-item>
        <el-descriptions-item label="文本片段" :span="2" v-if="viewingAnnotation.textSegment">
          {{ viewingAnnotation.textSegment }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="viewingAnnotation.remarks">
          {{ viewingAnnotation.remarks }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { annotationApi } from '@/services/annotation'

// 数据
const loading = ref(false)
const annotationList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const showCreateDialog = ref(false)
const showViewDialog = ref(false)
const editingAnnotation = ref(null)
const viewingAnnotation = ref(null)
const submitting = ref(false)
const availableLabels = ref([])

// 搜索表单
const searchForm = reactive({
  corpusId: null,
  fileId: null,
  annotationType: ''
})

// 标注表单
const annotationForm = reactive({
  corpusId: null,
  fileId: null,
  title: '',
  annotationType: '',
  status: '待审核',
  content: '',
  textSegment: '',
  startPosition: null,
  endPosition: null,
  remarks: ''
})

const annotationFormRef = ref(null)

// 表单验证规则
const rules = {
  corpusId: [{ required: true, message: '请输入语料ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 加载标注列表
const loadAnnotations = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    const response = await annotationApi.getAnnotations(params)
    annotationList.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载标注列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 加载标签列表
const loadLabels = async () => {
  try {
    const response = await annotationApi.getLabels()
    availableLabels.value = response.data || []
  } catch (error) {
    console.error('加载标签列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadAnnotations()
}

// 重置
const handleReset = () => {
  searchForm.corpusId = null
  searchForm.fileId = null
  searchForm.annotationType = ''
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadAnnotations()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadAnnotations()
}

// 查看标注
const viewAnnotation = (row) => {
  viewingAnnotation.value = row
  showViewDialog.value = true
}

// 编辑标注
const editAnnotation = (row) => {
  editingAnnotation.value = row
  Object.assign(annotationForm, {
    corpusId: row.corpusId,
    fileId: row.fileId,
    title: row.title,
    annotationType: row.annotationType,
    status: row.status,
    content: row.content,
    textSegment: row.textSegment || '',
    startPosition: row.startPosition,
    endPosition: row.endPosition,
    remarks: row.remarks || ''
  })
  showCreateDialog.value = true
}

// 删除标注
const deleteAnnotation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该标注吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await annotationApi.deleteAnnotation(row.annotationId)
    ElMessage.success('删除成功')
    loadAnnotations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!annotationFormRef.value) return
  await annotationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (editingAnnotation.value) {
          await annotationApi.updateAnnotation(editingAnnotation.value.annotationId, annotationForm)
          ElMessage.success('更新成功')
        } else {
          await annotationApi.createAnnotation(annotationForm)
          ElMessage.success('创建成功')
        }
        showCreateDialog.value = false
        loadAnnotations()
      } catch (error) {
        ElMessage.error((editingAnnotation.value ? '更新' : '创建') + '失败: ' + (error.response?.data?.message || error.message))
      } finally {
        submitting.value = false
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  editingAnnotation.value = null
  Object.assign(annotationForm, {
    corpusId: null,
    fileId: null,
    title: '',
    annotationType: '',
    status: '待审核',
    content: '',
    textSegment: '',
    startPosition: null,
    endPosition: null,
    remarks: ''
  })
  if (annotationFormRef.value) {
    annotationFormRef.value.clearValidate()
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已审核': 'success',
    '已发布': 'info'
  }
  return statusMap[status] || ''
}

// 初始化
onMounted(() => {
  loadAnnotations()
  loadLabels()
})
</script>

<style scoped lang="scss">
.annotation-management {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
    }
  }

  .search-container {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 20px;
  }

  .annotation-list-container {
    background: #fff;
    padding: 20px;
    border-radius: 4px;

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>

