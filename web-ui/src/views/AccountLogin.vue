<template>
  <!-- 登录页面的最外层容器 -->
  <div class="login-page">

    <!-- Element Plus 卡片组件，用来包装登录表单 -->
    <el-card class="login-card" shadow="always">

      <!-- 登录表单的标题 -->
      <template #header>
        <div class="card-header">
          <h1>广西大学东盟语料收集与管理平台</h1>
          <p>请输入您的账号信息登录</p>
        </div>
      </template>

      <!-- Element Plus 表单组件
           :model="loginForm" 绑定表单数据
           :rules="formRules" 绑定验证规则
           ref="loginFormRef" 创建表单引用，用于调用表单方法
      -->
      <el-form :model="loginForm" :rules="formRules" ref="loginFormRef" label-width="0px" size="large">

        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <!-- Element Plus 输入框组件
               v-model="loginForm.username" 双向绑定用户名数据
               placeholder="请输入用户名" 输入框提示文字
               prefix-icon="User" 输入框前面的用户图标
          -->
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" clearable />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <!-- Element Plus 密码输入框
               type="password" 表示这是密码输入框，会显示为黑点
               show-password 显示密码可见性切换按钮（小眼睛图标）
               @keyup.enter="handleLogin" 按回车键时触发登录
          -->
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password
                    clearable @keyup.enter="handleLogin" />
        </el-form-item>

        <!-- 记住用户名选项 -->
        <el-form-item>
          <!-- Element Plus 复选框
               v-model="rememberMe" 双向绑定记住密码的状态
          -->
          <el-checkbox v-model="rememberMe">记住用户名</el-checkbox>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <!-- Element Plus 按钮组件
               type="primary" 主要按钮样式（蓝色）
               :loading="loading" 根据loading状态显示加载动画
               @click="handleLogin" 点击时触发登录函数
          -->
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
// 这里导入Vue 3的核心功能
import { ref, reactive, onMounted } from 'vue'
// ref: 创建响应式的单个数据
// reactive: 创建响应式的对象
// onMounted: 页面加载完成后执行的钩子函数
import api from '@/services/api'
import { useRouter } from 'vue-router'  // 路由跳转功能
import { ElMessage } from 'element-plus'  // Element Plus 的消息提示组件
import { useUserStore } from '@/stores/user'  // 用户状态管理
import { JSEncrypt } from 'jsencrypt'  // RSA加密库
import heartbeatService from '@/services/heartbeat'  // 心跳检测服务

// ======= 响应式数据定义 =======
const router = useRouter()  // 创建路由对象，用于页面跳转
const userStore = useUserStore()  // 创建用户状态管理对象

// 创建响应式的单个数据
const loading = ref(false)     // 控制登录按钮的加载状态
const rememberMe = ref(false)  // 控制是否记住用户名的复选框状态
const publicKey = ref('')      // 存储从服务器获取的RSA公钥

// 创建表单引用，用于调用表单的验证方法
const loginFormRef = ref()

// 创建响应式的表单数据对象
const loginForm = reactive({
  username: '',  // 用户名输入框的值
  password: ''   // 密码输入框的值
})

// ======= 表单验证规则 =======
const formRules = reactive({
  // 用户名验证规则
  username: [
    {
      required: true,           // 必填字段
      message: '请输入用户名',   // 错误提示信息
      trigger: 'blur'          // 触发验证的时机（失去焦点时）
    }
  ],
  // 密码验证规则
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },
    {
      min: 1,                  // 最少1个字符
      message: '密码不能为空',
      trigger: 'blur'
    }
  ]
})

// ======= 获取RSA公钥函数 =======
const fetchPublicKey = async () => {
  try {
    console.log('正在获取RSA公钥...')
    const response = await api.get('/users/public-key')

    if (response.data.success) {
      publicKey.value = response.data.publicKey
      console.log('RSA公钥获取成功')
    } else {
      throw new Error(response.data.message || '获取公钥失败')
    }
  } catch (error) {
    console.error('获取RSA公钥失败:', error)
    ElMessage.error('获取加密密钥失败，请刷新页面重试')
  }
}

// ======= 页面加载时执行的函数 =======
onMounted(async () => {
  // 获取RSA公钥
  await fetchPublicKey()

  // 检查本地存储中是否有记住的用户名
  const rememberedUsername = localStorage.getItem('rememberedUsername')

  if (rememberedUsername) {
    // 如果有记住的用户名，自动填入表单
    loginForm.username = rememberedUsername
    rememberMe.value = true  // 勾选"记住用户名"复选框
  }
})

// ======= RSA加密密码函数 =======
const encryptPassword = (password) => {
  try {
    if (!publicKey.value) {
      throw new Error('RSA公钥未获取，请刷新页面重试')
    }

    // 创建JSEncrypt实例
    const encrypt = new JSEncrypt()

    // 设置公钥
    encrypt.setPublicKey(publicKey.value)

    // 加密密码
    const encryptedPassword = encrypt.encrypt(password)

    if (!encryptedPassword) {
      throw new Error('密码加密失败')
    }

    console.log('密码加密成功')
    return encryptedPassword
  } catch (error) {
    console.error('RSA密码加密失败:', error)
    throw error
  }
}

