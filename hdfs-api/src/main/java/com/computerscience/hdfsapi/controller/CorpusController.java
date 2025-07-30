package com.computerscience.hdfsapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.service.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 语料库控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/hdfs/corpus")
public class CorpusController {

    @Autowired
    private CorpusService corpusService;

    /**
     * 根据ID查询语料库
     */
    @GetMapping("/{id}")
    public ResponseEntity<Corpus> getCorpusById(@PathVariable Integer id) {
        Corpus corpus = corpusService.getById(id);
        if (corpus == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(corpus);
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
    public ResponseEntity<IPage<Corpus>> listCorpus(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String classification) {
        return ResponseEntity.ok(corpusService.findCorpusPage(page, size, language, classification));
    }

    /**
     * 创建语料库
     */
    @PostMapping
    public ResponseEntity<?> createCorpus(@RequestBody Corpus corpus) {
        if (corpusService.createCorpus(corpus)) {
            return ResponseEntity.ok(corpus);
        } else {
            return ResponseEntity.badRequest().body("创建语料库失败，名称可能已存在");
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
} 