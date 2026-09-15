<template>
  <el-dialog 
    v-model="visible"
    :title="isEdit ? '编辑产品' : '新增产品与交货节点'"
    width="550px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-alert
      v-if="planData && planData.status !== 0"
      title="父级计划已锁定，无法操作"
      type="error"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
    />

    <el-alert
      v-else-if="!isEdit"
      title="请填写产品信息并添加交货节点，完成后点击'确认新增'"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
    />

    <el-form 
      ref="formRef" 
      :model="form" 
      :rules="formRules" 
      label-width="100px" 
      size="default"
    >
      <el-form-item label="产品名称" prop="productName">
        <el-input 
          v-model="form.productName" 
          placeholder="请输入产品名称"
          :disabled="planData?.status !== 0"
        />
      </el-form-item>

      <el-form-item label="订单总量" prop="totalQuantity">
        <el-input-number 
          v-model="form.totalQuantity" 
          :min="1" 
          :precision="0" 
          controls-position="right" 
          style="width: 100%"
          :disabled="planData?.status !== 0"
        />
      </el-form-item>

      <el-form-item label="预测数量">
        <el-input-number 
          v-model="form.forecastQuantity" 
          :min="0" 
          :precision="0" 
          controls-position="right" 
          style="width: 100%"
          :disabled="planData?.status !== 0"
        />
      </el-form-item>

      <el-form-item label="期初库存">
        <el-input-number 
          v-model="form.openingInventory" 
          :min="0" 
          :precision="0" 
          controls-position="right" 
          style="width: 100%"
          :disabled="planData?.status !== 0"
        />
      </el-form-item>

      <el-divider content-position="left">交货计划</el-divider>

      <div 
        v-for="(delivery, dIdx) in form.deliveryList" 
        :key="dIdx" 
        class="dialog-delivery-item"
      >
        <div class="dialog-delivery-header">
          <span>交货 #{{ dIdx + 1 }}</span>
          <el-button 
          v-if="form.deliveryList.length > 1"
          type="danger" 
          link 
          size="small"
          :disabled="!!(isEdit && delivery.deliveryId) || delivery.status !== 0"
          @click="removeDelivery(dIdx)"
          :style="{ opacity: (isEdit && delivery.deliveryId) || delivery.status !== 0 ? 0.4 : 1 }"
          :title="(isEdit && delivery.deliveryId) ? '已保存的交货节点不允许删除' : (delivery.status !== 0 ? '该交货节点已锁定' : '删除此交货节点')"
        >
          删除
        </el-button>

        </div>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-date-picker
              v-model="delivery.deliveryDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled="delivery.status !== 0 || planData?.status !== 0"
            />
          </el-col>
          <el-col :span="10">
            <el-input-number
              v-model="delivery.planQuantity"
              :min="1"
              :step="100"
              controls-position="right"
              style="width: 100%"
              :disabled="delivery.status !== 0 || planData?.status !== 0"
              placeholder="数量"
            />
          </el-col>
          <el-col :span="2" style="display: flex; align-items: center; justify-content: center;">
            <el-tag 
              :type="getDeliveryStatusType(delivery.status)" 
              size="small"
            >
              {{ delivery.status === 0 ? '✅' : '🔒' }}
            </el-tag>
          </el-col>
        </el-row>
      </div>

      <el-button 
        type="primary" 
        link 
        size="small" 
        style="width: 100%; margin-top: 8px;"
        @click="addDelivery"
        :disabled="planData?.status !== 0"
      >
        + 添加交货日期
      </el-button>
    </el-form>

    <template #footer>
      <div class="dialog-footer" style="display: flex; justify-content: space-between; align-items: center;">
        <div v-if="!isEdit" style="font-size: 12px; color: #6b7280;">
          新增 {{ form.deliveryList.length }} 个交货节点
        </div>
        <div v-else></div>

        <div style="display: flex; gap: 8px;">
          <el-button @click="handleCancel">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSave"
            :disabled="planData?.status !== 0"
          >
            {{ isEdit ? '保存修改' : '确认新增' }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getDeliveryStatusType } from './utils.js'
import { SalesPlanLineVO, SalesPlanDeliveryVO } from '@/types/sales-plan'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  productData: { type: Object, default: () => ({}) },
  planData: { type: Object, default: () => ({}) },
  lineIndex: { type: Number, default: null }
})

const emit = defineEmits(['update:modelValue', 'save', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const isEdit = computed(() => !!props.productData?.lineId)

const formRef = ref(null)

const form = reactive(new SalesPlanLineVO({
  lineId: null,
  productName: '',
  totalQuantity: null,
  forecastQuantity: 0,
  openingInventory: 0,
  deliveryList: [
    new SalesPlanDeliveryVO({ deliveryId: null, deliveryDate: '', planQuantity: null, status: 0 })
  ]
}))

const formRules = {
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  totalQuantity: [{ required: true, message: '请输入订单总量', trigger: 'blur' }]
}

watch(() => props.modelValue, (val) => {
  if (val) {
    if (isEdit.value && props.productData) {
      Object.assign(form, {
        lineId: props.productData.lineId,
        productName: props.productData.productName || '',
        totalQuantity: props.productData.totalQuantity ?? null,
        forecastQuantity: props.productData.forecastQuantity ?? 0,
        openingInventory: props.productData.openingInventory ?? 0,
        status: props.productData.status ?? 0,
        deliveryList: (props.productData.deliveryList || []).map(d => ({
          deliveryId: d.deliveryId,
          deliveryDate: d.deliveryDate || '',
          planQuantity: d.planQuantity ?? null,
          status: d.status ?? 0
        }))
      })
    } else {
      resetForm()
    }
  }
})

function addDelivery() {
  form.deliveryList.push(new SalesPlanDeliveryVO({
    deliveryId: null,
    deliveryDate: '',
    planQuantity: null,
    status: 0
  }))
}

function removeDelivery(dIdx) {
  if (form.deliveryList.length <= 1) {
    ElMessage.warning('至少需要一个交货节点')
    return
  }
  form.deliveryList.splice(dIdx, 1)
}

async function handleSave() {
  try {
    await formRef.value?.validate()

    if (!props.planData) {
      ElMessage.error('未关联计划')
      return
    }

    const result = {
      ...form,
      isEdit: isEdit.value,
      originalLineIndex: props.lineIndex
    }

    emit('save', result)
    visible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('保存产品失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  }
}

function handleClose() {
  resetForm()
  emit('close')
}

function handleCancel() {
  visible.value = false
}

function resetForm() {
  Object.assign(form, new SalesPlanLineVO({
    lineId: null,
    productName: '',
    totalQuantity: null,
    forecastQuantity: 0,
    openingInventory: 0,
    deliveryList: [
      new SalesPlanDeliveryVO({ deliveryId: null, deliveryDate: '', planQuantity: null, status: 0 })
    ]
  }))
}
</script>

<style scoped>
.dialog-delivery-item {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
}
.dialog-delivery-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 500;
  color: #374151;
}
</style>
