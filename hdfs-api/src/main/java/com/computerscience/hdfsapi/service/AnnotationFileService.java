package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.AnnotationFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface AnnotationFileService extends IService<AnnotationFile> {

    /**
     * 上传标注文件
     *
     * @param file           上传的文件
     * @param originalFileId 原始文件ID
     * @param userId         当前操作用户ID
     * @return 标注文件实体
     */
    AnnotationFile uploadAnnotation(MultipartFile file, Integer originalFileId, Integer userId);

    /**
     * 删除标注文件
     *
     * @param annotationId 标注文件ID
     */
    void deleteAnnotation(Integer annotationId);

    /**
     * 下载单个标注文件
     *
     * @param annotationId 标注文件ID
     * @param response     HTTP响应
     */
    void downloadAnnotation(Integer annotationId, HttpServletResponse response);

    /**
     * 下载某语料下全部标注文件（打包ZIP）
     *
     * @param corpusId 语料ID
     * @param response HTTP响应
     */
    void downloadAllAnnotations(Integer corpusId, HttpServletResponse response);
}

