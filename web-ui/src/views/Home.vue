<script setup>
import { RouterView, useRouter } from 'vue-router'
import { ref, provide, onMounted } from 'vue'
import BreadcrumbNav from '../components/BreadcrumbNav.vue'
import { useBreadcrumbStore } from '../stores/breadcrumb'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'


const router = useRouter()
const userStore = useUserStore()




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

</script>

<template>
  <div class="app-container">
    <header class="nav-container">
      <el-menu :default-active="activeIndex" class="el-menu-demo top-nav" mode="horizontal" @select="handleSelect"
        router>
        <el-menu-item index="/" class="nav-item">首页</el-menu-item>
        <el-sub-menu index="2" class="nav-item">
          <template #title>语料清单</template>
          <el-menu-item index="/file-list">语料集列表</el-menu-item>
          <el-menu-item index="/file-upload">上传语料集</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/my-files" class="nav-item">我的文件</el-menu-item>
        <el-menu-item index="3" class="nav-item">用户</el-menu-item>
        <el-menu-item index="4" class="nav-item">权限管理</el-menu-item>
      </el-menu>
      <!-- 添加平台标题 -->
      <div class="platform-title">广西大学东盟语料收集与管理平台</div>
      <!-- 右侧用户信息和登出按钮 -->
      <div class="nav-right">
        <span class="user-info">欢迎：{{ userStore.displayName }}</span>
        <el-button type="danger" size="small" @click="handleLogout">注销</el-button>
      </div>
    </header>
    <!-- 使用全局面包屑组件 -->
    <div class="breadcrumb-container">
      <BreadcrumbNav />
    </div>
    <main class="main-container">
      <RouterView />
    </main>
  </div>
</template>

<style scoped lang="scss">
.nav-container {
  position: sticky;
  z-index: 12;
  top: 0;
  left: 0;
  height: 60px;
  width: 100%;
}

/* 平台标题样式 */
.platform-title {
  font-size: 22px;
  font-weight: bold;
  color: #ffffff;
  padding: 0 15px;
  position: absolute;
  left: 0;
  top: 12px;
}

/* 右侧用户信息和注销按钮 */
.nav-right {
  margin-left: 10px;
  display: flex;
  align-items: center;
  gap: 25px;
  margin-right: 10px;
  position: absolute;
  right: 0;
  top: 17px;
}

.user-info {
  color: #f5f7fa;
  font-size: 14px;
}
</style>
