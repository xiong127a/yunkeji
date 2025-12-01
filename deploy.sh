#!/bin/bash

# 部署脚本
# 用于在服务器上部署和启动整个云科记不动产查询系统

APP_NAME="yunkeji"
DEPLOY_DIR="/opt/${APP_NAME}"
LOG_FILE="${DEPLOY_DIR}/logs/deploy.log"

echo "开始部署 ${APP_NAME} 应用..." | tee -a ${LOG_FILE}

# 创建必要的目录
echo "创建必要的目录..." | tee -a ${LOG_FILE}
mkdir -p ${DEPLOY_DIR}/logs
mkdir -p ${DEPLOY_DIR}/uploads/real-estate

# 解压应用包
if [ -f "${APP_NAME}-*.tar.gz" ]; then
    echo "解压应用包..." | tee -a ${LOG_FILE}
    tar -xzf ${APP_NAME}-*.tar.gz -C /opt/
else
    echo "未找到应用包文件" | tee -a ${LOG_FILE}
    exit 1
fi

# 启动服务
echo "启动服务..." | tee -a ${LOG_FILE}
cd ${DEPLOY_DIR}
nohup ./start-prebuilt.sh > ${DEPLOY_DIR}/logs/app.log 2>&1 &

echo "应用部署完成！" | tee -a ${LOG_FILE}
echo "应用日志: ${DEPLOY_DIR}/logs/app.log" | tee -a ${LOG_FILE}