import request from '@/utils/request'

export function listBoard(params) {
  return request({
    url: '/api/schedule/board',
    method: 'get',
    params
  })
}

export function addSpan(data) {
  return request({
    url: '/api/schedule/span',
    method: 'post',
    data
  })
}

export function updateSpan(data) {
  return request({
    url: '/api/schedule/span',
    method: 'put',
    data
  })
}

export function deleteSpan(spanId, version) {
  return request({
    url: `/api/schedule/span/${spanId}`,
    method: 'delete',
    params: { version }
  })
}

export function batchIssueSpans(spanIds) {
  return request({
    url: '/api/schedule/batch-issue',
    method: 'post',
    data: spanIds
  })
}

export function moveSpan(data) {
  return request({
    url: '/api/schedule/span/move',
    method: 'put',
    data
  })
}