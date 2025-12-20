<template>
    <div class="all-files">
        <div class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form" label-width="100">
                <!-- 第一行：基础筛选条件 -->
                <el-form-item label="文件名">
                    <el-input v-model="searchForm.fileName" placeholder="按文件名搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                        <template #prefix>
                            <Search />
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="数据模态">
                    <el-select v-model="searchForm.dataFormat" placeholder="选择数据模态" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option label="文本" value="文本" />
                        <el-option label="图像" value="图像" />
                        <el-option label="音频" value="音频" />
                        <el-option label="视频" value="视频" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="所有者">
                    <el-input v-model="searchForm.creatorNickname" placeholder="按所有者姓名搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <!-- 第二行：语料相关筛选条件 -->
                <el-form-item label="语料名称">
                    <el-input v-model="searchForm.corpusName" placeholder="按语料名称搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                        <template #prefix>
                            <Search />
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="国家">
                    <el-select v-model="searchForm.country" placeholder="选择国家" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option v-for="country in aseanCountries" :key="country.value" :label="country.label"
                            :value="country.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="所属领域">
                    <el-select v-model="searchForm.domain" placeholder="选择所属领域" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option v-for="domain in corpusData.domains" :key="domain.domainName"
                            :label="domain.domainName" :value="domain.domainName">
                        </el-option>
                    </el-select>
                </el-form-item>

                <!-- 第三行：更多语料筛选条件 -->
                <el-form-item label="语言">
                    <el-select v-model="searchForm.language" placeholder="选择语言" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option v-for="language in corpusData.languages" :key="language.language"
                            :label="language.language" :value="language.language">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!-- <el-form-item label="数据模态">
                    <el-input v-model="searchForm.dataFormat" placeholder="按数据模态搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item> -->
                <el-form-item label="数据分类">
                    <el-select v-model="searchForm.classification" placeholder="选择数据分类" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option v-for="classification in corpusData.classifications"
                            :key="classification.classificationName" :label="classification.classificationName"
                            :value="classification.classificationName">
                        </el-option>
                    </el-select>
                </el-form-item>

                <!-- 第四行：年份和学院筛选 -->
                <el-form-item label="时间范围">
                    <el-date-picker v-model="searchForm.startDataYear" type="year" placeholder="" style="width: 113px;"
                        @change="handleSearch" clearable format="YYYY" value-format="YYYY">
                    </el-date-picker>
                    <span class="text-gray-500">-</span>
                    <el-date-picker v-model="searchForm.endDataYear" type="year" placeholder="" style="width: 113px;"
                        @change="handleSearch" clearable format="YYYY" value-format="YYYY">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="学院">
                    <el-select v-model="searchForm.creatorCollege" placeholder="选择学院" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option-group v-for="group in corpusData.collegeGroups" :key="group.label"
                            :label="group.label">
                            <el-option v-for="option in group.options" :key="option" :label="option" :value="option">
                            </el-option>
                        </el-option-group>
                    </el-select>
                </el-form-item>

                <!-- 操作按钮 -->
                <el-form-item label="   ">
                    <el-button type="primary" @click="handleSearch" :icon="Search">
                        搜索
                    </el-button>
                    <el-button @click="handleReset" :icon="Refresh">
                        重置
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="table-card">
            <!-- 批量操作工具栏 -->
            <div class="batch-toolbar">
                <span class="selected-count">已选择 {{ selectedFiles.length }} 个文件</span>
                <el-button @click="handleBatchDownload" :icon="Download">
                    批量下载
                </el-button>
                <el-button @click="clearSelection" :icon="Close">
                    取消选择
                </el-button>
            </div>

            <el-table :data="files" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange"
                ref="tableRef">
                <!-- 多选列 -->
                <el-table-column type="selection" width="55" />

                <el-table-column prop="fileName" label="文件" min-width="200">
                    <template #default="{row}">
                      <div>
                        <router-link :to="`/corpus-management-details/${row.corpusId}`">
                        {{ row.fileName }}
                        </router-link>
                        <a :href="getDownloadUrl(row)" style="margin-left: 4px" title="下载语料" download>
                          下载
                        </a>
                      </div>
                        <div style="color:#999999">{{ row.dataFormat }} | {{ formatFileSize(row.size)}}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="corpusName" label="语料" width="160">
                  <template #default="{row}">
                    {{row.corpusName}}
                    <div style="color:#999999">
                      {{ row.corpusCountry }} | {{ row.corpusDomain }}领域</div>
                  </template>
                </el-table-column>
                <el-table-column prop="creatorCollege" label="所有者" width="190">
                  <template #default="{row}">
                    <div>{{row.creatorCollege}}</div>
                    <div>{{row.creatorNickname}}</div>
                  </template>
                </el-table-column>

                <el-table-column prop="corpusClassification" label="数据分类" width="110" />
                <el-table-column prop="corpusDataYear" label="标注文件" min-width="200">
                  <template #default="{row}">
                    {{row?.annotationFile?.title}}
                    <a v-if="row.annotationFile" :href="getDownloadUrl(row)" style="margin-left: 4px" class="download-link" title="下载语料" download>
                      下载
                    </a>
                    <div style="color:#999999" v-if="row.annotationFile">{{ row.annotationFile?.qaPairCount}}对问答</div>
                  </template>
                </el-table-column>
                <el-table-column prop="annotationFile.annotationNickname" label="标注人" width="100" />
                <el-table-column label="操作" width="100">
                    <template #default="{row}">
                      <router-link :to="`/corpus-management-details/${row.corpusId}`" style="margin-left: 12px">
                        标注
                      </router-link>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-wrapper">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[20, 50, 100, 500]" :small="false" :disabled="false" :background="true"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handlePageChange" />
            </div>
        </div>
    </div>

    <!-- 上传标注对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传问答对标注文件" width="500px" @close="resetUpload">
      <div v-if="currentFile">
        <p>正在为以下文件上传标注：</p>
        <p><strong>{{ currentFile.fileName }}</strong></p>
      </div>
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        :limit="1"
        :auto-upload="false"
        :on-change="handleFileChange"
        :http-request="handleUpload"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            请上传与原始文件主名相同的 .txt 文件。
          </div>
        </template>
      </el-upload>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="isUploading">
            确认上传
          </el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Download, User, Close, UploadFilled } from '@element-plus/icons-vue';
