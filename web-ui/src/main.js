import './assets/main.scss'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import './assets/index.scss'

import App from './App.vue'
import router from './router'
import axios from 'axios'
import { useUserStore } from './stores/user'

// 导入Element Plus图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 注册所有Element Plus图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 在应用挂载前初始化用户状态
const userStore = useUserStore()
userStore.restoreFromStorage()

app.mount('#app')
