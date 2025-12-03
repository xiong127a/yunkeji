<template>
  <div class="header-container">
    <el-header class="header">
      <div class="header-content">
        <div class="logo-section">
          <img alt="云科技Logo" src="../assets/yunkeji-logo.jpg" class="logo-img"/>
          <router-link to="/" class="logo-text">云科技不动产查询系统</router-link>
        </div>
        
        <div class="nav-section">
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            :ellipsis="false"
            background-color="#333"
            text-color="#fff"
            active-text-color="#ffd04b"
            @select="handleSelect"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/dashboard" v-if="isLoggedIn">控制台</el-menu-item>
            <el-menu-item index="/query" v-if="isLoggedIn">查询</el-menu-item>
            
            <div class="flex-grow" />
            
            <el-menu-item index="/login" v-if="!isLoggedIn">登录</el-menu-item>
            <el-menu-item index="/register" v-if="!isLoggedIn">注册</el-menu-item>
            <el-sub-menu index="user" v-if="isLoggedIn">
              <template #title>{{ username }}</template>
              <el-menu-item index="profile">个人中心</el-menu-item>
              <el-menu-item index="settings">设置</el-menu-item>
              <el-menu-item index="logout">退出登录</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </div>
      </div>
    </el-header>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AuthService from '@/services/AuthService'

export default {
  name: 'AppHeader',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // 认证状态
    const isLoggedIn = ref(false)
    const username = ref('用户')
    
    const activeIndex = computed(() => {
      return route.path
    })
    
    const checkAuthStatus = () => {
      // 检查用户是否已登录
      isLoggedIn.value = AuthService.isAuthenticated()
      // 在实际应用中，可以从token或用户信息中获取用户名
      if (isLoggedIn.value) {
        username.value = '用户' // 这里应该从用户信息中获取真实用户名
      }
    }
    
    const handleSelect = (key) => {
      if (key === 'logout') {
        // 执行登出操作
        AuthService.logout()
        isLoggedIn.value = false
        router.push('/login')
      } else {
        router.push(key)
      }
    }
    
    // 组件挂载时检查认证状态
    onMounted(() => {
      checkAuthStatus()
      
      // 监听localStorage变化（通过storage事件，但只能监听其他窗口的变化）
      // 为了监听当前窗口的变化，我们监听路由变化
      window.addEventListener('storage', checkAuthStatus)
      
      // 监听自定义事件，用于在当前窗口内通知登录状态变化
      window.addEventListener('auth-status-changed', checkAuthStatus)
    })
    
    // 当组件被激活时重新检查
    onActivated(() => {
      checkAuthStatus()
    })
    
    // 监听路由变化，当路由变化时重新检查认证状态
    watch(() => route.path, () => {
      checkAuthStatus()
    })
    
    return {
      activeIndex,
      isLoggedIn,
      username,
      handleSelect
    }
  }
}
</script>

<style scoped>
.header-container {
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header {
  background: linear-gradient(135deg, #333 0%, #222 100%) !important;
  color: white;
  padding: 0;
  height: 60px !important;
  line-height: 60px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.logo-img {
  width: 45px;
  height: 45px;
  object-fit: contain;
  margin-right: 12px;
  border-radius: 4px;
}

.logo-text {
  color: white;
  text-decoration: none;
  transition: color 0.3s;
}

.logo-text:hover {
  color: #ffd04b;
}

.nav-section {
  flex: 1;
  margin-left: 20px;
}

.flex-grow {
  flex-grow: 1;
}

:deep(.el-menu--horizontal) {
  border-bottom: none !important;
  background: transparent !important;
}

:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  height: 60px !important;
  line-height: 60px !important;
  border-bottom: none !important;
}

:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.15) !important;
  border-bottom: none !important;
}
</style>