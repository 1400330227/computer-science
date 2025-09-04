package com.computerscience.hdfsapi.model;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("language_distribution")
public class LanguageDistribution implements Serializable {
    private static final long serialVersionUID = 1L;

    private String language;
    private Integer corpusCount;
    private Integer filesCount;
    private Double totalCapacityGb;
    private String countriesCovered;
}