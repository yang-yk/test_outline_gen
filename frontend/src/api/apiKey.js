import request from '@/utils/request'

export function getApiKeys() {
  return request({
    url: '/api-keys',
    method: 'get'
  })
}

export function createApiKey(name) {
  return request({
    url: '/api-keys',
    method: 'post',
    params: { name }
  })
}

export function deleteApiKey(keyId) {
  return request({
    url: `/api-keys/${keyId}`,
    method: 'delete'
  })
} 