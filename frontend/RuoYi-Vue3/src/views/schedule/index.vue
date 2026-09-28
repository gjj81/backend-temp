<template>
  <div class="schedule-page">
    <div class="page-header">
      <div class="header-top">
        <h1 class="main-title">排产工作台</h1>
      </div>

      <div class="tab-group">
        <div 
          :class="['tab-item', { active: activeView === 'gantt' }]"
          @click="activeView = 'gantt'"
        >
          排产工作台
        </div>
        <div 
          :class="['tab-item', { active: activeView === 'kanban' }]"
          @click="activeView = 'kanban'"
        >
          车间看板
        </div>
      </div>
    </div>

    <div v-if="activeView === 'gantt'" class="gantt-view">
      <div class="toolbar">
        <div class="toolbar-left">
          <span class="filter-label">车间</span>
          <el-select v-model="selectedWorkshop" placeholder="选择车间" size="default" style="width: 140px;" @change="onWorkshopChange">
            <el-option v-for="w in workshops" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>

          <el-date-picker 
            v-model="yearMonth" 
            type="month" 
            value-format="YYYY-MM" 
            style="width: 130px; margin-left: 12px;"
            @change="onMonthChange"
          />

          <span class="filter-label">客户：</span>
          <el-select v-model="selectedCustomer" placeholder="全部" clearable size="default" style="width: 120px;">
            <el-option v-for="c in customerList" :key="c" :label="c" :value="c" />
          </el-select>
        </div>

        <div class="toolbar-right">
          <span class="draft-count" v-if="draftCount > 0">草稿 {{ draftCount }} 条</span>
          <span class="issued-count" v-if="issuedCount > 0">未下发 {{ issuedCount }} 条</span>
          <el-button type="primary" size="default" @click="handleBatchSave" :disabled="draftCount === 0">
            批量保存
          </el-button>
          <el-button type="success" size="default" @click="handleBatchIssue" :disabled="issuedCount === 0">
            批量下发
          </el-button>
          <el-button size="default" @click="handleResetData">
            重置数据
          </el-button>
        </div>
      </div>

      <div class="board-container">
        <ScheduleEdit
          :board-data="boardData"
          :year-month="yearMonth"
          :workshop-id="selectedWorkshop"
          :customer-filter="selectedCustomer"
          @save="handleSave"
          @delete="handleDelete"
          @select-line="handleSelectLine"
        />
      </div>
    </div>

    <div v-else class="kanban-view">
      <ShopKanban :board-data="boardData" />
    </div>
  </div>
</template>

<script setup name="SchedulePlan">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ScheduleEdit from './ScheduleEdit/index.vue'
import ShopKanban from './ShopKanban.vue'

const yearMonth = ref('2026-05')
const activeView = ref('gantt')
const selectedWorkshop = ref(1)
const selectedCustomer = ref('')

const workshops = ref([
  { id: 1, name: '机加车间' },
  { id: 2, name: '装配车间' }
])

const basePoolLines = [
  {
    lineId: 1, productName: '1.5T某如皋进气', status: 0, customer: '奇瑞',
    totalQuantity: 12000, forecastQuantity: 2700, openingInventory: 200, scheduledQty: 4000
  },
  {
    lineId: 2, productName: '2.0T排气凸轮轴', status: 0, customer: '奇瑞',
    totalQuantity: 45000, forecastQuantity: 15000, openingInventory: 0, scheduledQty: 15700
  },
  {
    lineId: 3, productName: '1.0T进气凸轮轴', status: 0, customer: '奇瑞汽车',
    totalQuantity: 5000, forecastQuantity: 4000, openingInventory: 200, scheduledQty: 3000
  },
  {
    lineId: 4, productName: '1.5T排气凸轮轴', status: 2, customer: '奇瑞汽车',
    totalQuantity: 5000, forecastQuantity: 5000, openingInventory: 3200, scheduledQty: 4000
  },
  {
    lineId: 5, productName: '2.0T排气凸轮轴', status: 0, customer: '奇瑞汽车',
    totalQuantity: 45000, forecastQuantity: 5000, openingInventory: 0, scheduledQty: 0
  }
]

const workshopConfig = {
  1: { name: '机加车间', prodLines: [{ lineId: 1, lineName: '一号机' }, { lineId: 2, lineName: '二号机' }, { lineId: 3, lineName: '三号机' }] },
  2: { name: '装配车间', prodLines: [{ lineId: 4, lineName: '装配线A' }, { lineId: 5, lineName: '装配线B' }] }
}

