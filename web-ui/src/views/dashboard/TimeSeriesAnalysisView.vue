<template>
  <div class="page-container">
    <div class="card">
      <h2>语料收集趋势分析 (最近30天)</h2>
      <div ref="timeSeriesChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getTimeSeriesAnalysis } from '@/services/dashboard';

const timeSeriesChartRef = ref(null);

const initTimeSeriesChart = async () => {
  try {
    const response = await getTimeSeriesAnalysis();
    const chartData = response.data;
    const chart = echarts.init(timeSeriesChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: chartData.map(item => item.date)
      },
      yAxis: {
        type: 'value',
        name: 'Corpus Count'
      },
      series: [{
        data: chartData.map(item => item.corpusCount),
        type: 'line',
        smooth: true
      }]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch time series analysis data:', error);
  }
};

onMounted(() => {
  if (timeSeriesChartRef.value) {
    initTimeSeriesChart();
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