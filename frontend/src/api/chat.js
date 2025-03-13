import request from '../utils/request'

// 发送聊天消息
export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data
  })
}

// 发送聊天消息（流式响应）
export function sendMessageStream(data, onMessage, onError, onComplete) {
  const url = new URL('/api/chat/send/stream', window.location.origin)
  url.searchParams.append('content', data.content)
  if (data.model) {
    url.searchParams.append('model', data.model)
  }

  const eventSource = new EventSource(url.toString())

  eventSource.onmessage = (event) => {
    onMessage(event.data)
  }

  eventSource.onerror = (error) => {
    eventSource.close()
    onError(error)
  }

  // 当连接关闭时调用完成回调
  eventSource.addEventListener('complete', () => {
    eventSource.close()
    onComplete()
  })

  return eventSource
}

// 获取聊天历史记录
export function getChatHistory() {
  return request({
    url: '/chat/history',
    method: 'get'
  })
}

// 获取支持的大模型列表
export function getModelList() {
  return request({
    url: '/chat/models',
    method: 'get'
  })
}

// 保存 API Key
export function saveApiKey(data) {
  return request({
    url: '/chat/api-key',
    method: 'post',
    data
  })
}

// 获取 API Key 列表
export function getApiKeyList() {
  return request({
    url: '/chat/api-keys',
    method: 'get'
  })
}

// 删除 API Key
export function deleteApiKey(id) {
  return request({
    url: `/chat/api-key/${id}`,
    method: 'delete'
  })
} 