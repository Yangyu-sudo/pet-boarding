<template>
  <div class="pet-form-page">
    <el-card>
      <template #header>
        <h2>{{ isEdit ? '编辑宠物' : '添加宠物' }}</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="DOG">🐶 狗</el-radio>
            <el-radio value="CAT">🐱 猫</el-radio>
            <el-radio value="OTHER">🐾 其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="年龄(月)">
          <el-input-number v-model="form.age" :min="0" :max="300" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="MALE">♂ 公</el-radio>
            <el-radio value="FEMALE">♀ 母</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="照片">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="form.photo" :src="form.photo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="病史">
          <el-input v-model="form.medicalHistory" type="textarea" :rows="3" placeholder="请输入病史（如有）" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="3" placeholder="其他备注信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '添加宠物' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addPet, updatePet, getPetById } from '@/api/pet'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)

const form = reactive({
  name: '',
  type: 'DOG',
  breed: '',
  age: null,
  gender: '',
  weight: null,
  photo: '',
  medicalHistory: '',
  notes: ''
})

const rules = {
  name: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    try {
      const res = await getPetById(id)
      Object.assign(form, res.data)
    } catch {}
  }
})

function handleUploadSuccess(response) {
  form.photo = response.data
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updatePet(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await addPet(form)
      ElMessage.success('添加成功')
    }
    router.push('/customer/pets')
  } catch {} finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}
.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
