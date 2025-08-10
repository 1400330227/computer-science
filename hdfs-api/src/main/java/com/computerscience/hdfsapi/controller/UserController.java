package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.UserService;
import com.computerscience.hdfsapi.service.CryptoService;
import com.computerscience.hdfsapi.service.SessionManagementService;
import com.computerscience.hdfsapi.service.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CryptoService cryptoService;
    
    @Autowired
    private SessionManagementService sessionManagementService;
    
    @Autowired
    private SmsVerificationService smsVerificationService;

    // 临时管理员测试端点
    @GetMapping("/admin-test")
    public ResponseEntity<Map<String, Object>> adminTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "管理员测试端点正常工作");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // 从 Session 中获取当前登录用户信息
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        // 获取 Session 中存储的用户 ID
        Integer userId = (Integer) session.getAttribute("currentUser");
        
        // 根据用户 ID 查询最新的用户信息
        User user = userService.getById(userId);
        if (user == null) {
            // 如果用户不存在，清除 Session
            session.invalidate();
            return ResponseEntity.status(401).body("用户不存在，请重新登录");
        }
        
        // 组装返回数据，不包含敏感信息
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("account", user.getAccount());
        response.put("userType", user.getUserType());
        response.put("phone", user.getPhone());
        response.put("gender", user.getGender());
        response.put("accountStatus", user.getAccountStatus());
        response.put("address", user.getAddress());
        
        return ResponseEntity.ok(response);
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
        try {
            String incomingPassword = user.getPassword();
            if (incomingPassword == null || incomingPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("密码不能为空");
            }

            // 使用RSA解密前端传来的密码，然后使用BCrypt加密存储
            String decryptedPassword = cryptoService.decryptWithRSA(incomingPassword);
            if (decryptedPassword == null || decryptedPassword.isEmpty()) {
                return ResponseEntity.badRequest().body("密码解密失败");
            }

            String hashedPassword = cryptoService.encryptPasswordWithBCrypt(decryptedPassword);
            user.setPassword(hashedPassword);
            user.setUserType("user");

            if (userService.createUser(user)) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.badRequest().body("创建用户失败，账号可能已存在");
            }
        } catch (Exception e) {
            System.err.println("创建用户时密码处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body("创建用户失败：密码处理异常");
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
     * 发送手机验证码接口
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        
        if (phone == null || phone.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "手机号不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查手机号是否已注册（如果已注册，检查账号状态）
        User user = userService.findByPhone(phone);
        if (user != null) {
            // 用户已存在，检查账号状态
            if (!"active".equals(user.getAccountStatus()) && !"正常".equals(user.getAccountStatus())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "账号已被禁用，无法登录");
                return ResponseEntity.badRequest().body(response);
            }
            System.out.println("发送验证码给已注册用户: " + user.getAccount());
        } else {
            // 新用户，首次使用手机号登录
            System.out.println("发送验证码给新用户: " + phone);
        }
        
        // 发送验证码
        SmsVerificationService.SendResult result = smsVerificationService.sendVerificationCode(phone);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据手机号自动创建新用户
     * @param phone 手机号
     * @return 创建的用户对象，失败时返回null
     */
    private User createNewUserByPhone(String phone) {
        try {
            User newUser = new User();
            
            // 设置基本信息
            newUser.setPhone(phone);
            newUser.setAccount(phone); // 默认账号为手机号
            newUser.setNickname("用户" + phone.substring(7)); // 默认昵称：用户+后4位手机号
            newUser.setUserType("user"); // 默认为普通用户
            newUser.setAccountStatus("active"); // 默认状态为激活
            newUser.setGender("未知"); // 默认性别
            newUser.setCreator("system"); // 创建者为系统
            newUser.setRemarks("通过手机号验证码登录自动创建"); // 备注说明
            
            // 设置默认密码（使用BCrypt加密的空密码，因为现在用验证码登录）
            String defaultPassword = cryptoService.encryptPasswordWithBCrypt("SMS_LOGIN_USER");
            newUser.setPassword(defaultPassword);
            
            // 调用用户服务创建用户
            if (userService.createUser(newUser)) {
                System.out.println("=== 自动创建用户成功 ===");
                System.out.println("手机号: " + phone);
                System.out.println("用户ID: " + newUser.getUserId());
                System.out.println("账号: " + newUser.getAccount());
                System.out.println("昵称: " + newUser.getNickname());
                System.out.println("========================");
                return newUser;
            } else {
                System.err.println("自动创建用户失败：userService.createUser返回false");
                return null;
            }
        } catch (Exception e) {
            System.err.println("自动创建用户时发生异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 手机号验证码登录接口
     */
    @PostMapping("/login-with-sms")
    public ResponseEntity<?> loginWithSms(@RequestBody Map<String, String> loginRequest, 
                                         HttpServletRequest request) {
        String phone = loginRequest.get("phone");
        String code = loginRequest.get("code");
        
        System.out.println("=== 短信登录调试信息 ===");
        System.out.println("接收到的手机号: [" + phone + "]");
        System.out.println("接收到的验证码: [" + code + "]");
        
        // 检查参数是否为空
        if (phone == null || code == null) {
            return ResponseEntity.badRequest().body("手机号和验证码不能为空");
        }
        
        // 验证验证码
        SmsVerificationService.VerifyResult verifyResult = smsVerificationService.verifyCode(phone, code);
        if (!verifyResult.isSuccess()) {
            System.out.println("验证码验证失败: " + verifyResult.getMessage());
            return ResponseEntity.badRequest().body(verifyResult.getMessage());
        }
        
        // 根据手机号查找用户
        User user = userService.findByPhone(phone);
        if (user == null) {
            System.out.println("手机号对应的用户不存在，自动创建新用户");
            // 手机号首次登录，自动创建用户
            user = createNewUserByPhone(phone);
            if (user == null) {
                return ResponseEntity.status(500).body("自动创建用户失败，请联系管理员");
            }
            System.out.println("新用户创建成功，用户ID: " + user.getUserId());
        }
        
        // 检查账号状态
        if (!"active".equals(user.getAccountStatus()) && !"正常".equals(user.getAccountStatus())) {
            return ResponseEntity.badRequest().body("账号已被禁用");
        }
        
        // 登录成功，创建Session
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user.getUserId());
        
        // 处理单点登录控制
        boolean kickedOldSession = sessionManagementService.handleUserLogin(user.getUserId(), session);
        if (kickedOldSession) {
            System.out.println("用户 " + user.getAccount() + " 的旧会话已被踢出");
        }
        
        // 返回登录成功信息
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        
        // 判断是否是新用户（通过remarks字段或创建时间判断）
        boolean isNewUser = "通过手机号验证码登录自动创建".equals(user.getRemarks());
        if (isNewUser) {
            responseMap.put("message", "欢迎新用户！登录成功");
            responseMap.put("isNewUser", true);
        } else {
            responseMap.put("message", "登录成功");
            responseMap.put("isNewUser", false);
        }
        
        responseMap.put("userId", user.getUserId());
        responseMap.put("account", user.getAccount());
        responseMap.put("userType", user.getUserType());
        responseMap.put("phone", user.getPhone());
        responseMap.put("nickname", user.getNickname());
        
        System.out.println("短信登录成功，用户ID: " + user.getUserId() + (isNewUser ? " (新用户)" : " (已有用户)"));
        return ResponseEntity.ok(responseMap);
    }

    /**
     * 获取RSA公钥接口
     * 前端加密密码时需要先获取此公钥
     * @deprecated 使用短信验证码登录方式，此接口已废弃
     */
    @Deprecated
    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        try {
            String publicKeyPEM = cryptoService.getPublicKeyPEM();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("publicKey", publicKeyPEM);
            response.put("message", "公钥获取成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取公钥失败: " + e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取公钥失败");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 用户登录验证接口（支持RSA加密密码）
     * 这个接口用来验证用户的用户名和加密密码是否正确
     * @deprecated 已废弃，请使用短信验证码登录方式 /login-with-sms
     */
    @Deprecated
    @PostMapping("/login")  // 告诉Spring这是一个POST请求，访问地址是 /api/users/login
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request,
                                 HttpServletResponse response) {
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
        // 使用RSA解密密码，然后用BCrypt验证
        boolean passwordValid = false;
        try {
            // 检查数据库中的密码是否是BCrypt格式
            String storedPassword = user.getPassword();
            if (storedPassword.startsWith("$2")) {
                // 数据库中是BCrypt哈希密码，使用新的验证方式
                passwordValid = cryptoService.verifyEncryptedPassword(password, storedPassword);
                System.out.println("使用BCrypt验证方式，验证结果: " + passwordValid);
            } else {
                // 数据库中是明文密码（兼容旧数据），先解密RSA密码然后比较
                String decryptedPassword = cryptoService.decryptWithRSA(password);
                passwordValid = decryptedPassword.equals(storedPassword);
                System.out.println("使用明文比较方式（兼容模式），验证结果: " + passwordValid);
                
                // 如果验证成功，将明文密码转换为BCrypt哈希并更新数据库
                if (passwordValid) {
                    String hashedPassword = cryptoService.encryptPasswordWithBCrypt(decryptedPassword);
                    user.setPassword(hashedPassword);
                    userService.updateById(user);
                    System.out.println("已将用户密码升级为BCrypt哈希格式");
                }
            }
        } catch (Exception e) {
            System.err.println("密码验证过程中出错: " + e.getMessage());
            passwordValid = false;
        }
        
        if (!passwordValid) {
            // 如果密码不匹配，返回错误信息
            return ResponseEntity.badRequest().body("密码错误");
        }
        
        // 检查账号状态（确保账号没有被禁用）
        if (!"active".equals(user.getAccountStatus()) && !"正常".equals(user.getAccountStatus())) {
            // 如果账号状态不是active或正常，不允许登录
            return ResponseEntity.badRequest().body("账号已被禁用");
        }

        // 登录成功，将用户ID存储到Session中
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user.getUserId());
        
        // 处理单点登录控制
        boolean kickedOldSession = sessionManagementService.handleUserLogin(user.getUserId(), session);
        if (kickedOldSession) {
            System.out.println("用户 " + user.getAccount() + " 的旧会话已被踢出");
        }

        // 如果所有验证都通过，准备返回给前端的用户信息
        // 使用Map来组织返回的数据，就像一个信息盒子
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);                // 告诉前端登录成功了
        responseMap.put("message", "登录成功");           // 成功消息
        responseMap.put("userId", user.getUserId());     // 用户ID
        responseMap.put("account", user.getAccount());   // 用户账号
        responseMap.put("userType", user.getUserType()); // 用户类型（如：管理员、普通用户）
        responseMap.put("phone", user.getPhone());       // 用户手机号
        responseMap.put("nickname", user.getNickname());       // 用户手机号
        // 注意：这里故意不返回密码，保护用户隐私安全
        
        // 返回200成功状态码和用户信息
        return ResponseEntity.ok(responseMap);
    }
    
    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 获取用户ID用于清理session映射
            Integer userId = (Integer) session.getAttribute("currentUser");
            String sessionId = session.getId();
            
            // 清理session管理服务中的映射
            if (userId != null) {
                sessionManagementService.handleUserLogout(userId, sessionId);
            }
            
            // 销毁session
            session.invalidate();
        }
        
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("message", "已成功登出");
        
        return ResponseEntity.ok(responseMap);
    }
    
    /**
     * 获取系统在线用户统计信息（管理员接口）
     */
    @GetMapping("/online-stats")
    public ResponseEntity<?> getOnlineStats(HttpServletRequest request) {
        // 检查当前用户是否为管理员
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        Integer currentUserId = (Integer) session.getAttribute("currentUser");
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        // 获取当前用户信息
        User currentUser = userService.getById(currentUserId);
        if (currentUser == null || !"admin".equals(currentUser.getUserType())) {
            return ResponseEntity.status(403).body("权限不足，仅管理员可访问");
        }
        
        // 获取在线统计信息
        int onlineUserCount = sessionManagementService.getOnlineUserCount();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("onlineUserCount", onlineUserCount);
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("message", "在线用户统计");
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 强制踢出指定用户（管理员接口）
     */
    @PostMapping("/kick-user/{userId}")
    public ResponseEntity<?> kickUser(@PathVariable Integer userId, HttpServletRequest request) {
        // 检查当前用户是否为管理员
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        Integer currentUserId = (Integer) session.getAttribute("currentUser");
        if (currentUserId == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }
        
        // 获取当前用户信息
        User currentUser = userService.getById(currentUserId);
        if (currentUser == null || !"admin".equals(currentUser.getUserType())) {
            return ResponseEntity.status(403).body("权限不足，仅管理员可访问");
        }
        
        // 不能踢出自己
        if (userId.equals(currentUserId)) {
            return ResponseEntity.badRequest().body("不能踢出自己");
        }
        
        // 获取目标用户的session ID
        String sessionId = sessionManagementService.getCurrentSessionId(userId);
        if (sessionId == null) {
            return ResponseEntity.badRequest().body("目标用户当前未登录");
        }
        
        // 强制登出目标用户
        sessionManagementService.handleUserLogout(userId, sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户已被强制下线");
        response.put("kickedUserId", userId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 心跳检测接口 - 检查当前会话是否有效
     * 用于前端定期检查是否被踢出
     */
    @GetMapping("/heartbeat")
    public ResponseEntity<?> heartbeat(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
//            logger.debug("💓 心跳检测请求 - Session: {}", session != null ? session.getId() : "null");
            
            if (session == null) {
//                logger.debug("❌ 心跳检测失败 - 会话不存在");
                return ResponseEntity.status(401).body("会话不存在");
            }
            
            Integer userId = (Integer) session.getAttribute("currentUser");
            if (userId == null) {
//                logger.debug("❌ 心跳检测失败 - 用户未登录");
                return ResponseEntity.status(401).body("用户未登录");
            }
            
//            logger.debug("💓 心跳检测 - 用户ID: {}, SessionID: {}", userId, session.getId());
            
            // 检查session是否被踢出
            boolean isValid = sessionManagementService.isSessionValid(session);
//            logger.debug("🔍 Session有效性检查结果: {}", isValid);
            
            if (!isValid) {
                String sessionId = session.getId();
                logger.info("🚨 检测到用户{}的会话{}被踢出", userId, sessionId);
                
                // 从SessionManagementService获取踢出原因
                String kickReason = sessionManagementService.getKickReason(sessionId);
//                logger.debug("📝 踢出原因: {}", kickReason);
                
                if (kickReason == null) {
                    // 尝试从session获取（兼容处理）
                    try {
                        kickReason = (String) session.getAttribute("kick_reason");
//                        logger.debug("📝 从session获取踢出原因: {}", kickReason);
                    } catch (Exception e) {
                        // 忽略异常
                    }
                    
                    if (kickReason == null) {
                        kickReason = "您的账号在其他地方登录，当前会话已被强制下线";
//                        logger.debug("📝 使用默认踢出原因");
                    }
                }
                
                // 清理踢出记录（避免内存泄漏）
                sessionManagementService.clearKickRecord(sessionId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("code", "SESSION_KICKED_OUT");
                response.put("message", kickReason);
                response.put("timestamp", System.currentTimeMillis());
                
                logger.info("📤 返回踢出响应给前端: {}", kickReason);
                return ResponseEntity.status(401).body(response);
            }
            
            // 会话有效
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "会话有效");
            response.put("userId", userId);
            response.put("timestamp", System.currentTimeMillis());
            
//            logger.debug("✅ 心跳检测正常 - 用户: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("心跳检测异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "心跳检测失败");
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 