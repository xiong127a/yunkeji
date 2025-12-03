<template>
  <div class="register-container">
    <div class="register-background"></div>
    <div class="register-wrapper">
      <div class="register-left">
        <div class="register-illustration">
          <img src="@/assets/yunkeji-logo.jpg" alt="云科技logo" />
        </div>
        <h2>加入云科技不动产查询系统</h2>
        <p>快速注册，享受便捷的不动产信息查询服务</p>
      </div>

      <div class="register-right">
        <el-card class="register-card" shadow="always">
          <div class="register-header">
            <h2>用户注册</h2>
            <p>欢迎加入，请完善您的信息</p>
          </div>

          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form" label-width="0">
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名" 
                class="register-input"
                size="large"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="email">
              <el-input 
                v-model="registerForm.email" 
                placeholder="请输入邮箱" 
                class="register-input"
                size="large"
                prefix-icon="Message"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码" 
                class="register-input"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码" 
                class="register-input"
                size="large"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-checkbox v-model="agreeTerms" class="agree-terms">
                我已阅读并同意
                <el-link type="primary" :underline="false">《用户协议》</el-link>
                和
                <el-link type="primary" :underline="false">《隐私政策》</el-link>
              </el-checkbox>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleRegister" 
                :loading="loading" 
                class="register-button"
                size="large"
                round
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="register-footer">
            <div class="footer-text">
              已有账号？
              <el-link @click="$router.push('/login')" class="login-link">
                立即登录
              </el-link>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AuthService from '@/services/AuthService'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const registerFormRef = ref(null)
    const loading = ref(false)
    const agreeTerms = ref(false)
    
    const registerForm = reactive({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    })
    
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    const validateAgreeTerms = (rule, value, callback) => {
      if (!agreeTerms.value) {
        callback(new Error('请阅读并同意用户协议和隐私政策'))
      } else {
        callback()
      }
    }
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }
    
    const handleRegister = () => {
      if (!agreeTerms.value) {
        ElMessage.error('请阅读并同意用户协议和隐私政策')
        return
      }
      
      registerFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            console.log('正在尝试注册，请求数据:', {
              username: registerForm.username,
              email: registerForm.email,
              password: registerForm.password
            });
            
            // 调用注册API
            const response = await AuthService.register({
              username: registerForm.username,
              email: registerForm.email,
              password: registerForm.password
            })
            
            console.log('注册响应:', response)
            
            // 检查注册是否成功
            if (response.success) {
              console.log('注册成功:', response)
              ElMessage.success('注册成功')
              // 注册成功后跳转到登录页
              router.push('/login')
            } else {
              // 注册失败，显示错误消息
              console.log('注册失败:', response.message)
              ElMessage.error(response.message || '注册失败')
            }
          } catch (error) {
            console.error('注册失败:', error)
            console.error('错误详情:', {
              message: error.message,
              response: error.response,
              status: error.response ? error.response.status : 'N/A'
            });
            ElMessage.error('注册失败: ' + (error.message || '未知错误'))
          } finally {
            loading.value = false
          }
        } else {
          console.log('表单验证失败');
        }
      })
    }
    
    return {
      registerForm,
      rules,
      registerFormRef,
      loading,
      agreeTerms,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  position: relative;
  width: 100%;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.register-background {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #f0f5ff 0%, #fef8ff 100%);
  z-index: 0;
}

.register-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 1200px;
  gap: 80px;
}

.register-left {
  flex: 1;
  text-align: center;
  color: #2c3e50;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.register-left h2 {
  font-size: 36px;
  margin: 30px 0 16px;
  font-weight: 700;
}

.register-left p {
  font-size: 18px;
  color: #7f8c8d;
  margin: 0;
}

.register-illustration {
  margin-bottom: 10px;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-illustration img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 12px;
  object-fit: contain;
}

.register-right {
  flex: 1;
  display: flex;
  justify-content: center;
}

.register-card {
  width: 100%;
  max-width: 470px;
  border-radius: 20px;
  box-shadow: 0 15px 40px rgba(99, 102, 241, 0.15);
  border: none;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 700;
}

.register-header p {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.register-form {
  padding: 24px 30px;
}

.register-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: box-shadow 0.2s ease;
}

.register-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3) inset;
}

.agree-terms {
  color: #606266;
}

.register-button {
  width: 100%;
  padding: 16px;
  font-size: 18px;
  border-radius: 12px;
  margin-top: 10px;
  background: linear-gradient(135deg, #409eff, #5c7cfa);
  border: none;
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.35);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 25px rgba(64, 158, 255, 0.45);
}

.register-button:active {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #f2f3f5;
}

.footer-text {
  color: #606266;
  font-size: 15px;
}

.login-link {
  margin-left: 4px;
  font-weight: 600;
  color: #409eff !important;
}

@media (max-width: 992px) {
  .register-container {
    min-height: calc(100vh - 40px);
    padding: 20px;
  }
  
  .register-wrapper {
    flex-direction: column;
    gap: 40px;
    padding: 20px 10px;
  }
  
  .register-right,
  .register-left {
    width: 100%;
  }
  
  .register-card {
    max-width: 100%;
  }
}
</style>