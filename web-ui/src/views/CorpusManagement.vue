<template>
  <div class="corpus-management">
    <el-card class="page-header">
      <h2>语料管理</h2>
      <p>管理系统中的所有语料清单，支持转移语料所有权</p>
    </el-card>

    <el-card class="search-card">
      <div class="search-toolbar">
        <el-input
          v-model="filters.collectionName"
          placeholder="按语料名称搜索"
          style="width: 200px; margin-right: 16px;"
          @keyup.enter="fetchCorpora"
          clearable
          @clear="fetchCorpora"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-input
          v-model="filters.creatorAccount"
          placeholder="按用户账号搜索"
          style="width: 200px; margin-right: 16px;"
          @keyup.enter="fetchCorpora"
          clearable
          @clear="fetchCorpora"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="fetchCorpora" :icon="Search">
          搜索
        </el-button>
        <el-button @click="resetFilters" :icon="Refresh">
          重置
        </el-button>
      </div>
    </el-card>

    <el-card class="action-card">
      <div class="action-toolbar">
        <el-button 
          type="warning" 
          @click="showTransferDialog = true" 
          :disabled="selectedCorpora.length === 0"
          :icon="Switch"
        >
          转移选中的语料 ({{ selectedCorpora.length }})
        </el-button>
        <div class="selection-info">
          <el-tag v-if="selectedCorpora.length > 0" type="info">
            已选择 {{ selectedCorpora.length }} 个语料
          </el-tag>
        </div>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table 
        :data="corpora" 
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="corpusId" label="语料ID" width="100" align="center" />
        <el-table-column prop="collectionName" label="语料名称" min-width="200">
          <template #default="scope">
            <el-tag type="primary">{{ scope.row.collectionName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所有者" min-width="150" align="center">
          <template #default="scope">
            <div class="owner-info">
              <div>
                <el-tag type="success" size="small">{{ scope.row.creatorAccount || '未知用户' }}</el-tag>
              </div>
              <div class="owner-id">ID: {{ scope.row.creatorId }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="language" label="语种" width="100" align="center">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.language || '未设置' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataFormat" label="数据格式" width="120" align="center">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.dataFormat || '未设置' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataVolume" label="数据量" width="120" align="center">
          <template #default="scope">
            {{ scope.row.dataVolume ? `${scope.row.dataVolume} ${scope.row.volumeUnit || ''}` : '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 转移对话框 -->
    <el-dialog
      v-model="showTransferDialog"
      title="转移语料所有权"
      width="600px"
      :before-close="handleCloseTransferDialog"
    >
      <div class="transfer-content">
        <el-alert
          :title="`您即将转移 ${selectedCorpora.length} 个语料的所有权`"
          type="warning"
          show-icon
          :closable="false"
          style="margin-bottom: 20px;"
        />

        <div class="user-select-section">
          <label class="form-label">选择目标用户：</label>
          <el-input
            v-model="userSearchQuery"
            placeholder="输入用户账号进行搜索..."
            @input="searchUsers"
            clearable
            style="margin-bottom: 10px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <div v-if="searchedUsers.length > 0" class="user-list">
            <div 
              v-for="user in searchedUsers" 
              :key="user.userId"
              class="user-item"
              :class="{ selected: targetUser && targetUser.userId === user.userId }"
              @click="selectUser(user)"
            >
              <div class="user-info">
                <span class="user-account">{{ user.account }}</span>
                <span class="user-id">(ID: {{ user.userId }})</span>
              </div>
              <div class="user-meta">
                <el-tag :type="user.userType === 'admin' ? 'danger' : 'success'" size="small">
                  {{ user.userType === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </div>
            </div>
          </div>

          <div v-if="targetUser" class="selected-user">
            <el-alert
              :title="`已选择用户: ${targetUser.account} (ID: ${targetUser.userId})`"
              type="success"
              show-icon
              :closable="false"
            />
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseTransferDialog">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleTransfer" 
            :disabled="!targetUser"
            :loading="transferLoading"
          >
            确认转移
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { getCorpora, getUsers, transferCorpus } from '@/services/admin';
import { Search, Refresh, User, Switch } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import debounce from 'lodash-es/debounce';

export default {
  name: 'CorpusManagement',
  components: {
    Search,
    Refresh,
    User,
    Switch
  },
  setup() {
    const route = useRoute();
    const corpora = ref([]);
    const selectedCorpora = ref([]);
    const loading = ref(false);
    const transferLoading = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const showTransferDialog = ref(false);

    // 搜索相关
    const filters = reactive({
      collectionName: '',
      creatorAccount: '',
      creatorId: null,
    });

    // 转移相关
    const userSearchQuery = ref('');
    const searchedUsers = ref([]);
    const targetUser = ref(null);

    const fetchCorpora = async () => {
      loading.value = true;
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          collectionName: filters.collectionName || undefined,
          creatorAccount: filters.creatorAccount || undefined,
          creatorId: filters.creatorId || undefined,
        };
        const response = await getCorpora(params);
        corpora.value = response.data.data.list;
        total.value = response.data.data.total;
      } catch (error) {
        console.error('Failed to fetch corpora:', error);
        // 错误已在admin.js中处理
      } finally {
        loading.value = false;
      }
    };

    const handlePageChange = (page) => {
      currentPage.value = page;
      fetchCorpora();
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchCorpora();
    };

    const handleSelectionChange = (selection) => {
      selectedCorpora.value = selection.map(item => item.corpusId);
    };

    const resetFilters = () => {
      filters.collectionName = '';
      filters.creatorAccount = '';
      filters.creatorId = null;
      currentPage.value = 1;
      fetchCorpora();
    };

    const searchUsers = debounce(async () => {
      if (userSearchQuery.value.length < 2) {
        searchedUsers.value = [];
        return;
      }
      try {
        const response = await getUsers({ account: userSearchQuery.value, size: 10 });
        searchedUsers.value = response.data.data.list;
      } catch (error) {
        console.error('Failed to search users:', error);
        // 错误已在admin.js中处理
      }
    }, 300);

    const selectUser = (user) => {
      targetUser.value = user;
      userSearchQuery.value = user.account;
      searchedUsers.value = [];
    };

    const handleCloseTransferDialog = () => {
      showTransferDialog.value = false;
      userSearchQuery.value = '';
      searchedUsers.value = [];
      targetUser.value = null;
    };

    const handleTransfer = async () => {
      if (!targetUser.value || selectedCorpora.value.length === 0) {
        ElMessage.warning('请选择目标用户和至少一个语料');
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要将 ${selectedCorpora.value.length} 个语料转移给用户 ${targetUser.value.account} 吗？`,
          '确认转移',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );

        transferLoading.value = true;
        const response = await transferCorpus(selectedCorpora.value, targetUser.value.userId);
        
        ElMessage.success(response.data.message || '语料转移成功！');
        handleCloseTransferDialog();
        fetchCorpora();
        selectedCorpora.value = [];
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to transfer corpus:', error);
          // 错误已在admin.js中处理
        }
      } finally {
        transferLoading.value = false;
      }
    };

    // 监听路由查询参数
    watch(() => route.query.creatorId, (newCreatorId) => {
      if (newCreatorId) {
        filters.creatorId = parseInt(newCreatorId, 10);
        if (route.query.creatorAccount) {
          filters.creatorAccount = route.query.creatorAccount;
        }
        fetchCorpora();
      }
    }, { immediate: true });

    onMounted(() => {
      if (!route.query.creatorId) {
        fetchCorpora();
      }
    });

    return {
      corpora,
      selectedCorpora,
      loading,
      transferLoading,
      currentPage,
      pageSize,
      total,
      filters,
      showTransferDialog,
      userSearchQuery,
      searchedUsers,
      targetUser,
      fetchCorpora,
      handlePageChange,
      handleSizeChange,
      handleSelectionChange,
      resetFilters,
      searchUsers,
      selectUser,
      handleCloseTransferDialog,
      handleTransfer,
    };
  },
};
</script>

<style scoped>
.corpus-management {
  max-width: 1400px;
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

.search-card, .action-card, .table-card {
  margin-bottom: 20px;
}

.search-toolbar, .action-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.action-toolbar {
  justify-content: space-between;
}

.selection-info {
  margin-left: auto;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.transfer-content {
  padding: 0 10px;
}

.user-select-section {
  margin-top: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #606266;
}

.user-list {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user-item:last-child {
  border-bottom: none;
}

.user-item:hover {
  background-color: #f5f7fa;
}

.user-item.selected {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-account {
  font-weight: 500;
  color: #303133;
}

.user-id {
  color: #909399;
  font-size: 12px;
}

.selected-user {
  margin-top: 15px;
}

.dialog-footer {
  display: flex;
  gap: 10px;
}

.owner-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.owner-id {
  font-size: 12px;
  color: #909399;
}
</style> 