#!/bin/bash

# 停止后端
echo "正在停止后端服务..."
if [ -f backend/backend.pid ]; then
    kill $(cat backend/backend.pid)
    rm backend/backend.pid
    echo "后端服务已停止"
else
    echo "未找到后端服务进程"
fi

# 停止前端
echo "正在停止前端服务..."
if [ -f frontend/frontend.pid ]; then
    kill $(cat frontend/frontend.pid)
    rm frontend/frontend.pid
    echo "前端服务已停止"
else
    echo "未找到前端服务进程"
fi

# 确保所有相关进程都已停止
echo "清理残留进程..."
pkill -f "spring-boot:run" || true
pkill -f "node.*dev" || true

echo "所有服务已停止！" 