package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.Annotation;

import java.util.List;

/**
 * 标注服务接口
 */
public interface AnnotationService extends IService<Annotation> {
    
    /**
     * 根据语料ID查询标注列表
     * @param corpusId 语料ID
     * @return 标注列表
     */
    List<Annotation> findByCorpusId(Integer corpusId);
    
    /**
     * 根据文件ID查询标注列表
     * @param fileId 文件ID
     * @return 标注列表
     */
    List<Annotation> findByFileId(Integer fileId);
    
    /**
     * 分页查询标注列表
     * @param page 页码
     * @param size 每页大小
     * @param corpusId 语料ID（可选）
     * @param fileId 文件ID（可选）
     * @param annotationType 标注类型（可选）
     * @param creatorId 创建者ID（可选）
     * @return 分页标注列表
     */
    IPage<Annotation> findAnnotationPage(Integer page, Integer size, Integer corpusId, Integer fileId, String annotationType, Integer creatorId);
    
    /**
     * 创建标注
     * @param annotation 标注信息
     * @return 是否创建成功
     */
    boolean createAnnotation(Annotation annotation);
    
    /**
     * 更新标注
     * @param annotation 标注信息
     * @return 是否更新成功
     */
    boolean updateAnnotation(Annotation annotation);
    
    /**
     * 删除标注
     * @param annotationId 标注ID
     * @return 是否删除成功
     */
    boolean deleteAnnotation(Integer annotationId);
    
    /**
     * 根据语料ID统计标注数量
     * @param corpusId 语料ID
     * @return 标注数量
     */
    Long countByCorpusId(Integer corpusId);
}


