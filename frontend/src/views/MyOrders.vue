<template>
  <div class="my-orders">
    <div class="header">
      <h1>我的订单</h1>
      <el-button @click="$router.push('/')">返回首页</el-button>
      <el-button type="primary" @click="loadOrders">刷新</el-button>
    </div>

    <div class="order-list" v-loading="loading">
      <el-table :data="orderList" border style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productId" label="商品ID" width="80" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待付款</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已付款</el-tag>
            <el-tag v-else-if="row.status === 2" type="primary">已完成</el-tag>
            <el-tag v-else type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewProduct(row.productId)">查看商品</el-button>

            <!-- 卖家发货按钮 -->
            <el-button
                v-if="isSeller(row) && row.status === 1"
                size="small"
                type="primary"
                @click="handleShip(row.id)"
            >
              发货
            </el-button>

            <!-- 买家确认收货按钮 -->
            <el-button
                v-if="isBuyer(row) && row.status === 1"
                size="small"
                type="success"
                @click="handleConfirm(row.id)"
            >
              确认收货
            </el-button>

            <!-- 取消订单按钮（只有待付款状态可取消） -->
            <el-button
                v-if="(isBuyer(row) || isSeller(row)) && row.status === 0"
                size="small"
                type="danger"
                @click="handleCancel(row.id)"
            >
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单" />

      <div class="pagination" v-if="total > 0">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadOrders"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const currentUserId = ref(null)

// 获取当前用户ID
const getCurrentUserId = () => {
  const token = localStorage.getItem('token')
  if (token) {
    const parts = token.split('_')
    if (parts.length >= 1) {
      currentUserId.value = parseInt(parts[0])
    }
  }
}

// 判断是否是买家
const isBuyer = (order) => {
  return order.buyerId === currentUserId.value
}

// 判断是否是卖家
const isSeller = (order) => {
  return order.sellerId === currentUserId.value
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getMyOrders({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 查看商品
const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 发货
const handleShip = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认发货吗？', '提示', {
      confirmButtonText: '确认发货',
      cancelButtonText: '取消',
      type: 'info'
    })

    const res = await orderApi.ship(orderId)
    if (res.code === 200) {
      ElMessage.success('发货成功')
      loadOrders()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch {
    // 用户取消
  }
}

// 确认收货
const handleConfirm = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    const res = await orderApi.confirm(orderId)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      loadOrders()
    } else {
      ElMessage.error(res.message || '确认收货失败')
    }
  } catch {
    // 用户取消
  }
}

// 取消订单
const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认取消订单吗？', '提示', {
      confirmButtonText: '确认取消',
      cancelButtonText: '返回',
      type: 'warning'
    })

    const res = await orderApi.cancel(orderId)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadOrders()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  getCurrentUserId()
  loadOrders()
})
</script>

<style scoped>
.my-orders {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.order-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
  min-height: 400px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>