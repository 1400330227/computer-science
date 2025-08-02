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
    public IPage<FileEntity> findFilePage(Integer page, Integer size, String fileType, Integer corpusId) {
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(fileType)) {
            queryWrapper.eq(FileEntity::getFileType, fileType);
        }
        
        if (corpusId != null) {
            queryWrapper.eq(FileEntity::getCorpusId, corpusId);
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
} 