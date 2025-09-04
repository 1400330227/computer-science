package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.utils.UserContext;
import com.computerscience.hdfsapi.api.HdfsApi;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.fs.Path;

import java.net.URLEncoder;
import com.computerscience.hdfsapi.service.UserService;
import java.util.Set;
import java.util.HashSet;

/**
 * 语料库控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private FileService fileService;

    @Autowired
    @Qualifier("conf")
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String user;

    @Autowired
    private UserService userService;

    /**
     * 根据ID查询语料库
     */
    @GetMapping("/{id}")
    public ResponseEntity<Corpus> getCorpusById(@PathVariable Integer id) {
        Corpus corpus = corpusService.getById(id);
        if (corpus == null) {
            return ResponseEntity.notFound().build();
        }

        // 调试信息：显示语料的详细信息
        System.out.println("=== 语料详情调试信息 ===");
        System.out.println("语料ID: " + corpus.getCorpusId());
        System.out.println("语料名称: " + corpus.getCollectionName());
        System.out.println("创建者ID: " + corpus.getCreatorId());
        System.out.println("创建时间: " + corpus.getCreatedAt());
        System.out.println("========================");

        return ResponseEntity.ok(corpus);
    }

    /**
     * 根据ID查询当前用户的语料库详情
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserCorpusById(@PathVariable Integer id) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            Corpus corpus = corpusService.getById(id);
            if (corpus == null) {
                return ResponseEntity.notFound().build();
            }

            // 调试信息：权限检查详情
            System.out.println("=== 权限检查调试信息 ===");
            System.out.println("当前登录用户ID: " + currentUser.getUserId());
            System.out.println("当前登录用户账号: " + currentUser.getAccount());
            System.out.println("语料ID: " + corpus.getCorpusId());
            System.out.println("语料名称: " + corpus.getCollectionName());
            System.out.println("语料创建者ID: " + corpus.getCreatorId());
            System.out.println("权限检查结果: " + (corpus.getCreatorId().equals(currentUser.getUserId()) ? "通过" : "失败"));
            System.out.println("========================");

            // 检查语料是否属于当前用户
            if (!corpus.getCreatorId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("无权限访问该语料信息");
            }

            return ResponseEntity.ok(corpus);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取语料详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据名称查询语料库
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Corpus> getCorpusByName(@PathVariable String name) {
        Corpus corpus = corpusService.findByName(name);
        if (corpus == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(corpus);
    }

    /**
     * 分页查询语料库列表
     */
    @GetMapping
    public ResponseEntity<?> listCorpus(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String classification) {
        IPage<Corpus> corpusPage = corpusService.findCorpusPage(page, size, language, classification);

        // 回填创建者信息映射
        Set<Integer> creatorIds = new HashSet<>();
        for (Corpus c : corpusPage.getRecords()) {
            if (c.getCreatorId() != null) creatorIds.add(c.getCreatorId());
        }

        Map<Integer, User> userMap = new HashMap<>();
        if (!creatorIds.isEmpty() && userService != null) {
            List<User> users = userService.listByIds(creatorIds);
            for (User u : users) {
                userMap.put(u.getUserId(), u);
            }
        }

        // 将结果转换为包含创建者信息的结构，但保持分页结构不变
        Map<String, Object> result = new HashMap<>();
        result.put("total", corpusPage.getTotal());
        result.put("size", corpusPage.getSize());
        result.put("current", corpusPage.getCurrent());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Corpus c : corpusPage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("corpusId", c.getCorpusId());
            item.put("country", c.getCountry());
            item.put("collectionName", c.getCollectionName());
            item.put("domain", c.getDomain());
            item.put("language", c.getLanguage());
            item.put("dataFormat", c.getDataFormat());
            item.put("classification", c.getClassification());
            item.put("dataVolume", c.getDataVolume());
            item.put("volumeUnit", c.getVolumeUnit());
            item.put("estimatedCapacityGb", c.getEstimatedCapacityGb());
            item.put("dataYear", c.getDataYear());
            item.put("sourceLocation", c.getSourceLocation());
            item.put("dataSource", c.getDataSource());
            item.put("provider", c.getProvider());
            item.put("providerContact", c.getProviderContact());
            item.put("remarks", c.getRemarks());
            item.put("creatorId", c.getCreatorId());
            item.put("createdAt", c.getCreatedAt());

            User creator = userMap.get(c.getCreatorId());
            if (creator != null) {
                item.put("creatorAccount", creator.getAccount());
                item.put("creatorNickname", creator.getNickname());
                item.put("creatorUserType", creator.getUserType());
            }
            records.add(item);
        }
        result.put("records", records);
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询当前用户的语料库列表
     */
    @GetMapping("/my-corpus")
    public ResponseEntity<?> listMyCorpus(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String classification,
            @RequestParam(required = false) String collectionName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false, defaultValue = "like") String searchType
    ) {
        try {
            User currentUser = UserContext.getCurrentUser();
            return ResponseEntity.ok(corpusService.findUserCorpusPage(currentUser.getUserId(), page, size, language, classification, collectionName, country, searchType));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取语料列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建语料库
     */
    @PostMapping
    public ResponseEntity<?> createCorpus(@RequestBody Corpus corpus) {
        try {
            // 调试信息：检查Session状态
            System.out.println("=== 创建语料认证检查 ===");
            Integer userId = UserContext.getCurrentUserId();
            System.out.println("从Session获取的用户ID: " + userId);

            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            System.out.println("用户对象: " + (currentUser != null ? currentUser.getAccount() : "null"));
            System.out.println("认证成功，用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

            // 调试信息：检查estimatedCapacityGb字段
            System.out.println("=== estimatedCapacityGb 字段调试信息 ===");
            System.out.println("接收到的estimatedCapacityGb值: " + corpus.getEstimatedCapacityGb());
            System.out.println("estimatedCapacityGb的类型: " + (corpus.getEstimatedCapacityGb() != null ? corpus.getEstimatedCapacityGb().getClass().getName() : "null"));
            if (corpus.getEstimatedCapacityGb() != null) {
                System.out.println("estimatedCapacityGb的字符串表示: " + corpus.getEstimatedCapacityGb().toString());
                System.out.println("estimatedCapacityGb的double值: " + corpus.getEstimatedCapacityGb().doubleValue());
            }
            
            // 如果值为null或0，尝试从请求体中重新解析
            if (corpus.getEstimatedCapacityGb() == null || corpus.getEstimatedCapacityGb() == 0.0) {
                System.out.println("检测到estimatedCapacityGb为null或0，尝试手动解析...");
                // 这里可以添加手动解析逻辑
            }
            System.out.println("=====================================");

            // 设置创建者ID
            corpus.setCreatorId(currentUser.getUserId());

            if (corpusService.createCorpus(corpus)) {
                System.out.println("=== 语料创建成功 ===");
                System.out.println("语料ID: " + corpus.getCorpusId());
                System.out.println("语料名称: " + corpus.getCollectionName());
                System.out.println("创建者ID: " + corpus.getCreatorId());
                System.out.println("====================");

                return ResponseEntity.ok(corpus);
            } else {
                System.out.println("=== 语料创建失败 ===");
                System.out.println("失败原因: 语料名称已存在");
                System.out.println("语料名称: " + corpus.getCollectionName());
                System.out.println("用户ID: " + corpus.getCreatorId());
                System.out.println("==================");
                
                return ResponseEntity.badRequest().body("语料名称 \"" + corpus.getCollectionName() + "\" 已存在，请使用其他名称");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建语料库失败: " + e.getMessage());
        }
    }

    /**
     * 更新语料库信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCorpus(@PathVariable Integer id, @RequestBody Corpus corpus) {
        corpus.setCorpusId(id);
        if (corpusService.updateCorpus(corpus)) {
            return ResponseEntity.ok(corpus);
        } else {
            return ResponseEntity.badRequest().body("更新语料库失败，语料库可能不存在");
        }
    }

    /**
     * 删除语料库
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCorpus(@PathVariable Integer id) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            // 验证语料库是否存在且属于当前用户
            Corpus corpus = corpusService.getById(id);
            if (corpus == null) {
                return ResponseEntity.notFound().build();
            }

            if (!corpus.getCreatorId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("无权限删除该语料库");
            }

            System.out.println("=== 语料删除开始 ===");
            System.out.println("语料ID: " + id);
            System.out.println("语料名称: " + corpus.getCollectionName());
            System.out.println("删除用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

            // 获取语料库下的所有文件
            List<FileEntity> files = fileService.findByCorpusId(id);
            System.out.println("关联文件数量: " + (files != null ? files.size() : 0));

            // 删除HDFS上的文件
            if (files != null && !files.isEmpty()) {
                HdfsApi hdfsApi = new HdfsApi(conf, user);
                try {
                    for (FileEntity file : files) {
                        String hdfsPath = file.getFilePath();
                        System.out.println("删除HDFS文件: " + hdfsPath);
                        
                        // 检查文件是否存在
                        if (hdfsApi.existFile(hdfsPath)) {
                            // 删除文件（递归删除，跳过回收站）
                            boolean deleted = hdfsApi.rmdir(hdfsPath, false, true);
                            if (deleted) {
                                System.out.println("HDFS文件删除成功: " + hdfsPath);
                            } else {
                                System.out.println("HDFS文件删除失败: " + hdfsPath);
                            }
                        } else {
                            System.out.println("HDFS文件不存在，跳过删除: " + hdfsPath);
                        }
                    }
                    
                    // 尝试删除语料库目录
                    String corpusDir = "/corpus/" + currentUser.getAccount() + id;
                    if (hdfsApi.exists(corpusDir)) {
                        boolean dirDeleted = hdfsApi.rmdir(corpusDir, true, true);
                        if (dirDeleted) {
                            System.out.println("语料库目录删除成功: " + corpusDir);
                        } else {
                            System.out.println("语料库目录删除失败: " + corpusDir);
                        }
                    } else {
                        System.out.println("语料库目录不存在，跳过删除: " + corpusDir);
                    }
                } finally {
                    hdfsApi.close();
                }
            }

            // 删除数据库中的文件记录
            boolean filesDeleted = fileService.deleteFilesByCorpusId(id);
            System.out.println("数据库文件记录删除结果: " + filesDeleted);

            // 删除语料库记录
            boolean corpusDeleted = corpusService.deleteCorpus(id);
            System.out.println("数据库语料库记录删除结果: " + corpusDeleted);

            if (corpusDeleted) {
                System.out.println("=== 语料删除完成 ===");
                System.out.println("==================");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "语料库删除成功");
                response.put("corpusId", id);
                response.put("corpusName", corpus.getCollectionName());
                response.put("deletedFilesCount", files != null ? files.size() : 0);
                
                return ResponseEntity.ok(response);
            } else {
                System.out.println("=== 语料删除失败 ===");
                System.out.println("==================");
                return ResponseEntity.badRequest().body("删除语料库失败，语料库可能不存在");
            }

        } catch (Exception e) {
            System.err.println("删除语料库异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除语料库失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到语料库
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("corpusId") Integer corpusId) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            // 验证语料库是否存在且属于当前用户
            Corpus corpus = corpusService.getById(corpusId);
            if (corpus == null) {
                return ResponseEntity.badRequest().body("语料库不存在");
            }
            if (!corpus.getCreatorId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("无权限操作该语料库");
            }

            // 生成HDFS存储路径
            String fileName = file.getOriginalFilename();
            String hdfsPath = "/corpus/" + currentUser.getAccount() + corpusId + "/" + fileName;

            System.out.println("=== 文件上传开始 ===");
            System.out.println("文件名: " + fileName);
            System.out.println("语料ID: " + corpusId);
            System.out.println("HDFS路径: " + hdfsPath);
            System.out.println("文件大小: " + file.getSize() + " bytes");

            // 上传文件到HDFS
            HdfsApi hdfsApi = new HdfsApi(conf, user);
            InputStream inputStream = file.getInputStream();
            hdfsApi.upLoadFile(inputStream, hdfsPath);
            hdfsApi.close();

            System.out.println("HDFS上传成功");

            // 创建文件记录
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(fileName);
            fileEntity.setFileType(getFileExtension(fileName));
            fileEntity.setFilePath(hdfsPath);
            fileEntity.setCreatorId(currentUser.getUserId());
            fileEntity.setCorpusId(corpusId);
            fileEntity.setCreatedAt(LocalDateTime.now());
            
            // 设置文件大小，转换为GB并保留两位小数
//            double sizeInGB = (double) file.getSize() / (1024 * 1024 * 1024);
//            String formattedSize = String.format("%.2f", sizeInGB);
            fileEntity.setSize(String.valueOf(file.getSize()));

            // 保存文件记录到数据库
            if (fileService.save(fileEntity)) {
                System.out.println("数据库记录保存成功，文件ID: " + fileEntity.getFileId());
                System.out.println("==================");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "文件上传成功");
                response.put("fileId", fileEntity.getFileId());
                response.put("fileName", fileName);
                response.put("hdfsPath", hdfsPath);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).body("文件记录保存失败");
            }

        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{corpusId}")
    public void downloadCorpus(@PathVariable Integer corpusId, HttpServletResponse response) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            // 验证语料库是否存在且属于当前用户
            Corpus corpus = corpusService.getById(corpusId);
            if (corpus == null) {
                response.setStatus(404);
                response.getWriter().write("语料库不存在");
                return;
            }

            if (!corpus.getCreatorId().equals(currentUser.getUserId())) {
                response.setStatus(403);
                response.getWriter().write("无权限访问该语料库");
                return;
            }

            // 获取语料库下的所有文件
            List<FileEntity> files = fileService.findByCorpusId(corpusId);
            if (files == null || files.isEmpty()) {
                response.setStatus(404);
                response.getWriter().write("该语料库下暂无文件");
                return;
            }

            System.out.println("=== 语料下载开始 ===");
            System.out.println("语料ID: " + corpusId);
            System.out.println("语料名称: " + corpus.getCollectionName());
            System.out.println("文件数量: " + files.size());

            // 如果只有一个文件，直接下载该文件
            if (files.size() == 1) {
                FileEntity file = files.get(0);
                String hdfsPath = file.getFilePath();

                System.out.println("单文件下载，HDFS路径: " + hdfsPath);

                // 设置响应头
                String fileName = file.getFileName();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");

//                response.flushBuffer();

                // 从HDFS下载文件
                HdfsApi hdfsApi = new HdfsApi(conf, user);
                long fileSize = hdfsApi.getFileSize(hdfsPath);

                if (fileSize < 0) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "HDFS上的文件不存在: " + hdfsPath);
                    return;
                }

                response.setContentLengthLong(fileSize);

                System.out.println("获取到文件大小: " + fileSize + " bytes");

                hdfsApi.downLoadFile(hdfsPath, response, true);
                hdfsApi.close();

                System.out.println("单文件下载完成");
            } else {
//                String[] filePaths = files.stream().map(FileEntity::getFilePath).toArray(String[]::new);
                // 创建HDFS API连接
                HdfsApi hdfsApi = new HdfsApi(conf, user);
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
            }

            System.out.println("==================");

        } catch (Exception e) {
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                // ignore
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
} 