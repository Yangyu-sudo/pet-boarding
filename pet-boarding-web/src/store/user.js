import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo, updateUserInfo, changePassword } from '@/api/auth'
import { setToken, getToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isStaff = computed(() => userInfo.value?.role === 'STAFF')
  const isCustomer = computed(() => userInfo.value?.role === 'CUSTOMER')

  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    setToken(res.data.token)
    return res
  }

  async function register(form) {
    const res = await registerApi(form)
    return res
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
    } catch {
      logout()
    }
  }

  async function updateProfile(data) {
    const res = await updateUserInfo(data)
    userInfo.value = { ...userInfo.value, ...data }
    return res
  }

  async function doChangePassword(data) {
    return await changePassword(data)
  }

  function restoreLogin() {
    if (token.value) {
      fetchUserInfo()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    isAdmin,
    isStaff,
    isCustomer,
    login,
    register,
    fetchUserInfo,
    updateProfile,
    doChangePassword,
    restoreLogin,
    logout
  }
})
