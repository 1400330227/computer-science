<template>
  <div class="page-container">
    <div class="card">
      <h2>领域总览</h2>
      <div ref="domainChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getDomainOverview } from '@/services/dashboard';

const domainChartRef = ref(null);

const initChart = async () => {
  const response = await getDomainOverview();
  const data = response.data || [];
  const domains = data.map(i => i.domain);

  const chart = echarts.init(domainChartRef.value);
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['语料库数量', '文件总数', '容量(GB)', '覆盖国家数', '覆盖语言数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: domains, axisLabel: { interval: 0, rotate: 0 } },
    yAxis: { type: 'value' },
    series: [
      { name: '语料库数量', type: 'bar', data: data.map(i => i.corpusCount), barWidth: '40%' },
      { name: '文件总数', type: 'bar', data: data.map(i => i.filesCount), barWidth: '40%' },
      { name: '容量(GB)', type: 'line', data: data.map(i => i.totalCapacityGb), smooth: true },
      { name: '覆盖国家数', type: 'line', data: data.map(i => i.countriesCovered), smooth: true },
      { name: '覆盖语言数', type: 'line', data: data.map(i => i.languagesCovered), smooth: true }
    ]
  });

  window.addEventListener('resize', () => chart.resize());
};

onMounted(() => {
  if (domainChartRef.value) {
    initChart();
  }
});
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.chart-canvas {
  width: 100%;
  height: 600px;
}
</style> 