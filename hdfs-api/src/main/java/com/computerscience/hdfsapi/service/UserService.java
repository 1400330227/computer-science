package com.computerscience.hdfsapi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.computerscience.hdfsapi.model.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据账号查询用户
     * @param account 账号
     * @return 用户信息
     */
    User findByAccount(String account);
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 分页查询用户列表
     * @param page 页码
     * @param size 每页大小
     * @param userType 用户类型（可选）
     * @return 分页用户列表
     */
    IPage<User> findUserPage(Integer page, Integer size, String userType);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 是否创建成功
     */
    boolean createUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Integer userId);
} 