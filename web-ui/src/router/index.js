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
      meta: { requiresGuest: true, title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: { requiresGuest: true, title: '注册' }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true, title: '首页' },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: Dashboard,
          meta: { requiresAuth: true, title: '仪表盘' }
        },
        {
          path: '/file-list',
          name: 'file-list',
          component: () => import('../views/CorpusFileList.vue'),
          meta: { requiresAuth: true, title: '语料清单' }
        },
        {
          path: '/file-upload',
          name: 'file-upload',
          component: () => import('../views/CorpusFileUpload.vue'),
          meta: { requiresAuth: true, title: '语料上传' }
        },
        {
          path: 'corpus-details/:id',
          name: 'corpus-details',
          component: () => import('../views/corpusFileDetails.vue'),
          meta: { requiresAuth: true, title: '语料详情' }
        },
        {
          path: '/my-files',
          name: 'my-files',
          component: () => import('../views/MyFileList.vue'),
          meta: { requiresAuth: true, title: '我的文件' }
        },
        {
          path: '/user-management',
          name: 'user-management',
          component: () => import('../views/UserManagement.vue'),
          meta: { requiresAuth: true, requiresAdmin: true, title: '用户管理' }
        },
        {
          path: '/corpus-management',
          name: 'corpus-management',
          component: () => import('../views/CorpusManagement.vue'),
          meta: { requiresAuth: true, requiresAdmin: true, title: '语料管理' }
        },
        {
          path: '/corpus-management-details/:id',
          name: 'corpus-management-details',
          component: () => import('../views/CorpusManagementDetails.vue'),
          meta: { requiresAuth: true, requiresAdmin: true, title: '语料详情' }
        },
        {
          path: '/my-info',
          name: 'my-info',
          component: () => import('../views/MyInfor.vue'),
          meta: { requiresAuth: true, title: '个人信息' }
        },
        {
          path: '/data-visualization',
          name: 'data-visualization',
          component: () => import('../views/DataVisualizationDashboard.vue'),
          meta: {requiresAuth: true, title: '数据可视化'}
        },
        {
          path: '/dashboard/country-distribution',
          name: 'country-distribution',
          component: () => import('../views/dashboard/CountryDistributionView.vue'),
          meta: { requiresAuth: true, title: '国家分布统计' }
        },
        {
          path: '/dashboard/language-distribution',
          name: 'language-distribution',
          component: () => import('../views/dashboard/LanguageDistributionView.vue'),
          meta: { requiresAuth: true, title: '语言分布统计' }
        },
        {
          path: '/dashboard/contributor-distribution',
          name: 'contributor-distribution',
          component: () => import('../views/dashboard/ContributorDistributionView.vue'),
          meta: { requiresAuth: true, title: '贡献者分布统计' }
        },
        {
          path: '/dashboard/time-series-analysis',
          name: 'time-series-analysis',
          component: () => import('../views/dashboard/TimeSeriesAnalysisView.vue'),
          meta: { requiresAuth: true, title: '趋势分析' }
        },
        {
          path: '/dashboard/thematic-analysis',
          name: 'thematic-analysis',
          component: () => import('../views/dashboard/ThematicAnalysisView.vue'),
          meta: { requiresAuth: true, title: '主题分析' }
        }
      ]
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true, title: '关于' }
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
