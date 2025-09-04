package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.ContributorAnalysisMapper;
import com.computerscience.hdfsapi.model.ContributorAnalysis;
import com.computerscience.hdfsapi.service.ContributorAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class ContributorAnalysisServiceImpl extends ServiceImpl<ContributorAnalysisMapper, ContributorAnalysis> implements ContributorAnalysisService {}
