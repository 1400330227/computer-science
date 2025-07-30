package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 根据账号查询用户
     */
    @GetMapping("/account/{account}")
    public ResponseEntity<User> getUserByAccount(@PathVariable String account) {
        User user = userService.findByAccount(account);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public ResponseEntity<IPage<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String userType) {
        return ResponseEntity.ok(userService.findUserPage(page, size, userType));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.createUser(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body("创建用户失败，账号可能已存在");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (userService.updateUser(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body("更新用户失败，用户可能不存在");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("删除用户失败，用户可能不存在");
        }
    }
} 