<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserFiles, deleteUserFile } from '@/services/files';
import { useBreadcrumbStore } from '@/stores/breadcrumb';

// 面包屑配置
const breadcrumbStore = useBreadcrumbStore();
breadcrumbStore.setBreadcrumb([
    { text: '首页', to: '/' },
    { text: '我的文件', to: '/my-files' }
]);

const files = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const dateTimeFormat = new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
});

// 文件类型对应的图标
const fileTypeIcons = {
    'pdf': 'Document',
    'doc': 'Document',
    'docx': 'Document',
    'xls': 'Document',
    'xlsx': 'Document',
    'txt': 'Document',
    'zip': 'Folder',
    'rar': 'Folder',
    'default': 'Document'
};

// 获取文件图标
const getFileIcon = (fileType) => {
    const type = fileType ? fileType.toLowerCase() : '';
    return fileTypeIcons[type] || fileTypeIcons['default'];
};

// 获取用户文件列表
const fetchUserFiles = async () => {
    loading.value = true;
    try {
        const response = await getUserFiles(currentPage.value, pageSize.value);
        // 检查响应格式，适配分页数据结构
        if (response.data && Array.isArray(response.data.records)) {
            files.value = response.data.records;
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

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '未知时间';
    try {
        const date = new Date(dateTimeString);
        return dateTimeFormat.format(date);
    } catch (error) {
        return '日期格式错误';
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
    debugger
    return `/api/files/${fileEntity.fileId}/download`;
};

// 下载文件
function showDownloadMessage(row) {
    // 不使用 event.preventDefault()，让 <a> 标签的默认下载行为正常执行
    ElMessage.success(`正在下载文件: ${row.fileName}`);
}

// 处理分页变化
const handlePageChange = (page) => {
    currentPage.value = page;
    fetchUserFiles();
};

// 处理每页记录数变化
const handleSizeChange = (size) => {
    pageSize.value = size;
    fetchUserFiles();
};

// 组件挂载时获取文件列表
onMounted(() => {
    fetchUserFiles();
});
</script>

<template>
    <div class="my-files-container">
        <div class="box-card">
            <el-table v-loading="loading" :data="files" style="width: 100%">
                <el-table-column label="文件名" prop="fileName" min-width="200">
                    <template #default="scope">
                        <div class="file-name-cell">
                            <el-icon class="file-icon">
                                <component :is="getFileIcon(scope.row.fileType)" />
                            </el-icon>
                            <span>{{ scope.row.fileName }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="文件类型" prop="fileType" width="120" />
                <el-table-column label="文件大小GB" prop="size" />

                <el-table-column label="创建时间" width="180">
                    <template #default="scope">
                        {{ formatDateTime(scope.row.createdAt) }}
                    </template>
                </el-table-column>

                <!-- <el-table-column label="更新时间" width="180">
                    <template #default="scope">
                        {{ formatDateTime(scope.row.updatedAt) }}
                    </template>
                </el-table-column> -->

                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <a :href="getFileDownloadUrl(row)" class="download-link" @click="showDownloadMessage(row)"
                            download>
                            下载
                        </a>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handlePageChange" />
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.my-files-container {
    width: 1200px;
    margin: 0 auto;
    background-color: #fff;
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-actions {
    display: flex;
    gap: 10px;
}

.file-name-cell {
    display: flex;
    align-items: center;
    gap: 8px;
}

.file-icon {
    font-size: 18px;
    color: #409EFF;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
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
