package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("college_overview") // 确保与您的视图名称完全匹配
public class CollegeOverview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学院或机构名称
     */
    private String college;

    /**
     * 贡献者数量
     */
    private Integer contributorsCount;

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
}
