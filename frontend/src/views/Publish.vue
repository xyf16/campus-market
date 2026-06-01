<template>
  <div class="publish-container">
    <div class="header">
      <h1>发布商品</h1>
      <el-button @click="$router.push('/')">返回首页</el-button>
    </div>

    <el-card class="publish-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品标题" />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" placeholder="售价" />
        </el-form-item>

        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" placeholder="原价" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option label="数码产品" :value="1" />
            <el-option label="书籍教材" :value="2" />
            <el-option label="生活用品" :value="3" />
            <el-option label="服饰鞋包" :value="4" />
            <el-option label="运动户外" :value="5" />
            <el-option label="其他" :value="99" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品图片" prop="images">
          <el-input v-model="form.images" placeholder="图片URL，多张用逗号分隔" />
          <div class="tip">示例: https://example.com/1.jpg,https://example.com/2.jpg</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handlePublish" :loading="loading">发布商品</el-button>
          <el-button @click="$router.push('/')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  title: '',
  description: '',
  price: 0,
  originalPrice: 0,
  categoryId: null,
  images: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handlePublish = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const imagesArray = form.images ? form.images.split(',').map(s => s.trim()) : []
        const data = {
          ...form,
          images: JSON.stringify(imagesArray)
        }

        const res = await productApi.create(data)
        if (res.code === 200) {
          ElMessage.success('发布成功')
          router.push('/')
        } else {
          ElMessage.error(res.message || '发布失败')
        }
      } catch (error) {
        ElMessage.error('发布失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.publish-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.publish-card {
  padding: 20px;
}
.tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>