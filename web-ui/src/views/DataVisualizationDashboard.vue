<template>
  <div class="dashboard-container">
    <h1 class="dashboard-title">数据可视化仪表盘</h1>

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

    <div v-if="isLoading" class="loading-state">
      正在加载图表数据...
    </div>

    <!-- 图表网格布局 -->
    <div class="charts-grid">
      <!-- ... 其他图表无变化 ... -->
      <div class="card">
        <h2>国家分布统计</h2>
        <div ref="countryChartRef" class="chart-canvas"></div>
      </div>
      <div class="card">
        <h2>语言分布统计</h2>
        <div ref="languageChartRef" class="chart-canvas"></div>
      </div>
      <div class="card">
        <h2>贡献者语料容量分布 (Top 7)</h2>
        <div ref="contributorChartRef" class="chart-canvas"></div>
      </div>
      <div class="card">
        <h2>语料收集趋势分析 (最近30天)</h2>
        <div ref="timeSeriesChartRef" class="chart-canvas"></div>
      </div>

      <!-- 6. 主题分布 - 已更新为新的简约风格 -->
      <div class="card full-width">
        <!-- 标题更新以匹配图片 -->
        <h2>语料库主题深度分析</h2>
        <div ref="thematicChartRef" class="chart-canvas"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import apiClient from '@/services/api.js'

// ... 其他变量和图表初始化函数无变化 ...
const isLoading = ref(true);
const corpusOverview = ref(null);
const countryChartRef = ref(null);
const languageChartRef = ref(null);
const contributorChartRef = ref(null);
const timeSeriesChartRef = ref(null);
const thematicChartRef = ref(null);
let countryChartInstance = null;
let languageChartInstance = null;
let contributorChartInstance = null;
let timeSeriesChartInstance = null;
let thematicChartInstance = null;

const initCountryChart = (data) => {
  if (!countryChartRef.value) return;
  countryChartInstance = echarts.init(countryChartRef.value);
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(item => item.country), axisLabel: { interval: 0, rotate: 30 } },
    yAxis: { type: 'value', name: '语料库数量' },
    series: [ { name: '语料库数量', type: 'bar', data: data.map(item => item.corpusCount), itemStyle: { color: '#5470c6' } } ],
    dataZoom: [{ type: 'inside' }],
  };
  countryChartInstance.setOption(option);
};
const initLanguageChart = (data) => {
  if (!languageChartRef.value) return;
  languageChartInstance = echarts.init(languageChartRef.value);
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', data: data.map(item => item.language) },
    series: [ { name: '语言分布', type: 'pie', radius: '70%', center: ['60%', '50%'], data: data.map(item => ({ value: item.corpusCount, name: item.language })), emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } } } ]
  };
  languageChartInstance.setOption(option);
};
const initContributorChart = (data) => {
  if (!contributorChartRef.value) return;
  const topData = data.slice(0, 7).reverse();
  contributorChartInstance = echarts.init(contributorChartRef.value);
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: '{b}<br/>{a}: {c} GB' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '容量 (GB)', boundaryGap: [0, 0.01] },
    yAxis: { type: 'category', data: topData.map(item => item.contributorName) },
    series: [ { name: '贡献容量', type: 'bar', data: topData.map(item => item.totalCapacityGb), itemStyle: { color: '#fac858' } } ]
  };
  contributorChartInstance.setOption(option);
};
const initTimeSeriesChart = (data) => {
  if (!timeSeriesChartRef.value) return;
  const recentData = data.slice(0, 30).reverse();
  timeSeriesChartInstance = echarts.init(timeSeriesChartRef.value);
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['每日新增语料库', '每日新增文件'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: recentData.map(item => item.contributionDate) },
    yAxis: { type: 'value' },
    series: [
      { name: '每日新增语料库', type: 'line', data: recentData.map(item => item.dailyCorpusAdded), smooth: true, itemStyle: { color: '#fac858' } },
      { name: '每日新增文件', type: 'line', data: recentData.map(item => item.dailyFilesAdded), smooth: true, itemStyle: { color: '#ee6666' } }
    ]
  };
  timeSeriesChartInstance.setOption(option);
};

