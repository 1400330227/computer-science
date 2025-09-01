package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.computerscience.hdfsapi.dto.CorpusTransferRequest;
import com.computerscience.hdfsapi.dto.CorpusWithUserInfo;
import com.computerscience.hdfsapi.dto.UpdateUserRoleRequest;
import com.computerscience.hdfsapi.model.*;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.service.UserService;
import com.computerscience.hdfsapi.utils.DPage;
import com.computerscience.hdfsapi.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.api.HdfsApi;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private com.computerscience.hdfsapi.service.CryptoService cryptoService;

    @Autowired
    private FileService fileService;

    @Autowired
    @Qualifier("conf")
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String hdfsUser;

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
                    response.put("message", "姓名不能为空");
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
            if (payload.containsKey("college")) {
                user.setCollege((String) payload.get("college"));
            }
            if (payload.containsKey("title")) {
                user.setTitle((String) payload.get("title"));
            }
            if (payload.containsKey("major")) {
                user.setMajor((String) payload.get("major"));
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
                response.put("message", "姓名不能为空");
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
            user.setCollege((String) payload.getOrDefault("college", null));
            user.setTitle((String) payload.getOrDefault("title", null));
            user.setMajor((String) payload.getOrDefault("major", null));
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




    // 修改用户权限请求类

    /**
     * 管理员根据语料ID下载文件（单文件直下，多文件打包ZIP）
     */
    @GetMapping("/corpus/{corpusId}/download")
    public void adminDownloadCorpus(@PathVariable Integer corpusId, HttpServletResponse response) {
        try {
            Corpus corpus = corpusService.getById(corpusId);
            if (corpus == null) {
                response.setStatus(404);
                response.getWriter().write("语料库不存在");
                return;
            }

            List<FileEntity> files = fileService.findByCorpusId(corpusId);
            if (files == null || files.isEmpty()) {
                response.setStatus(404);
                response.getWriter().write("该语料库下暂无文件");
                return;
            }

            if (files.size() == 1) {
                FileEntity file = files.get(0);
                String hdfsPath = file.getFilePath();
                String fileName = file.getFileName();

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");

                HdfsApi hdfsApi = new HdfsApi(conf, hdfsUser);
                long fileSize = hdfsApi.getFileSize(hdfsPath);
                if (fileSize < 0) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "HDFS上的文件不存在: " + hdfsPath);
                    hdfsApi.close();
                    return;
                }
                response.setContentLengthLong(fileSize);

                hdfsApi.downLoadFile(hdfsPath, response, true);
                hdfsApi.close();
            } else {
                String[] filePaths = files.stream().map(FileEntity::getFilePath).toArray(String[]::new);
                // 创建HDFS API连接
                HdfsApi hdfsApi = new HdfsApi(conf, hdfsUser);
//                long fileSize = hdfsApi.getFileSizes(filePaths);

//                if (fileSize < 0) {
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "HDFS上的文件不存在");
//                    hdfsApi.close();
//                    return;
//                }

                // 多个文件时，打包成ZIP下载
                String zipFileName = corpus.getCollectionName() + ".zip";

                // 创建临时ZIP文件
                File tempZipFile = null;
                ZipOutputStream zipOut = null;
                try {
                    // 创建临时文件
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String random = String.valueOf(Thread.currentThread().getId());
                    tempZipFile = File.createTempFile("download_" + timestamp + "_" + random + "_", ".zip");

                    // 创建ZIP输出流指向临时文件
                    zipOut = new ZipOutputStream(new FileOutputStream(tempZipFile));

                    int successCount = 0;
                    int failCount = 0;

                    for (FileEntity file : files) {
                        try {
                            String hdfsPath = file.getFilePath();
                            String fileName = file.getFileName();

                            System.out.println("添加文件到ZIP: " + fileName + " (HDFS: " + hdfsPath + ")");

                            // 添加ZIP条目
                            ZipEntry zipEntry = new ZipEntry(fileName);
                            zipOut.putNextEntry(zipEntry);

                            // 从HDFS读取文件并写入ZIP
                            Path sPath = new Path(hdfsPath);
                            try (InputStream inputStream = hdfsApi.getFs().open(sPath)) {
                                byte[] buffer = new byte[8192];
                                int length;
                                while ((length = inputStream.read(buffer)) > 0) {
                                    zipOut.write(buffer, 0, length);
                                }
                            }

                            zipOut.closeEntry();
                            successCount++;

                        } catch (Exception e) {
                            System.err.println("添加文件到ZIP失败: " + file.getFileName() + ", 错误: " + e.getMessage());
                            failCount++;
                            // 继续处理其他文件，不中断整个下载过程
                        }
                    }

                    // 如果有文件失败，在ZIP中添加一个说明文件
                    if (failCount > 0) {
                        try {
                            ZipEntry infoEntry = new ZipEntry("下载说明.txt");
                            zipOut.putNextEntry(infoEntry);
                            String info = "注意: " + failCount + " 个文件未能包含在此下载包中，请联系管理员。";
                            zipOut.write(info.getBytes(StandardCharsets.UTF_8));
                            zipOut.closeEntry();
                        } catch (Exception e) {
                            System.err.println("无法添加说明文件: " + e.getMessage());
                        }
                    }

                    // 关闭ZIP输出流
                    zipOut.close();
                    zipOut = null;

                    System.out.println("ZIP打包完成: 成功 " + successCount + " 个文件, 失败 " + failCount + " 个文件");
                    System.out.println("临时ZIP文件大小: " + tempZipFile.length() + " 字节");

                    // 设置HTTP响应头
                    response.setContentType("application/zip");
                    response.setHeader("Content-Disposition",
                            "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                    response.setHeader("X-Content-Type-Options", "nosniff");

                    // 设置准确的内容长度
                    response.setContentLengthLong(tempZipFile.length());

                    // 将临时文件内容写入响应输出流
                    try (InputStream fileInputStream = new FileInputStream(tempZipFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            response.getOutputStream().write(buffer, 0, bytesRead);
                        }
                    }

                    System.out.println("多文件打包下载完成");

                } catch (Exception e) {
                    System.err.println("创建ZIP文件失败: " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建下载包失败");
                } finally {
                    // 确保资源被正确关闭
                    if (zipOut != null) {
                        try { zipOut.close(); } catch (Exception e) { /* 忽略关闭异常 */ }
                    }
                    hdfsApi.close();

                    // 删除临时文件
                    if (tempZipFile != null && tempZipFile.exists()) {
                        if (!tempZipFile.delete()) {
                            System.err.println("无法删除临时文件: " + tempZipFile.getAbsolutePath());
                        }
                    }
                }
//                String[] filePaths = files.stream().map(FileEntity::getFilePath).toArray(String[]::new);
//                HdfsApi hdfsApi = new HdfsApi(conf, hdfsUser);
//                long totalSize = hdfsApi.getFileSizes(filePaths);
//                if (totalSize < 0) {
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "HDFS上的文件不存在");
//                    hdfsApi.close();
//                    return;
//                }
//
//                String zipFileName = corpus.getCollectionName() + ".zip";
//                response.setContentType("application/zip");
//                response.setHeader("Content-Disposition",
//                        "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));
//                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//                response.setHeader("Pragma", "no-cache");
//                response.setHeader("Expires", "0");
//                response.setContentLengthLong(totalSize);
//
//                ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
//                for (FileEntity file : files) {
//                    try {
//                        String hdfsPath = file.getFilePath();
//                        String fileName = file.getFileName();
//                        ZipEntry zipEntry = new ZipEntry(fileName);
//                        zipOut.putNextEntry(zipEntry);
//
//                        Path sPath = new Path(hdfsPath);
//                        InputStream inputStream = hdfsApi.getFs().open(sPath);
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        while ((length = inputStream.read(buffer)) > 0) {
//                            zipOut.write(buffer, 0, length);
//                        }
//                        inputStream.close();
//                        zipOut.closeEntry();
//                    } catch (Exception e) {
//                        // 继续打包其他文件
//                    }
//                }
//                zipOut.close();
//                hdfsApi.close();
            }
        } catch (Exception e) {
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                // ignore
            }
        }
    }

} 