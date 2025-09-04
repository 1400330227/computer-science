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
      <!-- 2. 国家分布 - 柱状图 (无变化) -->
      <div class="card">
        <h2>国家分布统计</h2>
        <div ref="countryChartRef" class="chart-canvas"></div>
      </div>

      <!-- 3. 语言分布 - 饼图 (无变化) -->
      <div class="card">
        <h2>语言分布统计</h2>
        <div ref="languageChartRef" class="chart-canvas"></div>
      </div>

      <!-- 4. 贡献者分析 - 已应用新样式 -->
      <div class="card">
        <!-- 标题已更新以匹配新图表内容 -->
        <h2>贡献者语料容量分布 (Top 7)</h2>
        <div ref="contributorChartRef" class="chart-canvas"></div>
      </div>

      <!-- 5. 时间序列分析 - 折线图 (无变化) -->
      <div class="card">
        <h2>语料收集趋势分析 (最近30天)</h2>
        <div ref="timeSeriesChartRef" class="chart-canvas"></div>
      </div>

      <!-- 6. 主题分布 - 南丁格尔玫瑰图 (无变化) -->
      <div class="card full-width">
        <h2>语料库主题分布</h2>
        <div ref="thematicChartRef" class="chart-canvas"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import apiClient from '@/services/api.js'
// --- 响应式数据引用等 (无变化) ---
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

// --- ECharts 初始化函数 ---

// 2. 初始化国家分布图表 (无变化)
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

// 3. 初始化语言分布图表 (无变化)
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

// ==================== 核心修改区域 ====================
// 4. 初始化贡献者分析图表 (已仿照模板样式进行修改)
const initContributorChart = (data) => {
  if (!contributorChartRef.value) return;

  // 饼图不适合展示过多数据，取前7名
  const topData = data.slice(0, 7);

  contributorChartInstance = echarts.init(contributorChartRef.value);

  // 仿照模板的配置选项
  const option = {
    backgroundColor: 'transparent',
    color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452'],
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} GB ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    legend: {
      orient: 'horizontal',
      bottom: 10,
      itemGap: 20,
      textStyle: {
        color: '#2c3e50',
        fontSize: 14
      }
    },
    // 注意：模板中的 title 这里我们用 card 的 h2 代替，但可以保留一个副标题
    // title: { ... },
    series: [
      {
        name: '贡献容量', // 模板中是'语料完成度'，这里改为更准确的名称
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        },
        label: {
          show: true,
          formatter: '{b}: {c} GB',
          fontSize: 14,
          // fontWeight: 'bold' // 根据您的喜好决定是否加粗
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 15,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          length: 15,
          length2: 10,
          smooth: true
        },
        // 关键：将后端数据映射到图表需要的数据结构
        data: topData.map(item => ({
          name: item.contributorName,
          value: item.totalCapacityGb // 使用容量作为value
        }))
      }
    ]
  };

  contributorChartInstance.setOption(option);
};
// ======================================================

// 5. 初始化时间序列分析图表 (无变化)
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

// 6. 初始化主题分布图 (无变化)
const initThematicChart = (data) => {
  if (!thematicChartRef.value) return;
  thematicChartInstance = echarts.init(thematicChartRef.value);
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)' },
    legend: { left: 'center', top: 'bottom', data: data.map(item => item.inferredTopic) },
    series: [ { name: '主题分布', type: 'pie', radius: [20, 140], center: ['50%', '50%'], roseType: 'area', itemStyle: { borderRadius: 8 }, data: data.map(item => ({ value: item.corpusCount, name: item.inferredTopic })) } ]
  };
  thematicChartInstance.setOption(option);
}


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

  } catch (error) {
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
