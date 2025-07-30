package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.mapper.UserMapper;
import com.computerscience.hdfsapi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByAccount(String account) {
        if (!StringUtils.hasText(account)) {
            return null;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, account);
        
        return getOne(queryWrapper);
    }

    @Override
    public IPage<User> findUserPage(Integer page, Integer size, String userType) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(userType)) {
            queryWrapper.eq(User::getUserType, userType);
        }
        
        queryWrapper.orderByDesc(User::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional
    public boolean createUser(User user) {
        // 检查账号是否已存在
        User existingUser = findByAccount(user.getAccount());
        if (existingUser != null) {
            return false;
        }
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(LocalDateTime.now());
        }
        
        return save(user);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        
        // 检查用户是否存在
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            return false;
        }
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            return false;
        }
        
        return removeById(userId);
    }
} 