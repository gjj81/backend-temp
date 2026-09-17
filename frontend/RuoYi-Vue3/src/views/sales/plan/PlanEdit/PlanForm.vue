<template>
  <div class="left-panel">
    <div class="panel-card">
      <!-- 面板标题 -->
      <div class="panel-header">
        <span class="panel-title">
          <template v-if="editingId">
            编辑计划 - {{ editingData.customer || '未知客户' }}
          </template>
          <template v-else>
            新增销售计划
          </template>
        </span>
        <div style="display: flex; gap: 6px; align-items: center;">
          <el-tag 
            v-if="editingId && editingData.planStatus !== undefined"
            :type="editingData.planStatus === 0 ? 'success' : 'danger'"
            size="small"
          >
            {{ editingData.planStatus === 0 ? '可编辑' : '已锁定' }}
          </el-tag>
          <span 
            v-if="formData.lineList.length > 0"
            style="font-size: 11px; color: #6b7280; background: #f3f4f6; padding: 2px 8px; border-radius: 4px;"
          >
             {{ formData.lineList.length }} 个产品
          </span>
        </div>
      </div>

      <!-- 表单内容区 -->
      <div class="panel-form">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px" size="small" :key="formKey">
          <el-form-item label="客户" prop="customer">
            <el-input v-model="formData.customer" placeholder="请输入客户名称" />
          </el-form-item>
          <el-form-item label="计划名称" prop="planNo">
            <el-input v-model="formData.planNo" placeholder="请输入计划名称" />
          </el-form-item>

          <el-form-item label="计划月份" prop="yearMonth">
            <el-date-picker
              v-model="formData.yearMonth"
              type="month"
              placeholder="选择月份"
              format="YYYY-MM"
              value-format="YYYY-MM"
              style="width: 100%"
            />
          </el-form-item>

          <!-- 产品明细列表 -->
          <div class="section-title">产品明细</div>
          <div v-for="(line, lineIdx) in formData.lineList" :key="lineIdx" class="line-card">
            <div class="line-header">
              <span class="line-index">产品 #{{ lineIdx + 1 }}</span>
              <el-button 
              v-if="formData.lineList.length > 1"
              type="danger" 
              link 
              size="small"
              :disabled="!!(editingId && line.lineId)"
              @click="removeLine(lineIdx)"
              :style="{ opacity: editingId && line.lineId ? 0.4 : 1 }"
              :title="editingId && line.lineId ? '已保存的产品不允许删除' : '删除此产品'"
            >
              删除
            </el-button>
            </div>

            <el-form-item label="产品名称" :prop="`lineList[${lineIdx}].productName`" :rules="{ required: true, message: '请输入产品名称', trigger: 'blur' }">
              <el-input v-model="line.productName" placeholder="如：xxxx" />
            </el-form-item>

            <el-form-item label="订单总量" :prop="`lineList[${lineIdx}].totalQuantity`" :rules="{ required: true, message: '请输入订单总量', trigger: 'blur' }">
              <el-input-number v-model="line.totalQuantity" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-form-item label="预测数量">
              <el-input-number v-model="line.forecastQuantity" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-form-item label="期初库存">
              <el-input-number v-model="line.openingInventory" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>

            <!-- 交货计划 -->
            <div class="sub-section-title">交货计划</div>
            <div v-for="(delivery, dIdx) in line.deliveryList" :key="dIdx" class="delivery-row-vertical">
              <div class="delivery-header">
                <span class="delivery-index">交货 #{{ dIdx + 1 }}</span>
                  <el-button 
                    v-if="line.deliveryList.length > 1"
                    type="danger" 
                    link 
                    size="small"
                    :disabled="!!(editingId && delivery.deliveryId)"
                    @click="removeDelivery(lineIdx, dIdx)"
                    :style="{ opacity: editingId && delivery.deliveryId ? 0.4 : 1 }"
                    :title="editingId && delivery.deliveryId ? '已保存的交货节点不允许删除' : '删除此交货节点'"
                  >
                    删除
                  </el-button>
              </div>

              <el-form-item 
                label="交货日期" 
                :prop="`lineList[${lineIdx}].deliveryList[${dIdx}].deliveryDate`"
                :rules="{ required: true, message: '请选择日期', trigger: 'change' }"
              >
                <el-date-picker
                  v-model="delivery.deliveryDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item 
                label="交货数量" 
                :prop="`lineList[${lineIdx}].deliveryList[${dIdx}].planQuantity`"
                :rules="{ required: true, message: '请输入数量', trigger: 'blur' }"
              >
                <el-input-number v-model="delivery.planQuantity" :min="1" :precision="0" controls-position="right" style="width: 100%" />
              </el-form-item>
            </div>

            <el-button type="primary" link size="small" @click="addDelivery(lineIdx)">
              + 添加交货日期
            </el-button>
          </div>

          <el-button type="primary" link size="small" @click="addLine" style="margin-top: 8px; width: 100%;">
            + 添加产品
          </el-button>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div class="panel-actions">
        <div v-if="editingId" style="width: 100%; margin-bottom: 12px;">
          <el-alert
            v-if="editingData.planStatus !== 0"
            title="该计划已确认，无法修改基本信息"
            type="warning"
            :closable="false"
            show-icon
            style="margin-bottom: 8px;"
          />
        </div>

        <template v-if="editingId">
          <el-button 
            type="primary" 
            @click="handleSavePlan"
            :disabled="editingData.planStatus !== 0"
          >
            保存计划信息
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="handleAddPlan">提交新计划</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { SalesPlanVO, SalesPlanLineVO, SalesPlanDeliveryVO } from '@/types/sales-plan'
import { SalesPlanUpdateDTO, SalesPlanLineUpdateDTO,SalesPlanDeliveryUpdateDTO  } from '@/types/sales-plan'

