package com.example.market.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer categoryId;
    private String images;  // JSON格式的图片数组
}