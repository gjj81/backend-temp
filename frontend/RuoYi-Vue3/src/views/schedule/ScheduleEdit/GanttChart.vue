<template>
  <div class="gantt-chart">
    <div class="gantt-header" ref="headerRef">
      <div class="gantt-corner">产线 \ 日期</div>
      <div class="date-headers" ref="dateHeadersRef">
        <div 
          v-for="day in dateRange" 
          :key="day.date"
          :class="['date-cell', { 
            'is-today': day.isToday,
            'has-deadline': hasDeadline(day.date),
            'is-weekend': day.isWeekend
          }]"
          :title="getDeadlineTooltip(day.date)"
        >
          {{ String(day.day).padStart(2, '0') }}
        </div>
      </div>
    </div>

    <div class="gantt-body" ref="bodyRef" @scroll="onBodyScroll">
      <div 
        v-for="prodLine in prodLines" 
        :key="prodLine.lineId"
        class="gantt-row"
        :style="{ minHeight: getRowHeight(prodLine.lineId) + 'px' }"
      >
        <div class="row-label">{{ prodLine.lineName }}</div>
        
        <div class="row-cells">
          <div 
            v-for="day in dateRange" 
            :key="day.date"
            :class="['cell', { 
              'is-today': day.isToday,
              'is-weekend': day.isWeekend,
              'has-deadline': hasDeadline(day.date)
            }]"
            :data-date="day.date"
            :data-line-id="prodLine.lineId"
            @mousedown="onCellMouseDown($event, prodLine, day)"
          >
            <template v-if="getSpansForCell(prodLine.lineId, day.date).length > 0">
              <div
                v-for="(span, idx) in getSpansForCell(prodLine.lineId, day.date)"
                :key="span.spanId"
                class="span-block"
                :class="{ 
                  ['span-status-' + span.status]: true,
                  'span-selected': span.spanId === selectedSpanId,
                  'span-matched': selectedLine && span.lineId === selectedLine.lineId
                }"
                :style="getBlockStyle(idx)"
                @click.stop="onSpanClick($event, span)"
                @mousedown="onBlockMouseDown($event, span)"
              >
                <div class="span-name">{{ getProductName(span) }}</div>
                <div class="span-qty">{{ span.dailyQuantity }}</div>
              </div>
            </template>
          </div>
        </div>
      </div>

      <div v-if="!prodLines || prodLines.length === 0" class="empty-gantt">
        暂无产线数据
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'

const headerRef = ref(null)
const dateHeadersRef = ref(null)
const bodyRef = ref(null)