const emit = defineEmits(['add-plan', 'save-plan', 'cancel'])

const formRef = ref(null)
const editingId = ref(null)
const editingData = reactive({})
const formKey = ref(0)

const formData = reactive(new SalesPlanVO({
  customer: '',
  yearMonth: '',
  planNo: '',
  lineList: [
    new SalesPlanLineVO({
      productName: '',
      totalQuantity: null,
      forecastQuantity: 0,
      openingInventory: 0,
      deliveryList: [
        new SalesPlanDeliveryVO({ deliveryDate: '', planQuantity: null })
      ]
    })
  ]
}))

const formRules = {
  customer: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  planNo: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  yearMonth: [{ required: true, message: '请选择计划月份', trigger: 'change' }]
}

// ========== 行操作 ==========
function addLine() {
  formData.lineList.push(new SalesPlanLineVO({
    productName: '',
    totalQuantity: null,
    forecastQuantity: 0,
    openingInventory: 0,
    deliveryList: [
      new SalesPlanDeliveryVO({ deliveryDate: '', planQuantity: null })
    ]
  }))
}

function removeLine(index) {
  formData.lineList.splice(index, 1)
}

function addDelivery(lineIndex) {
  formData.lineList[lineIndex].deliveryList.push(
    new SalesPlanDeliveryVO({ deliveryDate: '', planQuantity: null })
  )
}

function removeDelivery(lineIndex, deliveryIndex) { 
  formData.lineList[lineIndex].deliveryList.splice(deliveryIndex, 1)
}

// ========== 表单操作 ==========
function resetForm() {
  editingId.value = null
  Object.assign(editingData, {})
  formKey.value++
  nextTick(() => {
    Object.assign(formData, new SalesPlanVO({
      customer: '',
      yearMonth: '',
      planNo: '',
      lineList: [
        new SalesPlanLineVO({
          productName: '',
          totalQuantity: null,
          forecastQuantity: 0,
          openingInventory: 0,
          deliveryList: [
            new SalesPlanDeliveryVO({ deliveryDate: '', planQuantity: null })
          ]
        })
      ]
    }))
  })
}

