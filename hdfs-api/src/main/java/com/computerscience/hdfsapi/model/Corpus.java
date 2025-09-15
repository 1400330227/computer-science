package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.computerscience.hdfsapi.utils.DoubleDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("corpus")
public class Corpus implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 语料ID (主键)
     */
    @TableId(value = "corpus_id", type = IdType.AUTO)
    private Integer corpusId;

    /**
     * 国家
     */
    private String country;

    /**
     * 语料集名称
     */
    @TableField("collection_name")
    private String collectionName;

    /**
     * 所属领域
     */
    private String domain;

    /**
     * 语种
     */
    private String language;

    /**
     * 数据形式
     */
    @TableField("data_format")
    private String dataFormat;

    /**
     * 数据分类
     */
    private String classification;

    /**
     * 数据量
     */
    @TableField("data_volume")
    private Double dataVolume;

    /**
     * 数据单位
     */
    @TableField("volume_unit")
    private String volumeUnit;

    /**
     * 容量估算（GB）
     */
    @TableField("estimated_capacity_gb")
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double estimatedCapacityGb;

    /**
     * 数据年份
     */
    @TableField("data_year")
    private String dataYear;

    /**
     * 来源归属地
     */
    @TableField("source_location")
    private String sourceLocation;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 数据提供方
     */
    private String provider;

    /**
     * 数据提供方联系方式
     */
    @TableField("provider_contact")
    private String providerContact;

    /**
     * 备注说明
     */
    private String remarks;

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

}