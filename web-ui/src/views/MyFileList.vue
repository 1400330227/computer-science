<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Download } from '@element-plus/icons-vue';
import { getUserFiles, deleteUserFile, batchDownloadFiles } from '@/services/files';
import { useBreadcrumbStore } from '@/stores/breadcrumb';
import corpusData from '@/assets/corpus.json';

// 面包屑配置
const breadcrumbStore = useBreadcrumbStore();
breadcrumbStore.setBreadcrumb([
    { text: '首页', to: '/' },
    { text: '我的文件', to: '/my-files' }
]);

// Reactive data
const files = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 批量选择相关状态
const selectedFiles = ref([]);
const batchDownloading = ref(false);

const searchForm = reactive({
    fileName: '',
    dataFormat: '',
    // 新增的语料相关筛选字段
    corpusName: '',
    country: '',
    domain: '',
    language: '',
    classification: '',
    dataYear: ''
});

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

// 计算是否有语料信息显示
const hasCorpusInfo = computed(() => {
    return files.value.some(file => file.corpusName || file.corpusCountry || file.corpusLanguage);
});

// 计算是否有选中的文件
const hasSelectedFiles = computed(() => {
    return selectedFiles.value.length > 0;
});

// Methods
const fetchUserFiles = async () => {
    loading.value = true;
    try {
        const response = await getUserFiles(currentPage.value, pageSize.value, searchForm);

        // 检查响应格式，适配分页数据结构
        if (response.data && Array.isArray(response.data.records)) {
            // 简单分页格式
            files.value = response.data.records;
            total.value = response.data.total || 0;
        } else if (response.data && response.data.list) {
            // DPage格式（有筛选条件时）
            files.value = response.data.list;
            total.value = response.data.total || 0;
        } else if (Array.isArray(response.data)) {
            // 兼容旧格式
            files.value = response.data;
            total.value = response.data.length;
        } else {
            files.value = [];
            total.value = 0;
            ElMessage.warning('返回数据格式不正确');
        }
    } catch (error) {
        console.error('获取文件列表失败:', error);
        ElMessage.error('获取文件列表失败，请稍后重试');
        files.value = [];
        total.value = 0;
    } finally {
        loading.value = false;
    }
};

const handlePageChange = (page) => {
    currentPage.value = page;
    selectedFiles.value = []; // 清空选择
    fetchUserFiles();
};

const handleSizeChange = (size) => {
    pageSize.value = size;
    currentPage.value = 1;
    selectedFiles.value = []; // 清空选择
    fetchUserFiles();
};

const handleSearch = () => {
    currentPage.value = 1;
    selectedFiles.value = []; // 清空选择
    fetchUserFiles();
};

const handleReset = () => {
    Object.keys(searchForm).forEach(key => {
        searchForm[key] = '';
    });
    currentPage.value = 1;
    selectedFiles.value = []; // 清空选择
    fetchUserFiles();
};

// 处理表格选择变化
const handleSelectionChange = (selection) => {
    selectedFiles.value = selection;
};

// 批量下载文件
const handleBatchDownload = async () => {
    if (selectedFiles.value.length === 0) {
        ElMessage.warning('请先选择要下载的文件');
        return;
    }

    try {
        await ElMessageBox.confirm(
            `确定要下载选中的 ${selectedFiles.value.length} 个文件吗？文件将打包为ZIP格式下载。`,
            '批量下载确认',
            {
                confirmButtonText: '确定下载',
                cancelButtonText: '取消',
                type: 'info'
            }
        );

        batchDownloading.value = true;
        const fileIds = selectedFiles.value.map(file => file.fileId);

        // 调用批量下载API
        await batchDownloadFiles(fileIds);

        ElMessage.success('批量下载开始，请稍候...');

    } catch (error) {
        if (error !== 'cancel') {
            console.error('批量下载失败:', error);
            ElMessage.error('批量下载失败，请稍后重试');
        }
    } finally {
        batchDownloading.value = false;
    }
};

