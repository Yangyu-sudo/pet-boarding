<template>
  <div class="pet-detail-page">
    <el-page-header @back="$router.back()" :content="pet.name || '宠物详情'" />

    <el-card style="margin-top: 20px" v-loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="照片">
          <el-image :src="pet.photo || '/default-pet.png'" style="width: 100px; height: 100px" fit="cover" />
        </el-descriptions-item>
        <el-descriptions-item label="名称">{{ pet.name }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag>{{ pet.type === 'DOG' ? '狗' : pet.type === 'CAT' ? '猫' : '其他' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="品种">{{ pet.breed || '-' }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ formatAge(pet.age) }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ pet.gender === 'MALE' ? '公' : pet.gender === 'FEMALE' ? '母' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ pet.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ pet.createTime }}</el-descriptions-item>
        <el-descriptions-item label="病史" :span="2">{{ pet.medicalHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ pet.notes || '无' }}</el-descriptions-item>
      </el-descriptions>

      <div class="detail-actions" style="margin-top: 20px">
        <el-button type="primary" @click="$router.push(`/customer/pets/${pet.id}/edit`)">编辑</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPetById } from '@/api/pet'

const route = useRoute()
const pet = ref({})
const loading = ref(false)

function formatAge(months) {
  if (months == null) return '-'
  if (months >= 12 && months % 12 === 0) return (months / 12) + '年'
  return months + '个月'
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getPetById(route.params.id)
    pet.value = res.data
  } catch {} finally {
    loading.value = false
  }
})
</script>
