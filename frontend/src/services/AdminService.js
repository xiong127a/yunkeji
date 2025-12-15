import apiClient from './api'

class AdminService {
  async getAllQueryRecords () {
    return apiClient.get('/admin/real-estate/records')
  }

  async updateQueryFee (recordId, queryFee) {
    return apiClient.put(`/admin/real-estate/records/${recordId}/fee`, { queryFee })
  }
}

export default new AdminService()

















