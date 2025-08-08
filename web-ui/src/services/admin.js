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
    if (response.data.success) {
      return response;
    } else {
      ElMessage.error(response.data.message || 'API请求失败');
      throw new Error(response.data.message || 'API请求失败');
    }
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
 * Fetches a paginated list of users.
 * @param {object} params - Query parameters { page, size, account }.
 * @returns {Promise}
 */
export const getUsers = (params) => {
  return handleApiResponse(api.get('/admin/users', { params }));
};

/**
 * Fetches a paginated list of corpora.
 * @param {object} params - Query parameters { page, size, collectionName, creatorAccount }.
 * @returns {Promise}
 */
export const getCorpora = (params) => {
  return handleApiResponse(api.get('/admin/corpus', { params }));
};

/**
 * Transfers ownership of corpora to a new user.
 * @param {number[]} corpusIds - Array of corpus IDs.
 * @param {number} targetUserId - The new owner's user ID.
 * @returns {Promise}
 */
export const transferCorpus = (corpusIds, targetUserId) => {
  return handleApiResponse(
    api.post('/admin/corpus/transfer', { corpusIds, targetUserId })
  );
};

/**
 * Updates a user's role (user type).
 * @param {number} userId - The user ID.
 * @param {string} userType - The new user type ('user' or 'admin').
 * @returns {Promise}
 */
export const updateUserRole = (userId, userType) => {
  return handleApiResponse(
    api.post(`/admin/users/${userId}/update-role`, { userType })
  );
}; 