package com.computerscience.hdfsapi.dto;

import com.computerscience.hdfsapi.model.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CorpusFileVO extends FileEntity {
    private static final long serialVersionUID = 1L;
    private List<AnnotationFileVO> annotationFiles;
    private Integer qaPairCount;
}

