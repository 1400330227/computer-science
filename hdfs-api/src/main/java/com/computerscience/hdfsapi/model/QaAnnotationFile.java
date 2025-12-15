package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问答对标注文件实体类
 * 记录用户上传的问答对标注文件的信息
 */
@Data
@TableName("qa_annotation_files")
public class QaAnnotationFile implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的原始文件ID (外键关联 files 表)
     */
    @TableField("original_file_id")
    private Integer originalFileId;

    /**
     * 标注文件在HDFS上的存储路径
     */
    @TableField("annotation_file_path")
    private String annotationFilePath;

    /**
     * 有效的问答对数量
     */
    @TableField("qa_pair_count")
    private Integer qaPairCount;

    /**
     * 上传状态 (e.g., "VALIDATED", "FAILED")
     */
    @TableField("status")
    private String status;

    /**
     * 校验失败时的备注信息
     */
    @TableField("validation_notes")
    private String validationNotes;

    /**
     * 创建者ID (外键关联 users 表)
     */
    @TableField("creator_id")
    private Integer creatorId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
