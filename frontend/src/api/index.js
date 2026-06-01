import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器 - 添加token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器 - 处理错误
request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

// 用户API
export const userApi = {
    login: (data) => request.post('/user/login', data),
    register: (data) => request.post('/user/register', data)
}

// 商品API
export const productApi = {
    getList: (params) => request.get('/products', { params }),
    getDetail: (id) => request.get(`/products/${id}`),
    create: (data) => request.post('/products', data),
    offShelf: (id) => request.put(`/products/${id}/off`),
    getMyProducts: (params) => request.get('/products/my', { params })
}

// 订单API
export const orderApi = {
    // 创建订单
    create: (data) => request.post('/orders', data),
    // 支付订单
    pay: (orderId) => request.put(`/orders/${orderId}/pay`),
    // 直接购买
    buy: (orderId) => request.put(`/orders/${orderId}/buy`),
    // 获取我的订单列表
    getMyOrders: (params) => request.get('/orders/my', { params }),
    // 获取订单详情
    getDetail: (orderId) => request.get(`/orders/${orderId}`),
    // 取消订单
    cancel: (orderId) => request.put(`/orders/${orderId}/cancel`),
    // 确认收货
    confirm: (orderId) => request.put(`/orders/${orderId}/confirm`),
    // 发货
    ship: (orderId) => request.put(`/orders/${orderId}/ship`)
}

export default request