async function handleAddPlan() {
  try {
    await formRef.value?.validate()

    const submitData = new SalesPlanUpdateDTO({
      planId: null,
      version: 0,
      planNo: formData.planNo,
      customer: formData.customer,
      planMonth: formData.yearMonth,
      planType: '',
      status: 0,
      remark: '',
      lines: formData.lineList.map(line => new SalesPlanLineUpdateDTO({
        lineId: null,
        planId: null,
        productId: null,
        productName: line.productName,
        totalQuantity: line.totalQuantity,
        forecastQuantity: line.forecastQuantity || line.totalQuantity,
        openingInventory: line.openingInventory || 0,
        scheduledQuantity: 0,
        finishedQuantity: 0,
        deliveredQuantity: 0,
        status: 0,
        version: 0,
        deliveries: line.deliveryList.map(d => new SalesPlanDeliveryUpdateDTO({
          deliveryId: null,
          lineId: null,
          nodeName: '',
          deliveryDate: d.deliveryDate,
          planQuantity: d.planQuantity,
          actualQuantity: 0,
          status: 0,
          version: 0
        }))
      }))
    })

    console.log('新增计划:', submitData)
    emit('add-plan', submitData)
    ElMessage.success('计划创建成功')
    resetForm()
  } catch (error) {
    if (error !== false) {
      console.error('新增计划失败:', error)
      ElMessage.error(error.message || '新增失败')
    }
  }
}

