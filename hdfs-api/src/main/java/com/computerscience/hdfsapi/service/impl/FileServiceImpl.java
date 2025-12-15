package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.mapper.FileMapper;
import com.computerscience.hdfsapi.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    @Override
    public FileEntity findByFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return null;
        }
        
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getFileName, fileName);
        
        return getOne(queryWrapper);
    }

    @Override
    public List<FileEntity> findByCorpusId(Integer corpusId) {
        if (corpusId == null) {
            return null;
        }
        
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getCorpusId, corpusId);
        queryWrapper.inSql(FileEntity::getFileId,
                "SELECT MAX(file_id) FROM files WHERE corpus_id = " + corpusId + " GROUP BY file_path"
        );
        queryWrapper.orderByDesc(FileEntity::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public List<FileEntity> findByCreatorId(Integer creatorId) {
        if (creatorId == null) {
            return null;
        }
        
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getCreatorId, creatorId);
        queryWrapper.orderByDesc(FileEntity::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public IPage<FileEntity> findFilePage(Integer page, Integer size, Integer creatorId, String fileType, 
                                         Integer corpusId, String fileName, String dataFormat, 
                                         List<Integer> filteredCorpusIds) {
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 创建者ID筛选
        if (creatorId != null) {
            queryWrapper.eq(FileEntity::getCreatorId, creatorId);
        }
        
        // 文件类型筛选
        if (StringUtils.hasText(fileType)) {
            queryWrapper.eq(FileEntity::getFileType, fileType);
        }
        
        // 语料库ID筛选
        if (corpusId != null) {
            queryWrapper.eq(FileEntity::getCorpusId, corpusId);
        }
        
        // 文件名模糊搜索
        if (StringUtils.hasText(fileName)) {
            queryWrapper.like(FileEntity::getFileName, fileName);
        }
        
        // 数据格式筛选
        if (StringUtils.hasText(dataFormat)) {
            queryWrapper.eq(FileEntity::getDataFormat, dataFormat);
        }
        
        // 根据语料条件筛选的文件ID
        if (filteredCorpusIds != null && !filteredCorpusIds.isEmpty()) {
            queryWrapper.in(FileEntity::getCorpusId, filteredCorpusIds);
        }
        
        queryWrapper.orderByDesc(FileEntity::getCreatedAt);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional
    public boolean createFile(FileEntity fileEntity) {
        if (fileEntity == null) {
            return false;
        }
        
        // 设置创建时间
        fileEntity.setCreatedAt(LocalDateTime.now());
        if (fileEntity.getUpdatedAt() == null) {
            fileEntity.setUpdatedAt(LocalDateTime.now());
        }
        
        return save(fileEntity);
    }

    @Override
    @Transactional
    public boolean updateFile(FileEntity fileEntity) {
        if (fileEntity == null || fileEntity.getFileId() == null) {
            return false;
        }
        
        // 检查文件是否存在
        FileEntity existingFile = getById(fileEntity.getFileId());
        if (existingFile == null) {
            return false;
        }
        
        // 设置更新时间
        fileEntity.setUpdatedAt(LocalDateTime.now());
        
        return updateById(fileEntity);
    }

    @Override
    @Transactional
    public boolean deleteFile(Integer fileId) {
        if (fileId == null) {
            return false;
        }
        
        return removeById(fileId);
    }
    
    @Override
    @Transactional
    public boolean deleteFilesByCorpusId(Integer corpusId) {
        if (corpusId == null) {
            return false;
        }
        
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getCorpusId, corpusId);
        
        return remove(queryWrapper);
    }

    @Override
    public IPage<FileEntity> findAllFiles(int page, int size, String annotationStatus) {
        Page<FileEntity> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(annotationStatus)) {
            queryWrapper.eq(FileEntity::getAnnotationStatus, annotationStatus);
        }
        
        queryWrapper.orderByDesc(FileEntity::getCreatedAt);
        return this.page(pageRequest, queryWrapper);
    }

    @Override
    public IPage<FileEntity> findFilesByCreator(Integer creatorId, int page, int size, String annotationStatus) {
        Page<FileEntity> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileEntity::getCreatorId, creatorId);

        if (StringUtils.hasText(annotationStatus)) {
            queryWrapper.eq(FileEntity::getAnnotationStatus, annotationStatus);
        }

        queryWrapper.orderByDesc(FileEntity::getCreatedAt);
        return this.page(pageRequest, queryWrapper);
    }
} 