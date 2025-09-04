package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.CorpusOverviewMapper;
import com.computerscience.hdfsapi.model.CorpusOverview;
import com.computerscience.hdfsapi.service.CorpusOverviewService;
import org.springframework.stereotype.Service;

@Service
public class CorpusOverviewServiceImpl extends ServiceImpl<CorpusOverviewMapper, CorpusOverview> implements CorpusOverviewService {}
