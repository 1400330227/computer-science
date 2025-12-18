package com.computerscience.hdfsapi.dto;

import com.computerscience.hdfsapi.model.AnnotationFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnnotationFileVO extends AnnotationFile {
    private Integer qaPairCount;
}
