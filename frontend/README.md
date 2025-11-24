# 云科技不动产查询系统前端

这是一个基于 Next.js 14 + TypeScript + Ant Design 的前端项目。

## 技术栈

- Next.js 14 (App Router)
- TypeScript
- Ant Design 5
- Axios
- js-cookie

## 项目结构

```
src/
├── app/
│   ├── api/           # API 客户端
│   ├── components/    # 公共组件
│   ├── layouts/       # 布局组件
│   ├── pages/         # 页面组件
│   ├── public/        # 静态资源
│   ├── styles/        # 样式文件
│   ├── utils/         # 工具函数
│   ├── types/         # TypeScript 类型定义
│   ├── layout.tsx     # 根布局
│   └── page.tsx       # 首页
```

## 功能特性

1. 用户认证（登录/注册）
2. 不动产信息查询
3. 文件上传（PDF、图片等）
4. 查询记录管理
5. 查询结果查看

## 环境要求

- Node.js >= 16.14.0
- npm 或 yarn

## 安装依赖

```bash
npm install
# 或
yarn install
```

## 开发环境运行

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:3000 查看应用。

## 构建生产版本

```bash
npm run build
# 或
yarn build
```

## 运行生产版本

```bash
npm start
# 或
yarn start
```

## 配置

在 `src/app/api/client.ts` 中配置后端 API 地址：

```typescript
this.axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});
```

## 页面说明

- `/` - 首页
- `/login` - 登录页面
- `/register` - 注册页面
- `/dashboard` - 用户控制台
- `/query` - 新建查询
- `/query/[id]` - 查询详情

## 注意事项

1. 确保后端服务已启动并可访问
2. 根据实际需求修改 API 地址
3. 文件上传大小限制根据后端配置调整