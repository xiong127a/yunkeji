<template>
  <div class="query-container">
    <div class="query-background"></div>
    <div class="query-inner">
      <el-row :gutter="20" class="query-row" justify="center">
        <el-col :xs="24" :sm="22" :md="16" :lg="14" :xl="12" class="query-col">
        <el-card class="query-card" shadow="always">
          <template #header>
            <div class="card-header">
              <span>大数据查询</span>
              <el-tag type="success">在线办理</el-tag>
            </div>
          </template>
          
          <div class="query-description">
            <p>请填写以下信息进行大数据查询，文件上传为可选项目</p>
          </div>
          
          <el-form :model="queryForm" :rules="rules" ref="formRef" label-width="120px" class="query-form">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="queryForm.name" placeholder="请输入姓名" size="large" clearable />
            </el-form-item>
            
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="queryForm.idCard" placeholder="请输入身份证号" size="large" clearable />
            </el-form-item>
            
            <el-form-item label="上传文件">
              <el-upload
                class="upload-demo"
                action="/api/upload"
                :multiple="true"
                :limit="3"
                v-model:file-list="fileList"
                drag
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
        
        <el-col :xs="24" :sm="22" :md="8" :lg="8" :xl="8" class="sidebar-col">
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
              <li>查询结果具有法律效力，请认真核对</li>
            </ul>
          </div>
        </el-card>
        
        <el-card class="records-card" style="margin-top: 20px;" shadow="always" v-if="isAuthenticated">
          <template #header>
            <div class="card-header">
              <span>查询记录</span>
              <el-button class="card-button" type="text">查看更多</el-button>
            </div>
          </template>
          
          <div class="records-list">
            <div class="record-item" v-for="record in recentRecords" :key="record.id">
              <div class="record-info">
                <div class="record-title">{{ record.type }}</div>
                <div class="record-time">{{ record.time }}</div>
              </div>
              <el-tag :type="record.statusType">{{ record.status }}</el-tag>
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
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import RealEstateService from '@/services/RealEstateService'
import AuthService from '@/services/AuthService'

export default {
  name: 'QueryView',
  setup() {
    const router = useRouter()
    const formRef = ref(null)
    const loading = ref(false)
    const recordsLoading = ref(false)
    
    // 使用计算属性动态检查用户是否已认证
    const isAuthenticated = computed(() => AuthService.isAuthenticated());
    
    const queryForm = reactive({
      name: '',
      idCard: ''
    })
    
    const fileList = ref([])
    
    const rules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      idCard: [
        { required: true, message: '请输入身份证号', trigger: 'blur' },
        { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
      ]
    }
    
    const recentRecords = ref([
      {
        id: 1,
        type: '大数据查询',
        time: '2023-11-28',
        status: '已完成',
        statusType: 'success'
      },
      {
        id: 2,
        type: '大数据查询',
        time: '2023-11-25',
        status: '处理中',
        statusType: 'warning'
      },
      {
        id: 3,
        type: '大数据查询',
        time: '2023-11-20',
        status: '已提交',
        statusType: 'info'
      }
    ])
    
    const goToLogin = () => {
      router.push('/login');
    };
    
    const submitQuery = () => {
      // 提交查询前检查用户是否已登录
      if (!isAuthenticated.value) {
        ElMessage.warning('请先登录后再提交查询');
        router.push('/login');
        return;
      }
      
      formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            // 处理文件上传
            let files = []
            if (fileList.value.length > 0) {
              // 如果有文件，则使用带文件的接口
              files = fileList.value.map(file => file.raw)
              const response = await RealEstateService.submitQueryWithFiles({
                name: queryForm.name,
                idCard: queryForm.idCard
              }, files)
              
              console.log('提交查询成功:', response)
              ElMessage.success('查询请求已提交')
            } else {
              // 没有文件则使用普通接口
              const response = await RealEstateService.submitQuery({
                name: queryForm.name,
                idCard: queryForm.idCard
              })
              
              console.log('提交查询成功:', response)
              ElMessage.success('查询请求已提交')
            }
            
            // 重新加载查询记录
            loadQueryRecords()
            // 重置表单
            resetForm()
          } catch (error) {
            console.error('提交查询失败:', error)
            ElMessage.error('提交查询失败: ' + (error.message || '未知错误'))
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    const resetForm = () => {
      formRef.value.resetFields()
      fileList.value = []
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
        // queryRecords.value = records
      } catch (error) {
        console.error('获取查询记录失败:', error)
        ElMessage.error('获取查询记录失败: ' + (error.message || '未知错误'))
      } finally {
        recordsLoading.value = false
      }
    }
    
    // 组件挂载时加载查询记录
    onMounted(() => {
      loadQueryRecords()
    })
    
    return {
      queryForm,
      rules,
      formRef,
      loading,
      recordsLoading,
      fileList,
      recentRecords,
      submitQuery,
      resetForm,
      viewDetail,
      isAuthenticated,
      goToLogin
    }
  }
}
</script>

<style scoped>
.query-container {
  position: relative;
  min-height: 100vh;
  padding: 40px 20px;
  background: #f4f6fb;
}

.query-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 10px;
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

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item:hover {
  background-color: #f8f9fa;
  padding-left: 10px;
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