// ==================== 核心修改区域 ====================
// 6. 初始化主题分布图 (已更新为新的简约风格)
const initThematicChart = (data) => {
  if (!thematicChartRef.value) return;
  thematicChartInstance = echarts.init(thematicChartRef.value);

  const bubbleData = data.map(item => ([
    item.corpusCount,
    item.countriesCovered,
    item.totalCapacityGb,
    item.inferredTopic
  ]));

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        const data = params.data;
        return `<strong>${data[3]}</strong><br/>` +
          `语料库数量: ${data[0]}<br/>` +
          `覆盖国家数: ${data[1]}<br/>` +
          `总容量: ${data[2].toFixed(2)} GB`;
      }
    },
    grid: {
      left: '8%', right: '12%', bottom: '10%', top: '10%',
    },
    xAxis: {
      type: 'value',
      name: '语料库数量',
      nameLocation: 'middle',
      nameGap: 30,
      axisLine: { show: true, lineStyle: { color: '#333' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { type: 'dashed', color: '#e9e9e9' } }
    },
    yAxis: {
      type: 'value',
      name: '覆盖国家数',
      nameTextStyle: { align: 'left' },
      axisLine: { show: true, lineStyle: { color: '#333' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { type: 'dashed', color: '#e9e9e9' } }
    },
    visualMap: {
      min: 0,
      max: Math.max(...data.map(item => item.totalCapacityGb)),
      dimension: 2,
      orient: 'vertical',
      right: 20,
      top: 'center',
      text: ['高', '低'],
      calculable: true,
      // 匹配图片的浅蓝色渐变
      inRange: {
        color: ['#cce7f4', '#a3d1e6', '#529BDB']
      }
    },
    series: [{
      name: '主题分析',
      type: 'scatter',
      symbolSize: function (data) {
        // 调整了乘数和基数，使气泡大小更接近示例
        return Math.sqrt(data[2]) * 5 + 15;
      },
      // 核心修改：将标签放在气泡内部
      label: {
        show: true,
        position: 'inside', // 标签位置
        formatter: '{@[3]}',
        color: '#fff', // 标签文字颜色
        // 添加文字描边或阴影，使其在不同颜色背景下都清晰可见
        textShadowBlur: 2,
        textShadowColor: 'rgba(0, 0, 0, 0.5)'
      },
      // 核心修改：调整气泡样式，添加柔和光晕
      itemStyle: {
        // 使用半透明的颜色作为阴影色，来模拟光晕效果
        shadowBlur: 10,
        shadowColor: 'rgba(82, 155, 219, 0.4)',
        shadowOffsetY: 0 // 确保光晕是均匀的，而不是下沉阴影
      },
      data: bubbleData
    }]
  };
  thematicChartInstance.setOption(option);
}
// ======================================================

// --- 数据获取与图表渲染 (无变化) ---
onMounted(async () => {
  try {
    const [
      overviewRes, countryRes, languageRes, contributorRes, timeSeriesRes, thematicRes,
    ] = await Promise.all([
      apiClient.get('/dataView/corpusOverview'),
      apiClient.get('/dataView/countryDistribution'),
      apiClient.get('/dataView/languageDistribution'),
      apiClient.get('/dataView/contributorAnalysis'),
      apiClient.get('/dataView/timeSeriesAnalysis'),
      apiClient.get('/dataView/thematicSummary'),
    ]);

    corpusOverview.value = overviewRes.data[0];

    await nextTick();
    initCountryChart(countryRes.data);
    initLanguageChart(languageRes.data);
    initContributorChart(contributorRes.data);
    initTimeSeriesChart(timeSeriesRes.data);
    initThematicChart(thematicRes.data);

  } catch (error)
  {
    console.error("加载数据可视化数据时出错:", error);
    alert("数据加载失败，请检查后端服务是否正常或查看控制台错误信息。");
  } finally {
    isLoading.value = false;
  }

  window.addEventListener('resize', () => {
    countryChartInstance?.resize();
    languageChartInstance?.resize();
    contributorChartInstance?.resize();
    timeSeriesChartInstance?.resize();
    thematicChartInstance?.resize();
  });
});
</script>

<style scoped>
/* 样式部分无变化 */
.dashboard-container {
  padding: 24px;
  background-color: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
.dashboard-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}
.loading-state {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #888;
}
.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  padding: 20px;
  margin-bottom: 20px;
}
.card h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}
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
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.full-width {
  grid-column: 1 / -1;
}
.chart-canvas {
  width: 100%;
  height: 400px;
}
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 768px) {
  .stat-item {
    flex-basis: 33.33%;
  }
  .stat-item .value {
    font-size: 2rem;
  }
}
</style>
