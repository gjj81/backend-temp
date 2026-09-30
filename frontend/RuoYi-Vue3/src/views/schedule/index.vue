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

    <div v-if="activeView === 'gantt'" class="toolbar">
      <div class="toolbar-left">
        <span class="filter-label">车间</span>
        <el-select v-model="selectedWorkshop" placeholder="选择车间" size="default" style="width: 140px;" @change="onWorkshopChange">
          <el-option v-for="w in workshops" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>

        <el-date-picker 
          v-model="dateRange" 
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 260px; margin-left: 12px;"
        />

        <span class="filter-label">客户：</span>
        <el-select v-model="selectedCustomer" placeholder="全部" clearable size="default" style="width: 120px;">
          <el-option v-for="c in customerList" :key="c" :label="c" :value="c" />
        </el-select>
      </div>

      <div class="toolbar-right">
        <span class="draft-count" v-if="draftCount > 0">未保存 {{ draftCount }} 条</span>
        <span class="issued-count" v-if="issuedCount > 0">待下发 {{ issuedCount }} 条</span>
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
      <keep-alive>
        <ScheduleEdit
          v-if="activeView === 'gantt'"
          key="schedule-edit"
          :board-data="boardData"
          :date-range="dateRange"
          :workshop-id="selectedWorkshop"
          :customer-filter="selectedCustomer"
          @save="handleSave"
          @delete="handleDelete"
          @select-line="handleSelectLine"
        />
        <ShopKanban v-else key="shop-kanban" :board-data="boardData" />
      </keep-alive>
    </div>
  </div>
</template>

<script setup name="ProductionPlan">
import { ref, computed, watch, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWorkbenchProducts, listProdProducts, saveSchedules, issueSchedules, deleteSchedule } from '@/api/schedule'
import { normalizeProductList, ProdScheduleSaveDTO } from '@/types/schedule'
import { selectWorkshop, selectProdLine } from '@/api/base'
import ScheduleEdit from './ScheduleEdit/index.vue'
import ShopKanban from './ShopKanban.vue'

let _cachedWorkbenchProducts = null
let _cachedSpans = null
let _cachedProdLines = null
let _cachedDraftSpans = null
let _cachedWorkshops = null
let _cachedWorkshopConfig = {}
let _lastDateRange = null
let _lastWorkshopId = null

const yearMonth = ref('2026-10')
const dateRange = ref(_lastDateRange || ['2026-10-01', '2026-10-31'])
const activeView = ref('gantt')
const selectedWorkshop = ref(_lastWorkshopId)
const selectedCustomer = ref('')
const loading = ref(false)

const workbenchProducts = ref(_cachedWorkbenchProducts ?? [])

const workshops = ref(_cachedWorkshops ?? [])

const workshopConfig = _cachedWorkshopConfig

async function fetchWorkshops() {
  if (_cachedWorkshops && _cachedWorkshops.length > 0) {
    workshops.value = [..._cachedWorkshops]
    return
  }
  try {
    const res = await selectWorkshop()
    const list = res.data ?? res ?? []
    workshops.value = list.map(w => ({
      id: w.workshopId,
      name: w.workshopName,
      code: w.workshopCode
    }))
    _cachedWorkshops = [...workshops.value]
  } catch {
    ElMessage.error('获取车间列表失败')
  }
}

async function fetchProdLines() {
  if (Object.keys(_cachedWorkshopConfig).length > 0) {
    Object.assign(workshopConfig, _cachedWorkshopConfig)
    return
  }
  try {
    const res = await selectProdLine()
    const list = res.data ?? res ?? []
    const grouped = {}
    list.forEach(pl => {
      const wsId = pl.workshopId
      if (!grouped[wsId]) grouped[wsId] = []
      grouped[wsId].push({
        lineId: pl.prodLineId,
        lineName: pl.lineName,
        lineCode: pl.lineCode,
        workshopId: wsId
      })
    })
    Object.keys(grouped).forEach(wsId => {
      if (!workshopConfig[wsId]) workshopConfig[wsId] = {}
      workshopConfig[wsId].prodLines = grouped[wsId]
    })
    _cachedWorkshopConfig = JSON.parse(JSON.stringify(workshopConfig))
  } catch {
    ElMessage.error('获取产线列表失败')
  }
}

