export function formatNum(n) {
  if (n == null || n === '') return '-'
  return Number(n).toLocaleString()
}

export function getSpanStatusText(status) {
  const texts = {
    0: '草稿',
    1: '未下发',
    2: '生产中',
    3: '已完工'
  }
  return texts[status] || '未知状态'
}

export function getSpanStatusType(status) {
  const types = {
    0: 'info',
    1: '',
    2: 'warning',
    3: 'success'
  }
  return types[status] || 'info'
}

export function getSpanStatusColor(status) {
  const colors = {
    0: '#909399',
    1: '#409eff',
    2: '#e6a23c',
    3: '#67c23a'
  }
  return colors[status] || '#909399'
}

export function getSpanStatusBg(status) {
  const bgs = {
    0: '#f4f4f5',
    1: '#ecf5ff',
    2: '#fdf6ec',
    2: '#f0f9eb'
  }
  return bgs[status] || '#f4f4f5'
}