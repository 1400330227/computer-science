package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("contributor_analysis")
public class ContributorAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String contributorName;
    private String college;
    private Integer corpusCount;
    private Integer filesCount;
    private Double totalCapacityGb;
    private LocalDateTime firstContribution;
    private LocalDateTime lastContribution;
}