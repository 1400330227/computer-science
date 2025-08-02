import api from './api';

// 获取用户的所有文件
export const getUserFiles = () => {
    return api.get('/files/my-files');
  };
  
  // 删除用户文件
  export const deleteUserFile = (fileId) => {
    return api.delete(`/files/${fileId}`);
  };
  
  // 获取文件下载URL
  export const getFileDownloadUrl = (fileId) => {
    return `${api.defaults.baseURL}/files/download/${fileId}`;
  };