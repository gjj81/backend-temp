<template>
  <div>
    <h3>计划日历</h3>
    <p>待实现：按月展示所有销售计划的日历视图</p>
    <p>当前月份计划数：{{ planList.length }}</p>
    <span v-for="plan in planList">
      {{ plan }}
    </span>
  </div>
</template>

<script setup name="PlanCalendar">
defineProps({ planList: { type: Array, default: () => [] } }) // 接收父组件传入的计划列表
</script>