package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("thematic_summary")
public class ThematicSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private String inferredTopic;
    private Integer corpusCount;
    private Integer countriesCovered;
    private String languagesUsed;
    private Double totalCapacityGb;
}
