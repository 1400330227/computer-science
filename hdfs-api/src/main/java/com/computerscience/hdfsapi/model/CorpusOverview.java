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

    /**
     * 文本文件总数
     */
    private Integer totalTextFiles;

    /**
     * 音频文件总数
     */
    private Integer totalAudioFiles;

    /**
     * 视频文件总数
     */
    private Integer totalVideoFiles;

    /**
     * 图像文件总数
     */
    private Integer totalImageFiles;

    /**
     * 文本文件数量在总文件数量中的百分比
     */
    private Double textFilesPercentage;

    /**
     * 音频文件数量在总文件数量中的百分比
     */
    private Double audioFilesPercentage;

    /**
     * 视频文件数量在总文件数量中的百分比
     */
    private Double videoFilesPercentage;

    /**
     * 图像文件数量在总文件数量中的百分比
     */
    private Double imageFilesPercentage;
}