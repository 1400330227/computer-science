package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("annotation_files")
public class AnnotationFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("file_id")
    private Integer fileId;

    @TableField("corpus_id")
    private Integer corpusId;

    @TableField("creator_id")
    private Integer creatorId;

    private String title;

    @TableField("file_type")
    private String fileType;

    @TableField("annotation_file_path")
    private String annotationFilePath;

    private String status; // VALIDATED, FAILED

    private String content;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("updated_by")
    private String updatedBy;

    @TableField(value = "updated_at", fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    // 非数据库字段，用于关联查询时回显
    @TableField(exist = false)
    private AnnotationFileDetails details;
}