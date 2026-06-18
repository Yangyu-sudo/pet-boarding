import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const breadcrumbs = ref([])
  const loading = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setBreadcrumbs(items) {
    breadcrumbs.value = items
  }

  function setLoading(val) {
    loading.value = val
  }

  return {
    sidebarCollapsed,
    breadcrumbs,
    loading,
    toggleSidebar,
    setBreadcrumbs,
    setLoading
  }
})
