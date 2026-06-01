package com.example.market.mapper;
// mapper/FavoriteMapper.java


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
