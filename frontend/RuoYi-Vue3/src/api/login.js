import request from '@/utils/request'

export function login(username, password) {
  return request({
    url: '/api/auth/login',
    headers: { isToken: false, repeatSubmit: false },
    method: 'post',
    data: { username, password }
  })
}

export function refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh',
    headers: { isToken: false, 'X-Refresh-Token': refreshToken },
    method: 'post'
  })
}

export function logout(token) {
  return request({
    url: '/api/auth/logout',
    headers: {
      Authorization: token ? `Bearer ${token}` : '',
      isToken: false
    },
    method: 'post'
  })
}