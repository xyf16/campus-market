import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Publish from '../views/Publish.vue'
import MyProducts from '../views/MyProducts.vue'
import ProductDetail from '../views/ProductDetail.vue'
import MyOrders from '../views/MyOrders.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: Login
        },
        {
            path: '/register',
            name: 'register',
            component: Register
        },
        {
            path: '/',
            name: 'home',
            component: Home
        },
        {
            path: '/publish',
            name: 'publish',
            component: Publish
        },
        {
            path: '/my-products',
            name: 'myProducts',
            component: MyProducts
        },
        {
            path: '/product/:id',
            name: 'productDetail',
            component: ProductDetail
        },
        {
            path: '/my-orders',
            name: 'myOrders',
            component: MyOrders
        }
    ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    if (to.path === '/login' || to.path === '/register') {
        next()
    } else {
        if (token) {
            next()
        } else {
            next('/login')
        }
    }
})

export default router