package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.mapper.AnnotationFileDetailsMapper;
import com.computerscience.hdfsapi.mapper.CorpusMapper;
import com.computerscience.hdfsapi.model.AnnotationFileDetails;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.service.AdminService;
import com.computerscience.hdfsapi.service.AnnotationFileDetailsService;
import com.computerscience.hdfsapi.service.CorpusService;
import org.springframework.stereotype.Service;

@Service
public class AnnotationFileDetailsServiceImpl extends ServiceImpl<AnnotationFileDetailsMapper, AnnotationFileDetails> implements AnnotationFileDetailsService {
}
