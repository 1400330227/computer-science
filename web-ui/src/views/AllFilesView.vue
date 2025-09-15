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
                <el-form-item label="文件类型">
                    <el-select v-model="searchForm.fileType" placeholder="选择文件类型" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option label="文本" value="txt" />
                        <!-- <el-option label="Word文档" value="doc" /> -->
                        <!-- <el-option label="Excel文档" value="xlsx" /> -->
                        <!-- <el-option label="PDF文档" value="pdf" /> -->
                        <el-option label="图片" value="jpg" />
                        <el-option label="音频" value="mp3" />
                        <el-option label="视频" value="mp4" />
                        <el-option label="其他" value="other" />
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
                    <el-input v-model="searchForm.country" placeholder="按国家搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>
                <el-form-item label="所属领域">
                    <el-input v-model="searchForm.domain" placeholder="按所属领域搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>

                <!-- 第三行：更多语料筛选条件 -->
                <el-form-item label="语种">
                    <el-input v-model="searchForm.language" placeholder="按语种搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>
                <el-form-item label="数据模态">
                    <el-input v-model="searchForm.dataFormat" placeholder="按数据模态搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>
                <el-form-item label="数据分类">
                    <el-input v-model="searchForm.classification" placeholder="按数据分类搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>

                <!-- 第四行：年份和文件大小筛选 -->
                <el-form-item label="数据年份">
                    <el-input v-model="searchForm.dataYear" placeholder="按数据年份搜索" style="width: 230px;"
                        @keyup.enter="handleSearch" clearable @clear="handleSearch">
                    </el-input>
                </el-form-item>
                <el-form-item label="大小(GB)">
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <el-input v-model="searchForm.minFileSize" placeholder="最小值" style="width: 105px;"
                            @keyup.enter="handleSearch" clearable>
                        </el-input>
                        <span>-</span>
                        <el-input v-model="searchForm.maxFileSize" placeholder="最大值" style="width: 105px;"
                            @keyup.enter="handleSearch" clearable>
                        </el-input>
                    </div>
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

                <el-table-column prop="fileName" label="文件" min-width="300">
                    <template #default="scope">
                        <div>{{ scope.row.fileName }}</div>
                        <div style="color:#999999"> 详情：{{ scope.row.corpusDomain }}领域 | {{ scope.row.corpusDataYear }}数据
                            | {{ formatFileSize(scope.row.size) }}GB</div>
                    </template>
                </el-table-column>
                <el-table-column prop="corpusName" label="语料名称" width="200">
                    <template #default="scope">
                        <div>
                            <span>{{ scope.row.corpusName }}</span>
                            <el-tag type="primary"> {{ scope.row.corpusClassification }}</el-tag>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="creatorCollege" label="学院" width="200"></el-table-column>
                <el-table-column prop="creatorNickname" label="所有者" width="100" />
                <el-table-column prop="createdAt" label="上传时间" width="110">
                    <template #default="scope">
                        {{ formatDate(scope.row.createdAt) }}
                    </template>
                </el-table-column>
                <el-table-column prop="corpusLanguage" label="语言" width="100" />
                <el-table-column label="操作" width="90">
                    <template #default="scope">
                        <a :href="getDownloadUrl(scope.row)" class="download-link" title="下载语料" download>
                            下载
                        </a>
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Download, Delete, User, Close } from '@element-plus/icons-vue';
import { getAllFiles, deleteFileById } from '@/services/admin';

// Reactive data
const files = ref([]);
const loading = ref(false);
const deleting = ref(null);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);
const selectedFiles = ref([]);
const tableRef = ref();

const searchForm = reactive({
    fileName: '',
    fileType: '',
    creatorNickname: '',
    // 新增的语料相关筛选字段
    corpusName: '',
    country: '',
    domain: '',
    language: '',
    dataFormat: '',
    classification: '',
    dataYear: '',
    minFileSize: '',
    maxFileSize: ''
});

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

const deleteFile = async (file) => {
    deleting.value = file.fileId;
    try {
        const response = await deleteFileById(file.fileId);
        if (response.success) {
            ElMessage.success('文件删除成功');
            fetchFiles(); // 重新加载列表
        } else {
            ElMessage.error(response.message || '文件删除失败');
        }
    } catch (error) {
        console.error('删除文件失败:', error);
        ElMessage.error('删除文件失败');
    } finally {
        deleting.value = null;
    }
};

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const formatFileSize = (sizeInBytes) => {
    if (!sizeInBytes || sizeInBytes === 0) return '0.000000 GB';
    const sizeInGB = sizeInBytes / (1024 * 1024 * 1024);
    return sizeInGB.toFixed(6);
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
    margin-bottom: 16px;
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
</style>