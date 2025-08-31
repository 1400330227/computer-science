<template>
  <!-- 个人信息页面的最外层容器 -->
  <div class="profile-page">

    <!-- Element Plus 卡片组件，用来包装个人信息表单 -->
    <el-card class="profile-card" shadow="never">

      <!-- 个人信息表单的标题 -->
      <template #header>
        <div class="card-header">
          <h1>个人信息</h1>
          <p>请完善您的个人信息，以便我们更好地为您服务</p>
        </div>
      </template>

      <!-- Element Plus 表单组件 -->
      <el-form :model="profileForm" :rules="formRules" ref="profileFormRef" label-width="80px" size="large">

        <!-- 账号输入框（只读） -->
        <el-form-item label="账号" prop="account">
          <el-input v-model="profileForm.account" placeholder="系统账号" prefix-icon="User" readonly />
        </el-form-item>

        <!-- 姓名输入框 -->
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入您的真实姓名" prefix-icon="User" clearable />
        </el-form-item>

        <!-- 手机号输入框 -->
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" prefix-icon="Phone" clearable />
        </el-form-item>

        <!-- 学院 -->
        <el-form-item label="学院" prop="college">
          <el-select v-model="profileForm.college" placeholder="请选择学院" clearable filterable autocomplete="on">
            <el-option-group v-for="group in collegeGroups" :key="group.label" :label="group.label">
              <el-option v-for="name in group.options" :key="name" :label="name" :value="name" />
            </el-option-group>
          </el-select>
        </el-form-item>

        <!-- 职称 -->
        <el-form-item label="职称" prop="title">
          <el-input v-model="profileForm.title" placeholder="请输入职称（可选）" clearable />
        </el-form-item>

        <!-- 专业 -->
        <el-form-item label="专业" prop="major">
          <el-input v-model="profileForm.major" placeholder="请输入专业（可选）" clearable />
        </el-form-item>

        <!-- 性别选择 -->
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 备注输入框 -->
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="profileForm.remarks" type="textarea" :rows="3" placeholder="请输入备注信息（可选）" clearable />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleUpdateProfile"
            style="width: 100%; margin-bottom: 10px;">
            {{ loading ? '更新中...' : '更新个人信息' }}
          </el-button>

          <!-- 修改密码按钮 -->
          <el-button type="info" @click="showPasswordDialog = true"
            style="width: 100%; margin-bottom: 10px; margin-left: 0">
            修改密码
          </el-button>

          <!-- 返回按钮 -->
          <el-button type="text" @click="goBack" style="width: 100%;">
            返回
          </el-button>
        </el-form-item>

        <!-- 更新成功后的提示 -->
        <el-form-item v-if="updateSuccess">
          <div class="success-hint">
            <el-alert title="个人信息更新成功！" type="success" :closable="false" />
          </div>
        </el-form-item>

      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="600px" :close-on-click-modal="false">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password clearable />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password clearable />
          <div class="password-hint">密码需至少8位，且包含大写字母、小写字母和数字</div>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password
            clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
            {{ passwordLoading ? '修改中...' : '确认修改' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { ElMessage } from 'element-plus'
import { JSEncrypt } from 'jsencrypt'  // 导入RSA加密库
import { useUserStore } from '@/stores/user'


// ======= 响应式数据定义 =======
const router = useRouter()
const userStore = useUserStore()

// 创建响应式的单个数据
const loading = ref(false)
const passwordLoading = ref(false)
const publicKey = ref('')  // 存储从服务器获取的RSA公钥
const updateSuccess = ref(false)  // 更新成功标志
const showPasswordDialog = ref(false)  // 显示密码修改对话框

// 创建表单引用，用于调用表单的验证方法
const profileFormRef = ref()
const passwordFormRef = ref()

// 创建响应式的个人信息表单数据对象
const profileForm = reactive({
  account: '',
  nickname: '',
  phone: '',
  college: '',
  title: '',
  major: '',
  gender: '男',
  remarks: ''
})

// 创建响应式的密码修改表单数据对象
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 学院选项（分组）
const collegeGroups = [
  {
    label: '学院',
    options: [
      '资源环境与材料学院',
      '轻工与食品工程学院',
      '计算机与电子信息学院',
      '海洋学院',
      '生命科学与技术学院',
      '农学院',
      '动物科学技术学院',
      '林学院',
      '数学与信息科学学院',
      '物理科学与工程技术学院',
      '文学院',
      '新闻与传播学院',
      '外国语学院',
      '艺术学院',
      '公共管理学院',
      '工商管理学院',
      '法学院',
      '马克思主义学院',
      '体育学院',
      '医学院',
      '继续教育学院',
      '中国—东盟经济学院 / 经济学院 / 中国—东盟金融合作学院'
    ]
  },
  {
    label: '党政管理机构',
    options: [
      '党委办公室、校长办公室（校务督查办公室、法治与法务办公室）',
      '驻校纪检监察组、校纪委',
      '党委巡察办、党委巡察组',
      '党委组织部（中共广西大学委员会党校）',
      '党委宣传部',
      '党委统战部',
      '党委学生工作部、学生工作处、武装部（就业指导中心）',
      '机关党委',
      '保卫处（治安综合治理委员会办公室）',
      '校团委',
      '校工会（校医院）',
      '党委教师工作部、人力资源处',
      '发展规划处',
      '教务处（教师教学服务中心）',
      '科研院',
      '研究生院',
      '教育教学质量监控与评价中心',
      '财务处',
      '审计处',
      '国际合作与交流处（留学生管理服务中心、港澳台事务办公室）',
      '国有资产与实验室管理处（招标与采购管理中心）',
      '后勤基建处',
      '离退休工作处'
    ]
  },
  {
    label: '研究机构',
    options: [
      '亚热带农业生物资源保护与利用国家重点实验室',
      '省部共建特色金属材料与组合结构全寿命安全国家重点实验室',
      '中国—东盟研究院/广西创新发展研究院',
      '君武文化研究院',
      '农牧产业发展研究院（广西牧草工作站、新农村发展研究院）'
    ]
  },
  {
    label: '教辅与服务机构',
    options: [
      '图书馆（档案馆）',
      '信息网络中心（网络安全与信息化办公室）',
      '校友工作办公室',
      '资产经营有限公司',
      '教育发展基金会'
    ]
  }
]

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
  } else if (value !== passwordForm.newPassword) {
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
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请选择学院', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  remarks: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
})

