c:\Users\g2026\Desktop\mes\backend-temp\frontend\RuoYi-Vue3\src\views\sales\plan\PlanCalendar.vue
<template>
  <div class="plan-calendar">
    <!-- 顶部工具栏 - 固定位置 -->
    <div class="toolbar">
      <div class="toolbar-actions">
        <el-badge :value="pendingChanges.length" :hidden="pendingChanges.length === 0">
          <el-button @click="handleReset" :disabled="pendingChanges.length === 0">重置</el-button>
        </el-badge>
        <el-button type="primary" @click="handleSave" :disabled="pendingChanges.length === 0">
          保存 ({{ pendingChanges.length }})
        </el-button>
      </div>
    </div>

    <!-- 主体区域 -->
    <div class="main-area">
      <!-- 左侧：日历 -->
      <div class="calendar-wrapper">
        <FullCalendar ref="calendarRef" :options="calendarOptions" />
      </div>

      <!-- 右侧：详情面板 -->
      <div class="side-panel">
        <template v-if="selectedDate">
          <div class="panel-header">
            <h3>{{ selectedDate }} 交货详情</h3>
            <el-button link @click="selectedDate = null">关闭</el-button>
          </div>
          <div
            v-for="item in selectedDeliveries"
            :key="item.deliveryId"
            class="delivery-card"
          >
            <div class="card-top">
              <span class="card-plan">{{ item.planNo }}</span>
              <el-tag :type="item.status === 0 ? 'success' : 'info'" size="small">
                {{ item.status === 0 ? '未下发' : '已下发' }}
              </el-tag>
            </div>
            <div class="card-product">{{ item.productName }}</div>
            <div class="card-info">
              <span>数量：{{ item.planQuantity?.toLocaleString() }} pcs</span>
              <span>节点：{{ item.nodeName }}</span>
            </div>
          </div>
          <el-empty v-if="selectedDeliveries.length === 0" description="该日期无交货节点" />
        </template>
        <div v-else class="panel-placeholder">点击日历格子查看详情</div>
      </div>
    </div>
  </div>
</template>

