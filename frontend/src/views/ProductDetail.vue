<template>
  <div class="product-detail" v-loading="loading">
    <div class="header">
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="detail-card" v-if="product">
      <el-row :gutter="30">
        <el-col :span="12">
          <img :src="getFirstImage(product.images)" class="product-image" />
        </el-col>
        <el-col :span="12">
          <h1>{{ product.title }}</h1>
          <p class="price">¥{{ product.price }}</p>
          <p class="original-price">原价: ¥{{ product.originalPrice }}</p>
          <p class="description">{{ product.description }}</p>
          <div class="info">
            <p>浏览量: {{ product.viewCount }}</p>
            <p>发布时间: {{ product.createTime }}</p>
          </div>

          <!-- 购买按钮区域 -->
          <div class="buy-section" v-if="product.status === 1 && product.userId !== currentUserId">
            <el-button type="danger" size="large" @click="showBuyDialog = true">立即购买</el-button>
          </div>
          <div class="buy-section" v-if="product.status === 2">
            <el-tag type="info" size="large">已售出</el-tag>
          </div>
          <div class="buy-section" v-if="product.userId === currentUserId">
            <el-tag type="warning" size="large">这是您发布的商品</el-tag>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 购买确认弹窗 -->
    <el-dialog v-model="showBuyDialog" title="确认购买" width="400px">
      <el-form>
        <el-form-item label="商品名称">
          <span>{{ product?.title }}</span>
        </el-form-item>
        <el-form-item label="价格">
          <span class="dialog-price">¥{{ product?.price }}</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
              v-model="buyRemark"
              type="textarea"
              placeholder="选填，比如约定交易地点或联系方式"
              :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBuyDialog = false">取消</el-button>
        <el-button type="danger" @click="handleBuy" :loading="buyLoading">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, orderApi } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const product = ref(null)
const currentUserId = ref(null)
const showBuyDialog = ref(false)
const buyRemark = ref('')
const buyLoading = ref(false)

// 从token获取当前用户ID
const getCurrentUserId = () => {
  const token = localStorage.getItem('token')
  if (token) {
    const parts = token.split('_')
    if (parts.length >= 1) {
      currentUserId.value = parseInt(parts[0])
    }
  }
}

// 获取第一张图片
const getFirstImage = (images) => {
  if (!images) return 'https://via.placeholder.com/400'
  try {
    const list = JSON.parse(images)
    return list[0] || 'https://via.placeholder.com/400'
  } catch {
    return 'https://via.placeholder.com/400'
  }
}

// 加载商品详情
const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await productApi.getDetail(id)
    if (res.code === 200) {
      product.value = res.data
    } else {
      ElMessage.error('商品不存在')
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 购买商品
const handleBuy = async () => {
  buyLoading.value = true
  try {
    // 1. 创建订单
    const createRes = await orderApi.create({
      productId: product.value.id,
      remark: buyRemark.value
    })

    if (createRes.code !== 200) {
      ElMessage.error(createRes.message || '创建订单失败')
      return
    }

    const orderId = createRes.data.orderId
    const orderNo = createRes.data.orderNo

    // 2. 支付订单
    const payRes = await orderApi.pay(orderId)

    if (payRes.code !== 200) {
      ElMessage.error(payRes.message || '支付失败')
      return
    }

    ElMessage.success('购买成功！订单号：' + orderNo)
    showBuyDialog.value = false
    buyRemark.value = ''

    // 刷新页面
    setTimeout(() => {
      loadDetail()
    }, 1000)

  } catch (error) {
    console.error('购买失败:', error)
    ElMessage.error('购买失败')
  } finally {
    buyLoading.value = false
  }
}

onMounted(() => {
  getCurrentUserId()
  loadDetail()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 60px);
  background-color: #f5f5f5;
}
.header {
  margin-bottom: 20px;
}
.detail-card {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.product-image {
  width: 100%;
  border-radius: 8px;
}
.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin: 20px 0;
}
.original-price {
  color: #999;
  text-decoration: line-through;
  margin-bottom: 20px;
}
.description {
  margin: 20px 0;
  line-height: 1.6;
  color: #666;
}
.info {
  color: #999;
  font-size: 14px;
  border-top: 1px solid #eee;
  padding-top: 20px;
  margin-top: 20px;
}
.buy-section {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.dialog-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>