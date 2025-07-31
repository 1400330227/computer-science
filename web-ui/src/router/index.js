import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/',
      name: 'dashboard',
      component: () => import('../views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/file-list',
      name: 'file-list',
      component: () => import('../views/FileList.vue'),
    },
    {
      path: '/corpus-details/:id',
      name: 'corpus-details',
      component: () => import('../views/corpusFileDetails.vue'),
    },
    {
      path: '/upload',
      name: 'file-upload',
      component: () => import('../views/UploadForm.vue'),
      meta: { requiresAuth: true }
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
  // 检查用户是否已登录
  const isAuthenticated = localStorage.getItem('isLoggedIn')

  // 如果路由需要认证且用户未登录，重定向到登录页面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
    return
  }
  
  // 如果用户未登录且访问根路径，重定向到登录页面
  if (!isAuthenticated && to.path === '/') {
    next({ name: 'login' })
    return
  }
  
  // 其他情况正常通过
  next()
})

export default router
