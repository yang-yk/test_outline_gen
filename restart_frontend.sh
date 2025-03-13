#!/bin/bash

echo "正在检查并关闭占用 3000 端口的进程..."

# 查找占用 3000 端口的进程
PID=$(lsof -ti:3000)

if [ ! -z "$PID" ]; then
    echo "找到占用 3000 端口的进程 (PID: $PID)，正在关闭..."
    kill -9 $PID
    echo "进程已关闭"
else
    echo "没有找到占用 3000 端口的进程"
fi

echo "正在启动前端服务..."
cd frontend && npm run dev 