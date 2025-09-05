<template>
  <div class="page-container">
    <div class="card">
      <h2>国家分布统计</h2>
      <div ref="countryChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getCountryDistribution } from '@/services/dashboard';

const countryChartRef = ref(null);

const initCountryChart = async () => {
  try {
    const response = await getCountryDistribution();
    const chartData = response.data;
    const chart = echarts.init(countryChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: chartData.map(item => item.country)
      },
      series: [
        {
          name: '国家分布',
          type: 'pie',
          radius: '50%',
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
          data: chartData.map(item => ({ name: item.country, value: item.corpusCount }))
        }
      ]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch country distribution data:', error);
  }
};

onMounted(() => {
  if (countryChartRef.value) {
    initCountryChart();
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