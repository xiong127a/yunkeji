/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  // 确保支持App Router
  pageExtensions: ['tsx', 'ts', 'jsx', 'js'],
  
  // 保持basePath配置
  basePath: '/yunkeji',
  
  // 配置rewrites，将API请求转发到后端
  async rewrites() {
    return [
      {
        source: '/api/:path*',  // 不需要包含basePath部分
        destination: 'http://localhost:8080/yunkeji/api/:path*' // 使用localhost，因为前后端部署在同一服务器上
      },
    ]
  },
};

module.exports = nextConfig