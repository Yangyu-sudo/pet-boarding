import request from '@/utils/request'

export function getDailyRecords(orderId) {
  return request({ url: `/records/order/${orderId}`, method: 'get' })
}

export function addDailyRecord(data) {
  return request({ url: '/records', method: 'post', data })
}

export function updateDailyRecord(id, data) {
  return request({ url: `/records/${id}`, method: 'put', data })
}
