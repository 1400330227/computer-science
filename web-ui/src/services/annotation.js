import api from './api'

/**
 * 标注相关API
 */

// 上传标注文件
export function uploadAnnotation(formData) {
  return api.post('/annotation-files/upload', formData, {
    headers: {'Content-Type': 'multipart/form-data'}
  })
}

// 删除标注文件
export function deleteAnnotation(annotationId) {
  return api.delete(`/annotation-files/${annotationId}`)
}


// 获取全部标注下载链接 (用于 window.location 或 a 标签)
export function getAllAnnotationsDownloadUrl(corpusId) {
  return `/api/annotation-files/download-all/${corpusId}`
}


// 获取单个标注下载链接
export function getSingleAnnotationDownloadUrl(annotationId) {
  return `/api/annotation-files/download/${annotationId}`
}



