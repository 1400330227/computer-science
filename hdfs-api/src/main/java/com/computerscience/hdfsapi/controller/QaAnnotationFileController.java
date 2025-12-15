package com.computerscience.hdfsapi.controller;

import com.computerscience.hdfsapi.model.QaAnnotationFile;
import com.computerscience.hdfsapi.service.QaAnnotationFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 问答对标注文件控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/qa-annotations")
public class QaAnnotationFileController {

    private final QaAnnotationFileService qaAnnotationFileService;

    public QaAnnotationFileController(QaAnnotationFileService qaAnnotationFileService) {
        this.qaAnnotationFileService = qaAnnotationFileService;
    }

    /**
     * 上传问答对标注文件
     * @param file 上传的 .txt 文件
     * @param originalFileId 关联的原始文件ID
     * @return 上传和处理结果
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAnnotationFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("originalFileId") Integer originalFileId) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传的文件不能为空");
        }

        try {
            QaAnnotationFile result = qaAnnotationFileService.processAnnotationFile(file, originalFileId);
            
            // 封装一个更友好的成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "文件上传和校验成功！");
            response.put("qaPairCount", result.getQaPairCount());
            response.put("hdfsPath", result.getAnnotationFilePath());
            response.put("recordId", result.getId());
            
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalAccessException e) {
            // 捕获可预期的校验错误，返回400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 捕获其他所有意外错误，返回500 Internal Server Error
            e.printStackTrace(); // 在服务器日志中打印完整的错误堆栈，方便排查问题
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
