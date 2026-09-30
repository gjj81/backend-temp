<template>
  <div class="shop-kanban">
    <div class="kanban-header">
      <span class="header-left">待开工（排产 = 1 已确认）</span>
      <span class="header-right">生产中（排产 = 2）</span>
    </div>

    <div class="kanban-body">
      <!-- 待开工列 -->
      <div class="kanban-col">
        <div 
          v-for="card in pendingCards" 
          :key="card.spanId" 
          class="kanban-card"
        >
          <div class="card-title">{{ card.productName }}</div>
          <div class="card-info">
            <span>{{ card.prodLineName }}·{{ card.startDate }}起 {{ card.days }}天</span>
            <span>{{ card.customer }}</span>
            <span v-if="card.hasData" class="has-data-tag">有填报数据</span>
          </div>
          <div class="card-footer">
            <span class="card-qty">{{ card.dailyQuantity * card.days.toLocaleString() }} 件</span>
            <div class="card-actions">
              <el-button size="small" type="primary" @click="handleStart(card)">开工</el-button>
              <el-button size="small" @click="handleCancel(card)" :disabled="card.hasData">撤销</el-button>
            </div>
          </div>
        </div>

        <div v-if="pendingCards.length === 0" class="empty-col">
          暂无待开工任务
        </div>
      </div>

      <!-- 生产中列 -->
      <div class="kanban-col">
        <div 
          v-for="card in producingCards" 
          :key="card.spanId" 
          class="kanban-card producing"
        >
          <div class="card-title">{{ card.productName }}</div>
          <div class="card-info">
            <span>{{ card.prodLineName }}·{{ card.startDate }}起 {{ card.days }}天</span>
            <span>{{ card.customer }}</span>
            <span v-if="card.hasData" class="has-data-tag">有填报数据</span>
          </div>
          <div class="card-footer">
            <span class="card-qty">{{ (card.dailyQuantity * card.days).toLocaleString() }} 件</span>
            <div class="card-actions">
              <el-button size="small" type="success" @click="handleFinish(card)">完工</el-button>
            </div>
          </div>
        </div>

        <div v-if="producingCards.length === 0" class="empty-col">
          暂无生产中任务
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ShopKanban">
import { computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  boardData: { type: Object, default: () => ({}) }
})

const allSpans = computed(() => props.boardData.spans || [])
const poolLines = computed(() => props.boardData.poolLines || [])

const pendingCards = computed(() => {
  return allSpans.value
    .filter(s => s.status === 1)
    .map(span => {
      const line = poolLines.value.find(l => l.lineId === span.lineId)
      return {
        ...span,
        productName: line?.productName || '未知产品',
        customer: line?.customer || '',
        hasData: Math.random() > 0.7
      }
    })
})

const producingCards = computed(() => {
  return allSpans.value
    .filter(s => s.status === 2)
    .map(span => {
      const line = poolLines.value.find(l => l.lineId === span.lineId)
      return {
        ...span,
        productName: line?.productName || '未知产品',
        customer: line?.customer || '',
        hasData: true
      }
    })
})

async function handleStart(card) {
  try {
    await ElMessageBox.confirm(`确定要开工 "${card.productName}" 吗？`, '开工确认', {
      confirmButtonText: '确认开工',
      cancelButtonText: '取消',
      type: 'info'
    })
    card.status = 2
    ElMessage.success('已开工')
  } catch (error) {
    // 取消
  }
}

async function handleFinish(card) {
  try {
    await ElMessageBox.confirm(`确定要完工 "${card.productName}" 吗？`, '完工确认', {
      confirmButtonText: '确认完工',
      cancelButtonText: '取消',
      type: 'success'
    })
    card.status = 3
    ElMessage.success('已完工')
  } catch (error) {
    // 取消
  }
}

async function handleCancel(card) {
  if (card.hasData) {
    ElMessage.warning('该任务已有填报数据，无法撤销')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要撤销 "${card.productName}" 吗？`, '撤销确认', {
      confirmButtonText: '确认撤销',
      cancelButtonText: '取消',
      type: 'warning'
    })
    card.status = 0
    ElMessage.success('已撤销')
  } catch (error) {
    // 取消
  }
}
</script>

<style scoped>
.shop-kanban { height: 100%; display: flex; flex-direction: column; overflow: hidden; background: #f5f7fa; padding: 16px 24px; }
.kanban-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 16px;
  padding: 12px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.header-left { font-size: 14px; font-weight: 600; color: #303133; }
.header-right { font-size: 14px; font-weight: 600; color: #e6a23c; }
.kanban-body { flex: 1; display: flex; gap: 24px; overflow: hidden; }
.kanban-col { 
  flex: 1; 
  background: #fff; 
  border-radius: 8px; 
  padding: 16px; 
  overflow-y: auto;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.kanban-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  transition: all 0.2s;
}
.kanban-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-color: #409eff; }
.kanban-card.producing { border-left: 4px solid #e6a23c; }
.card-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 10px; }
.card-info { 
  display: flex; 
  flex-direction: column; 
  gap: 6px; 
  margin-bottom: 12px; 
  font-size: 13px; 
  color: #606266; 
}
.has-data-tag { 
  display: inline-block; 
  padding: 2px 8px; 
  background: #ecf5ff; 
  color: #409eff; 
  border-radius: 4px; 
  font-size: 11px; 
  width: fit-content;
}
.card-footer { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  padding-top: 12px; 
  border-top: 1px solid #f0f0f0; 
}
.card-qty { font-size: 18px; font-weight: 700; color: #409eff; }
.card-actions { display: flex; gap: 8px; }
.empty-col { 
  text-align: center; 
  padding: 60px 20px; 
  color: #c0c4cc; 
  font-size: 14px; 
}
</style>