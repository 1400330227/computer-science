<script setup>
import { ref, onMounted } from 'vue';
import { getCorpusOverview } from '@/services/dashboard';
const corpusOverview = ref(null);
const initContributor = async () => {
  const response = await getCorpusOverview()
  corpusOverview.value = response.data[0]
  console.log(response)
}
onMounted(() => {
  initContributor();
});
</script>

<template>
  <div class="page-container">
    <h1 class="dashboard-title">语料库总览</h1>

    <!-- 1. 语料库总览 (无变化) -->
    <div v-if="corpusOverview" class="overview-section card">
      <div class="stat-item">
        <div class="value">{{ corpusOverview.totalCorpus }}</div>
        <div class="label">语料库总数</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ corpusOverview.totalFiles }}</div>
        <div class="label">文件总数</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ corpusOverview.totalCapacityGb }} GB</div>
        <div class="label">总估算容量</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ corpusOverview.countriesCount }}</div>
        <div class="label">覆盖国家数</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ corpusOverview.languagesCount }}</div>
        <div class="label">覆盖语言数</div>
      </div>
      <div class="stat-item">
        <div class="value">{{ corpusOverview.contributorsCount }}</div>
        <div class="label">贡献者总数</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overview-section {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}
.stat-item {
  text-align: center;
  padding: 10px 20px;
}
.stat-item .value {
  font-size: 2.5rem;
  font-weight: 600;
  color: #3f51b5;
}
.stat-item .label {
  font-size: 1rem;
  color: #666;
  margin-top: 8px;
}
.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  padding: 20px;
  margin-bottom: 20px;
}
.page-container {
  padding: 20px;
}
</style>
