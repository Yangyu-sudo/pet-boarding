<template>
  <div class="user-manage-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>用户管理</h2>
          <el-button type="primary" @click="showAdd"><el-icon><Plus /></el-icon> 添加用户</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'STAFF' ? 'warning' : 'info'" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : row.role === 'STAFF' ? '员工' : '顾客' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showEdit(row)">编辑</el-button>
            <el-button :type="row.status===1?'warning':'success'" link size="small" @click="toggleStatus(row)">
              {{ row.status===1?'禁用':'启用' }}
            </el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :required="!isEdit">
          <el-input v-model="form.password" type="password" :placeholder="isEdit ? '留空不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员 ADMIN" value="ADMIN" />
            <el-option label="员工 STAFF" value="STAFF" />
            <el-option label="顾客 CUSTOMER" value="CUSTOMER" />
          </el-select>
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
import { getUserList, addUser, updateUser, deleteUser, updateUserStatus } from '@/api/user'
import { ElMessage } from 'element-plus'

const tableData = ref([]); const loading = ref(false)
const page = ref(1); const size = ref(10); const total = ref(0)
const dialogVisible = ref(false); const isEdit = ref(false); const currentId = ref(null)

const form = reactive({ username: '', password: '', realName: '', phone: '', role: 'STAFF' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, size: size.value })
    tableData.value = res.data.records; total.value = res.data.total
  } catch {} finally { loading.value = false }
}

function showAdd() {
  isEdit.value = false; currentId.value = null
  Object.assign(form, { username: '', password: '', realName: '', phone: '', role: 'STAFF' })
  dialogVisible.value = true
}

function showEdit(row) {
  isEdit.value = true; currentId.value = row.id
  Object.assign(form, { username: row.username, password: '', realName: row.realName || '', phone: row.phone || '', role: row.role })
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (isEdit.value) {
      await updateUser(currentId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await addUser({ ...form })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false; fetchData()
  } catch {}
}

async function toggleStatus(row) {
  const s = row.status === 1 ? 0 : 1
  try { await updateUserStatus(row.id, s); row.status = s; ElMessage.success('状态已更新') } catch {}
}

async function handleDelete(id) {
  try { await deleteUser(id); ElMessage.success('删除成功'); fetchData() } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; }
</style>
