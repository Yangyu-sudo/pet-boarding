<template>
  <div class="create-order-page">
    <el-card>
      <template #header>
        <h2>预约寄养</h2>
      </template>

      <el-steps :active="step" align-center style="margin-bottom: 30px">
        <el-step title="选择宠物" />
        <el-step title="选择笼舍" />
        <el-step title="选择服务" />
        <el-step title="确认订单" />
      </el-steps>

      <!-- Step 1: 选择宠物 -->
      <div v-show="step === 0" class="pet-select-group">
        <el-empty v-if="pets.length === 0" description="还没有添加宠物，请先添加宠物">
          <el-button type="primary" @click="$router.push('/customer/pets/add')">添加宠物</el-button>
        </el-empty>
        <el-row :gutter="16">
          <el-col :span="8" v-for="pet in pets" :key="pet.id">
            <div class="pet-option" :class="{ selected: form.petId === pet.id }" @click="form.petId = pet.id">
              <img v-if="pet.photo" :src="pet.photo" class="pet-avatar" />
              <div v-else class="pet-avatar-placeholder">🐾</div>
              <h4>{{ pet.name }}</h4>
              <p>{{ pet.type === 'DOG' ? '🐶狗' : pet.type === 'CAT' ? '🐱猫' : '🐾其他' }} | {{ pet.breed || '未知品种' }}</p>
              <p>{{ formatAge(pet.age) }} | {{ pet.weight }}kg</p>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Step 2: 选择笼舍 -->
      <div v-show="step === 1">
        <el-radio-group v-model="form.cageId" class="cage-select-group">
          <el-row :gutter="16">
            <el-col :span="8" v-for="cage in cages" :key="cage.id">
              <div
                class="cage-option"
                :class="{ selected: form.cageId === cage.id, disabled: cage.status !== 'AVAILABLE' }"
                @click="cage.status === 'AVAILABLE' && (form.cageId = cage.id)"
              >
                <div class="cage-type-badge" :class="cage.type.toLowerCase()">
                  {{ cage.type === 'STANDARD' ? '标准' : cage.type === 'DELUXE' ? '豪华' : 'VIP' }}
                </div>
                <h4>{{ cage.cageNo }}</h4>
                <p>{{ cage.size }}</p>
                <p class="price">¥{{ cage.dailyPrice }}<span>/天</span></p>
                <el-tag :type="cage.status === 'AVAILABLE' ? 'success' : 'danger'" size="small">
                  {{ cage.status === 'AVAILABLE' ? '空闲' : '不可用' }}
                </el-tag>
                <p class="desc">{{ cage.description }}</p>
              </div>
            </el-col>
          </el-row>
        </el-radio-group>
      </div>

      <!-- Step 3: 选择附加服务 -->
      <div v-show="step === 2" class="service-select-group">
        <el-row :gutter="16">
          <el-col :span="24" v-for="svc in services" :key="svc.id" style="margin-bottom: 10px">
            <div
              class="service-option"
              :class="{ selected: selectedServiceIds.includes(svc.id) }"
              @click="toggleService(svc.id)"
            >
              <span class="svc-name">{{ svc.name }}</span>
              <span class="svc-price">¥{{ svc.price }}</span>
              <span class="svc-desc">{{ svc.description }}</span>
              <span v-if="svc.duration" class="svc-duration">时长：{{ svc.duration }}分钟</span>
              <el-icon v-if="selectedServiceIds.includes(svc.id)" class="svc-check"><CircleCheck /></el-icon>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Step 4: 确认订单 -->
      <div v-show="step === 3">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="寄养宠物">{{ selectedPet?.name }}</el-descriptions-item>
          <el-descriptions-item label="宠物类型">{{ selectedPet?.type === 'DOG' ? '狗' : selectedPet?.type === 'CAT' ? '猫' : '其他' }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ form.checkInDate }}</el-descriptions-item>
          <el-descriptions-item label="退房日期">{{ form.checkOutDate }}</el-descriptions-item>
          <el-descriptions-item label="笼舍">{{ selectedCage?.cageNo }} ({{ selectedCage?.type }})</el-descriptions-item>
          <el-descriptions-item label="笼舍价格">¥{{ selectedCage?.dailyPrice }}/天</el-descriptions-item>
          <el-descriptions-item label="附加服务" :span="2">
            <el-tag v-for="sid in selectedServiceIds" :key="sid" style="margin-right:8px">
              {{ getServiceName(sid) }}
            </el-tag>
            <span v-if="selectedServiceIds.length === 0">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="预计天数">{{ days }} 天</el-descriptions-item>
          <el-descriptions-item label="预计总价">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ estimatedTotal }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="特殊要求" :span="2">
            <el-input v-model="form.specialRequirements" type="textarea" :rows="2" placeholder="如有特殊要求请填写" />
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 日期选择（贯穿Steps） -->
      <div style="margin-top: 24px; padding-top: 24px; border-top: 1px solid #ebeef5">
        <el-form :inline="true">
          <el-form-item label="入住日期" required>
            <el-date-picker v-model="form.checkInDate" type="date" placeholder="选择入住日期"
              :disabled-date="disabledDate" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="退房日期" required>
            <el-date-picker v-model="form.checkOutDate" type="date" placeholder="选择退房日期"
              :disabled-date="disabledDate" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div style="margin-top: 24px; text-align: center">
        <el-button v-if="step > 0" @click="step--">上一步</el-button>
        <el-button v-if="step < 3" type="primary" @click="nextStep">下一步</el-button>
        <el-button v-if="step === 3" type="primary" size="large" @click="submitOrder" :loading="submitting">
          提交订单
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPetList } from '@/api/pet'
import { getCageList } from '@/api/cage'
import { getServiceList } from '@/api/service'
import { createOrder } from '@/api/order'
import { CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const step = ref(0)
const submitting = ref(false)
const selectedServiceIds = ref([])

const pets = ref([])
const cages = ref([])
const services = ref([])

const form = reactive({
  petId: null,
  cageId: null,
  checkInDate: '',
  checkOutDate: '',
  specialRequirements: ''
})

const selectedPet = computed(() => pets.value.find(p => p.id === form.petId))
const selectedCage = computed(() => cages.value.find(c => c.id === form.cageId))

const days = computed(() => {
  if (!form.checkInDate || !form.checkOutDate) return 0
  const d = dayjs(form.checkOutDate).diff(dayjs(form.checkInDate), 'day')
  return d > 0 ? d : 1
})

const estimatedTotal = computed(() => {
  let total = 0
  if (selectedCage.value) {
    total += selectedCage.value.dailyPrice * days.value
  }
  selectedServiceIds.value.forEach(sid => {
    const svc = services.value.find(s => s.id === sid)
    if (svc) total += svc.price
  })
  return total.toFixed(2)
})

function disabledDate(date) {
  return dayjs(date).isBefore(dayjs(), 'day')
}

function toggleService(id) {
  const idx = selectedServiceIds.value.indexOf(id)
  if (idx > -1) {
    selectedServiceIds.value.splice(idx, 1)
  } else {
    selectedServiceIds.value.push(id)
  }
}

function formatAge(months) {
  if (months == null) return '-'
  if (months >= 12 && months % 12 === 0) return (months / 12) + '年'
  return months + '个月'
}

function getServiceName(id) {
  const svc = services.value.find(s => s.id === id)
  return svc ? svc.name : ''
}

function nextStep() {
  if (step.value === 0 && !form.petId) { ElMessage.warning('请选择宠物'); return }
  if (step.value === 1 && !form.cageId) { ElMessage.warning('请选择笼舍'); return }
  if (!form.checkInDate || !form.checkOutDate) { ElMessage.warning('请选择日期'); return }
  step.value++
}

async function submitOrder() {
  submitting.value = true
  try {
    await createOrder({
      petId: form.petId,
      cageId: form.cageId,
      checkInDate: form.checkInDate,
      checkOutDate: form.checkOutDate,
      specialRequirements: form.specialRequirements,
      services: selectedServiceIds.value.map(sid => ({ serviceId: sid, quantity: 1 }))
    })
    ElMessage.success('下单成功')
    router.push('/customer/orders')
  } catch {} finally { submitting.value = false }
}

onMounted(async () => {
  try {
    const [petRes, cageRes, svcRes] = await Promise.all([
      getPetList({ page: 1, size: 100 }),
      getCageList({ page: 1, size: 100 }),
      getServiceList({ page: 1, size: 100 })
    ])
    pets.value = petRes.data.records
    cages.value = cageRes.data.records
    services.value = svcRes.data.records
  } catch {}
})
</script>

<style scoped>
.pet-select-group, .cage-select-group { width: 100%; }
.pet-option, .cage-option {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
  margin-bottom: 8px;
}
.service-option {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 8px;
}
.pet-option:hover, .cage-option:hover, .service-option:hover { border-color: #409eff; }
.pet-option.selected, .cage-option.selected { border-color: #409eff; background: #ecf5ff; }
.service-option.selected { border-color: #409eff; background: #ecf5ff; }
.cage-option.disabled { opacity: 0.5; cursor: not-allowed; }
.cage-type-badge {
  display: inline-block;
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  margin-bottom: 8px;
}
.cage-type-badge.standard { background: #909399; }
.cage-type-badge.deluxe { background: #409eff; }
.cage-type-badge.vip { background: #e6a23c; }
.price { color: #f56c6c; font-size: 20px; font-weight: bold; }
.price span { font-size: 12px; font-weight: normal; }
.desc { font-size: 12px; color: #909399; margin-top: 8px; }
.pet-option h4, .cage-option h4 { margin: 0 0 8px 0; }
.pet-option p, .cage-option p { margin: 4px 0; font-size: 13px; color: #606266; }
.service-option {
  display: flex; align-items: center; gap: 16px;
  padding: 12px 20px;
}
.svc-name { font-weight: bold; font-size: 15px; min-width: 80px; }
.svc-price { color: #f56c6c; font-size: 18px; font-weight: bold; min-width: 80px; }
.svc-desc { color: #909399; font-size: 13px; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.svc-duration { color: #606266; font-size: 13px; white-space: nowrap; }
.svc-check { color: #409eff; font-size: 20px; }
.pet-avatar {
  width: 80px; height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 8px;
  border: 2px solid #e4e7ed;
}
.pet-avatar-placeholder {
  width: 80px; height: 80px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex; align-items: center; justify-content: center;
  font-size: 40px;
  margin: 0 auto 8px;
}
</style>
