<template>
  <div class="cage-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>笼舍管理</h2>
          <el-button type="primary" @click="showAddDialog" v-permission="['ADMIN']">
            <el-icon><Plus /></el-icon> 添加笼舍
          </el-button>
        </div>
      </template>

      <!-- 笼舍网格视图 -->
      <el-row :gutter="16">
        <el-col :span="6" v-for="cage in tableData" :key="cage.id" style="margin-bottom: 16px">
          <el-card
            shadow="hover"
            :class="['cage-item', `cage-${cage.status.toLowerCase()}`]"
          >
            <div class="cage-header">
              <span class="cage-no">{{ cage.cageNo }}</span>
              <el-tag
                :type="cage.status === 'AVAILABLE' ? 'success' : cage.status === 'OCCUPIED' ? 'danger' : 'warning'"
                size="small"
              >
                {{ cage.status === 'AVAILABLE' ? '空闲' : cage.status === 'OCCUPIED' ? '使用中' : '维护中' }}
              </el-tag>
            </div>
            <div class="cage-body">
              <p><strong>类型：</strong>
                <el-tag :type="cage.type === 'VIP' ? 'warning' : cage.type === 'DELUXE' ? 'primary' : 'info'" size="small">
                  {{ cage.type === 'STANDARD' ? '标准' : cage.type === 'DELUXE' ? '豪华' : 'VIP' }}
                </el-tag>
              </p>
              <p><strong>尺寸：</strong>{{ cage.size }}</p>
              <p><strong>价格：</strong><span style="color:#f56c6c;font-weight:bold">¥{{ cage.dailyPrice }}/天</span></p>
            </div>
            <div class="cage-actions">
              <el-button type="primary" link size="small" @click="showEditDialog(cage)">编辑</el-button>
              <el-dropdown @command="(cmd) => handleStatusChange(cage, cmd)" style="margin-left:8px">
                <el-button type="warning" link size="small">状态</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="AVAILABLE">设为空闲</el-dropdown-item>
                    <el-dropdown-item command="OCCUPIED">设为使用中</el-dropdown-item>
                    <el-dropdown-item command="MAINTENANCE">设为维护中</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-popconfirm title="确定删除？" @confirm="handleDelete(cage.id)">
                <template #reference>
                  <el-button type="danger" link size="small" v-permission="['ADMIN']">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑笼舍' : '添加笼舍'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="笼舍编号">
          <el-input v-model="form.cageNo" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="标准 STANDARD" value="STANDARD" />
            <el-option label="豪华 DELUXE" value="DELUXE" />
            <el-option label="VIP" value="VIP" />
          </el-select>
        </el-form-item>
        <el-form-item label="尺寸">
          <el-input v-model="form.size" placeholder="如：2m×2m" />
        </el-form-item>
        <el-form-item label="每日价格">
          <el-input-number v-model="form.dailyPrice" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { getCageList, addCage, updateCage, deleteCage, updateCageStatus } from '@/api/cage'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const page = ref(1)
const size = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  cageNo: '',
  type: 'STANDARD',
  size: '',
  dailyPrice: 0,
  description: ''
})

async function fetchData() {
  try {
    const res = await getCageList({ page: page.value, size: size.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {}
}

function showAddDialog() {
  isEdit.value = false
  currentId.value = null
  Object.assign(form, { cageNo: '', type: 'STANDARD', size: '', dailyPrice: 0, description: '' })
  dialogVisible.value = true
}

function showEditDialog(cage) {
  isEdit.value = true
  currentId.value = cage.id
  Object.assign(form, cage)
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (isEdit.value) {
      await updateCage(currentId.value, form)
      ElMessage.success('更新成功')
    } else {
      await addCage(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {}
}

async function handleStatusChange(cage, status) {
  try {
    await updateCageStatus(cage.id, status)
    cage.status = status
    ElMessage.success('状态已更新')
  } catch {}
}

async function handleDelete(id) {
  try {
    await deleteCage(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; }
.cage-item { border-left: 4px solid #dcdfe6; }
.cage-item.cage-available { border-left-color: #67c23a; }
.cage-item.cage-occupied { border-left-color: #f56c6c; }
.cage-item.cage-maintenance { border-left-color: #e6a23c; }
.cage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.cage-no { font-size: 18px; font-weight: bold; }
.cage-body p { margin: 6px 0; font-size: 13px; color: #606266; }
.cage-actions { margin-top: 12px; display: flex; gap: 8px; border-top: 1px solid #ebeef5; padding-top: 12px; }
</style>