function restoreBoardData() {
  if (_cachedWorkbenchProducts != null) {
    workbenchProducts.value = [..._cachedWorkbenchProducts]
  }
  boardData.value.poolLines = _cachedWorkbenchProducts != null
    ? buildPoolLinesFromApi(_cachedWorkbenchProducts)
    : []
  boardData.value.spans = _cachedSpans != null ? [..._cachedSpans] : []
  boardData.value.prodLines = _cachedProdLines != null ? [..._cachedProdLines] : []
  boardData.value.draftSpans = _cachedDraftSpans != null ? [..._cachedDraftSpans] : []
}

const boardData = ref({
  poolLines: _cachedWorkbenchProducts != null ? buildPoolLinesFromApi(_cachedWorkbenchProducts) : [],
  spans: _cachedSpans ?? [],
  draftSpans: _cachedDraftSpans ?? [],
  prodLines: _cachedProdLines ?? []
})

function buildPoolLinesFromApi(products) {
  return (products || []).map(p => ({
    lineId: Number(p.lineId) || p.lineId,
    productName: p.productName || '',
    status: p.lineStatus ?? 0,
    customer: p.customer || '',
    planMonth: p.planMonth || '', 
    totalQuantity: p.totalQuantity || 0,
    forecastQuantity: p.forecastQuantity || 0,
    openingInventory: p.openingInventory || 0,
    scheduledQuantity: p.scheduledQuantity || 0,
    earliestDelivery: p.deliveryNodes?.[0]?.deliveryDate || null,
    deliveryList: (p.deliveryNodes || []).map((d, i) => ({
      deliveryId: `${p.lineId}_${i}`,
      deliveryDate: d.deliveryDate || null,
      planQuantity: d.planQuantity || 0,
      status: 0
    }))
  }))
}

function getCacheKey() {
  return `${selectedWorkshop.value}_${dateRange.value?.[0]}_${dateRange.value?.[1]}`
}

function isCacheValid() {
  const currentKey = getCacheKey()
  const cachedKey = `${_lastWorkshopId}_${_lastDateRange?.[0]}_${_lastDateRange?.[1]}`
  return _cachedWorkbenchProducts != null && _cachedSpans != null && currentKey === cachedKey
}

async function refreshWorkbench() {
  try {
    const res = await listWorkbenchProducts()
    workbenchProducts.value = normalizeProductList(res ?? [])
    _cachedWorkbenchProducts = [...workbenchProducts.value]
  } catch { /* ignore */ }
  rebuildPoolLines()
}

function rebuildPoolLines() {
  boardData.value.poolLines = buildPoolLinesFromApi(workbenchProducts.value)
}

async function rebuildSpans(force = false) {
  const wsId = selectedWorkshop.value
  if (!wsId || !dateRange.value || dateRange.value.length < 2) return
  const startDate = dateRange.value[0]
  const endDate = dateRange.value[1]

  if (!force && _cachedSpans != null && _lastWorkshopId === wsId && _lastDateRange?.[0] === startDate && _lastDateRange?.[1] === endDate) {
    const prodLines = workshopConfig[wsId]?.prodLines || []
    boardData.value.spans = [..._cachedSpans]
    boardData.value.prodLines = [...prodLines]
    return
  }

  try {
    const res = await listProdProducts(startDate, endDate)
    const list = res ?? []
    const prodLines = workshopConfig[wsId]?.prodLines || []
    const plMap = {}
    prodLines.forEach(pl => { plMap[pl.lineId] = pl })

    boardData.value.spans = list
      .filter(s => String(s.workshopId) === String(wsId))
      .map(s => {
        const pl = plMap[s.prodLineId]
        return {
          spanId: s.scheduleId,
          lineId: s.lineId,
          prodLineId: s.prodLineId,
          prodLineName: pl?.lineName || s.workshopName || '未知产线',
          startDate: formatDateStr(s.scheduleDate),
          endDate: formatDateStr(s.scheduleDate),
          dailyQuantity: s.quantity || 0,
          days: 1,
          status: s.status ?? 0,
          version: s.version ?? 1,
          actualQuantity: s.actualQuantity,
          qualifiedQuantity: s.qualifiedQuantity,
          defectQuantity: s.defectQuantity
        }
      })
    boardData.value.prodLines = [...prodLines]
    _cachedSpans = [...boardData.value.spans]
    _cachedProdLines = [...boardData.value.prodLines]
    _lastDateRange = [startDate, endDate]
    _lastWorkshopId = wsId
  } catch {
    ElMessage.error('获取排产数据失败')
  }
}

