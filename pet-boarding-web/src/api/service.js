import request from '@/utils/request'

export function getServiceList(params) {
  return request({ url: '/services', method: 'get', params })
}

export function getServiceById(id) {
  return request({ url: `/services/${id}`, method: 'get' })
}

export function addService(data) {
  return request({ url: '/services', method: 'post', data })
}

export function updateService(id, data) {
  return request({ url: `/services/${id}`, method: 'put', data })
}

export function deleteService(id) {
  return request({ url: `/services/${id}`, method: 'delete' })
}
