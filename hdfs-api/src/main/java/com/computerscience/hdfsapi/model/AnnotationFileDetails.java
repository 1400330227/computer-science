package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("annotation_file_details")
public class AnnotationFileDetails {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer annotationFilesId;
    private Integer qaPairCount;
}

