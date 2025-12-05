<template>
  <div class="home-container">
    <div class="home-background"></div>
    <el-row justify="center" class="home-row">
      <el-col :span="24" class="home-col">
        <div class="hero-section">
          <div class="hero-content">
            <h1 class="hero-title">云科技不动产查询系统</h1>
            <p class="hero-subtitle">便捷、安全、高效的不动产信息查询平台</p>
            <!-- 未登录状态 -->
            <div class="hero-actions" v-if="!isLoggedIn">
              <el-button type="primary" size="large" @click="$router.push('/login')" class="hero-button">
                立即体验
              </el-button>
              <el-button size="large" @click="$router.push('/register')" class="hero-button">
                免费注册
              </el-button>
            </div>
            <!-- 已登录状态 -->
            <div class="hero-actions" v-else>
              <el-button type="primary" size="large" @click="$router.push('/query')" class="hero-button">
                开始查询
              </el-button>
              <el-button size="large" @click="$router.push('/dashboard')" class="hero-button">
                查看控制台
              </el-button>
            </div>
          </div>
          <div class="hero-image">
            <div class="illustration-container">
              <div class="illustration-bg">
                <div class="illustration-icon">
                  <i class="el-icon-office-building"></i>
                </div>
                <div class="illustration-features">
                  <div class="feature-item">
                    <i class="el-icon-check"></i>
                    <span>快速查询</span>
                  </div>
                  <div class="feature-item">
                    <i class="el-icon-check"></i>
                    <span>安全可靠</span>
                  </div>
                  <div class="feature-item">
                    <i class="el-icon-check"></i>
                    <span>实时反馈</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row justify="center" class="features-row">
      <el-col :span="24" class="features-col">
        <div class="section-title">
          <h2>核心功能</h2>
          <p>为您提供全方位的不动产信息查询服务</p>
        </div>
        <el-row :gutter="30" class="features-grid">
          <el-col :span="8" v-for="(feature, index) in features" :key="index" class="feature-col">
            <el-card class="feature-card" shadow="hover">
              <div class="feature-icon">
                <i :class="feature.icon"></i>
              </div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- 未登录时显示注册引导 -->
    <el-row justify="center" class="cta-row" v-if="!isLoggedIn">
      <el-col :span="24" class="cta-col">
        <el-card class="cta-card" shadow="never">
          <div class="cta-content">
            <h2>开始您的不动产查询之旅</h2>
            <p>立即注册，享受便捷的不动产信息查询服务</p>
            <div class="cta-actions">
              <el-button type="primary" size="large" @click="$router.push('/register')">
                免费注册
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 已登录时显示快速入口 -->
    <el-row justify="center" class="cta-row" v-else>
      <el-col :span="24" class="cta-col">
        <el-card class="cta-card" shadow="never">
          <div class="cta-content">
            <h2>欢迎回来，{{ username }}！</h2>
            <p>快速开始您的不动产查询服务</p>
            <div class="cta-actions">
              <el-button type="primary" size="large" @click="$router.push('/query')">
                提交查询
              </el-button>
              <el-button size="large" @click="$router.push('/dashboard')" style="margin-left: 15px;">
                查看记录
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AuthService from '@/services/AuthService'

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const isLoggedIn = ref(false)
    const username = ref('')
    const currentUser = ref(null)
    
    const features = ref([
      {
        icon: 'el-icon-search',
        title: '在线查询',
        description: '随时随地提交不动产查询申请，无需排队等候'
      },
      {
        icon: 'el-icon-upload',
        title: '文件上传',
        description: '支持多种格式文件上传，保障信息完整性'
      },
      {
        icon: 'el-icon-time',
        title: '实时进度',
        description: '实时跟踪查询进度，掌握办理状态'
      }
    ])
    
    const checkAuthStatus = () => {
      isLoggedIn.value = AuthService.isAuthenticated()
      currentUser.value = AuthService.getCurrentUser()
      if (isLoggedIn.value && currentUser.value) {
        username.value = currentUser.value.username || '用户'
      } else {
        username.value = ''
      }
    }
    
    onMounted(() => {
      checkAuthStatus()
      // 监听认证状态变化
      window.addEventListener('auth-status-changed', checkAuthStatus)
      window.addEventListener('storage', checkAuthStatus)
    })
    
    // 监听路由变化
    watch(() => route.path, () => {
      checkAuthStatus()
    })
    
    return {
      isLoggedIn,
      username,
      features
    }
  }
}
</script>

