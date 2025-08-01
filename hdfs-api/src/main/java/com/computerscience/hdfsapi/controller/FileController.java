package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.utils.UserContext;
import com.computerscience.hdfsapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

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
            if (currentUser == null) {
                System.out.println("认证失败：用户未登录");
                return ResponseEntity.status(401).body("用户未登录");
            }
            
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
            if (currentUser == null) {
                System.out.println("认证失败：用户未登录");
                return ResponseEntity.status(401).body("用户未登录");
            }
            
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
     * 分页查询文件列表
     */
    @GetMapping
    public ResponseEntity<IPage<FileEntity>> listFiles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) Integer corpusId) {
        return ResponseEntity.ok(fileService.findFilePage(page, size, fileType, corpusId));
    }

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
     * 示例：带权限检查的文件操作
     */
    @PostMapping("/upload-with-auth")
    public ResponseEntity<?> uploadWithAuth(@RequestParam("file") MultipartFile file) {
        try {
            // 检查用户是否登录
            if (!UserContext.isUserLoggedIn()) {
                return ResponseEntity.status(401).body("用户未登录");
            }

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