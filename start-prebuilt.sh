#!/bin/bash

# 预构建环境启动脚本
# 适用于已打包的应用程序，无需额外构建步骤

# 应用名称和目录
APP_NAME="yunkeji"
APP_HOME=$(cd "$(dirname "$0")"; pwd)
JAR_FILE="$APP_HOME/lib/yunkeji-boot-*-exec.jar"

# 日志和PID文件
LOG_DIR="$APP_HOME/logs"
STDOUT_LOG="$LOG_DIR/stdout.log"
STDERR_LOG="$LOG_DIR/stderr.log"
PID_FILE="$LOG_DIR/app.pid"

# JVM参数
JAVA_OPTS="-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"

# 检查是否已经运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null; then
        echo "应用程序已在运行 (PID: $PID)"
        exit 1
    else
        # PID文件存在但进程不存在，删除PID文件
        rm -f "$PID_FILE"
    fi
fi

# 查找可执行的JAR文件
if ! ls $JAR_FILE 1> /dev/null 2>&1; then
    echo "错误: 找不到可执行的JAR文件: $JAR_FILE"
    exit 1
fi

# 创建必要的目录
mkdir -p "$LOG_DIR"
mkdir -p "$APP_HOME/uploads/real-estate"

# 启动应用（使用生产环境配置）
echo "启动 $APP_NAME 应用 (生产环境)..."
echo "日志文件: $STDOUT_LOG"
nohup java $JAVA_OPTS -Dspring.profiles.active=prod -jar $JAR_FILE > "$STDOUT_LOG" 2> "$STDERR_LOG" &

# 保存PID
echo $! > "$PID_FILE"

echo "$APP_NAME 应用启动成功 (PID: $(cat "$PID_FILE"))"
echo "正在查看启动日志（最近200行），按 Ctrl+C 停止查看日志..."

# 只跟随一次日志：先输出最近200行，再持续跟随；放在前台，Ctrl+C 可退出
tail -n 200 -F "$STDOUT_LOG"