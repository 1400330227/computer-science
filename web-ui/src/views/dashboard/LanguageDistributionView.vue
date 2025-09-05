<template>
  <div class="page-container">
    <div class="card">
      <h2>语言分布统计</h2>
      <div ref="languageChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getLanguageDistribution } from '@/services/dashboard';

const languageChartRef = ref(null);

const initLanguageChart = async () => {
  try {
    const response = await getLanguageDistribution();
    const chartData = response.data;
    const chart = echarts.init(languageChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: chartData.map(item => item.language)
      },
      series: [
        {
          name: '语言分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '30',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: chartData.map(item => ({ name: item.language, value: item.corpusCount }))
        }
      ]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch language distribution data:', error);
  }
};

onMounted(() => {
  if (languageChartRef.value) {
    initLanguageChart();
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