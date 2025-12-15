package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.AnnotationLabel;
import com.computerscience.hdfsapi.mapper.AnnotationLabelMapper;
import com.computerscience.hdfsapi.service.AnnotationLabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标注标签服务实现类
 */
@Service
public class AnnotationLabelServiceImpl extends ServiceImpl<AnnotationLabelMapper, AnnotationLabel> implements AnnotationLabelService {

    @Override
    public List<AnnotationLabel> findByCategory(String category) {
        LambdaQueryWrapper<AnnotationLabel> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(AnnotationLabel::getCategory, category);
        }
        
        queryWrapper.orderByAsc(AnnotationLabel::getLabelName);
        
        return list(queryWrapper);
    }

    @Override
    public List<AnnotationLabel> findSystemLabels() {
        LambdaQueryWrapper<AnnotationLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnnotationLabel::getIsSystem, true)
                   .orderByAsc(AnnotationLabel::getLabelName);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean createLabel(AnnotationLabel label) {
        if (label == null) {
            return false;
        }
        
        // 设置创建时间
        label.setCreatedAt(LocalDateTime.now());
        
        // 如果不是系统标签，默认为false
        if (label.getIsSystem() == null) {
            label.setIsSystem(false);
        }
        
        return save(label);
    }

    @Override
    @Transactional
    public boolean updateLabel(AnnotationLabel label) {
        if (label == null || label.getLabelId() == null) {
            return false;
        }
        
        // 检查标签是否存在
        AnnotationLabel existingLabel = getById(label.getLabelId());
        if (existingLabel == null) {
            return false;
        }
        
        return updateById(label);
    }

    @Override
    @Transactional
    public boolean deleteLabel(Integer labelId) {
        if (labelId == null) {
            return false;
        }
        
        // 检查是否为系统标签，系统标签不允许删除
        AnnotationLabel label = getById(labelId);
        if (label != null && Boolean.TRUE.equals(label.getIsSystem())) {
            return false;
        }
        
        return removeById(labelId);
    }
}


