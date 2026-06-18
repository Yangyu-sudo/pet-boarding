import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 文件下载返回完整response
    if (response.config.responseType === 'blob') {
      return response
    }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 401: token过期或无效
      if (res.code === 401) {
        removeToken()
        router.push({ name: 'Login' })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      removeToken()
      router.push({ name: 'Login' })
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
