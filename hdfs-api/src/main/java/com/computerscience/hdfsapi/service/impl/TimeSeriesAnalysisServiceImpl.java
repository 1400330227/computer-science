package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.TimeSeriesAnalysisMapper;
import com.computerscience.hdfsapi.model.TimeSeriesAnalysis;
import com.computerscience.hdfsapi.service.TimeSeriesAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class TimeSeriesAnalysisServiceImpl extends ServiceImpl<TimeSeriesAnalysisMapper, TimeSeriesAnalysis> implements TimeSeriesAnalysisService {}
