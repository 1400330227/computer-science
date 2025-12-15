package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.computerscience.hdfsapi.dto.CorpusWithComputedCapacity;
import com.computerscience.hdfsapi.mapper.AdminMapper;
import com.computerscience.hdfsapi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Page<CorpusWithComputedCapacity> findCorpusWithComputedCapacity(int page, int size, String collectionName, String creatorAccount) {
        Page<CorpusWithComputedCapacity> pageRequest = new Page<>(page, size);
        return adminMapper.findCorpusWithComputedCapacity(pageRequest, collectionName, creatorAccount);
    }
} 