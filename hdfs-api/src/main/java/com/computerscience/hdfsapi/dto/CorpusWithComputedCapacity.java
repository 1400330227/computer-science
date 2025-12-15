package com.computerscience.hdfsapi.dto;

import com.computerscience.hdfsapi.model.Corpus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CorpusWithComputedCapacity extends Corpus {
    private Double computedCapacityGb;
} 