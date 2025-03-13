<template>
  <div class="user-profile">
    <el-row :gutter="20" class="main-row">
      <!-- 个人信息部分 -->
      <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="4">
        <el-card class="profile-section">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEdit" v-if="!isEditing">编辑</el-button>
              <div v-else>
                <el-button type="success" @click="handleSave">保存</el-button>
                <el-button @click="handleCancel">取消</el-button>
              </div>
            </div>
          </template>
          
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
              <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>

          <el-form :model="userForm" :disabled="!isEditing" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧部分 -->
      <el-col :xs="24" :sm="24" :md="18" :lg="18" :xl="20">
        <!-- 安全设置部分 -->
        <el-card class="security-section">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
            </div>
          </template>
          
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- API密钥管理部分 -->
        <el-card class="api-keys-section mt-4">
          <template #header>
            <div class="card-header">
              <span>API密钥管理</span>
              <el-button type="primary" @click="handleCreateApiKey">创建密钥</el-button>
            </div>
          </template>
          
          <el-table :data="apiKeys" style="width: 100%">
            <el-table-column prop="name" label="名称" min-width="120"/>
            <el-table-column prop="keyValue" label="密钥" min-width="300">
              <template #default="{ row }">
                <div class="key-value-cell">
                  <el-input :value="row.keyValue" :show-password="true" readonly />
                  <el-button type="primary" link @click="copyApiKey(row.keyValue)">
                    复制
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDeleteApiKey(row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 创建API密钥对话框 -->
    <el-dialog
      v-model="createKeyDialogVisible"
      title="创建API密钥"
      width="30%">
      <el-form :model="newKeyForm" ref="newKeyFormRef">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入密钥名称' }]">
          <el-input v-model="newKeyForm.name" placeholder="请输入密钥名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createKeyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCreateApiKey">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, changePassword, uploadAvatar } from '@/api/user'
import { getApiKeys, createApiKey, deleteApiKey } from '@/api/apiKey'
import { Plus } from '@element-plus/icons-vue'

// 用户表单数据
const userForm = ref({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})

// 编辑状态
const isEditing = ref(false)
const originalUserForm = ref(null)

// 密码表单数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不能小于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }
    }
  ]
}

// API密钥相关
const apiKeys = ref([])
const createKeyDialogVisible = ref(false)
const newKeyForm = ref({ name: '' })

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const { data } = await getUserProfile()
    userForm.value = data
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 获取API密钥列表
const fetchApiKeys = async () => {
  try {
    const { data } = await getApiKeys()
    apiKeys.value = data
  } catch (error) {
    ElMessage.error('获取API密钥列表失败')
  }
}

// 编辑用户信息
const handleEdit = () => {
  originalUserForm.value = { ...userForm.value }
  isEditing.value = true
}

// 保存用户信息
const handleSave = async () => {
  try {
    await updateUserProfile(userForm.value)
    isEditing.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 取消编辑
const handleCancel = () => {
  userForm.value = { ...originalUserForm.value }
  isEditing.value = false
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await changePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

// 上传头像
const uploadUrl = '/api/user/avatar'
const headers = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

const handleAvatarSuccess = (response) => {
  userForm.value.avatar = response
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 创建API密钥
const handleCreateApiKey = () => {
  createKeyDialogVisible.value = true
  newKeyForm.value.name = ''
}

const confirmCreateApiKey = async () => {
  try {
    await createApiKey(newKeyForm.value.name)
    createKeyDialogVisible.value = false
    ElMessage.success('API密钥创建成功')
    fetchApiKeys()
  } catch (error) {
    ElMessage.error('API密钥创建失败')
  }
}

// 删除API密钥
const handleDeleteApiKey = async (keyId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个API密钥吗？', '提示', {
      type: 'warning'
    })
    await deleteApiKey(keyId)
    ElMessage.success('删除成功')
    fetchApiKeys()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 复制API密钥
const copyApiKey = async (keyValue) => {
  try {
    await navigator.clipboard.writeText(keyValue)
    ElMessage.success('复制成功')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  fetchUserProfile()
  fetchApiKeys()
})
</script>

<style scoped>
.user-profile {
  width: 100%;
  min-height: 100%;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  background-color: #f5f7fa;
}

.main-row {
  width: 100%;
  max-width: 1600px; /* 设置最大宽度 */
  margin: 0 auto !important;
  padding: 20px;
  box-sizing: border-box;
}

/* 响应式布局调整 */
@media screen and (max-width: 1920px) {
  .main-row {
    max-width: 1600px;
  }
}

@media screen and (max-width: 1600px) {
  .main-row {
    max-width: 1400px;
  }
}

@media screen and (max-width: 1440px) {
  .main-row {
    max-width: 1200px;
  }
}

@media screen and (max-width: 1280px) {
  .main-row {
    max-width: 1000px;
  }
}

@media screen and (max-width: 1024px) {
  .main-row {
    max-width: 900px;
    padding: 15px;
  }
}

.profile-section,
.security-section,
.api-keys-section {
  width: 100%;
  margin-bottom: 20px;
  box-sizing: border-box;
}

:deep(.el-card) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

/* 调整表格在不同宽度下的显示 */
.api-keys-section :deep(.el-table) {
  width: 100% !important;
}

.api-keys-section :deep(.el-table__body) {
  width: 100% !important;
}

/* 确保列宽合理分配 */
:deep(.el-table .el-table__cell) {
  padding: 8px;
}

:deep(.el-table .key-value-cell) {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    width: 100px;
    height: 100px;
    margin: 0 auto;
  }

  :deep(.el-upload:hover) {
    border-color: var(--el-color-primary);
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.key-value-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.key-value-cell .el-input {
  flex: 1;
}

.mt-4 {
  margin-top: 1rem;
}

@media screen and (max-width: 768px) {
  .user-profile {
    padding: 10px;
  }
  
  .profile-section,
  .security-section,
  .api-keys-section {
    margin-bottom: 15px;
  }

  .key-value-cell {
    flex-direction: column;
    gap: 5px;
  }

  .key-value-cell .el-button {
    width: 100%;
    margin-left: 0;
  }
}
</style> 