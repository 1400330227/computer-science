# 语料上传错误提示改进方案

## 🎯 问题描述

用户反馈：上传语料名称重复时，前端只显示模糊的"上传失败，请稍后重试"，没有明确指出是语料名称重复导致的失败。

## 🔍 问题分析

### 原始问题
1. **错误消息不明确**: 前端显示通用错误消息，不能准确反映具体失败原因
2. **缺乏实时反馈**: 用户只有在提交时才知道名称重复
3. **用户体验差**: 需要用户猜测失败原因

## ✅ 改进方案

### 1. 改进后端错误消息

#### 更明确的错误提示
```java
// 修改前
return ResponseEntity.badRequest().body("创建语料库失败，名称可能已存在");

// 修改后
return ResponseEntity.badRequest().body("语料名称 \"" + corpus.getCollectionName() + "\" 已存在，请使用其他名称");
```

#### 增加调试日志
```java
System.out.println("=== 语料创建失败 ===");
System.out.println("失败原因: 语料名称已存在");
System.out.println("语料名称: " + corpus.getCollectionName());
System.out.println("用户ID: " + corpus.getCreatorId());
System.out.println("==================");
```

### 2. 改进前端错误处理

#### 增强错误消息显示
```javascript
// 修改前
ElMessage.error(error.response?.data || error.message || '上传失败，请稍后重试')

// 修改后
let errorMessage = '上传失败，请稍后重试'

if (error.response?.data) {
  errorMessage = error.response.data
} else if (error.message) {
  errorMessage = error.message
}

ElMessage({
  message: errorMessage,
  type: 'error',
  duration: 5000, // 延长显示时间
  showClose: true // 允许手动关闭
})
```

### 3. 添加实时名称检查

#### 失去焦点时检查重复
```vue
<el-input 
  v-model="formData.collectionName" 
  placeholder="请填写语料集名称"
  @blur="checkCollectionNameDuplicate"
></el-input>
<div v-if="collectionNameStatus.show" class="name-check-status">
  <span :class="collectionNameStatus.type">
    {{ collectionNameStatus.message }}
  </span>
</div>
```

#### 实时检查逻辑
```javascript
const checkCollectionNameDuplicate = async () => {
  const collectionName = formData.collectionName?.trim()
  
  if (!collectionName) {
    collectionNameStatus.show = false
    return
  }
  
  try {
    const response = await api.get(`/corpus/my-corpus`, {
      params: {
        page: 1,
        size: 1,
        collectionName: collectionName
      }
    })
    
    if (response.data && response.data.records && response.data.records.length > 0) {
      // 找到重复的语料
      collectionNameStatus.show = true
      collectionNameStatus.type = 'error'
      collectionNameStatus.message = '⚠️ 该语料集名称已存在，请使用其他名称'
    } else {
      // 名称可用
      collectionNameStatus.show = true
      collectionNameStatus.type = 'success'
      collectionNameStatus.message = '✅ 语料集名称可用'
    }
  } catch (error) {
    console.error('检查语料名称失败:', error)
    collectionNameStatus.show = false
  }
}
```

## 🎨 用户体验改进

### 改进前的用户体验
```
用户填写表单 → 点击保存 → 显示"上传失败，请稍后重试" → 用户困惑，不知道为什么失败
```

### 改进后的用户体验
```
用户填写语料名称 → 失去焦点 → 实时检查 → 显示"⚠️ 该语料集名称已存在，请使用其他名称"
用户修改名称 → 失去焦点 → 实时检查 → 显示"✅ 语料集名称可用"
用户点击保存 → 如果仍然重复 → 显示明确错误："语料名称 'xxx' 已存在，请使用其他名称"
```

## 🔍 错误提示层级

### 1. 预防性提示（实时检查）
- **时机**: 用户输入语料名称后失去焦点
- **样式**: 输入框下方的彩色提示文字
- **内容**: "⚠️ 该语料集名称已存在" 或 "✅ 语料集名称可用"

### 2. 阻止性提示（提交时）
- **时机**: 用户点击保存按钮，后端返回错误
- **样式**: 页面顶部的错误消息框
- **内容**: "语料名称 'xxx' 已存在，请使用其他名称"
- **特点**: 5秒显示时间，可手动关闭

## 📱 视觉效果

### 实时检查状态
```css
/* 成功状态 */
.name-check-status .success {
  color: #67c23a; /* 绿色 */
}

/* 错误状态 */
.name-check-status .error {
  color: #f56c6c; /* 红色 */
}
```

### 错误消息框
- **持续时间**: 5秒（比默认3秒更长）
- **可关闭**: 支持手动关闭按钮
- **类型**: 错误类型（红色背景）

## 🚀 改进效果

### 1. 用户体验提升
- ✅ 实时反馈，提前预警
- ✅ 明确的错误信息，不再困惑
- ✅ 更长的错误显示时间，确保用户看到

### 2. 开发调试优化
- ✅ 详细的后端日志，便于问题排查
- ✅ 结构化的错误处理逻辑
- ✅ 前端错误处理更加健壮

### 3. 减少用户操作成本
- ✅ 减少重复提交的次数
- ✅ 提前发现问题，避免填写完整表单后才发现错误
- ✅ 明确的指导信息，知道如何修复问题

---

**改进状态**: ✅ 已完成  
**测试建议**: 
1. 输入已存在的语料名称，查看实时提示
2. 提交重复名称，查看错误消息
3. 修改为新名称，查看成功提示 