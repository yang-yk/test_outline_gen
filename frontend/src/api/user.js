import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: 'user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: 'user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserProfile() {
  return request({
    url: 'user/profile',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserProfile(data) {
  return request({
    url: 'user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(oldPassword, newPassword) {
  return request({
    url: 'user/password',
    method: 'put',
    params: {
      oldPassword,
      newPassword
    }
  })
}

// 上传头像
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: 'user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取API Key列表
export function getApiKeys() {
  return request({
    url: 'user/api-keys',
    method: 'get'
  })
}

// 创建API Key
export function createApiKey(data) {
  return request({
    url: 'user/api-keys',
    method: 'post',
    data
  })
}

// 删除API Key
export function deleteApiKey(id) {
  return request({
    url: `user/api-keys/${id}`,
    method: 'delete'
  })
} 