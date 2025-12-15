# 标注功能使用说明

## 功能概述

标注功能允许用户对语料库中的语料进行标注和注释，支持多种标注类型，如情感分析、实体识别、语法标注等。

## 数据库设置

### 1. 执行SQL脚本

在MySQL数据库中执行以下SQL脚本创建标注相关的表：

```bash
# 执行SQL脚本
mysql -u your_username -p your_database < hdfs-api/src/main/resources/db/annotation_tables.sql
```

或者直接在MySQL客户端中执行 `annotation_tables.sql` 文件。

### 2. 表结构说明

#### annotations 表（标注表）
- `annotation_id`: 标注ID（主键）
- `corpus_id`: 关联的语料ID（外键）
- `file_id`: 关联的文件ID（可选，外键）
- `title`: 标注标题
- `content`: 标注内容/注释
- `annotation_type`: 标注类型/标签
- `status`: 标注状态（待审核、已审核、已发布等）
- `text_segment`: 标注的文本片段
- `start_position`: 文本片段起始位置
- `end_position`: 文本片段结束位置
- `creator_id`: 标注创建者ID（外键）
- `created_at`: 创建时间
- `updated_at`: 更新时间
- `remarks`: 备注说明

#### annotation_labels 表（标注标签表）
- `label_id`: 标签ID（主键）
- `label_name`: 标签名称（唯一）
- `description`: 标签描述
- `color`: 标签颜色（用于前端显示）
- `category`: 标签分类（如：情感、实体、语法等）
- `creator_id`: 创建者ID（外键）
- `created_at`: 创建时间
- `is_system`: 是否系统默认标签

## API接口说明

### 标注相关接口

#### 1. 获取标注列表
```
GET /api/annotation
参数：
- page: 页码（默认1）
- size: 每页大小（默认10）
- corpusId: 语料ID（可选）
- fileId: 文件ID（可选）
- annotationType: 标注类型（可选）
- creatorId: 创建者ID（可选）
```

#### 2. 根据ID获取标注
```
GET /api/annotation/{id}
```

#### 3. 根据语料ID获取标注列表
```
GET /api/annotation/corpus/{corpusId}
```

#### 4. 根据文件ID获取标注列表
```
GET /api/annotation/file/{fileId}
```

#### 5. 创建标注
```
POST /api/annotation
Body:
{
  "corpusId": 1,
  "fileId": 1,  // 可选
  "title": "标注标题",
  "content": "标注内容",
  "annotationType": "情感分析",
  "status": "待审核",
  "textSegment": "文本片段",  // 可选
  "startPosition": 0,  // 可选
  "endPosition": 10,  // 可选
  "remarks": "备注"  // 可选
}
```

#### 6. 更新标注
```
PUT /api/annotation/{id}
Body: 同创建标注
```

#### 7. 删除标注
```
DELETE /api/annotation/{id}
```

#### 8. 统计语料的标注数量
```
GET /api/annotation/corpus/{corpusId}/count
```

### 标注标签相关接口

#### 1. 获取所有标签
```
GET /api/annotation/labels?category=情感  // category可选
```

#### 2. 获取系统默认标签
```
GET /api/annotation/labels/system
```

#### 3. 创建标签
```
POST /api/annotation/labels
Body:
{
  "labelName": "新标签",
  "description": "标签描述",
  "color": "#FF6B6B",
  "category": "情感"
}
```

#### 4. 更新标签
```
PUT /api/annotation/labels/{id}
Body: 同创建标签
```

#### 5. 删除标签
```
DELETE /api/annotation/labels/{id}
注意：系统标签不能删除
```

## 前端使用

### 1. 访问标注管理页面

登录系统后，在导航菜单中点击"标注管理"即可进入标注管理页面。

### 2. 创建标注

1. 点击"新建标注"按钮
2. 填写标注信息：
   - 语料ID（必填）
   - 文件ID（可选）
   - 标题（必填）
   - 标注类型（从下拉列表选择）
   - 状态（默认：待审核）
   - 内容（必填）
   - 文本片段（可选）
   - 起始位置和结束位置（可选）
   - 备注（可选）
3. 点击"创建"按钮

### 3. 查看标注

在标注列表中点击"查看"按钮可以查看标注的详细信息。

### 4. 编辑标注

在标注列表中点击"编辑"按钮可以修改标注信息。只有标注的创建者可以编辑。

### 5. 删除标注

在标注列表中点击"删除"按钮可以删除标注。只有标注的创建者可以删除。

### 6. 搜索标注

可以使用以下条件搜索标注：
- 语料ID
- 文件ID
- 标注类型

## 权限说明

- 所有登录用户都可以创建、查看、编辑和删除自己创建的标注
- 只有标注的创建者可以编辑和删除自己的标注
- 管理员可以查看所有标注

## 注意事项

1. 标注必须关联到一个语料（corpusId必填）
2. 文件ID是可选的，如果提供，标注将关联到特定文件
3. 系统默认标签不能删除
4. 删除语料时，关联的标注也会被删除（CASCADE）
5. 删除文件时，关联的标注不会被删除，但fileId会被设置为NULL

## 扩展建议

1. **批量标注**：可以添加批量创建标注的功能
2. **标注导出**：可以添加将标注导出为JSON、CSV等格式的功能
3. **标注审核**：可以添加标注审核工作流
4. **标注统计**：可以添加标注统计和可视化功能
5. **标注模板**：可以添加标注模板功能，方便快速创建相似标注

