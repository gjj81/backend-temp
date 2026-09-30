export function formatNum(n) {
  if (n == null || n === '') return '-'
  return Number(n).toLocaleString()
}

// ==================== LINE 产品行状态 ====================
// 0=LINE_DRAFT(草稿/未下发) 1=LINE_ISSUED(已下发未排产) 2=LINE_SCHEDULED(已排产) 3=LINE_PRODUCING(生产中) 4=LINE_DONE(已完成)
export function getLineStatusText(status) {
  const texts = { 0: '草稿', 1: '已下发', 2: '已排产', 3: '生产中', 4: '已完成' }
  return texts[status] || '未知'
}
export function getLineStatusType(status) {
  const types = { 0: 'info', 1: '', 2: 'warning', 3: 'danger', 4: 'success' }
  return types[status] || 'info'
}

// ==================== SPAN 排产块状态 ====================
// 0=待确认 1=已确认 2=生产中 3=已完工（对齐 prod_schedule.status）
// _isNew 标识界面草稿（未保存到数据库），status 与已保存的待确认同为 0
export function getSpanStatusText(status) {
  const texts = { 0: '待确认', 1: '已确认', 2: '生产中', 3: '已完工' }
  return texts[status] ?? '未知状态'
}
export function getSpanDisplayStatus(span) {
  if (span?._isNew) return { text: '未保存', type: 'info', color: '#c0c4cc', bg: '#fafafa' }
  return {
    text: getSpanStatusText(span?.status),
    type: getSpanStatusType(span?.status),
    color: getSpanStatusColor(span?.status),
    bg: getSpanStatusBg(span?.status)
  }
}
export function getSpanStatusType(status) {
  const types = { 0: 'info', 1: '', 2: 'warning', 3: 'success' }
  return types[status] || 'info'
}
export function getSpanStatusColor(status) {
  const colors = { 0: '#909399', 1: '#409eff', 2: '#e6a23c', 3: '#67c23a' }
  return colors[status] || '#909399'
}
export function getSpanStatusBg(status) {
  const bgs = { 0: '#f4f4f5', 1: '#ecf5ff', 2: '#fdf6ec', 3: '#f0f9eb' }
  return bgs[status] || '#f4f4f5'
}