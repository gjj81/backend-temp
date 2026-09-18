<!--
  ╔══════════════════════════════════════════════════════════════╗
  ║  第二层：协调器 (Coordinator)                                 ║
  ║  职责：                                                      ║
  ║    1. 接收第一层传入的 props: planList, yearMonth            ║
  ║    2. 协调第三层子组件：PlanForm, PlanList, Dialogs          ║
  ║    3. 接收第三层事件 → 数据预处理 → 向上 emit 到第一层      ║
  ║    4. 管理局部 UI 状态：弹窗显隐、当前选中对象              ║
  ║    5. 左右面板数据同步（右侧操作后同步左侧表单）            ║
  ╚══════════════════════════════════════════════════════════════╝
-->
<template>
  <div class="plan-edit-page">
    <!-- 左侧：表单组件 -->
    <PlanForm 
      ref="planFormRef" 
      @add-plan="handleAddPlan" 
      @save-plan="handleSavePlan" 
    />

    <!-- 右侧：列表组件 -->
    <PlanList
      :plan-list="planList"
      @load-plan="handleLoadPlan"
      @delete-plan="handleDeletePlan"
      @delete-product="handleDeleteProduct"
      @open-edit-product="handleOpenEditProduct"
      @open-add-product="handleOpenAddProduct"
      @date-click="handleDateClick"
      @add-delivery="handleAddDelivery"
    />

    <!-- 弹窗：交货节点详情 -->
    <DeliveryDialog
      v-model="deliveryDialogVisible"
      :delivery-data="currentDelivery"
      :line-data="currentLineForDelivery"
      :plan-data="currentPlanForDelivery"
      @save="handleDeliverySaved"
      @delete="handleDeliveryDeleted"
    />

    <!-- 弹窗：产品管理（新增/编辑） -->
    <ProductDialog
      v-model="productDialogVisible"
      :product-data="currentProductForDialog"
      :plan-data="currentPlanForProduct"
      :line-index="currentLineIdxForEdit"
      @save="handleProductSaved"
    />
  </div>
</template>

<script setup name="PlanEdit">
/**
 * 第二层：协调器
 * 
 * 向上对接：SalesPlan (第一层) —— 统一 emit('save', { type, data })
 * 向下对接：PlanForm, PlanList, DeliveryDialog, ProductDialog (第三层)
 * 
 * 核心原则：
 *   - 不直接调用 API（API 在第一层）
 *   - 不持有全局数据（planList 来自 props）
 *   - 负责数据预处理和事件转发（统一封装 type）
 *   - 管理局部 UI 状态（弹窗、当前选中）
 * 
 * ⚠️ 向上 emit 必须统一为：{ type: string, data: any }
 *    第一层通过 const { type, data } = payload 解构
 *    所有 emit 必须显式硬编码 type，禁止透传！
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { SalesPlanVO, SalesPlanLineVO, SalesPlanDeliveryVO,SalesPlanDeliveryUpdateDTO,SalesPlanLineUpdateDTO,SalesPlanUpdateDTO} from '@/types/sales-plan'
import PlanForm from './PlanForm.vue'
import PlanList from './PlanList.vue'
import DeliveryDialog from './DeliveryDialog.vue'
import ProductDialog from './ProductDialog.vue'

// ==================== Props / Emit ====================

const props = defineProps({
  /** 计划列表（来自第一层） */
  planList: { type: Array, default: () => [] },
  /** 当前月份（来自第一层） */
  yearMonth: { type: String, default: '' },
  /** 关闭弹窗信号（来自第一层） */
  closeDialogSignal: { 
    type: Object, 
    default: () => ({ type: '', timestamp: 0 }) 
  }
})

const emit = defineEmits(['save', 'delete','close-dialog'])

// ==================== Refs ====================

const planFormRef = ref(null)

// ==================== 弹窗状态管理 ====================

const deliveryDialogVisible = ref(false)
const currentDelivery = ref(null)
const currentLineForDelivery = ref(null)
const currentPlanForDelivery = ref(null)

const productDialogVisible = ref(false)
const currentPlanForProduct = ref(null)
const currentLineIdxForEdit = ref(null)
const currentProductForDialog = ref(null)

// ==================== 左侧表单事件（来自 PlanForm）====================

/** 新增计划 —— 显式封装 { type, data } */
function handleAddPlan(submitData) {
  emit('save', { type: 'plan:add', data: submitData })
}

/** 保存计划基本信息 —— 显式封装 { type, data } */
function handleSavePlan(submitData) {
  emit('save', { type: 'plan:edit', data: submitData })
}

