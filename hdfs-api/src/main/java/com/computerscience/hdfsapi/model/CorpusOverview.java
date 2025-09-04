package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("corpus_overview")
public class CorpusOverview implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer totalCorpus;
    private Integer totalFiles;
    private Double totalCapacityGb;
    private Integer countriesCount;
    private Integer languagesCount;
    private Integer contributorsCount;
}