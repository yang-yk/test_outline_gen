# 中科院软件所测试大纲智能生成工具 - 前端

## 项目介绍

本项目是中科院软件所测试大纲智能生成工具的前端部分，基于 Vue3 + Element Plus 开发。

## 功能特性

- 用户管理（登录、注册、个人中心）
- 需求规格说明书上传和管理
- 测试大纲智能生成
- 文档管理（测试大纲、需求说明书）
- 大模型对话
- API Key 管理

## 技术栈

- Vue3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 开发环境要求

- Node.js >= 16
- npm >= 7

## 安装和运行

1. 安装依赖

```bash
npm install
```

2. 开发环境运行

```bash
npm run dev
```

3. 生产环境构建

```bash
npm run build
```

## 项目结构

```
src/
  ├── api/              # API 接口
  ├── assets/           # 静态资源
  ├── components/       # 公共组件
  ├── layout/          # 布局组件
  ├── router/          # 路由配置
  ├── stores/          # 状态管理
  ├── utils/           # 工具函数
  ├── views/           # 页面组件
  ├── App.vue          # 根组件
  └── main.js          # 入口文件
```

## 环境变量

在项目根目录创建 `.env` 文件：

```
# API 基础路径
VITE_API_BASE_URL=/api

# 应用标题
VITE_APP_TITLE=中科院软件所测试大纲智能生成工具

# 上传文件大小限制（单位：MB）
VITE_UPLOAD_SIZE_LIMIT=10

# 支持的文件类型
VITE_SUPPORTED_FILE_TYPES=.pdf,.doc,.docx
```

## 开发规范

1. 组件命名采用大驼峰命名法
2. API 接口统一管理在 `api` 目录下
3. 状态管理使用 Pinia
4. 路由配置统一管理在 `router` 目录下
5. 工具函数统一管理在 `utils` 目录下

## 部署

1. 构建生产环境代码

```bash
npm run build
```

2. 将 `dist` 目录下的文件部署到 Web 服务器

## 代理配置

开发环境下，API 请求会被代理到 `http://localhost:8080`。可以在 `vite.config.js` 中修改代理配置：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```
