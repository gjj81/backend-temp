<template>
  <div class="schedule-edit-page">
    <!-- 左侧：待排产池 -->
    <PoolPanel
      :lines="filteredPoolLines"
      :selected-id="selectedLineId"
      @select="handleSelectLine"
    />

    <!-- 右侧：甘特图区域 -->
    <div class="gantt-area">
      <!-- 选中行信息条 -->
      <InfoBar
        v-if="selectedLine"
        :line-data="selectedLine"
        @scheduled="handleMarkScheduled"
      />

      <!-- 编辑条（选中任意块时都显示，包括已下发的） -->
      <EditBar
        v-if="selectedSpan"
        :span-data="selectedSpan"
        :line-data="selectedLine"
        @save="handleSaveSpan"
        @delete="handleDeleteSpan"
        @issue="handleIssueSpan"
        @close="selectedSpan = null"
      />

      <!-- 甘特图 -->
      <GanttChart
        :spans="filteredSpans"
        :prod-lines="boardData.prodLines || []"
        :year-month="yearMonth"
        :selected-line="selectedLine"
        :selected-span-id="selectedSpan?.spanId"
        :delivery-dates="deliveryDateSet"
        :board-data="boardData"
        @create="handleCreateSpan"
        @move="handleMoveSpan"
        @select="handleSelectSpan"
      />

      <!-- 底部提示 -->
      <div class="bottom-tips">
        📌 产线操作：单击/拖拽空白格 = 创建1天排产（最小单位=1天）| 拖拽草稿块 = 移动<br>
        📌 块操作：点击任意块 = 查看/编辑详情 | 未下发可修改数量和日期 | 支持单独下发
      </div>
    </div>
  </div>
</template>

<script setup name="ScheduleEdit">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import PoolPanel from './PoolPanel.vue'
import GanttChart from './GanttChart.vue'
import EditBar from './EditBar.vue'
import InfoBar from './InfoBar.vue'

const props = defineProps({
  boardData: { type: Object, default: () => ({}) },
  yearMonth: { type: String, default: '' },
  workshopId: { type: [Number, String], default: null },
  customerFilter: { type: String, default: '' }
})

const emit = defineEmits(['save', 'delete', 'select-line'])

const selectedLineId = ref(null)
const selectedSpan = ref(null)

const poolLines = computed(() => props.boardData.poolLines || [])
const allSpans = computed(() => props.boardData.spans || [])

const filteredPoolLines = computed(() => {
  let result = [...poolLines.value]
  if (props.customerFilter) {
    result = result.filter(l => l.customer === props.customerFilter)
  }
  return result.sort((a, b) => {
    const dateA = a.earliestDelivery || ''
    const dateB = b.earliestDelivery || ''
    return dateA.localeCompare(dateB)
  })
})

const filteredSpans = computed(() => {
  if (!props.customerFilter) return allSpans.value
  const lineIds = new Set(filteredPoolLines.value.map(l => l.lineId))
  return allSpans.value.filter(s => lineIds.has(s.lineId))
})

const selectedLine = computed(() => 
  poolLines.value.find(l => l.lineId === selectedLineId.value) || null
)

const deliveryDateSet = computed(() => {
  if (!selectedLine.value?.deliveryList) return new Set()
  return new Set(
    selectedLine.value.deliveryList
      .filter(d => d.status === 1)
      .map(d => d.deliveryDate)
  )
})

function handleSelectLine(line) {
  selectedLineId.value = line?.lineId || null
  selectedSpan.value = null
  emit('select-line', line)
}

function handleSelectSpan(span) {
  selectedSpan.value = span
  if (span.lineId) {
    selectedLineId.value = span.lineId
  }
}

function handleCreateSpan(data) {
  const newSpan = {
    spanId: Date.now(),
    ...data,
    status: 0,
    version: 1
  }
  emit('save', { type: 'span:add', data: newSpan })
}

function handleMoveSpan(data) {
  emit('save', { type: 'span:move', data })
}

function handleSaveSpan(data) {
  const spanIndex = allSpans.value.findIndex(s => s.spanId === data.spanId)
  if (spanIndex !== -1) {
    const span = allSpans.value[spanIndex]
    const wasDraft = span.status === 0

    Object.assign(span, {
      dailyQuantity: data.dailyQuantity,
      days: data.days || 1,
      startDate: data.startDate,
      _isNew: false,
      status: wasDraft ? 1 : span.status
    })

    if (wasDraft) {
      span.endDate = span.startDate
      span.days = 1
    }

    emit('save', { type: 'span:update', data: { ...span } })
  }
}

function handleDeleteSpan(data) {
  emit('delete', { type: 'span', data })
  selectedSpan.value = null
}

function handleIssueSpan(data) {
  const spanIndex = allSpans.value.findIndex(s => s.spanId === data.spanId)
  if (spanIndex !== -1) {
    const span = allSpans.value[spanIndex]
    if (span.status === 1) {
      span.status = 2
      ElMessage.success('排产块已下发到生产')
      emit('save', { type: 'span:update', data: { ...span } })
    } else {
      ElMessage.warning('仅"未下发"状态的块可以下发')
    }
  }
  selectedSpan.value = null
}

function handleMarkScheduled() {
  if (selectedLine.value) {
    ElMessage.info(`已标记 ${selectedLine.value.productName} 为已排程`)
  }
}
</script>

<style scoped>
.schedule-edit-page {
  display: flex;
  gap: 16px;
  height: 100%;
  overflow: hidden;
}
.gantt-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.bottom-tips {
  flex-shrink: 0;
  padding: 10px 20px;
  background: #f5f7fa;
  border-top: 1px solid #e4e7ed;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>