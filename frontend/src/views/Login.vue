<template>
  <div class="login-container">
    <div class="login-background"></div>
    <div class="login-wrapper">
          <div class="login-left">
            <div class="login-illustration">
              <img src="../assets/yunkeji-logo.jpg" alt="云科技logo" style="max-width: 100%; height: auto; border-radius: 10px; display: block; margin: 0 auto;" />
            </div>
            <h2>云科技不动产查询系统</h2>
            <p>便捷、安全、高效的不动产信息查询平台</p>
          </div>
          
          <div class="login-right">
            <el-card class="login-card" shadow="always">
              <div class="login-header">
                <h2>用户登录</h2>
                <p>欢迎回来，请输入您的账号信息</p>
              </div>
              
              <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
                <el-form-item prop="username">
                  <el-input 
                    v-model="loginForm.username" 
                    placeholder="请输入用户名" 
                    autocomplete="off" 
                    class="login-input"
                    prefix-icon="User"
                    size="large"
                    clearable
                  />
                </el-form-item>
                
                <el-form-item prop="password">
                  <el-input 
                    v-model="loginForm.password" 
                    type="password" 
                    placeholder="请输入密码" 
                    autocomplete="off" 
                    class="login-input"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item>
                  <div class="login-options">
                    <el-checkbox v-model="rememberMe" class="remember-me">记住我</el-checkbox>
                    <el-link type="primary" :underline="false" class="forgot-password-link">忘记密码？</el-link>
                  </div>
                </el-form-item>
                
                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="handleLogin" 
                    :loading="loading" 
                    class="login-button"
                    size="large"
                    round
                  >
                    登录
                  </el-button>
                </el-form-item>
              </el-form>
              
              <div class="login-footer">
                <el-link @click="$router.push('/register')" class="register-link">
                  还没有账号？立即注册
                </el-link>
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
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const loading = ref(false)
    const rememberMe = ref(false)
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ]
    }
    
    const handleLogin = () => {
      loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            console.log('正在尝试登录，请求数据:', {
              username: loginForm.username,
              password: loginForm.password
            });
            
            // 调用登录API
            const response = await AuthService.login({
              username: loginForm.username,
              password: loginForm.password
            })
            
            console.log('登录响应:', response)
            
            // 检查登录是否成功
            if (response.success) {
              console.log('登录成功:', response)
              console.log('Token:', response.token ? response.token.substring(0, 20) + '...' : 'null')
              
              // 等待一下确保 token 已经保存到 localStorage
              await new Promise(resolve => setTimeout(resolve, 50))
              
              // 验证 token 是否已保存
              const savedToken = localStorage.getItem('authToken')
              console.log('保存后的Token:', savedToken ? savedToken.substring(0, 20) + '...' : 'null')
              
              ElMessage.success('登录成功')
              
              // 触发自定义事件，通知其他组件登录状态已变化
              window.dispatchEvent(new Event('auth-status-changed'))
              
              // 登录成功后跳转到dashboard
              router.push('/dashboard')
            } else {
              // 登录失败，显示错误消息
              console.log('登录失败:', response.message)
              ElMessage.error(response.message || '登录失败')
            }
          } catch (error) {
            console.error('登录失败:', error)
            console.error('错误详情:', {
              message: error.message,
              response: error.response,
              status: error.response ? error.response.status : 'N/A'
            });
            ElMessage.error('登录失败: ' + (error.message || '未知错误'))
          } finally {
            loading.value = false
          }
        } else {
          console.log('表单验证失败');
        }
      })
    }
    
    return {
      loginForm,
      rules,
      loginFormRef,
      loading,
      rememberMe,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.login-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 1200px;
  gap: 80px;
  margin: 0 auto;
}

.login-left {
  flex: 1;
  text-align: center;
  color: white;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 300px;
}

.login-left h2 {
  font-size: 36px;
  margin-bottom: 20px;
  margin-top: 0;
  color: #2c3e50;
  font-weight: 700;
}

.login-left p {
  font-size: 18px;
  color: #7f8c8d;
  margin: 0;
}

.login-illustration {
  margin-bottom: 30px;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-illustration img {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  border-radius: 10px;
  object-fit: contain;
}

.login-right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 400px;
}

.login-card {
  width: 100%;
  max-width: 450px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: none;
  margin-top: 0;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  padding-top: 0;
}

.login-header h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 16px;
  color: #7f8c8d;
}

.login-form {
  padding: 20px 30px;
}

.login-input {
  border-radius: 10px !important;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  flex-wrap: nowrap;
}

.remember-me {
  color: #606266;
  white-space: nowrap;
  margin: 0;
}

.forgot-password-link {
  white-space: nowrap;
  margin-left: auto;
}

.login-button {
  width: 100%;
  padding: 16px;
  font-size: 18px;
  border-radius: 10px;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #eee;
}

.register-link {
  font-size: 16px;
  color: #409eff !important;
}

@media (max-width: 992px) {
  .login-container {
    min-height: calc(100vh - 60px);
    padding: 20px;
  }
  
  .login-wrapper {
    flex-direction: column;
    gap: 40px;
    padding: 40px 20px;
    align-items: center;
  }
  
  .login-left {
    width: 100%;
  }
  
  .login-illustration {
    width: 150px;
    height: 150px;
  }
  
  .login-right {
    width: 100%;
  }
  
  .login-card {
    max-width: 100%;
  }
}
</style>