// 删除文件
const handleDeleteFile = async (fileId) => {
    try {
        await ElMessageBox.confirm('确定要删除此文件吗？此操作不可恢复！', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        // 调用删除文件API
        await deleteUserFile(fileId);

        ElMessage.success('文件删除成功');
        fetchUserFiles(); // 重新获取文件列表
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除文件失败:', error);
            ElMessage.error('删除文件失败，请稍后重试');
        }
    }
};

function getFileDownloadUrl(fileEntity) {
    return `/api/files/${fileEntity.fileId}/download`;
}

const formatFileSize = (sizeInBytes) => {
    if (!sizeInBytes || sizeInBytes === 0) return '0.000000';
    const sizeInGB = sizeInBytes / (1024 * 1024 * 1024);
    return sizeInGB.toFixed(6);
};

// 组件挂载时获取文件列表
onMounted(() => {
    fetchUserFiles();
});
</script>

<template>
    <div class="my-files">
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
                <el-form-item label="数据格式">
                    <el-select v-model="searchForm.dataFormat" placeholder="选择数据格式" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option label="文本" value="文本" />
                        <el-option label="图像" value="图像" />
                        <el-option label="音频" value="音频" />
                        <el-option label="视频" value="视频" />
                        <el-option label="其他" value="其他" />
                    </el-select>
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
                <el-form-item label="数据分类">
                    <el-select v-model="searchForm.classification" placeholder="选择数据分类" style="width: 230px;" clearable
                        @change="handleSearch">
                        <el-option v-for="classification in corpusData.classifications"
                            :key="classification.classificationName" :label="classification.classificationName"
                            :value="classification.classificationName">
                        </el-option>
                    </el-select>
                </el-form-item>

                <!-- 第四行：年份筛选 -->
                <el-form-item label="数据年份">
                    <el-date-picker v-model="searchForm.dataYear" type="year" placeholder="选择数据年份" style="width: 230px;"
                        @change="handleSearch" clearable format="YYYY" value-format="YYYY">
                    </el-date-picker>
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
                <span class="selected-info">已选择 {{ selectedFiles.length }} 个文件</span>
                <el-button :icon="Download" @click="handleBatchDownload" :loading="batchDownloading"
                    :disabled="batchDownloading">
                    {{ batchDownloading ? '正在下载...' : '批量下载' }}
                </el-button>
            </div>

            <el-table :data="files" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column prop="fileName" label="文件" min-width="300">
                    <template #default="scope">
                        <span>{{ scope.row.fileName }}</span>
                        <span style="color:#999999">&nbsp;&nbsp;{{ scope.row.dataFormat }} | {{ formatFileSize(scope.row.size)
                        }}GB</span>
                    </template>
                </el-table-column>
                <el-table-column prop="corpusName" label="语料名称"></el-table-column>
                <el-table-column prop="corpusDomain" label="所属领域"></el-table-column>
                <el-table-column prop="corpusCountry" label="国家"></el-table-column>
                <el-table-column prop="corpusClassification" label="数据分类"></el-table-column>
                <el-table-column prop="corpusDataYear" label="数据年份" width="100" v-if="hasCorpusInfo"></el-table-column>
                <el-table-column prop="corpusLanguage" label="语言" width="100" v-if="hasCorpusInfo" />
                <el-table-column label="操作" width="90">
                    <template #default="scope">
                        <a :href="getFileDownloadUrl(scope.row)" class="download-link" title="下载文件" download>
                            下载
                        </a>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-wrapper">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :small="false" :disabled="false" :background="true"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handlePageChange" />
            </div>
        </div>
    </div>
</template>

<style scoped>
.my-files {
    max-width: 1200px;
    margin: 0 auto;
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
    justify-content: space-between;
    align-items: center;
    /* margin-bottom: 16px; */
    padding: 12px 16px;
    /* background-color: #f5f7fa; */
    border-bottom: 1px solid #e4e7ed;
    /* border-radius: 4px; */
}

.selected-info {
    color: #606266;
    font-size: 14px;
}

.pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.download-link {
    display: inline-block;
    padding: 0;
    margin-right: 15px;
    color: #4169e1;
    text-decoration: none;
    font-size: 16px;
    cursor: pointer;
    transition: color 0.3s ease;
    border: none;
    background: none;
}

.download-link:hover {
    color: #66b1ff;
}
</style>
