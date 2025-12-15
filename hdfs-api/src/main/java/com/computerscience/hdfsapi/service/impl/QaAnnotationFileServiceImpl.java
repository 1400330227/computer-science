package com.computerscience.hdfsapi.service.impl;

import com.computerscience.hdfsapi.api.HdfsApi;
import com.computerscience.hdfsapi.mapper.QaAnnotationFileMapper;
import com.computerscience.hdfsapi.model.Corpus;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.model.QaAnnotationFile;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.service.QaAnnotationFileService;
import com.computerscience.hdfsapi.service.CorpusService;
import com.computerscience.hdfsapi.utils.UserContext;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper; // Import SqlHelper

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 问答对标注文件的业务逻辑服务实现
 */
@Service
public class QaAnnotationFileServiceImpl implements QaAnnotationFileService {

    private final FileService fileService;
    private final CorpusService corpusService;
    private final Configuration hadoopConf;
    private final String hdfsUser;
    private final QaAnnotationFileMapper qaAnnotationFileMapper;

    @Autowired
    public QaAnnotationFileServiceImpl(FileService fileService,
                                       CorpusService corpusService,
                                       @Qualifier("conf") Configuration hadoopConf,
                                       @Value("${hadoop.hdfs.user}") String hdfsUser,
                                       QaAnnotationFileMapper qaAnnotationFileMapper) {
        this.fileService = fileService;
        this.corpusService = corpusService;
        this.hadoopConf = hadoopConf;
        this.hdfsUser = hdfsUser;
        this.qaAnnotationFileMapper = qaAnnotationFileMapper;
    }

    @Override
    public QaAnnotationFile processAnnotationFile(MultipartFile file, Integer originalFileId) throws Exception {
        // --- 步骤 1: 获取当前登录用户 ---
        User currentUser = UserContext.getCurrentUser();
// ... more code ...
        if (currentUser == null) {
            throw new IllegalStateException("用户未登录，无法进行操作");
        }

        // --- 步骤 2: 校验原始文件是否存在 ---
        FileEntity originalFile = fileService.getById(originalFileId);
// ... more code ...
        if (originalFile == null) {
            throw new IllegalArgumentException("关联的原始文件不存在，ID：" + originalFileId);
        }

        // --- 步骤 3: 文件名校验 ---
        String uploadedFileName = file.getOriginalFilename();
// ... more code ...
        // 移除原始文件的扩展名来构建期望的文件名
        String originalFileNameWithoutExt = originalFile.getFileName().substring(0, originalFile.getFileName().lastIndexOf('.'));
        String expectedFileName = originalFileNameWithoutExt + ".txt";

        if (uploadedFileName == null || !uploadedFileName.equals(expectedFileName)) {
            throw new IllegalArgumentException("上传的文件名不正确。期望的文件名为：" + expectedFileName);
        }

        // --- 步骤 4: 内容校验与解析 ---
        int qaPairCount = 0;
// ... more code ...
        List<String> linesBuffer = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 忽略完全空白的行，这可以增加格式的灵活性
                if (line.trim().isEmpty()) {
                    continue;
                }
                linesBuffer.add(line);
            }
        }

        if (linesBuffer.isEmpty()) {
            throw new IllegalArgumentException("文件内容不能为空。");
        }

        // 检查总行数是否是3的倍数
        if (linesBuffer.size() % 3 != 0) {
            throw new IllegalArgumentException("文件格式错误：总行数不是3的倍数，存在不完整的问答对。");
        }

        qaPairCount = linesBuffer.size() / 3;

        // --- 步骤 5: HDFS 存储 ---
        // 定义一个新的存储目录来存放标注文件
        String hdfsPath = "/qa_annotations/" + currentUser.getAccount() + "/" + originalFileId + "/" + uploadedFileName;
        
        HdfsApi hdfsApi = new HdfsApi(hadoopConf, hdfsUser);
// ... more code ...
        try {
            hdfsApi.upLoadFile(file.getInputStream(), hdfsPath);
        } finally {
            hdfsApi.close(); // 确保HDFS连接被关闭
        }

        // --- 步骤 6: 数据库记录 ---
        QaAnnotationFile annotationRecord = new QaAnnotationFile();
