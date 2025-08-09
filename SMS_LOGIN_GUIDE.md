# 短信验证码登录功能使用指南

## 概述

本项目已将登录方式从"账号+密码"改为"手机号+验证码"的方式，提供更安全便捷的登录体验。

## 新增API接口

### 1. 发送验证码接口

**接口地址：** `POST /api/users/send-verification-code`

**请求参数：**
```json
{
    "phone": "13800138000"
}
```

**响应示例：**
```json
{
    "success": true,
    "message": "验证码已发送"
}
```

**错误响应：**
```json
{
    "success": false,
    "message": "该手机号尚未注册，请先注册"
}
```

### 2. 验证码登录接口

**接口地址：** `POST /api/users/login-with-sms`

**请求参数：**
```json
{
    "phone": "13800138000",
    "code": "123456"
}
```

**响应示例：**
```json
{
    "success": true,
    "message": "登录成功",
    "userId": 1,
    "account": "testuser",
    "userType": "user",
    "phone": "13800138000",
    "nickname": "测试用户"
}
```

## 功能特性

### 1. 安全措施
- **验证码有效期：** 5分钟
- **发送频率限制：** 每个手机号每分钟最多发送一次
- **验证尝试限制：** 每个验证码最多允许验证3次
- **账号状态检查：** 只有状态为'active'或'正常'的账号才能登录

### 2. 自动清理
- 系统每5分钟自动清理过期的验证码
- 自动清理过期的发送限制记录

### 3. 兼容性
- 原有的账号+密码登录接口仍保留，但标记为已废弃
- 新的短信登录与现有的Session管理、拦截器完全兼容
- 支持单点登录控制（同一用户多地登录会踢出旧会话）

## 数据库要求

确保用户表(`users`)中的`phone`字段已正确填写手机号，且手机号格式为11位数字（以1开头）。

## 开发环境测试

在开发环境中，验证码会直接在控制台日志中显示，方便测试：

```
=== 模拟短信发送 ===
手机号: 13800138000
验证码: 123456
==================
```

## 前端集成示例

### 1. 发送验证码
```javascript
// 发送验证码
async function sendVerificationCode(phone) {
    try {
        const response = await fetch('/api/users/send-verification-code', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ phone: phone })
        });
        
        const result = await response.json();
        if (result.success) {
            alert('验证码已发送，请查收');
        } else {
            alert(result.message);
        }
    } catch (error) {
        alert('发送失败，请重试');
    }
}
```

### 2. 验证码登录
```javascript
// 验证码登录
async function loginWithSms(phone, code) {
    try {
        const response = await fetch('/api/users/login-with-sms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                phone: phone, 
                code: code 
            })
        });
        
        const result = await response.json();
        if (result.success) {
            alert('登录成功');
            // 保存用户信息，跳转到主页面
            localStorage.setItem('userInfo', JSON.stringify(result));
            window.location.href = '/dashboard';
        } else {
            alert(result.message);
        }
    } catch (error) {
        alert('登录失败，请重试');
    }
}
```

## 注意事项

1. **生产环境配置：** 需要配置真实的短信服务商API，替换`SmsVerificationService.simulateSendSms()`方法
2. **Redis集成：** 建议在生产环境中使用Redis存储验证码，替换内存存储方式
3. **监控告警：** 建议添加短信发送失败的监控和告警机制
4. **防刷机制：** 可以考虑添加IP级别的防刷限制

## 故障排查

### 常见问题

1. **"该手机号尚未注册"**
   - 检查数据库中用户的phone字段是否正确填写

2. **"发送过于频繁"**
   - 每个手机号每分钟只能发送一次验证码，请等待60秒后重试

3. **"验证码已过期"**
   - 验证码有效期为5分钟，请重新获取验证码

4. **"验证失败次数过多"**
   - 每个验证码最多验证3次，请重新获取验证码

## 升级路径

如果需要从旧的密码登录方式迁移：

1. **前端更新：** 将登录表单从账号+密码改为手机号+验证码
2. **测试验证：** 使用新接口进行完整的登录流程测试
3. **逐步下线：** 可以先并行运行两种方式，确认无误后再完全切换

---

*更新时间：[当前日期]*
*版本：v1.0* 