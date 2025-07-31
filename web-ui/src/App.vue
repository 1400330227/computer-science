<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ref, provide, computed, onMounted } from 'vue'
import BreadcrumbNav from './components/BreadcrumbNav.vue'
import { useBreadcrumbStore } from './stores/breadcrumb'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 判断是否是登录页面
const isLoginPage = computed(() => {
  return route.name === 'login'
})

const breadcrumbStore = useBreadcrumbStore()
provide('breadcrumb', {
  items: breadcrumbStore.breadcrumbItems,
  setBreadcrumb: breadcrumbStore.setBreadcrumb,
  clearBreadcrumb: breadcrumbStore.clearBreadcrumb,
  addBreadcrumbItem: breadcrumbStore.addBreadcrumbItem
})

const activeIndex = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath)
}

// 用户信息
const username = ref('')

// 页面加载时获取用户信息
onMounted(() => {
  username.value = localStorage.getItem('username') || '未知用户'
})

// 登出功能
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '确认登出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 清除所有登录相关的localStorage数据
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('userType')
    localStorage.removeItem('phone')
    
    ElMessage.success('已成功退出登录')
    
    // 跳转到登录页面
    router.push({ name: 'login' })
    
  } catch {
    // 用户点击了取消，不做任何操作
  }
}

import { ArrowRight } from '@element-plus/icons-vue'
</script>

<template>
  <div class="app-container">
    <!-- 只在非登录页面显示导航栏 -->
    <template v-if="!isLoginPage">
      <el-menu :default-active="activeIndex" class="el-menu-demo top-nav" mode="horizontal" @select="handleSelect" router>
        <el-menu-item index="/" class="nav-item">首页</el-menu-item>
        <el-sub-menu index="2" class="nav-item">
          <template #title>语料清单</template>
          <el-menu-item index="file-list">语料集列表</el-menu-item>
          <el-menu-item index="file-details">语料集详情</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="3" class="nav-item">用户</el-menu-item>
        <el-menu-item index="4" class="nav-item">权限管理</el-menu-item>
        
        <!-- 右侧用户信息和登出按钮 -->
        <div class="nav-right">
          <span class="user-info">欢迎，{{ username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">登出</el-button>
        </div>
      </el-menu>
      <!-- 使用全局面包屑组件 -->
      <BreadcrumbNav />
    </template>
    
    <main class="main-container" :class="{ 'login-container': isLoginPage }">
      <RouterView />
    </main>
  </div>
</template>

<style scoped lang="scss">
.app-container {
  min-height: 100vh;
}

.main-container {
  padding: 20px;
  margin-top: 60px; /* 为顶部导航栏留出空间 */
}

/* 登录页面样式 */
.login-container {
  padding: 0;
  margin-top: 0; /* 登录页面不需要上边距 */
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 顶部导航栏样式 */
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 右侧用户信息和登出按钮 */
.nav-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 15px;
  margin-right: 20px;
}

.user-info {
  color: #606266;
  font-size: 14px;
}
</style>