// ... more code ...
        annotationRecord.setOriginalFileId(originalFileId);
        annotationRecord.setCreatorId(currentUser.getUserId());
        annotationRecord.setAnnotationFilePath(hdfsPath);
        annotationRecord.setQaPairCount(qaPairCount);
        annotationRecord.setStatus("VALIDATED"); // 状态标记为已验证
        annotationRecord.setCreatedAt(LocalDateTime.now());
        
        // 直接调用 mapper 的 insert 方法
        qaAnnotationFileMapper.insert(annotationRecord);

        // --- 步骤 7: 更新原始文件的标注状态 ---
        originalFile.setAnnotationStatus("COMPLETED");
        fileService.updateById(originalFile);

        // --- 步骤 8: 更新语料的标注状态与标注人 ---
        if (originalFile.getCorpusId() != null) {
            Corpus corpusToUpdate = new Corpus();
            corpusToUpdate.setCorpusId(originalFile.getCorpusId());
            corpusToUpdate.setAnnotationStatus("COMPLETED");
            corpusToUpdate.setAnnotationUploaderId(currentUser.getUserId());
            corpusService.updateById(corpusToUpdate);
        }

        return annotationRecord;
    }

    // --- 手动实现 IService 接口中的方法 ---
    @Override
    public boolean save(QaAnnotationFile entity) {
        return qaAnnotationFileMapper.insert(entity) > 0;
    }

    @Override
    public boolean saveBatch(Collection<QaAnnotationFile> entityList) {
        // This is a simplified implementation. For actual batch saving, a loop or a mapper-level batch insert would be needed.
        for (QaAnnotationFile entity : entityList) {
            if (qaAnnotationFileMapper.insert(entity) <= 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean saveBatch(Collection<QaAnnotationFile> entityList, int batchSize) {
        // Not implemented for this use case
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<QaAnnotationFile> entityList, int batchSize) {
        // Not implemented for this use case
        return false;
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        return qaAnnotationFileMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return qaAnnotationFileMapper.deleteByMap(columnMap) > 0;
    }

    @Override
    public boolean remove(Wrapper<QaAnnotationFile> queryWrapper) {
        return qaAnnotationFileMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        return qaAnnotationFileMapper.deleteBatchIds(list) > 0;
    }

    @Override
    public boolean updateById(QaAnnotationFile entity) {
        return qaAnnotationFileMapper.updateById(entity) > 0;
    }

    @Override
    public boolean update(QaAnnotationFile entity, Wrapper<QaAnnotationFile> updateWrapper) {
        return qaAnnotationFileMapper.update(entity, updateWrapper) > 0;
    }

    @Override
    public boolean updateBatchById(Collection<QaAnnotationFile> entityList, int batchSize) {
        // Not implemented for this use case
        return false;
    }

    @Override
    public boolean saveOrUpdate(QaAnnotationFile entity) {
        // Not implemented for this use case
        return false;
    }

    @Override
    public QaAnnotationFile getById(java.io.Serializable id) {
        return qaAnnotationFileMapper.selectById(id);
    }

    @Override
    public List<QaAnnotationFile> listByIds(Collection<? extends java.io.Serializable> idList) {
        return qaAnnotationFileMapper.selectBatchIds(idList);
    }

    @Override
    public List<QaAnnotationFile> listByMap(Map<String, Object> columnMap) {
        return qaAnnotationFileMapper.selectByMap(columnMap);
    }
    
    @Override
    public QaAnnotationFile getOne(Wrapper<QaAnnotationFile> queryWrapper, boolean throwEx) {
        return qaAnnotationFileMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<String, Object> getMap(Wrapper<QaAnnotationFile> queryWrapper) {
        List<Map<String, Object>> list = getBaseMapper().selectMaps(queryWrapper);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<QaAnnotationFile> queryWrapper, Function<? super Object, V> mapper) {
        List<Object> list = getBaseMapper().selectObjs(queryWrapper);
        if (list != null && !list.isEmpty()) {
            return mapper.apply(list.get(0));
        }
        return null;
    }

    @Override
    public BaseMapper<QaAnnotationFile> getBaseMapper() {
        return this.qaAnnotationFileMapper;
    }

    @Override
    public Class<QaAnnotationFile> getEntityClass() {
        return QaAnnotationFile.class;
    }

    @Override
    public Optional<QaAnnotationFile> getOneOpt(Wrapper<QaAnnotationFile> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getOne(queryWrapper, throwEx));
    }
}