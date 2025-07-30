<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>HDFS 文件管理系统</h1>
      </div>
      <div class="login-form">
        <h2>用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-item">
            <div class="input-wrapper">
              <input 
                v-model="loginForm.username"
                type="text"
                placeholder="请输入用户名"
                class="login-input"
                autofocus
              />
            </div>
            <div class="error-message" v-if="errors.username">{{ errors.username }}</div>
          </div>
          
          <div class="form-item">
            <div class="input-wrapper">
              <input 
                v-model="loginForm.password"
                :type="passwordVisible ? 'text' : 'password'"
                placeholder="请输入密码"
                class="login-input password-input"
                @keyup.enter="handleLogin"
              />
              <span class="password-toggle" @click="togglePasswordVisibility">
                {{ passwordVisible ? '👁️' : '👁️‍🗨️' }}
              </span>
            </div>
            <div class="error-message" v-if="errors.password">{{ errors.password }}</div>
          </div>
          
          <div class="form-item remember">
            <label class="checkbox-label">
              <input type="checkbox" v-model="rememberMe" />
              <span class="checkbox-text">记住密码</span>
            </label>
            <div class="forget-link">
              <a href="javascript:;">忘记密码？</a>
            </div>
          </div>
          
          <div class="form-item">
            <button type="submit" class="login-button" :disabled="loading">
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const rememberMe = ref(false)
const passwordVisible = ref(false)
const errors = reactive({
  username: '',
  password: ''
})

const loginForm = reactive({
  username: '',
  password: ''
})

const togglePasswordVisibility = () => {
  passwordVisible.value = !passwordVisible.value
}

const validateForm = () => {
  let valid = true
  errors.username = ''
  errors.password = ''
  
  if (!loginForm.username) {
    errors.username = '请输入用户名'
    valid = false
  }
  
  if (!loginForm.password) {
    errors.password = '请输入密码'
    valid = false
  }
  
  return valid
}

const handleLogin = () => {
  if (!validateForm()) return
  
  loading.value = true
  
  // 设置登录状态
  localStorage.setItem('isLoggedIn', 'true')
  localStorage.setItem('username', loginForm.username)
  
  // 显示成功消息
  alert('登录成功')
  
  // 跳转到主页(Dashboard)
  router.push('/')

  // 模拟延迟，实际应用中应该调用API
  setTimeout(() => {
    loading.value = false
  }, 1000)
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(120deg, #5a8de1, #497ccf);
}

.login-box {
  width: 380px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #4B70BD;
  font-size: 24px;
  font-weight: 500;
}

.login-form h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 25px;
  font-weight: 500;
  text-align: center;
}

.form-item {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
}

.login-input {
  width: 100%;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 0 15px;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s;
  outline: none;
  box-sizing: border-box;
}

.password-input {
  padding-right: 40px;
}

.password-toggle {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  user-select: none;
  color: #909399;
}

.login-input:focus {
  border-color: #409eff;
}

.login-input::placeholder {
  color: #c0c4cc;
}

.login-button {
  width: 100%;
  height: 40px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #66b1ff;
}

.login-button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.remember {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-text {
  margin-left: 5px;
  color: #606266;
  font-size: 14px;
}

.forget-link a {
  color: #5a8de1;
  text-decoration: none;
  font-size: 14px;
}

.forget-link a:hover {
  text-decoration: underline;
}

.error-message {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 5px;
}
</style>
