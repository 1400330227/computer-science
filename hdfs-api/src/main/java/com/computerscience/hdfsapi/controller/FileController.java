package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.utils.UserContext;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.api.HdfsApi;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.hadoop.conf.Configuration;

// 添加这些导入
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.computerscience.hdfsapi.utils.DPage;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 文件控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private CorpusService corpusService;

    @Autowired
    @Qualifier("conf")
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String user;

    /**
     * 根据ID查询文件（需要用户权限）
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFileById(@PathVariable Integer id) {
        try {
            // 调试信息
            System.out.println("=== 查询文件详情 ===");
            System.out.println("请求的文件ID: " + id);
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            System.out.println("当前用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");
            // 查询文件
            FileEntity file = fileService.getById(id);
            if (file == null) {
                System.out.println("文件不存在，ID: " + id);
                return ResponseEntity.notFound().build();
            }

            System.out.println("文件信息: " + file.getFileName() + " (创建者ID: " + file.getCreatorId() + ")");

            // 检查文件是否属于当前用户（通过创建者ID）
            if (!file.getCreatorId().equals(currentUser.getUserId())) {
                // 如果文件创建者ID不匹配，还需要检查文件所属语料是否属于当前用户
                if (file.getCorpusId() != null) {
                    Corpus corpus = corpusService.getById(file.getCorpusId());
                    if (corpus != null && corpus.getCreatorId().equals(currentUser.getUserId())) {
                        System.out.println("权限检查通过：文件属于用户的语料");
                    } else {
                        System.out.println("权限检查失败：用户 " + currentUser.getUserId() + " 尝试访问用户 " + file.getCreatorId() + " 的文件");
                        return ResponseEntity.status(403).body("无权限访问该文件");
                    }
                } else {
                    System.out.println("权限检查失败：文件创建者不匹配，且无语料关联");
                    return ResponseEntity.status(403).body("无权限访问该文件");
                }
            } else {
                System.out.println("权限检查通过：文件创建者匹配");
            }

            System.out.println("=================");
            return ResponseEntity.ok(file);

        } catch (Exception e) {
            System.err.println("查询文件失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("查询文件失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件（需要用户权限）
     */
    @GetMapping("/{id}/download")
    public void downloadFile(@PathVariable Integer id, HttpServletResponse response) {
        try {
            // 调试信息
            System.out.println("=== 下载文件 ===");
            System.out.println("请求的文件ID: " + id);
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            System.out.println("当前用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");
            // 查询文件
            FileEntity file = fileService.getById(id);
            if (file == null) {
                System.out.println("文件不存在，ID: " + id);
                response.setStatus(404);
                response.getWriter().write("文件不存在");
                return;
            }

            System.out.println("文件信息: " + file.getFileName() + " (创建者ID: " + file.getCreatorId() + ")");

            // 检查文件是否属于当前用户（通过创建者ID）
            if (!file.getCreatorId().equals(currentUser.getUserId())) {
                // 如果文件创建者ID不匹配，还需要检查文件所属语料是否属于当前用户
                if (file.getCorpusId() != null) {
                    Corpus corpus = corpusService.getById(file.getCorpusId());
                    if (corpus != null && corpus.getCreatorId().equals(currentUser.getUserId())) {
                        System.out.println("权限检查通过：文件属于用户的语料");
                    } else {
                        System.out.println("权限检查失败：用户 " + currentUser.getUserId() + " 尝试访问用户 " + file.getCreatorId() + " 的文件");
                        response.setStatus(403);
                        response.getWriter().write("无权限访问该文件");
                        return;
                    }
                } else {
                    System.out.println("权限检查失败：文件创建者不匹配，且无语料关联");
                    response.setStatus(403);
                    response.getWriter().write("无权限访问该文件");
                    return;
                }
            } else {
                System.out.println("权限检查通过：文件创建者匹配");
            }

            // 设置响应头让浏览器立刻识别为下载
            String fileName = file.getFileName();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

//            // 立刻刷新响应头到客户端
//            response.flushBuffer();

            System.out.println("开始从HDFS下载文件: " + file.getFilePath());

            // 从HDFS下载文件
            HdfsApi hdfsApi = new HdfsApi(conf, user);
            String hdfsPath = file.getFilePath();
            long fileSize = hdfsApi.getFileSize(hdfsPath);

            if (fileSize < 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "HDFS上的文件不存在: " + hdfsPath);
                return;
            }
            response.setContentLengthLong(fileSize);

            hdfsApi.downLoadFile(hdfsPath, response, true);
            hdfsApi.close();

            System.out.println("文件下载完成: " + fileName);
            System.out.println("===============");

        } catch (Exception e) {
            System.err.println("文件下载失败: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 根据语料库ID查询文件列表（需要用户权限）
     */
    @GetMapping("/corpus/{corpusId}")
    public ResponseEntity<?> getFilesByCorpusId(@PathVariable Integer corpusId) {
        try {
            // 调试信息
            System.out.println("=== 查询语料文件列表 ===");
            System.out.println("请求的语料ID: " + corpusId);

            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();

            System.out.println("当前用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

            // 检查语料是否存在且属于当前用户
            Corpus corpus = corpusService.getById(corpusId);
            if (corpus == null) {
                System.out.println("语料不存在，ID: " + corpusId);
                return ResponseEntity.notFound().build();
            }

            System.out.println("语料信息: " + corpus.getCollectionName() + " (创建者ID: " + corpus.getCreatorId() + ")");

            if (!corpus.getCreatorId().equals(currentUser.getUserId())) {
                System.out.println("权限检查失败：用户 " + currentUser.getUserId() + " 尝试访问用户 " + corpus.getCreatorId() + " 的语料");
                return ResponseEntity.status(403).body("无权限访问该语料的文件列表");
            }

            System.out.println("权限检查通过，查询文件列表");

            // 查询文件列表
            List<FileEntity> files = fileService.findByCorpusId(corpusId);
            System.out.println("找到文件数量: " + (files != null ? files.size() : 0));

            if (files == null || files.isEmpty()) {
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }

            System.out.println("===================");
            return ResponseEntity.ok(files);

        } catch (Exception e) {
            System.err.println("查询文件列表失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("查询文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询当前用户的所有文件
     */
    @GetMapping("/my-files")
    public ResponseEntity<?> getMyFiles(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "dataFormat", required = false) String dataFormat,
            @RequestParam(value = "corpusName", required = false) String corpusName,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "classification", required = false) String classification,
            @RequestParam(value = "dataYear", required = false) String dataYear,
            @RequestParam(value = "startDataYear", required = false) String startDataYear,
            @RequestParam(value = "endDataYear", required = false) String endDataYear,
            @RequestParam(value = "corpusId", required = false) Integer corpusId) {
        try {
            // 调试信息
            System.out.println("=== 查询当前用户的所有文件（分页+筛选） ===");
            System.out.println("页码: " + page + ", 每页大小: " + size);
            System.out.println("文件名: " + fileName);
            System.out.println("数据格式: " + dataFormat);
            System.out.println("语料ID: " + corpusId);
            System.out.println("语料名称: " + corpusName);
            System.out.println("国家: " + country);
            System.out.println("所属领域: " + domain);
            System.out.println("语种: " + language);
            System.out.println("数据分类: " + classification);
            System.out.println("数据年份: " + dataYear);
            System.out.println("起始年份: " + startDataYear);
            System.out.println("终止年份: " + endDataYear);

            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            System.out.println("当前用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

            // 先根据语料条件筛选出符合条件的语料ID
            List<Integer> filteredCorpusIds = null;
            boolean hasCorpusFilters = StringUtils.hasText(corpusName) || StringUtils.hasText(country) ||
                    StringUtils.hasText(domain) || StringUtils.hasText(language) || StringUtils.hasText(classification) ||
                    StringUtils.hasText(dataYear) || StringUtils.hasText(startDataYear) || StringUtils.hasText(endDataYear) || corpusId != null;

            if (hasCorpusFilters) {
                LambdaQueryWrapper<Corpus> corpusQueryWrapper = new LambdaQueryWrapper<>();

                if (corpusId != null) {
                    corpusQueryWrapper.eq(Corpus::getCorpusId, corpusId);
                }
                if (StringUtils.hasText(corpusName)) {
                    corpusQueryWrapper.like(Corpus::getCollectionName, corpusName);
                }
                if (StringUtils.hasText(country)) {
                    corpusQueryWrapper.like(Corpus::getCountry, country);
                }
                if (StringUtils.hasText(domain)) {
                    corpusQueryWrapper.like(Corpus::getDomain, domain);
                }
                if (StringUtils.hasText(language)) {
                    corpusQueryWrapper.like(Corpus::getLanguage, language);
                }
                if (StringUtils.hasText(classification)) {
                    corpusQueryWrapper.like(Corpus::getClassification, classification);
                }
                if (StringUtils.hasText(dataYear)) {
                    corpusQueryWrapper.like(Corpus::getDataYear, dataYear);
                }
                if (StringUtils.hasText(startDataYear)) {
                    corpusQueryWrapper.ge(Corpus::getDataYear, startDataYear);
                }
                if (StringUtils.hasText(endDataYear)) {
                    corpusQueryWrapper.le(Corpus::getDataYear, endDataYear);
                }

                List<Corpus> filteredCorpusList = corpusService.list(corpusQueryWrapper);
                filteredCorpusIds = filteredCorpusList.stream()
                        .map(Corpus::getCorpusId)
                        .collect(Collectors.toList());

                if (filteredCorpusIds.isEmpty()) {
                    // 如果没有符合条件的语料，返回空结果
                    DPage<Map<String, Object>> emptyResult = new DPage<>(new ArrayList<>(), 0L, page, size);
                    return ResponseEntity.ok(emptyResult);
                }
            }

            // 查询文件列表（分页+筛选）
            IPage<FileEntity> pageResult = fileService.findFilePage(
                    page, size, currentUser.getUserId(), null, corpusId, fileName, dataFormat, filteredCorpusIds);

            System.out.println("总记录数: " + pageResult.getTotal());
            System.out.println("总页数: " + pageResult.getPages());
            System.out.println("当前页记录数: " + pageResult.getRecords().size());

            // 获取所有相关语料信息
            Set<Integer> corpusIdSet = new HashSet<>();
            for (FileEntity file : pageResult.getRecords()) {
                if (file.getCorpusId() != null) {
                    corpusIdSet.add(file.getCorpusId());
                }
            }

            Map<Integer, Corpus> corpusMap = new HashMap<>();
            if (!corpusIdSet.isEmpty()) {
                List<Integer> corpusIds = new ArrayList<>(corpusIdSet);
                List<Corpus> corpusList = corpusService.listByIds(corpusIds);
                for (Corpus corpus : corpusList) {
                    corpusMap.put(corpus.getCorpusId(), corpus);
                }
            }

            // 转换为包含语料信息的DTO
            List<Map<String, Object>> fileWithInfoList = new ArrayList<>();
            for (FileEntity file : pageResult.getRecords()) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileId", file.getFileId());
                fileInfo.put("fileName", file.getFileName());
                fileInfo.put("fileType", file.getFileType());
                fileInfo.put("filePath", file.getFilePath());
                fileInfo.put("size", file.getSize());
                fileInfo.put("creatorId", file.getCreatorId());
                fileInfo.put("corpusId", file.getCorpusId());
                fileInfo.put("createdAt", file.getCreatedAt());
                fileInfo.put("updatedAt", file.getUpdatedAt());
                fileInfo.put("dataFormat", file.getDataFormat());

                // 添加语料信息
                if (file.getCorpusId() != null) {
                    Corpus corpus = corpusMap.get(file.getCorpusId());
                    if (corpus != null) {
                        fileInfo.put("corpusName", corpus.getCollectionName());
                        fileInfo.put("corpusCountry", corpus.getCountry());
                        fileInfo.put("corpusLanguage", corpus.getLanguage());
                        fileInfo.put("corpusDomain", corpus.getDomain());
                        fileInfo.put("corpusDataYear", corpus.getDataYear());
                        fileInfo.put("corpusDataSource", corpus.getDataSource());
                        fileInfo.put("corpusClassification", corpus.getClassification());
                        fileInfo.put("corpusDataFormat", corpus.getDataFormat());
                    }
                }

                fileWithInfoList.add(fileInfo);
            }

            DPage<Map<String, Object>> result = new DPage<>(fileWithInfoList, pageResult.getTotal(), page, size);
            System.out.println("文件查询成功，返回 " + result.getList().size() + " 条记录");
            System.out.println("===================");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("查询用户文件列表失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("查询用户文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询文件列表
     */
//    @GetMapping
//    public ResponseEntity<IPage<FileEntity>> listFiles(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(required = false) String fileType,
//            @RequestParam(required = false) Integer corpusId) {
//        return ResponseEntity.ok(fileService.findFilePage(page, size, null, fileType, corpusId, null, null, null));
//    }

    /**
     * 创建文件
     */
    @PostMapping
    public ResponseEntity<?> createFile(@RequestBody FileEntity fileEntity) {
        if (fileService.createFile(fileEntity)) {
            return ResponseEntity.ok(fileEntity);
        } else {
            return ResponseEntity.badRequest().body("创建文件失败");
        }
    }

    /**
     * 更新文件信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFile(@PathVariable Integer id, @RequestBody FileEntity fileEntity) {
        fileEntity.setFileId(id);
        if (fileService.updateFile(fileEntity)) {
            return ResponseEntity.ok(fileEntity);
        } else {
            return ResponseEntity.badRequest().body("更新文件失败，文件可能不存在");
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer id) {
        if (fileService.deleteFile(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("删除文件失败，文件可能不存在");
        }
    }

    /**
     * 批量下载文件（打包为ZIP）
     */
    @PostMapping("/batch-download")
    public void batchDownloadFiles(@RequestBody List<Integer> fileIds, HttpServletResponse response) {
        try {
            // 调试信息
            System.out.println("=== 批量下载文件 ===");
            System.out.println("请求下载的文件ID列表: " + fileIds);
            
            if (fileIds == null || fileIds.isEmpty()) {
                response.setStatus(400);
                response.getWriter().write("文件ID列表不能为空");
                return;
            }

            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            System.out.println("当前用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

            // 查询所有文件并验证权限
            List<FileEntity> files = new ArrayList<>();
            for (Integer fileId : fileIds) {
                FileEntity file = fileService.getById(fileId);
                if (file == null) {
                    System.out.println("文件不存在，ID: " + fileId);
                    continue;
                }

                // 检查文件权限
                boolean hasPermission = false;
                if (file.getCreatorId().equals(currentUser.getUserId())) {
                    hasPermission = true;
                } else if (file.getCorpusId() != null) {
                    Corpus corpus = corpusService.getById(file.getCorpusId());
                    if (corpus != null && corpus.getCreatorId().equals(currentUser.getUserId())) {
                        hasPermission = true;
                    }
                }

                if (hasPermission) {
                    files.add(file);
                    System.out.println("文件权限检查通过: " + file.getFileName());
                } else {
                    System.out.println("文件权限检查失败: " + file.getFileName() + " (ID: " + fileId + ")");
                }
            }

            if (files.isEmpty()) {
                response.setStatus(403);
                response.getWriter().write("没有可下载的文件或权限不足");
                return;
            }

            HdfsApi hdfsApi = new HdfsApi(conf, user);

            // 设置响应头
            String zipFileName = "batch_download_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".zip";
            System.out.println("开始创建ZIP文件: " + zipFileName);

            // 创建临时ZIP文件
            File tempZipFile = null;
            ZipOutputStream zipOut = null;
            try {
                // 创建临时文件
                String timestamp = String.valueOf(System.currentTimeMillis());
                String random = String.valueOf(Thread.currentThread().getId());
                tempZipFile = File.createTempFile("batch_download_" + timestamp + "_" + random + "_", ".zip");

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

                System.out.println("批量下载完成");

            } catch (Exception e) {
                System.err.println("创建ZIP文件失败: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建下载包失败");
            } finally {
                // 确保资源被正确关闭
                if (zipOut != null) {
                    try {
                        zipOut.close();
                    } catch (Exception e) { /* 忽略关闭异常 */ }
                }
                hdfsApi.close();

                // 删除临时文件
                if (tempZipFile != null && tempZipFile.exists()) {
                    if (!tempZipFile.delete()) {
                        System.err.println("无法删除临时文件: " + tempZipFile.getAbsolutePath());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("批量下载失败: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(500);
                response.getWriter().write("批量下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 示例：带权限检查的文件操作
     */
    @PostMapping("/upload-with-auth")
    public ResponseEntity<?> uploadWithAuth(@RequestParam("file") MultipartFile file) {
        try {
                // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();

            // 检查用户权限（示例：仅管理员可上传）
            if (!"admin".equals(currentUser.getUserType())) {
                return ResponseEntity.status(403).body("权限不足，仅管理员可执行此操作");
            }

            // 执行文件上传逻辑...
            // fileService.uploadFile(file, currentUser.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("uploadedBy", currentUser.getAccount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }
}