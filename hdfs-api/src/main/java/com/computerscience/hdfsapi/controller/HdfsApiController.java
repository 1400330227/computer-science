package com.computerscience.hdfsapi.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletResponse;

import com.computerscience.hdfsapi.OP;
import com.computerscience.hdfsapi.api.HdfsApi;
import com.computerscience.hdfsapi.exception.HdfsApiException;
import com.computerscience.hdfsapi.model.HDFSOp;
import com.computerscience.hdfsapi.service.HdfsApiService;
import com.computerscience.hdfsapi.utils.DPage;
import com.computerscience.hdfsapi.utils.ListFilter;
import com.computerscience.hdfsapi.utils.UserContext;
import com.computerscience.hdfsapi.model.User;
import org.apache.hadoop.conf.Configuration;
import org.eclipse.jetty.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.computerscience.hdfsapi.model.HDFSFileStatus;


@RestController
@RequestMapping("/hdfs")
public class HdfsApiController {

    @Value("${hadoop.hdfs.user}")
    private String user;


    @Value("${hadoop.hdfs.root-path}")
    private String rootPath;

    @Autowired
    private Configuration conf;

    @Autowired
    private HdfsApiService apiService;

    @PostMapping
    public ResponseEntity<String> oPHdfs(@RequestBody HDFSOp hdfsOp, HttpServletResponse response) throws Exception {
        /**
         * 自定义扩展
         */
        boolean result = false;
        HdfsApi api = new HdfsApi(conf, user);
        OP op = hdfsOp.getOp();
        if (op == null) {
            throw new HdfsApiException("无法接收文件操作标识为空的请求");
        }

        if (op.equals(OP.CREATE)) {
            result = apiService.create(api, hdfsOp);
        } else if (op.equals(OP.DELETE)) {
            result = apiService.delete(api, hdfsOp);
        } else if (op.equals(OP.COPY)) {
            result = apiService.copy(api, hdfsOp);
        } else if (op.equals(OP.EMPTYTRASH)) {
            result = apiService.emptyTrash(api);
        } else if (op.equals(OP.MOVE)) {
            result = apiService.move(api, hdfsOp);
        } else if (op.equals(OP.RENAME)) {
            result = apiService.rename(api, hdfsOp);
        } else if (op.equals(OP.WRITE)) {
            result = apiService.write(api, hdfsOp);
        } else if (op.equals(OP.APPEND)) {
            result = apiService.append(api, hdfsOp);
        } else if (op.equals(OP.OPEN)) {
            apiService.open(api, hdfsOp, response);
        }
        api.close();
        if (result) {
            return ResponseEntity.ok("成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器异常");
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upLoadFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "destPath") String destPath)
            throws Exception {
        User currentUser = UserContext.getCurrentUser();
        destPath = rootPath + currentUser.getAccount();

        HdfsApi api = new HdfsApi(conf, user);
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        api.upLoadFile(is, destPath + "/" + name);
        api.close();
        return ResponseEntity.ok("文件上传成功");
    }

    @GetMapping("/file")
    public void downLoadFile(@RequestParam(name = "filePath") String filePath,
                             HttpServletResponse response, @RequestParam(name = "flag") boolean flag) throws Exception {
        if (!UserContext.isUserLoggedIn()) {
            response.setStatus(401);
            response.getWriter().write("用户未登录");
            return;
        }
        HdfsApi api = new HdfsApi(conf, user);
        api.downLoadFile(filePath, response, flag);
    }

    /**
     * 获取指定路径下的文件列表
     *
     * @param path HDFS路径，默认为根目录
     * @return 文件列表
     * @throws Exception
     */
    @GetMapping("/files")
    public ResponseEntity<?> getFileList(@RequestParam(name = "path", defaultValue = "/") String path)
            throws Exception {
        HdfsApi api = new HdfsApi(conf, user);
        try {
            if (!api.exists(path)) {
                return ResponseEntity.badRequest().body("路径不存在: " + path);
            }
            List<HDFSFileStatus> fileList = api.getFileList(path, null);
            return ResponseEntity.ok(fileList);
        } finally {
            api.close();
        }
    }


    @GetMapping
    public ResponseEntity<?> getFileStatus(ListFilter listFilter) throws Exception {
        HdfsApi api = new HdfsApi(conf, user);
        OP op = OP.getEnum(listFilter.getOp());
        if (op == null) {
            throw new HdfsApiException("无法接收文件操作标识为空的请求");
        }

        DPage<HDFSFileStatus> dPage = null;
        try {
            if (op.equals(OP.FILElIST)) {
                dPage = apiService.getFileListStatus(api, listFilter);
            } else if (op.equals(OP.HOMELIST)) {
                dPage = apiService.getHomeListStatus(api, listFilter);
            } else if (op.equals(OP.TRASHLIST)) {
                dPage = apiService.getTrashListStatus(api, listFilter);
            }
            return ResponseEntity.ok(dPage);
        } finally {
            api.close();
        }
    }

    @GetMapping("/example")
    public ResponseEntity<?> exampleWithUserInfo() {
        // 获取当前登录用户信息
        User currentUser = UserContext.getCurrentUser();

        // 判断用户是否已登录
        if (currentUser == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }

        // 使用用户信息进行业务处理
        Map<String, Object> response = new HashMap<>();
        response.put("message", "操作成功");
        response.put("userName", currentUser.getAccount());
        response.put("userType", currentUser.getUserType());

        return ResponseEntity.ok(response);
    }
}
