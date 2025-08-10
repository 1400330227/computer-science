package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.Corpus;

import java.util.List;

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
     * 根据用户ID和语料集名称查询语料库
     * @param userId 用户ID
     * @param collectionName 语料集名称
     * @return 语料库信息
     */
    Corpus findByUserIdAndName(Integer userId, String collectionName);
    
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
     * 分页查询指定用户的语料库列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param language 语种（可选）
     * @param classification 数据分类（可选）
     * @param collectionName 语料集名称（可选）
     * @param country 国家（可选）
     * @return 分页语料库列表
     */
    IPage<Corpus> findUserCorpusPage(Integer userId, Integer page, Integer size, String language, String classification, String collectionName, String country, String searchType);
    
    /**
     * [Admin] 分页查询所有语料库
     * @param page 页码
     * @param size 每页大小
     * @param collectionName 语料库名称（可选）
     * @param creatorId 用户ID（可选）
     * @return 分页语料库列表
     */
    IPage<Corpus> findCorpusPageForAdmin(Integer page, Integer size, String collectionName, Integer creatorId);

    /**
     * [Admin] 转移语料库所有权
     * @param corpusIds 语料库ID列表
     * @param targetUserId 目标用户ID
     * @return 是否转移成功
     */
    boolean transferCorpusOwnership(List<Integer> corpusIds, Integer targetUserId);

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