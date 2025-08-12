package com.computerscience.hdfsapi.dto;

import java.util.List;

public class CorpusTransferRequest {
    private List<Integer> corpusIds;
    private Integer targetUserId;

    public List<Integer> getCorpusIds() { return corpusIds; }
    public void setCorpusIds(List<Integer> corpusIds) { this.corpusIds = corpusIds; }
    public Integer getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Integer targetUserId) { this.targetUserId = targetUserId; }
}
