import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  uploadRequirement,
  generateOutline,
  getOutlineList,
  getRequirementList,
  deleteDocument,
  downloadDocument,
  saveOutline,
  getOutlineDetail
} from '../api/outline'

export const useOutlineStore = defineStore('outline', () => {
  const outlineList = ref([])
  const requirementList = ref([])
  const currentOutline = ref(null)
  const generating = ref(false)

  // 上传需求规格说明书
  async function uploadRequirementAction(file) {
    try {
      const res = await uploadRequirement(file)
      return res
    } catch (error) {
      throw error
    }
  }

  // 生成测试大纲
  async function generateOutlineAction(data) {
    try {
      generating.value = true
      const res = await generateOutline(data)
      currentOutline.value = res
      return res
    } catch (error) {
      throw error
    } finally {
      generating.value = false
    }
  }

  // 获取测试大纲列表
  async function loadOutlineList() {
    try {
      const res = await getOutlineList()
      outlineList.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取需求规格说明书列表
  async function loadRequirementList() {
    try {
      const res = await getRequirementList()
      requirementList.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 删除文档
  async function deleteDocumentAction(id, type) {
    try {
      await deleteDocument(id, type)
      if (type === 'requirement') {
        requirementList.value = requirementList.value.filter(item => item.id !== id)
      } else {
        outlineList.value = outlineList.value.filter(item => item.id !== id)
      }
    } catch (error) {
      throw error
    }
  }

  // 下载文档
  async function downloadDocumentAction(id, type) {
    try {
      const res = await downloadDocument(id, type)
      // 创建下载链接
      const blob = new Blob([res], { type: 'application/octet-stream' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${type === 'requirement' ? '需求说明书' : '测试大纲'}_${id}.docx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      throw error
    }
  }

  // 保存测试大纲
  async function saveOutlineAction(data) {
    try {
      const res = await saveOutline(data)
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取测试大纲详情
  async function loadOutlineDetail(id) {
    try {
      const res = await getOutlineDetail(id)
      currentOutline.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  return {
    outlineList,
    requirementList,
    currentOutline,
    generating,
    uploadRequirementAction,
    generateOutlineAction,
    loadOutlineList,
    loadRequirementList,
    deleteDocumentAction,
    downloadDocumentAction,
    saveOutlineAction,
    loadOutlineDetail
  }
}) 