<script setup name="PlanCalendar">
import { ref, watch, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import zhLocale from '@fullcalendar/core/locales/zh-cn'

const props = defineProps({
  planList: { type: Array, default: () => [] },
  yearMonth: { type: String, default: '' }
})

const emit = defineEmits(['save'])

const calendarRef = ref(null)
const calendarData = ref({})

// ==================== 右侧面板状态 ====================

const selectedDate = ref(null)
const selectedDeliveries = ref([])

// ==================== 拖拽变更暂存 ====================

const pendingChanges = ref([])

// ==================== 数据处理 ====================

function buildCalendarEvents() {
  const events = []
  const dataMap = {}

  props.planList.forEach(plan => {
    const lineList = plan.lineList || plan.lines || []

    lineList.forEach(line => {
      const deliveryList = line.deliveryList || line.deliveries || []

      deliveryList.forEach(delivery => {
        if (!delivery.deliveryDate) return

        const dateStr = delivery.deliveryDate.split(' ')[0]
        // 检查是否有暂存变更，有则用新日期
        const change = pendingChanges.value.find(c => c.deliveryId === delivery.deliveryId)
        const displayDate = change ? change.newDate : dateStr

        events.push({
          id: delivery.deliveryId,
          title: `${line.productName}\n${delivery.planQuantity?.toLocaleString()}`,
          start: displayDate,
          allDay: true,
          editable: delivery.status === 0,
          backgroundColor: delivery.status === 0 ? '#409eff' : '#909399',
          borderColor: delivery.status === 0 ? '#409eff' : '#909399',
          extendedProps: {
            planId: plan.planId,
            planNo: plan.planNo,
            customer: plan.customer,
            lineId: line.lineId,
            productName: line.productName,
            totalQuantity: line.totalQuantity,
            deliveryId: delivery.deliveryId,
            nodeName: delivery.nodeName,
            deliveryDate: dateStr,
            planQuantity: delivery.planQuantity,
            status: delivery.status,
            version: delivery.version
          }
        })

        if (!dataMap[displayDate]) {
          dataMap[displayDate] = []
        }
        dataMap[displayDate].push({
          planId: plan.planId,
          planNo: plan.planNo,
          customer: plan.customer,
          lineId: line.lineId,
          productName: line.productName,
          totalQuantity: line.totalQuantity,
          deliveryId: delivery.deliveryId,
          nodeName: delivery.nodeName,
          deliveryDate: dateStr,
          planQuantity: delivery.planQuantity,
          status: delivery.status,
          version: delivery.version
        })
      })
    })
  })

  calendarData.value = dataMap
  return events
}

// ==================== 刷新日历事件 ====================

function refreshEvents() {
  const api = calendarRef.value?.getApi()
  if (!api) return
  api.removeAllEvents()
  buildCalendarEvents().forEach(e => api.addEvent(e))
}

// ==================== 日历配置 ====================

const calendarOptions = {
  plugins: [dayGridPlugin, interactionPlugin],
  locale: zhLocale,
  headerToolbar: false,
  editable: true,
  eventDisplay: 'block',
  dayMaxEvents: 3,
  moreLinkText: (n) => `+${n} 项`,

  // 点击日期格子
  dateClick: (info) => {
    selectedDate.value = info.dateStr
    selectedDeliveries.value = calendarData.value[info.dateStr] || []
  },

  // 点击事件卡片
  eventClick: (info) => {
    const delivery = info.event.extendedProps
    selectedDate.value = delivery.nodeName
      ? `${delivery.deliveryDate} (${delivery.nodeName})`
      : delivery.deliveryDate
    selectedDeliveries.value = calendarData.value[delivery.deliveryDate] || []
  },

  // 拖拽结束
  eventDrop: (info) => {
    const delivery = info.event.extendedProps
    const oldDate = info.oldEvent.startStr
    const newDate = info.event.startStr
    const newMonth = newDate.substring(0, 7)
    if (newMonth !== props.yearMonth) {
      info.revert()
      ElMessage.warning('仅支持当月内调整日期')
      return
    }

    if (oldDate === newDate) return

    // 去重：同一节点只留一条
    const existing = pendingChanges.value.find(c => c.deliveryId === delivery.deliveryId)
    if (existing) {
      existing.newDate = newDate
    } else {
      pendingChanges.value.push({
        deliveryId: delivery.deliveryId,
        planId: delivery.planId,
        lineId: delivery.lineId,
        oldDate,
        newDate,
        version: delivery.version
      })
    }

    // 刷新日历和右侧面板
    refreshEvents()
    if (selectedDate.value) {
      selectedDeliveries.value = calendarData.value[selectedDate.value] || []
    }

    ElMessage.success(`已移动到 ${newDate}`)
  }
}

// ==================== 工具栏操作 ====================

function handleReset() {
  pendingChanges.value = []
  refreshEvents()
  selectedDate.value = null
  selectedDeliveries.value = []
  ElMessage.info('已重置')
}

function handleSave() {
  if (pendingChanges.value.length === 0) {
    ElMessage.info('没有需要保存的变更')
    return
  }

  // 构建 deliveryId → 原始交货数据的映射
  const deliveryMap = {}
  props.planList.forEach(plan => {
    const lineList = plan.lineList || plan.lines || []
    lineList.forEach(line => {
      const deliveryList = line.deliveryList || line.deliveries || []
      deliveryList.forEach(d => {
        deliveryMap[d.deliveryId] = d
      })
    })
  })

  // 转换为 SalesPlanDeliveryUpdateDTO 列表
  const dtoList = pendingChanges.value.map(change => {
    const original = deliveryMap[change.deliveryId] || {}
    return {
      deliveryId: change.deliveryId,
      lineId: change.lineId,
      nodeName: original.nodeName || '',
      deliveryDate: change.newDate,
      planQuantity: original.planQuantity || 0,
      actualQuantity: original.actualQuantity || 0,
      status: original.status ?? 0,
      version: change.version || original.version || 0
    }
  })

  emit('save', {
    type: 'delivery:batch-move',
    data: dtoList
  })
}

// ==================== 生命周期 ====================

onMounted(() => {
  nextTick(() => {
    refreshEvents()
    if (props.yearMonth) {
      const api = calendarRef.value?.getApi()
      api?.gotoDate(`${props.yearMonth}-01`)
    }
  })
})

// ==================== 监听 ====================

watch(
  () => props.planList,
  () => {
    pendingChanges.value = []
    refreshEvents()
  },
  { deep: true }
)

watch(
  () => props.yearMonth,
  (val) => {
    if (val && calendarRef.value) {
      const api = calendarRef.value.getApi()
      api.gotoDate(`${val}-01`)
    }
  }
)
</script>

<style scoped>
.plan-calendar {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  overflow: hidden;
}

.toolbar {
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.main-area {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.calendar-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.side-panel {
  flex-shrink: 0;
  width: 300px;
  overflow-y: auto;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  padding: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
  color: #303133;
}

.delivery-card {
  padding: 12px;
  margin-bottom: 10px;
  background: #f5f7fa;
  border-radius: 6px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.card-plan {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
}

.card-product {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.card-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.panel-placeholder {
  color: #909399;
  text-align: center;
  margin-top: 40px;
}
</style>