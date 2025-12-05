import apiClient from './api'

class UserService {
  async getProfile () {
    return apiClient.get('/user/profile')
  }

  async getMyQueryRecords () {
    return apiClient.get('/user/real-estate/records')
  }
}

export default new UserService()
















