package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.QaAnnotationFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 问答对标注文件的业务逻辑服务接口
 */
public interface QaAnnotationFileService extends IService<QaAnnotationFile> {

     /**
     * 处理用户上传的问答对标注文件
     *
     * @param file           上传的 .txt 文件
     * @param originalFileId 关联的原始文件ID
     * @return 处理结果，成功则返回创建的 QaAnnotationFile 记录
     * @throws Exception 处理过程中可能发生的异常
     */
     QaAnnotationFile processAnnotationFile(MultipartFile file, Integer originalFileId) throws Exception;
    }