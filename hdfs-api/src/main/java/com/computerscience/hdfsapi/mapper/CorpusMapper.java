package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.Corpus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 语料库数据访问层
 */
@Mapper
public interface CorpusMapper extends BaseMapper<Corpus> {
} 