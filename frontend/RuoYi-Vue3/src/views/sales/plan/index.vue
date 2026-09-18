<!--
  ╔══════════════════════════════════════════════════════════════╗
  ║  第一层：页面容器 (Page Container)                            ║
  ║  职责：                                                      ║
  ║    1. 数据获取：onMounted 调用 listPlan API                  ║
  ║    2. 数据处理：接收第二层 emit 的 save/delete 事件          ║
  ║    3. API 调用：savePlan / deletePlan / refresh              ║
  ║    4. 全局状态：planList, yearMonth, activeView              ║
  ║    5. 统一提示：ElMessage 成功/错误提示                     ║
  ╚══════════════════════════════════════════════════════════════╝
-->
<template>
  <div class="plan-page">
    <!-- 固定头部 -->
    <header class="plan-header">
      <!-- 左侧：标题 + 月份筛选 -->
      <div class="header-left">
        <div class="title-row">
          <span class="main-title">销售计划管理</span>
        </div>
        <div class="filter-bar">
          <el-date-picker 
            v-model="yearMonth" 
            type="month" 
            value-format="YYYY-MM" 
            placeholder="选择月份"
            @change="getPlanList"
          />
        </div>
      </div>

      <!-- 中间：Tab 切换 -->
      <div class="header-center">
        <div class="tab-group">
          <div 
            v-for="tab in tabs" 
            :key="tab.key"
            :class="['tab-item', { active: activeView === tab.key }]"
            @click="activeView = tab.key"
          >
            {{ tab.label }}
          </div>
        </div>
      </div>

      <!-- 右侧：导出按钮 -->
      <div class="header-right">
        <el-button size="small" @click="handleExport">导出 excel</el-button>
      </div>
    </header>

    <!-- 内容区：动态组件 -->
    <div class="plan-body">
      <div class="view-container">
        <component 
          :is="activeComponent" 
          :plan-list="planList"
          :year-month="yearMonth"
          @save="handleSave"
          @delete="handleDelete"
        />
      </div>
    </div>
  </div>
</template>

<script setup name="SalesPlan">
/**
 * 第一层：页面容器 / 数据层
 * 
 * 向上对接：路由 (router)
 * 向下对接：PlanEdit (协调器) / PlanCalendar (视图)
 * 
 * 核心原则：
 *   - 所有 API 调用集中在此层
 *   - 所有全局数据状态在此层管理
 *   - 统一处理成功/失败提示
 *   - 不处理 UI 细节，不管理弹窗
 * 
 * 接收的数据结构（来自第二层）：统一为 { type, data }
 * 
 * save 事件 type 枚举：
 *   - 'plan:add'   → data: { customer, planMonth, planNo, lines }
 *   - 'plan:edit'  → data: { planId, customer, planMonth, planNo }
 *   - 'line:edit'  → data: 产品完整对象
 *   - 'line:add'   → data: { newLine, planId }
 *   - 'delivery'   → data: 交货节点对象
 * 
 * delete 事件 type 枚举：
 *   - 'plan'     → data: { planId, customer }
 *   - 'line'     → data: { lineId, planId }
 *   - 'delivery' → data: 交货节点对象
 */
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  listPlan,
  addPlan,
  updatePlan,
  addPlanLine,
  updatePlanLine,
  addPlanDelivery,
  updatePlanDelivery,
  updatePlanDeliveryList,
  deletePlan,
  deletePlanLine,
  deletePlanDelivery
} from '@/api/sales-plan'
import PlanEdit from './PlanEdit/index.vue'
import PlanCalendar from './PlanCalendar.vue'
import { 
  SalesPlanUpdateDTO, 
  SalesPlanLineUpdateDTO,
  SalesPlanDeliveryUpdateDTO,
  SalesPlanDeliveryVO
} from '@/types/sales-plan'

// ==================== 全局状态 ====================
const planList = ref([])
const yearMonth = ref('2026-05')
const activeView = ref('edit')

const activeComponent = computed(() => 
  activeView.value === 'calendar' ? PlanCalendar : PlanEdit
)

const tabs = [
  { key: 'edit', label: '计划录入' },
  { key: 'calendar', label: '计划日历' }
]

// ==================== 数据获取 ====================

/**
 * 获取计划列表（核心数据刷新）
 * @param {string} month - 格式：YYYY-MM
 */
async function getPlanList(month = yearMonth.value) {
  try {
    planList.value = await listPlan(month)
  } catch (error) {
    planList.value = []
    ElMessage.error(error.message || '获取计划列表失败')
  }
}

// ==================== 事件处理（对接第二层）====================

