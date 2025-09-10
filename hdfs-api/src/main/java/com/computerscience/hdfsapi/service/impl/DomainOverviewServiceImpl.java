package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.DomainOverview;
import com.computerscience.hdfsapi.mapper.DomainOverviewMapper;
import com.computerscience.hdfsapi.service.DomainOverviewService;
import org.springframework.stereotype.Service;

@Service
public class DomainOverviewServiceImpl extends ServiceImpl<DomainOverviewMapper, DomainOverview> implements DomainOverviewService {
}
