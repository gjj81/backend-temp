// utils.js - 共享工具函数
export function formatNum(n) {
  if (n == null || n === '') return '-'
  return Number(n).toLocaleString()
}

export function formatDateTag(d) {
  let dateStr = ''

  if (d.deliveryDate) {
    const datePart = d.deliveryDate.slice(0, 10)
    const parts = datePart.split('-')

    if (parts.length === 3) {
      dateStr = `${parts[1]}月${parts[2]}号`
    } else {
      dateStr = d.nodeName || '未定'
    }
  } else {
    dateStr = d.nodeName || '未定'
  }

  const text = `${dateStr} ${d.planQuantity}`

  let bgColor = '#eff6ff', textColor = '#2563eb'

  if (d.status === null || d.status === undefined) {
    bgColor = '#f3f4f6'; textColor = '#9ca3af'
  } else if (d.status === 0) {
    bgColor = '#dbeafe'; textColor = '#1e40af'
  } else if (d.status === 1) {
    bgColor = '#e0e7ff'; textColor = '#3730a3'
  } else {
    bgColor = '#f3f4f6'; textColor = '#9ca3af'
  }

  return { 
    text, 
    style: `background: ${bgColor}; color: ${textColor};`
  }
}

export function getDeliveryStatusType(status) {
  const types = {
    0: '',
    1: 'info'
  }
  return types[status] || 'info'
}

export function getDeliveryStatusText(status) {
  const texts = {
    0: '未下发',
    1: '已下发'
  }
  return texts[status] || '未知状态'
}