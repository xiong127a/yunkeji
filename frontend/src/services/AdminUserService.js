import apiClient from './api'

class AdminUserService {
  async getAllUsers () {
    return apiClient.get('/admin/users')
  }

  async updateUserPrice (userId, queryPrice) {
    return apiClient.put(`/admin/users/${userId}/price`, { queryPrice })
  }

  async rechargeUser (userId, amount, remark) {
    return apiClient.put(`/admin/users/${userId}/recharge`, { amount, remark })
  }
}

export default new AdminUserService()

