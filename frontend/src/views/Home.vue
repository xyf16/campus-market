<template>
  <div class="home">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo" @click="loadProducts">🏫 校园二手交易平台</div>
        <div class="nav-links">
          <el-button type="primary" size="small" @click="goToPublish">发布商品</el-button>
          <el-button size="small" @click="goToMyProducts">我的发布</el-button>
          <el-button size="small" @click="goToMyOrders">我的订单</el-button>
          <el-button size="small" @click="logout">退出登录</el-button>
        </div>
      </div>
    </nav>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
          v-model="keyword"
          placeholder="搜索商品..."
          style="width: 400px"
          @keyup.enter="searchProducts"
      >
        <template #append>
          <el-button @click="searchProducts">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 商品列表 -->
    <div class="product-list" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in productList" :key="product.id">
          <el-card class="product-card" @click="goToDetail(product.id)">
            <img :src="getFirstImage(product.images)" class="product-img" />
            <h3>{{ product.title }}</h3>
            <p class="price">¥{{ product.price }}</p>
            <p class="original-price">原价: ¥{{ product.originalPrice }}</p>
            <el-tag v-if="product.status === 2" type="info" size="small">已售出</el-tag>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[8, 16, 24]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const productList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const keyword = ref('')

// 获取第一张图片
const getFirstImage = (images) => {
  if (!images) return 'https://via.placeholder.com/200'
  try {
    const list = JSON.parse(images)
    return list[0] || 'https://via.placeholder.com/200'
  } catch {
    return 'https://via.placeholder.com/200'
  }
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }

    const res = await productApi.getList(params)
    if (res.code === 200) {
      productList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      productList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    productList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索商品
const searchProducts = () => {
  currentPage.value = 1
  loadProducts()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadProducts()
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadProducts()
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

// 跳转发布页
const goToPublish = () => {
  router.push('/publish')
}

// 跳转我的发布
const goToMyProducts = () => {
  router.push('/my-products')
}

// 跳转我的订单
const goToMyOrders = () => {
  router.push('/my-orders')
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 页面加载时获取数据
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.home {
  background-color: #f5f5f5;
  min-height: 100vh;
}
.navbar {
  background-color: #409eff;
  color: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}
.nav-links {
  display: flex;
  gap: 10px;
}
.search-bar {
  display: flex;
  justify-content: center;
  padding: 30px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.product-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.product-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}
.product-card h3 {
  margin: 10px 0;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin: 5px 0;
}
.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
  margin: 0;
}
.pagination {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  margin-top: 20px;
}
</style>