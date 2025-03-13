#!/bin/bash

# 启动后端
echo "正在启动后端服务..."
cd backend
./mvnw spring-boot:run &
echo $! > backend.pid
cd ..

# 等待后端启动完成
echo "等待后端服务启动..."
sleep 10

# 启动前端
echo "正在启动前端服务..."
cd frontend
npm install
npm run dev &
echo $! > frontend.pid
cd ..

echo "服务已启动！"
echo "后端运行在 http://localhost:8080"
echo "前端运行在 http://localhost:5173" 