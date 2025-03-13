<template>
  <div class="outline-container">
    <el-row :gutter="100">
      <!-- 左侧配置面板 -->
      <el-col :span="8">
        <el-card class="config-card">
          <template #header>
            <div class="card-header">
              <span>配置信息</span>
              <el-button
                type="primary"
                size="small"
                plain
                @click="showModelConfig = true"
              >
                <el-icon><setting /></el-icon>
                大模型配置
              </el-button>
            </div>
          </template>

          <el-form
            ref="configFormRef"
            :model="configForm"
            :rules="configRules"
            label-position="left"
            :label-width="120"
          >
            <!-- 大模型选择 -->
            <el-form-item label="大模型选择" prop="model">
              <el-select
                v-model="configForm.model"
                placeholder="请选择大模型"
                class="w-100"
                @change="handleModelChange"
              >
                <el-option label="GPT-4" value="gpt4" />
                <el-option label="ChatGLM" value="chatglm" />
                <el-option label="通义千问" value="qwen" />
                <el-option 
                  v-for="model in customModels" 
                  :key="model.id" 
                  :label="model.name" 
                  :value="model.id" 
                />
              </el-select>
            </el-form-item>

            <!-- 大纲模板选择 -->
            <el-form-item label="大纲模板" prop="template">
              <el-select
                v-model="configForm.template"
                placeholder="请选择大纲模板"
                class="w-100"
              >
                <el-option label="标准模板" value="standard" />
                <el-option label="简化模板" value="simple" />
                <el-option label="详细模板" value="detailed" />
              </el-select>
            </el-form-item>

            <!-- 知识库选择 -->
            <el-form-item label="知识库选择" prop="knowledgeBase">
              <el-select
                v-model="configForm.knowledgeBase"
                placeholder="请选择知识库"
                class="w-100"
              >
                <el-option label="通用测试知识库" value="general" />
                <el-option label="Web应用测试知识库" value="web" />
                <el-option label="移动应用测试知识库" value="mobile" />
              </el-select>
            </el-form-item>

            <!-- 文件上传 -->
            <el-form-item label="需求规格说明书" prop="file" class="upload-form-item">
              <el-upload
                class="w-100"
                drag
                action="/api/outline/upload"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                accept=".pdf,.doc,.docx"
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 PDF、Word 格式文件
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <!-- 生成按钮 -->
            <el-form-item class="generate-button-container">
              <el-button
                type="primary"
                :loading="generating"
                class="generate-button"
                @click="handleGenerate"
              >
                生成测试大纲
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 中间预览面板 -->
      <el-col :span="10">
        <el-card class="preview-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <span>测试大纲预览</span>
                <el-switch
                  v-model="isEditing"
                  active-text="编辑模式"
                  inactive-text="预览模式"
                  class="edit-switch"
                />
              </div>
              <div class="header-actions">
                <el-button v-if="outlineContent" type="primary" size="small" @click="handleDownload">
                  <el-icon><download /></el-icon>
                  导出Word
                </el-button>
                <el-button 
                  v-if="outlineContent && isEditing" 
                  type="success" 
                  size="small" 
                  @click="handleSave"
                >
                  <el-icon><document-add /></el-icon>
                  保存
                </el-button>
              </div>
            </div>
          </template>

          <div v-if="generating" class="generating-placeholder">
            <el-icon class="is-loading"><loading /></el-icon>
            <p>正在生成测试大纲，请稍候...</p>
          </div>

          <div v-else-if="outlineContent" class="outline-preview">
            <div v-if="isEditing" class="editor-container">
              <el-input
                v-model="outlineContent"
                type="textarea"
                :rows="20"
                resize="none"
                :autosize="{ minRows: 10, maxRows: 99999 }"
                @input="handleContentChange"
              />
            </div>
            <div v-else class="preview-content markdown-body" v-html="renderedContent"></div>
          </div>

          <div v-else class="empty-placeholder">
            <el-empty description="暂无内容" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧聊天面板 -->
      <el-col :span="6">
        <el-card class="chat-card">
          <template #header>
            <div class="card-header">
              <span>与AI助手对话</span>
            </div>
          </template>
          <chat-window
            :messages="chatMessages"
            :sending="chatLoading"
            :user-avatar="userAvatar"
            :ai-avatar="aiAvatar"
            @send="handleSendMessage"
            @cancel="handleCancelMessage"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 大模型配置对话框 -->
    <el-dialog
      v-model="showModelConfig"
      title="大模型配置"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="modelConfigFormRef"
        :model="modelConfigForm"
        :rules="modelConfigRules"
        label-width="100px"
      >
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="modelConfigForm.name" placeholder="请输入模型名称" />
        </el-form-item>
        
        <el-form-item label="API地址" prop="apiUrl">
          <el-input v-model="modelConfigForm.apiUrl" placeholder="请输入API地址" />
        </el-form-item>
        
        <el-form-item label="API Key" prop="apiKey">
          <el-input 
            v-model="modelConfigForm.apiKey" 
            placeholder="请输入API Key"
            show-password 
          />
        </el-form-item>
        
        <el-form-item label="模型类型" prop="type">
          <el-select v-model="modelConfigForm.type" placeholder="请选择模型类型">
            <el-option label="OpenAI兼容" value="openai" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>

        <el-divider>模型参数配置</el-divider>

        <el-form-item label="Temperature" prop="temperature">
          <el-slider
            v-model="modelConfigForm.temperature"
            :min="0"
            :max="1"
            :step="0.1"
          />
        </el-form-item>

        <el-form-item label="最大长度" prop="maxTokens">
          <el-input-number
            v-model="modelConfigForm.maxTokens"
            :min="100"
            :max="4000"
            :step="100"
            class="w-100"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showModelConfig = false">取消</el-button>
          <el-button type="primary" @click="handleSaveModelConfig">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  UploadFilled,
  Download,
  DocumentAdd,
  Loading,
  Setting
} from '@element-plus/icons-vue'
import ChatWindow from '@/components/ChatWindow.vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

