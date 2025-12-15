package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.QaAnnotationFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答对标注文件的数据访问层
 */
@Mapper
public interface QaAnnotationFileMapper extends BaseMapper<QaAnnotationFile> {
}