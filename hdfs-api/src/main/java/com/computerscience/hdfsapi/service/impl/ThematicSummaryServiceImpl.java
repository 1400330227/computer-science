package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.ThematicSummaryMapper;
import com.computerscience.hdfsapi.model.ThematicSummary;
import com.computerscience.hdfsapi.service.ThematicSummaryService;
import org.springframework.stereotype.Service;

@Service
public class ThematicSummaryServiceImpl extends ServiceImpl<ThematicSummaryMapper, ThematicSummary> implements ThematicSummaryService {}