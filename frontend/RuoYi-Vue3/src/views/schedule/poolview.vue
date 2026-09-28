<template>
  <div class="pool-view">
    <div class="pool-toolbar">
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable size="default" style="width: 140px;">
        <el-option label="全部" value="" />
        <el-option label="待排程" :value="0" />
        <el-option label="已排程" :value="1" />
        <el-option label="生产中" :value="2" />
        <el-option label="已完成" :value="3" />
      </el-select>

      <el-select v-model="filterCustomer" placeholder="客户筛选" clearable size="default" style="width: 160px;">
        <el-option 
          v-for="c in customerList" 
          :key="c" 
          :label="c" 
          :value="c" 
        />
      </el-select>

      <div class="toolbar-stats">
        <el-tag type="warning">待排产: {{ filteredLines.length }} 条</el-tag>
        <el-tag type="danger">未排满: {{ unfilledCount }} 条</el-tag>
      </div>
    </div>

    <div class="pool-grid">
      <div
        v-for="line in filteredLines"
        :key="line.lineId"
        class="pool-card-expanded"
      >
        <div class="card-main">
          <div class="card-left">
            <span 
              class="status-dot"
              :class="'status-' + (line.status || 0)"
            ></span>
            <div class="card-title">
              <h3>{{ line.productName }}</h3>
              <p class="customer">{{ line.customer }}</p>
            </div>
          </div>

          <div class="card-status">
            <el-tag :type="getSpanStatusType(line.status)" size="large">
              {{ getSpanStatusText(line.status) }}
            </el-tag>
          </div>
        </div>

        <div class="card-details">
          <div class="detail-row">
            <div class="detail-item">
              <span class="label">订单总量</span>
              <span class="value primary">{{ formatNum(line.totalQuantity) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">已排数量</span>
              <span class="value success">{{ formatNum(line.scheduledQty || 0) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">剩余</span>
              <span class="value warning">{{ formatNum((line.totalQuantity || 0) - (line.scheduledQty || 0)) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">预测</span>
              <span class="value">{{ formatNum(line.forecastQuantity) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">库存</span>
              <span class="value">{{ formatNum(line.openingInventory) }}</span>
            </div>
          </div>

          <div class="progress-section">
            <div class="progress-bar-large">
              <div 
                class="progress-fill"
                :style="{ width: getScheduledPercent(line) + '%' }"
                :class="{ warning: getScheduledPercent(line) < 100, complete: getScheduledPercent(line) >= 100 }"
              ></div>
            </div>
            <span class="progress-text">{{ getScheduledPercent(line) }}%</span>
          </div>

          <div v-if="line.deliveryList?.length" class="delivery-nodes">
            <span class="nodes-label">交货节点:</span>
            <div class="nodes-list">
              <span
                v-for="(d, idx) in line.deliveryList"
                :key="idx"
                :class="['node-chip', { urgent: isUrgent(d.deliveryDate), completed: d.status === 1 }]"
              >
                {{ d.deliveryDate }} {{ d.planQuantity }}件
              </span>
            </div>
          </div>

          <div v-if="(line.totalQuantity || 0) > (line.scheduledQty || 0)" class="unfilled-banner">
            ⚠ 未排满：还需排产 {{ formatNum((line.totalQuantity || 0) - (line.scheduledQty || 0)) }} 件
          </div>
        </div>
      </div>

      <div v-if="filteredLines.length === 0" class="empty-pool">
        <el-empty description="暂无符合条件的待排产任务" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { formatNum, getSpanStatusText, getSpanStatusType } from './ScheduleEdit/utils.js'

const props = defineProps({
  boardData: { type: Object, default: () => ({}) },
  yearMonth: { type: String, default: '' }
})

const filterStatus = ref('')
const filterCustomer = ref('')

const poolLines = computed(() => props.boardData.poolLines || [])

const customerList = computed(() => {
  const customers = new Set(poolLines.value.map(l => l.customer).filter(Boolean))
  return [...customers].sort()
})

const filteredLines = computed(() => {
  let result = [...poolLines.value]
  
  if (filterStatus.value !== '') {
    result = result.filter(l => l.status === Number(filterStatus.value))
  }
  
  if (filterCustomer.value) {
    result = result.filter(l => l.customer === filterCustomer.value)
  }
  
  return result.sort((a, b) => {
    const dateA = a.earliestDelivery || ''
    const dateB = b.earliestDelivery || ''
    return dateA.localeCompare(dateB)
  })
})

const unfilledCount = computed(() => {
  return filteredLines.value.filter(l => 
    (l.totalQuantity || 0) > (l.scheduledQty || 0)
  ).length
})

function isUrgent(dateStr) {
  if (!dateStr) return false
  const targetDate = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.ceil((targetDate - now) / (1000 * 60 * 60 * 24))
  return diffDays <= 7 && diffDays >= 0
}

function getScheduledPercent(line) {
  const total = line.totalQuantity || 0
  if (total === 0) return 0
  const scheduled = line.scheduledQty || 0
  return Math.min(100, Math.round((scheduled / total) * 100))
}
</script>

<style scoped>
.pool-view { height: 100%; display: flex; flex-direction: column; overflow: hidden; }
.pool-toolbar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  margin-bottom: 16px;
}
.toolbar-stats { margin-left: auto; display: flex; gap: 8px; }
.pool-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(480px, 1fr));
  gap: 16px;
  padding: 4px;
}
.pool-card-expanded {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}
.pool-card-expanded:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}
.card-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
.card-left { display: flex; align-items: center; gap: 12px; }
.status-dot { width: 12px; height: 12px; border-radius: 50%; box-shadow: 0 0 8px currentColor; }
.status-dot.status-0 { background: #fff; opacity: 0.6; }
.status-dot.status-1 { background: #67c23a; }
.status-dot.status-2 { background: #e6a23c; }
.status-dot.status-3 { background: #909399; }
.card-title h3 { margin: 0 0 4px; font-size: 18px; font-weight: 600; }
.card-title .customer { margin: 0; font-size: 13px; opacity: 0.9; }
.card-details { padding: 20px; }
.detail-row { display: flex; gap: 24px; margin-bottom: 20px; }
.detail-item { display: flex; flex-direction: column; gap: 4px; }
.detail-item .label { font-size: 12px; color: #909399; }
.detail-item .value { font-size: 18px; font-weight: 700; color: #303133; }
.detail-item .value.primary { color: #409eff; }
.detail-item .value.success { color: #67c23a; }
.detail-item .value.warning { color: #e6a23c; }
.progress-section { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.progress-bar-large { flex: 1; height: 10px; background: #ebeef5; border-radius: 5px; overflow: hidden; }
.progress-fill { height: 100%; background: #67c23a; transition: width 0.3s; border-radius: 5px; }
.progress-fill.warning { background: #e6a23c; }
.progress-fill.complete { background: #67c23a; }
.progress-text { font-size: 14px; font-weight: 600; color: #606266; min-width: 45px; }
.delivery-nodes { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 12px; }
.nodes-label { font-size: 12px; color: #909399; white-space: nowrap; padding-top: 2px; }
.nodes-list { display: flex; flex-wrap: wrap; gap: 6px; }
.node-chip {
  padding: 4px 10px;
  background: #f0f2f5;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  border: 1px solid #e4e7ed;
}
.node-chip.urgent { background: #fef0f0; border-color: #f56c6c; color: #f56c6c; }
.node-chip.completed { background: #f0f9eb; border-color: #67c23a; color: #67c23a; text-decoration: line-through; opacity: 0.7; }
.unfilled-banner {
  padding: 10px 14px;
  background: #fdf6ec;
  border: 1px solid #faecd8;
  border-radius: 6px;
  color: #e6a23c;
  font-size: 13px;
  font-weight: 500;
  text-align: center;
}
.empty-pool { grid-column: 1 / -1; padding: 40px; }
</style>