package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.AnnotationFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationFileMapper extends BaseMapper<AnnotationFile> {
    List<Map<String, Object>> selectCorpusAnnotationStats(List<Integer> corpusIds);
}

