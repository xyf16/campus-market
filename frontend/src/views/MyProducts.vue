<template>
  <div class="my-products">
    <div class="header">
      <h1>我的发布</h1>
      <el-button @click="$router.push('/')">返回首页</el-button>
      <el-button type="primary" @click="loadProducts">刷新</el-button>
    </div>

    <div class="product-list" v-loading="loading">
      <el-table :data="productList" border style="width: 100%">
        <el-table-column prop="title" label="商品标题" min-width="200" />
        <el-table-column prop="price" label="售价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            ¥{{ row.originalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">在售</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已售出</el-tag>
            <el-tag v-else type="danger">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row.id)">查看</el-button>
            <el-button
                v-if="row.status === 1"
                size="small"
                type="danger"
                @click="offShelf(row.id)"
            >
              下架
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="total > 0">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>

      <el-empty v-if="!loading && productList.length === 0" description="暂无发布商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const productList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getMyProducts({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200) {
      productList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/product/${id}`)
}

// 下架商品
const offShelf = async (id) => {
  try {
    await ElMessageBox.confirm('确定要下架该商品吗？下架后商品将不再展示。', '提示', {
      confirmButtonText: '确定下架',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await productApi.offShelf(id)
    if (res.code === 200) {
      ElMessage.success('下架成功')
      loadProducts() // 刷新列表
    } else {
      ElMessage.error(res.message || '下架失败')
    }
  } catch {
    // 用户取消操作
  }
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

// 页面加载
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.my-products {
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
.product-list {
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