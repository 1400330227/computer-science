package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.AnnotationLabel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标注标签数据访问层
 */
@Mapper
public interface AnnotationLabelMapper extends BaseMapper<AnnotationLabel> {
}


