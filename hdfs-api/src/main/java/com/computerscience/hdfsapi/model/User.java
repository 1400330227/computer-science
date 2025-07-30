package com.computerscience.hdfsapi.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号状态
     */
    private String accountStatus;

    /**
     * 用户创建者
     */
    private String creator;

    /**
     * 文件创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户信息更新者
     */
    private String updater;

    /**
     * 用户信息更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 用户邮箱
     */
    private String email;
}