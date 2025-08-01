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

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 语料库控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/hdfs/corpus")
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
        return ResponseEntity.ok(corpusService.findCorpusPage(page, size, language, classification));
    }

    /**
     * 分页查询当前用户的语料库列表
     */
    @GetMapping("/my-corpus")
    public ResponseEntity<?> listMyCorpus(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String classification) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            return ResponseEntity.ok(corpusService.findUserCorpusPage(currentUser.getUserId(), page, size, language, classification));
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
            
            if (currentUser == null) {
                System.out.println("认证失败：用户未登录");
                return ResponseEntity.status(401).body("用户未登录");
            }
            
            System.out.println("认证成功，用户: " + currentUser.getAccount() + " (ID: " + currentUser.getUserId() + ")");

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
                return ResponseEntity.badRequest().body("创建语料库失败，名称可能已存在");
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
        if (corpusService.deleteCorpus(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("删除语料库失败，语料库可能不存在");
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
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

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
            String hdfsPath = "/corpus/" + corpusId + "/" + fileName;

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