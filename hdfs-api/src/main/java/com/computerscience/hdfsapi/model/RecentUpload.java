package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("recent_uploads_feed") // 对应视图名称
public class RecentUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uploadTime;
    private String contributorName;
    private String contributorCollege;
    private String corpusName;
    private String corpusDomain;
    private Double capacityGb;
}

