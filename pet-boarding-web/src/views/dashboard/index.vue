<template>
  <div class="dashboard-page">
    <h2 style="margin-bottom: 20px">仪表盘</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="16" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>近7天订单趋势</template>
          <div ref="orderChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>笼舍类型分布</template>
          <div ref="cageChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单状态分布 -->
    <el-row :gutter="16" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>订单状态分布</template>
          <div ref="orderStatusRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>快捷操作</template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="$router.push('/admin/orders')">
              <el-icon><Document /></el-icon> 订单管理
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/admin/cages')">
              <el-icon><House /></el-icon> 笼舍管理
            </el-button>
            <el-button type="warning" size="large" @click="$router.push('/admin/pets')">
              <el-icon><Star /></el-icon> 宠物管理
            </el-button>
            <el-button type="info" size="large" @click="$router.push('/admin/finance')">
              <el-icon><Money /></el-icon> 财务管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboardStats, getDashboardRevenue } from '@/api/dashboard'
import * as echarts from 'echarts'
import { Star, House, Document, Money, DataAnalysis, TrendCharts, PieChart } from '@element-plus/icons-vue'

const orderChartRef = ref(null)
const cageChartRef = ref(null)
const orderStatusRef = ref(null)

const stats = ref({})
const revenue = ref({ dailyData: [], cageTypeDistribution: {} })

const statCards = computed(() => [
  { label: '今日寄养中', value: stats.value.todayBoarding || 0, icon: Star, color: '#409eff' },
  { label: '笼舍使用率', value: (stats.value.cageUsageRate || 0) + '%', icon: House, color: '#67c23a' },
  { label: '待处理订单', value: stats.value.pendingOrders || 0, icon: Document, color: '#e6a23c' },
  { label: '今日收入', value: '¥' + (stats.value.todayRevenue || 0), icon: Money, color: '#f56c6c' }
])

function initOrderChart() {
  if (!orderChartRef.value) return
  const chart = echarts.init(orderChartRef.value)
  const dates = (revenue.value.dailyData || []).map(d => d.date)
  const counts = (revenue.value.dailyData || []).map(d => d.orderCount)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '订单数', min: 0, interval: 1 },
    series: [{ data: counts, type: 'line', smooth: true, areaStyle: { color: 'rgba(64,158,255,0.2)' }, itemStyle: { color: '#409eff' } }]
  })
}

function initCageChart() {
  if (!cageChartRef.value) return
  const chart = echarts.init(cageChartRef.value)
  const data = revenue.value.cageTypeDistribution || {}
  const nameMap = { STANDARD: '标准', DELUXE: '豪华', VIP: 'VIP' }
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: Object.entries(data).map(([k, v]) => ({ name: nameMap[k] || k, value: v })),
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  })
}

function initOrderStatusChart() {
  if (!orderStatusRef.value) return
  const chart = echarts.init(orderStatusRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: '60%',
      data: [
        { name: '待确认', value: stats.value.pendingOrders || 0, itemStyle: { color: '#e6a23c' } },
        { name: '已确认', value: stats.value.confirmedOrders || 0, itemStyle: { color: '#409eff' } },
        { name: '已完成', value: stats.value.completedOrders || 0, itemStyle: { color: '#67c23a' } },
        { name: '寄养中', value: stats.value.todayBoarding || 0, itemStyle: { color: '#909399' } }
      ]
    }]
  })
}

onMounted(async () => {
  try {
    const [statsRes, revenueRes] = await Promise.all([
      getDashboardStats(), getDashboardRevenue()
    ])
    stats.value = statsRes.data
    revenue.value = revenueRes.data
    setTimeout(() => {
      initOrderChart()
      initCageChart()
      initOrderStatusChart()
    }, 100)
  } catch {}
})
</script>

<style scoped>
.stat-card { cursor: pointer; }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.quick-actions { display: flex; flex-wrap: wrap; gap: 16px; }
.quick-actions .el-button { width: calc(50% - 8px); height: 60px; font-size: 16px; }
</style>
