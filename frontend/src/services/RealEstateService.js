import apiClient from './api'

class RealEstateService {
  // 提交不动产查询请求
  async submitQuery(queryData) {
    try {
      const response = await apiClient.post('/user/real-estate/query', queryData)
      return response
    } catch (error) {
      throw error
    }
  }

  // 提交不动产查询请求（带文件）
  async submitQueryWithFiles(queryData, files) {
    try {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(queryData)], {
        type: 'application/json'
      }));
      
      if (files && files.length > 0) {
        files.forEach((file, index) => {
          formData.append('files', file);
        });
      }
      
      const response = await apiClient.post('/user/real-estate/query-with-files', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      return response;
    } catch (error) {
      throw error;
    }
  }

  // 获取用户的查询记录
  async getQueryRecords() {
    try {
      const response = await apiClient.get('/user/real-estate/records')
      return response
    } catch (error) {
      throw error
    }
  }

  // 获取查询记录详情
  async getQueryRecordDetail(recordId) {
    try {
      const response = await apiClient.get(`/user/real-estate/records/${recordId}`)
      return response
    } catch (error) {
      throw error
    }
  }

  // 获取查询结果
  async getQueryResult(requestNo) {
    try {
      const response = await apiClient.get(`/user/real-estate/result/${requestNo}`)
      return response
    } catch (error) {
      throw error
    }
  }
}

export default new RealEstateService()