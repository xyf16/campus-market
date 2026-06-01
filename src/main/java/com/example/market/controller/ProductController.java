package com.example.market.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.dto.ProductDTO;
import com.example.market.dto.Result;
import com.example.market.entity.Product;
import com.example.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询商品列表（首页展示）
     */
    @GetMapping
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId) {

        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 只查询在售商品
        wrapper.eq(Product::getStatus, 1);

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getTitle, keyword)
                    .or()
                    .like(Product::getDescription, keyword));
        }

        // 分类筛选
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        // 按时间倒序
        wrapper.orderByDesc(Product::getCreateTime);

        return Result.success(productService.page(pageParam, wrapper));
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 增加浏览量
        product.setViewCount(product.getViewCount() + 1);
        productService.updateById(product);
        return Result.success(product);
    }

    /**
     * 发布商品
     */
    @PostMapping
    public Result<String> create(@RequestBody ProductDTO productDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Product product = new Product();
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setCategoryId(productDTO.getCategoryId());
        product.setImages(productDTO.getImages());
        product.setUserId(userId);
        product.setStatus(1);
        product.setViewCount(0);
        product.setCreateTime(LocalDateTime.now());

        boolean success = productService.save(product);
        if (success) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    /**
     * 下架商品
     */
    @PutMapping("/{id}/off")
    public Result<String> offShelf(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Product product = productService.getById(id);

        if (product == null) {
            return Result.error("商品不存在");
        }

        if (!product.getUserId().equals(userId)) {
            return Result.error("无权限操作");
        }

        product.setStatus(3);
        productService.updateById(product);
        return Result.success("下架成功");
    }

    /**
     * 获取当前用户的商品列表（我的发布）
     */
    @GetMapping("/my")
    public Result<Page<Product>> myProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getUserId, userId);
        wrapper.orderByDesc(Product::getCreateTime);

        return Result.success(productService.page(pageParam, wrapper));
    }
}