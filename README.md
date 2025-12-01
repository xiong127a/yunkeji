# 云科记不动产查询系统

## 项目简介

云科记不动产查询系统是一个基于Java和React的不动产信息查询平台，提供用户管理、资产信息查询、文件上传下载等功能。

## 技术栈

### 后端
- Java 11
- Spring Boot 2.7.18
- Maven 3+
- MyBatis-Flex
- MySQL 8.0
- Druid 连接池
- Flyway 数据库迁移

### 前端
- Next.js 13.5.6
- React 18.2.0
- Ant Design 5.10.0
- TypeScript 5.9.3

## 系统要求

- JDK 11+ (运行环境)
- Node.js 16+ (开发环境)
- MySQL 8.0
- Maven 3+ (开发环境)

## 构建项目

### 一体化构建
```bash
# 清理并编译整个项目（包括前端和后端）
mvn clean compile

# 打包整个项目
mvn package
```

### 单独构建前端
```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 构建生产版本
npm run build
```

## 部署

### 使用Maven Assembly插件打包
```bash
# 使用assembly插件打包
mvn assembly:single
```

这将会生成两个压缩包文件：
- `yunkeji-1.0-SNAPSHOT-dist.tar.gz`
- `yunkeji-1.0-SNAPSHOT-dist.zip`

### 部署步骤

1. 将打包好的文件上传到服务器
2. 解压文件到目标目录（例如：`/opt/yunkeji`）
3. 创建MySQL数据库并执行初始化脚本
4. 修改配置文件 `config/application-prod.yml`
5. 运行启动脚本：
   ```bash
   chmod +x start-prebuilt.sh
   ./start-prebuilt.sh
   ```

## 一体化部署说明

本项目采用前后端一体化部署方式：

1. 前端应用通过Next.js构建为静态资源
2. 静态资源被打包进后端应用中
3. 后端应用启动时同时提供REST API和前端静态资源服务
4. 只需启动一个进程即可运行整个系统

访问地址：
- 前端页面: http://localhost:8080/yunkeji/frontend/
- 后端API: http://localhost:8080/yunkeji/api/

## 配置说明

### 数据库配置
在 `config/application-prod.yml` 中修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://数据库地址:3306/yunkeji
    username: 数据库用户名
    password: 数据库密码
```

### 文件上传路径
默认上传文件保存在 `./uploads/real-estate/` 目录下，可通过以下配置修改：
```yaml
file:
  upload:
    real-estate:
      path: 自定义路径
```

## 系统优化（针对2C2G服务器）

1. 使用Undertow替代Tomcat以减少内存占用
2. 优化JVM参数：-Xms256m -Xmx768m
3. 调整数据库连接池大小
4. 使用G1垃圾收集器

## 目录结构
```
yunkeji/
├── config/                  # 配置文件
├── logs/                    # 日志目录
├── uploads/                 # 上传文件目录
│   └── real-estate/        # 不动产相关文件
├── yunkeji-boot-*.jar      # 后端可执行jar包
├── start-prebuilt.sh       # Linux预构建版启动脚本(仅需要JRE)
└── stop.sh                 # Linux停止脚本
```

## 注意事项

1. 确保服务器有足够的权限运行脚本
2. 生产环境中建议使用Nginx进行反向代理
3. 定期备份数据库和上传文件
4. 监控日志文件大小，避免磁盘空间不足
5. 在2C2G服务器上，一体化部署可以有效节省资源
6. 部署包是预构建版本，仅需要JRE环境即可运行，不需要Node.js和Maven