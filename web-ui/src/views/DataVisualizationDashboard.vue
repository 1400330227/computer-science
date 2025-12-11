<template>
  <div class="dashboard-nav">
    <h1>数据可视化仪表板</h1>
    <!-- <p>请选择一个图表查看详情：</p> -->
    <ul>
      <li><router-link to="/dashboard/corpus-overview">语料库总览</router-link></li>
      <li><router-link to="/dashboard/contributor-distribution">贡献者语料容量分布</router-link></li>
      <li><router-link to="/dashboard/time-series-analysis">语料收集趋势分析</router-link></li>
      <li><router-link to="/dashboard-landing" target="_blank"
          rel="noopener noreferrer">广西大学东盟语料库管理与标注平台数据可视化(对外开放)</router-link></li>
    </ul>
  </div>
</template>

<script setup>
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
