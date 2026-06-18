<template>
  <div class="logs-page">
    <el-card>
      <template #header><h2>操作日志</h2></template>

      <el-table :data="tableData" v-loading="loading" border stripe max-height="600">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="操作用户" width="100" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operation" label="操作" width="150" />
        <el-table-column prop="method" label="请求方法" width="80" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="duration" label="耗时(ms)" width="80">
          <template #default="{ row }">
            <el-tag :type="row.duration > 1000 ? 'danger' : row.duration > 500 ? 'warning' : 'success'" size="small">
              {{ row.duration }}ms
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const tableData = ref([]); const loading = ref(false)
const page = ref(1); const size = ref(10); const total = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const res = await request({ url: '/logs', method: 'get', params: { page: page.value, size: size.value } })
    tableData.value = res.data.records || []; total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

onMounted(fetchData)
</script>
