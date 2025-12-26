#!/bin/bash

# 停止云科记大数据查询系统

echo "正在停止云科记大数据查询系统..."

# 查找并终止Java进程
pids=$(ps aux | grep "yunkeji-boot" | grep -v grep | awk '{print $2}')
if [ -n "$pids" ]; then
    kill $pids
    echo "系统已停止"
else
    echo "未找到运行中的系统进程"
fi