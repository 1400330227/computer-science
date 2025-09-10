<template>
  <div class="page-container">
    <div class="card">
      <h2>学院/机构总览 — 语料与容量</h2>
      <div ref="collegeCorpusChartRef" class="chart-canvas"></div>
    </div>
    <div class="card">
      <h2>学院/机构贡献者与文件数</h2>
      <div ref="collegeContributorChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getCollegeOverview } from '@/services/dashboard';

const collegeCorpusChartRef = ref(null);
const collegeContributorChartRef = ref(null);

const initCharts = async () => {
  const response = await getCollegeOverview();
  const data = response.data || [];
  const colleges = data.map(item => item.college);

  // Chart 1: Corpus count and total capacity per college
  const corpusChart = echarts.init(collegeCorpusChartRef.value);
  corpusChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['语料库数量', '总估算容量(GB)'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: colleges, axisLabel: { interval: 0, rotate: 0 } },
    yAxis: [
      { type: 'value', name: '语料库数量' },
      { type: 'value', name: '容量(GB)' }
    ],
    series: [
      {
        name: '语料库数量',
        type: 'bar',
        data: data.map(i => i.corpusCount),
        barWidth: '45%',
        itemStyle: { color: '#5470c6' },
        label: { show: true, position: 'top' }
      },
      {
        name: '总估算容量(GB)',
        type: 'line',
        yAxisIndex: 1,
        data: data.map(i => i.totalCapacityGb),
        smooth: true,
        itemStyle: { color: '#91cc75' }
      }
    ]
  });

  // Chart 2: Contributors and files count per college
  const contributorChart = echarts.init(collegeContributorChartRef.value);
  contributorChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['贡献者数量', '文件总数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: colleges, axisLabel: { interval: 0, rotate: 0 } },
    yAxis: { type: 'value' },
    series: [
      {
        name: '贡献者数量',
        type: 'bar',
        data: data.map(i => i.contributorsCount),
        barWidth: '45%',
        itemStyle: { color: '#fac858' },
        label: { show: true, position: 'top' }
      },
      {
        name: '文件总数',
        type: 'bar',
        data: data.map(i => i.filesCount),
        barWidth: '45%',
        itemStyle: { color: '#73c0de' },
        label: { show: true, position: 'top' }
      }
    ]
  });

  window.addEventListener('resize', () => {
    corpusChart.resize();
    contributorChart.resize();
  });
};

onMounted(() => {
  if (collegeCorpusChartRef.value && collegeContributorChartRef.value) {
    initCharts();
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
  margin-bottom: 20px;
}
.chart-canvas {
  width: 100%;
  height: 520px;
}
</style> 