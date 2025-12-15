package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.AnnotationLabel;

import java.util.List;

/**
 * 标注标签服务接口
 */
public interface AnnotationLabelService extends IService<AnnotationLabel> {
    
    /**
     * 根据分类查询标签列表
     * @param category 分类
     * @return 标签列表
     */
    List<AnnotationLabel> findByCategory(String category);
    
    /**
     * 查询所有系统默认标签
     * @return 标签列表
     */
    List<AnnotationLabel> findSystemLabels();
    
    /**
     * 创建标签
     * @param label 标签信息
     * @return 是否创建成功
     */
    boolean createLabel(AnnotationLabel label);
    
    /**
     * 更新标签
     * @param label 标签信息
     * @return 是否更新成功
     */
    boolean updateLabel(AnnotationLabel label);
    
    /**
     * 删除标签
     * @param labelId 标签ID
     * @return 是否删除成功
     */
    boolean deleteLabel(Integer labelId);
}


