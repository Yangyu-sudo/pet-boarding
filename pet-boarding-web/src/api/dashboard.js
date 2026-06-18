import request from '@/utils/request'

export function getDashboardStats() {
  return request({ url: '/dashboard/stats', method: 'get' })
}

export function getDashboardRevenue() {
  return request({ url: '/dashboard/revenue', method: 'get' })
}
