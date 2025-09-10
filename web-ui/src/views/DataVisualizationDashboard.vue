<template>
  <div class="dashboard-nav">
    <h1>数据可视化仪表板</h1>
    <!-- <p>请选择一个图表查看详情：</p> -->
    <ul>
      <!--      <li><router-link to="/dashboard/country-distribution">国家分布统计</router-link></li>-->
      <!--      <li><router-link to="/dashboard/language-distribution">语言分布统计</router-link></li>-->
      <li><router-link to="/dashboard/corpus-overview">语料库总览</router-link></li>
      <li><router-link to="/dashboard/contributor-distribution">贡献者语料容量分布</router-link></li>
      <li><router-link to="/dashboard/time-series-analysis">语料收集趋势分析</router-link></li>
      <li><router-link to="/dashboard/thematic-analysis">语料库主题深度分析</router-link></li>
      <li><router-link to="/dashboard/college-overview">学院/机构总览</router-link></li>
      <li><router-link to="/dashboard/domain-overview">领域总览</router-link></li>
    </ul>
  </div>
</template>

<script setup>
// This component now serves as a navigation page.
// All chart logic has been moved to individual components.
</script>

<style scoped>
.dashboard-nav {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

p {
  text-align: center;
  margin-bottom: 40px;
  font-size: 1.2em;
  color: #555;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  background-color: #fff;
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

li:hover {
  transform: translateY(-5px);
}

li a {
  display: block;
  padding: 20px;
  text-decoration: none;
  color: #333;
  font-weight: bold;
  font-size: 1.1em;
}
</style>