// 表单数据
const configFormRef = ref(null)
const configForm = reactive({
  file: null,
  knowledgeBase: '',
  template: 'standard',
  model: '',
  temperature: 0.7,
  maxTokens: 2000,
  apiKey: ''
})

// 表单验证规则
const configRules = {
  template: [{ required: true, message: '请选择大纲模板', trigger: 'change' }],
  model: [{ required: true, message: '请选择大模型', trigger: 'change' }]
}

// 状态变量
const generating = ref(false)
const outlineContent = ref('')
const chatLoading = ref(false)
const chatMessages = ref([])
const currentOutlineId = ref(null)

// 头像
const userAvatar = ref('/avatars/user.png')
const aiAvatar = ref('/avatars/ai.png')

// 计算属性
const needApiKey = computed(() => {
  return ['gpt4'].includes(configForm.model)
})

// 编辑状态
const isEditing = ref(false)
const contentChanged = ref(false)

// 渲染预览内容
const renderedContent = computed(() => {
  if (!outlineContent.value) return ''
  const htmlContent = marked(outlineContent.value)
  return DOMPurify.sanitize(htmlContent)
})

// 处理内容变化
const handleContentChange = () => {
  contentChanged.value = true
}

// 文件上传相关方法
const beforeUpload = (file) => {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  if (!isValidType) {
    ElMessage.error('只能上传PDF或Word文件！')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  configForm.file = response.data
  ElMessage.success('文件上传成功')
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

// 模型选择变化
const handleModelChange = () => {
  if (configForm.model === 'gpt4') {
    // 使用默认配置
    configForm.temperature = 0.7
    configForm.maxTokens = 2000
    if (!configForm.apiKey) {
      ElMessage.warning('请注意：当前选择的模型需要配置API Key')
    }
  } else {
    // 使用自定义模型的配置
    const selectedModel = customModels.value.find(model => model.id === configForm.model)
    if (selectedModel) {
      configForm.temperature = selectedModel.temperature
      configForm.maxTokens = selectedModel.maxTokens
    }
  }
}

// 生成测试大纲
const handleGenerate = async () => {
  if (!configFormRef.value) return

  await configFormRef.value.validate(async (valid) => {
    if (valid) {
      generating.value = true
      try {
        const generateDTO = {
          requirementId: configForm.file ? configForm.file.id : null,
          title: configForm.file ? configForm.file.fileName.replace(/\.[^/.]+$/, '') + '_测试大纲' : '新测试大纲',
          model: configForm.model,
          knowledgeBase: configForm.knowledgeBase || null,
          template: configForm.template,
          temperature: configForm.temperature,
          maxTokens: configForm.maxTokens
        }

        const response = await generateOutline(generateDTO)
        outlineContent.value = response.content
        currentOutlineId.value = response.id
        ElMessage.success('测试大纲生成成功')
      } catch (error) {
        ElMessage.error('生成失败：' + error.message)
      } finally {
        generating.value = false
      }
    }
  })
}

// 导出Word文档
const handleDownload = async () => {
  try {
    const response = await downloadDocument(currentOutlineId.value, 'OUTLINE')
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '测试大纲.docx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败：' + error.message)
  }
}

// 发送聊天消息
const handleSendMessage = async (message) => {
  if (chatLoading.value) return

  chatMessages.value.push({ role: 'user', content: message })
  
  chatLoading.value = true
  try {
    // 添加一个带loading状态的消息
    const aiMessage = {
      role: 'assistant',
      content: '',
      loading: true
    }
    chatMessages.value.push(aiMessage)

    // TODO: 调用聊天API
    const response = await fetch('/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ message })
    })

    if (!response.ok) throw new Error('请求失败')
    
    const data = await response.json()
    aiMessage.content = data.response
    aiMessage.loading = false
  } catch (error) {
    ElMessage.error('发送失败：' + error.message)
    // 移除失败的消息
    chatMessages.value.pop()
  } finally {
    chatLoading.value = false
  }
}

