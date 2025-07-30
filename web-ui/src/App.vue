<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">Processing Center</el-menu-item>
      <el-sub-menu index="2">
        <template #title>Workspace</template>
        <el-menu-item index="2-1">item one</el-menu-item>
        <el-menu-item index="2-2">item two</el-menu-item>
        <el-menu-item index="2-3">item three</el-menu-item>
        <el-sub-menu index="2-4">
          <template #title>item four</template>
          <el-menu-item index="2-4-1">item one</el-menu-item>
          <el-menu-item index="2-4-2">item two</el-menu-item>
          <el-menu-item index="2-4-3">item three</el-menu-item>
        </el-sub-menu>
      </el-sub-menu>
      <el-menu-item index="3" disabled>Info</el-menu-item>
      <el-menu-item index="4">Orders</el-menu-item>
    </el-menu>
    <!-- <nav>
      <div class="top-nav">
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/file-list" class="nav-link active">语料清单</a>
          <a href="/settings" class="nav-link">用户</a>
          <a href="/settings" class="nav-link">权限管理</a>
        </div>
      </div>
    </nav> -->
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <span>文件管理</span> > <span class="active">语料集列表</span>
    </div>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-container {
  background-color: #F8FAFD;
}

/* 顶部导航样式 */
.top-nav {
  background-color: #4169e1;
  padding: 0;
  height: 60px;
  display: flex;
  align-items: center;
  color: white;
  justify-content: center;
}

.nav-links {
  display: flex;
  width: 1200px;
  padding: 0 20px;
}

.nav-link {
  padding: 0 20px;
  color: white;
  text-decoration: none;
  height: 60px;
  line-height: 60px;
  transition: background-color 0.2s;
  font-size: 16px;
}

.nav-link:hover,
.nav-link.active {
  background-color: rgba(255, 255, 255, 0.2);
}

/* 面包屑导航样式 */
.breadcrumb {
  padding: 10px 0;
  color: #666;
  font-size: 12px;
  display: flex;
  justify-content: left;
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb>div {
  width: 1200px;
  padding: 0 20px;
}

.breadcrumb .active {
  color: #333;
  font-weight: 500;
}
</style>