import { getAllFiles } from '@/services/admin';
import { uploadAnnotation } from '@/services/annotation';
import corpusData from '@/assets/corpus.json';

// Reactive data
const files = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);
const selectedFiles = ref([]);
const tableRef = ref();

const uploadDialogVisible = ref(false);
const isUploading = ref(false);
const currentFile = ref(null);
const fileToUpload = ref(null);
const uploadRef = ref();

const searchForm = reactive({
    fileName: '',
    dataFormat: '',
    creatorNickname: '',
    // 新增的语料相关筛选字段
    corpusName: '',
    country: '',
    domain: '',
    language: '',
    classification: '',
    startDataYear: '',
    endDataYear: '',
    creatorCollege: ''
});

// 学院选项数据

// 中国和东盟十国选项
const aseanCountries = ref([
    { label: '中国', value: '中国' },
    { label: '文莱', value: '文莱' },
    { label: '柬埔寨', value: '柬埔寨' },
    { label: '印度尼西亚', value: '印度尼西亚' },
    { label: '老挝', value: '老挝' },
    { label: '马来西亚', value: '马来西亚' },
    { label: '缅甸', value: '缅甸' },
    { label: '菲律宾', value: '菲律宾' },
    { label: '新加坡', value: '新加坡' },
    { label: '泰国', value: '泰国' },
    { label: '越南', value: '越南' }
]);

// Methods
const fetchFiles = async () => {
    loading.value = true;
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            ...searchForm
        };
        // 移除空值
        Object.keys(params).forEach(key => {
            if (params[key] === '' || params[key] === null || params[key] === undefined) {
                delete params[key];
            }
        });

        let response = await getAllFiles(params);
        response = response.data
        if (response.success) {
            files.value = response.data.list || [];
            total.value = response.data.total || 0;
        } else {
            ElMessage.error(response.message || '获取文件列表失败');
        }
    } catch (error) {
        console.error('获取文件列表失败:', error);
        ElMessage.error('获取文件列表失败');
    } finally {
        loading.value = false;
    }
};

