<template>
  <div class="admin-pet-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>宠物管理</h2>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm">
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="宠物名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="狗" value="DOG" />
            <el-option label="猫" value="CAT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'DOG' ? 'primary' : row.type === 'CAT' ? 'success' : 'warning'" size="small">
              {{ row.type === 'DOG' ? '狗' : row.type === 'CAT' ? '猫' : '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="品种" width="120" />
        <el-table-column prop="age" label="年龄(月)" width="80" />
        <el-table-column prop="weight" label="体重(kg)" width="80" />
        <el-table-column prop="ownerId" label="主人ID" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
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
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="宠物详情">
      <el-descriptions :column="2" border v-if="currentPet">
        <el-descriptions-item label="名称">{{ currentPet.name }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentPet.type }}</el-descriptions-item>
        <el-descriptions-item label="品种">{{ currentPet.breed }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ currentPet.age }}个月</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentPet.gender }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ currentPet.weight }}kg</el-descriptions-item>
        <el-descriptions-item label="病史" :span="2">{{ currentPet.medicalHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentPet.notes || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPetList, deletePet } from '@/api/pet'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentPet = ref(null)

const searchForm = reactive({ name: '', type: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getPetList({ page: page.value, size: size.value, ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {} finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.name = ''
  searchForm.type = ''
  fetchData()
}

function showDetail(row) {
  currentPet.value = row
  detailVisible.value = true
}

async function handleDelete(id) {
  try {
    await deletePet(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h2 {
  margin: 0;
}
</style>
