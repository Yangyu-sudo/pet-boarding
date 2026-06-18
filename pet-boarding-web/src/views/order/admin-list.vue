<template>
  <div class="admin-order-page">
    <el-card>
      <template #header>
        <h2>订单管理</h2>
      </template>

      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="订单编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="寄养中" value="CHECKED_IN" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="petName" label="宠物" width="100" />
        <el-table-column prop="ownerName" label="主人" width="100" />
        <el-table-column prop="cageNo" label="笼舍" width="80" />
        <el-table-column prop="checkInDate" label="入住" width="110" />
        <el-table-column prop="checkOutDate" label="退房" width="110" />
        <el-table-column label="总价" width="90">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/admin/orders/${row.id}`)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" type="success" link size="small" @click="handleConfirm(row)">确认</el-button>
            <el-button v-if="row.status === 'CONFIRMED'" type="primary" link size="small" @click="showCheckIn(row)">入住</el-button>
            <el-button v-if="row.status === 'CHECKED_IN'" type="success" link size="small" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="['PENDING','CONFIRMED'].includes(row.status)" type="danger" link size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 入住分配笼舍弹窗 -->
    <el-dialog v-model="checkInVisible" title="入住登记 - 分配笼舍" width="500px">
      <el-form label-width="100px">
        <el-form-item label="选择笼舍">
          <el-select v-model="selectedCageId" placeholder="请选择笼舍">
            <el-option
              v-for="cage in availableCages" :key="cage.id"
              :label="`${cage.cageNo} (${cage.type}) - ¥${cage.dailyPrice}/天`"
              :value="cage.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible = false">取消</el-button>
        <el-button type="primary" @click="doCheckIn">确认入住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList, confirmOrder, checkInOrder, completeOrder, cancelOrder } from '@/api/order'
import { getCageList } from '@/api/cage'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1); const size = ref(10); const total = ref(0)
const checkInVisible = ref(false)
const selectedCageId = ref(null)
const currentOrder = ref(null)
const availableCages = ref([])

const searchForm = reactive({ orderNo: '', status: '' })

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
    const res = await getOrderList({ page: page.value, size: size.value, ...searchForm })
    tableData.value = res.data.records; total.value = res.data.total
  } catch {} finally { loading.value = false }
}

function resetSearch() { searchForm.orderNo = ''; searchForm.status = ''; fetchData() }

async function handleConfirm(row) {
  try { await confirmOrder(row.id); ElMessage.success('已确认'); fetchData() } catch {}
}

async function showCheckIn(row) {
  currentOrder.value = row
  const res = await getCageList({ page: 1, size: 100, status: 'AVAILABLE' })
  availableCages.value = res.data.records
  selectedCageId.value = row.cageId || null
  checkInVisible.value = true
}

async function doCheckIn() {
  try {
    await checkInOrder(currentOrder.value.id, selectedCageId.value)
    ElMessage.success('入住登记成功')
    checkInVisible.value = false; fetchData()
  } catch {}
}

async function handleComplete(row) {
  try {
    await ElMessageBox.confirm('确定完成此订单？将释放笼舍。', '提示', { type: 'warning' })
    await completeOrder(row.id); ElMessage.success('已完成'); fetchData()
  } catch {}
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确定取消？', '提示', { type: 'warning' })
    await cancelOrder(row.id); ElMessage.success('已取消'); fetchData()
  } catch {}
}

onMounted(fetchData)
</script>
