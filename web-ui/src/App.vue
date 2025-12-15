<script setup>
import { RouterView } from 'vue-router'
import MaintenanceView from './views/MaintenanceView.vue'

// 控制维护模式的开关
// true: 开启维护模式，网站显示维护页面
// false: 关闭维护模式，网站正常运行
const MAINTENANCE_MODE = false;
</script>

<template>
  <MaintenanceView v-if="MAINTENANCE_MODE" />
  <RouterView v-else />
</template>

<style scoped lang="scss">
// App.vue 只负责最基础的全局样式
</style>
