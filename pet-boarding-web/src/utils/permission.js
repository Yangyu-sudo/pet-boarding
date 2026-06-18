import { useUserStore } from '@/store/user'

/**
 * 权限指令 v-permission
 * 用法: v-permission="['ADMIN']" 或 v-permission="['ADMIN','STAFF']"
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    if (value && Array.isArray(value) && value.length > 0) {
      const userStore = useUserStore()
      const userRole = userStore.userInfo?.role
      if (!value.includes(userRole)) {
        el.parentNode?.removeChild(el)
      }
    }
  }
}
