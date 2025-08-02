<script setup>
import { RouterView } from 'vue-router'
import { onMounted, ref } from 'vue'
import DownloadProgress from './components/DownloadProgress.vue'
import { setProgressComponent } from './utils/downloadManager'

const downloadProgressRef = ref(null)

onMounted(() => {
  // 设置全局下载进度组件引用
  if (downloadProgressRef.value) {
    setProgressComponent(downloadProgressRef.value)
  }
})
</script>

<template>
  <RouterView />
  <!-- 全局下载进度组件 -->
  <DownloadProgress ref="downloadProgressRef" />
</template>

<style scoped lang="scss">
// App.vue 只负责最基础的全局样式
</style>
