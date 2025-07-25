import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/hdfs': {
        target: 'http://localhost:8080', // 后端服务端口
        changeOrigin: true,
        rewrite: path => path.replace(/^\/hdfs/, '/hdfs')
      }
    }
  }
})
