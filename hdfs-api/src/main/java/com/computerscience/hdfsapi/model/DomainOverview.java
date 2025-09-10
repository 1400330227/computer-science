package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("domain_overview") // 确保与您的视图名称完全匹配
public class DomainOverview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 领域名称
     */
    private String domain;

    /**
     * 语料库数量
     */
    private Integer corpusCount;

    /**
     * 文件总数
     */
    private Integer filesCount;

    /**
     * 总估算容量 (GB)
     */
    private Double totalCapacityGb;

    /**
     * 覆盖的国家数量
     */
    private Integer countriesCovered;

    /**
     * 覆盖的语言数量
     */
    private Integer languagesCovered;
}
