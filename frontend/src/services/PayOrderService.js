import apiClient from './api'

class PayOrderService {
  async getPendingOrders () {
    return apiClient.get('/user/pay/orders/pending')
  }

  async regenerateOrder (orderId, payChannel) {
    const channelParam = payChannel ? `?payChannel=${payChannel}` : ''
    return apiClient.post(`/user/pay/orders/${orderId}/regenerate${channelParam}`)
  }
}

export default new PayOrderService()


