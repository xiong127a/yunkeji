# 前端部署指南

## 构建项目

```bash
npm install
npm run build
```

构建完成后，会在 `dist` 目录下生成生产环境的静态文件。

## 部署到服务器

1. 将 `dist` 目录下的所有文件复制到服务器的 `/opt/frontend/dist` 目录下
2. 确保Nginx配置文件正确指向该目录
3. 重启Nginx服务

## Nginx配置说明

- 前端静态文件部署在 `/opt/frontend/dist` 目录下
- 通过 `/yunkeji/` 路径访问前端应用
- API请求会被代理到后端服务 `http://localhost:8080/yunkeji/api/`

## 权限设置

部署后，请确保Nginx进程对静态文件有读取权限：

```bash
sudo chown -R nginx:nginx /opt/frontend
sudo chmod -R 755 /opt/frontend
```

## 故障排除

如果遇到403或其他错误，请检查：

1. 文件权限是否正确设置
2. Nginx配置是否正确加载
3. 防火墙是否允许HTTP流量
4. 目录结构是否与配置匹配

```bash
# 检查Nginx错误日志
sudo tail -f /var/log/nginx/error.log

# 验证文件是否存在
ls -la /opt/frontend/dist/index.html
```