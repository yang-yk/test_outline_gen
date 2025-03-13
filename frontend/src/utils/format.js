// 格式化日期
export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

// 格式化文件大小
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化文件名
export function formatFileName(fileName, maxLength = 20) {
  if (!fileName) return ''
  if (fileName.length <= maxLength) return fileName
  const ext = fileName.split('.').pop()
  const name = fileName.substring(0, fileName.length - ext.length - 1)
  const truncatedName = name.substring(0, maxLength - ext.length - 3) + '...'
  return `${truncatedName}.${ext}`
} 