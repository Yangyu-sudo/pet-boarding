import request from '@/utils/request'

export function getCageList(params) {
  return request({ url: '/cages', method: 'get', params })
}

export function getCageById(id) {
  return request({ url: `/cages/${id}`, method: 'get' })
}

export function addCage(data) {
  return request({ url: '/cages', method: 'post', data })
}

export function updateCage(id, data) {
  return request({ url: `/cages/${id}`, method: 'put', data })
}

export function deleteCage(id) {
  return request({ url: `/cages/${id}`, method: 'delete' })
}

export function updateCageStatus(id, status) {
  return request({ url: `/cages/${id}/status`, method: 'put', params: { status } })
}
