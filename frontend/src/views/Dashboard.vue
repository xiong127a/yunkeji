<template>
  <div class="dashboard-container">
    <div class="dashboard-background"></div>
    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="24" class="welcome-col">
        <el-card class="welcome-card" shadow="always">
          <div class="welcome-content">
            <h2>欢迎使用云科技不动产查询系统</h2>
            <p>一站式不动产信息查询服务平台，为您提供便捷、安全、高效的服务体验</p>
            <el-button type="primary" @click="$router.push('/query')" class="start-button">
              开始查询 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="records-row">
      <el-col :span="16" class="records-col">
        <el-card class="main-card" shadow="always">
          <template #header>
            <div class="card-header">
              <span>我的查询记录</span>
              <el-button class="card-button" type="text">查看更多</el-button>
            </div>
          </template>
          <div class="records-placeholder">
            <i class="el-icon-document"></i>
            <p>暂无查询记录</p>
            <el-button type="primary" @click="$router.push('/query')">立即查询</el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8" class="sidebar-col">
        <el-card class="sidebar-card" shadow="always">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
            </div>
          </template>
          <div class="notification-item" v-for="notification in notifications" :key="notification.id">
            <h4>{{ notification.title }}</h4>
            <p>{{ notification.content }}</p>
            <span class="notification-time">{{ notification.time }}</span>
          </div>
        </el-card>
        
        <el-card class="sidebar-card" style="margin-top: 20px;" shadow="always">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/query')" class="action-button">
              <i class="el-icon-search"></i>
              <span>不动产查询</span>
            </el-button>
            <el-button @click="$router.push('/dashboard')" class="action-button">
              <i class="el-icon-user"></i>
              <span>个人资料</span>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DashboardView',
  data() {
    return {
      notifications: [
        {
          id: 1,
          title: '系统维护通知',
          content: '系统将于2023年12月15日 00:00-02:00进行例行维护',
          time: '2023-12-01'
        },
        {
          id: 2,
          title: '新功能上线',
          content: '新增土地查询功能，支持更多类型的不动产信息查询',
          time: '2023-11-28'
        }
      ]
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  position: relative;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  overflow: hidden;
}

.dashboard-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: -1;
}

.dashboard-row {
  width: 100%;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.welcome-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.welcome-content h2 {
  color: white;
  font-size: 32px;
  margin-bottom: 15px;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  margin-bottom: 30px;
  max-width: 600px;
}

.start-button {
  padding: 16px 32px;
  font-size: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.start-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.records-row {
  margin-top: 30px;
}

.main-card, .sidebar-card {
  border: none;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.main-card:hover, .sidebar-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
}

.card-button {
  padding: 0;
}

.records-placeholder {
  text-align: center;
  padding: 60px 20px;
}

.records-placeholder i {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 20px;
}

.records-placeholder p {
  font-size: 18px;
  color: #909399;
  margin-bottom: 30px;
}

.sidebar-card {
  margin-bottom: 20px;
}

.notification-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: #f8f9fa;
  padding-left: 10px;
}

.notification-item h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.notification-item p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  font-size: 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

@media (max-width: 992px) {
  .records-row {
    flex-direction: column;
  }
  
  .records-col, .sidebar-col {
    width: 100%;
  }
  
  .sidebar-col {
    margin-top: 20px;
  }
}
</style>