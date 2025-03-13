import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  sendMessage,
  sendMessageStream,
  getChatHistory,
  getModelList,
  saveApiKey,
  getApiKeyList,
  deleteApiKey
} from '../api/chat'

export const useChatStore = defineStore('chat', () => {
  const chatMessages = ref([])
  const modelList = ref([])
  const apiKeys = ref([])
  const loading = ref(false)
  const currentEventSource = ref(null)

  // 发送消息（普通响应）
  async function sendMessageAction(message) {
    try {
      loading.value = true
      const res = await sendMessage(message)
      chatMessages.value.push({
        role: 'user',
        content: message
      })
      chatMessages.value.push({
        role: 'assistant',
        content: res.content
      })
      return res
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 发送消息（流式响应）
  function sendMessageStreamAction(message) {
    loading.value = true
    
    // 添加用户消息
    chatMessages.value.push({
      role: 'user',
      content: message.content
    })
    
    // 添加助手消息（初始为空）
    const assistantMessage = {
      role: 'assistant',
      content: ''
    }
    chatMessages.value.push(assistantMessage)

    // 关闭之前的连接
    if (currentEventSource.value) {
      currentEventSource.value.close()
    }

    // 创建新的流式连接
    currentEventSource.value = sendMessageStream(
      message,
      // 处理消息
      (content) => {
        assistantMessage.content += content
      },
      // 处理错误
      (error) => {
        loading.value = false
        assistantMessage.content = '消息处理失败：' + error.message
      },
      // 处理完成
      () => {
        loading.value = false
        currentEventSource.value = null
      }
    )
  }

  // 取消当前的流式响应
  function cancelStream() {
    if (currentEventSource.value) {
      currentEventSource.value.close()
      currentEventSource.value = null
      loading.value = false
    }
  }

  // 获取聊天历史
  async function loadChatHistory() {
    try {
      const res = await getChatHistory()
      chatMessages.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取支持的大模型列表
  async function loadModelList() {
    try {
      const res = await getModelList()
      modelList.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 保存 API Key
  async function saveApiKeyAction(data) {
    try {
      const res = await saveApiKey(data)
      await loadApiKeyList()
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取 API Key 列表
  async function loadApiKeyList() {
    try {
      const res = await getApiKeyList()
      apiKeys.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 删除 API Key
  async function deleteApiKeyAction(id) {
    try {
      await deleteApiKey(id)
      apiKeys.value = apiKeys.value.filter(item => item.id !== id)
    } catch (error) {
      throw error
    }
  }

  // 清空聊天记录
  function clearChatMessages() {
    chatMessages.value = []
  }

  return {
    chatMessages,
    modelList,
    apiKeys,
    loading,
    sendMessageAction,
    sendMessageStreamAction,
    cancelStream,
    loadChatHistory,
    loadModelList,
    saveApiKeyAction,
    loadApiKeyList,
    deleteApiKeyAction,
    clearChatMessages
  }
}) 