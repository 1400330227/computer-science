import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Dashboard from '@/views/Dashboard.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/AccountLogin.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: Dashboard,
          meta: { requiresAuth: true }
        },
        {
          path: '/file-list',
          name: 'file-list',
          component: () => import('../views/CorpusFileList.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/file-upload',
          name: 'file-upload',
          component: () => import('../views/CorpusFileUpload.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'corpus-details/:id',
          name: 'corpus-details',
          component: () => import('../views/corpusFileDetails.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/my-files',
          name: 'my-files',
          component: () => import('../views/MyFileList.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/user-management',
          name: 'user-management',
          component: () => import('../views/UserManagement.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: '/corpus-management',
          name: 'corpus-management',
          component: () => import('../views/CorpusManagement.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        }
      ]
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true }
    },

  ]
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

  // 如果路由需要管理员权限但用户不是管理员，重定向到首页
  if (to.meta.requiresAdmin && userStore.user?.userType !== 'admin') {
    alert('You do not have permission to access this page.');
    next({ name: 'home' });
    return;
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
