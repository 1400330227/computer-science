package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标注标签实体类
 * 用于管理标注的类别和标签
 */
@Data
@TableName("annotation_labels")
public class AnnotationLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID (主键)
     */
    @TableId(value = "label_id", type = IdType.AUTO)
    private Integer labelId;

    /**
     * 标签名称
     */
    @TableField("label_name")
    private String labelName;

    /**
     * 标签描述
     */
    @TableField("description")
    private String description;

    /**
     * 标签颜色（用于前端显示）
     */
    @TableField("color")
    private String color;

    /**
     * 标签分类（如：情感、实体、语法等）
     */
    @TableField("category")
    private String category;

    /**
     * 创建者ID (外键关联users表)
     */
    @TableField("creator_id")
    private Integer creatorId;

    /**
     * 创建时间 (默认当前时间)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 是否系统默认标签
     */
    @TableField("is_system")
    private Boolean isSystem;
}