function formatDateStr(d) {
  if (d == null || d === '') return ''
  if (typeof d === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(d)) return d
  const dt = d instanceof Date ? d : new Date(d)
  if (isNaN(dt.getTime())) return ''
  return dt.getFullYear() + '-' + String(dt.getMonth() + 1).padStart(2, '0') + '-' + String(dt.getDate()).padStart(2, '0')
}

async function fetchAndRebuild(force = false) {
  if (!force && isCacheValid()) {
    restoreBoardData()
    return
  }

  loading.value = true
  try {
    const res = await listWorkbenchProducts()
    workbenchProducts.value = normalizeProductList(res ?? [])
    _cachedWorkbenchProducts = [...workbenchProducts.value]
  } catch {
    ElMessage.error('获取月度销售计划失败')
    workbenchProducts.value = []
  } finally {
    loading.value = false
  }
  rebuildPoolLines()
  await rebuildSpans(force)
  _lastDateRange = [dateRange.value[0], dateRange.value[1]]
  _lastWorkshopId = selectedWorkshop.value
}

const dateRangeKey = computed(() => dateRange.value ? `${dateRange.value[0]}_${dateRange.value[1]}` : '')

watch(dateRangeKey, (newKey, oldKey) => {
  if (oldKey === undefined) return
  if (newKey && newKey !== oldKey) {
    fetchAndRebuild(true)
  }
})

onMounted(async () => {
  await fetchWorkshops()
  await fetchProdLines()
  if (workshops.value.length > 0 && !selectedWorkshop.value) {
    selectedWorkshop.value = workshops.value[0].id
  }
  if (isCacheValid()) {
    restoreBoardData()
  } else {
    await fetchAndRebuild()
  }
})

onActivated(() => {
  if (isCacheValid()) {
    restoreBoardData()
  }
})

async function onWorkshopChange() {
  await rebuildSpans(true)
}

const customerList = computed(() => {
  const customers = new Set(boardData.value.poolLines.map(l => l.customer).filter(Boolean))
  return [...customers]
})

const draftCount = computed(() => boardData.value.draftSpans.length)
const issuedCount = computed(() => boardData.value.spans.filter(s => s.status === 0).length)

async function handleSave(payload) {
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

      boardData.value.draftSpans.push(completeSpan)

      const line = boardData.value.poolLines.find(l => l.lineId === completeSpan.lineId)
      if (line) line.scheduledQty = (line.scheduledQty || 0) + completeSpan.dailyQuantity
      break
    }
    case 'span:move': {
      const { spanId, date, prodLineId } = payload.data
      const span = findSpan(spanId)
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
    case 'span:save': {
      const { spanId } = payload.data
      const span = findSpan(spanId)
      if (!span) break
      try {
        await saveSchedules([spanToDTO(span, 0)])
        if (boardData.value.draftSpans.includes(span)) {
          boardData.value.draftSpans = boardData.value.draftSpans.filter(s => s.spanId !== spanId)
        }
        await rebuildSpans(true)
        ElMessage.success('保存成功')
      } catch (e) {
        ElMessage.error(e?.message || '保存失败')
      }
      break
    }
    case 'span:issue': {
      const { spanId, version } = payload.data
      const span = findSpan(spanId)
      if (!span || span.status !== 0) break
      try {
        const dto = spanToDTO(span, 1)
        dto.version = version || span.version
        await issueSchedules([dto])
        span.status = 1
        _cachedSpans = null
        await refreshWorkbench()
        ElMessage.success('下发成功')
      } catch (e) {
        ElMessage.error(e?.message || '下发失败')
      }
      break
    }
  }
}

