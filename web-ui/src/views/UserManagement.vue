<template>
  <div class="user-management">
    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="账号">
          <el-input v-model="searchForm.account" placeholder="按账号搜索用户" style="width: 300px; margin-right: 16px;"
            @keyup.enter="handleSearch" clearable @clear="handleSearch">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleReset" :icon="Refresh">
            重置
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="openCreateDialog">
            新增用户
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="users" v-loading="loading" style="width: 100%">
        <!-- <el-table-column prop="userId" label="用户ID" width="100" /> -->
        <el-table-column prop="account" label="账号" min-width="120" />
        <el-table-column prop="nickname" label="姓名" width="100" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="title" label="职称"  />
        <el-table-column prop="major" label="专业"  />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.userType" size="small" @change="handleUserTypeChange(scope.row)"
              :disabled="updating === scope.row.userId">
              <el-option label="普通用户" value="user" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'warning'" size="small">
              {{ scope.row.gender || '未设置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accountStatus" label="账号状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.accountStatus === 'active' ? 'success' : 'danger'" size="small">
              {{ scope.row.accountStatus === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="100">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="primary" link @click="openEditDialog(scope.row)">
              编辑
            </el-button>
            <el-button type="primary" link @click="handleResetPassword(scope.row)">
              密码重置
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :current-page="currentPage"
          @current-change="handlePageChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <!-- 新增用户对话框 -->
    <el-dialog v-model="createDialogVisible" title="新增用户" width="520px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="90px">
        <el-form-item label="账号" required>
          <el-input v-model="createForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="createForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="createForm.phone" maxlength="11" placeholder="请输入11位手机号"
                    @input="createForm.phone = createForm.phone.replace(/\D/g, '')" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="createForm.college" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="createForm.title" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="createForm.major" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="createForm.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="未知" value="未知" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="createForm.accountStatus">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="createForm.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="createForm.remarks" />
        </el-form-item>
        <el-alert type="info" :closable="false" show-icon title="默认密码：Gxu123456" />
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="creating" @click="saveCreateUser">保 存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="520px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="90px">
        <el-form-item label="账号">
          <el-input v-model="editForm.account" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" maxlength="11" placeholder="请输入11位手机号"
                    @input="editForm.phone = editForm.phone.replace(/\D/g, '')" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="editForm.college" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="editForm.major" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="未知" value="未知" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="editForm.accountStatus">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editForm.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="editForm.remarks" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="saving" @click="saveUser">保 存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { getUsers, updateUserRole, getUserDetail, updateUser, resetUserPassword, createUser } from '@/services/admin';
import { Search, Refresh, Document } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

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
    const updating = ref(null); // 正在更新权限的用户ID
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const searchForm = reactive({
      account: '',
    });

    const createDialogVisible = ref(false);
    const creating = ref(false);
    const createFormRef = ref();
    const createForm = reactive({
      account: '',
      nickname: '',
      phone: '',
      college: '',
      title: '',
      major: '',
      gender: '未知',
      accountStatus: 'active',
      address: '',
      remarks: '',
    });
    const createRules = reactive({
      nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur', 'change'] },
      ],
    });

    const editDialogVisible = ref(false);
    const saving = ref(false);
    const editFormRef = ref();
    const editingUserId = ref(null);
    const editForm = reactive({
      account: '',
      nickname: '',
      phone: '',
      college: '',
      title: '',
      major: '',
      gender: '',
      accountStatus: '',
      address: '',
      remarks: '',
    });
    const editRules = reactive({
      nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur', 'change'] },
      ],
    });

    const fetchUsers = async () => {
      loading.value = true;
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          account: searchForm.account || undefined,
        };
        const response = await getUsers(params);
        users.value = response.data.data.list;
        total.value = response.data.data.total;
      } catch (error) {
        console.error("Failed to fetch users:", error);
        // 错误已在admin.js中处理
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
      searchForm.account = '';
      currentPage.value = 1;
      fetchUsers();
    };

    const openCreateDialog = () => {
      createForm.account = '';
      createForm.nickname = '';
      createForm.phone = '';
      createForm.college = '';
      createForm.title = '';
      createForm.major = '';
      createForm.gender = '未知';
      createForm.accountStatus = 'active';
      createForm.address = '';
      createForm.remarks = '';
      createDialogVisible.value = true;
    };

    const saveCreateUser = async () => {
      try {
        if (!createFormRef.value) return;
        await createFormRef.value.validate();
        creating.value = true;
        await createUser({
          account: createForm.account.trim(),
          nickname: createForm.nickname,
          phone: createForm.phone,
          college: createForm.college,
          title: createForm.title,
          major: createForm.major,
          gender: createForm.gender,
          accountStatus: createForm.accountStatus,
          address: createForm.address,
          remarks: createForm.remarks,
        });
        ElMessage.success('用户创建成功，默认密码为 Gxu123456');
        createDialogVisible.value = false;
        await fetchUsers();
      } catch (e) {
        console.error('创建用户失败', e);
      } finally {
        creating.value = false;
      }
    };

    const openEditDialog = async (row) => {
      try {
        const res = await getUserDetail(row.userId);
        const data = res.data.data;
        editingUserId.value = data.userId;
        editForm.account = data.account;
        editForm.nickname = data.nickname;
        editForm.phone = data.phone;
        editForm.college = data.college || '';
        editForm.title = data.title || '';
        editForm.major = data.major || '';
        editForm.gender = data.gender || '';
        editForm.accountStatus = data.accountStatus || 'active';
        editForm.address = data.address || '';
        editForm.remarks = data.remarks || '';
        editDialogVisible.value = true;
      } catch (e) {
        console.error('获取用户详情失败', e);
      }
    };

    const handleResetPassword = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要重置用户 ${row.account} 的密码为默认值吗？\n默认密码：Gxu123456`,
          '确认密码重置',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        );
        await resetUserPassword(row.userId);
        ElMessage.success('密码已重置为默认值：Gxu123456');
      } catch (e) {
        if (e !== 'cancel') {
          console.error('密码重置失败', e);
        }
      }
    };

    const saveUser = async () => {
      try {
        if (!editFormRef.value) return;
        await editFormRef.value.validate();
        saving.value = true;
        const payload = {
          account: editForm.account,
          nickname: editForm.nickname,
          phone: editForm.phone,
          college: editForm.college,
          title: editForm.title,
          major: editForm.major,
          gender: editForm.gender,
          accountStatus: editForm.accountStatus,
          address: editForm.address,
          remarks: editForm.remarks,
        };
        await updateUser(editingUserId.value, payload);
        ElMessage.success('保存成功');
        editDialogVisible.value = false;
        await fetchUsers();
      } catch (e) {
        console.error('保存用户失败', e);
      } finally {
        saving.value = false;
      }
    };

    const viewUserCorpus = (user) => {
      router.push({
        path: '/corpus-management',
        query: { creatorId: user.userId, creatorAccount: user.account }
      });
    };

    const handleUserTypeChange = async (user) => {
      const oldUserType = user.userType === 'admin' ? 'user' : 'admin'; // 获取变更前的值
      const newUserType = user.userType;

      try {
        await ElMessageBox.confirm(
          `确定要将用户 ${user.account} 的权限修改为 ${newUserType === 'admin' ? '管理员' : '普通用户'} 吗？`,
          '确认修改权限',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );

        updating.value = user.userId;
        const response = await updateUserRole(user.userId, newUserType);

        ElMessage.success(response.data.message || '用户权限修改成功');

        // 刷新用户列表
        await fetchUsers();
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to update user role:', error);
          // 恢复原来的值
          user.userType = oldUserType;
        } else {
          // 用户取消，恢复原来的值
          user.userType = oldUserType;
        }
      } finally {
        updating.value = null;
      }
    };

    // 日期格式化：YYYY-MM-DD
    const formatDate = (value) => {
      if (!value) return '-';
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) return '-';
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    onMounted(fetchUsers);

    return {
      users,
      loading,
      updating,
      total,
      currentPage,
      pageSize,
      searchForm,
      handlePageChange,
      handleSizeChange,
      handleSearch,
      handleReset,
      viewUserCorpus,
      handleUserTypeChange,
      formatDate,
      editDialogVisible,
      editForm,
      openEditDialog,
      saveUser,
      saving,
      handleResetPassword,
      createDialogVisible,
      createForm,
      createRules,
      createFormRef,
      openCreateDialog,
      saveCreateUser,
      creating,
      editRules,
      editFormRef,
    };
  },
};
</script>

<style scoped>
.user-management {
  max-width: 1200px;
  margin: 0 auto;
  background-color: #fff;
  padding: 20px;
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

.search-form {
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
