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
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#eaeaea',
        borderWidth: 1,
        textStyle: {
          color: '#333'
        },
        axisPointer: {
          type: 'shadow',
          label: {
            backgroundColor: '#6a7985'
          }
        },
      },
      legend: {
        data: ['每日新增语料容量(GB)', '每日新增文件数量(份)'],
        bottom: 10,
        itemWidth: 15,
        itemHeight: 9,
        textStyle: {
          color: '#555'
        }
      },
      grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: chartData.map(item => item.contributionDate),
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        },
        axisLabel: {
          color: '#7f8c8d'
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '每日新增文件数量(份)',
        },
      ],
      series: [
        {
          name: '每日新增语料容量(GB)',
          type: 'line',
          smooth: true,
          symbol: 'emptyCircle',
          symbolSize: 8,
          lineStyle: {
            width: 4,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#5470c6' },
                { offset: 1, color: '#81c3fd' }
              ]
            }
          },
          data: chartData.map(item => item.dailyCapacityAdded),
          itemStyle: {
            color: '#5470c6',
            borderWidth: 2,
            borderColor: '#fff'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(84, 112, 198, 0.5)' },
                { offset: 1, color: 'rgba(84, 112, 198, 0.1)' }
              ]
            }
          },
          emphasis: {
            focus: 'series'
          },
        },
        {
          name: '每日新增文件数量(份)',
          type: 'line',
          smooth: true,
          symbol: 'emptyCircle',
          symbolSize: 8,
          lineStyle: {
            width: 4,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#91cc75' },
                { offset: 1, color: '#bef67a' }
              ]
            }
          },
          itemStyle: {
            color: '#91cc75',
            borderWidth: 2,
            borderColor: '#fff'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(145, 204, 117, 0.5)' },
                { offset: 1, color: 'rgba(145, 204, 117, 0.1)' }
              ]
            }
          },
          emphasis: {
            focus: 'series'
          },
          data: chartData.map(item => item.dailyFilesAdded),
        }
      ]
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
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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
