import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/home.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: '/file-list',
          name: 'file-list',
          component: () => import('../views/FileList.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/file-details',
          name: 'upload',
          component: () => import('../views/UploadForm.vue'),
          meta: { requiresAuth: true }
        },
      ]
    },
    
    
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true }
    }
  ],
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 尝试从 localStorage 恢复用户状态
  userStore.restoreFromStorage()
  
  // 如果路由需要认证且用户未登录，重定向到登录页面
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next({ name: 'login' })
    return
  }
  
  // 如果路由需要游客状态（如登录页）且用户已登录，重定向到首页
  if (to.meta.requiresGuest && userStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }
  
  // 其他情况正常通过
  next()
})

export default router
