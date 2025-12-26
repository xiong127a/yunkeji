import apiClient from './api'

class RealEstateService {
  // 提交大数据查询请求
  async submitQuery(queryData) {
    const response = await apiClient.post('/user/real-estate/query', queryData)
    return response
  }

  // 提交大数据查询请求（带文件）
  async submitQueryWithFiles(queryData, files) {
    const formData = new FormData()
    formData.append('request', new Blob([JSON.stringify(queryData)], { type: 'application/json' }))

    if (files && files.length > 0) {
      files.forEach((file) => {
        formData.append('files', file)
      })
    }

    const response = await apiClient.post('/user/real-estate/query-with-files', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response
  }

  // 获取用户的查询记录
  async getQueryRecords() {
    const response = await apiClient.get('/user/real-estate/records')
    return response
  }

  // 获取查询记录详情
  async getQueryRecordDetail(recordId) {
    const response = await apiClient.get(`/user/real-estate/records/${recordId}`)
    return response
  }

  // 获取查询结果
  async getQueryResult(requestNo) {
    const response = await apiClient.get(`/user/real-estate/result/${requestNo}`)
    return response
  }

  // 刷新查询结果
  async refreshQueryResult(recordId) {
    const response = await apiClient.post(`/user/real-estate/records/${recordId}/refresh`)
    return response
  }

  // 删除查询记录
  async deleteRecord(recordId) {
    await apiClient.delete(`/user/real-estate/records/${recordId}`)
  }
}

export default new RealEstateService()

