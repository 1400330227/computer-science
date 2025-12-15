package com.computerscience.hdfsapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.computerscience.hdfsapi.utils.DoubleDeserializer;
import lombok.Data;
import java.time.LocalDateTime;

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

    // Getters and Setters
    public Integer getCorpusId() { return corpusId; }
    public void setCorpusId(Integer corpusId) { this.corpusId = corpusId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public Double getDataVolume() { return dataVolume; }
    public void setDataVolume(Double dataVolume) { this.dataVolume = dataVolume; }

    public String getVolumeUnit() { return volumeUnit; }
    public void setVolumeUnit(String volumeUnit) { this.volumeUnit = volumeUnit; }

    public Double getEstimatedCapacityGb() { return estimatedCapacityGb; }
    public void setEstimatedCapacityGb(Double estimatedCapacityGb) { this.estimatedCapacityGb = estimatedCapacityGb; }

    public String getDataYear() { return dataYear; }
    public void setDataYear(String dataYear) { this.dataYear = dataYear; }

    public String getSourceLocation() { return sourceLocation; }
    public void setSourceLocation(String sourceLocation) { this.sourceLocation = sourceLocation; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderContact() { return providerContact; }
    public void setProviderContact(String providerContact) { this.providerContact = providerContact; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getCreatorId() { return creatorId; }
    public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatorAccount() { return creatorAccount; }
    public void setCreatorAccount(String creatorAccount) { this.creatorAccount = creatorAccount; }

    public String getCreatorNickname() { return creatorNickname; }
    public void setCreatorNickname(String creatorNickname) { this.creatorNickname = creatorNickname; }

    public String getCreatorUserType() { return creatorUserType; }
    public void setCreatorUserType(String creatorUserType) { this.creatorUserType = creatorUserType; }
}