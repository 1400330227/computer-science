package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件实体类
 */
@Data
@TableName("files")
public class FileEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID (主键)
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件地址
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小，以GB为单位
     */
    @TableField("size")
    private String size;

    /**
     * 文件创建者ID (外键关联users表)
     */
    @TableField("creator_id")
    private Integer creatorId;

    /**
     * 文件创建时间 (默认当前时间)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 文件更新者
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 文件更新时间 (更新时自动设置为当前时间)
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 关联的语料ID (外键关联corpus表)
     */
    @TableField("corpus_id")
    private Integer corpusId;
}
