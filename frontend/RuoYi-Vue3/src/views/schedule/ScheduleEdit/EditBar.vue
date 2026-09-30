<template>
  <div class="edit-bar">
    <div class="edit-bar-content">
      <div class="edit-info">
        <el-tag :type="getSpanDisplayStatus(spanData).type" size="small">
          {{ getSpanDisplayStatus(spanData).text }}
        </el-tag>
      </div>

      <div class="edit-form" v-if="isEditable">
        <div class="form-item">
          <label>{{ isNewBlock ? '每天数量' : '数量' }}:</label>
          <el-input-number
            v-model="editForm.dailyQuantity"
            :min="1"
            :max="10000"
            size="small"
            style="width: 120px;"
          />
          <span class="unit">{{ isNewBlock ? '件/天' : '件' }}</span>
        </div>

        <div class="form-item" v-if="isNewBlock">
          <label>天数:</label>
          <el-input-number
            v-model="editForm.days"
            :min="1"
            :max="30"
            size="small"
            style="width: 100px;"
          />
          <span class="unit">天</span>
        </div>

        <div class="form-item">
          <label>日期:</label>
          <el-date-picker
            v-model="editForm.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            size="small"
            style="width: 150px;"
          />
        </div>

        <div class="form-item total-qty">
          <label>排产量:</label>
          <span class="total-value">{{ (editForm.dailyQuantity * (isNewBlock ? (editForm.days || 1) : 1)).toLocaleString() }} 件</span>
        </div>
      </div>

      <div class="read-only-info" v-else>
        <span class="info-item">
          {{ spanData.startDate }}
        </span>
        <span class="info-item highlight">
          {{ spanData.dailyQuantity.toLocaleString() }} 件
        </span>
        <span v-if="spanData.endDate" class="info-item">
          结束: {{ spanData.endDate }}
        </span>
      </div>

      <div class="edit-actions">
        <template v-if="isEditable">
          <el-button type="primary" size="small" @click="handleSave">保存</el-button>
          <el-button v-if="!isNewBlock" type="success" size="small" @click="handleIssue">下发</el-button>
          <el-button size="small" @click="$emit('close')">取消</el-button>
          <el-button
            type="danger"
            size="small"
            plain
            @click="handleDelete"
          >
            删除
          </el-button>
        </template>

        <template v-else>
          <el-button size="small" @click="$emit('close')">关闭</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSpanDisplayStatus } from './utils.js'

const props = defineProps({
  spanData: { type: Object, required: true },
  lineData: { type: Object, default: null }
})

const emit = defineEmits(['save', 'delete', 'issue', 'close'])

const editForm = ref({
  dailyQuantity: 0,
  days: 0,
  startDate: ''
})

const isNewBlock = computed(() => props.spanData?._isNew === true)

const isEditable = computed(() => {
  const s = props.spanData?.status
  return props.spanData?._isNew || s === 0
})

watch(() => props.spanData, (newVal) => {
  if (newVal) {
    editForm.value = {
      dailyQuantity: newVal.dailyQuantity || 1000,
      days: newVal.days || 1,
      startDate: newVal.startDate || ''
    }
  }
}, { immediate: true, deep: true })

async function handleSave() {
  if (!editForm.value.dailyQuantity) {
    ElMessage.warning('请填写数量')
    return
  }

  try {
    const days = isNewBlock.value ? (editForm.value.days || 1) : 1
    const totalQty = editForm.value.dailyQuantity * days

    await ElMessageBox.confirm(
      `保存排产块？\n数量：${editForm.value.dailyQuantity.toLocaleString()} 件` +
      (isNewBlock.value ? `/天 × ${days}天` : '') +
      `\n总产量：${totalQty.toLocaleString()} 件`,
      '确认保存',
      {
        confirmButtonText: '保存',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const saveData = {
      spanId: props.spanData.spanId,
      dailyQuantity: editForm.value.dailyQuantity,
      days: isNewBlock.value ? days : 1,
      startDate: editForm.value.startDate,
      version: (props.spanData.version || 0) + 1,
      _isNew: false
    }

    console.log('保存排产块:', saveData)
    emit('save', saveData)
    ElMessage.success('已保存')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
    }
  }
}

async function handleDelete() {
  if (!props.spanData?._isNew && props.spanData?.status !== 0) {
    ElMessage.warning('已下发/生产中/已完工的排产块不允许删除')
    return
  }
  try {
    const label = isNewBlock.value
      ? `草稿块（${editForm.value.dailyQuantity}件/天 × ${editForm.value.days || 1}天）`
      : `${editForm.value.dailyQuantity.toLocaleString()} 件`

    await ElMessageBox.confirm(
      `确定要删除该排产块吗？\n${label}`,
      '删除确认',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )

    emit('delete', {
      spanId: props.spanData.spanId,
      version: props.spanData.version
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

async function handleIssue() {
  try {
    await ElMessageBox.confirm(
      `确定要下发该排产块吗？\n` +
      `产品：${props.lineData?.productName || '未知'}\n` +
      `${props.spanData.prodLineName || '未知产线'} | ${props.spanData.startDate}\n` +
      `${props.spanData.dailyQuantity.toLocaleString()} 件`,
      '下发确认',
      {
        confirmButtonText: '确认下发',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    emit('issue', {
      spanId: props.spanData.spanId,
      version: props.spanData.version
    })

    ElMessage.success('已成功下发')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下发失败:', error)
    }
  }
}
</script>

<style scoped>
.edit-bar {
  flex-shrink: 0;
  background: #fff;
  border-bottom: 2px solid #409eff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.15);
  z-index: 20;
}
.edit-bar-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
}
.edit-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.info-detail { font-size: 12px; color: #909399; }
.edit-form {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}
.form-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.form-item label { font-size: 13px; color: #606266; white-space: nowrap; }
.unit { font-size: 12px; color: #909399; }
.read-only-info {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #606266;
  flex: 1;
  align-items: center;
  flex-wrap: wrap;
}
.info-item {
  background: #f5f7fa;
  padding: 6px 14px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.info-item.highlight {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
  font-weight: 600;
}
.product-name {
  background: #fff;
  border-color: #dcdfe6;
  min-width: 150px;
}
.total-qty {
  margin-left: auto;
  padding-left: 20px;
  border-left: 2px solid #409eff;
}
.total-value {
  font-size: 16px;
  font-weight: 700;
  color: #409eff;
}
.edit-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>