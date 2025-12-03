import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/yunkeji/',
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/yunkeji/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 不需要rewrite，因为baseURL已经是/yunkeji/api
      }
    }
  }
})