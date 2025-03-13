#!/bin/bash

echo "=== 开始重启服务 ==="

echo "1. 重启后端服务..."
./restart_backend.sh &

echo "等待后端服务启动..."
sleep 10

echo "2. 重启前端服务..."
./restart_frontend.sh &

echo "=== 服务重启完成 ==="
echo "后端服务运行在 http://localhost:8080"
echo "前端服务运行在 http://localhost:3000" 