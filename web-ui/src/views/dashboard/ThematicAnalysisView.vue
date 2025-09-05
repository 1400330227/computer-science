<template>
  <div class="page-container">
    <div class="card full-width">
      <h2>语料库主题深度分析</h2>
      <div ref="thematicChartRef" class="chart-canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getThematicSummary } from '@/services/dashboard';

const thematicChartRef = ref(null);

const initThematicChart = async () => {
  try {
    const response = await getThematicSummary();
    const chartData = response.data;
    const chart = echarts.init(thematicChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}'
      },
      series: [{
        type: 'wordCloud',
        shape: 'circle',
        left: 'center',
        top: 'center',
        width: '100%',
        height: '100%',
        right: null,
        bottom: null,
        sizeRange: [12, 60],
        rotationRange: [-90, 90],
        rotationStep: 45,
        gridSize: 8,
        drawOutOfBound: false,
        textStyle: {
          fontFamily: 'sans-serif',
          fontWeight: 'bold',
          color: function () {
            return 'rgb(' + [
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160)
            ].join(',') + ')';
          }
        },
        emphasis: {
          focus: 'self',
          textStyle: {
            shadowBlur: 10,
            shadowColor: '#333'
          }
        },
        data: chartData.map(item => ({ name: item.theme, value: item.corpusCount }))
      }]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch thematic summary data:', error);
  }
};

onMounted(() => {
  if (thematicChartRef.value) {
    initThematicChart();
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
.full-width {
  grid-column: 1 / -1;
}
</style> 