package com.example.market.mapper;
// mapper/ProductMapper.java


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}