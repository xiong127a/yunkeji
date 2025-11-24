/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  // 确保支持App Router
  pageExtensions: ['tsx', 'ts', 'jsx', 'js'],
}

module.exports = nextConfig