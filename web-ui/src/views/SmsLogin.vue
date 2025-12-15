<template>
  <!-- 登录页面的最外层容器 -->
  <div class="login-page">

    <!-- Element Plus 卡片组件，用来包装登录表单 -->
    <el-card class="login-card" shadow="always">

      <!-- 登录表单的标题 -->
      <template #header>
        <div class="card-header">
          <h1>广西大学东盟语料库管理与标注平台</h1>
          <p>请输入您的手机号信息登录</p>
        </div>
      </template>

      <!-- Element Plus 表单组件
           :model="loginForm" 绑定表单数据
           :rules="formRules" 绑定验证规则
           ref="loginFormRef" 创建表单引用，用于调用表单方法
      -->
      <el-form :model="loginForm" :rules="formRules" ref="loginFormRef" label-width="0px" size="large">

        <!-- 手机号输入框 -->
        <el-form-item prop="phone">
          <el-input v-model="loginForm.phone" placeholder="请输入手机号" prefix-icon="Iphone" clearable maxlength="11" />
        </el-form-item>

        <!-- 验证码输入框 -->
        <el-form-item prop="code">
          <div style="display: flex; gap: 10px;">
            <el-input v-model="loginForm.code" placeholder="请输入验证码" prefix-icon="Message" clearable maxlength="6"
              style="flex: 1;" @keyup.enter="handleLogin" />
            <el-button type="primary" :disabled="codeButtonDisabled" @click="sendVerificationCode"
              style="min-width: 120px;">
              {{ codeButtonText }}
            </el-button>
          </div>
        </el-form-item>

        <!-- 记住手机号选项 -->
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住手机号</el-checkbox>
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

import heartbeatService from '@/services/heartbeat'  // 心跳检测服务

// ======= 响应式数据定义 =======
const router = useRouter()  // 创建路由对象，用于页面跳转
const userStore = useUserStore()  // 创建用户状态管理对象

// 创建响应式的单个数据
const loading = ref(false)     // 控制登录按钮的加载状态
const rememberMe = ref(false)  // 控制是否记住手机号的复选框状态
const codeButtonDisabled = ref(false)  // 控制验证码按钮的禁用状态
const countdown = ref(0)       // 倒计时秒数

// 创建表单引用，用于调用表单的验证方法
const loginFormRef = ref()

// 创建响应式的表单数据对象
const loginForm = reactive({
  phone: '',  // 手机号输入框的值
  code: ''    // 验证码输入框的值
})

// 验证码按钮文字
const codeButtonText = ref('获取验证码')

// ======= 表单验证规则 =======
const formRules = reactive({
  // 手机号验证规则
  phone: [
    {
      required: true,
      message: '请输入手机号',
      trigger: 'blur'
    },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号格式',
      trigger: 'blur'
    }
  ],
  // 验证码验证规则
  code: [
    {
      required: true,
      message: '请输入验证码',
      trigger: 'blur'
    },
    {
      pattern: /^\d{6}$/,
      message: '验证码为6位数字',
      trigger: 'blur'
    }
  ]
})

// ======= 发送验证码函数 =======
const sendVerificationCode = async () => {
  // 首先验证手机号格式
  if (!loginForm.phone) {
    ElMessage.error('请先输入手机号')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(loginForm.phone)) {
    ElMessage.error('请输入正确的手机号格式')
    return
  }

  try {
    codeButtonDisabled.value = true

    const response = await api.post('/users/send-verification-code', {
      phone: loginForm.phone
    })

    if (response.data.success) {
      ElMessage.success('验证码已发送，请查收')

      // 开始60秒倒计时
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        codeButtonText.value = `${countdown.value}秒后重发`

        if (countdown.value <= 0) {
          clearInterval(timer)
          codeButtonDisabled.value = false
          codeButtonText.value = '获取验证码'
        }
      }, 1000)
    } else {
      ElMessage.error(response.data.message || '发送验证码失败')
      codeButtonDisabled.value = false
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    const errorMessage = error.response?.data?.message || '发送验证码失败，请稍后重试'
    ElMessage.error(errorMessage)
    codeButtonDisabled.value = false
  }
}

// ======= 页面加载时执行的函数 =======
onMounted(async () => {
  // 检查本地存储中是否有记住的手机号
  const rememberedPhone = localStorage.getItem('rememberedPhone')

  if (rememberedPhone) {
    // 如果有记住的手机号，自动填入表单
    loginForm.phone = rememberedPhone
    rememberMe.value = true  // 勾选"记住手机号"复选框
  }
})



// ======= 登录处理函数 =======
const handleLogin = async () => {
  // 首先验证表单是否填写正确
  if (!loginFormRef.value) return

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
    // 向后端发送短信登录请求
    console.log("正在向后端发送短信登录请求:", {
      phone: loginForm.phone,
      code: loginForm.code
    })

    const response = await api.post('/users/login-with-sms', {
      phone: loginForm.phone,
      code: loginForm.code
    })

    console.log("后端响应:", response.data)

    // 如果请求成功，response.data 包含后端返回的用户信息
    const userData = response.data

    // 使用 store 保存用户信息
    userStore.login(userData)

    // 如果用户勾选了"记住手机号"
    if (rememberMe.value) {
      localStorage.setItem('rememberedPhone', loginForm.phone)
    } else {
      // 如果没有勾选，删除之前记住的手机号
      localStorage.removeItem('rememberedPhone')
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
      if (errorMessage.includes('该手机号尚未注册')) {
        ElMessage.error('该手机号尚未注册，请先注册')
      } else if (errorMessage.includes('验证码不正确')) {
        ElMessage.error('验证码不正确，请重新输入')
      } else if (errorMessage.includes('验证码已过期')) {
        ElMessage.error('验证码已过期，请重新获取')
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
