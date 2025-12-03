# 构建和部署脚本
#!/bin/bash

# 安装依赖
npm install

# 构建项目
npm run build

# 创建部署包
mkdir -p deploy-package
cp -r dist deploy-package/
cp nginx.conf deploy-package/

# 打包为zip文件
zip -r yunkeji-frontend-deploy.zip deploy-package

echo "构建完成，部署包已生成: yunkeji-frontend-deploy.zip"