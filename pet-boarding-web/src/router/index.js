import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 布局组件
const CustomerLayout = () => import('@/components/CustomerLayout.vue')
const AdminLayout = () => import('@/components/AdminLayout.vue')

const routes = [
  // 公开路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { title: '注册', noAuth: true }
  },

  // 顾客端路由
  {
    path: '/customer',
    component: CustomerLayout,
    meta: { requiresAuth: true, role: 'CUSTOMER' },
    redirect: '/customer/pets',
    children: [
      {
        path: 'pets',
        name: 'MyPets',
        component: () => import('@/views/pet/list.vue'),
        meta: { title: '我的宠物' }
      },
      {
        path: 'pets/add',
        name: 'AddPet',
        component: () => import('@/views/pet/form.vue'),
        meta: { title: '添加宠物' }
      },
      {
        path: 'pets/:id',
        name: 'PetDetail',
        component: () => import('@/views/pet/detail.vue'),
        meta: { title: '宠物详情' }
      },
      {
        path: 'pets/:id/edit',
        name: 'EditPet',
        component: () => import('@/views/pet/form.vue'),
        meta: { title: '编辑宠物' }
      },
      {
        path: 'orders',
        name: 'MyOrders',
        component: () => import('@/views/order/customer-list.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'orders/create',
        name: 'CreateOrder',
        component: () => import('@/views/order/create.vue'),
        meta: { title: '预约寄养' }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },

  // 管理端路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: ['ADMIN', 'STAFF'] },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'orders',
        name: 'OrderManage',
        component: () => import('@/views/order/admin-list.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'orders/:id',
        name: 'AdminOrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'cages',
        name: 'CageManage',
        component: () => import('@/views/cage/list.vue'),
        meta: { title: '笼舍管理' }
      },
      {
        path: 'pets',
        name: 'PetManage',
        component: () => import('@/views/pet/admin-list.vue'),
        meta: { title: '宠物管理' }
      },
      {
        path: 'customers',
        name: 'CustomerManage',
        component: () => import('@/views/customer/list.vue'),
        meta: { title: '客户管理' }
      },
      {
        path: 'services',
        name: 'ServiceManage',
        component: () => import('@/views/service/list.vue'),
        meta: { title: '服务管理' }
      },
      {
        path: 'finance',
        name: 'Finance',
        component: () => import('@/views/finance/index.vue'),
        meta: { title: '财务管理' }
      },
      {
        path: 'system/users',
        name: 'UserManage',
        component: () => import('@/views/system/users.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      },
      {
        path: 'system/logs',
        name: 'OperationLogs',
        component: () => import('@/views/system/logs.vue'),
        meta: { title: '操作日志', role: 'ADMIN' }
      }
    ]
  },

  // 默认重定向
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宠物寄养馆` : '宠物寄养馆管理系统'

  const userStore = useUserStore()

  if (to.meta.noAuth) {
    next()
    return
  }

  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 角色权限检查
  if (to.meta.role) {
    const requiredRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]
    if (!requiredRoles.includes(userStore.userInfo?.role)) {
      next({ name: 'Dashboard' })
      return
    }
  }

  next()
})

export default router