async function handleSavePlan() {
  try {
    await formRef.value?.validate()

    if (!editingId.value) {
      ElMessage.warning('未选择要编辑的计划')
      return
    }
    const submitData = new SalesPlanUpdateDTO({
      planId: editingId.value,  // 编辑时 planId 有值
      version: editingData.version || 0,  // 编辑时 version 来自加载的数据
      planNo: formData.planNo,
      customer: formData.customer,
      planMonth: formData.yearMonth,
      planType: formData.planType || '',
      status: editingData.planStatus ?? 0,
      remark: formData.remark || '',
      lines: formData.lineList.map(line => new SalesPlanLineUpdateDTO({
        lineId: line.lineId || null,  // 编辑时 lineId 有值（已有产品），新增产品为 null
        planId: editingId.value,
        productId: line.productId || null,
        productName: line.productName,
        totalQuantity: line.totalQuantity,
        forecastQuantity: line.forecastQuantity || line.totalQuantity,
        openingInventory: line.openingInventory || 0,
        scheduledQuantity: line.scheduledQuantity || 0,
        finishedQuantity: line.finishedQuantity || 0,
        deliveredQuantity: line.deliveredQuantity || 0,
        status: line.status || 0,
        version: line.version || 0,  // 编辑时 version 来自加载的数据
        deliveries: line.deliveryList.map(d => new SalesPlanDeliveryUpdateDTO({
          deliveryId: d.deliveryId || null,  // 编辑时 deliveryId 有值（已有节点），新增节点为 null
          lineId: line.lineId || null,
          nodeName: d.nodeName || '',
          deliveryDate: d.deliveryDate,
          planQuantity: d.planQuantity,
          actualQuantity: d.actualQuantity || 0,
          status: d.status || 0,
          version: d.version || 0  // 编辑时 version 来自加载的数据
        }))
      }))
    })
    console.log('保存计划信息:', submitData)
    emit('save-plan', submitData)
    ElMessage.success('计划信息保存成功')
  } catch (error) {
    if (error !== false) {
      console.error('保存计划失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  }
}

function handleCancel() {
  resetForm()
  ElMessage.info('已取消编辑')
}

// ========== 外部调用方法 ==========

/** 加载计划到表单（点击右侧客户名称时调用） */
function loadPlan(plan) {
  if (plan.status !== 0) {
    ElMessage.warning(`该计划已确认（状态=${plan.status}），无法编辑`)
    return false
  }

  editingId.value = plan.planId

  Object.assign(editingData, {
    planId: plan.planId,
    customer: plan.customer,
    yearMonth: plan.yearMonth,
    planStatus: plan.status,
    version: plan.version,
    originalLines: JSON.parse(JSON.stringify(plan.lineList))
  })

  formKey.value++

  nextTick(() => {
    Object.assign(formData, {
      customer: plan.customer || '',
      yearMonth: plan.yearMonth || '',
      planNo: plan.planNo || '',
      lineList: (plan.lineList || []).map(line => new SalesPlanLineVO({
        lineId: line.lineId,
        productName: line.productName || '',
        totalQuantity: line.totalQuantity ?? null,
        forecastQuantity: line.forecastQuantity ?? 0,
        openingInventory: line.openingInventory ?? 0,
        status: line.status ?? 0,
        version: line.version ?? 0, 
        deliveryList: (line.deliveryList || []).map(d => new SalesPlanDeliveryVO({
          deliveryId: d.deliveryId,
          deliveryDate: d.deliveryDate || '',
          planQuantity: d.planQuantity ?? null,
          status: d.status ?? 0,
          version: d.version ?? 0,   // ← 新增
          nodeName: d.nodeName || '', // ← 新增
          actualQuantity: d.actualQuantity ?? 0  // ← 新增
        }))
      }))
    })
  })

  ElMessage.success(`已加载计划：${plan.customer}`)
  return true
}

/** 根据 lineId 移除一行（右侧删除产品时同步） */
function removeLineById(lineId) {
  const idx = formData.lineList.findIndex(l => l.lineId === lineId)
  if (idx > -1) {
    formData.lineList.splice(idx, 1)
  }
}

/** 同步更新 lineList（右侧新增/编辑产品时同步） */
function syncLineList(lineList) {
formData.lineList = lineList.map(line => new SalesPlanLineVO({
  lineId: line.lineId,
  productName: line.productName || '',
  totalQuantity: line.totalQuantity ?? null,
  forecastQuantity: line.forecastQuantity ?? 0,
  openingInventory: line.openingInventory ?? 0,
  status: line.status ?? 0,
  version: line.version ?? 0,  
  deliveryList: (line.deliveryList || []).map(d => new SalesPlanDeliveryVO({
    deliveryId: d.deliveryId,
    deliveryDate: d.deliveryDate || '',
    planQuantity: d.planQuantity ?? null,
    status: d.status ?? 0,
    version: d.version ?? 0,   
    nodeName: d.nodeName || '', 
    actualQuantity: d.actualQuantity ?? 0  
  }))
}))
}

/** 获取当前编辑的计划ID */
function getEditingId() {
  return editingId.value
}

defineExpose({
  loadPlan,
  resetForm,
  removeLineById,
  syncLineList,
  getEditingId
})
</script>

<style scoped>
.left-panel {
  width: 320px;
  min-width: 320px;
  flex-shrink: 0;
  overflow-y: auto;
  height: 100%;
}
.panel-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  padding: 20px;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}
.panel-form {
  margin-bottom: 20px;
}
.panel-actions {
  display: flex;
  gap: 8px;
}
.panel-actions .el-button {
  flex: 1;
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin: 16px 0 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e5e7eb;
}
.sub-section-title {
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
  margin: 10px 0 8px;
  padding-left: 4px;
}
.line-card {
  background: #f9fafb;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
  border: 1px solid #e5e7eb;
}
.line-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.line-index {
  font-size: 13px;
  font-weight: 600;
  color: #2563eb;
}
.delivery-row-vertical {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
}
.delivery-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.delivery-index {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
}
.delivery-row-vertical .el-form-item {
  margin-bottom: 12px;
}
.delivery-row-vertical .el-form-item:last-child {
  margin-bottom: 0;
}
.panel-form :deep(.el-form-item) {
  margin-bottom: 12px;
}
.panel-form :deep(.el-form-item__label) {
  font-size: 12px;
  color: #6b7280;
}
</style>