function buildDeliveryList(lineId, month) {
  const [y, m] = month.split('-').map(Number)
  const day = 5 + (lineId * 3)
  return [
    { deliveryId: lineId * 100 + 1, deliveryDate: `${y}-${String(m).padStart(2, '0')}-${String(day).padStart(2, '0')}`, planQuantity: 4000, status: 0 },
    { deliveryId: lineId * 100 + 2, deliveryDate: `${y}-${String(m).padStart(2, '0')}-${String(day + 10).padStart(2, '0')}`, planQuantity: 4000, status: 0 },
    { deliveryId: lineId * 100 + 3, deliveryDate: `${y}-${String(m).padStart(2, '0')}-${String(day + 11).padStart(2, '0')}`, planQuantity: 4000, status: 0 }
  ]
}

function buildPoolLines(month) {
  return basePoolLines.map(line => {
    const outs = buildDeliveryList(line.lineId, month)
    return {
      ...line,
      earliestDelivery: outs[0]?.deliveryDate || null,
      deliveryList: outs
    }
  })
}

function buildSpans(workshopId, month) {
  const [y, m] = month.split('-').map(Number)
  const pad = (d) => `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`
  const pls = workshopConfig[workshopId]?.prodLines || workshopConfig[1].prodLines

  const templates = workshopId === 1 ? [
    { spanId: 1,  lineId: 1, prodLineId: 1, day: 6,  qty: 1000, status: 1 },
    { spanId: 2,  lineId: 1, prodLineId: 1, day: 7,  qty: 1000, status: 1 },
    { spanId: 3,  lineId: 1, prodLineId: 1, day: 10, qty: 1660, status: 1 },
    { spanId: 4,  lineId: 1, prodLineId: 2, day: 11, qty: 1000, status: 0 },
    { spanId: 5,  lineId: 2, prodLineId: 2, day: 9,  qty: 1000, status: 2 },
    { spanId: 11, lineId: 2, prodLineId: 3, day: 16, qty: 1800, status: 3 },
    { spanId: 7,  lineId: 3, prodLineId: 1, day: 13, qty: 1500, status: 1 },
    { spanId: 10, lineId: 3, prodLineId: 2, day: 14, qty: 800,  status: 0 },
    { spanId: 8,  lineId: 4, prodLineId: 1, day: 4,  qty: 1000, status: 2 },
    { spanId: 9,  lineId: 4, prodLineId: 2, day: 6,  qty: 1000, status: 3 }
  ] : [
    { spanId: 51, lineId: 1, prodLineId: 4, day: 8,  qty: 1200, status: 1 },
    { spanId: 52, lineId: 1, prodLineId: 4, day: 9,  qty: 1200, status: 1 },
    { spanId: 53, lineId: 2, prodLineId: 5, day: 12, qty: 2000, status: 0 },
    { spanId: 54, lineId: 2, prodLineId: 5, day: 13, qty: 2000, status: 0 },
    { spanId: 55, lineId: 3, prodLineId: 4, day: 15, qty: 800,  status: 1 },
    { spanId: 56, lineId: 4, prodLineId: 5, day: 5,  qty: 900,  status: 2 }
  ]

  return templates.map(t => {
    const p = pls.find(pl => pl.lineId === t.prodLineId)
    return {
      spanId: t.spanId,
      lineId: t.lineId,
      prodLineId: t.prodLineId,
      prodLineName: p?.lineName || '未知产线',
      startDate: pad(t.day),
      endDate: pad(t.day),
      dailyQuantity: t.qty,
      days: 1,
      status: t.status
    }
  })
}

const boardData = ref({ poolLines: [], spans: [], prodLines: [] })

function rebuildBoardData() {
  boardData.value = {
    poolLines: buildPoolLines(yearMonth.value),
    spans: buildSpans(selectedWorkshop.value, yearMonth.value),
    prodLines: (workshopConfig[selectedWorkshop.value] || workshopConfig[1]).prodLines
  }
}

rebuildBoardData()

function onWorkshopChange() {
  rebuildBoardData()
}

function onMonthChange() {
  rebuildBoardData()
}

const customerList = computed(() => {
  const customers = new Set(boardData.value.poolLines.map(l => l.customer).filter(Boolean))
  return [...customers]
})

const draftCount = computed(() => boardData.value.spans.filter(s => s.status === 0).length)
const issuedCount = computed(() => boardData.value.spans.filter(s => s.status === 1).length)

