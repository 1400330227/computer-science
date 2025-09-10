package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.CollegeOverview;
import com.computerscience.hdfsapi.mapper.CollegeOverviewMapper;
import com.computerscience.hdfsapi.service.CollegeOverviewService;
import org.springframework.stereotype.Service;

@Service
public class CollegeOverviewServiceImpl extends ServiceImpl<CollegeOverviewMapper, CollegeOverview> implements CollegeOverviewService {
}
