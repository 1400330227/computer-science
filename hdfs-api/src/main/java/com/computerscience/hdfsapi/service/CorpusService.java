package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.Corpus;

/**
 * 语料库服务接口
 */
public interface CorpusService extends IService<Corpus> {
    
    /**
     * 根据名称查询语料库
     * @param collectionName 语料库名称
     * @return 语料库信息
     */
    Corpus findByName(String collectionName);
    
    /**
     * 分页查询语料库列表
     * @param page 页码
     * @param size 每页大小
     * @param language 语种（可选）
     * @param classification 数据分类（可选）
     * @return 分页语料库列表
     */
    IPage<Corpus> findCorpusPage(Integer page, Integer size, String language, String classification);
    
    /**
     * 创建语料库
     * @param corpus 语料库信息
     * @return 是否创建成功
     */
    boolean createCorpus(Corpus corpus);
    
    /**
     * 更新语料库信息
     * @param corpus 语料库信息
     * @return 是否更新成功
     */
    boolean updateCorpus(Corpus corpus);
    
    /**
     * 删除语料库
     * @param corpusId 语料库ID
     * @return 是否删除成功
     */
    boolean deleteCorpus(Integer corpusId);
} 