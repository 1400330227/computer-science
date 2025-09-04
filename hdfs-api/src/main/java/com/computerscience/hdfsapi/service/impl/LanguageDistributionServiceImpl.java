package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.LanguageDistributionMapper;
import com.computerscience.hdfsapi.model.LanguageDistribution;
import com.computerscience.hdfsapi.service.LanguageDistributionService;
import org.springframework.stereotype.Service;

@Service
public class LanguageDistributionServiceImpl extends ServiceImpl<LanguageDistributionMapper, LanguageDistribution> implements LanguageDistributionService {}
