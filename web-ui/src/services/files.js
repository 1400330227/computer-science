import api from './api';

// 获取用户的所有文件（支持筛选）
export const getUserFiles = (page = 1, size = 10, filters = {}) => {
  const params = { page, size, ...filters };
  // 移除空值
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) {
      delete params[key];
    }
  });
  
  return api.get('/files/my-files', { params });
};
  
// 删除用户文件
export const deleteUserFile = (fileId) => {
  return api.delete(`/files/${fileId}`);
};
  
// 获取文件下载URL
export const getFileDownloadUrl = (fileId) => {
    return `${api.defaults.baseURL}/files/download/${fileId}`;
};

// 批量下载文件
export const batchDownloadFiles = async (fileIds) => {
  try {
    const response = await api.post('/files/batch-download', fileIds, {
      responseType: 'blob', // 重要：指定响应类型为blob来处理二进制数据
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // 创建blob URL并触发下载
    const blob = new Blob([response.data], { type: 'application/zip' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    // 从响应头中获取文件名，或使用默认文件名
    const contentDisposition = response.headers['content-disposition'];
    let fileName = 'batch_download.zip';
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = decodeURIComponent(fileNameMatch[1].replace(/['"]/g, ''));
      }
    }
    
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    
    // 清理
    window.URL.revokeObjectURL(url);
    document.body.removeChild(link);
    
    return response;
  } catch (error) {
    console.error('批量下载失败:', error);
    throw error;
  }
};