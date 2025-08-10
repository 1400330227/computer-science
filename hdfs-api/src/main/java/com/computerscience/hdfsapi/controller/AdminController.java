package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.service.UserService;
import com.computerscience.hdfsapi.utils.DPage;
import com.computerscience.hdfsapi.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private com.computerscience.hdfsapi.service.CryptoService cryptoService;

    // 测试端点 - 用于验证Controller是否正常工作
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "AdminController正常工作");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam Map<String, String> params) {
        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int size = Integer.parseInt(params.getOrDefault("size", "10"));
            String account = params.get("account");

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(account)) {
                queryWrapper.like(User::getAccount, account);
            }
            queryWrapper.orderByDesc(User::getCreatedAt);
            
            IPage<User> userPage = userService.page(new Page<>(page, size), queryWrapper);
            DPage<User> result = new DPage<>(userPage.getRecords(), userPage.getTotal(), page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }
            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserByAdmin(@PathVariable Integer userId, @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 允许更新的字段（不允许通过此接口修改password和userType）
            if (payload.containsKey("account")) {
                Object accountVal = payload.get("account");
                if (accountVal instanceof String && StringUtils.hasText((String) accountVal)) {
                    String newAccount = ((String) accountVal).trim();
                    // 若修改了账号，则校验唯一性
                    if (!newAccount.equals(user.getAccount())) {
                        LambdaQueryWrapper<User> dupCheck = new LambdaQueryWrapper<>();
                        dupCheck.eq(User::getAccount, newAccount);
                        dupCheck.ne(User::getUserId, userId);
                        long count = userService.count(dupCheck);
                        if (count > 0) {
                            response.put("success", false);
                            response.put("message", "账号已存在，请更换其他账号");
                            return ResponseEntity.badRequest().body(response);
                        }
                    }
                    user.setAccount(newAccount);
                }
            }
            if (payload.containsKey("nickname")) {
                String nickname = (String) payload.get("nickname");
                if (!StringUtils.hasText(nickname)) {
                    response.put("success", false);
                    response.put("message", "昵称不能为空");
                    return ResponseEntity.badRequest().body(response);
                }
                user.setNickname(nickname);
            }
            if (payload.containsKey("phone")) {
                String phone = (String) payload.get("phone");
                if (!StringUtils.hasText(phone)) {
                    response.put("success", false);
                    response.put("message", "手机号不能为空");
                    return ResponseEntity.badRequest().body(response);
                }
                user.setPhone(phone);
            }
            if (payload.containsKey("gender")) {
                user.setGender((String) payload.get("gender"));
            }
            if (payload.containsKey("accountStatus")) {
                user.setAccountStatus((String) payload.get("accountStatus"));
            }
            if (payload.containsKey("address")) {
                user.setAddress((String) payload.get("address"));
            }
            if (payload.containsKey("remarks")) {
                user.setRemarks((String) payload.get("remarks"));
            }

            boolean success = userService.updateById(user);
            if (success) {
                response.put("success", true);
                response.put("message", "用户信息更新成功");
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户信息更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户信息时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUserByAdmin(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String account = payload.get("account") != null ? payload.get("account").toString().trim() : null;
            if (!StringUtils.hasText(account)) {
                response.put("success", false);
                response.put("message", "账号不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            String nickname = payload.get("nickname") != null ? payload.get("nickname").toString().trim() : null;
            if (!StringUtils.hasText(nickname)) {
                response.put("success", false);
                response.put("message", "昵称不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            String phone = payload.get("phone") != null ? payload.get("phone").toString().trim() : null;
            if (!StringUtils.hasText(phone)) {
                response.put("success", false);
                response.put("message", "手机号不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 校验账号唯一性
            LambdaQueryWrapper<User> dupCheck = new LambdaQueryWrapper<>();
            dupCheck.eq(User::getAccount, account);
            if (userService.count(dupCheck) > 0) {
                response.put("success", false);
                response.put("message", "账号已存在，请更换其他账号");
                return ResponseEntity.badRequest().body(response);
            }

            User user = new User();
            user.setAccount(account);
            user.setNickname(nickname);
            user.setPhone(phone);
            user.setGender((String) payload.getOrDefault("gender", "未知"));
            user.setAccountStatus((String) payload.getOrDefault("accountStatus", "active"));
            user.setAddress((String) payload.getOrDefault("address", null));
            user.setRemarks((String) payload.getOrDefault("remarks", null));
            user.setUserType("user");
            // 使用系统时间作为创建时间/更新时间
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // 设置默认密码（BCrypt）
            String defaultPlainPassword = "Gxu123456";
            String hashedPassword = cryptoService.encryptPasswordWithBCrypt(defaultPlainPassword);
            user.setPassword(hashedPassword);

            boolean saved = userService.save(user);
            if (saved) {
                response.put("success", true);
                response.put("message", "用户创建成功，默认密码为 Gxu123456");
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户创建失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建用户时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<Map<String, Object>> resetUserPassword(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            String defaultPlainPassword = "Gxu123456";
            String hashedPassword = cryptoService.encryptPasswordWithBCrypt(defaultPlainPassword);
            user.setPassword(hashedPassword);

            boolean success = userService.updateById(user);
            if (success) {
                response.put("success", true);
                response.put("message", "密码已重置为默认值");
                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getUserId());
                data.put("account", user.getAccount());
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "密码重置失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "密码重置时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/corpus")
    public ResponseEntity<Map<String, Object>> getCorpusList(@RequestParam Map<String, String> params) {
        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int size = Integer.parseInt(params.getOrDefault("size", "10"));
            String collectionName = params.get("collectionName");
            String creatorAccount = params.get("creatorAccount");
            
            System.out.println("=== 语料查询调试 ===");
            System.out.println("页码: " + page + ", 大小: " + size);
            System.out.println("语料名称: " + collectionName);
            System.out.println("创建者账号: " + creatorAccount);
            
            LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(collectionName)) {
                queryWrapper.like(Corpus::getCollectionName, collectionName);
            }
            
            // 如果按用户账号搜索，先查找用户ID
            if (StringUtils.hasText(creatorAccount)) {
                LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
                userQueryWrapper.like(User::getAccount, creatorAccount);
                List<User> users = userService.list(userQueryWrapper);
                
                if (!users.isEmpty()) {
                    List<Integer> userIds = new ArrayList<>();
                    for (User user : users) {
                        userIds.add(user.getUserId());
                    }
                    queryWrapper.in(Corpus::getCreatorId, userIds);
                } else {
                    // 如果没有找到匹配的用户，返回空结果
                    DPage<CorpusWithUserInfo> emptyResult = new DPage<>(new ArrayList<>(), 0L, page, size);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", emptyResult);
                    return ResponseEntity.ok(response);
                }
            }
            
            queryWrapper.orderByDesc(Corpus::getCreatedAt);

            IPage<Corpus> corpusPage = corpusService.page(new Page<>(page, size), queryWrapper);
            System.out.println("查询到的语料数量: " + corpusPage.getRecords().size());
            
            // 获取所有相关用户信息
            Set<Integer> creatorIdSet = new HashSet<>();
            for (Corpus corpus : corpusPage.getRecords()) {
                creatorIdSet.add(corpus.getCreatorId());
            }
            List<Integer> creatorIds = new ArrayList<>(creatorIdSet);
            System.out.println("需要查询的用户ID: " + creatorIds);
            
            Map<Integer, User> userMap = new HashMap<>();
            // 只有当creatorIds不为空时才查询用户信息
            if (!creatorIds.isEmpty()) {
                List<User> users = userService.listByIds(creatorIds);
                System.out.println("查询到的用户数量: " + users.size());
                for (User user : users) {
                    userMap.put(user.getUserId(), user);
                }
            }
            
            // 转换为包含用户信息的DTO
            List<CorpusWithUserInfo> corpusWithUserInfoList = new ArrayList<>();
            for (Corpus corpus : corpusPage.getRecords()) {
                CorpusWithUserInfo dto = new CorpusWithUserInfo();
                dto.setCorpusId(corpus.getCorpusId());
                dto.setCollectionName(corpus.getCollectionName());
                dto.setCountry(corpus.getCountry());
                dto.setDomain(corpus.getDomain());
                dto.setLanguage(corpus.getLanguage());
                dto.setDataFormat(corpus.getDataFormat());
                dto.setClassification(corpus.getClassification());
                dto.setDataVolume(corpus.getDataVolume());
                dto.setVolumeUnit(corpus.getVolumeUnit());
                dto.setEstimatedCapacityGb(corpus.getEstimatedCapacityGb());
                dto.setDataYear(corpus.getDataYear());
                dto.setSourceLocation(corpus.getSourceLocation());
                dto.setDataSource(corpus.getDataSource());
                dto.setProvider(corpus.getProvider());
                dto.setProviderContact(corpus.getProviderContact());
                dto.setRemarks(corpus.getRemarks());
                dto.setCreatorId(corpus.getCreatorId());
                dto.setCreatedAt(corpus.getCreatedAt());
                
                User creator = userMap.get(corpus.getCreatorId());
                if (creator != null) {
                    dto.setCreatorAccount(creator.getAccount());
                    dto.setCreatorNickname(creator.getNickname());
                    dto.setCreatorUserType(creator.getUserType());
                }
                
                corpusWithUserInfoList.add(dto);
            }
            
            DPage<CorpusWithUserInfo> result = new DPage<>(corpusWithUserInfoList, corpusPage.getTotal(), page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            System.out.println("语料查询成功，返回 " + result.getList().size() + " 条记录");
            System.out.println("===================");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("语料查询异常: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取语料列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/corpus/transfer")
    @Transactional
    public ResponseEntity<Map<String, Object>> transferCorpus(@RequestBody CorpusTransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (request.getCorpusIds() == null || request.getCorpusIds().isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要转移的语料");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getTargetUserId() == null) {
                response.put("success", false);
                response.put("message", "请选择目标用户");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证目标用户是否存在
            User targetUser = userService.getById(request.getTargetUserId());
            if (targetUser == null) {
                response.put("success", false);
                response.put("message", "目标用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            List<Corpus> corporaToUpdate = corpusService.listByIds(request.getCorpusIds());
            if (corporaToUpdate.size() != request.getCorpusIds().size()) {
                response.put("success", false);
                response.put("message", "部分语料不存在，请检查选择的语料");
                return ResponseEntity.badRequest().body(response);
            }

            for (Corpus corpus : corporaToUpdate) {
                corpus.setCreatorId(request.getTargetUserId());
            }

            boolean success = corpusService.updateBatchById(corporaToUpdate);
            
            if (success) {
                response.put("success", true);
                response.put("message", "语料转移成功");
                Map<String, Object> data = new HashMap<>();
                data.put("transferredCount", corporaToUpdate.size());
                data.put("targetUser", targetUser.getAccount());
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "语料转移失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "转移过程中发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/users/{userId}/update-role")
    public ResponseEntity<Map<String, Object>> updateUserRole(@PathVariable Integer userId, @RequestBody UpdateUserRoleRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userId == null || request.getUserType() == null) {
                response.put("success", false);
                response.put("message", "用户ID和用户类型不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证用户类型值
            if (!"user".equals(request.getUserType()) && !"admin".equals(request.getUserType())) {
                response.put("success", false);
                response.put("message", "用户类型只能是 'user' 或 'admin'");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 获取要修改的用户
            User targetUser = userService.getById(userId);
            if (targetUser == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 防止管理员取消自己的管理员权限（至少保留一个管理员）
            User currentUser = UserContext.getCurrentUser();
            if (currentUser != null && currentUser.getUserId().equals(userId) && "user".equals(request.getUserType())) {
                // 检查是否还有其他管理员
                LambdaQueryWrapper<User> adminQuery = new LambdaQueryWrapper<>();
                adminQuery.eq(User::getUserType, "admin");
                adminQuery.ne(User::getUserId, userId);
                long adminCount = userService.count(adminQuery);
                
                if (adminCount == 0) {
                    response.put("success", false);
                    response.put("message", "不能取消最后一个管理员的权限");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            // 更新用户权限
            String oldUserType = targetUser.getUserType();
            targetUser.setUserType(request.getUserType());
            boolean success = userService.updateById(targetUser);
            
            if (success) {
                response.put("success", true);
                response.put("message", "用户权限修改成功");
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId);
                data.put("account", targetUser.getAccount());
                data.put("oldUserType", oldUserType);
                data.put("newUserType", request.getUserType());
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("message", "用户权限修改失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "修改用户权限时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // DTO类用于返回包含用户信息的语料数据
    public static class CorpusWithUserInfo {
        private Integer corpusId;
        private String country;
        private String collectionName;
        private String domain;
        private String language;
        private String dataFormat;
        private String classification;
        private Double dataVolume;
        private String volumeUnit;
        private Double estimatedCapacityGb;
        private String dataYear;
        private String sourceLocation;
        private String dataSource;
        private String provider;
        private String providerContact;
        private String remarks;
        private Integer creatorId;
        private LocalDateTime createdAt;
        
        // 用户信息字段
        private String creatorAccount;
        private String creatorNickname;
        private String creatorUserType;

        // Getters and Setters
        public Integer getCorpusId() { return corpusId; }
        public void setCorpusId(Integer corpusId) { this.corpusId = corpusId; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getCollectionName() { return collectionName; }
        public void setCollectionName(String collectionName) { this.collectionName = collectionName; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public String getDataFormat() { return dataFormat; }
        public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }

        public String getClassification() { return classification; }
        public void setClassification(String classification) { this.classification = classification; }

        public Double getDataVolume() { return dataVolume; }
        public void setDataVolume(Double dataVolume) { this.dataVolume = dataVolume; }

        public String getVolumeUnit() { return volumeUnit; }
        public void setVolumeUnit(String volumeUnit) { this.volumeUnit = volumeUnit; }

        public Double getEstimatedCapacityGb() { return estimatedCapacityGb; }
        public void setEstimatedCapacityGb(Double estimatedCapacityGb) { this.estimatedCapacityGb = estimatedCapacityGb; }

        public String getDataYear() { return dataYear; }
        public void setDataYear(String dataYear) { this.dataYear = dataYear; }

        public String getSourceLocation() { return sourceLocation; }
        public void setSourceLocation(String sourceLocation) { this.sourceLocation = sourceLocation; }

        public String getDataSource() { return dataSource; }
        public void setDataSource(String dataSource) { this.dataSource = dataSource; }

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }

        public String getProviderContact() { return providerContact; }
        public void setProviderContact(String providerContact) { this.providerContact = providerContact; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }

        public Integer getCreatorId() { return creatorId; }
        public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public String getCreatorAccount() { return creatorAccount; }
        public void setCreatorAccount(String creatorAccount) { this.creatorAccount = creatorAccount; }

        public String getCreatorNickname() { return creatorNickname; }
        public void setCreatorNickname(String creatorNickname) { this.creatorNickname = creatorNickname; }

        public String getCreatorUserType() { return creatorUserType; }
        public void setCreatorUserType(String creatorUserType) { this.creatorUserType = creatorUserType; }
    }

    public static class CorpusTransferRequest {
        private List<Integer> corpusIds;
        private Integer targetUserId;

        public List<Integer> getCorpusIds() { return corpusIds; }
        public void setCorpusIds(List<Integer> corpusIds) { this.corpusIds = corpusIds; }
        public Integer getTargetUserId() { return targetUserId; }
        public void setTargetUserId(Integer targetUserId) { this.targetUserId = targetUserId; }
    }

    // 修改用户权限请求类
    public static class UpdateUserRoleRequest {
        private String userType;

        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }
} 