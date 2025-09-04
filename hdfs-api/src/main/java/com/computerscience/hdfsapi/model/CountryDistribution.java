package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("country_distribution")
public class CountryDistribution implements Serializable {
    private static final long serialVersionUID = 1L;

    private String country;
    private Integer corpusCount;
    private Integer filesCount;
    private Double totalCapacityGb;
    private String languagesUsed;
}