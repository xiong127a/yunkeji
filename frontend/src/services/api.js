import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '/yunkeji/api', // 后端配置了context-path: /yunkeji，所以API路径需要加上前缀
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么，比如添加token
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    return response.data
  },
  (error) => {
    // 对响应错误做点什么
    if (error.response && error.response.status === 401) {
      // 处理未授权错误，比如清除token并重定向到登录页
      localStorage.removeItem('authToken')
      // 只有在当前不在登录页面时才重定向
      if (router.currentRoute.value.path !== '/login') {
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default apiClient