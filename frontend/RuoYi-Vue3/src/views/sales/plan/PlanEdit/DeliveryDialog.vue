<template>
  <el-dialog 
    v-model="visible"
    :title="isEdit ? '编辑交货节点' : '新增交货节点'"
    width="450px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form :model="form" label-width="100px" size="default">
      <el-form-item label="节点名称">
        <el-input 
          v-model="form.nodeName" 
          placeholder="请输入节点名称"
          :disabled="!isEdit && form.status !== 0"
        />
      </el-form-item>
      <el-form-item label="交货日期">
        <el-date-picker
          v-model="form.deliveryDate"
          type="date"
          placeholder="选择交货日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
          :disabled="!isEdit && form.status !== 0"
        />
      </el-form-item>

      <el-form-item label="计划数量">
        <el-input-number
          v-model="form.planQuantity"
          :min="1"
          :step="100"
          style="width: 100%"
          placeholder="请输入数量"
          :disabled="!isEdit && form.status !== 0"
        />
      </el-form-item>

      <el-form-item label="当前状态" v-if="isEdit">
        <el-tag :type="getDeliveryStatusType(form.status)">
          {{ getDeliveryStatusText(form.status) }}
        </el-tag>
        <span 
          v-if="form.status !== 0" 
          style="margin-left: 10px; color: #f56c6c; font-size: 12px;"
        >
          (当前状态不允许修改)
        </span>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer" style="display: flex; justify-content: flex-end; gap: 8px;">
        <el-button @click="handleCancel">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSave"
          :disabled="isEdit && form.status !== 0"
        >
          {{ isEdit ? '保存修改' : '确认新增' }}
        </el-button>
        <el-button 
          v-if="isEdit"
          type="danger" 
          @click="handleDelete"
          :disabled="form.status !== 0"
        >
          删除节点
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 第三层：交货节点弹窗（支持新增/编辑双模式）
 * 
 * 职责：
 *   - 新增模式：isEdit=false，不传 deliveryData
 *   - 编辑模式：isEdit=true，需要 deliveryData
 */
import { reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeliveryStatusType, getDeliveryStatusText } from './utils.js'
import { SalesPlanDeliveryVO, SalesPlanDeliveryUpdateDTO } from '@/types/sales-plan'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  deliveryData: { type: Object, default: null },  // 新增时为 null
  lineData: { type: Object, default: () => ({}) },
  planData: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:modelValue', 'save', 'delete', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 判断是否为编辑模式
const isEdit = computed(() => !!props.deliveryData?.deliveryId)

const form = reactive(new SalesPlanDeliveryVO({
  deliveryId: '',
  nodeName: '',
  deliveryDate: '',
  planQuantity: 0,
  status: 0,
  version: 0
}))

watch(() => props.modelValue, (val) => {
  if (val) {
    if (props.deliveryData?.deliveryId) {
      // 编辑模式：初始化表单数据
      Object.assign(form, {
        deliveryId: props.deliveryData.deliveryId || '',
        nodeName: props.deliveryData.nodeName || '',
        deliveryDate: props.deliveryData.deliveryDate || '',
        planQuantity: props.deliveryData.planQuantity || 0,
        status: props.deliveryData.status ?? 0,
        version: props.deliveryData.version || 0
      })
    } else {
      // 新增模式：重置表单
      resetForm()
    }
  }
})

/** 保存 —— 根据模式决定是新增还是编辑 */
function handleSave() {
  const dto = new SalesPlanDeliveryUpdateDTO({
    deliveryId: form.deliveryId,
    lineId: props.lineData?.lineId || '',
    nodeName: form.nodeName,
    deliveryDate: form.deliveryDate,
    planQuantity: form.planQuantity,
    status: form.status,
    version: form.version || 0
  })

  if (isEdit.value) {
      // 编辑模式：只发核心数据
      emit('save', {
        data: dto,
        originalData: props.deliveryData,
        lineData: props.lineData,
        isEdit: true
      })
      visible.value = false
    } else {
      // 新增模式：只发核心数据
      emit('save', {
        data: dto,
        lineData: props.lineData,
        isEdit: false
      })
      visible.value = false
    }

}

/** 删除 —— 只有编辑模式才有 */
async function handleDelete() {
  if (!props.deliveryData || !props.lineData) {
    ElMessage.warning('未选择交货节点')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除交货节点 "${form.nodeName || form.deliveryDate}" 吗？`,
      '删除确认',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    // 不需要 DTO，直接传参数
    emit('delete', {
          data: {
            deliveryId: form.deliveryId,
            version: form.version || 0
          },
          lineData: props.lineData,
          planData: props.planData
        })
        visible.value = false
      } catch {
        ElMessage.info('已取消删除')
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
  Object.assign(form, {
    deliveryId: '',
    nodeName: '',
    deliveryDate: '',
    planQuantity: 100,  // 默认值
    status: 0,
    version: 0
  })
}
</script>