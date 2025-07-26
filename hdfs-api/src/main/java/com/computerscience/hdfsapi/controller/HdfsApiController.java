package com.computerscience.hdfsapi.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.computerscience.hdfsapi.HdfsApi;
import com.computerscience.hdfsapi.HdfsApiException;
import com.computerscience.hdfsapi.OP;
import com.computerscience.hdfsapi.model.HDFSOp;
import com.computerscience.hdfsapi.service.HdfsApiService;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin
@RestController
@RequestMapping("/hdfs")
public class HdfsApiController {

    @Value("${hadoop.hdfs.user:hdfs}")
    private String user;

    @Value("${hadoop.hdfs.namenode:localhost:9000}")
    private String namenode;


    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upLoadFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "destPath") String destPath)
            throws Exception {
        System.out.println("你已经成功的调用到上传文件的后端服务了");
        // 直接创建Configuration
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + namenode);
        conf.set("hadoop.user.name", user);
        
        HdfsApi api = new HdfsApi(conf, user);
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        api.upLoadFile(is, destPath + "/" + name);
        api.close();
        return ResponseEntity.ok("文件上传成功: " + destPath);
    }

}
