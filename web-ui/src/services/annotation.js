import api from './api'

/**
 * 标注相关API
 */
export const annotationApi = {
  // 获取标注列表
  getAnnotations(params) {
    return api.get('/annotation', { params })
  },

  // 根据ID获取标注
  getAnnotationById(id) {
    return api.get(`/annotation/${id}`)
  },

  // 根据语料ID获取标注列表
  getAnnotationsByCorpusId(corpusId) {
    return api.get(`/annotation/corpus/${corpusId}`)
  },

  // 根据文件ID获取标注列表
  getAnnotationsByFileId(fileId) {
    return api.get(`/annotation/file/${fileId}`)
  },

  // 创建标注
  createAnnotation(data) {
    return api.post('/annotation', data)
  },

  // 更新标注
  updateAnnotation(id, data) {
    return api.put(`/annotation/${id}`, data)
  },

  // 删除标注
  deleteAnnotation(id) {
    return api.delete(`/annotation/${id}`)
  },

  // 统计语料的标注数量
  countAnnotationsByCorpusId(corpusId) {
    return api.get(`/annotation/corpus/${corpusId}/count`)
  },

  // ========== 标注标签相关 ==========

  // 获取所有标签
  getLabels(category) {
    const params = category ? { category } : {}
    return api.get('/annotation/labels', { params })
  },

  // 获取系统默认标签
  getSystemLabels() {
    return api.get('/annotation/labels/system')
  },

  // 创建标签
  createLabel(data) {
    return api.post('/annotation/labels', data)
  },

  // 更新标签
  updateLabel(id, data) {
    return api.put(`/annotation/labels/${id}`, data)
  },

  // 删除标签
  deleteLabel(id) {
    return api.delete(`/annotation/labels/${id}`)
  }
}

import axios from './api'; // 引入封装好的axios实例

/**
 * 上传问答对标注文件
 * @param {File} file - 用户选择的 .txt 文件
 * @param {number} originalFileId - 关联的原始文件ID
 * @param {Function} onUploadProgress - 一个可选的回调函数，用于跟踪上传进度
 * @returns {Promise}
 */
export const uploadQaAnnotationFile = (file, originalFileId, onUploadProgress) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('originalFileId', originalFileId);

    return axios.post('/qa-annotations/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress
    });
};

