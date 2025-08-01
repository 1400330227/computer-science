const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { query, transaction } = require('../config/database');

const router = express.Router();

// JWT配置
const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key-change-in-production';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '24h';

// ===== 输入验证函数 =====
const validateRegisterInput = (data) => {
  const errors = [];
  
  // 用户名验证
  if (!data.username || data.username.trim().length < 2) {
    errors.push('用户名至少需要2个字符');
  }
  if (data.username && data.username.length > 20) {
    errors.push('用户名不能超过20个字符');
  }
  
  // 登录账号验证
  if (!data.account || data.account.trim().length < 3) {
    errors.push('登录账号至少需要3个字符');
  }
  if (data.account && data.account.length > 20) {
    errors.push('登录账号不能超过20个字符');
  }
  if (data.account && !/^[a-zA-Z0-9_]+$/.test(data.account)) {
    errors.push('登录账号只能包含字母、数字和下划线');
  }
  
  // 密码验证
  if (!data.password || data.password.length < 6) {
    errors.push('密码至少需要6个字符');
  }
  if (data.password && data.password.length > 20) {
    errors.push('密码不能超过20个字符');
  }
  if (data.password && !/(?=.*[a-zA-Z])(?=.*\d)/.test(data.password)) {
    errors.push('密码必须包含字母和数字');
  }
  
  // 手机号验证（可选）
  if (data.phone && !/^1[3-9]\d{9}$/.test(data.phone)) {
    errors.push('手机号格式不正确');
  }
  
  // 用户类型验证
  if (!data.userType || !['user', 'admin'].includes(data.userType)) {
    errors.push('用户类型必须是user或admin');
  }
  
  return errors;
};

const validateLoginInput = (data) => {
  const errors = [];
  
  if (!data.username || data.username.trim().length === 0) {
    errors.push('请输入用户名或账号');
  }
  
  if (!data.password || data.password.length === 0) {
    errors.push('请输入密码');
  }
  
  return errors;
};

