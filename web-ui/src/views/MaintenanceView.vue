<template>
  <div class="maintenance-container">
    <div class="maintenance-card">
      <h1>系统维护中</h1>
      <p>为了提供更好的服务，HDFS语料管理系统正在进行服务器迁移和系统升级。</p>
      <p>服务预计将在两天后恢复。给您带来的不便，敬请谅解。</p>
      <hr>
      <p class="en-notice">
        <strong>Under Maintenance</strong><br>
        To provide better service, the HDFS Corpus Management System is undergoing server migration and system upgrades.
        Service is expected to resume in a couple of days. We apologize for any inconvenience.
      </p>
    </div>
  </div>
</template>

<style scoped>
.maintenance-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}
.maintenance-card {
  text-align: center;
  background-color: #fff;
  padding: 40px 50px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  max-width: 600px;
}
h1 {
  font-size: 2em;
  color: #303133;
  margin-bottom: 20px;
}
p {
  font-size: 1.1em;
  color: #606266;
  line-height: 1.6;
}
hr {
  border: none;
  border-top: 1px solid #e4e7ed;
  margin: 30px 0;
}
.en-notice {
  font-size: 0.9em;
  color: #909399;
}
</style>
