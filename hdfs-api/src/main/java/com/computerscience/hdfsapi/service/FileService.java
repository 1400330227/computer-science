package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.FileEntity;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService extends IService<FileEntity> {
    
    /**
     * 根据文件名查询文件
     * @param fileName 文件名
     * @return 文件信息
     */
    FileEntity findByFileName(String fileName);
    
    /**
     * 根据语料库ID查询文件列表
     * @param corpusId 语料库ID
     * @return 文件列表
     */
    List<FileEntity> findByCorpusId(Integer corpusId);
    
    /**
     * 根据创建者ID查询文件列表
     * @param creatorId 创建者ID
     * @return 文件列表
     */
    List<FileEntity> findByCreatorId(Integer creatorId);
    
    /**
     * 根据创建者ID分页查询文件列表
     * @param creatorId 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页文件列表
     */
    IPage<FileEntity> findByCreatorIdPage(Integer creatorId, Integer page, Integer size);
    
    /**
     * 分页查询文件列表
     * @param page 页码
     * @param size 每页大小
     * @param fileType 文件类型（可选）
     * @param corpusId 语料库ID（可选）
     * @return 分页文件列表
     */
    IPage<FileEntity> findFilePage(Integer page, Integer size, String fileType, Integer corpusId);
    
    /**
     * 创建文件
     * @param fileEntity 文件信息
     * @return 是否创建成功
     */
    boolean createFile(FileEntity fileEntity);
    
    /**
     * 更新文件信息
     * @param fileEntity 文件信息
     * @return 是否更新成功
     */
    boolean updateFile(FileEntity fileEntity);
    
    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Integer fileId);
    
    /**
     * 根据语料库ID删除所有相关文件
     * @param corpusId 语料库ID
     * @return 是否删除成功
     */
    boolean deleteFilesByCorpusId(Integer corpusId);
} 