package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.mapper.CorpusMapper;
import com.computerscience.hdfsapi.service.CorpusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 语料库服务实现类
 */
@Service
public class CorpusServiceImpl extends ServiceImpl<CorpusMapper, Corpus> implements CorpusService {

    @Override
    public Corpus findByName(String collectionName) {
        if (!StringUtils.hasText(collectionName)) {
            return null;
        }
        
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Corpus::getCollectionName, collectionName);
        
        return getOne(queryWrapper);
    }

    @Override
    public IPage<Corpus> findCorpusPage(Integer page, Integer size, String language, String classification) {
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(language)) {
            queryWrapper.eq(Corpus::getLanguage, language);
        }
        
        if (StringUtils.hasText(classification)) {
            queryWrapper.eq(Corpus::getClassification, classification);
        }
        
        queryWrapper.orderByDesc(Corpus::getCreatedAt);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public IPage<Corpus> findUserCorpusPage(Integer userId, Integer page, Integer size, String language, String classification) {
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
        
        // 筛选当前用户的语料
        queryWrapper.eq(Corpus::getCreatorId, userId);
        
        if (StringUtils.hasText(language)) {
            queryWrapper.eq(Corpus::getLanguage, language);
        }
        
        if (StringUtils.hasText(classification)) {
            queryWrapper.eq(Corpus::getClassification, classification);
        }
        
        queryWrapper.orderByDesc(Corpus::getCreatedAt);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional
    public boolean createCorpus(Corpus corpus) {
        // 检查语料库名称是否已存在
        Corpus existingCorpus = findByName(corpus.getCollectionName());
        if (existingCorpus != null) {
            return false;
        }
        
        // 设置创建时间
        corpus.setCreatedAt(LocalDateTime.now());
        
        return save(corpus);
    }

    @Override
    @Transactional
    public boolean updateCorpus(Corpus corpus) {
        if (corpus == null || corpus.getCorpusId() == null) {
            return false;
        }
        
        // 检查语料库是否存在
        Corpus existingCorpus = getById(corpus.getCorpusId());
        if (existingCorpus == null) {
            return false;
        }
        
        return updateById(corpus);
    }

    @Override
    @Transactional
    public boolean deleteCorpus(Integer corpusId) {
        if (corpusId == null) {
            return false;
        }
        
        return removeById(corpusId);
    }
} 