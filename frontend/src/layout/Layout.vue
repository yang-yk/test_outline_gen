<!-- Layout.vue -->
<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-title">
        中科院软件所<br>测试大纲智能生成工具
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/outline">
          <el-icon><Document /></el-icon>
          <span>大纲生成</span>
        </el-menu-item>
        <el-menu-item index="/document">
          <el-icon><Folder /></el-icon>
          <span>文档管理</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主容器 -->
    <el-container>
      <!-- 顶部标题栏 -->
      <el-header class="header">
        <div class="header-title">
          {{ getPageTitle }}
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Document, Folder, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户信息从状态管理中获取
const username = computed(() => userStore.userInfo.username)
const userAvatar = computed(() => userStore.userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 页面标题
const getPageTitle = computed(() => {
  const titles = {
    '/outline': '测试大纲生成',
    '/document': '文档管理',
    '/profile': '个人中心'
  }
  return titles[route.path] || '欢迎使用'
})

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('退出登录成功')
  }
}

// 在组件挂载时加载用户信息
onMounted(async () => {
  try {
    await userStore.loadUserInfo()
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: #fff;
  overflow: hidden;
}

.sidebar-title {
  padding: 20px 0;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.5;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  color: #333;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style> 