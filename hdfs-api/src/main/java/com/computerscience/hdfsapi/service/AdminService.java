package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.computerscience.hdfsapi.dto.CorpusWithComputedCapacity;

public interface AdminService {
    Page<CorpusWithComputedCapacity> findCorpusWithComputedCapacity(int page, int size, String collectionName, String creatorAccount);
} 