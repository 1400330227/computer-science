package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")  // 指定数据库表名为 users（复数形式）
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID (主键)
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 账号
     */
    private String account;

    private String nickname;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 帐号状态 (默认 'active')
     */
    private String accountStatus;

    /**
     * 用户创建者
     */
    private String creator;

    /**
     * 创建时间 (默认当前时间)
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 用户信息更新者
     */
    private String updatedBy;

    /**
     * 用户信息更新时间 (更新时自动设置为当前时间)
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 地址
     */
    private String address;
}