function handleSave(payload) {
  switch (payload.type) {
    case 'span:add': {
      const newSpan = payload.data
      const completeSpan = {
        ...newSpan,
        spanId: newSpan.spanId || Date.now(),
        startDate: newSpan.date,
        endDate: newSpan.date,
        days: 1,
        status: 0,
        version: 1,
        _isNew: true
      }
      const pl = boardData.value.prodLines.find(p => p.lineId === completeSpan.prodLineId)
      if (pl) completeSpan.prodLineName = pl.lineName

      boardData.value.spans.push(completeSpan)

      const line = boardData.value.poolLines.find(l => l.lineId === completeSpan.lineId)
      if (line) line.scheduledQty = (line.scheduledQty || 0) + completeSpan.dailyQuantity
      break
    }
    case 'span:move': {
      const { spanId, date, prodLineId } = payload.data
      const span = boardData.value.spans.find(s => s.spanId === spanId)
      if (span) {
        span.startDate = date
        span.endDate = date
        span.days = 1
        if (prodLineId) {
          span.prodLineId = prodLineId
          const pl = boardData.value.prodLines.find(p => p.lineId === prodLineId)
          if (pl) span.prodLineName = pl.lineName
        }
      }
      break
    }
    case 'span:update': {
      const data = payload.data
      const idx = boardData.value.spans.findIndex(s => s.spanId === data.spanId)
      if (idx !== -1) {
        if (data.startDate) { data.endDate = data.startDate; data.days = 1 }
        Object.assign(boardData.value.spans[idx], data)
      }
      break
    }
  }
}

function handleDelete(payload) {
  if (payload.type === 'span' && payload.data) {
    const spanId = payload.data.spanId || payload.data
    const idx = boardData.value.spans.findIndex(s => s.spanId === spanId)
    if (idx !== -1) {
      const removed = boardData.value.spans.splice(idx, 1)[0]
      const line = boardData.value.poolLines.find(l => l.lineId === removed.lineId)
      if (line && line.scheduledQty > 0) {
        line.scheduledQty = Math.max(0, line.scheduledQty - removed.dailyQuantity)
      }
      ElMessage.success('已删除排产块')
    }
  }
}

function handleSelectLine(line) {}

async function handleBatchSave() {
  try {
    await ElMessageBox.confirm(`确定要批量保存 ${draftCount.value} 条草稿吗？`, '批量保存', {
      confirmButtonText: '确认保存', cancelButtonText: '取消', type: 'warning'
    })
    boardData.value.spans.forEach(span => {
      if (span.status === 0) {
        span.status = 1
        span._isNew = false
        span.days = 1
        span.endDate = span.startDate
      }
    })
    ElMessage.success(`已批量保存 ${draftCount.value} 条`)
  } catch (_) {}
}

async function handleBatchIssue() {
  try {
    await ElMessageBox.confirm(`确定要批量下发 ${issuedCount.value} 条未下发排产吗？`, '批量下发', {
      confirmButtonText: '确认下发', cancelButtonText: '取消', type: 'success'
    })
    boardData.value.spans.forEach(span => {
      if (span.status === 1) span.status = 2
    })
    ElMessage.success(`已批量下发 ${issuedCount.value} 条`)
  } catch (_) {}
}

async function handleResetData() {
  try {
    await ElMessageBox.confirm('确定要重置当前车间所有排产数据吗？此操作不可撤销！', '重置数据', {
      confirmButtonText: '确认重置', cancelButtonText: '取消', type: 'warning'
    })
    boardData.value.spans = []
    ElMessage.success('数据已重置')
  } catch (_) {}
}
</script>

<style scoped>
.schedule-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);
  overflow: hidden;
  background: #f5f7fa;
}
.page-header {
  flex-shrink: 0;
  background: #fff;
  padding: 16px 24px 0;
  border-bottom: 1px solid #e4e7ed;
}
.header-top { margin-bottom: 16px; }
.main-title { margin: 0 0 8px; font-size: 20px; font-weight: 600; color: #303133; }
.tab-group { display: flex; gap: 4px; border-bottom: 2px solid #e4e7ed; }
.tab-item { 
  padding: 10px 24px; cursor: pointer; font-size: 14px; color: #606266; 
  border-bottom: 2px solid transparent; margin-bottom: -2px; transition: all 0.2s; user-select: none; 
}
.tab-item:hover { color: #409eff; }
.tab-item.active { color: #303133; font-weight: 600; border-bottom-color: #409eff; }
.gantt-view, .kanban-view { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.toolbar {
  flex-shrink: 0; display: flex; align-items: center; justify-content: space-between;
  padding: 12px 24px; background: #fff; border-bottom: 1px solid #e4e7ed;
}
.toolbar-left { display: flex; align-items: center; gap: 8px; }
.filter-label { font-size: 13px; color: #606266; white-space: nowrap; }
.toolbar-right { display: flex; align-items: center; gap: 12px; }
.draft-count { font-size: 13px; color: #e6a23c; font-weight: 600; padding: 4px 12px; background: #fdf6ec; border-radius: 4px; }
.issued-count { font-size: 13px; color: #409eff; font-weight: 600; padding: 4px 12px; background: #ecf5ff; border-radius: 4px; }
.board-container { flex: 1; overflow: hidden; padding: 16px 24px; }
</style>