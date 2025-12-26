<template>
  <div class="dashboard-container">
    <div class="dashboard-background"></div>
    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="24" class="welcome-col">
        <el-card class="welcome-card" shadow="always">
          <div class="welcome-content">
            <h2>欢迎使用云科技大数据查询系统</h2>
            <p>一站式大数据信息查询服务平台，为您提供便捷、安全、高效的服务体验</p>
            <el-button type="primary" @click="$router.push('/query')" class="start-button">
              开始查询 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="records-row">
      <el-col :span="18" class="records-col">
        <el-card class="main-card" shadow="always">
      <template #header>
        <div class="card-header">
          <span>我的查询记录</span>
          <div style="display: flex; gap: 10px;">
            <el-button class="card-button" type="text" @click="loadMyRecords" :loading="recordsLoading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
            <el-button class="card-button" type="text" @click="$router.push('/query')">查看更多</el-button>
          </div>
        </div>
      </template>
      <div v-if="isLoggedIn" class="records-table-wrap">
        <el-table
          :data="myRecords"
          size="small"
          border
          v-loading="recordsLoading"
          empty-text="暂无查询记录"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="idCard" label="证件号" min-width="180" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="时间" min-width="160" />
          <el-table-column label="本次费用(元)" width="130">
            <template #default="{ row }">
              {{ formatCurrency(row.queryFee) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
              <el-button 
                v-if="canRefreshRecord(row.status)" 
                type="warning" 
                size="small" 
                @click="refreshRecord(row.id)"
                :loading="refreshingRecordId === row.id"
              >
                刷新
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else class="records-placeholder">
        <i class="el-icon-document"></i>
        <p>登录后可以查看您的查询记录</p>
        <el-button
          type="primary"
          @click="$router.push('/login')"
        >
          立即登录
        </el-button>
      </div>
        </el-card>
      </el-col>
      
      <el-col :span="6" class="sidebar-col">
        <el-card class="sidebar-card" shadow="always" v-if="isLoggedIn">
          <template #header>
            <div class="card-header">
              <span>账户信息</span>
            </div>
          </template>
          <div v-if="accountLoading" class="account-info loading">
            <div class="account-balance">
              <div class="label">账户余额</div>
              <div class="value">加载中...</div>
            </div>
            <div class="account-price">
              <div class="label">每次查询单价</div>
              <div class="value">加载中...</div>
            </div>
          </div>
          <div v-else class="account-info">
            <div class="account-balance">
              <div class="label">账户余额</div>
              <div class="value">{{ formatCurrency(balance) }}</div>
            </div>
            <div class="account-price">
              <div class="label">每次查询单价</div>
              <div class="value">{{ formatCurrency(queryPrice) }}</div>
            </div>
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
              <span>大数据查询</span>
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
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import UserService from '@/services/UserService'
import AuthService from '@/services/AuthService'
import RealEstateService from '@/services/RealEstateService'

export default {
  name: 'DashboardView',
  components: {
    Refresh
  },
  data() {
    const authed = AuthService.isAuthenticated()
    return {
      isLoggedIn: authed,
      balance: 0,
      queryPrice: 0,
      myRecords: [],
      recordsLoading: authed, // 已登录时默认显示加载态，避免闪烁
      accountLoading: authed,
      refreshingRecordId: null,
      Refresh,
    }
  },
  created() {
    if (this.isLoggedIn) {
      this.loadProfile()
      this.loadMyRecords()
    }
  },
  methods: {
    async loadProfile() {
      try {
        const profile = await UserService.getProfile()
        this.balance = profile.balance ?? 0
        this.queryPrice = profile.queryPrice ?? 0
      } catch (e) {
        console.error('加载账户信息失败', e)
      } finally {
        this.accountLoading = false
      }
    },
    async loadMyRecords() {
      this.recordsLoading = true
      try {
        const records = await UserService.getMyQueryRecords()
        this.myRecords = Array.isArray(records) ? records : []
      } catch (e) {
        console.error('加载查询记录失败', e)
        ElMessage.error('加载查询记录失败')
      } finally {
        this.recordsLoading = false
      }
    },
    viewDetail(recordId) {
      this.$router.push(`/query/${recordId}`)
    },
    canRefreshRecord(status) {
      return status !== 'COMPLETED' && status !== 'REJECTED' && status !== 'FAILED'
    },
    async refreshRecord(recordId) {
      this.refreshingRecordId = recordId
      try {
        const updated = await RealEstateService.refreshQueryResult(recordId)
        // 更新列表中的记录
        const index = this.myRecords.findIndex(r => r.id === recordId)
        if (index !== -1) {
          this.myRecords[index] = updated
        }
        ElMessage.success('刷新成功')
      } catch (error) {
        console.error('刷新查询结果失败:', error)
        ElMessage.error('刷新失败: ' + (error.message || '未知错误'))
      } finally {
        this.refreshingRecordId = null
      }
    },
    formatCurrency(value) {
      if (value === null || value === undefined) return '¥0.00'
      const num = typeof value === 'number' ? value : Number(value)
      if (Number.isNaN(num)) return '¥0.00'
      return `¥${num.toFixed(2)}`
    },
    getStatusType(status) {
      const statusMap = {
        'SUBMITTED': 'info',
        'PROCESSING': 'warning',
        'COMPLETED': 'success',
        'FAILED': 'danger',
        'REJECTED': 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        'SUBMITTED': '已提交',
        'PROCESSING': '处理中',
        'COMPLETED': '已完成',
        'FAILED': '失败',
        'REJECTED': '已拒绝'
      }
      return statusMap[status] || status || '未知'
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

.records-table-wrap {
  min-height: 260px; /* 固定占位，避免加载后高度变化过大 */
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

.account-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.account-balance .value {
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
}

.account-price .value {
  font-size: 18px;
  font-weight: 600;
  color: #34495e;
}

.account-info .label {
  font-size: 13px;
  color: #909399;
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