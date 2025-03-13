import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/outline'
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    component: () => import('../layout/Layout.vue'),
    children: [
      {
        path: 'outline',
        name: 'outline',
        component: () => import('../views/OutlineGeneration.vue')
      },
      {
        path: 'document',
        name: 'document',
        component: () => import('../views/DocumentManagement.vue')
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path === '/login' || to.path === '/register') {
    if (token) {
      next('/outline')
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  next()
})

export default router 