<style scoped>
.home-container {
  position: relative;
  min-height: calc(100vh - 60px);
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  overflow: hidden;
}

.home-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.1;
  z-index: -1;
}

.home-row {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 60px 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 25px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
  margin-top: 0;
  backdrop-filter: blur(10px);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.hero-section:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.hero-content {
  flex: 1;
  padding: 0 60px;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 20px;
  line-height: 1.3;
}

.hero-subtitle {
  font-size: 20px;
  color: #7f8c8d;
  margin-bottom: 40px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.hero-button {
  padding: 16px 32px;
  font-size: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.hero-button:first-child {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.hero-button:first-child:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.hero-button:last-child:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.hero-image {
  flex: 1;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.illustration-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.illustration-bg {
  position: relative;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 20px;
  padding: 50px 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.15);
  border: 2px solid rgba(102, 126, 234, 0.2);
  backdrop-filter: blur(10px);
}

.illustration-icon {
  font-size: 100px;
  color: #667eea;
  margin-bottom: 30px;
  animation: float 4s ease-in-out infinite;
  text-align: center;
  position: relative;
}

.illustration-icon::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  border-radius: 50%;
  z-index: -1;
  animation: pulse 3s ease-in-out infinite;
}

.illustration-icon i {
  font-size: inherit;
  color: inherit;
  display: block;
}

.illustration-features {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  font-size: 16px;
  color: #2c3e50;
  font-weight: 500;
}

.feature-item:hover {
  transform: translateX(5px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.95);
}

.feature-item i {
  color: #67c23a;
  font-size: 20px;
  font-weight: bold;
}

.feature-item span {
  flex: 1;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.3;
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
  }
  100% {
    transform: translateY(0px);
  }
}

.features-row {
  margin-top: 80px;
}

.features-col {
  max-width: 1200px;
}

.section-title {
  text-align: center;
  margin-bottom: 50px;
}

.section-title h2 {
  font-size: 36px;
  margin-bottom: 15px;
  color: #2c3e50;
}

.section-title p {
  font-size: 18px;
  color: #7f8c8d;
}

.features-grid {
  display: flex;
  flex-wrap: wrap;
}

.feature-col {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.feature-col:hover {
  transform: translateY(-8px);
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  height: 100%;
  border-radius: 15px;
  border: none;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-card:hover {
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 22px;
  margin-bottom: 15px;
  color: #2c3e50;
}

.feature-card p {
  font-size: 16px;
  color: #7f8c8d;
  line-height: 1.6;
}

.cta-row {
  margin-top: 80px;
  margin-bottom: 50px;
}

.cta-col {
  max-width: 1200px;
}

.cta-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 60px 40px;
  border: none;
  border-radius: 20px;
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s ease;
}

.cta-card:hover {
  transform: translateY(-5px);
}

.cta-content h2 {
  font-size: 36px;
  margin-bottom: 20px;
  color: white;
}

.cta-content p {
  font-size: 20px;
  margin-bottom: 40px;
  color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 992px) {
  .hero-section {
    flex-direction: column;
  }
  
  .hero-content {
    padding: 30px;
    text-align: center;
  }
  
  .hero-actions {
    justify-content: center;
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .hero-image {
    margin-top: 30px;
  }
  
  .features-grid {
    flex-direction: column;
  }
  
  .feature-col {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .hero-title {
    font-size: 32px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .section-title h2 {
    font-size: 28px;
  }
  
  .cta-content h2 {
    font-size: 28px;
  }
  
  .cta-content p {
    font-size: 18px;
  }
}
</style>