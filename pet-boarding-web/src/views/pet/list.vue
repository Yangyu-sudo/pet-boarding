<template>
  <div class="pet-list-page">
    <div class="page-header">
      <h2>我的宠物</h2>
      <el-button type="primary" @click="$router.push('/customer/pets/add')">
        <el-icon><Plus /></el-icon> 添加宠物
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="8" v-for="pet in pets" :key="pet.id" style="margin-bottom: 20px">
        <el-card shadow="hover" @click="$router.push(`/customer/pets/${pet.id}`)" class="pet-card">
          <div class="pet-photo">
            <img :src="pet.photo || '/default-pet.png'" alt="" />
          </div>
          <div class="pet-info">
            <h3>{{ pet.name }}</h3>
            <el-tag :type="pet.type === 'DOG' ? 'primary' : pet.type === 'CAT' ? 'success' : 'warning'" size="small">
              {{ pet.type === 'DOG' ? '🐶 狗' : pet.type === 'CAT' ? '🐱 猫' : '🐾 其他' }}
            </el-tag>
            <p>品种：{{ pet.breed || '未设置' }}</p>
            <p>年龄：{{ formatAge(pet.age) }} | 体重：{{ pet.weight }}kg</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="pets.length === 0" description="还没有添加宠物" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPetList } from '@/api/pet'

const pets = ref([])

function formatAge(months) {
  if (months == null) return '-'
  if (months >= 12 && months % 12 === 0) return (months / 12) + '年'
  return months + '个月'
}

async function fetchPets() {
  try {
    const res = await getPetList({ page: 1, size: 100 })
    pets.value = res.data.records
  } catch {}
}

onMounted(fetchPets)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.pet-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.pet-card:hover {
  transform: translateY(-4px);
}
.pet-photo {
  height: 180px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pet-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.pet-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
}
.pet-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #909399;
}
</style>
