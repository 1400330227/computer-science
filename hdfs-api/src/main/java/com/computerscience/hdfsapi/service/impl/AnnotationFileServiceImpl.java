package com.computerscience.hdfsapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.computerscience.hdfsapi.api.HdfsApi;
import com.computerscience.hdfsapi.enums.AnnotationFileFormat;
import com.computerscience.hdfsapi.enums.DataFormat;
import com.computerscience.hdfsapi.mapper.AnnotationFileDetailsMapper;
import com.computerscience.hdfsapi.mapper.AnnotationFileMapper;
import com.computerscience.hdfsapi.mapper.FileMapper; // 假设存在
import com.computerscience.hdfsapi.model.AnnotationFile;
import com.computerscience.hdfsapi.model.AnnotationFileDetails;
import com.computerscience.hdfsapi.model.FileEntity; // 假设存在
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.AnnotationFileService;
import com.computerscience.hdfsapi.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; // 避免类名冲突
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class AnnotationFileServiceImpl extends ServiceImpl<AnnotationFileMapper, AnnotationFile> implements AnnotationFileService {

    @Autowired
    private AnnotationFileDetailsMapper detailsMapper;

    @Autowired
    private FileMapper filesMapper; // 需要根据fileId查corpusId

    @Value("${hadoop.hdfs.annotations-path}") // 模拟HDFS路径
    private String uploadBasePath;

    @Autowired
    @Qualifier("conf")
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String user;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnnotationFile uploadAnnotation(MultipartFile file, Integer originalFileId, Integer userId) {
        // 1. 校验
        DataFormat dataFormat = AnnotationFileFormat.getByFileName(file.getOriginalFilename());
        if (file.isEmpty() || dataFormat == null) {
            throw new RuntimeException("文件为空或格式不正确（仅支持.txt）");
        }

        // 2. 获取原始文件信息以确定 corpusId
        FileEntity originalFile = filesMapper.selectById(originalFileId);
        if (originalFile == null) {
            throw new RuntimeException("原始文件不存在");
        }
        User currentUser = UserContext.getCurrentUser();
        try {
            // 3. 解析文件内容计算 QA 对数量 (简单逻辑：假设一行 Q 一行 A，或者根据特定标记)
            // 这里简单读取文件行数作为示例，实际需根据业务规则解析
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            int qaCount = calculateQaPairs(content);

            // 4. 保存文件到磁盘/HDFS
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String hdfsPath = uploadBasePath + currentUser.getAccount() + originalFile.getCorpusId() + "/" + originalFilename;;

            HdfsApi hdfsApi = new HdfsApi(conf, user);
            InputStream inputStream = file.getInputStream();
            hdfsApi.upLoadFile(inputStream, hdfsPath);
            hdfsApi.close();


            // 5. 保存详情表
            AnnotationFileDetails details = new AnnotationFileDetails();
            details.setQaPairCount(qaCount);
            detailsMapper.insert(details);

            // 6. 保存主表
            // 注意：因为是 1对N，这里直接新增。如果业务要求每个原始文件只能有一个最新标注，需先查询并更新状态。
            AnnotationFile annotationFile = new AnnotationFile();
            annotationFile.setFileId(originalFileId);
            annotationFile.setCorpusId(originalFile.getCorpusId());
            annotationFile.setCreatorId(userId);
            annotationFile.setAnnotationFileDetailsId(details.getId());
            annotationFile.setTitle(originalFilename);
            annotationFile.setFileType("txt");
            annotationFile.setAnnotationFilePath(hdfsPath);
            annotationFile.setStatus("VALIDATED");
            annotationFile.setCreatedAt(LocalDateTime.now());

            this.save(annotationFile);

            // 回填 details 供前端展示
            annotationFile.setDetails(details);
            return annotationFile;

        } catch (InterruptedException | IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnotation(Integer annotationId) {
        AnnotationFile annotationFile = this.getById(annotationId);
        if (annotationFile == null) {
            throw new RuntimeException("标注文件不存在");
        }

        // 1. 删除物理文件 (可选，根据业务需求是否保留历史)
        try {
            Files.deleteIfExists(Paths.get(annotationFile.getAnnotationFilePath()));
        } catch (IOException e) {
            log.warn("物理文件删除失败: {}", annotationFile.getAnnotationFilePath());
        }

        // 2. 删除数据库记录
        this.removeById(annotationId);
        detailsMapper.deleteById(annotationFile.getAnnotationFileDetailsId());
    }

    @Override
    public void downloadAnnotation(Integer annotationId, HttpServletResponse response) {
        AnnotationFile annotationFile = this.getById(annotationId);
        if (annotationFile == null) throw new RuntimeException("文件不存在");

        File file = new File(annotationFile.getAnnotationFilePath());
        if (!file.exists()) throw new RuntimeException("物理文件丢失");

        downloadFile(file, annotationFile.getTitle(), response);
    }

    @Override
    public void downloadAllAnnotations(Integer corpusId, HttpServletResponse response) {
        // 1. 查询该语料下所有标注文件
        List<AnnotationFile> list = this.list(new LambdaQueryWrapper<AnnotationFile>()
                .eq(AnnotationFile::getCorpusId, corpusId)
                .eq(AnnotationFile::getStatus, "VALIDATED"));

        if (list.isEmpty()) {
            throw new RuntimeException("该语料下暂无标注文件");
        }

        // 2. 设置响应头
        try {
            String zipName = "corpus_" + corpusId + "_annotations.zip";
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(zipName, "UTF-8"));

            // 3. 创建 ZIP 流
            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (AnnotationFile af : list) {
                    File file = new File(af.getAnnotationFilePath());
                    if (file.exists()) {
                        // 防止重名文件覆盖，可以加 ID 前缀
                        ZipEntry zipEntry = new ZipEntry(af.getId() + "_" + af.getTitle());
                        zos.putNextEntry(zipEntry);
                        Files.copy(file.toPath(), zos);
                        zos.closeEntry();
                    }
                }
                zos.finish();
            }
        } catch (IOException e) {
            log.error("批量下载失败", e);
            throw new RuntimeException("批量下载失败");
        }
    }

    // --- 辅助方法 ---

    /**
     * 解析内容计算 QA 对数量
     * 简单模拟：统计 "Q:" 或 "Ask:" 出现的次数，或者非空行数/2
     */
    private int calculateQaPairs(String content) {
        if (!StringUtils.hasText(content)) {
            return 0;
        }

        // 使用正则表达式匹配所有问题和答案对
        // 匹配模式：问题：xxx 答案：xxx
        Pattern pattern = Pattern.compile(
                "问题：\\s*([^\\n]+?)\\s*答案：\\s*([^\\n]+)",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(content);
        int count = 0;

        while (matcher.find()) {
            String question = matcher.group(1).trim();
            String answer = matcher.group(2).trim();

            // 验证问题和答案不是空的占位符
            if (!question.isEmpty() && !answer.isEmpty() &&
                    !question.equals("在此处输入待标注的具体问题或文本片段") &&
                    !answer.equals("在此处输入与问题相对应的标准答案或正确标注结果")) {
                count++;
            }
        }

        return count;
    }

    // 更灵活的正则表达式，允许换行
    private int calculateQaPairsAdvanced(String content) {
        if (!StringUtils.hasText(content)) {
            return 0;
        }

        // 匹配：问题：(任意内容)答案：(任意内容)
        // 使用DOTALL模式让.匹配换行符
        Pattern pattern = Pattern.compile(
                "问题：\\s*(.*?)\\s*答案：\\s*(.*?)(?=\\s*(?:背景：|问题：|$))",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(content);
        int count = 0;

        while (matcher.find()) {
            String question = matcher.group(1).trim();
            String answer = matcher.group(2).trim();

            // 移除可能的换行符和多余空格
            question = question.replaceAll("\\s+", " ").trim();
            answer = answer.replaceAll("\\s+", " ").trim();

            if (!question.isEmpty() && !answer.isEmpty() &&
                    !question.matches("^在此处输入[\\s\\S]*$") &&
                    !answer.matches("^在此处输入[\\s\\S]*$")) {
                count++;
            }
        }

        return count;
    }

    private int countByBlockSeparation(String content) {
        // 用两个连续换行符分割（考虑到不同系统的换行符）
        String[] blocks = content.split("\\r\\n\\s*\\r\\n|\\r\\s*\\r|\\n\\s*\\n");
        int count = 0;

        for (String block : blocks) {
            if (isValidQaPair(block.trim())) {
                count++;
            }
        }

        return count;
    }

    private boolean isValidQaPair(String block) {
        if (block == null || block.trim().isEmpty()) {
            return false;
        }

        // 检查是否包含问题和答案
        if (!block.contains("问题：") || !block.contains("答案：")) {
            return false;
        }

        // 可选：检查问题在答案之前
        int questionIndex = block.indexOf("问题：");
        int answerIndex = block.indexOf("答案：");

        return questionIndex < answerIndex;
    }

    private int countByKeywords(String content) {
        // 统计"问题："出现的次数（假设每个问答对都有一个"问题："）
        Pattern pattern = Pattern.compile("问题：");
        Matcher matcher = pattern.matcher(content);
        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    private void downloadFile(File file, String fileName, HttpServletResponse response) {
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentLengthLong(file.length());

            byte[] buffer = new byte[4096];
            int b;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("下载文件出错", e);
        }
    }
}

