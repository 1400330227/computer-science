package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 根据ID查询文件
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileEntity> getFileById(@PathVariable Long id) {
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
    public ResponseEntity<List<FileEntity>> getFilesByCorpusId(@PathVariable Long corpusId) {
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
            @RequestParam(required = false) Long corpusId) {
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
    public ResponseEntity<?> updateFile(@PathVariable Long id, @RequestBody FileEntity fileEntity) {
        fileEntity.setId(id);
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
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        if (fileService.deleteFile(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("删除文件失败，文件可能不存在");
        }
    }
} 