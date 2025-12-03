import apiClient from './api'

class AuthService {
  // 用户登录
  async login(credentials) {
    try {
      const response = await apiClient.post('/auth/login', credentials)
      // 保存token到localStorage（仅当登录成功时）
      // 注意：apiClient 的响应拦截器已经返回了 response.data，所以这里直接使用 response
      this.persistAuthInfo(response)
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
      this.persistAuthInfo(response)
      return response
    } catch (error) {
      throw error
    }
  }

  // 用户登出
  logout() {
    localStorage.removeItem('authToken')
    localStorage.removeItem('currentUser')
    window.dispatchEvent(new Event('auth-status-changed'))
  }

  // 检查用户是否已登录
  isAuthenticated() {
    const token = localStorage.getItem('authToken')
    return !!token
  }

  getCurrentUser() {
    const raw = localStorage.getItem('currentUser')
    if (!raw) {
      return null
    }
    try {
      return JSON.parse(raw)
    } catch (error) {
      return null
    }
  }

  isAdmin() {
    const user = this.getCurrentUser()
    return user?.role === 'ADMIN'
  }

  persistAuthInfo(response) {
    const token = response?.token || response?.data?.token
    const user = response?.user || response?.data?.user
    if (response?.success && token) {
      localStorage.setItem('authToken', token)
      console.log('Token已保存到localStorage:', token.substring(0, 20) + '...')
    }
    if (response?.success && user) {
      localStorage.setItem('currentUser', JSON.stringify(user))
    }
    if (response?.success) {
      window.dispatchEvent(new Event('auth-status-changed'))
    }
  }
}

export default new AuthService()