package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("corpus")
public class Corpus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 国家
     */
    private String country;

    /**
     * 语料集名称
     */
    private String name;

    /**
     * 明暗领域
     */
    private String domainType;

    /**
     * 语种
     */
    private String language;

    /**
     * 数据形式
     */
    private String dataForm;

    /**
     * 数据分类
     */
    private String dataCategory;

    /**
     * 数据量
     */
    private String dataVolume;

    /**
     * 数据量单位
     */
    private String dataVolumeUnit;

    /**
     * 容量估算(GB)
     */
    private Double estimatedCapacityGB;

    /**
     * 数据年份
     */
    private String dataYear;

    /**
     * 采集归属期
     */
    private String collectionPeriod;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 数据提供方
     */
    private String dataProvider;

    /**
     * 数据提供方联系方式
     */
    private String dataProviderContact;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者ID
     */
    private Long updaterId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}