import request from '@/utils/request'

export function getOrderList(params) {
  return request({ url: '/orders', method: 'get', params })
}

export function getOrderById(id) {
  return request({ url: `/orders/${id}`, method: 'get' })
}

export function createOrder(data) {
  return request({ url: '/orders', method: 'post', data })
}

export function confirmOrder(id) {
  return request({ url: `/orders/${id}/confirm`, method: 'put' })
}

export function checkInOrder(id, cageId) {
  return request({ url: `/orders/${id}/checkin`, method: 'put', params: { cageId } })
}

export function completeOrder(id) {
  return request({ url: `/orders/${id}/complete`, method: 'put' })
}

export function cancelOrder(id) {
  return request({ url: `/orders/${id}/cancel`, method: 'put' })
}
