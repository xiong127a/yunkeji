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

  async createUser (payload) {
    return apiClient.post('/users', payload)
  }

  async transferToChild (childId, amount, remark) {
    return apiClient.post(`/user/subordinates/${childId}/transfer`, { amount, remark })
  }

  async updateUserStatus (userId, status) {
    return apiClient.post(`/admin/users/${userId}/status`, { status })
  }

  async updateUserTrust (userId, trusted) {
    return apiClient.post(`/admin/users/${userId}/trust`, { trusted })
  }
}

export default new AdminUserService()