const handlePageChange = (page) => {
    currentPage.value = page;
    fetchFiles();
};

const handleSizeChange = (size) => {
    pageSize.value = size;
    currentPage.value = 1;
    fetchFiles();
};

const handleSearch = () => {
    currentPage.value = 1;
    fetchFiles();
};

const handleReset = () => {
    Object.keys(searchForm).forEach(key => {
        searchForm[key] = '';
    });
    currentPage.value = 1;
    fetchFiles();
};

// 多选相关方法
const handleSelectionChange = (selection) => {
    selectedFiles.value = selection;
};

const clearSelection = () => {
    if (tableRef.value) {
        tableRef.value.clearSelection();
    }
    selectedFiles.value = [];
};

// 下载相关方法
const downloadSingleFile = (file) => {
    const downloadUrl = `/api/admin/download/${file.fileId}`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = file.fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};

const handleBatchDownload = async () => {
    if (selectedFiles.value.length === 0) {
        ElMessage.warning('请先选择要下载的文件');
        return;
    }

    try {
        // 如果只有一个文件，直接下载
        if (selectedFiles.value.length === 1) {
            downloadSingleFile(selectedFiles.value[0]);
            return;
        }

        // 多个文件，创建ZIP下载
        const fileIds = selectedFiles.value.map(file => file.fileId);
        const downloadUrl = `/api/admin/batch-download?fileIds=${fileIds.join(',')}`;

        // 创建临时链接下载
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `批量下载_${new Date().getTime()}.zip`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        ElMessage.success(`开始下载 ${selectedFiles.value.length} 个文件`);
    } catch (error) {
        console.error('批量下载失败:', error);
        ElMessage.error('批量下载失败');
    }
};

const getDownloadUrl = (file) => {
    return `/api/admin/download/${file.fileId}`;
};



const formatFileSize = (sizeInBytes) => {
    if (!sizeInBytes || sizeInBytes === 0) return '0.00B';
    const kb = 1024
    const mb = kb * 1024
    const gb = mb * 1024

    if (Math.abs(sizeInBytes) < kb) {
      return sizeInBytes.toFixed(2) + ' B'
    } else if (Math.abs(sizeInBytes) < mb) {
      return (sizeInBytes / kb).toFixed(2) + ' KB'
    } else if (Math.abs(sizeInBytes) < gb) {
      return (sizeInBytes / mb).toFixed(2) + ' MB'
    } else {
      return (sizeInBytes / gb).toFixed(2) + ' GB'
    }
};


const handleFileChange = (file) => {
  fileToUpload.value = file;
};

const resetUpload = () => {
  currentFile.value = null;
  fileToUpload.value = null;
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
};

const submitUpload = () => {
  if (!fileToUpload.value) {
    ElMessage.error('请先选择一个文件');
    return;
  }
  uploadRef.value.submit();
};

const handleUpload = async () => {
  if (!currentFile.value || !fileToUpload.value) return;

  isUploading.value = true;
  try {
    const response = await uploadAnnotation(fileToUpload.value.raw, currentFile.value.fileId);
    ElMessage.success(`文件上传成功！包含 ${response.data.qaPairCount} 个问答对。`);
    uploadDialogVisible.value = false;
  } catch (error) {
    const errorMessage = error.response?.data || '上传失败，请稍后再试';
    ElMessage.error(errorMessage);
  } finally {
    isUploading.value = false;
  }
};

onMounted(fetchFiles);
</script>

<style scoped>
.all-files {
    max-width: 1200px;
    margin: 0 auto;
}

.page-header {
    margin-bottom: 20px;
}

.page-header h2 {
    margin: 0 0 8px 0;
    color: #303133;
    font-size: 20px;
    font-weight: 600;
}

.page-header p {
    margin: 0;
    color: #606266;
    font-size: 14px;
}

.search-card {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #ffffff;
    border-radius: 4px;
}

.search-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
}

.table-card {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #ffffff;
}

.batch-toolbar {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    /* background-color: #f5f7fa; */
    border-bottom: 1px solid #e4e7ed;
    /* border-radius: 4px; */
    /* margin-bottom: 16px; */
}

.selected-count {
    color: #409eff;
    font-weight: 500;
}

.pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.upload-demo {
  margin-top: 20px;
}
</style>
