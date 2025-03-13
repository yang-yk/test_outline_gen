<script setup>
// 移除不必要的导入
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  width: 100%;
  min-height: 100vh;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.el-container {
  height: 100%;
  width: 100%;
  min-width: 100%;
  padding: 0;
  margin: 0;
}

.el-main {
  padding: 0;
  width: 100%;
  min-width: 100%;
  height: 100%;
}

/* 移除右侧留白 */
.el-row {
  margin-right: 0 !important;
  margin-left: 0 !important;
}

.el-col {
  padding-right: 0 !important;
  padding-left: 0 !important;
}

/* 卡片样式优化 */
.el-card {
  border-radius: 8px;
  transition: all 0.3s;
  margin-bottom: 16px;
}

.el-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.el-card__header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.el-card__body {
  padding: 20px;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
