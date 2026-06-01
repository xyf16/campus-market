package com.example.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.market.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrderWithObject(Long productId, Long buyerId, String remark);
    String createOrder(Long productId, Long buyerId, String remark);
    boolean buyOrder(Long orderId, Long buyerId);
    boolean payOrder(Long orderId, Long buyerId);
    boolean cancelOrder(Long orderId, Long userId);
    boolean confirmOrder(Long orderId, Long userId);
    boolean shipOrder(Long orderId, Long userId);
}