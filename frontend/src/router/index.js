import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'

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
  }
]

const router = createRouter({
  history: createWebHistory('/yunkeji'),
  routes
})

export default router