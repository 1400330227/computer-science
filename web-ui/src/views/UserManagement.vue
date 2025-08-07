<template>
  <div class="user-management">
    <el-card class="page-header">
      <h2>用户管理</h2>
      <p>管理系统中的所有用户信息</p>
    </el-card>

    <el-card class="search-card">
      <div class="search-toolbar">
        <el-input
          v-model="searchAccount"
          placeholder="按账号搜索用户"
          style="width: 300px; margin-right: 16px;"
          @keyup.enter="handleSearch"
          clearable
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch" :icon="Search">
          搜索
        </el-button>
        <el-button @click="handleReset" :icon="Refresh">
          重置
        </el-button>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table 
        :data="users" 
        v-loading="loading" 
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="userId" label="用户ID" width="100" align="center" />
        <el-table-column prop="account" label="账号" min-width="120">
          <template #default="scope">
            <el-tag type="primary" size="small">{{ scope.row.account }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="userType" label="用户类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.userType === 'admin' ? 'danger' : 'success'" size="small">
              {{ scope.row.userType === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'warning'" size="small">
              {{ scope.row.gender || '未设置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accountStatus" label="账号状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.accountStatus === 'active' ? 'success' : 'danger'" size="small">
              {{ scope.row.accountStatus === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewUserCorpus(scope.row)"
              :icon="Document"
            >
              查看语料
            </el-button>
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
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getUsers } from '@/services/admin';
import { Search, Refresh, Document } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'UserManagement',
  components: {
    Search,
    Refresh,
    Document
  },
  setup() {
    const router = useRouter();
    const users = ref([]);
    const loading = ref(false);
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const searchAccount = ref('');

    const fetchUsers = async () => {
      loading.value = true;
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          account: searchAccount.value || undefined,
        };
        const response = await getUsers(params);
        users.value = response.data.data.list;
        total.value = response.data.data.total;
      } catch (error) {
        console.error("Failed to fetch users:", error);
        // 错误已在admin.js中处理，这里不需要再显示错误消息
      } finally {
        loading.value = false;
      }
    };

    const handlePageChange = (page) => {
      currentPage.value = page;
      fetchUsers();
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchUsers();
    };

    const handleSearch = () => {
      currentPage.value = 1;
      fetchUsers();
    };

    const handleReset = () => {
      searchAccount.value = '';
      currentPage.value = 1;
      fetchUsers();
    };

    const viewUserCorpus = (user) => {
      router.push({
        path: '/corpus-management',
        query: { creatorId: user.userId, creatorAccount: user.account }
      });
    };

    onMounted(fetchUsers);

    return {
      users,
      loading,
      total,
      currentPage,
      pageSize,
      searchAccount,
      handlePageChange,
      handleSizeChange,
      handleSearch,
      handleReset,
      viewUserCorpus,
    };
  },
};
</script>

<style scoped>
.user-management {
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
}

.search-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 