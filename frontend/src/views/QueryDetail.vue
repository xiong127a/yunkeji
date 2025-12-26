<template>
  <div class="query-detail-container">
    <div class="detail-background"></div>
    <el-card class="detail-card" shadow="always" v-loading="loading">
      <div class="detail-header">
        <h2>查询详情</h2>
        <div class="header-actions">
          <el-tag :type="getStatusType(record?.status)" class="status-tag">{{ getStatusText(record?.status) }}</el-tag>
          <el-button 
            v-if="canRefresh" 
            type="primary" 
            :loading="refreshing" 
            @click="refreshResult"
            size="default"
            style="margin-left: 10px;"
          >
            <el-icon><Refresh /></el-icon>
            刷新结果
          </el-button>
        </div>
      </div>
      
      <el-divider></el-divider>
      
      <div v-if="!record && !loading" class="empty-state">
        <el-empty description="查询记录不存在" />
      </div>
      
      <el-row v-else :gutter="20" class="detail-row">
        <el-col :span="16" class="main-col">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>基本信息</span>
              </div>
            </template>
            
            <el-descriptions :column="1" label-class-name="desc-label" content-class-name="desc-content">
              <el-descriptions-item label="查询编号">{{ record?.id || '-' }}</el-descriptions-item>
              <el-descriptions-item label="请求编号">{{ record?.requestNo || '-' }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ formatDateTime(record?.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(record?.updatedAt) }}</el-descriptions-item>
              <el-descriptions-item label="申请人">{{ record?.name || '-' }}</el-descriptions-item>
              <el-descriptions-item label="身份证号">{{ maskIdCard(record?.idCard) }}</el-descriptions-item>
              <el-descriptions-item label="查询费用">{{ formatCurrency(record?.queryFee) }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
          
          <el-card class="result-card" style="margin-top: 20px;" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>查询结果</span>
              </div>
            </template>
            <div class="result-content" v-if="record">
              <div v-if="record.status === 'COMPLETED' && record.result">
                <div v-if="parsedResult">
                  <p v-if="parsedResult.rejectReason" class="reject-reason">
                    审核不通过：{{ parsedResult.rejectReason }}
                  </p>
                  <div v-else class="report-container">
                    <!-- 报告头部 -->
                    <div class="report-header">
                      <h3 class="report-title">大数据查询报告</h3>
                      <div class="report-meta">
                        <span class="report-date">查询时间：{{ formatDateTime(record.updatedAt) }}</span>
                        <el-tag type="success" size="small">核查成功</el-tag>
                      </div>
                    </div>
                    
                    <!-- 查询人信息 -->
                    <div class="report-section">
                      <h4 class="section-title">
                        <el-icon><User /></el-icon>
                        查询人信息
                      </h4>
                      <div class="info-grid">
                        <div class="info-item">
                          <span class="info-label">姓名：</span>
                          <span class="info-value">{{ record.name }}</span>
                        </div>
                        <div class="info-item">
                          <span class="info-label">身份证号：</span>
                          <span class="info-value">{{ maskIdCard(record.idCard) }}</span>
                        </div>
                        <div class="info-item">
                          <span class="info-label">请求编号：</span>
                          <span class="info-value">{{ record.requestNo || '-' }}</span>
                        </div>
                        <div class="info-item">
                          <span class="info-label">审批状态：</span>
                          <span class="info-value">{{ getApprovalStatusText(parsedResult.approvalStatus) }}</span>
                        </div>
                        <div class="info-item" v-if="parsedResult.reqOrderNo">
                          <span class="info-label">请求订单号：</span>
                          <span class="info-value">{{ parsedResult.reqOrderNo }}</span>
                        </div>
                      </div>
                    </div>
                    
                    <!-- 查询结果 -->
                    <div v-if="parsedResult.authResults && parsedResult.authResults.length > 0" class="report-section">
                      <h4 class="section-title">
                        <el-icon><Document /></el-icon>
                        查询结果
                      </h4>
                      <div v-for="(authResult, index) in parsedResult.authResults" :key="index" class="auth-result-item">
                        <div class="auth-status">
                          <el-tag :type="getAuthStateType(authResult.authState)" size="large">
                            {{ authResult.authStateDesc || getAuthStateText(authResult.authState) }}
                          </el-tag>
                          <span class="auth-card-num">身份证：{{ maskIdCard(authResult.cardNum) }}</span>
                        </div>
                        <div class="info-grid auth-meta">
                          <div class="info-item">
                            <span class="info-label">是否重新核查：</span>
                            <span class="info-value">{{ authResult.isReAuth === 1 || authResult.isReAuth === '1' ? '是' : '否' }}</span>
                          </div>
                          <div class="info-item">
                            <span class="info-label">状态说明：</span>
                            <span class="info-value">{{ authResult.authStateDesc || '-' }}</span>
                          </div>
                        </div>
                        
                        <!-- 大数据列表 -->
                        <div v-if="authResult.resultList && authResult.resultList.length > 0" class="property-list">
                          <div v-for="(property, pIndex) in authResult.resultList" :key="pIndex" class="property-card">
                            <div class="property-header">
                              <h5 class="property-title">大数据 {{ pIndex + 1 }}</h5>
                            </div>
                            <div class="property-content">
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">大数据信息编号：</span>
                                  <span class="property-value">{{ property.certNo || '-' }}</span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">大数据信息单元号：</span>
                                  <span class="property-value">{{ property.unitNo || '-' }}</span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">坐落：</span>
                                  <span class="property-value highlight">{{ property.location || '-' }}</span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">产权类型：</span>
                                  <span class="property-value">{{ property.rightsType || '-' }}</span>
                                </div>
                                <div class="property-item">
                                  <span class="property-label">用途：</span>
                                  <span class="property-value">{{ property.useTo || '-' }}</span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">面积：</span>
                                  <span class="property-value highlight">{{ property.houseArea ? property.houseArea + ' 平方米' : '-' }}</span>
                                </div>
                                <div class="property-item">
                                  <span class="property-label">共有情况：</span>
                                  <span class="property-value">{{ property.ownership || '-' }}</span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">使用期限：</span>
                                  <span class="property-value">
                                    {{ property.rightsStartTime || '-' }} 至 {{ property.rightsEndTime || '-' }}
                                  </span>
                                </div>
                              </div>
                              <div class="property-row">
                                <div class="property-item">
                                  <span class="property-label">是否查封：</span>
                                  <el-tag :type="property.isSealUp === '是' ? 'danger' : 'success'" size="small">
                                    {{ property.isSealUp || '否' }}
                                  </el-tag>
                                </div>
                                <div class="property-item">
                                  <span class="property-label">是否抵押：</span>
                                  <el-tag :type="property.isMortgaged === '是' ? 'warning' : 'success'" size="small">
                                    {{ property.isMortgaged || '否' }}
                                  </el-tag>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div v-else class="no-property">
                          <p>暂无大数据信息</p>
                        </div>
                      </div>
                    </div>
                    
                    <!-- 若没有 authResults，展示原始结果 -->
                    <div v-else class="report-section">
                      <h4 class="section-title">
                        <el-icon><Document /></el-icon>
                        查询结果
                      </h4>
                      <pre class="result-json">{{ formatResult(parsedResult) }}</pre>
                    </div>
                    
                    <!-- 报告底部 -->
                    <div class="report-footer">
                      <p class="footer-note">本报告由系统自动生成，仅供参考</p>
                    </div>
                  </div>
                </div>
                <div v-else>
                  <p>查询已完成，结果已保存</p>
                </div>
              </div>
              <div v-else-if="record.status === 'PROCESSING' || record.status === 'SUBMITTED'">
                <p class="processing-text">查询正在处理中，请稍候...</p>
                <p class="processing-tip">您可以点击右上角的"刷新结果"按钮主动获取最新状态</p>
              </div>
              <div v-else-if="record.status === 'FAILED'">
                <p class="error-text">查询失败，请稍后重试或联系客服</p>
              </div>
              <div v-else-if="record.status === 'REJECTED'">
                <p class="error-text">查询请求已被拒绝</p>
                <p v-if="parsedResult?.rejectReason" class="reject-reason">
                  拒绝原因：{{ parsedResult.rejectReason }}
                </p>
              </div>
              <div v-else>
                <p>暂无结果</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8" class="sidebar-col">
          <el-card class="files-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>相关文件</span>
              </div>
            </template>
            
            <div v-if="filesLoading" class="files-loading">加载中...</div>
            <div v-else-if="files.length === 0" class="files-empty">暂无文件</div>
            <div v-else class="file-list">
              <div class="file-item" v-for="file in files" :key="file.id">
                <div class="file-info">
                  <i class="el-icon-document file-icon"></i>
                  <div class="file-details">
                    <div class="file-name">{{ file.fileName }}</div>
                    <div class="file-size">{{ formatFileSize(file.fileSize) }}</div>
                  </div>
                </div>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain 
                  class="download-button"
                  @click="downloadFile(file)"
                >
                  下载
                </el-button>
              </div>
            </div>
            
            <div class="actions">
              <el-button type="primary" @click="$router.back()" class="back-button">
                返回列表
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, User, Document } from '@element-plus/icons-vue'
import RealEstateService from '@/services/RealEstateService'

