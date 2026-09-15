import router from '@/router'
import cache from '@/plugins/cache'
import { ElMessageBox, } from 'element-plus'
import { login, logout } from '@/api/login'
import { getToken, setToken, removeToken, setRefreshToken, removeRefreshToken } from '@/utils/auth'
import { isHttp, isEmpty } from "@/utils/validate"
import defAva from '@/assets/images/profile.jpg'

const useUserStore = defineStore(
  'user',
  {
    state: () => ({
      token: getToken(),
      id: '',
      name: '',
      nickName: '',
      avatar: '',
      roles: [],
      permissions: []
    }),
    actions: {
      // 登录
      login(userInfo) {
        const username = userInfo.username.trim()
        const password = userInfo.password
        return new Promise((resolve, reject) => {
          login(username, password).then(res => {
            const accessToken = res.accessToken || res.token
            if (!accessToken) {
              reject(new Error('登录接口未返回 accessToken'))
              return
            }
            setToken(accessToken)
            if (res.refreshToken) {
              setRefreshToken(res.refreshToken)
            }
            this.token = accessToken
            this.id = res.userId
            this.name = res.username
            this.roles = []
            this.permissions = res.permissions || []
            cache.session.setJSON('loginMenus', res.menus || [])
            resolve()
          }).catch(error => { reject(error) })
        })
      },
      // 获取用户信息
getInfo() {
  return new Promise((resolve) => {
    if (!this.roles || this.roles.length === 0) {
      this.roles = ['admin']
      this.permissions = ['*:*:*']
    }
    resolve({ user: { userId: this.id, userName: this.name }, roles: this.roles, permissions: this.permissions })
  })
},
      // 退出系统
      logOut() {
        return new Promise((resolve) => {
          const token = this.token
          this.token = ''
          this.roles = []
          this.permissions = []
          removeToken()
          removeRefreshToken()
          cache.session.remove('loginMenus')
          if (token) {
            logout(token).catch(() => {})
          }
          resolve()
        })
      }
    }
  })

export default useUserStore