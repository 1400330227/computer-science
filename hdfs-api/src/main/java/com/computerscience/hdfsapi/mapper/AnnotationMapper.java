package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.Annotation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标注数据访问层
 */
@Mapper
public interface AnnotationMapper extends BaseMapper<Annotation> {
}


