<template>
  <div class="finance-page">
    <el-card>
      <template #header><h2>财务管理</h2></template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="收入概览" name="overview">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-statistic title="总收入" :value="totalRevenue" prefix="¥" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="已结算订单" :value="paidCount" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="待结算" :value="unpaidCount" />
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="结算记录" name="records">
          <el-table :data="billings" v-loading="loadingBilling" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="orderId" label="订单ID" width="80" />
            <el-table-column label="总金额" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column label="已付定金" width="100">
              <template #default="{ row }">¥{{ row.deposit }}</template>
            </el-table-column>
            <el-table-column label="最终金额" width="100">
              <template #default="{ row }">¥{{ row.finalAmount }}</template>
            </el-table-column>
            <el-table-column label="支付状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.paymentStatus === 'FULLY_PAID' ? 'success' : 'warning'" size="small">
                  {{ row.paymentStatus === 'FULLY_PAID' ? '已支付' : '未支付' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式" width="100" />
            <el-table-column prop="paymentTime" label="支付时间" width="160" />
            <el-table-column label="操作" width="100" v-if="false">
              <template #default="{ row }">
                <el-button
                  v-if="row.paymentStatus !== 'FULLY_PAID'"
                  type="primary" link size="small"
                  @click="handlePay(row)"
                >确认收款</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderList } from '@/api/order'
import { getDashboardStats } from '@/api/dashboard'

const activeTab = ref('overview')
const orders = ref([])
const billings = ref([])
const loadingBilling = ref(false)
const stats = ref({})

const totalRevenue = computed(() => {
  return orders.value
    .filter(o => o.status === 'COMPLETED')
    .reduce((sum, o) => sum + ((o.totalPrice || 0) * 1), 0)
    .toFixed(2)
})
const paidCount = computed(() => billings.value.filter(b => b.paymentStatus === 'FULLY_PAID').length)
const unpaidCount = computed(() => billings.value.filter(b => b.paymentStatus !== 'FULLY_PAID').length)

onMounted(async () => {
  try {
    const [orderRes, statsRes] = await Promise.all([
      getOrderList({ page: 1, size: 1000, status: 'COMPLETED' }),
      getDashboardStats()
    ])
    orders.value = orderRes.data.records
    stats.value = statsRes.data
  } catch {}
})
</script>
