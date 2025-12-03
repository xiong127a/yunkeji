import apiClient from './api'

class AuthService {
  // 用户登录
  async login(credentials) {
    try {
      const response = await apiClient.post('/auth/login', credentials)
      // 保存token到localStorage（仅当登录成功时）
      // 注意：apiClient 的响应拦截器已经返回了 response.data，所以这里直接使用 response
      if (response.success && response.token) {
        localStorage.setItem('authToken', response.token)
        console.log('Token已保存到localStorage:', response.token.substring(0, 20) + '...')
      } else if (response.success && response.data && response.data.token) {
        // 兼容其他可能的响应格式
        localStorage.setItem('authToken', response.data.token)
        console.log('Token已保存到localStorage (data格式):', response.data.token.substring(0, 20) + '...')
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 用户注册
  async register(userData) {
    try {
      const response = await apiClient.post('/auth/register', userData)
      // 如果注册成功且返回了token，也保存到localStorage
      // 注意：apiClient 的响应拦截器已经返回了 response.data，所以这里直接使用 response
      if (response.success && response.token) {
        localStorage.setItem('authToken', response.token)
        console.log('Token已保存到localStorage:', response.token.substring(0, 20) + '...')
      } else if (response.success && response.data && response.data.token) {
        // 兼容其他可能的响应格式
        localStorage.setItem('authToken', response.data.token)
        console.log('Token已保存到localStorage (data格式):', response.data.token.substring(0, 20) + '...')
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 用户登出
  logout() {
    localStorage.removeItem('authToken')
  }

  // 检查用户是否已登录
  isAuthenticated() {
    const token = localStorage.getItem('authToken')
    return !!token
  }
}

export default new AuthService()