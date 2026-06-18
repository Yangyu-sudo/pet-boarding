<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <h2>🐾 宠物寄养馆</h2>
      </div>
      <div class="header-right">
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          router
          :ellipsis="false"
        >
          <el-menu-item index="/customer/pets">我的宠物</el-menu-item>
          <el-menu-item index="/customer/orders">寄养订单</el-menu-item>
          <el-menu-item index="/customer/orders/create">
            <el-button type="primary" size="small">预约寄养</el-button>
          </el-menu-item>
        </el-menu>
        <el-dropdown @command="handleCommand">
          <span class="user-avatar">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
            <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="layout-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/customer/profile')
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: #f5f7fa;
}
.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 60px;
}
.header-left h2 {
  margin: 0;
  color: #409eff;
  font-size: 20px;
  white-space: nowrap;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-right .el-menu {
  border-bottom: none;
}
.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
  color: #606266;
}
.layout-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>