// ===== 生成JWT Token =====
const generateToken = (user) => {
  const payload = {
    userId: user.id,
    username: user.username,
    account: user.account,
    userType: user.user_type
  };
  
  return jwt.sign(payload, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
};

// ===== 记录登录日志 =====
const logLoginAttempt = async (userId, req, status, failureReason = null) => {
  try {
    const clientIP = req.ip || req.connection.remoteAddress || req.socket.remoteAddress;
    const userAgent = req.get('User-Agent') || '';
    
    await query(
      'INSERT INTO login_logs (user_id, login_ip, user_agent, login_status, failure_reason) VALUES (?, ?, ?, ?, ?)',
      [userId, clientIP, userAgent, status, failureReason]
    );
  } catch (error) {
    console.error('记录登录日志失败:', error);
  }
};

// ===== 用户注册 API =====
router.post('/register', async (req, res, next) => {
  try {
    const { username, account, password, phone, userType } = req.body;
    
    console.log('收到注册请求:', { username, account, userType, phone: phone ? '已提供' : '未提供' });
    
    // 输入验证
    const validationErrors = validateRegisterInput(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({
        error: '输入验证失败',
        message: validationErrors.join('; '),
        details: validationErrors
      });
    }
    
    // 使用事务确保数据一致性
    const result = await transaction(async (connection) => {
      // 检查用户名是否已存在
      const [existingUsername] = await connection.execute(
        'SELECT id FROM users WHERE username = ?',
        [username.trim()]
      );
      
      if (existingUsername.length > 0) {
        throw new Error('用户名已存在');
      }
      
      // 检查登录账号是否已存在
      const [existingAccount] = await connection.execute(
        'SELECT id FROM users WHERE account = ?',
        [account.trim()]
      );
      
      if (existingAccount.length > 0) {
        throw new Error('账号已存在');
      }
      
      // 检查手机号是否已存在（如果提供了手机号）
      if (phone && phone.trim()) {
        const [existingPhone] = await connection.execute(
          'SELECT id FROM users WHERE phone = ?',
          [phone.trim()]
        );
        
        if (existingPhone.length > 0) {
          throw new Error('手机号已被使用');
        }
      }
      
      // 加密密码
      const saltRounds = parseInt(process.env.BCRYPT_ROUNDS) || 12;
      const hashedPassword = await bcrypt.hash(password, saltRounds);
      
      // 插入新用户
      const [insertResult] = await connection.execute(
        'INSERT INTO users (username, account, password, phone, user_type) VALUES (?, ?, ?, ?, ?)',
        [
          username.trim(),
          account.trim(),
          hashedPassword,
          phone ? phone.trim() : null,
          userType
        ]
      );
      
      // 获取新创建的用户信息
      const [newUser] = await connection.execute(
        'SELECT id, username, account, phone, user_type, status, created_at FROM users WHERE id = ?',
        [insertResult.insertId]
      );
      
      return newUser[0];
    });
    
    console.log('用户注册成功:', { id: result.id, username: result.username, account: result.account });
    
    // 返回成功响应（不包含敏感信息）
    res.status(201).json({
      message: '注册成功',
      user: {
        id: result.id,
        username: result.username,
        account: result.account,
        phone: result.phone,
        userType: result.user_type,
        status: result.status,
        createdAt: result.created_at
      }
    });
    
  } catch (error) {
    console.error('注册失败:', error);
    next(error);
  }
});

// ===== 用户登录 API =====
router.post('/login', async (req, res, next) => {
  try {
    const { username, password } = req.body;
    
    console.log('收到登录请求:', { username });
    
    // 输入验证
    const validationErrors = validateLoginInput(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({
        error: '输入验证失败',
        message: validationErrors.join('; ')
      });
    }
    
    // 查找用户（支持用户名或账号登录）
    const users = await query(
      'SELECT id, username, account, password, phone, user_type, status, last_login_at FROM users WHERE username = ? OR account = ?',
      [username.trim(), username.trim()]
    );
    
    if (users.length === 0) {
      await logLoginAttempt(null, req, 'failed', '用户不存在');
      return res.status(401).json({
        error: '登录失败',
        message: '用户名或密码错误'
      });
    }
    
    const user = users[0];
    
    // 检查账户状态
    if (user.status === 'banned') {
      await logLoginAttempt(user.id, req, 'failed', '账户已被禁用');
      return res.status(401).json({
        error: '登录失败',
        message: '账户已被禁用，请联系管理员'
      });
    }
    
    if (user.status === 'inactive') {
      await logLoginAttempt(user.id, req, 'failed', '账户未激活');
      return res.status(401).json({
        error: '登录失败',
        message: '账户未激活，请联系管理员'
      });
    }
    
    // 验证密码
    const isPasswordValid = await bcrypt.compare(password, user.password);
    
    if (!isPasswordValid) {
      await logLoginAttempt(user.id, req, 'failed', '密码错误');
      return res.status(401).json({
        error: '登录失败',
        message: '用户名或密码错误'
      });
    }
    
    // 登录成功，更新最后登录时间
    await query(
      'UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?',
      [user.id]
    );
    
    // 记录成功登录日志
    await logLoginAttempt(user.id, req, 'success');
    
    // 生成JWT Token
    const token = generateToken(user);
    
    console.log('用户登录成功:', { id: user.id, username: user.username, account: user.account });
    
    // 返回成功响应
    res.json({
      message: '登录成功',
      token: token,
      user: {
        userId: user.id,
        username: user.username,
        account: user.account,
        phone: user.phone,
        userType: user.user_type,
        status: user.status,
        lastLoginAt: user.last_login_at
      }
    });
    
  } catch (error) {
    console.error('登录失败:', error);
    next(error);
  }
});

// ===== 获取用户信息 API =====
router.get('/profile', authenticateToken, async (req, res, next) => {
  try {
    const userId = req.user.userId;
    
    const users = await query(
      'SELECT id, username, account, phone, user_type, status, created_at, last_login_at FROM users WHERE id = ?',
      [userId]
    );
    
    if (users.length === 0) {
      return res.status(404).json({
        error: '用户不存在'
      });
    }
    
    const user = users[0];
    
    res.json({
      user: {
        userId: user.id,
        username: user.username,
        account: user.account,
        phone: user.phone,
        userType: user.user_type,
        status: user.status,
        createdAt: user.created_at,
        lastLoginAt: user.last_login_at
      }
    });
    
  } catch (error) {
    console.error('获取用户信息失败:', error);
    next(error);
  }
});

// ===== JWT认证中间件 =====
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN
  
  if (!token) {
    return res.status(401).json({
      error: '访问令牌缺失',
      message: '请提供有效的访问令牌'
    });
  }
  
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) {
      console.error('JWT验证失败:', err);
      return res.status(403).json({
        error: '访问令牌无效',
        message: '令牌已过期或无效'
      });
    }
    
    req.user = user;
    next();
  });
}

// ===== 检查数据库连接状态 =====
router.get('/health', async (req, res) => {
  try {
    await query('SELECT 1');
    res.json({
      status: 'healthy',
      database: 'connected',
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('数据库连接检查失败:', error);
    res.status(500).json({
      status: 'unhealthy',
      database: 'disconnected',
      error: error.message,
      timestamp: new Date().toISOString()
    });
  }
});

module.exports = router; 