// 取消聊天消息
const handleCancelMessage = () => {
  chatLoading.value = false
  if (chatMessages.value.length > 0) {
    const lastMessage = chatMessages.value[chatMessages.value.length - 1]
    if (lastMessage.role === 'assistant' && lastMessage.loading) {
      chatMessages.value.pop()
    }
  }
}

// 保存前确认
const handleSave = async () => {
  try {
    if (!contentChanged.value) {
      ElMessage.info('内容未发生变化')
      return
    }

    await ElMessageBox.confirm(
      '确定要保存修改吗？',
      '保存确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await saveOutline(currentOutlineId.value, outlineContent.value)
    contentChanged.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('保存失败：' + error.message)
    }
  }
}

// 关闭页面前确认
onBeforeUnmount(() => {
  if (contentChanged.value) {
    ElMessageBox.confirm(
      '有未保存的修改，确定要离开吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).catch(() => {
      // 用户取消离开
    })
  }
})

// 自定义模型列表
const customModels = ref([])

// 模型配置相关
const showModelConfig = ref(false)
const modelConfigFormRef = ref(null)
const modelConfigForm = reactive({
  name: '',
  apiUrl: '',
  apiKey: '',
  type: 'openai',
  temperature: 0.7,
  maxTokens: 2000
})

const modelConfigRules = {
  name: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入API地址', trigger: 'blur' }],
  apiKey: [{ required: true, message: '请输入API Key', trigger: 'blur' }],
  type: [{ required: true, message: '请选择模型类型', trigger: 'change' }],
  temperature: [{ required: true, message: '请设置temperature值', trigger: 'change' }],
  maxTokens: [{ required: true, message: '请设置最大长度', trigger: 'change' }]
}

// 保存模型配置
const handleSaveModelConfig = async () => {
  if (!modelConfigFormRef.value) return
  
  await modelConfigFormRef.value.validate(async (valid) => {
    if (valid) {
      const newModel = {
        id: Date.now().toString(),
        name: modelConfigForm.name,
        apiUrl: modelConfigForm.apiUrl,
        apiKey: modelConfigForm.apiKey,
        type: modelConfigForm.type,
        temperature: modelConfigForm.temperature,
        maxTokens: modelConfigForm.maxTokens
      }
      
      customModels.value.push(newModel)
      showModelConfig.value = false
      ElMessage.success('模型配置保存成功')
      
      // 重置表单
      modelConfigForm.name = ''
      modelConfigForm.apiUrl = ''
      modelConfigForm.apiKey = ''
      modelConfigForm.type = 'openai'
      modelConfigForm.temperature = 0.7
      modelConfigForm.maxTokens = 2000
    }
  })
}
</script>

