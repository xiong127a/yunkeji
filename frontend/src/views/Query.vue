<template>
  <div class="query-container">
    <div class="query-background"></div>
    <el-row :gutter="20" class="query-row">
      <el-col :span="16" class="query-col">
        <el-card class="query-card" shadow="always">
          <template #header>
            <div class="card-header">
              <span>不动产查询</span>
              <el-tag type="success">在线办理</el-tag>
            </div>
          </template>
          
          <div class="query-description">
            <p>请填写以下信息进行不动产查询，文件上传为可选项目</p>
            <div v-if="isAuthenticated" class="pay-mode-row">
              <span class="pay-mode-label">支付方式：</span>
              <el-radio-group v-model="payMode" size="large">
                <el-radio-button label="STORED_VALUE">余额扣费</el-radio-button>
                <el-radio-button label="DIRECT_PAY">扫码支付</el-radio-button>
              </el-radio-group>
              <span class="pay-mode-tip" v-if="payMode === 'STORED_VALUE'">
                当前单价：{{ formatCurrency(accountInfo.queryPrice) }}，先充值后自动扣费
              </span>
            </div>
            <div v-if="isAuthenticated && payMode === 'DIRECT_PAY'" class="pay-channel-row">
              <span class="pay-mode-label">支付渠道：</span>
              <el-radio-group v-model="payChannel" size="large">
                <el-radio-button label="WECHAT">微信支付</el-radio-button>
                <el-radio-button label="ALIPAY">支付宝</el-radio-button>
              </el-radio-group>
              <span class="pay-mode-tip">
                当前单价：{{ formatCurrency(accountInfo.queryPrice) }}，提交后将生成 {{ payChannel === 'ALIPAY' ? '支付宝' : '微信' }} 扫码二维码
              </span>
            </div>
          </div>
          
          <el-form :model="queryForm" :rules="rules" ref="formRef" label-width="120px" class="query-form">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="queryForm.name" placeholder="请输入姓名" size="large" clearable />
            </el-form-item>
            
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="queryForm.idCard" placeholder="请输入身份证号" size="large" clearable />
            </el-form-item>
            
            <el-form-item label="授权书">
              <div class="authorization-section">
                <div class="authorization-download">
                  <el-button type="primary" @click="downloadAuthorization" size="default">
                    <span style="margin-right: 5px;">⬇</span>下载授权书
                  </el-button>
                  <span class="authorization-tip">请下载授权书，打印后签名并拍照上传</span>
                </div>
                <el-upload
                  class="authorization-upload"
                  :auto-upload="false"
                  :limit="1"
                  :on-change="handleAuthorizationChange"
                  :file-list="authorizationFileList"
                  accept="image/*,.pdf"
                >
                  <el-button type="success" size="default">
                    <span style="margin-right: 5px;">⬆</span>上传已签名的授权书
                  </el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      支持jpg/png/pdf文件，单个文件不超过5MB（必填）
                    </div>
                  </template>
                </el-upload>
              </div>
            </el-form-item>
            
            <el-form-item label="其他文件（可选）">
              <el-upload
                class="upload-demo"
                :auto-upload="false"
                :multiple="true"
                :limit="3"
                :on-change="handleFileChange"
                :file-list="fileList"
                drag
                accept="image/*,.pdf"
              >
                <el-icon class="el-icon--upload"><i class="el-icon-upload"></i></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    支持jpg/png/pdf文件，单个文件不超过5MB，最多可上传3个文件（非必填）
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            
            <el-form-item class="form-buttons">
              <el-button type="primary" @click="submitQuery" :loading="loading" class="submit-button" size="large">
                提交查询
              </el-button>
              <el-button @click="resetForm" size="large" class="reset-button">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="8" class="sidebar-col">
        <el-card class="info-card" shadow="always">
          <template #header>
            <div class="card-header">
              <span>查询说明</span>
            </div>
          </template>
          
          <div class="info-content">
            <h4>查询须知</h4>
            <ul>
              <li>请确保填写的姓名和身份证号准确无误</li>
              <li>查询结果将在1-3个工作日内返回</li>
              <li>如有相关证明文件可上传以加快查询进度</li>
              <li>查询结果仅限本人使用，不得传播</li>
            </ul>
            
            <h4 style="margin-top: 20px;">注意事项</h4>
            <ul>
              <li>系统会对您的个人信息严格保密</li>
              <li>查询过程中如有问题请联系客服</li>
            </ul>

            <div v-if="isAuthenticated" class="billing-info">
              <h4 style="margin-top: 20px;">计费信息</h4>
              <p>当前每次查询费用：<strong>{{ formatCurrency(accountInfo.queryPrice) }}</strong></p>
              <p>当前账户余额：<strong>{{ formatCurrency(accountInfo.balance) }}</strong></p>
            </div>
          </div>
        </el-card>
        
        <el-card class="sidebar-card pending-card" style="margin-top: 20px;" shadow="always" v-if="isAuthenticated">
          <template #header>
            <div class="card-header">
              <span>待支付订单</span>
            </div>
          </template>
          <div v-if="pendingLoading" class="pending-empty">加载中...</div>
          <div v-else-if="pendingOrders.length === 0" class="pending-empty">暂无待支付订单</div>
          <div v-else class="pending-list">
            <div class="pending-item" v-for="order in pendingOrders" :key="order.id">
              <div class="pending-info">
                <div class="pending-title">{{ order.recordName || '查询记录' }}</div>
                <div class="pending-sub">订单号：{{ order.orderNo }}</div>
                <div class="pending-sub">金额：{{ formatCurrency(order.amount) }}</div>
                <div class="pending-sub">渠道：{{ order.payChannel === 'ALIPAY' ? '支付宝' : '微信' }}</div>
              </div>
              <div class="pending-actions">
                <el-button size="small" @click="regeneratePayOrder(order)">重新获取二维码</el-button>
                <el-button 
                  size="small" 
                  type="danger"
                  :loading="deletingOrderId === order.id"
                  @click="deletePendingOrder(order)"
                >删除</el-button>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="records-card" style="margin-top: 20px;" shadow="always" v-if="checkAuth">
          <template #header>
            <div class="card-header">
              <span>查询记录</span>
              <div style="display: flex; gap: 10px;">
                <el-button class="card-button" type="text" @click="loadQueryRecords" :loading="recordsLoading">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
                <el-button class="card-button" type="text" @click="viewAllRecords">查看更多</el-button>
              </div>
            </div>
          </template>
          
          <div v-if="recordsLoading" class="records-loading">加载中...</div>
          <div v-else-if="queryRecords.length === 0" class="records-empty">暂无查询记录</div>
          <div v-else class="records-list">
            <div class="record-item" v-for="record in queryRecords.slice(0, 5)" :key="record.id" @click="viewDetail(record.id)">
              <div class="record-info">
                <div class="record-title">{{ record.name || '不动产查询' }}</div>
                <div class="record-time">{{ formatTime(record.createdAt) }}</div>
              </div>
              <div class="record-actions">
                <el-tag :type="getStatusType(record.status)">{{ getStatusText(record.status) }}</el-tag>
                <el-button 
                  v-if="record.status === 'PENDING_PAY'"
                  type="text"
                  size="small"
                  :loading="deletingRecordId === record.id"
                  @click.stop="deleteRecord(record.id)"
                >删除</el-button>
                <el-button 
                  v-if="canRefreshRecord(record.status)" 
                  type="text" 
                  size="small" 
                  @click.stop="refreshRecord(record.id)"
                  :loading="refreshingRecordId === record.id"
                >
                  刷新
                </el-button>
                <el-button type="text" size="small" @click.stop="viewDetail(record.id)">查看</el-button>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="login-prompt-card" style="margin-top: 20px;" shadow="always" v-else>
          <template #header>
            <div class="card-header">
              <span>查询记录</span>
            </div>
          </template>
          
          <div class="login-prompt">
            <p>登录后可以查看您的查询记录</p>
            <el-button type="primary" @click="goToLogin">立即登录</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog
      v-model="qrDialogVisible"
      width="380px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
      title="扫码支付"
    >
      <div class="qr-dialog-content" v-if="qrContent">
        <p class="qr-text">订单号：{{ currentOrder.orderNo }}</p>
        <p class="qr-text">金额：{{ formatCurrency(currentOrder.amount) }}</p>
        <qrcode-vue :value="qrContent" :size="220" level="H" />
        <p class="qr-tip">
          请使用{{ currentOrder.payChannel === 'ALIPAY' ? '支付宝' : '微信' }}扫码完成支付
        </p>
      </div>
      <template #footer>
        <el-button @click="qrDialogVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onActivated, watch, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import RealEstateService from '@/services/RealEstateService'
