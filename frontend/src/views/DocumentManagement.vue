<template>
  <div class="document-container">
    <el-tabs v-model="activeTab">
      <!-- 需求文档管理 -->
      <el-tab-pane label="需求文档" name="requirements">
        <div class="action-bar">
          <el-upload
            class="upload-button"
            action="/api/outline/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <el-button type="primary">
              <el-icon><upload-filled /></el-icon>
              上传需求文档
            </el-button>
          </el-upload>
        </div>

        <el-table :data="requirementList" style="width: 100%" v-loading="loading">
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="fileSize" label="大小">
            <template #default="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="fileType" label="类型" width="100" />
          <el-table-column prop="createTime" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button-group>
                <el-button
                  type="primary"
                  link
                  @click="handleDownload(scope.row.id, 'REQUIREMENT')"
                >
                  <el-icon><download /></el-icon>
                  下载
                </el-button>
                <el-button
                  type="primary"
                  link
                  @click="handleGenerate(scope.row)"
                >
                  <el-icon><magic-stick /></el-icon>
                  生成大纲
                </el-button>
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(scope.row.id, 'REQUIREMENT')"
                >
                  <el-icon><delete /></el-icon>
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 测试大纲管理 -->
      <el-tab-pane label="测试大纲" name="outlines">
        <el-table :data="outlineList" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="requirement.fileName" label="关联需求" />
          <el-table-column prop="model" label="使用模型" width="120" />
          <el-table-column prop="createTime" label="生成时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button-group>
                <el-button
                  type="primary"
                  link
                  @click="handlePreview(scope.row)"
                >
                  <el-icon><view /></el-icon>
                  预览
                </el-button>
                <el-button
                  type="primary"
                  link
                  @click="handleDownload(scope.row.id, 'OUTLINE')"
                >
                  <el-icon><download /></el-icon>
                  下载
                </el-button>
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(scope.row.id, 'OUTLINE')"
                >
                  <el-icon><delete /></el-icon>
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialog"
      title="测试大纲预览"
      width="60%"
      :before-close="handleClosePreview"
    >
      <div class="preview-content" v-html="currentOutline.content"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClosePreview">关闭</el-button>
          <el-button type="primary" @click="handleDownload(currentOutline.id, 'OUTLINE')">
            导出Word
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  UploadFilled,
  Download,
  Delete,
  View,
  MagicStick
} from '@element-plus/icons-vue'
import {
  uploadRequirement,
  getRequirementList,
  getOutlineList,
  downloadDocument,
  deleteDocument
} from '@/api/outline'
import { formatDate, formatFileSize } from '@/utils/format'

// 状态变量
const activeTab = ref('requirements')
const loading = ref(false)
const requirementList = ref([])
const outlineList = ref([])
const previewDialog = ref(false)
const currentOutline = ref({})

// 初始化数据
onMounted(async () => {
  await loadDocuments()
})

// 加载文档列表
const loadDocuments = async () => {
  loading.value = true
  try {
    const [requirements, outlines] = await Promise.all([
      getRequirementList(),
      getOutlineList()
    ])
    requirementList.value = requirements
    outlineList.value = outlines
  } catch (error) {
    ElMessage.error('加载文档列表失败：' + error.message)
  } finally {
    loading.value = false
  }
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

const handleUploadSuccess = async (response) => {
  ElMessage.success('文件上传成功')
  await loadDocuments()
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

// 下载文档
const handleDownload = async (id, type) => {
  try {
    const response = await downloadDocument(id, type)
    const blob = new Blob([response], {
      type: type === 'OUTLINE'
        ? 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        : 'application/octet-stream'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = type === 'OUTLINE' ? '测试大纲.docx' : '需求文档.pdf'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    ElMessage.error('下载失败：' + error.message)
  }
}

// 删除文档
const handleDelete = async (id, type) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该文档吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteDocument(id, type)
    ElMessage.success('删除成功')
    await loadDocuments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 生成测试大纲
const handleGenerate = (requirement) => {
  // 跳转到生成页面，并传递需求文档信息
  router.push({
    name: 'OutlineGeneration',
    query: { requirementId: requirement.id }
  })
}

// 预览测试大纲
const handlePreview = (outline) => {
  currentOutline.value = outline
  previewDialog.value = true
}

const handleClosePreview = () => {
  previewDialog.value = false
  currentOutline.value = {}
}
</script>

<style scoped>
.document-container {
  padding: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.preview-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 20px;
}

:deep(.el-upload) {
  width: auto;
}

:deep(.el-table) {
  margin-top: 20px;
}
</style> 