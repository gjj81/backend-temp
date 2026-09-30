import request from '@/utils/request'

export function selectWorkshop() {
  return request({
    url: '/api/base/workshop/list',
    method: 'get',
  })
}

export function selectProdLine() {
  return request({
    url: '/api/base/prodLine/list',
    method: 'get',
  })
}