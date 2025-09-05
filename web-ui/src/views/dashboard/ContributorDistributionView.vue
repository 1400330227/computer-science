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
        },
        formatter: '{b}<br/>容量: {c} GB'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: chartData.map(item => item.contributorName),
        axisLabel: {
          rotate: 0, // 倾斜标签防止重叠
          interval: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '上传语料容量 (GB)'
      },
      series: [{
        data: chartData.map(item => item.totalCapacityGb),
        type: 'bar',
        showBackground: true,
        barWidth: '60%',
        // itemStyle: {
        //   color: '#5470c6'
        // },
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        },
        // ==================== 核心修改区域 ====================
        // 在这里添加 label 配置
        label: {
          show: true,         // 设置为 true 来显示标签
          position: 'top',    // 标签的位置，'top' 表示在柱子顶部
          formatter: '{c}GB',   // 标签内容格式，{c} 代表数据值
          color: '#333',       // 标签文字颜色
          fontSize: 12          // 标签文字大小
        }
        // ======================================================
      }]
    };    chart.setOption(option);

    window.addEventListener('resize', () => {
      chart.resize();
    });
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
