import request from '@/utils/request'
import { normalizePlanList } from '@/types/sales-plan'
import { 
  SalesPlanUpdateDTO, 
  SalesPlanLineUpdateDTO, 
  SalesPlanDeliveryUpdateDTO 
} from '@/types/sales-plan'

export function listPlan(yearMonth) {
  return request({
    url: '/api/sales-plan/monthly',
    method: 'get',
    params: { yearMonth }
  }).then(normalizePlanList)
}

export function deletePlan(planId, version) {
  return request({
    url: `/api/sales-plan/delete/plan/${planId}/${version}`,
    method: 'get'
  })
}

export function deletePlanLine(planLineId, version) {
  return request({
    url: `/api/sales-plan/delete/line/${planLineId}/${version}`,
    method: 'get'
  })
}

export function deletePlanDelivery(deliveryId, version) {
  return request({
    url: `/api/sales-plan/delete/delivery/${deliveryId}/${version}`,
    method: 'get'
  })
}

export function addPlanDelivery(data) {
  return request({
    url: '/api/sales-plan/add/delivery',
    method: 'post',
    data
  })
}

export function addPlanLine(data) {
  return request({
    url: '/api/sales-plan/add/line',
    method: 'post',
    data
  })
}

export function addPlan(data) {
  return request({
    url: '/api/sales-plan/add/plan',
    method: 'post',
    data
  })
}

export function updatePlan(data) {
  return request({
    url: '/api/sales-plan/update/plan',
    method: 'post',
    data
  })
}

export function updatePlanLine(data) {
  return request({
    url: '/api/sales-plan/update/line',
    method: 'post',
    data
  })
}

export function updatePlanDelivery(data) {
  return request({
    url: '/api/sales-plan/update/delivery',
    method: 'post',
    data
  })
}

export function updatePlanDeliveryList(data) {
  return request({
    url: '/api/sales-plan/update/list/delivery',
    method: 'post',
    data
  })
}