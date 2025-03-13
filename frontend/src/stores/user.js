import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register, getUserProfile } from '../api/user'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({
    username: '',
    email: '',
    avatar: ''
  })

  // 登录
  async function loginAction(loginForm) {
    try {
      const res = await login(loginForm)
      token.value = res.token
      localStorage.setItem('token', res.token)
      await loadUserInfo()
      router.push('/outline')
      return res
    } catch (error) {
      throw error
    }
  }

  // 注册
  async function registerAction(registerForm) {
    try {
      const res = await register(registerForm)
      return res
    } catch (error) {
      throw error
    }
  }

  // 加载用户信息
  async function loadUserInfo() {
    try {
      const res = await getUserProfile()
      userInfo.value = res
      return res
    } catch (error) {
      throw error
    }
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = {
      username: '',
      email: '',
      avatar: ''
    }
    localStorage.removeItem('token')
    router.push('/login')
  }

  return {
    token,
    userInfo,
    loginAction,
    registerAction,
    loadUserInfo,
    logout
  }
}) 