import AuthService from '@/services/AuthService'
import UserService from '@/services/UserService'
import PayOrderService from '@/services/PayOrderService'
import QrcodeVue from 'qrcode.vue'
import apiClient from '@/services/api'

export default {
  name: 'QueryView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const formRef = ref(null)
    const loading = ref(false)
    // 初始加载时先显示加载态，避免闪现“暂无数据”
    const recordsLoading = ref(AuthService.isAuthenticated())
    
    // 使用ref来存储认证状态，立即检查当前状态
    const isAuthenticated = ref(AuthService.isAuthenticated())
    // 添加一个响应式触发器，用于强制更新
    const authCheckTrigger = ref(0)
    
    // 检查并更新认证状态的函数
    const checkAuthStatus = () => {
      const authStatus = AuthService.isAuthenticated()
      console.log('检查认证状态:', authStatus, 'Token:', localStorage.getItem('authToken'))
      if (authStatus !== isAuthenticated.value) {
        isAuthenticated.value = authStatus
        // 触发响应式更新
        authCheckTrigger.value++
      }
      return authStatus
    }
    
    // 使用 computed 来实时检查认证状态，依赖于 authCheckTrigger 来触发更新
    const checkAuth = computed(() => {
      // 读取触发器以确保响应式依赖
      const _ = authCheckTrigger.value
      const auth = AuthService.isAuthenticated()
      // 同步更新 ref 值
      if (auth !== isAuthenticated.value) {
        isAuthenticated.value = auth
      }
      return auth
    })
    
    const queryForm = reactive({
      name: '',
      idCard: ''
    })
    
    const fileList = ref([])
    const authorizationFileList = ref([])
    const accountInfo = reactive({
      balance: 0,
      queryPrice: 0
    })
    
    const rules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      idCard: [
        { required: true, message: '请输入身份证号', trigger: 'blur' },
        { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
      ]
    }
    
    const queryRecords = ref([])
    
    const payMode = ref('STORED_VALUE')
    const payChannel = ref('WECHAT')
    const qrDialogVisible = ref(false)
    const qrContent = ref('')
    const currentOrder = reactive({
      orderNo: '',
      amount: 0,
      payChannel: 'WECHAT'
    })
    const pendingOrders = ref([])
    const pendingLoading = ref(false)
    const refreshingRecordId = ref(null)
    const deletingRecordId = ref(null)
    const deletingOrderId = ref(null)
    
    const goToLogin = () => {
      router.push('/login');
    };
    
    // 下载授权书
    const downloadAuthorization = () => {
      const url = '/yunkeji/api/authorization/download'
      const link = document.createElement('a')
      link.href = url
      link.download = '云科技个人信息查询及使用授权书.pdf'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('授权书下载成功，请打印后签名并拍照上传')
    }
    
    // 处理授权书上传
    const handleAuthorizationChange = (file, fileList) => {
      authorizationFileList.value = fileList
    }
    
    // 处理其他文件上传
    const handleFileChange = (file, fileList) => {
      // 过滤掉授权书文件
      const filteredList = fileList.filter(f => {
        const isAuthFile = authorizationFileList.value.some(af => af.uid === f.uid)
        return !isAuthFile
      })
      fileList.value = filteredList
    }
    
    const extractErrorMessage = (error) => {
      if (!error) return '未知错误'
      const resp = error.response
      if (resp) {
        const data = resp.data
        if (typeof data === 'string' && data.trim().length > 0) {
          return data
        }
        if (data?.message) {
          return data.message
        }
        if (data?.error) {
          return data.error
        }
      }
      return error.message || '未知错误'
    }
    
    const submitQuery = () => {
      // 提交查询前检查用户是否已登录
      if (!isAuthenticated.value) {
        ElMessage.warning('请先登录后再提交查询');
        router.push('/login');
        return;
      }
      
      formRef.value.validate(async (valid) => {
        if (valid) {
          // 检查是否上传了授权书
          if (authorizationFileList.value.length === 0) {
            ElMessage.warning('请先下载授权书，签名后拍照上传')
            return
          }
          
          loading.value = true
          try {
            // 合并授权书和其他文件
            let files = []
            // 先添加授权书（必填）
            if (authorizationFileList.value.length > 0) {
              files.push(...authorizationFileList.value.map(file => file.raw))
            }
            // 再添加其他文件（可选）
            if (fileList.value.length > 0) {
              files.push(...fileList.value.map(file => file.raw))
            }
            
            const payload = {
              name: queryForm.name,
              idCard: queryForm.idCard
            }
            
            let response
            if (files.length > 0) {
              // 有文件则使用带文件的接口
              if (payMode.value === 'STORED_VALUE') {
                response = await RealEstateService.submitQueryWithFiles(payload, files)
                console.log('提交查询成功:', response)
                ElMessage.success('查询请求已提交')
              } else {
                response = await RealEstateService.submitQueryWithFilesDirectPay(payload, files, payChannel.value)
                handleDirectPayResponse(response)
              }
            } else {
              // 没有文件则使用普通接口（理论上不会到这里，因为授权书是必填的）
              if (payMode.value === 'STORED_VALUE') {
                response = await RealEstateService.submitQuery(payload)
                console.log('提交查询成功:', response)
                ElMessage.success('查询请求已提交')
              } else {
                response = await RealEstateService.submitQueryDirectPay(payload, payChannel.value)
                handleDirectPayResponse(response)
              }
            }
            
            // 重新加载查询记录与账户信息
            loadQueryRecords()
            loadProfile()
            // 重置表单
            resetForm()
          } catch (error) {
            console.error('提交查询失败:', error)
            ElMessage.error('提交查询失败: ' + extractErrorMessage(error))
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    const resetForm = () => {
      formRef.value.resetFields()
      fileList.value = []
      authorizationFileList.value = []
    }
    
    const viewDetail = (id) => {
      router.push(`/query/${id}`)
    }
    
    const loadQueryRecords = async () => {
      // 只有已认证用户才能加载查询记录
      if (!isAuthenticated.value) return;
      
      recordsLoading.value = true
      try {
        const records = await RealEstateService.getQueryRecords()
        queryRecords.value = Array.isArray(records) ? records : []
      } catch (error) {
        console.error('获取查询记录失败:', error)
        ElMessage.error('获取查询记录失败: ' + (error.message || '未知错误'))
      } finally {
        recordsLoading.value = false
      }
    }
    
    const viewAllRecords = () => {
      router.push('/dashboard')
    }
    
    const canRefreshRecord = (status) => {
      return status !== 'COMPLETED' && status !== 'REJECTED' && status !== 'FAILED'
    }
    
    const refreshRecord = async (recordId) => {
      refreshingRecordId.value = recordId
      try {
        const updated = await RealEstateService.refreshQueryResult(recordId)
        const index = queryRecords.value.findIndex(r => r.id === recordId)
        if (index !== -1) {
          queryRecords.value[index] = updated
        }
        ElMessage.success('刷新成功')
      } catch (error) {
        console.error('刷新查询结果失败:', error)
        ElMessage.error('刷新失败: ' + (error.message || '未知错误'))
      } finally {
        refreshingRecordId.value = null
      }
    }

    const deleteRecord = async (recordId) => {
      deletingRecordId.value = recordId
      try {
        await RealEstateService.deleteRecord(recordId)
        queryRecords.value = queryRecords.value.filter(r => r.id !== recordId)
        ElMessage.success('已删除待支付的查询记录')
      } catch (error) {
        console.error('删除查询记录失败:', error)
        ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'))
      } finally {
        deletingRecordId.value = null
      }
    }
    
    const getStatusType = (status) => {
      const statusMap = {
        'SUBMITTED': 'info',
        'PROCESSING': 'warning',
        'COMPLETED': 'success',
        'FAILED': 'danger',
        'REJECTED': 'danger',
        'PENDING_PAY': 'warning'
      }
      return statusMap[status] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        'SUBMITTED': '已提交',
        'PROCESSING': '处理中',
        'COMPLETED': '已完成',
        'FAILED': '失败',
        'REJECTED': '已拒绝',
        'PENDING_PAY': '待支付'
      }
      return statusMap[status] || status
    }
    
    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
    
    const loadPendingOrders = async () => {
      if (!isAuthenticated.value) {
        pendingOrders.value = []
        return
      }
      pendingLoading.value = true
      try {
        const list = await PayOrderService.getPendingOrders()
        pendingOrders.value = Array.isArray(list) ? list : []
      } catch (error) {
        console.error('获取待支付订单失败:', error)
      } finally {
        pendingLoading.value = false
      }
    }
    
    const handleDirectPayResponse = (resp) => {
      if (!resp || !resp.orderNo || !resp.qrContent) {
        ElMessage.error('创建支付订单失败')
        return
      }
      currentOrder.orderNo = resp.orderNo
      currentOrder.amount = resp.amount
      currentOrder.payChannel = resp.payChannel || payChannel.value
      qrContent.value = resp.qrContent
      qrDialogVisible.value = true
      loadPendingOrders()
    }
    
    const regeneratePayOrder = async (order) => {
      try {
        const channel = payChannel.value || order.payChannel
        const resp = await PayOrderService.regenerateOrder(order.id, channel)
        handleDirectPayResponse(resp)
        await loadPendingOrders()
      } catch (error) {
        ElMessage.error('重新生成二维码失败: ' + extractErrorMessage(error))
      }
    }

    const deletePendingOrder = async (order) => {
      deletingOrderId.value = order.id
      try {
        await PayOrderService.deletePendingOrder(order.id)
        pendingOrders.value = pendingOrders.value.filter(o => o.id !== order.id)
        ElMessage.success('已删除待支付订单')
      } catch (error) {
        ElMessage.error('删除失败: ' + extractErrorMessage(error))
      } finally {
        deletingOrderId.value = null
      }
    }
    
    const loadProfile = async () => {
      if (!isAuthenticated.value) return
      try {
        const profile = await UserService.getProfile()
        accountInfo.balance = profile.balance ?? 0
        accountInfo.queryPrice = profile.queryPrice ?? 0
      } catch (error) {
        console.error('获取账户信息失败:', error)
      }
    }
    
    const formatCurrency = (value) => {
      if (value === null || value === undefined) return '¥0.00'
      const num = typeof value === 'number' ? value : Number(value)
      if (Number.isNaN(num)) return '¥0.00'
      return `¥${num.toFixed(2)}`
    }
    
    // 组件挂载时检查认证状态并加载查询记录
    onMounted(async () => {
      // 立即检查一次
      checkAuthStatus()
      // 如果已登录，先标记加载态，避免空白闪烁
      if (isAuthenticated.value) {
        recordsLoading.value = true
      }
      
      // 使用 nextTick 确保在 DOM 更新后检查
      await nextTick()
      // 延迟一点时间确保 token 已经保存（如果刚登录）
      setTimeout(() => {
        checkAuthStatus()
        loadQueryRecords()
        loadProfile()
        loadPendingOrders()
      }, 100)
      
      // 监听自定义事件，用于在当前窗口内通知登录状态变化
      const handleAuthChange = () => {
        checkAuthStatus()
        loadQueryRecords()
        loadProfile()
        loadPendingOrders()
      }
      window.addEventListener('auth-status-changed', handleAuthChange)
      
      // 定期检查认证状态（每500ms检查一次，持续5秒，用于处理登录后立即跳转的情况）
      let checkCount = 0
      const maxChecks = 10
      const checkInterval = setInterval(() => {
        checkAuthStatus()
        checkCount++
        if (checkCount >= maxChecks) {
          clearInterval(checkInterval)
        }
      }, 500)
    })
    
    // 当组件被激活时（从其他页面返回时）重新检查认证状态
    onActivated(async () => {
      await nextTick()
      setTimeout(() => {
        checkAuthStatus()
        loadQueryRecords()
        loadProfile()
        loadPendingOrders()
      }, 50)
    })
    
    // 监听路由变化，当路由变化时重新检查认证状态
    watch(() => route.path, async (newPath, oldPath) => {
      // 当进入 query 页面时立即检查
      if (newPath === '/query') {
        // 立即检查
        checkAuthStatus()
        await nextTick()
        // 再次检查确保状态正确
        setTimeout(() => {
          checkAuthStatus()
          loadQueryRecords()
          loadProfile()
          loadPendingOrders()
        }, 50)
      }
    }, { immediate: true })
    
    return {
      queryForm,
      rules,
      formRef,
      loading,
      recordsLoading,
      fileList,
      queryRecords,
      submitQuery,
      viewAllRecords,
      getStatusType,
      getStatusText,
      formatTime,
      resetForm,
      viewDetail,
      isAuthenticated,
      goToLogin,
      checkAuth,
      accountInfo,
      formatCurrency,
      payMode,
      payChannel,
      qrDialogVisible,
      qrContent,
      currentOrder,
      pendingOrders,
      pendingLoading,
      regeneratePayOrder,
      downloadAuthorization,
      authorizationFileList,
      handleAuthorizationChange,
      handleFileChange,
      Refresh,
      canRefreshRecord,
      refreshRecord,
      refreshingRecordId,
      deleteRecord,
      deletingRecordId,
      deletePendingOrder,
      deletingOrderId
    }
  }
}
</script>

<style scoped>
.query-container {
  position: relative;
  padding: 20px;
  max-width: 1400px; /* 与控制台一致 */
  margin: 0 auto;
  min-height: 100vh;
  overflow: hidden;
}

.query-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: -1;
}

.query-row {
  width: 100%;
  margin-top: 30px; /* 与控制台页面间距保持一致 */
}

.query-card, .info-card, .records-card, .login-prompt-card {
  border: none;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.query-card:hover, .info-card:hover, .records-card:hover, .login-prompt-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.query-description {
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(120deg, #e0f7fa 0%, #c8e6c9 100%);
  border-radius: 12px;
  border-left: 5px solid #4caf50;
}

.query-description p {
  margin: 0;
  color: #2e7d32;
  font-size: 16px;
  font-weight: 500;
}

.query-form {
  padding: 0 20px 20px;
}

.form-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.submit-button {
  padding: 14px 30px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.submit-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
}

.reset-button {
  padding: 14px 30px;
  font-size: 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.reset-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.info-content h4 {
  margin-top: 0;
  color: #2c3e50;
  font-weight: 600;
}

.info-content ul {
  padding-left: 20px;
}

.info-content li {
  margin-bottom: 10px;
  line-height: 1.6;
  color: #606266;
}

.billing-info p {
  margin: 4px 0;
}

.pay-mode-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pay-channel-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pay-mode-label {
  font-weight: 600;
  color: #2c3e50;
}

.pay-mode-tip {
  font-size: 13px;
  color: #606266;
}

.qr-dialog-content {
  text-align: center;
}

.qr-text {
  margin: 6px 0;
  font-size: 14px;
}

.qr-tip {
  margin-top: 12px;
  font-size: 14px;
  color: #606266;
}

.pending-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pending-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.pending-info {
  flex: 1;
}

.pending-title {
  font-weight: 600;
  color: #303133;
}

.pending-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.pending-empty {
  text-align: center;
  color: #909399;
  padding: 10px 0;
}

.authorization-section {
  width: 100%;
}

.authorization-download {
  margin-bottom: 15px;
  padding: 15px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.authorization-tip {
  color: #409eff;
  font-size: 14px;
  flex: 1;
}

.authorization-upload {
  margin-top: 10px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
  cursor: pointer;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item:hover {
  background-color: #f8f9fa;
  padding-left: 20px;
}

.record-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.records-loading, .records-empty {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 5px;
  font-weight: 500;
}

.record-time {
  font-size: 14px;
  color: #909399;
}

.login-prompt {
  text-align: center;
  padding: 20px 0;
}

.login-prompt p {
  margin-bottom: 20px;
  color: #606266;
  font-size: 16px;
}

:deep(.el-upload-dragger) {
  border-radius: 12px;
  transition: all 0.3s ease;
}

:deep(.el-upload-dragger:hover) {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

@media (max-width: 992px) {
  .query-row {
    flex-direction: column;
  }
  
  .query-col, .sidebar-col {
    width: 100%;
  }
  
  .sidebar-col {
    margin-top: 20px;
  }
  
  .form-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .submit-button, .reset-button {
    width: 100%;
    max-width: 250px;
  }
}
</style>