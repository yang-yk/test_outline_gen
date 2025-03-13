import request from '../utils/request'

// 上传需求规格说明书
export function uploadRequirement(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/outline/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 生成测试大纲
export function generateOutline(data) {
  return request({
    url: '/outline/generate',
    method: 'post',
    data
  })
}

// 获取测试大纲列表
export function getOutlineList() {
  return request({
    url: '/outline/list',
    method: 'get'
  })
}

// 获取需求规格说明书列表
export function getRequirementList() {
  return request({
    url: '/outline/requirement/list',
    method: 'get'
  })
}

// 删除文档（需求规格说明书或测试大纲）
export function deleteDocument(id, type) {
  return request({
    url: `/outline/delete/${id}`,
    method: 'delete',
    params: { type }
  })
}

// 下载文档
export function downloadDocument(id, type) {
  return request({
    url: `/outline/download/${id}`,
    method: 'get',
    params: { type },
    responseType: 'blob'
  })
}

// 保存测试大纲
export function saveOutline(id, content) {
  return request({
    url: `/outline/save/${id}`,
    method: 'put',
    data: content
  })
}

// 获取测试大纲详情
export function getOutlineDetail(id) {
  return request({
    url: `/outline/detail/${id}`,
    method: 'get'
  })
} 