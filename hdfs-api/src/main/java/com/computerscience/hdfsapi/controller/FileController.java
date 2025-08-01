package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.service.FileService;
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

    /**
     * 根据ID查询文件
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileEntity> getFileById(@PathVariable Integer id) {
        FileEntity file = fileService.getById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(file);
    }

    /**
     * 根据语料库ID查询文件列表
     */
    @GetMapping("/corpus/{corpusId}")
    public ResponseEntity<List<FileEntity>> getFilesByCorpusId(@PathVariable Integer corpusId) {
        List<FileEntity> files = fileService.findByCorpusId(corpusId);
        if (files == null || files.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(files);
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