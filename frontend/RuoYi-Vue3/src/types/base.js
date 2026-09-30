export class WorkshopVO {
  constructor({ workshopId, workshopName, workshopCode } = {}) {
    this.workshopId = workshopId || ''
    this.workshopName = workshopName || ''
    this.workshopCode = workshopCode || ''
  }
}

export class ProdLine {
  constructor({ prodLineId, workshopId, lineCode,lineName } = {}) {
    this.prodLineId = prodLineId || ''
    this.workshopId = workshopId || ''
    this.lineCode = lineCode || ''
    this.lineName = lineName || ''
  }
}