export default {
  name: 'QueryDetailView',
  components: {
    Refresh,
    User,
    Document
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const refreshing = ref(false)
    const filesLoading = ref(false)
    const record = ref(null)
    const files = ref([])
    
    const recordId = computed(() => {
      return route.params.id ? parseInt(route.params.id) : null
    })
    
    const canRefresh = computed(() => {
      if (!record.value) return false
      // 只要不是已完成或已拒绝的状态，都可以刷新
      const status = record.value.status
      return status !== 'COMPLETED' && status !== 'REJECTED' && status !== 'FAILED'
    })
    
    const parsedResult = computed(() => {
      if (!record.value?.result) return null
      try {
        return JSON.parse(record.value.result)
      } catch (e) {
        return null
      }
    })
    
    const loadRecordDetail = async () => {
      if (!recordId.value) {
        ElMessage.error('查询记录ID不存在')
        return
      }
      
      loading.value = true
      try {
        const detail = await RealEstateService.getQueryRecordDetail(recordId.value)
        record.value = detail
      } catch (error) {
        console.error('获取查询记录详情失败:', error)
        ElMessage.error('获取查询记录详情失败: ' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    const loadFiles = async () => {
      if (!recordId.value) return
      
      filesLoading.value = true
      try {
        const fileList = await RealEstateService.getQueryRecordFiles(recordId.value)
        files.value = Array.isArray(fileList) ? fileList : []
      } catch (error) {
        console.error('获取文件列表失败:', error)
      } finally {
        filesLoading.value = false
      }
    }
    
    const refreshResult = async () => {
      if (!recordId.value) return
      
      refreshing.value = true
      try {
        const updated = await RealEstateService.refreshQueryResult(recordId.value)
        record.value = updated
        ElMessage.success('刷新成功')
      } catch (error) {
        console.error('刷新查询结果失败:', error)
        ElMessage.error('刷新失败: ' + (error.message || '未知错误'))
      } finally {
        refreshing.value = false
      }
    }
    
    const downloadFile = (file) => {
      // 构建下载URL
      const url = `/yunkeji/api/user/real-estate/files/${file.id}/download`
      // 创建临时链接下载
      const link = document.createElement('a')
      link.href = url
      link.download = file.fileName || 'file'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
    
    const getStatusType = (status) => {
      const statusMap = {
        'SUBMITTED': 'info',
        'PROCESSING': 'warning',
        'COMPLETED': 'success',
        'FAILED': 'danger',
        'REJECTED': 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        'SUBMITTED': '已提交',
        'PROCESSING': '处理中',
        'COMPLETED': '已完成',
        'FAILED': '失败',
        'REJECTED': '已拒绝'
      }
      return statusMap[status] || status || '未知'
    }
    
    const formatDateTime = (timeStr) => {
      if (!timeStr) return '-'
      const date = new Date(timeStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
    
    const maskIdCard = (idCard) => {
      if (!idCard) return '-'
      if (idCard.length <= 8) return idCard
      return idCard.substring(0, 3) + '****' + idCard.substring(idCard.length - 4)
    }
    
    const formatCurrency = (value) => {
      if (value === null || value === undefined) return '¥0.00'
      const num = typeof value === 'number' ? value : Number(value)
      if (Number.isNaN(num)) return '¥0.00'
      return `¥${num.toFixed(2)}`
    }
    
    const formatFileSize = (bytes) => {
      if (!bytes) return '0 B'
      const kb = bytes / 1024
      if (kb < 1024) {
        return `${kb.toFixed(2)} KB`
      }
      const mb = kb / 1024
      return `${mb.toFixed(2)} MB`
    }
    
    const getAuthStateType = (authState) => {
      const stateMap = {
        '10': 'info',      // 审核中
        '20': 'warning',   // 核查中
        '30': 'success',   // 核查成功
        '40': 'danger'     // 核查异常
      }
      return stateMap[authState] || 'info'
    }
    
    const getAuthStateText = (authState) => {
      const stateMap = {
        '10': '审核中',
        '20': '核查中',
        '30': '核查成功',
        '40': '核查异常'
      }
      return stateMap[authState] || '未知状态'
    }

    const getApprovalStatusText = (status) => {
      const map = {
        'APPROVED': '审核通过',
        'UN_APPROVED': '审核不通过',
        'IN_APPROVAL': '待审核'
      }
      return map[status] || status || '-'
    }
    
    onMounted(async () => {
      await loadRecordDetail()
      await loadFiles()
    })
    
    return {
      loading,
      refreshing,
      filesLoading,
      record,
      files,
      canRefresh,
      parsedResult,
      refreshResult,
      downloadFile,
      getStatusType,
      getStatusText,
      formatDateTime,
      maskIdCard,
      formatCurrency,
      formatFileSize,
      getAuthStateType,
      getAuthStateText,
      getApprovalStatusText
    }
  }
}
</script>

<style scoped>
.query-detail-container {
  position: relative;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  overflow: hidden;
}

.detail-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: -1;
}

.detail-card {
  border: none;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.detail-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.status-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
}

.info-card, .result-card, .files-card {
  border: none;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.info-card:hover, .result-card:hover, .files-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  font-weight: 600;
  font-size: 18px;
  color: #2c3e50;
}

.desc-label {
  font-weight: 500;
  width: 100px;
  color: #606266;
}

.desc-content {
  color: #303133;
  font-weight: 500;
}

.result-content p {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
}

.processing-text {
  font-size: 16px;
  color: #e6a23c;
  font-weight: 500;
}

.processing-tip {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.error-text {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 500;
}

.reject-reason {
  font-size: 16px;
  color: #f56c6c;
  padding: 15px;
  background: #fef0f0;
  border-radius: 8px;
  border-left: 4px solid #f56c6c;
}

.result-json {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
}

/* 报告模板样式 */
.report-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.report-header {
  border-bottom: 2px solid #e4e7ed;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.report-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.report-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-date {
  color: #909399;
  font-size: 14px;
}

.report-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
  min-width: 100px;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.auth-result-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.auth-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.auth-card-num {
  color: #606266;
  font-size: 14px;
}

.property-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.property-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.property-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.property-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.property-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.property-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.property-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.property-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.property-label {
  font-weight: 500;
  color: #606266;
  min-width: 100px;
  flex-shrink: 0;
}

.property-value {
  color: #303133;
  flex: 1;
}

.property-value.highlight {
  color: #409eff;
  font-weight: 600;
  font-size: 15px;
}

.no-property {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.report-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  text-align: center;
}

.footer-note {
  color: #909399;
  font-size: 12px;
  margin: 0;
}

.files-loading, .files-empty {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

.result-details {
  margin-top: 20px;
  padding: 20px;
  background: linear-gradient(120deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 12px;
}

.result-details h4 {
  margin-top: 0;
  color: #1565c0;
  font-weight: 600;
}

.result-details ul {
  padding-left: 20px;
}

.result-details li {
  margin-bottom: 8px;
  line-height: 1.6;
  color: #1565c0;
}

.file-list {
  margin-bottom: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item:hover {
  background-color: #f8f9fa;
  padding-left: 10px;
}

.file-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.file-icon {
  font-size: 24px;
  color: #667eea;
  margin-right: 12px;
}

.file-details {
  flex: 1;
}

.file-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
  font-weight: 500;
}

.file-size {
  font-size: 14px;
  color: #909399;
}

.download-button {
  transition: all 0.3s ease;
}

.download-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.actions {
  text-align: center;
}

.back-button {
  padding: 14px 32px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.back-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
}

@media (max-width: 992px) {
  .detail-row {
    flex-direction: column;
  }
  
  .main-col, .sidebar-col {
    width: 100%;
  }
  
  .sidebar-col {
    margin-top: 20px;
  }
}
</style>