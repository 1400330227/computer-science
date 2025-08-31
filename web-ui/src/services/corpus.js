import api from './api';
import { ElMessage } from 'element-plus';

/**
 * 处理API响应
 * @param {Promise} request - API请求Promise
 * @returns {Promise}
 */
const handleApiResponse = async (request) => {
  try {
    const response = await request;
    return response;
  } catch (error) {
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error('网络请求失败，请检查网络连接');
    }
    throw error;
  }
};

/**
 * 获取语料库列表
 * @param {object} params - 查询参数 { page, size, language, classification }
 * @returns {Promise}
 */
export const getCorpusList = (params) => {
  return handleApiResponse(api.get('/corpus', { params }));
};

/**
 * 获取当前用户的语料库列表
 * @param {object} params - 查询参数 { page, size, language, classification, collectionName, country, searchType }
 * @returns {Promise}
 */
export const getMyCorpusList = (params) => {
  return handleApiResponse(api.get('/corpus/my-corpus', { params }));
};

/**
 * 根据ID获取语料库详情
 * @param {number} id - 语料库ID
 * @returns {Promise}
 */
export const getCorpusById = (id) => {
  return handleApiResponse(api.get(`/corpus/${id}`));
};

/**
 * 根据ID获取当前用户的语料库详情
 * @param {number} id - 语料库ID
 * @returns {Promise}
 */
export const getUserCorpusById = (id) => {
  return handleApiResponse(api.get(`/corpus/user/${id}`));
};

/**
 * 创建语料库
 * @param {object} data - 语料库数据
 * @returns {Promise}
 */
export const createCorpus = (data) => {
  return handleApiResponse(api.post('/corpus', data));
};

/**
 * 更新语料库信息
 * @param {number} id - 语料库ID
 * @param {object} data - 更新的语料库数据
 * @returns {Promise}
 */
export const updateCorpus = (id, data) => {
  return handleApiResponse(api.put(`/corpus/${id}`, data));
};

/**
 * 删除语料库
 * @param {number} id - 语料库ID
 * @returns {Promise}
 */
export const deleteCorpus = (id) => {
  return handleApiResponse(api.delete(`/corpus/${id}`));
};

/**
 * 上传文件到语料库
 * @param {FormData} formData - 包含文件和语料库ID的表单数据
 * @returns {Promise}
 */
export const uploadFileToCorpus = (formData) => {
  return handleApiResponse(api.post('/corpus/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }));
};

/**
 * 下载语料库文件
 * @param {number} corpusId - 语料库ID
 * @returns {string} 下载URL
 */
export const getCorpusDownloadUrl = (corpusId) => {
  return `/api/corpus/download/${corpusId}`;
}; 