function findSpan(spanId) {
  return boardData.value.spans.find(s => s.spanId === spanId)
      || boardData.value.draftSpans.find(s => s.spanId === spanId)
}

function spanToDTO(span, targetStatus) {
  const ws = workshops.value.find(w => w.id === selectedWorkshop.value)
  return new ProdScheduleSaveDTO({
    scheduleId: span._isNew ? undefined : (span.scheduleId || String(span.spanId)),
    lineId: String(span.lineId),
    prodLineId: String(span.prodLineId),
    scheduleDate: span.startDate,
    quantity: span.dailyQuantity,
    workshopId: selectedWorkshop.value,
    workshopName: ws?.name || '',
    productId: span.productId,
    status: targetStatus,
    version: span.version ?? 1
  })
}

async function handleDelete(payload) {
  if (payload.type === 'span' && payload.data) {
    const spanId = payload.data.spanId || payload.data
    const span = findSpan(spanId)
    if (!span) return
    if (!span._isNew && span.status !== 0) {
      ElMessage.warning('已下发/生产中/已完工的排产块不允许删除')
      return
    }
    try {
      if (!span._isNew) {
        const id = span.scheduleId || String(span.spanId)
        await deleteSchedule(id, span.version ?? 1)
      }
      let idx = boardData.value.spans.findIndex(s => s.spanId === spanId)
      if (idx !== -1) {
        boardData.value.spans.splice(idx, 1)
      } else {
        idx = boardData.value.draftSpans.findIndex(s => s.spanId === spanId)
        if (idx !== -1) boardData.value.draftSpans.splice(idx, 1)
      }
      _cachedSpans = null
      ElMessage.success('已删除排产块')
    } catch (e) {
      ElMessage.error(e?.message || '删除失败')
    }
  }
}

function handleSelectLine(line) {}

async function handleBatchSave() {
  if (draftCount.value === 0) return
  try {
    await ElMessageBox.confirm(`确定要批量保存 ${draftCount.value} 条草稿吗？`, '批量保存', {
      confirmButtonText: '确认保存', cancelButtonText: '取消', type: 'warning'
    })
    const dtoList = boardData.value.draftSpans.map(s => spanToDTO(s, 0))
    await saveSchedules(dtoList)
    boardData.value.draftSpans = []
    await rebuildSpans(true)
    ElMessage.success(`已批量保存 ${dtoList.length} 条`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '保存失败')
    }
  }
}

async function handleBatchIssue() {
  if (issuedCount.value === 0) return
  try {
    await ElMessageBox.confirm(`确定要批量下发 ${issuedCount.value} 条未下发排产吗？`, '批量下发', {
      confirmButtonText: '确认下发', cancelButtonText: '取消', type: 'success'
    })
    const toIssue = boardData.value.spans.filter(s => s.status === 0)
    const dtoList = toIssue.map(s => spanToDTO(s, 1))
    await issueSchedules(dtoList)
    toIssue.forEach(s => { s.status = 1 })
    _cachedSpans = null
    await refreshWorkbench()
    ElMessage.success(`已批量下发 ${dtoList.length} 条`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '下发失败')
    }
  }
}

async function handleResetData() {
  try {
    await ElMessageBox.confirm('确定要清除所有未保存的草稿排产吗？已保存的数据不受影响。', '重置数据', {
      confirmButtonText: '确认清除', cancelButtonText: '取消', type: 'warning'
    })
    boardData.value.draftSpans = []
    ElMessage.success('未保存的数据已清除')
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