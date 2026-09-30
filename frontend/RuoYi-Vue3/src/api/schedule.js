import request from '@/utils/request'

export function listWorkbenchProducts() {
  return request({
    url: '/api/prod-schedule/workbench/list',
    method: 'get'
  })
}

export function listProdProducts(startDate, endDate) {
  return request({
    url: '/api/prod-schedule/prod/list',
    method: 'get',
    params: { startDate, endDate }
  })
}

export function saveSchedules(data) {
  return request({
    url: '/api/prod-schedule/prod/add',
    method: 'post',
    data
  })
}

export function issueSchedules(data) {
  return request({
    url: '/api/prod-schedule/prod/update',
    method: 'put',
    data
  })
}

export function deleteSchedule(id, version) {
  return request({
    url: '/api/prod-schedule/prod/delete',
    method: 'delete',
    params: { id, version }
  })
}