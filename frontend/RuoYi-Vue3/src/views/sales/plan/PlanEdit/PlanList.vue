<template>
  <div class="right-list">
    <div v-for="plan in planList" :key="plan.planId" class="group-card">
      <!-- 分组头 -->
      <div class="group-header">
        <div style="display: flex; align-items: center; gap: 8px;">
          <el-icon v-if="plan.status !== 0" color="#f56c6c" :size="16">
            <Lock />
          </el-icon>
          <el-icon v-else color="#67c23a" :size="16">
            <Unlock />
          </el-icon>

          <span 
            class="group-name" 
            @click="handleLoadPlan(plan)"
            :title="plan.status === 0 ? '点击编辑整个计划' : '计划已锁定'"
            :style="{ cursor: plan.status === 0 ? 'pointer' : 'not-allowed', opacity: plan.status !== 0 ? 0.6 : 1 }"
          >
            {{ plan.customer }}
          </span>

          <span class="plan-no-tag" :title="`计划编号: ${plan.planNo}`">
            {{ plan.planNo }}
          </span>

          <el-tag 
            :type="plan.status === 0 ? 'success' : 'danger'" 
            size="small"
            effect="dark"
          >
            {{ plan.status === 0 ? '待确认' : '已确认' }}
          </el-tag>
        </div>

        <div style="display: flex; gap: 4px; align-items: center;">
          <el-tooltip content="添加新产品" placement="top">
            <el-button 
              type="primary" 
              link 
              size="small"
              @click.stop="handleOpenAddProduct(plan)"
              :disabled="plan.status !== 0"
            >
              ➕
            </el-button>
          </el-tooltip>

          <el-tooltip content="删除此计划" placement="top">
            <el-button 
              type="danger" 
              link 
              size="small"
              class="btn-delete-plan"
              @click.stop="handleDeletePlan(plan)"
              :disabled="plan.status !== 0"
              title="删除该客户的所有计划"
            >
              🗑️
            </el-button>
          </el-tooltip>
        </div>
      </div>

      <!-- 表格 -->
      <div class="plan-table">
        <div class="table-header">
          <span class="col-part">零件</span>
          <span class="col-qty">订单量</span>
          <span class="col-date">交货日期和数量</span>
          <span class="col-qty">预测</span>
          <span class="col-qty">库存</span>
          <span class="col-qty">状态</span>
          <span class="col-qty">删除</span>
        </div>

        <div class="table-row">
          <div 
            v-for="(line, lineIdx) in plan.lineList" 
            :key="line.lineId"
            :class="['row-line', { 
              'row-line-first': lineIdx === 0,
              'locked': line.status !== 0 || plan.status !== 0
            }]"
            @click="handleOpenEditProduct(plan, line, lineIdx)"
            :title="(line.status === 0 && plan.status === 0) ? '点击编辑此产品' : '当前状态不允许编辑'"
          >
            <span 
              class="col-part" 
              :title="line.productName"
              :style="{ cursor: (line.status === 0 && plan.status === 0) ? 'pointer' : 'not-allowed' }"
            >
              <el-icon v-if="line.status !== 0 || plan.status !== 0" color="#f56c6c" :size="12" style="margin-right: 4px;">
                <Lock />
              </el-icon>
              {{ line.productName }}
            </span>

            <span class="col-qty">{{ formatNum(line.totalQuantity) }}</span>

            <span class="col-date">
              <el-button
                v-if="line.status === 0 && plan.status === 0"
                type="primary"
                link
                size="small"
                class="btn-add-delivery"
                @click.stop="handleAddDelivery(line, plan)"
                title="新增交货节点"
              >
                +
              </el-button>
              <template v-if="line.deliveryList?.length">
                <span 
                  v-for="d in line.deliveryList" 
                  :key="d.deliveryId"
                  class="date-tag"
                  :style="formatDateTag(d).style"
                  @click.stop="handleDateClick(d, line, plan)"
                >
                  {{ formatDateTag(d).text }}
                </span>
              </template>
            </span>

            <span class="col-qty">{{ formatNum(line.forecastQuantity) }}</span>
            <span class="col-qty">{{ formatNum(line.openingInventory) }}</span>

            <span class="col-qty">
              <el-tag 
                :type="line.status === 0 ? 'success' : 'danger'" 
                size="small"
                effect="plain"
              >
                {{ line.status === 0 ? '待生产' : '生产中' }}
              </el-tag>
            </span>

            <span class="col-qty action-col">                
              <button 
                class="btn-delete-product"
                @click.stop="handleDeleteProduct(plan, line, lineIdx)"
                :disabled="line.status !== 0 || plan.status !== 0"
                v-show="line.status === 0 && plan.status === 0"
                title="删除此产品"
              >×</button>
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="planList.length === 0" class="empty-state">
      暂无数据，请在左侧添加计划
    </div>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatNum, formatDateTag } from './utils.js'
import { SalesPlanVO, SalesPlanLineVO, SalesPlanDeliveryVO } from '@/types/sales-plan'

const props = defineProps({
  planList: { type: Array, default: () => [] }
})

