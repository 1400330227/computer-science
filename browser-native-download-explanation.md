# 浏览器原生下载管理器工作原理

## 你看到的下载弹框是什么？

截图中浏览器右上角的下载弹框（显示下载进度和速度）是**浏览器的原生下载管理器**，这是浏览器内置的功能，不是网站代码实现的。

## 触发原生下载管理器的条件

### 1. 后端响应头设置
```java
// 在 CorpusController.java 中
response.setContentType("application/zip");
response.setHeader("Content-Disposition", 
    "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));
```

关键响应头：
- `Content-Type`: 指定文件类型
- `Content-Disposition: attachment`: 告诉浏览器这是要下载的文件，不是要显示的内容
- `filename=`: 指定下载文件名

### 2. 前端实现方式

#### 方式A：直接链接下载（触发原生下载管理器）
```html
<a href="/api/corpus/download/123" download>下载</a>
```

**特点：**
- ✅ 显示浏览器原生下载进度和速度
- ✅ 可以暂停/恢复下载
- ✅ 下载历史记录
- ✅ 支持大文件下载
- ✅ 用户可以右键"另存为"
- ❌ 无法自定义下载进度UI
- ❌ 无法在JavaScript中监控下载进度

#### 方式B：Blob下载（不触发原生下载管理器）
```javascript
// 通过JavaScript下载
fetch('/api/corpus/download/123')
  .then(response => response.blob())
  .then(blob => {
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'filename.zip'
    a.click()
  })
```

**特点：**
- ❌ 不显示浏览器下载管理器
- ✅ 可以自定义下载进度UI
- ✅ 可以在JavaScript中监控进度
- ❌ 大文件下载可能有内存问题
- ❌ 用户体验不如原生下载

## 当前项目的实现

我已经为你修改了代码，现在支持**浏览器原生下载管理器**：

### 语料列表页面 (CorpusFileList.vue)
```javascript
function handleDownloadClick(row, event) {
  // 使用浏览器原生下载管理器
  ElMessage.success(`正在下载语料: ${row.collectionName}`);
  // 不阻止默认行为，让 <a> 标签直接下载
}
```

### 文件详情页面 (corpusFileDetails.vue)
```javascript
function handleFileDownloadClick(file, event) {
  // 使用浏览器原生下载管理器
  ElMessage.success(`正在下载文件: ${file.fileName}`)
  // 不阻止默认行为，让 <a> 标签直接下载
}
```

## 如何切换下载方式

如果你想使用**原来的Blob下载方式**（有自定义进度但无原生下载管理器），可以修改代码：

1. 取消注释这些行：
```javascript
// event.preventDefault();
// downloadFile(file);
```

2. 注释掉原生下载的代码

## 测试原生下载管理器

1. 启动项目
2. 点击任何下载链接
3. 观察浏览器右上角是否出现下载弹框
4. 弹框会显示：
   - 文件名
   - 下载进度条
   - 下载速度
   - 剩余时间
   - 暂停/恢复按钮

## 技术原理总结

浏览器原生下载管理器的触发需要：

1. **后端**：正确的HTTP响应头（Content-Disposition: attachment）
2. **前端**：直接的URL导航（`<a href="...">` 或 `window.location`）
3. **不能**：使用fetch/axios然后创建Blob（这会绕过原生下载）

这就是为什么GitHub、百度网盘等网站的下载都会显示浏览器原生下载管理器的原因！ 