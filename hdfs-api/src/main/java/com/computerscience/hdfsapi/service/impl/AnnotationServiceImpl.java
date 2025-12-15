package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.Annotation;
import com.computerscience.hdfsapi.mapper.AnnotationMapper;
import com.computerscience.hdfsapi.service.AnnotationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标注服务实现类
 */
@Service
public class AnnotationServiceImpl extends ServiceImpl<AnnotationMapper, Annotation> implements AnnotationService {

    @Override
    public List<Annotation> findByCorpusId(Integer corpusId) {
        if (corpusId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Annotation::getCorpusId, corpusId)
                   .orderByDesc(Annotation::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public List<Annotation> findByFileId(Integer fileId) {
        if (fileId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Annotation::getFileId, fileId)
                   .orderByDesc(Annotation::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public IPage<Annotation> findAnnotationPage(Integer page, Integer size, Integer corpusId, Integer fileId, String annotationType, Integer creatorId) {
        LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
        
        if (corpusId != null) {
            queryWrapper.eq(Annotation::getCorpusId, corpusId);
        }
        
        if (fileId != null) {
            queryWrapper.eq(Annotation::getFileId, fileId);
        }
        
        if (StringUtils.hasText(annotationType)) {
            queryWrapper.eq(Annotation::getAnnotationType, annotationType);
        }
        
        if (creatorId != null) {
            queryWrapper.eq(Annotation::getCreatorId, creatorId);
        }
        
        queryWrapper.orderByDesc(Annotation::getCreatedAt);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional
    public boolean createAnnotation(Annotation annotation) {
        if (annotation == null) {
            return false;
        }
        
        // 设置创建时间
        annotation.setCreatedAt(LocalDateTime.now());
        annotation.setUpdatedAt(LocalDateTime.now());
        
        // 如果没有设置状态，默认为"待审核"
        if (!StringUtils.hasText(annotation.getStatus())) {
            annotation.setStatus("待审核");
        }
        
        return save(annotation);
    }

    @Override
    @Transactional
    public boolean updateAnnotation(Annotation annotation) {
        if (annotation == null || annotation.getAnnotationId() == null) {
            return false;
        }
        
        // 检查标注是否存在
        Annotation existingAnnotation = getById(annotation.getAnnotationId());
        if (existingAnnotation == null) {
            return false;
        }
        
        // 设置更新时间
        annotation.setUpdatedAt(LocalDateTime.now());
        
        return updateById(annotation);
    }

    @Override
    @Transactional
    public boolean deleteAnnotation(Integer annotationId) {
        if (annotationId == null) {
            return false;
        }
        
        return removeById(annotationId);
    }

    @Override
    public Long countByCorpusId(Integer corpusId) {
        if (corpusId == null) {
            return 0L;
        }
        
        LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Annotation::getCorpusId, corpusId);
        
        return count(queryWrapper);
    }
}


