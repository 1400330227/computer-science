<template>
  <!-- 注册页面的最外层容器 -->
  <div class="register-page">

    <!-- Element Plus 卡片组件，用来包装注册表单 -->
    <el-card class="register-card" shadow="never">

      <!-- 注册表单的标题 -->
      <template #header>
        <div class="card-header">
          <h1>用户注册</h1>
          <!-- <p>创建您的HDFS文件管理系统账户</p> -->
        </div>
      </template>

      <!-- Element Plus 表单组件 -->
      <el-form :model="registerForm" :rules="formRules" ref="registerFormRef" label-width="80px" size="large">

        <!-- 账号输入框 -->
        <el-form-item label="账号" prop="account">
          <el-input v-model="registerForm.account" placeholder="请输入账号" prefix-icon="User" clearable />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password
            clearable />
          <div class="password-hint">密码需至少8位，且包含大写字母、小写字母和数字</div>
        </el-form-item>

        <!-- 确认密码输入框 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock"
            show-password clearable @keyup.enter="handleRegister" />
        </el-form-item>

        <!-- 手机号输入框 -->
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="Phone" clearable />
        </el-form-item>

        <!-- 性别选择 -->
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 备注输入框 -->
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="registerForm.remarks" type="textarea" :rows="3" placeholder="请输入备注信息（可选）" clearable />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister"
            style="width: 100%; margin-bottom: 10px;">
            {{ loading ? '注册中...' : '注册' }}
          </el-button>

          <!-- 返回登录按钮 -->
          <el-button type="text" @click="goToLogin" style="width: 100%;">
            已有账号？返回登录
          </el-button>
        </el-form-item>

        <!-- 注册成功后的倒计时提示 -->
        <el-form-item v-if="countdown > 0">
          <div class="countdown-hint">
            <div>注册成功！{{ countdown }} 秒后将自动跳转到登录页面</div>
            <el-button link type="primary" @click="goToLogin">立即前往</el-button>
          </div>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { ElMessage } from 'element-plus'
import { JSEncrypt } from 'jsencrypt'  // 导入RSA加密库


// ======= 响应式数据定义 =======
const router = useRouter()

// 创建响应式的单个数据
const loading = ref(false)
const publicKey = ref('')  // 存储从服务器获取的RSA公钥
const countdown = ref(0)   // 注册成功后的倒计时（秒）
let countdownTimer = null  // 计时器句柄

// 创建表单引用，用于调用表单的验证方法
const registerFormRef = ref()

// 创建响应式的表单数据对象
const registerForm = reactive({
  account: '',
  password: '',
  confirmPassword: '',
  phone: '',
  gender: '男',
  remarks: ''
})

// ======= 表单验证规则 =======
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (!/[A-Z]/.test(value)) {
    callback(new Error('密码需包含大写字母'))
  } else if (!/[a-z]/.test(value)) {
    callback(new Error('密码需包含小写字母'))
  } else if (!/[0-9]/.test(value)) {
    callback(new Error('密码需包含数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

const formRules = reactive({
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  remarks: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
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

// 页面加载时获取公钥
onMounted(async () => {
  // 获取RSA公钥
  await fetchPublicKey()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})

// ======= 注册处理函数 =======
const handleRegister = async () => {
  // 首先验证表单是否填写正确
  if (!registerFormRef.value) return

  try {
    // 调用Element Plus表单的validate方法进行验证
    await registerFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请填写完整的注册信息')
    return
  }

  // 设置加载状态为true
  loading.value = true

  try {
    // 检查是否有公钥
    if (!publicKey.value) {
      ElMessage.error('加密密钥未准备就绪，请刷新页面重试')
      return
    }

    // 使用RSA加密密码
    let encryptedPassword
    try {
      encryptedPassword = encryptPassword(registerForm.password)
      console.log("密码加密成功")
    } catch (error) {
      ElMessage.error('密码加密失败：' + error.message)
      return
    }

    // 向后端发送注册请求
    console.log("正在向后端发送注册请求:", {
      account: registerForm.account,
      password: "*** RSA加密密码 ***",
      phone: registerForm.phone,
      gender: registerForm.gender,
      remarks: registerForm.remarks
    })

    const response = await api.post('/users', {
      account: registerForm.account,
      password: encryptedPassword,  // 使用加密后的密码
      phone: registerForm.phone,
      gender: registerForm.gender,
      remarks: registerForm.remarks
    })

    console.log("后端响应:", response.data)

    // 如果请求成功，显示成功消息
    ElMessage.success('注册成功！5秒后将自动跳转到登录页面')

    // 启动 5 秒倒计时，并在结束时跳转登录页
    countdown.value = 5
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
    countdownTimer = setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
        router.push({ name: 'login' })
      }
    }, 1000)

  } catch (error) {
    // 如果注册失败，处理错误信息
    const errorMessage = error.response?.data || '注册失败，请检查网络连接'

    // 根据不同的错误信息显示相应的提示
    if (typeof errorMessage === 'string') {
      if (errorMessage.includes('账号已存在')) {
        ElMessage.error('账号已存在，请选择其他账号')
      } else if (errorMessage.includes('账号不能为空')) {
        ElMessage.error('账号不能为空')
      } else if (errorMessage.includes('密码不能为空')) {
        ElMessage.error('密码不能为空')
      } else if (errorMessage.includes('手机号不能为空')) {
        ElMessage.error('手机号不能为空')
      } else {
        ElMessage.error(errorMessage)
      }
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    // 无论成功还是失败，都要取消加载状态
    loading.value = false
  }
}

// ======= 跳转到登录页面 =======
const goToLogin = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  router.push({ name: 'login' })
}
</script>

<style scoped>
/* scoped 表示这些样式只在当前组件中生效，不会影响其他页面 */

.register-page {
  /* 设置注册页面为全屏居中布局 */
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  /* background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); */
  background-color: #F8FAFD;
  padding: 20px;
}

.register-card {
  /* 注册卡片的样式 */
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
}

.card-header {
  /* 卡片头部（标题区域）的样式 */
  text-align: center;
  /* padding: 20px 0; */
}

.card-header h1 {
  /* 主标题样式 */
  color: #303133;
  font-size: 24px;
  /* font-weight: 600; */
  /* margin-bottom: 8px; */
}

.card-header p {
  /* 副标题样式 */
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.password-hint {
  font-size: 12px;
  color: #909399;
  /* margin-top: 5px; */
}

.countdown-hint {
  width: 100%;
}

/* 响应式设计：在小屏幕上调整样式 */
@media (max-width: 480px) {
  .register-page {
    padding: 10px;
  }

  .register-card {
    max-width: 100%;
  }
}
</style>