const emit = defineEmits([
  'load-plan',
  'delete-plan',
  'delete-product',
  'open-edit-product',
  'open-add-product',
  'add-delivery',   
  'date-click'
])

// ========== 加载计划到左侧编辑 ==========
function handleLoadPlan(plan) {
  if (plan.status !== 0) {
    ElMessage.warning(`该计划已确认（状态=${plan.status}），无法编辑`)
    return
  }
  emit('load-plan', plan) // 发射事件到父组件
}

// ========== 删除计划 ==========
function handleDeletePlan(plan) {
  console.log(plan.lineList)
  ElMessageBox.confirm(
    `确定要删除客户 "${plan.customer}" 的所有计划吗？此操作不可撤销！`,
    '删除确认',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    emit('delete-plan', plan)
    ElMessage.success(`已删除客户 "${plan.customer}" 的所有计划`)
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// ========== 删除产品 ==========
async function handleDeleteProduct(plan, line, lineIdx) {
  if (plan.status !== 0) {
    ElMessage.warning(`父级计划已锁定（状态=${plan.status}），无法删除产品`)
    return
  }

  if (line.status !== 0) {
    ElMessage.warning(`产品"${line.productName}"状态为${line.status}，无法删除`)
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除产品 "${line.productName}" 吗？\n\n⚠️ 注意：该产品的所有交货节点也将被删除！`,
      '删除确认',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )

    // 直接从本地数组移除
    plan.lineList.splice(lineIdx, 1)

    ElMessage.success(`已删除产品 "${line.productName}"`)
    emit('delete-product', { plan, line, lineIdx })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error)
      ElMessage.error(error.message || '删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

// ========== 打开产品编辑弹窗 ==========
function handleOpenEditProduct(plan, line, lineIdx) {
  if (plan.status !== 0) {
    ElMessage.warning(`父级计划已锁定（状态=${plan.status}），无法编辑产品`)
    return
  }
  if (line.status !== 0) {
    ElMessage.warning(`产品"${line.productName}"状态为${line.status}，无法编辑`)
    return
  }
  emit('open-edit-product', { plan, line, lineIdx })
}

// ========== 打开产品新增弹窗 ==========
function handleOpenAddProduct(plan) {
  if (plan.status !== 0) {
    ElMessage.warning(`父级计划已锁定（状态=${plan.status}），无法新增产品`)
    return
  }
  emit('open-add-product', plan)
}

// ========== 点击交货标签 ==========
function handleDateClick(delivery, line, plan) {
  emit('date-click', { delivery, line, plan })
}

// ========== 新增交货节点 ==========

/** 打开新增交货节点弹窗 */
function handleAddDelivery(line, plan) {
  // 状态检查
  if (line.status !== 0) {
    ElMessage.warning('该产品已锁定，无法新增交货节点')
    return
  }
  if (plan.status !== 0) {
    ElMessage.warning('父级计划已锁定，无法新增交货节点')
    return
  }
  
  // 向上发射事件
  emit('add-delivery', { line, plan })
}
</script>

<style scoped>
.right-list {
  flex: 1;
  overflow-x: auto;
  overflow-y: auto;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 12px;
}
.group-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.06);
  overflow: hidden;
}
.group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}
.group-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  cursor: pointer;
  transition: color 0.2s;
}
.group-name:hover {
  color: #2563eb;
}
.plan-no-tag{
  font-size: 11px;
  color: #9ca3af;
}
.plan-table {
  font-size: 13px;
}
.table-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  gap: 12px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 12px;
  font-weight: 500;
}
.table-row {
  position: relative;
  cursor: pointer;
  transition: all 0.15s;
}
.table-row:hover {
  background: #fafafa;
}
.row-line {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  gap: 12px;
  border-bottom: 1px solid #f5f5f5;
}
.row-line:not(:last-child) {
  border-bottom-style: dashed;
}
.row-line-first {
  padding-top: 14px;
}
.col-part {
  min-width: 100px;
  flex-shrink: 0;
  font-weight: 500;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.col-qty {
  min-width: 60px;
  flex-shrink: 0;
  text-align: right;
  color: #374151;
}
.col-date {
  flex: 1;
  min-width: 180px;
  max-width: 500px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
}
.table-header .col-date {
  justify-content: center;
  text-align: center;
}
.date-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 4px;
  font-size: 11px;
  white-space: nowrap;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
  font-size: 14px;
}
.row-line.locked {
  opacity: 0.6;
  background: #fef2f2;
}
.action-col {
  display: flex;
  gap: 4px;
  justify-content: center;
  align-items: center;
}
.btn-delete-product {
  width: 20px;
  height: 20px;
  border: none;
  background: transparent;
  color: #ef4444;
  font-size: 18px;
  cursor: pointer;
  border-radius: 4px;
  line-height: 1;
  opacity: 0.6;
  transition: all 0.15s;
}

.btn-delete-product:hover {
  opacity: 1;     
  background: #fef2f2;
}
.btn-delete-product:disabled {
  color: #d1d5db;
  cursor: not-allowed;
  opacity: 0;
}
</style>
