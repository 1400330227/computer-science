import './assets/main.scss'
import './styles/element-plus-theme.scss'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import axios from 'axios'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 在应用挂载前初始化用户状态
const userStore = useUserStore()
userStore.restoreFromStorage()

app.mount('#app')
