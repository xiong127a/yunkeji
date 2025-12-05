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
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/yunkeji/api': {
        target: 'http://127.0.0.1:8080', // 使用127.0.0.1而不是localhost，避免IPv6问题
        changeOrigin: true,
        secure: false,
        timeout: 30000,
        ws: true,
        rewrite: (path) => path, // 保持路径不变
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.warn('代理错误:', err.message);
            console.warn('请确保后端服务运行在 http://127.0.0.1:8080');
            // 如果响应对象存在，返回错误响应
            if (res && !res.headersSent) {
              res.writeHead(500, {
                'Content-Type': 'application/json',
              });
              res.end(JSON.stringify({
                error: '代理错误：无法连接到后端服务',
                message: '请确保后端服务已启动并运行在 http://127.0.0.1:8080'
              }));
            }
          });
        },
      }
    }
  }
})