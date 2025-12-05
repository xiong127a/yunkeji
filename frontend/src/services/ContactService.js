import apiClient from './api'

const ContactService = {
  /**
   * 获取客服联系方式
   */
  async getContactInfo() {
    try {
      const response = await apiClient.get('/contact/info')
      console.log('客服信息获取成功:', response)
      return response
    } catch (error) {
      console.error('获取客服信息失败:', error)
      console.error('错误详情:', error.response?.data || error.message)
      // 返回默认值，避免前端报错
      return {
        wechat: '',
        phone: '',
        workHours: '周一至周五 9:00-18:00'
      }
    }
  }
}

export default ContactService

