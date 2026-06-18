<template>
  <div class="order-list-page">
    <div class="page-header">
      <h2>我的订单</h2>
      <el-button type="primary" @click="$router.push('/customer/orders/create')">
        <el-icon><Plus /></el-icon> 预约寄养
      </el-button>
    </div>

    <el-tabs v-model="activeStatus" @tab-change="fetchData">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待确认" name="PENDING" />
      <el-tab-pane label="已确认" name="CONFIRMED" />
      <el-tab-pane label="寄养中" name="CHECKED_IN" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已取消" name="CANCELLED" />
    </el-tabs>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="petName" label="宠物" width="100" />
      <el-table-column prop="cageNo" label="笼舍" width="80" />
      <el-table-column label="入住日期" width="110">
        <template #default="{ row }">{{ row.checkInDate }}</template>
      </el-table-column>
      <el-table-column label="退房日期" width="110">
        <template #default="{ row }">{{ row.checkOutDate }}</template>
      </el-table-column>
      <el-table-column label="总价" width="100">
        <template #default="{ row }">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ row.createTime }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="$router.push(`/customer/orders/${row.id}`)">
            详情
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
            type="danger" link size="small"
            @click="handleCancel(row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="fetchData"
      style="margin-top: 20px; justify-content: flex-end"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, cancelOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const activeStatus = ref('')

function statusType(status) {
  const map = { PENDING: 'warning', CONFIRMED: 'primary', CHECKED_IN: 'success', COMPLETED: 'info', CANCELLED: 'danger' }
  return map[status] || 'info'
}

function statusText(status) {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', CHECKED_IN: '寄养中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getOrderList({
      page: page.value, size: size.value,
      status: activeStatus.value || undefined
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {} finally { loading.value = false }
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(row.id)
    ElMessage.success('订单已取消')
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; }
</style>
