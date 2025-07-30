package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件数据访问层
 */
@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {
} 