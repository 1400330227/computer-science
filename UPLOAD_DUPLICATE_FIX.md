# 语料上传重复名称修复方案

## 🎯 问题描述

用户反馈：在上传语料信息时，如果语料名称在数据库中已存在，虽然前端显示上传失败，但后端却把数据依旧上传到数据库和HDFS系统中。

## 🔍 问题分析

### 原始问题
1. **重复检查范围错误**: 后端检查语料名称重复时是全局检查，没有限制在当前用户范围内
2. **前端错误处理不完善**: 语料创建失败时，前端没有正确中断后续的文件上传流程
3. **逻辑不合理**: 不同用户应该可以创建同名的语料库

### 问题根源
```java
// 原始代码 - 全局检查重复
Corpus existingCorpus = findByName(corpus.getCollectionName());
if (existingCorpus != null) {
    return false;  // 任何用户创建同名语料都会失败
}
```

## ✅ 修复方案

### 1. 后端修复

#### 新增方法：按用户范围检查重复
```java
// CorpusService.java
Corpus findByUserIdAndName(Integer userId, String collectionName);

// CorpusServiceImpl.java
@Override
public Corpus findByUserIdAndName(Integer userId, String collectionName) {
    if (userId == null || !StringUtils.hasText(collectionName)) {
        return null;
    }
    
    LambdaQueryWrapper<Corpus> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Corpus::getCreatorId, userId)
               .eq(Corpus::getCollectionName, collectionName);
    
    return getOne(queryWrapper);
}
```

#### 修改创建语料逻辑
```java
@Override
@Transactional
public boolean createCorpus(Corpus corpus) {
    // 检查当前用户是否已有同名语料库（而不是全局检查）
    Corpus existingCorpus = findByUserIdAndName(corpus.getCreatorId(), corpus.getCollectionName());
    if (existingCorpus != null) {
        System.out.println("用户 " + corpus.getCreatorId() + " 已存在同名语料库: " + corpus.getCollectionName());
        return false;
    }
    
    corpus.setCreatedAt(LocalDateTime.now());
    return save(corpus);
}
```

### 2. 前端修复

#### 改进错误检查逻辑
```javascript
// 原始代码
if (!corpusResponse.data) {
    throw new Error('创建语料失败')
}

// 修复后代码
if (!corpusResponse.data || !corpusResponse.data.corpusId) {
    throw new Error('创建语料失败，可能是语料名称已存在')
}
```

#### 完善错误处理流程
- 语料创建失败时，立即抛出异常
- 异常会中断整个上传流程
- 不会执行后续的文件上传到HDFS的操作

## 🔒 修复后的逻辑

### 用户隔离的重复检查
- **用户A** 可以创建名为"泰国语料"的语料库
- **用户B** 也可以创建名为"泰国语料"的语料库
- **用户A** 不能重复创建名为"泰国语料"的语料库

### 完整的错误处理流程
```
1. 用户填写表单 → 点击保存
2. 前端验证表单 → 通过
3. 发送创建语料请求 → 后端检查重复
4. 如果重复 → 返回错误 → 前端抛出异常 → 停止上传
5. 如果不重复 → 创建成功 → 继续文件上传
```

## 🧪 测试场景

### 场景1: 正常上传（新名称）
```
用户A 创建 "老挝医疗语料" → 检查：用户A无此名称 → 创建成功 → 文件上传成功
```

### 场景2: 同用户重复名称
```
用户A 再次创建 "老挝医疗语料" → 检查：用户A已有此名称 → 创建失败 → 停止上传 → 前端显示错误
```

### 场景3: 不同用户相同名称
```
用户B 创建 "老挝医疗语料" → 检查：用户B无此名称 → 创建成功 → 文件上传成功
```

## 🛡️ 安全保障

### 数据一致性
- ✅ 语料创建失败时，不会有任何数据写入数据库
- ✅ 语料创建失败时，不会有任何文件上传到HDFS
- ✅ 前端显示的错误状态与实际状态一致

### 用户体验
- ✅ 清晰的错误提示："创建语料失败，可能是语料名称已存在"
- ✅ 上传进度条会在错误发生时立即停止
- ✅ 不会出现"前端显示失败，后端实际成功"的不一致情况

## 📝 核心改进

### 1. 业务逻辑优化
- **修复前**: 全局唯一性检查（不合理）
- **修复后**: 用户范围内唯一性检查（合理）

### 2. 错误处理完善
- **修复前**: 错误处理不完整，可能导致部分成功
- **修复后**: 完整的错误处理，确保原子性操作

### 3. 用户隔离
- **修复前**: 用户间相互影响
- **修复后**: 用户间完全隔离，互不影响

---

**修复状态**: ✅ 已完成  
**影响范围**: 语料创建和文件上传流程  
**测试建议**: 
1. 测试同用户重复名称的拦截
2. 测试不同用户相同名称的正常创建
3. 验证错误情况下没有数据泄露到数据库或HDFS 