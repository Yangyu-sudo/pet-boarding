<template>
  <div class="order-detail-page">
    <el-page-header @back="$router.back()" :content="`订单详情 - ${order.orderNo || ''}`" />

    <el-card style="margin-top: 20px" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>订单信息</span>
          <el-tag :type="statusType(order.status)" size="large">{{ statusText(order.status) }}</el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="宠物">{{ order.petName }}</el-descriptions-item>
        <el-descriptions-item label="主人">{{ order.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="笼舍">{{ order.cageNo || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ order.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ order.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="总价">
          <span style="color:#f56c6c;font-weight:bold">¥{{ order.totalPrice }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="定金">¥{{ order.deposit }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="特殊要求" :span="2">{{ order.specialRequirements || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 附加服务 -->
      <el-divider>附加服务</el-divider>
      <el-table :data="order.services || []" border size="small">
        <el-table-column prop="serviceName" label="服务名称" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
      </el-table>

      <!-- 每日记录（如有） -->
      <el-divider>每日记录</el-divider>
      <el-timeline v-if="records.length > 0">
        <el-timeline-item
          v-for="record in records" :key="record.id"
          :timestamp="record.recordDate"
          placement="top"
        >
          <el-card shadow="hover" size="small">
            <p><strong>喂食：</strong>{{ record.feedingStatus || '-' }}</p>
            <p><strong>健康：</strong>{{ record.healthStatus || '-' }}</p>
            <p><strong>情绪：</strong>{{ record.mood || '-' }}</p>
            <p v-if="record.notes"><strong>备注：</strong>{{ record.notes }}</p>
            <div v-if="record.photos" style="margin-top:8px">
              <el-image
                v-for="(img, i) in (record.photos || '').split(',').filter(Boolean)"
                :key="i"
                :src="img"
                style="width:80px;height:80px;margin-right:8px"
                fit="cover"
                :preview-src-list="[img]"
              />
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无每日记录" />

      <!-- 操作按钮 -->
      <div class="order-actions" v-if="isAdmin">
        <el-button v-if="order.status === 'PENDING'" type="success" @click="handleAction('confirm')">确认订单</el-button>
        <el-button v-if="order.status === 'CONFIRMED'" type="primary" @click="handleAction('checkin')">入住登记</el-button>
        <el-button v-if="order.status === 'CHECKED_IN'" type="success" @click="handleAction('complete')">退房完成</el-button>
        <el-button v-if="['PENDING','CONFIRMED'].includes(order.status)" type="danger" @click="handleAction('cancel')">取消订单</el-button>
      </div>
      <div class="order-actions" v-else>
        <el-button v-if="order.status === 'PENDING'" type="danger" @click="handleAction('cancel')">取消订单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getOrderById, confirmOrder, checkInOrder, completeOrder, cancelOrder } from '@/api/order'
import { getDailyRecords } from '@/api/record'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const order = ref({})
const records = ref([])
const loading = ref(false)

const isAdmin = computed(() => userStore.isAdmin || userStore.isStaff)

function statusType(s) {
  const m = { PENDING:'warning', CONFIRMED:'primary', CHECKED_IN:'success', COMPLETED:'info', CANCELLED:'danger' }
  return m[s] || 'info'
}
function statusText(s) {
  const m = { PENDING:'待确认', CONFIRMED:'已确认', CHECKED_IN:'寄养中', COMPLETED:'已完成', CANCELLED:'已取消' }
  return m[s] || s
}

async function fetchData() {
  loading.value = true
  try {
    const [orderRes, recordRes] = await Promise.all([
      getOrderById(route.params.id),
      getDailyRecords(route.params.id)
    ])
    order.value = orderRes.data
    records.value = recordRes.data || []
  } catch {} finally { loading.value = false }
}

async function handleAction(action) {
  try {
    if (action === 'cancel') {
      await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    }
    switch (action) {
      case 'confirm': await confirmOrder(order.value.id); ElMessage.success('已确认'); break
      case 'checkin': await checkInOrder(order.value.id, order.value.cageId); ElMessage.success('入住登记成功'); break
      case 'complete': await completeOrder(order.value.id); ElMessage.success('退房完成'); break
      case 'cancel': await cancelOrder(order.value.id); ElMessage.success('已取消'); break
    }
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.order-actions { margin-top: 24px; display: flex; gap: 12px; justify-content: center; }
</style>
