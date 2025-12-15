package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.Annotation;
import com.computerscience.hdfsapi.model.AnnotationLabel;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.AnnotationService;
import com.computerscience.hdfsapi.service.AnnotationLabelService;
import com.computerscience.hdfsapi.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标注控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/annotation")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private AnnotationLabelService annotationLabelService;

    /**
     * 根据ID查询标注
     */
    @GetMapping("/{id}")
    public ResponseEntity<Annotation> getAnnotationById(@PathVariable Integer id) {
        Annotation annotation = annotationService.getById(id);
        if (annotation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(annotation);
    }

    /**
     * 根据语料ID查询标注列表
     */
    @GetMapping("/corpus/{corpusId}")
    public ResponseEntity<List<Annotation>> getAnnotationsByCorpusId(@PathVariable Integer corpusId) {
        List<Annotation> annotations = annotationService.findByCorpusId(corpusId);
        return ResponseEntity.ok(annotations);
    }

    /**
     * 根据文件ID查询标注列表
     */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<Annotation>> getAnnotationsByFileId(@PathVariable Integer fileId) {
        List<Annotation> annotations = annotationService.findByFileId(fileId);
        return ResponseEntity.ok(annotations);
    }

    /**
     * 分页查询标注列表
     */
    @GetMapping
    public ResponseEntity<?> listAnnotations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer corpusId,
            @RequestParam(required = false) Integer fileId,
            @RequestParam(required = false) String annotationType,
            @RequestParam(required = false) Integer creatorId) {
        IPage<Annotation> annotationPage = annotationService.findAnnotationPage(
                page, size, corpusId, fileId, annotationType, creatorId);
        return ResponseEntity.ok(annotationPage);
    }

    /**
     * 创建标注
     */
    @PostMapping
    public ResponseEntity<?> createAnnotation(@RequestBody Annotation annotation) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            // 设置创建者ID
            annotation.setCreatorId(currentUser.getUserId());

            if (annotationService.createAnnotation(annotation)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标注创建成功");
                response.put("annotation", annotation);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标注创建失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建标注失败: " + e.getMessage());
        }
    }

    /**
     * 更新标注
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnnotation(@PathVariable Integer id, @RequestBody Annotation annotation) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            // 检查标注是否存在
            Annotation existingAnnotation = annotationService.getById(id);
            if (existingAnnotation == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查权限：只有创建者可以修改
            if (!existingAnnotation.getCreatorId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("无权限修改该标注");
            }

            annotation.setAnnotationId(id);
            if (annotationService.updateAnnotation(annotation)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标注更新成功");
                response.put("annotation", annotation);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标注更新失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新标注失败: " + e.getMessage());
        }
    }

    /**
     * 删除标注
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnotation(@PathVariable Integer id) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            // 检查标注是否存在
            Annotation annotation = annotationService.getById(id);
            if (annotation == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查权限：只有创建者可以删除
            if (!annotation.getCreatorId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("无权限删除该标注");
            }

            if (annotationService.deleteAnnotation(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标注删除成功");
                response.put("annotationId", id);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标注删除失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除标注失败: " + e.getMessage());
        }
    }

    /**
     * 统计语料的标注数量
     */
    @GetMapping("/corpus/{corpusId}/count")
    public ResponseEntity<?> countAnnotationsByCorpusId(@PathVariable Integer corpusId) {
        Long count = annotationService.countByCorpusId(corpusId);
        Map<String, Object> response = new HashMap<>();
        response.put("corpusId", corpusId);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ========== 标注标签相关接口 ==========

    /**
     * 查询所有标签
     */
    @GetMapping("/labels")
    public ResponseEntity<List<AnnotationLabel>> getAllLabels(
            @RequestParam(required = false) String category) {
        List<AnnotationLabel> labels;
        if (category != null && !category.isEmpty()) {
            labels = annotationLabelService.findByCategory(category);
        } else {
            labels = annotationLabelService.list();
        }
        return ResponseEntity.ok(labels);
    }

    /**
     * 查询系统默认标签
     */
    @GetMapping("/labels/system")
    public ResponseEntity<List<AnnotationLabel>> getSystemLabels() {
        List<AnnotationLabel> labels = annotationLabelService.findSystemLabels();
        return ResponseEntity.ok(labels);
    }

    /**
     * 创建标签
     */
    @PostMapping("/labels")
    public ResponseEntity<?> createLabel(@RequestBody AnnotationLabel label) {
        try {
            // 获取当前登录用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }

            // 设置创建者ID
            label.setCreatorId(currentUser.getUserId());

            if (annotationLabelService.createLabel(label)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标签创建成功");
                response.put("label", label);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标签创建失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建标签失败: " + e.getMessage());
        }
    }

    /**
     * 更新标签
     */
    @PutMapping("/labels/{id}")
    public ResponseEntity<?> updateLabel(@PathVariable Integer id, @RequestBody AnnotationLabel label) {
        try {
            label.setLabelId(id);
            if (annotationLabelService.updateLabel(label)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标签更新成功");
                response.put("label", label);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标签更新失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新标签失败: " + e.getMessage());
        }
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/labels/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Integer id) {
        try {
            if (annotationLabelService.deleteLabel(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "标签删除成功");
                response.put("labelId", id);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("标签删除失败，可能是系统标签或标签不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除标签失败: " + e.getMessage());
        }
    }
}

