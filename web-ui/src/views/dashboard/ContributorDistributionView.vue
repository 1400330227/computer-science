<template>
  <div class="page-container">
    <div class="card">
      <h2>贡献者语料容量分布</h2>
      <div ref="contributorChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getContributorAnalysis } from '@/services/dashboard';

const contributorChartRef = ref(null);

const initContributorChart = async () => {
  try {
    const response = await getContributorAnalysis();
    const chartData = response.data;
    const chart = echarts.init(contributorChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: chartData.map(item => item.contributorName)
      },
      yAxis: {
        type: 'value',
        name: 'Capacity (GB)'
      },
      series: [{
        data: chartData.map(item => item.totalCapacityGB),
        type: 'bar',
        barWidth: '60%'
      }]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch contributor analysis data:', error);
  }
};

onMounted(() => {
  if (contributorChartRef.value) {
    initContributorChart();
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
h2 {
  margin-top: 0;
  margin-bottom: 20px;
}
.chart-canvas {
  width: 100%;
  height: 600px;
}
</style> 