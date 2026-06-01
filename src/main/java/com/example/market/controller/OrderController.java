package com.example.market.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.dto.Result;
import com.example.market.entity.Order;
import com.example.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Map<String, Object>> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Long productId = Long.valueOf(params.get("productId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";

        try {
            Order order = orderService.createOrderWithObject(productId, userId, remark);
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getId());
            data.put("orderNo", order.getOrderNo());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PutMapping("/{orderId}/pay")
    public Result<String> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        boolean success = orderService.payOrder(orderId, userId);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败");
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my")
    public Result<Page<Order>> myOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        wrapper.orderByDesc(Order::getCreateTime);

        return Result.success(orderService.page(pageParam, wrapper));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            return Result.error("无权限查看");
        }

        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<String> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        boolean success = orderService.cancelOrder(orderId, userId);
        if (success) {
            return Result.success("取消成功");
        }
        return Result.error("取消失败");
    }

    /**
     * 确认收货
     */
    @PutMapping("/{orderId}/confirm")
    public Result<String> confirmOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        boolean success = orderService.confirmOrder(orderId, userId);
        if (success) {
            return Result.success("确认收货成功");
        }
        return Result.error("确认收货失败");
    }

    /**
     * 发货
     */
    @PutMapping("/{orderId}/ship")
    public Result<String> shipOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        boolean success = orderService.shipOrder(orderId, userId);
        if (success) {
            return Result.success("发货成功");
        }
        return Result.error("发货失败");
    }
}