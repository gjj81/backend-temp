<template>
  <div class="pool-panel">
    <div class="panel-header">
      <span class="panel-title">月度销售计划 | 按交货日期排序</span>
    </div>

    <div class="pool-list">
      <div
        v-for="line in lines"
        :key="line.lineId"
        :class="['pool-card', { selected: line.lineId === selectedId }]"
        @click="$emit('select', line)"
      >
        <div class="card-header">
          <span 
            class="status-dot"
            :class="'status-' + (line.status || 0)"
          ></span>
          <span class="product-name">{{ line.productName }}</span>
          <el-tag 
            :type="getLineStatusType(line.status)" 
            size="small" 
            effect="plain"
          >
            {{ getLineStatusText(line.status) }}
          </el-tag>
        </div>

        <div class="card-meta">
          <span>{{ line.customer }}</span>
          <span v-if="line.planMonth">创建月份 {{ line.planMonth }}</span>
          <span>预测 {{ formatNum(line.forecastQuantity) }}</span>
          <span>库存 {{ formatNum(line.openingInventory) }}</span>
        </div>

        <div class="card-qty">
          订单 {{ formatNum(line.totalQuantity) }} 已排 {{ formatNum(line.scheduledQuantity || 0) }} 剩余 {{ formatNum((line.totalQuantity || 0) - (line.scheduledQuantity || 0)) }}
        </div>

        <div v-if="line.deliveryList?.length" class="delivery-chips">
          <span
            v-for="(d, idx) in line.deliveryList"
            :key="idx"
            :class="['chip', { urgent: isUrgent(d.deliveryDate), done: d.status === 1 }]"
          >
            {{ d.deliveryDate }} {{ d.planQuantity.toLocaleString() }}
          </span>
        </div>

        <div v-if="(line.totalQuantity || 0) > (line.scheduledQty || 0)" class="unfilled-warning">
          ⚠ 未排满
        </div>
      </div>

      <div v-if="lines.length === 0" class="empty-pool">
        暂无待排产任务
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatNum, getLineStatusText, getLineStatusType } from './utils.js'

defineProps({
  lines: { type: Array, default: () => [] },
  selectedId: { type: [String, Number], default: null }
})

defineEmits(['select'])

function isUrgent(dateStr) {
  if (!dateStr) return false
  const targetDate = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.ceil((targetDate - now) / (1000 * 60 * 60 * 24))
  return diffDays <= 7 && diffDays >= 0
}
</script>

<style scoped>
.pool-panel {
  width: 300px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.panel-header {
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}
.panel-title { font-size: 12px; color: #909399; font-weight: 500; }
.pool-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.pool-card {
  padding: 10px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafafa;
}
.pool-card:hover { border-color: #409eff; background: #ecf5ff; }
.pool-card.selected { border-color: #409eff; background: #ecf5ff; box-shadow: 0 0 0 2px rgba(64,158,255,0.2); }
.card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; flex-wrap: wrap; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.status-dot.status-0 { background: #909399; }
.status-dot.status-1 { background: #409eff; }
.status-dot.status-2 { background: #e6a23c; }
.status-dot.status-3 { background: #f56c6c; }
.status-dot.status-4 { background: #67c23a; }
.product-name { font-size: 13px; font-weight: 600; color: #303133; }
.card-meta { display: flex; gap: 10px; margin-bottom: 6px; font-size: 11px; color: #909399; flex-wrap: wrap; }
.card-qty { font-size: 12px; color: #606266; margin-bottom: 6px; line-height: 1.5; }
.delivery-chips { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 6px; }
.chip {
  padding: 2px 6px;
  background: #f0f2f5;
  border-radius: 3px;
  font-size: 11px;
  color: #606266;
  border: 1px solid #e4e7ed;
}
.chip.urgent { background: #fef0f0; border-color: #f56c6c; color: #f56c6c; }
.chip.done { background: #f0f9eb; border-color: #67c23a; color: #67c23a; text-decoration: line-through; opacity: 0.7; }
.unfilled-warning { font-size: 11px; color: #e6a23c; text-align: center; font-weight: 500; }
.empty-pool { text-align: center; padding: 40px 20px; color: #c0c4cc; font-size: 13px; }
</style>