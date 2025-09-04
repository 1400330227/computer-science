package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("time_series_analysis")
public class TimeSeriesAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate contributionDate;
    private Integer dailyCorpusAdded;
    private Integer dailyFilesAdded;
    private Double dailyCapacityAdded;
    private String contributorsOfDay;
}