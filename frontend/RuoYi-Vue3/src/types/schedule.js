function pad(n) { return n < 10 ? '0' + n : '' + n }

function formatDate(d) {
  if (d == null || d === '') return d
  if (typeof d === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(d)) return d
  const dt = d instanceof Date ? d : new Date(d)
  if (isNaN(dt.getTime())) return ''
  return dt.getFullYear() + '-' + pad(dt.getMonth() + 1) + '-' + pad(dt.getDate())
}

export class ScheduleDeliveryVO {
  constructor({ deliveryDate, planQuantity } = {}) {
    this.deliveryDate = formatDate(deliveryDate)
    this.planQuantity = planQuantity || 0
  }
}

export class ProdScheduleVO {
  constructor({ scheduleId, lineId, prodLineId, scheduleDate, quantity, workshopId, workshopName, productId, actualQuantity, qualifiedQuantity, defectQuantity, status, remark, version } = {}) {
    this.scheduleId = scheduleId || ''
    this.lineId = lineId || ''
    this.prodLineId = prodLineId || ''
    this.scheduleDate = scheduleDate || null
    this.quantity = quantity || 0
    this.workshopId = workshopId || ''
    this.workshopName = workshopName || ''
    this.productId = productId || ''
    this.actualQuantity = actualQuantity || 0
    this.qualifiedQuantity = qualifiedQuantity || 0
    this.defectQuantity = defectQuantity || 0
    this.status = status ?? 0
    this.remark = remark || ''
    this.version = version ?? 1
  }
}

export class ScheduleProductVO {
  constructor({ lineId, planNo, customer, productId, productName, scheduledQuantity,totalQuantity, planMonth, forecastQuantity, openingInventory, lineStatus, version, deliveryNodes } = {}) {
    this.lineId = lineId || ''
    this.planNo = planNo || ''
    this.customer = customer || ''
    this.productId = productId || ''
    this.productName = productName || ''
    this.totalQuantity = totalQuantity || 0
    this.planMonth = planMonth || ''
    this.forecastQuantity = forecastQuantity || 0
    this.openingInventory = openingInventory || 0
    this.lineStatus = lineStatus ?? 0
    this.version = version ?? 1
    this.scheduledQuantity = scheduledQuantity || 0
    this.deliveryNodes = Array.isArray(deliveryNodes)
      ? deliveryNodes.map(d => new ScheduleDeliveryVO(d))
      : []
  }
}

export function normalizeProductList(arr) {
  return Array.isArray(arr)
    ? arr.map(p => new ScheduleProductVO(p))
    : []
}

export class ProdScheduleSaveDTO {
  constructor({
    scheduleId,
    lineId,
    prodLineId,
    scheduleDate,
    quantity,
    workshopId,
    workshopName,
    productId,
    remark,
    status,
    version
  } = {}) {
    this.scheduleId = scheduleId
    this.lineId = lineId || ''
    this.prodLineId = prodLineId || ''
    this.scheduleDate = scheduleDate || ''
    this.quantity = quantity ?? 0
    this.workshopId = workshopId || ''
    this.workshopName = workshopName || ''
    this.productId = productId || ''
    this.remark = remark || ''
    this.status = status ?? 0
    this.version = version ?? 1
  }
}