const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
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

// ======= 获取用户信息函数 =======
const fetchUserInfo = () => {
  try {
    console.log('正在获取用户信息...')
    const userInfo = userStore.userInfo

    // 填充表单数据
    Object.assign(profileForm, {
      account: userInfo.account || '',
      nickname: userInfo.nickname || '',
      phone: userInfo.phone || '',
      college: userInfo.college || '',
      title: userInfo.title || '',
      major: userInfo.major || '',
      gender: userInfo.gender || '男',
      remarks: userInfo.remarks || ''
    })
    console.log('用户信息获取成功')
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，请刷新页面重试')
  }
}

// 页面加载时获取公钥和用户信息
onMounted(async () => {
  // 获取RSA公钥
  await fetchPublicKey()
  // 获取用户信息
  fetchUserInfo()
})

// ======= 更新个人信息处理函数 =======
const handleUpdateProfile = async () => {
  // 首先验证表单是否填写正确
  if (!profileFormRef.value) return

  try {
    // 调用Element Plus表单的validate方法进行验证
    await profileFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请填写完整的个人信息')
    return
  }

  // 设置加载状态为true
  loading.value = true

  try {
    // 向后端发送更新请求
    console.log("正在向后端发送个人信息更新请求:", {
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      college: profileForm.college,
      title: profileForm.title,
      major: profileForm.major,
      gender: profileForm.gender,
      remarks: profileForm.remarks
    })

    const response = await api.put('/users/profile', {
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      college: profileForm.college,
      title: profileForm.title,
      major: profileForm.major,
      gender: profileForm.gender,
      remarks: profileForm.remarks
    })

    console.log("后端响应:", response.data)

    // 如果请求成功，显示成功消息
    ElMessage.success('个人信息更新成功！')
    updateSuccess.value = true

    // 更新本地用户状态
    userStore.updateUserInfo({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      college: profileForm.college,
      title: profileForm.title,
      major: profileForm.major,
      gender: profileForm.gender,
      remarks: profileForm.remarks
    })

    // 3秒后隐藏成功提示
    setTimeout(() => {
      updateSuccess.value = false
    }, 3000)

  } catch (error) {
    // 如果更新失败，处理错误信息
    const errorMessage = error.response?.data?.message || '更新失败，请检查网络连接'
    ElMessage.error(errorMessage)
  } finally {
    // 无论成功还是失败，都要取消加载状态
    loading.value = false
  }
}

// ======= 修改密码处理函数 =======
const handleChangePassword = async () => {
  // 首先验证表单是否填写正确
  if (!passwordFormRef.value) return

  try {
    // 调用Element Plus表单的validate方法进行验证
    await passwordFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请填写完整的密码信息')
    return
  }

  // 设置加载状态为true
  passwordLoading.value = true

  try {
    // 检查是否有公钥
    if (!publicKey.value) {
      ElMessage.error('加密密钥未准备就绪，请刷新页面重试')
      return
    }

    // 使用RSA加密密码
    let encryptedOldPassword, encryptedNewPassword
    try {
      encryptedOldPassword = encryptPassword(passwordForm.oldPassword)
      encryptedNewPassword = encryptPassword(passwordForm.newPassword)
      console.log("密码加密成功")
    } catch (error) {
      ElMessage.error('密码加密失败：' + error.message)
      return
    }

    // 向后端发送密码修改请求
    const response = await api.put('/users/password', {
      oldPassword: encryptedOldPassword,
      newPassword: encryptedNewPassword
    })

    console.log("后端响应:", response.data)

    // 如果请求成功，显示成功消息
    ElMessage.success('密码修改成功！')
    showPasswordDialog.value = false

    // 清空密码表单
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

  } catch (error) {
    // 如果修改失败，处理错误信息
    const errorMessage = error.response?.data?.message || '密码修改失败，请检查网络连接'
    ElMessage.error(errorMessage)
  } finally {
    // 无论成功还是失败，都要取消加载状态
    passwordLoading.value = false
  }
}

// ======= 返回上一页 =======
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
/* scoped 表示这些样式只在当前组件中生效，不会影响其他页面 */

.profile-page {
  /* 设置个人信息页面为全屏居中布局 */
  display: flex;
  justify-content: center;
  /* align-items: center; */
  min-height: 100vh;
  background-color: #F8FAFD;
  /* padding: 20px; */
}

.profile-card {
  /* 个人信息卡片的样式 */
  width: 100%;
  max-width: 600px;
  /* border-radius: 12px; */
}

.card-header {
  /* 卡片头部（标题区域）的样式 */
  text-align: center;
}

.card-header h1 {
  /* 主标题样式 */
  color: #303133;
  font-size: 24px;
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
  line-height: 1.5;
}

.success-hint {
  width: 100%;
}

/* 响应式设计：在小屏幕上调整样式 */
@media (max-width: 480px) {
  .profile-page {
    padding: 10px;
  }

  .profile-card {
    max-width: 100%;
  }
}
</style>