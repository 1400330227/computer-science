<script setup>
import { RouterView } from 'vue-router'
import { ref, provide } from 'vue'
import BreadcrumbNav from './components/BreadcrumbNav.vue'
import { useBreadcrumbStore } from './stores/breadcrumb'

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

import { ArrowRight } from '@element-plus/icons-vue'
</script>

<template>
  <div class="app-container">
    <el-menu :default-active="activeIndex" class="el-menu-demo top-nav" mode="horizontal" @select="handleSelect" router>
      <el-menu-item index="/" class="nav-item">首页</el-menu-item>
      <el-sub-menu index="2" class="nav-item">
        <template #title>语料清单</template>
        <el-menu-item index="file-list">语料集列表</el-menu-item>
        <el-menu-item index="file-details">语料集详情</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="3" class="nav-item">用户</el-menu-item>
      <el-menu-item index="4" class="nav-item">权限管理</el-menu-item>
    </el-menu>
    <!-- 使用全局面包屑组件 -->
    <BreadcrumbNav />
    <main class="main-container">
      <RouterView />
    </main>
  </div>
</template>

<style scoped lang="scss"></style>
