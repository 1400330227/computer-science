package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        user.setUserId(id.intValue());  // Long转换为Integer
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
        if (userService.deleteUser(id.intValue())) {  // Long转换为Integer
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("删除用户失败，用户可能不存在");
        }
    }

    /**
     * 用户登录验证接口
     * 这个接口用来验证用户的用户名和密码是否正确
     */
    @PostMapping("/login")  // 告诉Spring这是一个POST请求，访问地址是 /api/users/login
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // 从请求体中提取用户名和密码
        String account = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // 添加调试日志
        System.out.println("=== 登录调试信息 ===");
        System.out.println("接收到的用户名: [" + account + "]");
        System.out.println("用户名长度: " + (account != null ? account.length() : "null"));
        System.out.println("接收到的密码: [" + password + "]");
        
        // 检查参数是否为空
        if (account == null || password == null) {
            // 如果任何一个为空，返回400错误状态码和错误信息
            return ResponseEntity.badRequest().body("用户名和密码不能为空");
        }
        
        // 调用用户服务，根据账号在数据库中查找用户
        System.out.println("开始查询用户，account参数: [" + account + "]");
        User user = userService.findByAccount(account);
        System.out.println("查询结果: " + (user != null ? "找到用户，ID=" + user.getUserId() + ", account=" + user.getAccount() : "未找到用户"));
        
        // 检查用户是否存在
        if (user == null) {
            System.out.println("用户查询结果为null，返回用户不存在错误");
            // 如果数据库中没有这个用户，返回错误信息
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        // 验证密码是否正确
        // 注意：实际项目中密码应该是加密存储的，这里为了简单直接比较
        if (!password.equals(user.getPassword())) {
            // 如果密码不匹配，返回错误信息
            return ResponseEntity.badRequest().body("密码错误");
        }
        
        // 检查账号状态（确保账号没有被禁用）
        if (!"active".equals(user.getAccountStatus()) && !"正常".equals(user.getAccountStatus())) {
            // 如果账号状态不是active或正常，不允许登录
            return ResponseEntity.badRequest().body("账号已被禁用");
        }
        
        // 如果所有验证都通过，准备返回给前端的用户信息
        // 使用Map来组织返回的数据，就像一个信息盒子
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);                // 告诉前端登录成功了
        response.put("message", "登录成功");           // 成功消息
        response.put("userId", user.getUserId());     // 用户ID
        response.put("account", user.getAccount());   // 用户账号
        response.put("userType", user.getUserType()); // 用户类型（如：管理员、普通用户）
        response.put("phone", user.getPhone());       // 用户手机号
        // 注意：这里故意不返回密码，保护用户隐私安全
        
        // 返回200成功状态码和用户信息
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试端点：查看数据库中所有用户（用于调试）
     */
    @GetMapping("/debug/all")
    public ResponseEntity<?> getAllUsersForDebug() {
        try {
            List<User> allUsers = userService.list();
            System.out.println("=== 数据库中所有用户 ===");
            System.out.println("总用户数: " + allUsers.size());
            
            for (User u : allUsers) {
                System.out.println("用户ID: " + u.getUserId() + 
                                 ", account: [" + u.getAccount() + "]" +
                                 ", userType: " + u.getUserType() + 
                                 ", phone: " + u.getPhone());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", allUsers.size());
            response.put("users", allUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("查询所有用户时出错: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("查询失败: " + e.getMessage());
        }
    }
} 