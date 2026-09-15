export class SalesPlanDeliveryVO {
  constructor({ deliveryId, nodeName, deliveryDate, planQuantity, actualQuantity, status, version }) {
    this.deliveryId = deliveryId
    this.nodeName = nodeName
    this.deliveryDate = deliveryDate
    this.planQuantity = planQuantity
    this.actualQuantity = actualQuantity
    this.status = status
    this.version = version
  }
}

export class SalesPlanLineVO {
  constructor({ lineId, productId, productName, totalQuantity, forecastQuantity, openingInventory, scheduledQuantity, finishedQuantity, deliveredQuantity, status, version, deliveryList }) {
    this.lineId = lineId
    this.productId = productId
    this.productName = productName
    this.totalQuantity = totalQuantity
    this.forecastQuantity = forecastQuantity
    this.openingInventory = openingInventory
    this.scheduledQuantity = scheduledQuantity
    this.finishedQuantity = finishedQuantity
    this.deliveredQuantity = deliveredQuantity
    this.status = status
    this.version = version
    this.deliveryList = deliveryList
  }
}

export class SalesPlanVO {
  constructor({ planId, planNo, customer, yearMonth, planType, status, remark, version, lineList }) {
    this.planId = planId
    this.planNo = planNo
    this.customer = customer
    this.yearMonth = yearMonth
    this.planType = planType
    this.status = status
    this.remark = remark
    this.version = version
    this.lineList = lineList
  }
}


// ==================== DTO====================

export class SalesPlanDeliveryUpdateDTO {
  constructor({ deliveryId, lineId, nodeName, deliveryDate, planQuantity, actualQuantity, status, version }) {
    this.deliveryId = deliveryId
    this.lineId = lineId
    this.nodeName = nodeName
    this.deliveryDate = deliveryDate
    this.planQuantity = planQuantity
    this.actualQuantity = actualQuantity
    this.status = status
    this.version = version
  }
}

export class SalesPlanLineUpdateDTO {
  constructor({ lineId, planId, productId, productName, totalQuantity, forecastQuantity, openingInventory, scheduledQuantity, finishedQuantity, deliveredQuantity, status, version, deliveries }) {
    this.lineId = lineId
    this.planId = planId
    this.productId = productId
    this.productName = productName
    this.totalQuantity = totalQuantity
    this.forecastQuantity = forecastQuantity
    this.openingInventory = openingInventory
    this.scheduledQuantity = scheduledQuantity
    this.finishedQuantity = finishedQuantity
    this.deliveredQuantity = deliveredQuantity
    this.status = status
    this.version = version
    this.deliveries = deliveries
  }
}

export class SalesPlanUpdateDTO {
  constructor({ planId, version, planNo, customer, planMonth, planType, status, remark, lines }) {
    this.planId = planId
    this.version = version
    this.planNo = planNo
    this.customer = customer
    this.planMonth = planMonth
    this.planType = planType
    this.status = status
    this.remark = remark
    this.lines = lines
  }
}

// ==================== 插入结束（工具函数之前）====================


function pad(n) { return n < 10 ? '0' + n : '' + n }

function formatMonth(d) {
  if (d == null || d === '') return ''
  if (typeof d === 'string' && /^\d{4}-\d{2}$/.test(d)) return d
  const dt = d instanceof Date ? d : new Date(d)
  if (isNaN(dt.getTime())) return ''
  return dt.getFullYear() + '-' + pad(dt.getMonth() + 1)
}

function formatDate(d) {
  if (d == null || d === '') return ''
  if (typeof d === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(d)) return d
  const dt = d instanceof Date ? d : new Date(d)
  if (isNaN(dt.getTime())) return ''
  return dt.getFullYear() + '-' + pad(dt.getMonth() + 1) + '-' + pad(dt.getDate())
}

function normalizeDelivery(d) {
  if (!d) return null
  return { ...d, deliveryDate: formatDate(d.deliveryDate) }
}

function normalizeLine(l) {
  if (!l) return null
  return {
    ...l,
    deliveryList: Array.isArray(l.deliveries) ? l.deliveries.map(normalizeDelivery) : []
  }
}

export function normalizePlan(p) {
  if (!p) return {}
  return {
    ...p,
    yearMonth: formatMonth(p.planMonth),
    lineList: Array.isArray(p.lines) ? p.lines.map(normalizeLine) : []
  }
}

export function normalizePlanList(arr) {
  return Array.isArray(arr) ? arr.map(normalizePlan) : []
}