const props = defineProps({
  spans: { type: Array, default: () => [] },
  prodLines: { type: Array, default: () => [] },
  yearMonth: { type: String, default: '' },
  selectedLine: { type: Object, default: null },
  selectedSpanId: { type: [String, Number], default: null },
  deliveryDates: { type: Set, default: () => new Set() },
  boardData: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['create', 'move', 'select'])

const dateRange = computed(() => {
  if (!props.yearMonth) return []
  const [year, month] = props.yearMonth.split('-').map(Number)
  const daysInMonth = new Date(year, month, 0).getDate()
  const today = new Date()
  
  return Array.from({ length: daysInMonth }, (_, i) => {
    const date = `${year}-${String(month).padStart(2, '0')}-${String(i + 1).padStart(2, '0')}`
    const d = new Date(date)
    return {
      date,
      day: i + 1,
      isToday: d.toDateString() === today.toDateString(),
      isWeekend: d.getDay() === 0 || d.getDay() === 6
    }
  })
})

function getSpansForCell(lineId, date) {
  if (!props.spans || !Array.isArray(props.spans)) return []
  return props.spans.filter(span => {
    if (span.prodLineId !== lineId) return false
    if (date < span.startDate || date > span.endDate) return false
    return true
  })
}

function getProductName(span) {
  if (!props.boardData?.poolLines) return `产品${span.lineId}`
  const product = props.boardData.poolLines.find(l => l.lineId === span.lineId)
  return product?.productName || `产品${span.lineId}`
}

function hasDeadline(date) {
  return props.deliveryDates?.has(date)
}

function getDeadlineTooltip(date) {
  return hasDeadline(date) ? '交货节点' : ''
}

const BLOCK_HEIGHT = 44
const BLOCK_GAP = 4

function getBlockStyle(index) {
  return {
    top: `${index * (BLOCK_HEIGHT + BLOCK_GAP)}px`,
    height: `${BLOCK_HEIGHT}px`
  }
}

function getMaxBlocksInRow(prodLineId) {
  let max = 1
  if (!dateRange.value) return max
  dateRange.value.forEach(day => {
    const count = getSpansForCell(prodLineId, day.date).length
    if (count > max) max = count
  })
  return max
}

function getRowHeight(prodLineId) {
  const maxBlocks = getMaxBlocksInRow(prodLineId)
  return Math.max(52, maxBlocks * BLOCK_HEIGHT + (maxBlocks - 1) * BLOCK_GAP + 8)
}

const dragState = ref({
  active: false,
  type: null,
  startProdLineId: null,
  startDate: null,
  startClientX: 0,
  startClientY: 0,
  hasMoved: false,
  span: null,
  currentProdLineId: null
})

const touchedDates = ref(new Set())

function onCellMouseDown(event, prodLine, day) {
  const target = event.target
  if (target.closest('.span-block')) {
    return
  }

  if (!props.selectedLine) {
    ElMessage.warning('请先在左侧选择一个产品')
    return
  }

  dragState.value = {
    active: true,
    type: 'new',
    startProdLineId: prodLine.lineId,
    startDate: day.date,
    startClientX: event.clientX,
    startClientY: event.clientY,
    hasMoved: false,
    span: null,
    currentProdLineId: prodLine.lineId
  }
  touchedDates.value = new Set([day.date])

  document.addEventListener('mousemove', onGlobalMouseMove)
  document.addEventListener('mouseup', onGlobalMouseUp)
}

function onSpanClick(event, span) {
  emit('select', span)
}

function onBlockMouseDown(event, span) {
  if (span.status !== 0) {
    emit('select', span)
    return
  }

  event.preventDefault()
  event.stopPropagation()

  dragState.value = {
    active: true,
    type: 'move',
    startProdLineId: span.prodLineId,
    startDate: span.startDate,
    startClientX: event.clientX,
    startClientY: event.clientY,
    hasMoved: false,
    span: span,
    currentProdLineId: span.prodLineId
  }
  touchedDates.value = new Set()

  document.addEventListener('mousemove', onGlobalMouseMove)
  document.addEventListener('mouseup', onGlobalMouseUp)
}

function onGlobalMouseMove(event) {
  if (!dragState.value.active) return

  const dx = Math.abs(event.clientX - dragState.value.startClientX)
  const dy = Math.abs(event.clientY - dragState.value.startClientY)

  if (dx > 3 || dy > 3) {
    dragState.value.hasMoved = true
  }

  const targetElement = document.elementFromPoint(event.clientX, event.clientY)
  if (targetElement) {
    const cell = targetElement.closest('.cell')
    if (cell && cell.dataset.date) {
      if (dragState.value.type === 'new') {
        touchedDates.value.add(cell.dataset.date)
      } else {
        dragState.value.currentDate = cell.dataset.date
        dragState.value.currentProdLineId = Number(cell.dataset.lineId)
      }
    }
  }
}

function onGlobalMouseUp(event) {
  document.removeEventListener('mousemove', onGlobalMouseMove)
  document.removeEventListener('mouseup', onGlobalMouseUp)

  if (!dragState.value.active) return

  try {
    if (dragState.value.type === 'new') {
      const dates = dragState.value.hasMoved 
        ? [...touchedDates.value].sort()
        : [dragState.value.startDate]

      let lastCreatedSpanId = null

      dates.forEach((date) => {
        const spanId = Date.now() + Math.random()
        lastCreatedSpanId = spanId

        emit('create', {
          lineId: props.selectedLine?.lineId,
          prodLineId: dragState.value.startProdLineId,
          date: date,
          quantity: 1000,
          days: 1,
          spanId: spanId,
          _isNew: true
        })
      })

      if (dates.length === 1) {
        ElMessage.success(`已在 ${dates[0]} 创建草稿`)
      } else {
        ElMessage.success(`已在 ${dates.length} 天创建 ${dates.length} 个草稿块`)
      }

      setTimeout(() => {
        if (lastCreatedSpanId) {
          const newSpan = props.spans.find(s => s.spanId === lastCreatedSpanId)
          if (newSpan) {
            emit('select', newSpan)
          }
        }
      }, 100)

    } else if (dragState.value.type === 'move' && dragState.value.span) {
      if (!dragState.value.hasMoved) {
        emit('select', dragState.value.span)
      } else {
        const targetElement = document.elementFromPoint(event.clientX, event.clientY)
        if (targetElement) {
          const cell = targetElement.closest('.cell')
          if (cell && cell.dataset.date) {
            const newDate = cell.dataset.date
            const newProdLineId = Number(cell.dataset.lineId)

            emit('move', {
              spanId: dragState.value.span.spanId,
              date: newDate,
              prodLineId: newProdLineId
            })
            ElMessage.success('已移动排产块')
          }
        }
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    dragState.value = {
      active: false,
      type: null,
      startProdLineId: null,
      startDate: null,
      startClientX: 0,
      startClientY: 0,
      hasMoved: false,
      span: null,
      currentProdLineId: null
    }
    touchedDates.value = new Set()
  }
}

function onBodyScroll(event) {
  if (dateHeadersRef.value) {
    dateHeadersRef.value.scrollLeft = event.target.scrollLeft
  }
}

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', onGlobalMouseMove)
  document.removeEventListener('mouseup', onGlobalMouseUp)
})
</script>

<style scoped>
.gantt-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
  background: #fafafa;
  position: relative;
  user-select: none;
}
.gantt-header {
  display: flex;
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
  border-bottom: 2px solid #e4e7ed;
}
.gantt-corner {
  width: 120px;
  flex-shrink: 0;
  padding: 8px 16px;
  font-weight: 600;
  font-size: 13px;
  color: #606266;
  border-right: 2px solid #e4e7ed;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.date-headers {
  display: flex;
  flex: 1;
  overflow-x: hidden;
}
.date-cell {
  min-width: 70px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #606266;
  border-right: 1px solid #f0f0f0;
  position: relative;
  flex-shrink: 0;
}
.date-cell.is-today {
  color: #f56c6c;
  font-weight: 700;
  background: #fef0f0;
}
.date-cell.is-weekend {
  color: #c0c4cc;
  background: #fafafa;
}
.date-cell.has-deadline::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 20%;
  right: 20%;
  height: 3px;
  background: #f56c6c;
  border-radius: 2px;
}
.gantt-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: auto;
}
.gantt-row {
  display: flex;
  min-height: 52px;
  border-bottom: 1px solid #ebeef5;
}
.row-label {
  width: 120px;
  flex-shrink: 0;
  padding: 14px 16px;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
  border-right: 2px solid #e4e7ed;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.row-cells {
  display: flex;
  flex: 1;
  position: relative;
}
.cell {
  min-width: 70px;
  height: 100%;
  border-right: 1px solid #f0f0f0;
  position: relative;
  cursor: crosshair;
  transition: background 0.15s;
  flex-shrink: 0;
}
.cell:hover { background: #ecf5ff; }
.cell.is-today { background: #fef0f0; }
.cell.is-weekend { background: #fafafa; }
.cell.has-deadline { background: #fef0f0; }

.span-block {
  position: absolute;
  left: 1px;
  right: 1px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  padding: 4px 6px;
  transition: all 0.15s;
  box-sizing: border-box;
}
.span-block:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  z-index: 3;
}
.span-block.span-selected {
  border-left: 4px solid #67c23a;
  border-radius: 0 6px 6px 0;
  padding-left: 8px;
  z-index: 4;
}
.span-block.span-matched:not(.span-selected) {
  border-left: 4px solid #f56c6c;
  border-radius: 0 6px 6px 0;
  padding-left: 8px;
  z-index: 3;
}
.span-status-0 { 
  background: #f4f4f5; 
  border: 2px dashed #c0c4cc; 
  color: #909399; 
  cursor: grab; 
}
.span-status-0:active { cursor: grabbing; }
.span-status-1 { 
  background: #409eff; 
  border: 1px solid #337ecc; 
  color: #fff; 
}
.span-status-2 { 
  background: #e6a23c; 
  border: 1px solid #b88230; 
  color: #fff; 
}
.span-status-3 { 
  background: #909399; 
  border: 1px solid #73767a; 
  color: #fff; 
  opacity: 0.8;
}
.span-name {
  font-size: 11px;
  font-weight: 700;
  line-height: 1.3;
  text-align: center;
  width: 100%;
  word-break: break-word;
  margin-bottom: 2px;
}
.span-qty {
  font-size: 13px;
  font-weight: 600;
  line-height: 1.2;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}
.empty-gantt {
  text-align: center;
  padding: 60px 20px;
  color: #c0c4cc;
  font-size: 14px;
}
</style>