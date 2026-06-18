<template>
  <div class="service-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>服务项目管理</h2>
          <el-button type="primary" @click="showAddDialog" v-permission="['ADMIN','STAFF']">
            <el-icon><Plus /></el-icon> 添加服务
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="服务名称" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(val) => toggleStatus(row, val)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small" v-permission="['ADMIN']">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务' : '添加服务'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="服务名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number v-model="form.duration" :min="0" :step="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getServiceList, addService, updateService, deleteService } from '@/api/service'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({ name: '', description: '', price: 0, duration: 30 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getServiceList({ page: page.value, size: size.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {} finally { loading.value = false }
}

function showAddDialog() {
  isEdit.value = false; currentId.value = null
  Object.assign(form, { name: '', description: '', price: 0, duration: 30 })
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true; currentId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (isEdit.value) {
      await updateService(currentId.value, form)
      ElMessage.success('更新成功')
    } else {
      await addService(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false; fetchData()
  } catch {}
}

async function toggleStatus(row, val) {
  try {
    await updateService(row.id, { status: val ? 1 : 0 })
    row.status = val ? 1 : 0
    ElMessage.success('状态已更新')
  } catch {}
}

async function handleDelete(id) {
  try { await deleteService(id); ElMessage.success('删除成功'); fetchData() } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; }
</style>
