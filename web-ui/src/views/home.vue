<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ref, provide, computed, onMounted } from 'vue'
import BreadcrumbNav from '../components/BreadcrumbNav.vue'
import { useBreadcrumbStore } from '../stores/breadcrumb'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

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

// 页面加载时恢复用户信息
onMounted(() => {
  userStore.restoreFromStorage()
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

    // 使用 store 清除用户信息
    userStore.logout()

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
  <div class="app-wrapper">
    <!-- 固定导航栏 -->
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo top-nav"
      mode="horizontal"
      @select="handleSelect"
      router
      style="position: fixed !important; top: 0 !important; left: 0 !important; right: 0 !important; width: 100% !important; z-index: 1000 !important;"
    >
      <el-menu-item index="/" class="nav-item">首页</el-menu-item>
      <el-sub-menu index="2" class="nav-item">
        <template #title>语料清单</template>
        <el-menu-item index="/file-list">语料集列表</el-menu-item>
        <el-menu-item index="/file-upload">上传语料集</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="3" class="nav-item">用户</el-menu-item>
      <el-menu-item index="4" class="nav-item">权限管理</el-menu-item>

      <!-- 右侧用户信息和登出按钮 -->
      <div class="nav-right">
        <span class="user-info">欢迎：{{ userStore.displayName }}</span>
        <el-button type="danger" size="small" @click="handleLogout">注销</el-button>
      </div>
    </el-menu>

    <!-- 主要内容区域 -->
    <div class="app-container">
      <!-- 使用全局面包屑组件 -->
      <div class="breadcrumb-container">
        <BreadcrumbNav />
      </div>

      <main class="main-container">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style lang="scss">
/* 应用包装器 */
.app-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

/* 强制导航栏固定定位 */
.el-menu-demo.top-nav {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  width: 100% !important;
  z-index: 1000 !important;
  margin: 0 !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
}

/* 右侧用户信息和注销按钮 */
.nav-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-right: 20px;
}

.user-info {
  color: white;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

/* 主要内容容器 */
.app-container {
  padding-top: 64px; /* 为固定导航栏留出空间 */
  min-height: calc(100vh - 64px);
}

/* 面包屑容器样式 */
.breadcrumb-container {
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
}

.main-container {
  padding: 0;
  background: transparent;
}

/* 导航栏菜单项样式 */
.el-menu-demo.top-nav .el-menu-item,
.el-menu-demo.top-nav .el-sub-menu .el-sub-menu__title {
  color: white !important;
  border-bottom: none !important;
}

.el-menu-demo.top-nav .el-menu-item:hover,
.el-menu-demo.top-nav .el-sub-menu .el-sub-menu__title:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: white !important;
}

.el-menu-demo.top-nav .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2) !important;
  color: white !important;
  border-bottom: 2px solid white !important;
}

/* 注销按钮样式 */
.nav-right .el-button--danger {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.nav-right .el-button--danger:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}
</style>
