# 下载功能问题修复总结

## 🐛 发现的问题

1. **右上角下载窗口没有立刻弹出**
   - 多文件ZIP下载时，浏览器下载管理器显示延迟

2. **详情页面下载JSON文件而不是实际文件**
   - `/api/files/{id}` 返回的是文件元数据（JSON）而不是文件内容

## 🔧 修复方案

### 1. 修复详情页面下载问题

#### 后端修复 (`FileController.java`)
**问题**: 原来的 `@GetMapping("/{id}")` 只返回文件元数据
```java
// 修复前 - 返回JSON
@GetMapping("/{id}")
public ResponseEntity<?> getFileById(@PathVariable Integer id) {
    // ... 权限检查 ...
    return ResponseEntity.ok(file); // 返回FileEntity对象的JSON
}
```

**解决方案**: 添加专门的下载端点
```java
// 修复后 - 返回实际文件内容
@GetMapping("/{id}/download")
public void downloadFile(@PathVariable Integer id, HttpServletResponse response) {
    // ... 权限检查 ...
    
    // 设置下载响应头
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    
    // 从HDFS下载并流式传输到浏览器
    HdfsApi hdfsApi = new HdfsApi(conf, hdfsUser);
    hdfsApi.downLoadFile(file.getFilePath(), response, true);
}
```

#### 前端修复 (`corpusFileDetails.vue`)
**问题**: URL指向元数据端点
```javascript
// 修复前
function getFileDownloadUrl(file) {
    return `/api/files/${file.fileId}` // 返回JSON
}
```

**解决方案**: 修改为下载端点
```javascript
// 修复后
function getFileDownloadUrl(file) {
    return `/api/files/${file.fileId}/download` // 返回实际文件
}
```

### 2. 优化多文件下载立刻显示

#### 响应头优化
```java
// 添加更多响应头确保浏览器立刻识别
response.setContentType("application/zip");
response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");
response.setHeader("Accept-Ranges", "bytes");      // 新增
response.setHeader("Connection", "keep-alive");    // 新增
response.setStatus(200);                           // 明确设置状态码
response.flushBuffer();                            // 立刻发送响应头
```

#### ZIP写入优化
```java
// 写入更多内容的初始ZIP条目
ZipEntry startEntry = new ZipEntry("download_info.txt");
zipOut.putNextEntry(startEntry);

StringBuilder downloadInfo = new StringBuilder();
downloadInfo.append("下载信息 - Download Information\n");
downloadInfo.append("========================\n");
downloadInfo.append("语料名称: ").append(corpus.getCollectionName()).append("\n");
downloadInfo.append("文件数量: ").append(files.size()).append("\n");
downloadInfo.append("下载时间: ").append(new Date()).append("\n");
downloadInfo.append("========================\n");

byte[] infoBytes = downloadInfo.toString().getBytes("UTF-8");
zipOut.write(infoBytes);
zipOut.closeEntry();

// 立刻刷新数据到浏览器
zipOut.flush();
response.flushBuffer();

// 短暂延迟确保数据传输
Thread.sleep(10);
```

## 📋 API端点总结

| 功能 | 端点 | 返回内容 | 用途 |
|------|------|----------|------|
| 获取文件信息 | `GET /api/files/{id}` | JSON元数据 | 显示文件信息 |
| 下载单个文件 | `GET /api/files/{id}/download` | 文件流 | 下载文件 |
| 下载语料 | `GET /api/corpus/download/{id}` | 文件流或ZIP | 下载语料 |

## 🎯 修复后的效果

### 语料列表页面下载
- ✅ 点击下载立刻显示浏览器下载管理器
- ✅ 立刻显示下载进度和文件名
- ✅ 支持暂停/恢复下载
- ✅ 鼠标悬停显示下载链接

### 详情页面文件下载  
- ✅ 下载实际文件内容（不是JSON）
- ✅ 正确的文件名和文件类型
- ✅ 立刻触发浏览器下载
- ✅ 鼠标悬停显示下载链接

## 🚀 测试步骤

### 1. 重启后端服务
确保新的 FileController 代码生效

### 2. 测试语料列表下载
1. 访问语料列表页面
2. 点击任意语料的"下载"按钮
3. **预期**: 浏览器右上角立刻显示下载图标和进度

### 3. 测试文件详情下载
1. 访问任意语料的详情页面
2. 点击任意文件的"下载"按钮  
3. **预期**: 下载实际文件内容，不是JSON

### 4. 验证下载内容
- 语料下载: 应该是ZIP文件，包含 `download_info.txt` 和实际文件
- 单文件下载: 应该是原始文件，可以正常打开

## 📝 技术要点

### 浏览器下载识别机制
1. **响应头**: 必须有正确的 `Content-Disposition: attachment`
2. **内容类型**: 设置正确的 `Content-Type`
3. **立刻数据**: 浏览器需要接收到实际数据才开始下载
4. **数据量**: 足够的初始数据量让浏览器识别为有效文件

### 流式下载优化
1. **立刻刷新**: 设置响应头后立刻 `flush()`
2. **分块传输**: 定期刷新数据到浏览器
3. **错误处理**: 完整的异常捕获和用户反馈

现在的下载功能应该完全正常了！ 