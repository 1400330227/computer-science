package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标注实体类
 * 用于对语料进行标注和注释
 */
@Data
@TableName("annotations")
public class Annotation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标注ID (主键)
     */
    @TableId(value = "annotation_id", type = IdType.AUTO)
    private Integer annotationId;

    /**
     * 关联的语料ID (外键关联corpus表)
     */
    @TableField("corpus_id")
    private Integer corpusId;

    /**
     * 关联的文件ID (外键关联files表，可选)
     */
    @TableField("file_id")
    private Integer fileId;

    /**
     * 标注标题
     */
    @TableField("title")
    private String title;

    /**
     * 标注内容/注释
     */
    @TableField("content")
    private String content;

    /**
     * 标注类型/标签（如：情感分析、实体识别、语法标注等）
     */
    @TableField("annotation_type")
    private String annotationType;

    /**
     * 标注状态（如：待审核、已审核、已发布等）
     */
    @TableField("status")
    private String status;

    /**
     * 标注的文本片段（如果是对文本的特定部分进行标注）
     */
    @TableField("text_segment")
    private String textSegment;

    /**
     * 文本片段起始位置
     */
    @TableField("start_position")
    private Integer startPosition;

    /**
     * 文本片段结束位置
     */
    @TableField("end_position")
    private Integer endPosition;

    /**
     * 标注创建者ID (外键关联users表)
     */
    @TableField("creator_id")
    private Integer creatorId;

    /**
     * 标注创建时间 (默认当前时间)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 标注更新时间 (更新时自动设置为当前时间)
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 备注说明
     */
    @TableField("remarks")
    private String remarks;
}


