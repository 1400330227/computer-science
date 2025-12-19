package com.computerscience.hdfsapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.computerscience.hdfsapi.utils.DoubleDeserializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CorpusWithUserInfo {
    private Integer corpusId;
    private String country;
    private String collectionName;
    private String domain;
    private String language;
    private String dataFormat;
    private String classification;
    private Double dataVolume;
    private String volumeUnit;
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double estimatedCapacityGb;
    private String dataYear;
    private String sourceLocation;
    private String dataSource;
    private String provider;
    private String providerContact;
    private String remarks;
    private Integer creatorId;
    private LocalDateTime createdAt;

    // 用户信息字段
    private String creatorAccount;
    private String creatorNickname;
    private String creatorUserType;

    // 标注相关
    private String annotationStatus;
    private String annotationUploaderName;

    private Integer totalAnnotationFiles;  // 该语料下的标注文件总数
    private Integer totalQaPairs;          // 该语料下的问答对总数
    private LocalDateTime latestAnnotationDate;     // 最新标注时间

}