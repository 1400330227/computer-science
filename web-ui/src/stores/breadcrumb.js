import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useBreadcrumbStore = defineStore('breadcrumb', () => {
  const breadcrumbItems = ref([])
  
  function setBreadcrumb(items) {
    breadcrumbItems.value = items
  }
  
  function clearBreadcrumb() {
    breadcrumbItems.value = []
  }
  
  function addBreadcrumbItem(item) {
    breadcrumbItems.value.push(item)
  }

  return {
    breadcrumbItems,
    setBreadcrumb,
    clearBreadcrumb,
    addBreadcrumbItem
  }
}) 