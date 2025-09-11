package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.computerscience.hdfsapi.model.RecentUpload;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecentUploadMapper extends BaseMapper<RecentUpload> {
}

