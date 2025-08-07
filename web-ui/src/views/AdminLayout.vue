<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="admin-sidebar">
      <div class="admin-header">
        <h3>管理员面板</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        class="admin-menu"
        router
        unique-opened
      >
        <el-menu-item index="/user-management">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/corpus-management">
          <el-icon><Document /></el-icon>
          <span>语料管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
import { User, Document } from '@element-plus/icons-vue'

export default {
  name: 'AdminLayout',
  components: {
    User,
    Document
  }
};
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-sidebar {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
}

.admin-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #434a50;
}

.admin-header h3 {
  color: #ffffff;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.admin-menu {
  border-right: none;
  background-color: transparent;
}

.admin-menu .el-menu-item {
  color: #bfcbd9;
  border-bottom: 1px solid #434a50;
}

.admin-menu .el-menu-item:hover {
  background-color: #434a50;
  color: #ffffff;
}

.admin-menu .el-menu-item.is-active {
  background-color: #409eff;
  color: #ffffff;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 24px;
}
</style> 