/**
 * 处理保存事件（来自 PlanEdit 协调器）
 * @param {Object} payload —— 统一格式：{ type, data }
 * 
 * type 枚举：
 *   - 'plan:add'   → data: { customer, planMonth, planNo, lines }
 *   - 'plan:edit'  → data: { planId, customer, planMonth, planNo }
 *   - 'line:edit'  → data: 产品完整对象
 *   - 'line:add'   → data: { newLine, planId }
 *   - 'delivery'   → data: 交货节点对象
 */
async function handleSave(payload) {// 统一处理修改
  console.log('收到 save 事件:', payload)
  const { type, data, optimistic, originalData, targetInfo } = payload
  try {
    let result
    switch (type) {
      case 'delivery:update':
        console.log('处理交货节点更新:', data.value)
        result = await updatePlanDelivery(data)
        if (optimistic && originalData === false) {
          Object.assign(originalData, {
            deliveryDate: data.deliveryDate,
            planQuantity: data.planQuantity,
            nodeName: data.nodeName
          }) 
        }
        break
      case 'delivery:add':
        console.log('处理交货节点新增:', data.value)
        result = await addPlanDelivery(data)
        const newDeliveryId = result?.data || result?.msg || data.deliveryId
        if (targetInfo?.targetArray && optimistic === false) {// 仅在非乐观模式下处理新增
          // 创建新的交货节点对象
          const newDelivery = new SalesPlanDeliveryVO({
            deliveryId: newDeliveryId,
            deliveryDate: data.deliveryDate,
            planQuantity: data.planQuantity,
            nodeName: data.nodeName || data.deliveryDate,
            status: 0,
            version: 1
          })
          // 添加到目标数组
          targetInfo.targetArray.push(newDelivery)
        }
        break
      case 'plan:add':
        console.log('处理新增计划:', data)
        result = await addPlan(data)
        break
      case 'plan:edit':
        console.log('处理编辑计划:', data)
        result = await updatePlan(data)
        break
      case 'line:add':
        console.log('处理新增产品行:', data)
        result = await addPlanLine(data)
        break
      case 'line:edit':
        console.log('处理编辑产品行:', data)
        result = await updatePlanLine(data)
        break
      case 'delivery:batch-move':
        console.log('处理批量移动交货节点:', data)
        result = await updatePlanDeliveryList(data)
        break
      default:
        throw new Error(`未知的保存类型: ${type}`)
    }

    ElMessage.success('保存成功')
    await getPlanList() // 刷新列表
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  }
}

/**
 * 处理删除事件（来自 PlanEdit 协调器）
 * @param {Object} payload —— 统一格式：{ type, data }
 * 
 * type 枚举：
 *   - 'plan'     → data: { planId, customer }
 *   - 'line'     → data: { lineId, planId }
 *   - 'delivery' → data: 交货节点对象
 */
async function handleDelete(payload) {
  const { type, data, optimistic, originalData, targetInfo } = payload

  try {
    // TODO: 根据 type 调用不同的删除 API
    switch (type) {
      case 'delivery:delete':
        console.log('确认删除交货节点:', data.deliveryId, '版本号:', data.version)
        await deletePlanDelivery(data.deliveryId, data.version)
        if (targetInfo?.targetArray && optimistic === false) {
          // 从目标数组中移除
          const index = targetInfo.targetArray.findIndex(d => d.deliveryId === data.deliveryId)
          if (index !== -1) { // 仅在找到的情况下移除
            targetInfo.targetArray.splice(index, 1)
          }
        }
        break
      case 'plan':
        console.log('确认删除计划:', data.planId, '版本号:', data.version)
        await deletePlan(data.planId, data.version)
        break
      case 'line':
        await deletePlanLine(data.lineId, data.version)
        break
      default:
        throw new Error(`未知的删除类型: ${type}`)
    }


    ElMessage.success('删除成功')
    await getPlanList() // 刷新列表
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error(error.message || '删除失败')
  }
}

// ==================== 其他操作 ====================

function handleExport() {
  // TODO: 实现导出 Excel
  ElMessage.info('导出功能开发中...')
}


// ==================== 生命周期 ====================
onMounted(() => {
  getPlanList(yearMonth.value)
})
</script>

<style scoped>
.plan-page {
  display: flex;
  flex-direction: column;
  height: 100%; 
  overflow: hidden;
}
.plan-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  z-index: 100;
  margin-bottom: 12px;
}
.header-left .title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.main-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}
.tab-group {
  display: flex;
  background: #f3f4f6;
  border-radius: 8px;
  padding: 4px;
}
.tab-item {
  padding: 8px 20px;
  font-size: 14px;
  color: #6b7280;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}
.tab-item:hover {
  color: #374151;
}
.tab-item.active {
  background: #1f2937;
  color: #fff;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-bar {
  padding: 8px 0;
}
.plan-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}
.view-container {
  flex: 1;
  overflow: hidden;
}
</style>
