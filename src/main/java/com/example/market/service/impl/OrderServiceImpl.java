package com.example.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.market.entity.Order;
import com.example.market.entity.Product;
import com.example.market.mapper.OrderMapper;
import com.example.market.service.OrderService;
import com.example.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ProductService productService;

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + random;
    }

    @Override
    public Order createOrderWithObject(Long productId, Long buyerId, String remark) {
        Product product = productService.getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (product.getUserId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setAmount(product.getPrice());
        order.setStatus(0);
        order.setRemark(remark);

        this.save(order);
        return order;
    }

    @Override
    public String createOrder(Long productId, Long buyerId, String remark) {
        Order order = createOrderWithObject(productId, buyerId, remark);
        return order.getOrderNo();
    }

    @Override
    @Transactional
    public boolean buyOrder(Long orderId, Long buyerId) {
        Order order = this.getById(orderId);
        if (order == null) return false;
        if (!order.getBuyerId().equals(buyerId)) return false;
        if (order.getStatus() != 0) return false;

        order.setStatus(1);

        Product product = productService.getById(order.getProductId());
        product.setStatus(2);
        productService.updateById(product);

        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean payOrder(Long orderId, Long buyerId) {
        Order order = this.getById(orderId);
        if (order == null) return false;
        if (!order.getBuyerId().equals(buyerId)) return false;
        if (order.getStatus() != 0) return false;

        order.setStatus(1);  // 已付款

        // 更新商品状态为已售出
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(2);
            productService.updateById(product);
        }

        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            return false;
        }
        if (order.getStatus() != 0) return false;

        order.setStatus(3);
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean confirmOrder(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId)) return false;
        if (order.getStatus() != 1) return false;

        order.setStatus(2);
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean shipOrder(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) return false;
        if (!order.getSellerId().equals(userId)) return false;
        if (order.getStatus() != 1) return false;

        order.setStatus(1);
        return this.updateById(order);
    }
}