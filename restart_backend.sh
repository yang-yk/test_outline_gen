#!/bin/bash

echo "正在检查并关闭占用 8080 端口的进程..."

# 查找占用 8080 端口的进程
PID=$(lsof -ti:8080)

if [ ! -z "$PID" ]; then
    echo "找到占用 8080 端口的进程 (PID: $PID)，正在关闭..."
    kill -9 $PID
    echo "进程已关闭"
else
    echo "没有找到占用 8080 端口的进程"
fi

echo "正在启动后端服务..."
cd backend && mvn spring-boot:run 