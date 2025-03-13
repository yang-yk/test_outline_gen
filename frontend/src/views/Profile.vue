<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-container">
            <el-avatar
              :size="100"
              :src="userInfo.avatar"
              @error="handleAvatarError"
            />
            <el-upload
              class="avatar-uploader"
              action="/api/user/avatar"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small" type="primary" class="upload-btn">
                更换头像
              </el-button>
            </el-upload>
          </div>
          
          <div class="user-info">
            <h3>{{ userInfo.username }}</h3>
            <p>{{ userInfo.email }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <span>个人设置</span>
            </div>
          </template>

          <el-form
            ref="settingsFormRef"
            :model="settingsForm"
            :rules="settingsRules"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="settingsForm.username" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="settingsForm.email" disabled />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="settingsForm.newPassword"
                type="password"
                show-password
                placeholder="如需修改密码请输入新密码"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="settingsForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="updating"
                @click="handleUpdateProfile"
              >
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="api-keys-card">
          <template #header>
            <div class="card-header">
              <span>API Keys 管理</span>
              <el-button
                type="primary"
                size="small"
                @click="handleAddApiKey"
              >
                添加 API Key
              </el-button>
            </div>
          </template>

          <el-table :data="apiKeys" style="width: 100%">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  link
                  @click="handleDeleteApiKey(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加 API Key 对话框 -->
    <el-dialog
      v-model="apiKeyDialogVisible"
      title="添加 API Key"
      width="500px"
    >
      <el-form
        ref="apiKeyFormRef"
        :model="apiKeyForm"
        :rules="apiKeyRules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="apiKeyForm.name" placeholder="请输入名称" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-select v-model="apiKeyForm.type" placeholder="请选择类型">
            <el-option label="OpenAI" value="openai" />
            <el-option label="智谱 AI" value="zhipu" />
            <el-option label="通义千问" value="qwen" />
          </el-select>
        </el-form-item>

        <el-form-item label="API Key" prop="key">
          <el-input
            v-model="apiKeyForm.key"
            type="password"
            show-password
            placeholder="请输入 API Key"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="apiKeyDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="addingApiKey"
            @click="handleSubmitApiKey"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 用户信息
const userInfo = reactive({
  username: '测试用户',
  email: 'test@example.com',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 设置表单
const settingsFormRef = ref(null)
const updating = ref(false)
const settingsForm = reactive({
  username: userInfo.username,
  email: userInfo.email,
  newPassword: '',
  confirmPassword: ''
})

const settingsRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (settingsForm.newPassword && value !== settingsForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// API Keys 管理
const apiKeys = ref([
  {
    id: 1,
    name: 'OpenAI API',
    type: 'openai',
    createTime: '2024-03-13 10:00:00'
  }
])

const apiKeyDialogVisible = ref(false)
const addingApiKey = ref(false)
const apiKeyFormRef = ref(null)
const apiKeyForm = reactive({
  name: '',
  type: '',
  key: ''
})

const apiKeyRules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  key: [
    { required: true, message: '请输入 API Key', trigger: 'blur' }
  ]
}

// 工具函数
const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

// 头像相关方法
const handleAvatarError = () => {
  userInfo.avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  userInfo.avatar = response.data
  ElMessage.success('头像更新成功')
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!settingsFormRef.value) return

  await settingsFormRef.value.validate(async (valid) => {
    if (valid) {
      updating.value = true
      try {
        // TODO: 调用更新API
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        ElMessage.success('个人信息更新成功')
        // 更新本地数据
        userInfo.username = settingsForm.username
        // 清空密码字段
        settingsForm.newPassword = ''
        settingsForm.confirmPassword = ''
      } catch (error) {
        ElMessage.error('更新失败：' + error.message)
      } finally {
        updating.value = false
      }
    }
  })
}

// API Key 相关方法
const handleAddApiKey = () => {
  apiKeyForm.name = ''
  apiKeyForm.type = ''
  apiKeyForm.key = ''
  apiKeyDialogVisible.value = true
}

const handleSubmitApiKey = async () => {
  if (!apiKeyFormRef.value) return

  await apiKeyFormRef.value.validate(async (valid) => {
    if (valid) {
      addingApiKey.value = true
      try {
        // TODO: 调用添加API
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        apiKeys.value.push({
          id: Date.now(),
          name: apiKeyForm.name,
          type: apiKeyForm.type,
          createTime: new Date().toLocaleString()
        })
        apiKeyDialogVisible.value = false
        ElMessage.success('API Key 添加成功')
      } catch (error) {
        ElMessage.error('添加失败：' + error.message)
      } finally {
        addingApiKey.value = false
      }
    }
  })
}

const handleDeleteApiKey = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.name} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // TODO: 调用删除API
      apiKeys.value = apiKeys.value.filter(item => item.id !== row.id)
      ElMessage.success('删除成功')
    } catch (error) {
      ElMessage.error('删除失败：' + error.message)
    }
  })
}

// 生命周期钩子
onMounted(async () => {
  try {
    // TODO: 获取用户信息和API Keys列表
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  }
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.avatar-container {
  padding: 20px 0;
}

.upload-btn {
  margin-top: 10px;
}

.user-info {
  margin-top: 20px;
}

.user-info h3 {
  margin: 10px 0;
  font-size: 18px;
}

.user-info p {
  color: #909399;
  margin: 0;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-upload) {
  text-align: center;
}

:deep(.el-form-item__content) {
  justify-content: flex-start;
}
</style> 