<style scoped>
.outline-container {
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.config-card,
.preview-card,
.chat-card {
  height: calc(100vh - 140px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
}

.config-card :deep(.el-card__body),
.preview-card :deep(.el-card__body),
.chat-card :deep(.el-card__body) {
  flex: 1;
  padding: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.config-card :deep(.el-card__header),
.preview-card :deep(.el-card__header),
.chat-card :deep(.el-card__header) {
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  padding: 15px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.w-100 {
  width: 100%;
}

.generating-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
}

.generating-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.outline-preview {
  padding: 20px;
}

.empty-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;
}

.message.assistant {
  flex-direction: row-reverse;
}

.message-content {
  background: #f4f4f5;
  padding: 12px;
  border-radius: 8px;
  max-width: 80%;
}

.message.assistant .message-content {
  background: #ecf5ff;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #dcdfe6;
}

.float-chat-button {
  display: none !important;
}

.chat-drawer {
  display: none !important;
}

.el-drawer__header {
  display: none !important;
}

.el-drawer__body {
  display: none !important;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.edit-switch {
  margin-left: 16px;
}

.editor-container {
  height: 100%;
  padding: 20px;
}

.editor-container :deep(.el-textarea__inner) {
  font-family: Monaco, Consolas, Courier New, monospace;
  font-size: 14px;
  line-height: 1.6;
  padding: 12px;
}

.preview-content {
  padding: 20px;
  min-height: 300px;
}

/* Markdown 样式 */
.markdown-body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body :deep(h1) {
  font-size: 2em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
}

.markdown-body :deep(h2) {
  font-size: 1.5em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
}

.markdown-body :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin-top: 0;
  margin-bottom: 16px;
  padding-left: 2em;
}

.markdown-body :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
}

.markdown-body :deep(code) {
  font-family: Monaco, Consolas, Courier New, monospace;
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 6px;
}

.markdown-body :deep(pre code) {
  padding: 0;
  margin: 0;
  font-size: 100%;
  word-break: normal;
  white-space: pre;
  background: transparent;
  border: 0;
}

.markdown-body :deep(table) {
  border-spacing: 0;
  border-collapse: collapse;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(table th),
.markdown-body :deep(table td) {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body :deep(table tr:nth-child(2n)) {
  background-color: #f6f8fa;
}

.upload-container {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
}

.upload-container :deep(.el-upload) {
  width: 100%;
  display: block;
}

.upload-container :deep(.el-upload-dragger) {
  width: 100%;
  height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color .3s;
}

.upload-container :deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
}

.upload-container :deep(.el-icon--upload) {
  margin: 0 0 4px;
  font-size: 20px;
}

.upload-container :deep(.el-upload__text) {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.upload-container :deep(.el-upload__text em) {
  color: #409EFF;
  font-style: normal;
  margin-left: 4px;
}

.upload-container :deep(.el-upload__tip) {
  display: block;
  text-align: center;
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
}

.upload-container :deep(.el-upload--text) {
  width: 100%;
}

.config-card :deep(.el-form-item__label) {
  text-align: left;
  padding-right: 12px;
}

.config-card :deep(.el-form-item) {
  margin-bottom: 20px;
  text-align: left;
}

.config-card :deep(.el-select),
.config-card :deep(.el-input),
.config-card :deep(.el-input-number) {
  width: 100%;
  text-align: left;
}

.config-card :deep(.el-upload) {
  display: block;
  width: 100%;
}

.config-card :deep(.el-upload-dragger) {
  width: 100%;
  height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color .3s;
}

.config-card :deep(.el-upload--text) {
  width: 100%;
}

.config-card :deep(.el-upload-list) {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.el-divider {
  margin: 24px 0;
}

.generate-button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
}

.generate-button {
  width: 60%;
  height: 40px;
  font-size: 16px;
  padding: 12px 20px;
}

.upload-form-item :deep(.el-form-item__label) {
  line-height: 60px;
  margin: 0;
  padding: 0;
}

.upload-form-item :deep(.el-form-item__content) {
  display: flex;
  align-items: center;
  line-height: 60px;
}

.upload-form-item :deep(.el-upload) {
  width: 100%;
}

.upload-form-item :deep(.el-upload-dragger) {
  width: 100%;
  height: 60px;
  margin: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.upload-form-item :deep(.el-icon--upload) {
  margin: 0;
  font-size: 20px;
  height: 20px;
  line-height: 1;
}

.upload-form-item :deep(.el-upload__text) {
  font-size: 13px;
  margin: 0;
  line-height: 1;
}
</style> 