// ======= 登录处理函数 =======
const handleLogin = async () => {
  // async 表示这是一个异步函数，可以使用 await 等待网络请求

  // 首先验证表单是否填写正确
  if (!loginFormRef.value) return  // 如果表单引用不存在，直接返回

  try {
    // 调用Element Plus表单的validate方法进行验证
    await loginFormRef.value.validate()
  } catch (error) {
    // 如果验证失败，显示错误信息并停止执行
    ElMessage.error('请填写完整的登录信息')
    return
  }

  // 设置加载状态为true，按钮会显示"登录中..."
  loading.value = true

  try {
    // 每次登录前刷新一次公钥，避免使用过期公钥导致解密失败
    await fetchPublicKey()
    // 检查是否有公钥
    if (!publicKey.value) {
      ElMessage.error('加密密钥未准备就绪，请刷新页面重试')
      return
    }

    // 使用RSA加密密码
    let encryptedPassword
    try {
      encryptedPassword = encryptPassword(loginForm.password)
      console.log("密码加密成功")
    } catch (error) {
      ElMessage.error('密码加密失败：' + error.message)
      return
    }

    // 向后端发送登录请求
    console.log("正在向后端发送登录请求:", {
      username: loginForm.username,
      password: "*** RSA加密密码 ***"
    })

    const response = await api.post('/users/login', {
      username: loginForm.username,    // 发送用户名
      password: encryptedPassword      // 发送RSA加密后的密码
    })

    console.log("后端响应:", response.data)

    // 如果请求成功，response.data 包含后端返回的用户信息
    const userData = response.data

    // 使用 store 保存用户信息
    userStore.login(userData)

    // 如果用户勾选了"记住用户名"
    if (rememberMe.value) {
      localStorage.setItem('rememberedUsername', loginForm.username)
    } else {
      // 如果没有勾选，删除之前记住的用户名
      localStorage.removeItem('rememberedUsername')
    }

    // 显示登录成功的消息
    ElMessage.success(`登录成功！欢迎您，${userData.account}`)

    // 启动心跳检测服务
    heartbeatService.start()
    console.log('🔄 登录成功，已启动心跳检测')

    // 跳转到首页
    router.push({ name: 'dashboard' })

  } catch (error) {
    // 如果登录失败，处理错误信息
    const errorMessage = error.response?.data || '登录失败，请检查网络连接'

    // 根据不同的错误信息显示相应的提示
    if (typeof errorMessage === 'string') {
      if (errorMessage.includes('用户不存在')) {
        ElMessage.error('用户不存在，请检查用户名')
      } else if (errorMessage.includes('密码错误')) {
        ElMessage.error('密码错误，请重新输入')
      } else if (errorMessage.includes('密码解密失败')) {
        ElMessage.error('密码解密失败，请刷新页面后重试')
      } else if (errorMessage.includes('账号已被禁用')) {
        ElMessage.error('账号已被禁用，请联系管理员')
      } else {
        ElMessage.error(errorMessage)
      }
    } else {
      ElMessage.error('登录失败，请稍后重试')
    }
  } finally {
    // 无论成功还是失败，都要取消加载状态
    loading.value = false
  }
}
</script>

<style scoped>
/* scoped 表示这些样式只在当前组件中生效，不会影响其他页面 */

.login-page {
  /* 设置登录页面为全屏居中布局 */
  display: flex;
  /* 弹性布局 */
  justify-content: center;
  /* 水平居中 */
  align-items: center;
  /* 垂直居中 */
  min-height: 100vh;
  /* 最小高度为视窗高度 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  /* 渐变背景 */
  padding: 20px;
  /* 内边距 */
}

.login-card {
  /* 登录卡片的样式 */
  width: 100%;
  /* 宽度100% */
  max-width: 500px;
  /* 最大宽度400px */
  border-radius: 12px;
  /* 圆角 */
}

.card-header {
  /* 卡片头部（标题区域）的样式 */
  text-align: center;
  /* 文字居中 */
  padding: 20px 0;
  /* 上下内边距 */
}

.card-header h1 {
  /* 主标题样式 */
  color: #303133;
  /* 深灰色 */
  font-size: 24px;
  /* 字体大小 */
  font-weight: 600;
  /* 字体粗细 */
  margin-bottom: 8px;
  /* 下边距 */
}

.card-header p {
  /* 副标题样式 */
  color: #909399;
  /* 浅灰色 */
  font-size: 14px;
  /* 字体大小 */
  margin: 0;
  /* 清除默认边距 */
}

/* 响应式设计：在小屏幕上调整样式 */
@media (max-width: 480px) {
  .login-page {
    padding: 10px;
    /* 减少内边距 */
  }

  .login-card {
    max-width: 100%;
    /* 小屏幕上占满宽度 */
  }
}
</style>
