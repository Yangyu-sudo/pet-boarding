<template>
  <el-container class="admin-container">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="admin-sidebar">
      <div class="sidebar-header">
        <span v-if="!isCollapsed" class="logo-text">🐾 宠物寄养馆</span>
        <span v-else class="logo-text-mini">🐾</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/cages">
          <el-icon><House /></el-icon>
          <span>笼舍管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/pets">
          <el-icon><Star /></el-icon>
          <span>宠物管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/customers">
          <el-icon><User /></el-icon>
          <span>客户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/services">
          <el-icon><Goods /></el-icon>
          <span>服务管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/finance">
          <el-icon><Money /></el-icon>
          <span>财务管理</span>
        </el-menu-item>
        <el-sub-menu index="system" v-permission="['ADMIN']">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/system/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/system/logs">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed" :size="22">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-tag size="small" type="warning" v-if="userStore.isAdmin">管理员</el-tag>
              <el-tag size="small" type="success" v-else-if="userStore.isStaff">员工</el-tag>
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

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)

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
.admin-container {
  height: 100vh;
}
.admin-sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
.logo-text {
  white-space: nowrap;
}
.admin-sidebar .el-menu {
  border-right: none;
}
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
