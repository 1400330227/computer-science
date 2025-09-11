package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.RecentUpload;
import com.computerscience.hdfsapi.mapper.RecentUploadMapper;
import com.computerscience.hdfsapi.service.RecentUploadService;
import org.springframework.stereotype.Service;

@Service
public class RecentUploadServiceImpl extends ServiceImpl<RecentUploadMapper, RecentUpload> implements RecentUploadService {
}
