<template>
  <div class="info-bar">
    <div class="info-main">
      <span class="product-name">{{ lineData.productName }}</span>
      <el-tag :type="getSpanStatusType(lineData.status)" size="small">
        {{ getSpanStatusText(lineData.status) }}
      </el-tag>
      <span class="customer">{{ lineData.customer }}</span>
      <span class="divider">|</span>
      <span class="qty-info">订单 {{ formatNum(lineData.totalQuantity) }} 已排 {{ formatNum(lineData.scheduledQty || 0) }} 草稿 {{ draftCount }}</span>
    </div>

    <div v-if="lineData.deliveryList?.length" class="delivery-ref">
      <span class="ref-label">交货节点（按产参）：</span>
      <span 
        v-for="(d, idx) in lineData.deliveryList" 
        :key="idx"
        :class="['ref-date', { urgent: isUrgent(d.deliveryDate) }]"
      >
        {{ d.deliveryDate }}前 {{ d.planQuantity.toLocaleString() }}
      </span>
    </div>

    <div class="tip-text">
      选中产品后，在下方产线上<strong>单击</strong>排 1 天，或<strong>按住拖动</strong>跨天（可拖到别的产线）
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { formatNum, getSpanStatusText, getSpanStatusType } from './utils.js'

const props = defineProps({
  lineData: { type: Object, required: true }
})

const draftCount = computed(() => {
  return props.lineData.draftCount || 0
})

function isUrgent(dateStr) {
  if (!dateStr) return false
  const targetDate = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.ceil((targetDate - now) / (1000 * 60 * 60 * 24))
  return diffDays <= 7 && diffDays >= 0
}
</script>

<style scoped>
.info-bar {
  flex-shrink: 0;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}
.info-main {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.product-name { 
  font-size: 15px; 
  font-weight: 600; 
  color: #303133; 
}
.customer { 
  color: #606266; 
  font-size: 13px;
}
.divider {
  color: #dcdfe6;
}
.qty-info { 
  color: #909399; 
  font-size: 12px; 
}
.delivery-ref { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  padding: 8px 12px; 
  background: #f5f7fa; 
  border-radius: 4px; 
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.ref-label { 
  font-size: 12px; 
  color: #909399; 
  white-space: nowrap; 
}
.ref-date { 
  padding: 2px 8px; 
  background: #ecf5ff; 
  border-radius: 4px; 
  font-size: 12px; 
  color: #409eff;
  border: 1px solid #d9ecff;
}
.ref-date.urgent { 
  background: #fef0f0; 
  color: #f56c6c; 
  border-color: #fde2e2;
}
.tip-text { 
  font-size: 12px; 
  color: #c0c4cc; 
  text-align: center;
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
}
.tip-text strong {
  color: #409eff;
}
</style>