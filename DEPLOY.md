# 部署指南

## 前置要求

- JDK 11+
- Maven 3+
- Node.js 16+ (仅开发环境需要，生产环境不需要)
- MySQL 8.0

## 部署步骤

### 1. 构建前端

```bash
cd frontend
npm install
npm run build
```

构建完成后，前端静态文件会生成在 `frontend/dist` 目录下。

### 2. 构建后端（包含前端资源）

```bash
# 回到项目根目录
cd ..

# 清理并构建整个项目
mvn clean package
```

Maven 构建过程会自动：
- 复制前端构建产物到 `yunkeji-boot/target/classes/static/` 目录
- 打包成可执行的 JAR 文件

### 3. 打包部署文件

```bash
# 使用 assembly 插件打包
mvn assembly:single
```

这会在 `target` 目录下生成：
- `yunkeji-1.0-SNAPSHOT-dist.tar.gz`
- `yunkeji-1.0-SNAPSHOT-dist.zip`

### 4. 部署到服务器

#### 4.1 上传文件

将打包好的文件上传到服务器：

```bash
# 使用 scp 上传（示例）
scp target/yunkeji-1.0-SNAPSHOT-dist.tar.gz user@server:/opt/
```

#### 4.2 解压文件

```bash
# SSH 登录服务器
ssh user@server

# 解压文件
cd /opt
tar -xzf yunkeji-1.0-SNAPSHOT-dist.tar.gz
# 或者
unzip yunkeji-1.0-SNAPSHOT-dist.zip

# 重命名目录（可选）
mv yunkeji-1.0-SNAPSHOT-dist yunkeji
```

#### 4.3 配置数据库

1. 创建数据库：

```sql
CREATE DATABASE yunkeji CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：

```bash
mysql -u root -p yunkeji < yunkeji-dao/src/main/resources/db/migration/V1__Create_tables.sql
```

#### 4.4 修改配置文件

编辑 `config/application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://数据库地址:3306/yunkeji?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: 数据库用户名
    password: 数据库密码
```

#### 4.5 启动应用

```bash
cd /opt/yunkeji
chmod +x start-prebuilt.sh
./start-prebuilt.sh
```

#### 4.6 停止应用

```bash
./stop.sh
```

### 5. 验证部署

访问以下地址验证部署：

- 前端页面: `http://服务器IP:8080/yunkeji/`
- 后端API: `http://服务器IP:8080/yunkeji/api/`
- API文档: `http://服务器IP:8080/yunkeji/swagger-ui.html`

## 目录结构

部署后的目录结构：

```
/opt/yunkeji/
├── config/                  # 配置文件
│   ├── application.yml
│   ├── application-prod.yml
│   └── log4j2.xml
├── lib/                     # JAR 文件
│   └── yunkeji-boot-1.0-SNAPSHOT-exec.jar
├── logs/                    # 日志目录
├── uploads/                 # 上传文件目录
│   └── real-estate/
├── start-prebuilt.sh        # 启动脚本
└── stop.sh                  # 停止脚本
```

## 注意事项

1. **端口配置**：默认端口是 8080，如需修改，编辑 `config/application-prod.yml` 中的 `server.port`

2. **文件上传路径**：上传文件保存在 `./uploads/real-estate/` 目录，确保有写入权限

3. **日志文件**：日志文件在 `logs/` 目录，定期清理避免磁盘空间不足

4. **防火墙**：确保服务器防火墙开放 8080 端口

5. **JVM 参数**：如需调整 JVM 参数，编辑 `start-prebuilt.sh` 中的 `JAVA_OPTS`

6. **进程管理**：建议使用 systemd 或 supervisor 管理进程，确保服务自动重启

## 使用 Nginx 反向代理（可选）

如果需要使用 Nginx 作为反向代理：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /yunkeji/ {
        proxy_pass http://localhost:8080/yunkeji/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 故障排除

### 查看日志

```bash
# 查看应用日志
tail -f logs/stdout.log

# 查看错误日志
tail -f logs/stderr.log
```

### 检查进程

```bash
# 检查应用是否运行
ps aux | grep yunkeji

# 检查端口占用
netstat -tlnp | grep 8080
```

### 常见问题

1. **端口被占用**：修改 `application-prod.yml` 中的端口号
2. **数据库连接失败**：检查数据库配置和网络连接
3. **文件上传失败**：检查 `uploads` 目录权限
4. **前端页面404**：确保前端构建产物已正确复制到 JAR 包中














