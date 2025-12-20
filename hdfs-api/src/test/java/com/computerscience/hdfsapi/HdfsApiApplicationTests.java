package com.computerscience.hdfsapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.computerscience.hdfsapi.api.HdfsApi;
import com.computerscience.hdfsapi.model.FileEntity;
import com.computerscience.hdfsapi.model.User;
import com.computerscience.hdfsapi.service.FileService;
import com.computerscience.hdfsapi.service.UserService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HdfsApiApplicationTests {
    @Autowired
    @Qualifier("conf")
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String user;

    @Value("${hadoop.hdfs.root-path}")
    private String rootPath;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Test
    void contextLoads() {
    }

//    @Test
    public void migratePaths() {
        try {
            HdfsApi hdfsApi = new HdfsApi(conf, user);
            List<User> userList = userService.list();

            int totalFiles = 0;
            int successFiles = 0;
            int failedFiles = 0;
            int existFiles = 0;

            System.out.println("开始迁移路径，共有用户: " + userList.size() + " 个");

            for (User user : userList) {
                System.out.println("处理用户: " + user.getAccount() + " (ID: " + user.getUserId() + ")");

                QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("creator_id", user.getUserId());
                List<FileEntity> fileEntities = fileService.list(queryWrapper);

                System.out.println("  用户 " + user.getAccount() + " 有 " + fileEntities.size() + " 个文件");

                for (FileEntity fileEntity : fileEntities) {
                    totalFiles++;
                    String fileName = fileEntity.getFileName();
                    Integer corpusId = fileEntity.getCorpusId();

                    // 构建路径，确保格式正确
                    String oldPath = rootPath + user.getAccount() + corpusId + "/" + fileName;
                    String newPath = rootPath + user.getAccount() + "/" + corpusId + "/" + fileName;
                    String newDir = rootPath + user.getAccount() + "/" + corpusId;

                    try {
                        // 1. 检查原文件是否存在
                        if (!hdfsApi.existFile(oldPath)) {
                            System.out.println("    跳过 - 原文件不存在: " + oldPath);
                            failedFiles++;
                            continue;
                        }

                        // 2. 检查目标文件是否已存在
                        if (hdfsApi.existFile(newPath)) {
                            existFiles++;
                            System.out.println("    跳过 - 目标文件已存在: " + newPath);
                            // 可以选择覆盖
                            // hdfsApi.rmdir(newPath, false, true);
                            continue;
                        }

                        // 3. 创建目标目录
                        if (!hdfsApi.existDir(newDir, false)) {
                            System.out.println("    创建目录: " + newDir);
                            hdfsApi.mkdir(newDir);
                        }

                        // 4. 执行复制
                        System.out.println("    复制: " + oldPath + " -> " + newPath);
                        hdfsApi.copy(oldPath, newPath);

                        // 5. 验证复制结果（可选）
                        if (hdfsApi.existFile(newPath)) {
                            successFiles++;
                            System.out.println("    ✓ 复制成功");

                            // 6. 删除原文件（如果需要）
                            // hdfsApi.rmdir(oldPath, false, true);
                            // System.out.println("    ✓ 删除原文件");

                        } else {
                            failedFiles++;
                            System.err.println("    ✗ 复制失败，目标文件不存在");
                        }

                    } catch (Exception e) {
                        failedFiles++;
                        System.err.println("    ✗ 处理文件失败: " + fileName);
                        System.err.println("      错误: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("\n=== 迁移完成 ===");
            System.out.println("总文件数: " + totalFiles);
            System.out.println("存在文件数: " + existFiles);
            System.out.println("成功: " + successFiles);
            System.out.println("失败: " + failedFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
