package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.CountryDistributionMapper;
import com.computerscience.hdfsapi.model.CountryDistribution;
import com.computerscience.hdfsapi.service.CountryDistributionService;
import org.springframework.stereotype.Service;

@Service
public class CountryDistributionServiceImpl extends ServiceImpl<CountryDistributionMapper, CountryDistribution> implements CountryDistributionService {}
