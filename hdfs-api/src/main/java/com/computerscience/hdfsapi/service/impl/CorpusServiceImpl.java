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
import java.util.List;

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
    public Corpus findByUserIdAndName(Integer userId, String collectionName) {
        if (userId == null || !StringUtils.hasText(collectionName)) {
            return null;
        }
        
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Corpus::getCreatorId, userId)
                   .eq(Corpus::getCollectionName, collectionName);
        
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
    public IPage<Corpus> findUserCorpusPage(Integer userId, Integer page, Integer size, String language, String classification, String collectionName, String country) {
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
        
        // 筛选当前用户的语料
        queryWrapper.eq(Corpus::getCreatorId, userId);
        
        if (StringUtils.hasText(language)) {
            queryWrapper.eq(Corpus::getLanguage, language);
        }
        
        if (StringUtils.hasText(classification)) {
            queryWrapper.eq(Corpus::getClassification, classification);
        }
        
        // 根据语料集名称筛选（模糊查询）
        if (StringUtils.hasText(collectionName)) {
            queryWrapper.like(Corpus::getCollectionName, collectionName);
        }
        
        // 根据国家筛选
        if (StringUtils.hasText(country)) {
            queryWrapper.eq(Corpus::getCountry, country);
        }
        
        queryWrapper.orderByDesc(Corpus::getCreatedAt);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public IPage<Corpus> findCorpusPageForAdmin(Integer page, Integer size, String collectionName, Integer creatorId) {
        LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(collectionName)) {
            queryWrapper.like(Corpus::getCollectionName, collectionName);
        }

        if (creatorId != null) {
            queryWrapper.eq(Corpus::getCreatorId, creatorId);
        }

        queryWrapper.orderByDesc(Corpus::getCreatedAt);

        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional
    public boolean transferCorpusOwnership(List<Integer> corpusIds, Integer targetUserId) {
        if (corpusIds == null || corpusIds.isEmpty() || targetUserId == null) {
            return false;
        }
        
        // Note: In a real-world scenario, you should also validate if the targetUser exists.
        // This is omitted for brevity here but recommended for production code.

        List<Corpus> corporaToUpdate = listByIds(corpusIds);
        if (corporaToUpdate.size() != corpusIds.size()) {
            // This means some of the provided corpus IDs were not found, which might indicate an issue.
            // Depending on requirements, you might want to throw an exception here.
            return false;
        }
        
        for (Corpus corpus : corporaToUpdate) {
            corpus.setCreatorId(targetUserId);
        }

        return updateBatchById(corporaToUpdate);
    }

    @Override
    @Transactional
    public boolean createCorpus(Corpus corpus) {
        // 检查当前用户是否已有同名语料库
        Corpus existingCorpus = findByUserIdAndName(corpus.getCreatorId(), corpus.getCollectionName());
        if (existingCorpus != null) {
            System.out.println("用户 " + corpus.getCreatorId() + " 已存在同名语料库: " + corpus.getCollectionName());
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