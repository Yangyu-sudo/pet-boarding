<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <h2>个人信息</h2>
      </template>
      <el-tabs>
        <el-tab-pane label="基本信息">
          <el-form :model="userForm" label-width="100px" style="max-width: 500px">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.userInfo?.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="userForm.realName" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag>{{ userStore.userInfo?.role }}</el-tag>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="修改密码">
          <el-form :model="passwordForm" label-width="100px" style="max-width: 400px" ref="pwdFormRef">
            <el-form-item label="旧密码" prop="oldPassword" :rules="[{ required: true, message: '请输入旧密码' }]">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword" :rules="[{ required: true, min: 6, message: '密码至少6位' }]">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword" :rules="[
              { required: true, message: '请确认密码' },
              { validator: (r, v, cb) => v === passwordForm.newPassword ? cb() : cb(new Error('两次密码不一致')) }
            ]">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const pwdFormRef = ref(null)

const userForm = reactive({
  realName: userStore.userInfo?.realName || '',
  phone: userStore.userInfo?.phone || '',
  email: userStore.userInfo?.email || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

async function handleUpdateProfile() {
  try {
    await userStore.updateProfile(userForm)
    ElMessage.success('信息更新成功')
  } catch {}
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await userStore.doChangePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch {}
}
</script>
