package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件实体类
 */
@Data
@TableName("file")
public class FileEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件地址
     */
    private String fileLocation;

    /**
     * 文件创建者ID
     */
    private Long creatorId;

    /**
     * 文件创建者名称
     */
    private String creatorName;

    /**
     * 文件创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文件更新者ID
     */
    private Long updaterId;

    /**
     * 文件更新者名称
     */
    private String updaterName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 关联的语料库ID
     */
    private Long corpusId;

}
