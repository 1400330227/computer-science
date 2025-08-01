<template>
  <!-- 注册页面的最外层容器 -->
  <div class="register-page">
    
    <!-- Element Plus 卡片组件，用来包装注册表单 -->
    <el-card class="register-card" shadow="always">
      
      <!-- 注册表单的标题 -->
      <template #header>
        <div class="card-header">
          <h1>用户注册</h1>
          <p>创建您的HDFS文件管理系统账户</p>
        </div>
      </template>

      <!-- Element Plus 表单组件 -->
      <el-form 
        :model="registerForm" 
        :rules="formRules" 
        ref="registerFormRef"
        label-width="80px"
        size="large"
      >
        
        <!-- 账号输入框 -->
        <el-form-item label="账号" prop="account">
          <el-input 
            v-model="registerForm.account"
            placeholder="请输入账号"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 确认密码输入框 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <!-- 手机号输入框 -->
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <!-- 性别选择 -->
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 邮箱地址输入框 -->
        <el-form-item label="邮箱" prop="address">
          <el-input 
            v-model="registerForm.address"
            placeholder="请输入您的邮箱地址"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <!-- 备注输入框 -->
        <el-form-item label="备注" prop="remarks">
          <el-input 
            v-model="registerForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            clearable
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleRegister"
            style="width: 100%; margin-bottom: 10px;"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>

        <!-- 返回登录按钮 -->
          <el-button 
            type="text"
            @click="goToLogin"
            style="width: 100%;"
          >
            已有账号？返回登录
          </el-button>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// ======= 响应式数据定义 =======
const router = useRouter()

// 创建响应式的单个数据
const loading = ref(false)

// 创建表单引用，用于调用表单的验证方法
const registerFormRef = ref()

// 创建响应式的表单数据对象
const registerForm = reactive({
  account: '',
  password: '',
  confirmPassword: '',
  phone: '',
  gender: '男',
  address: '',
  remarks: ''
})

// ======= 表单验证规则 =======
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
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
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { 
      type: 'email', 
      message: '请输入正确的邮箱格式', 
      trigger: 'blur' 
    }
  ],
  remarks: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
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
    // 向后端发送注册请求
    console.log("正在向后端发送注册请求:", registerForm)
    
    const response = await axios.post('http://localhost:8080/api/users/register', {
      account: registerForm.account,
      password: registerForm.password,
      phone: registerForm.phone,
      gender: registerForm.gender,
      address: registerForm.address,
      remarks: registerForm.remarks
    })
    
    console.log("后端响应:", response.data)

    // 如果请求成功，显示成功消息
    ElMessage.success('注册成功！请使用您的账号登录')
    
    // 跳转到登录页面
      router.push({ name: 'login' })

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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  /* 注册卡片的样式 */
  width: 100%;
  max-width: 450px;
  border-radius: 12px;
}

.card-header {
  /* 卡片头部（标题区域）的样式 */
  text-align: center;
  padding: 20px 0;
}
  
.card-header h1 {
  /* 主标题样式 */
    color: #303133;
  font-size: 24px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
.card-header p {
  /* 副标题样式 */
    color: #909399;
    font-size: 14px;
    margin: 0;
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