// ==================== 右侧列表事件（来自 PlanList）====================

/** 加载计划到左侧表单 */
function handleLoadPlan(plan) {
  planFormRef.value?.loadPlan(plan)
}

/** 删除计划 —— 显式封装 { type, data } */
function handleDeletePlan(plan) {
  emit('delete', { 
    type: 'plan', 
    data: { planId: plan.planId, version: plan.version } 
  })
}

/** 删除产品 —— 先同步左侧表单，再显式封装 { type, data } */
function handleDeleteProduct({ plan, line }) {
  if (planFormRef.value?.getEditingId() === plan.planId) {
    planFormRef.value?.removeLineById(line.lineId)
  }
  emit('delete', { 
    type: 'line', 
    data: { lineId: line.lineId, version: line.version || 0 } 
  })
}

/** 打开产品编辑弹窗 */
function handleOpenEditProduct({ plan, line, lineIdx }) {
  currentPlanForProduct.value = plan
  currentLineIdxForEdit.value = lineIdx
  // 将 plan.planId 合并到 line 对象中
  currentProductForDialog.value = { ...line, planId: plan.planId }
  productDialogVisible.value = true
}

/** 打开产品新增弹窗 */
function handleOpenAddProduct(plan) {
  currentPlanForProduct.value = plan
  currentLineIdxForEdit.value = null
  currentProductForDialog.value = null
  productDialogVisible.value = true
}

/** 点击交货标签 —— 打开交货弹窗 */
function handleDateClick({ delivery, line, plan }) {
  currentDelivery.value = delivery
  currentLineForDelivery.value = line
  currentPlanForDelivery.value = plan
  deliveryDialogVisible.value = true
}

// ==================== 新增：打开新增交货节点弹窗 ====================

/** 打开新增交货节点弹窗 */
function handleAddDelivery({ line, plan }) {
  currentDelivery.value = null           // ← 关键：设为 null 表示新增模式
  currentLineForDelivery.value = line
  currentPlanForDelivery.value = plan
  deliveryDialogVisible.value = true
}

// ==================== 弹窗事件（来自 Dialogs）====================

/** 交货节点保存 —— 显式封装 { type, data }（第三层只发纯数据） */
function handleDeliverySaved(eventData) {
  emit('save', {
    type: eventData.isEdit ? 'delivery:update' : 'delivery:add',
    data: eventData.data,
    optimistic: false,
    originalData: eventData.originalData,
    targetInfo: !eventData.isEdit 
      ? { targetArray: eventData.lineData.deliveryList }
      : undefined
  })
}

/** 交货节点删除 —— 显式封装 { type, data }（第三层只发纯数据） */
function handleDeliveryDeleted(eventData) {
  emit('delete', {
    type: 'delivery:delete',
    data: eventData.data,
    optimistic: false,
    targetInfo: {
      targetArray: eventData.lineData.deliveryList,
      targetId: eventData.data.deliveryId
    }
  })
}

/** 产品保存（新增/编辑）—— 更新本地数据 + 同步左侧 + 显式封装 { type, data } */
function handleProductSaved(eventData) {
  const plan = eventData.planData || currentPlanForProduct.value
  if (!plan) return

  const result = eventData.data

  if (eventData.isEdit) {
    // 编辑模式
    if (eventData.originalLineIndex !== null) {
      Object.assign(plan.lineList[eventData.originalLineIndex], {
        productName: result.productName,
        totalQuantity: result.totalQuantity,
        forecastQuantity: result.forecastQuantity,
        openingInventory: result.openingInventory,
        deliveryList: result.deliveries || []
      })
    }
    ElMessage.success('产品修改成功')
    emit('save', { type: 'line:edit', data: result })
  } else {
    // 新增模式
    const newLine = {
      lineId: null,
      productName: result.productName,
      totalQuantity: result.totalQuantity,
      forecastQuantity: result.forecastQuantity,
      openingInventory: result.openingInventory,
      status: 0,
      deliveryList: result.deliveries || []
    }
    plan.lineList.push(newLine)
    ElMessage.success('产品新增成功')
    emit('save', { type: 'line:add', data: result })
  }

  if (planFormRef.value?.getEditingId() === plan.planId) {
    planFormRef.value?.syncLineList(plan.lineList)
  }
}
</script>

<style scoped>
.plan-edit-page {
  display: flex;
  gap: 16px;
  height: 100%;
  overflow: hidden;
}
</style>
