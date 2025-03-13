<template>
  <div class="chat-window">
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" 
           :key="index" 
           :class="['message', message.role]">
        <div class="message-content">
          <div class="avatar">
            <el-avatar :size="36" :src="message.role === 'user' ? userAvatar : aiAvatar" />
          </div>
          <div class="text">
            <div class="name">{{ message.role === 'user' ? '你' : 'AI助手' }}</div>
            <div class="content" v-html="formatMessage(message.content)"></div>
          </div>
        </div>
        <div v-if="message.role === 'assistant' && message.loading" class="typing-indicator">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        placeholder="请输入消息..."
        resize="none"
        @keydown.enter.prevent="handleSend"
      />
      <div class="button-group">
        <el-button 
          type="primary" 
          :disabled="!inputMessage.trim() || sending" 
          @click="handleSend">
          发送
        </el-button>
        <el-button 
          v-if="sending" 
          type="danger" 
          @click="handleCancel">
          取消
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  userAvatar: {
    type: String,
    default: ''
  },
  aiAvatar: {
    type: String,
    default: ''
  },
  sending: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'cancel', 'close'])

const inputMessage = ref('')
const messagesContainer = ref(null)

const handleSend = () => {
  if (!inputMessage.value.trim() || props.sending) return
  emit('send', inputMessage.value)
  inputMessage.value = ''
}

const handleCancel = () => {
  emit('cancel')
}

const formatMessage = (content) => {
  // 使用marked将markdown转换为HTML，并使用DOMPurify清理HTML
  return DOMPurify.sanitize(marked(content))
}

// 监听消息变化，自动滚动到底部
watch(() => props.messages, async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}, { deep: true })

// 组件挂载时滚动到底部
onMounted(() => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
})
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 20px;
}

.message-content {
  display: flex;
  gap: 12px;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  flex-shrink: 0;
}

.text {
  max-width: 80%;
}

.message.user .text {
  text-align: right;
}

.name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.content {
  background: #f4f4f5;
  padding: 12px;
  border-radius: 8px;
  word-break: break-word;
}

.message.user .content {
  background: #ecf5ff;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

/* 打字指示器样式 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px;
  justify-content: center;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #90939975;
  border-radius: 50%;
  animation: bounce 1.3s linear infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes bounce {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-4px);
  }
}

:deep(pre) {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
}

:deep(code) {
  font-family: Monaco, Consolas, Courier New, monospace;
}

:deep(p) {
  margin: 8px 0;
}

:deep(ul), :deep(ol) {
  padding-left: 20px;
}
</style> 