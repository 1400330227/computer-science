const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const path = require('path');
require('dotenv').config();

// 导入数据库配置
const { testConnection, closePool } = require('./config/database');

const app = express();
const PORT = process.env.PORT || 8080;

// ===== 安全中间件 =====
app.use(helmet());

// ===== CORS配置 =====
app.use(cors({
  origin: ['http://localhost:5173', 'http://localhost:3000', 'http://localhost:8080'],
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// ===== 限流中间件 =====
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15分钟
  max: 100, // 限制每个IP 15分钟内最多100个请求
  message: {
    error: '请求过于频繁，请稍后再试',
    retryAfter: '15分钟'
  }
});
app.use(limiter);

// 注册请求限流（更严格）
const registerLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1小时
  max: 5, // 限制每个IP 1小时内最多5次注册请求
  message: {
    error: '注册请求过于频繁，请1小时后再试',
    retryAfter: '1小时'
  }
});

// ===== JSON解析中间件 =====
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// ===== 请求日志中间件 =====
app.use((req, res, next) => {
  const timestamp = new Date().toISOString();
  console.log(`[${timestamp}] ${req.method} ${req.path} - IP: ${req.ip}`);
  next();
});

// ===== 导入路由 =====
const userRoutes = require('./routes/users');

// ===== 应用路由 =====
app.use('/api/users', registerLimiter, userRoutes);

// ===== 根路径 =====
app.get('/', (req, res) => {
  res.json({
    message: 'HDFS文件管理系统 API 服务器',
    version: '1.0.0',
    status: 'running',
    timestamp: new Date().toISOString()
  });
});

// ===== 健康检查端点 =====
app.get('/api/health', (req, res) => {
  res.json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime()
  });
});

// ===== 404错误处理 =====
app.use('*', (req, res) => {
  res.status(404).json({
    error: '接口不存在',
    message: `路径 ${req.originalUrl} 未找到`,
    timestamp: new Date().toISOString()
  });
});

// ===== 全局错误处理中间件 =====
app.use((err, req, res, next) => {
  console.error('服务器错误:', err);
  
  // 默认错误响应
  let status = err.status || 500;
  let message = err.message || '服务器内部错误';
  
  // 数据库错误处理
  if (err.code === 'ER_DUP_ENTRY') {
    status = 400;
    if (err.sqlMessage.includes('username')) {
      message = '用户名已存在';
    } else if (err.sqlMessage.includes('account')) {
      message = '账号已存在';
    } else if (err.sqlMessage.includes('phone')) {
      message = '手机号已被使用';
    } else {
      message = '数据重复，请检查输入信息';
    }
  } else if (err.code === 'ER_NO_SUCH_TABLE') {
    status = 500;
    message = '数据库表不存在，请联系管理员';
  } else if (err.code && err.code.startsWith('ER_')) {
    status = 500;
    message = '数据库操作失败';
  }
  
  // JWT错误处理
  if (err.name === 'JsonWebTokenError') {
    status = 401;
    message = '无效的访问令牌';
  } else if (err.name === 'TokenExpiredError') {
    status = 401;
    message = '访问令牌已过期';
  }
  
  res.status(status).json({
    error: message,
    timestamp: new Date().toISOString(),
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
});

// ===== 启动服务器 =====
const startServer = async () => {
  try {
    // 初始化数据库连接
    await testConnection();
    
    // 启动服务器
    app.listen(PORT, () => {
      console.log('==========================================');
      console.log(`🚀 HDFS文件管理系统 API 服务器已启动`);
      console.log(`📡 服务器地址: http://localhost:${PORT}`);
      console.log(`🌍 环境: ${process.env.NODE_ENV || 'development'}`);
      console.log(`⏰ 启动时间: ${new Date().toLocaleString()}`);
      console.log('==========================================');
    });
    
  } catch (error) {
    console.error('❌ 服务器启动失败:', error.message);
    process.exit(1);
  }
};

startServer();

// ===== 优雅关闭 =====
const gracefulShutdown = async (signal) => {
  console.log(`🛑 收到${signal}信号，正在优雅关闭服务器...`);
  try {
    await closePool();
    console.log('✅ 服务器已安全关闭');
    process.exit(0);
  } catch (error) {
    console.error('❌ 关闭服务器时发生错误:', error);
    process.exit(1);
  }
};

process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
process.on('SIGINT', () => gracefulShutdown('SIGINT'));

module.exports = app; 