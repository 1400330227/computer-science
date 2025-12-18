package com.computerscience.hdfsapi.controller;

import com.computerscience.hdfsapi.model.AnnotationFile;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.AnnotationFileService;
import com.computerscience.hdfsapi.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/annotation-files")
public class AnnotationFileController {

    @Autowired
    private AnnotationFileService annotationFileService;

    /**
     * 上传标注文件
     * 对应前端: api.post('/qa-annotations/upload', form)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAnnotation(
            @RequestParam("file") MultipartFile file,
            @RequestParam("originalFileId") Integer originalFileId) {

        User currentUser = UserContext.getCurrentUser();
        // 模拟从 SecurityContext 获取当前用户ID
        Integer currentUserId = currentUser.getUserId(); // SecurityUtils.getUserId();

        AnnotationFile result = annotationFileService.uploadAnnotation(file, originalFileId, currentUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "上传成功");
        response.put("qaPairCount", result.getDetails().getQaPairCount());
        response.put("data", result);

        return ResponseEntity.ok(response);
    }

    /**
     * 删除标注文件
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnotation(@PathVariable Integer id) {
        annotationFileService.deleteAnnotation(id);
        return ResponseEntity.ok().body(Map.of("code", 200, "message", "删除成功"));
    }

    /**
     * 下载单个标注文件
     * 对应前端: getFileDownloadUrl -> 可能是单独的接口，或者公用下载接口
     */
    @GetMapping("/download/{id}")
    public void downloadAnnotation(@PathVariable Integer id, HttpServletResponse response) {
        annotationFileService.downloadAnnotation(id, response);
    }

    /**
     * 下载全部标注文件（根据语料ID）
     * 对应前端按钮: <el-button>下载全部标注文件</el-button>
     */
    @GetMapping("/download-all/{corpusId}")
    public void downloadAllAnnotations(@PathVariable Integer corpusId, HttpServletResponse response) {
        annotationFileService.downloadAllAnnotations(corpusId, response);
    }
}

