import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import AuthService from '@/services/AuthService'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue')
  },
  {
    path: '/query',
    name: 'Query',
    component: () => import('@/views/Query.vue')
  },
  {
    path: '/query/:id',
    name: 'QueryDetail',
    component: () => import('@/views/QueryDetail.vue')
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/Test.vue')
  },
  {
    path: '/test2',
    name: 'Test2',
    component: () => import('@/views/Test2.vue')
  },
  {
    path: '/admin',
    name: 'AdminQueryManagement',
    component: () => import('@/views/AdminQueryManagement.vue'),
    meta: { requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory('/yunkeji'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta?.requiresAdmin) {
    if (!AuthService.isAuthenticated()) {
      next({ path: '/login' })
      return
    }
    if (!AuthService.isAdmin()) {
      next({ path: '/' })
      return
    }
  }
  next()
})

export default router