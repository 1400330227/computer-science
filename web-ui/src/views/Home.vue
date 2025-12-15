<script setup>
import { RouterView, useRouter } from 'vue-router'
import { ref, provide, onMounted, onUnmounted } from 'vue'
import BreadcrumbNav from '../components/BreadcrumbNav.vue'
import { useBreadcrumbStore } from '../stores/breadcrumb'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import heartbeatService from '@/services/heartbeat'
import { template } from 'lodash-es'


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

  // 启动心跳检测服务
  if (userStore.isAuthenticated) {
    heartbeatService.start()
    console.log('🔄 主页面已启动心跳检测')
  }
})

// 组件卸载时停止心跳检测
onUnmounted(() => {
  heartbeatService.stop()
  console.log('🛑 主页面已停止心跳检测')
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

    // 停止心跳检测
    heartbeatService.stop()

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
          <el-menu-item index="/file-list">我的语料集</el-menu-item>
          <el-menu-item index="/file-upload">上传语料集</el-menu-item>
          <el-menu-item index=""></el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/my-files" class="nav-item">我的文件</el-menu-item>
        
        <!-- Admin Menu -->
        <template v-if="userStore.user?.userType === 'admin'">
          <el-menu-item class="nav-item" index="/all-files">所有文件</el-menu-item>
          <el-menu-item class="nav-item" index="/corpus-management">语料库管理</el-menu-item>
          <el-menu-item class="nav-item" index="/user-management">用户管理</el-menu-item>
        </template>

        <el-menu-item class="nav-item" index="/data-visualization">数据可视化</el-menu-item>

      </el-menu>
      <!-- 添加平台标题 -->
      <div class="platform-title">
        <router-link to="/">广西大学东盟语料库管理与标注平台</router-link>
      </div>
      <!-- 右侧用户信息和登出按钮 -->
      <div class="nav-right">
        <span class="user-info">
          <el-link type="primary" @click="router.push({ name: 'my-info' })" style="color: #ffffff;">
            {{ userStore.displayName }}
          </el-link>
        </span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
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
.platform-title > a{
  font-size: 16px;
  font-weight: bold;
  color: #ffffff;
  padding: 0 15px;
  position: absolute;
  left: 0;
  top: 17px;
  text-decoration: none;
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
